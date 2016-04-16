// Controller
tsApp
  .controller(
    'SourceCtrl',
    function($scope, $location, $http, $q, $interval, NgTableParams, sourceDataService,
      utilService, securityService, gpService, FileUploader, tabService, configureService,
      metadataService) {
      console.debug('configure SourceCtrl');

      // Handle resetting tabs on "back" button
      if (tabService.selectedTab.label != 'Sources') {
        tabService.setSelectedTabByLabel('Sources');
      }

      // 
      // Local variables
      // 
      $scope.isSnomedLoaded = null;
      $scope.currentSourceData = null;
      $scope.polls = {};

      //
      // Initialization
      //
      configureService.isConfigured().then(function(isConfigured) {
        if (isConfigured) {
          $scope.initialize();

        } else {
          $location.path('/configure');
        }
      });

      $scope.initialize = function() {
        metadataService.initTerminologies().then(function(response) {
          $scope.isSnomedLoaded = response.totalCount > 0;
          $scope.getSourceData();
        });
      }

      //
      // Load, remove, cancel functions
      //
      // start load and initiate polling
      $scope.loadData = function() {
        if (!$scope.currentSourceData) {
          console.error('no source data to load data for');
          return;
        }
        sourceDataService.loadFromSourceData($scope.currentSourceData).then(function() {
          $scope.startPolling($scope.currentSourceData);
        });
        $scope.currentSourceData.status = 'LOADING';
      };

      $scope.cancel = function() {
        sourceDataService.cancelSourceDataProcess($scope.currentSourceData).then(function() {
          $scope.getSourceData();
        });
      };

      $scope.destroy = function() {
        configureService.destroy().then(function() {
          $scope.isSnomedLoaded = false;
          $scope.currentSourceData = null;

          // factory reset and users cleared, force relogin
          $location.path('/login');
        });
      }

      // TODO Add remove/clear

      // TODO watch for debug, remove later
      $scope.$watch('currentSourceData', function() {
        console.debug('current source data', $scope.currentSourceData);
      })

      //
      // Utility Functions
      //

      $scope.refreshFilesTable = function() {
        var files = [];
        files = $scope.currentSourceData.sourceDataFiles;
        console.debug(files);

        $scope.tpSourceDataFiles = new NgTableParams({}, {
          dataset : files,
          counts : []
        // hides page sizes
        });
      }
      
      $scope.setSourceData = function(sourceData) {
        $scope.currentSourceData = sourceData;
        if (!$scope.currentSourceData) {
          console.error('Attempted to set null source data');
          return;
        }
        if (!$scope.currentSourceData.status || $scope.currentSourceData.status === 'NEW') {
          console.debug('New status detected')
        } else if ($scope.currentSourceData.status === 'LOADING') {
          console.debug('Loading status detected');
          $scope.startPolling($scope.currentSourceData);
        } else if ($scope.currentSourceData.status === 'LOADING_COMPLETE') {
          console.debug('Loading complete status detected')
          $scope.isSnomedLoaded = true;
        } else {
          console.debug('Non-new, non-loading status detected');
        }
        $scope.refreshFilesTable();

      
      }

      $scope.getSourceData = function() {
        console.debug('get sourceData');

        // if no source data, get the latest
        if (!$scope.currentSourceData) {
          sourceDataService.getSourceDatas().then(function(response) {
            console.debug('All source datas', response);

            if (response.count > 0) {
              // sort by descending modification (want most recent)
              var sds = response.sourceDatas.sort(function(a, b) {
                return a.lastModified < b.lastModified;
              });
              $scope.setSourceData(sds[0]);
            }
              
          })
        } else {

          sourceDataService.getSourceData($scope.currentSourceData.id).then(function(response) {
            $scope.setSourceData(response);

          });
        }
      }

      // scope to capitalize first initials only
      $scope.getHumanText = function(str) {
        if (!str) {
          return null;
        }
        return str.replace('_', ' ').replace(/\w\S*/g, function(txt) {
          return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        });

      };
      $scope.getFilePath = function(file) {
        var id = $scope.currentSourceData.id;
        return file.path.substring(file.path.indexOf(id) + id.toString().length + 1);
      };

      //
      // Uploader Controls

      //Specify the angular-file-uploader
      var uploader = $scope.uploader = new FileUploader({
        url : sourceDataUrl + 'upload'
      });
      uploader.filters = [];

      // CALLBACKS

      uploader.onAfterAddingFile = function(item) {

        // create the blank source data
        var sourceData = {
          name : (!$scope.isSnomedLoaded ? 'Snapshot_' : 'Delta_') + Date.now(),
          description : null,
          handler : (!$scope.isSnomedLoaded ? 'com.wci.umls.server.jpa.services.handlers.Rf2SnapshotSourceDataHandler'
            : 'com.wci.umls.server.jpa.services.handlers.Rf2DeltaSourceDataHandler'),

          terminology : 'SNOMEDCT',
          version : 'latest'
        }

        gpService.increment();

        // create the source data
        sourceDataService.updateSourceData(sourceData).then(function(newSourceData) {
          $scope.currentSourceData = newSourceData;

          // dynamically set the upload url with the unzip flag
          item.url = sourceDataUrl + '/upload/' + $scope.currentSourceData.id + '?unzip=true';
          // manually set the headers on the item's request (does not inherit from
          // $http, apparently)
          item.headers = {
            'Authorization' : 'admin'
          };
          item.upload();

        }, function() {
          gpService.decrement();
        });

      };

      uploader.onErrorItem = function(fileItem, response, status, headers) {
        gpService.decrement();
        // console.info('onErrorItem', fileItem, response, status, headers);
        utilService.handleError({
          data : response ? response : "Error uploading source data",
          status : status,
          headers : headers
        });
        $scope.getSourceData();
      };

      uploader.onCompleteAll = function() {
        gpService.decrement();
        // re-retrieve source datas
        $scope.getSourceData();
      };

      //
      // polling
      //
      $scope.startPolling = function(sourceData) {
        console.log('Starting status polling for ' + sourceData.name);

        // construct the polling object
        $scope.polls[sourceData.id] = {
          status : 'Initializing...',
          poll : null,
          logEntries : null
        };

        $scope.polls[sourceData.id].poll = $interval(function() {

          //console.debug('Polling', $scope.polls[sourceData.id]);

          // get the source data by id (suppress glass pane)
          sourceDataService.getSourceData(sourceData.id, true).then(function(polledSourceData) {
            if (polledSourceData) {
              $scope.updateSourceDataFromPoll(polledSourceData);
            } else {
              console.debug('Failed to retrieve polled source data');
              $scope.stopPolling(sourceData);
            }
          });

          // get and set the log entries
          sourceDataService.getSourceDataLog(sourceData.terminology, sourceData.version, null, 100)
            .then(function(logEntries) {
              if ($scope.polls[sourceData.id]) {
                $scope.polls[sourceData.id].logEntries = logEntries;
                var objDiv = document.getElementById("activityLog");
                objDiv.scrollTop = objDiv.scrollHeight;
              }
            });

        }, 3142);
      };

      $scope.stopPolling = function(sourceData) {
        console.log('Stop polling for source data ' + sourceData.id + ': ' + sourceData.name,
          $scope.polls);
        if ($scope.polls.hasOwnProperty(sourceData.id)) {

          $interval.cancel($scope.polls[sourceData.id].poll);
        }
        delete $scope.polls[sourceData.id];
      };

      // cancel all polling on reloads or navigation
      $scope.$on("$routeChangeStart", function(event, next, current) {
        for ( var key in $scope.polls) {
          if ($scope.polls.hasOwnProperty(key)) {
            $interval.cancel($scope.polls[key].poll);
          }
          delete $scope.polls[sourceData.id];
        }
      });

      $scope.updateSourceDataFromPoll = function(polledSourceData) {
        if (!polledSourceData) {
          console.error('Cannot update source data from poll results');
          return;
        }
        console.debug('updating source data', polledSourceData);

        // perform actions nased on newly polled status
        switch (polledSourceData.status) {
        case 'LOADING_COMPLETE':
          utilService.handleSuccess('Terminology load completed for '
            + polledSourceData.terminology + ', ' + polledSourceData.version);
          $scope.stopPolling(polledSourceData);
          $scope.getSourceData();
          break;
        case 'LOADING_FAILED':
          utilService.setError('Terminology load failed for ' + polledSourceData.terminology + ', '
            + polledSourceData.version);
          $scope.stopPolling(polledSourceData);
          $scope.getSourceData();
          break;
        case 'REMOVAL_COMPLETE':
          utilService.handleSuccess('Terminology removal completed for '
            + polledSourceData.terminology + ', ' + polledSourceData.version);
          $scope.stopPolling(polledSourceData);
          $scope.getSourceData();
          break;
        case 'REMOVAL_FAILED':
          utilService.setError('Terminology removal failed for ' + polledSourceData.terminology
            + ', ' + polledSourceData.version);
          $scope.stopPolling(polledSourceData);
          $scope.getSourceData();
          break;
        default:
          // update poll status from data
          if ($scope.polls.hasOwnProperty(polledSourceData.id)) {
            $scope.polls[polledSourceData.id].status = polledSourceData.status;
          }
        }

      };

    });