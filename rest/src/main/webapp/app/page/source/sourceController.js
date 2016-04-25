// Controller
tsApp
  .controller(
    'SourceCtrl',
    function($scope, $location, $http, $q, $interval, $uibModal, NgTableParams, sourceDataService,
      utilService, contentService, securityService, gpService, FileUploader, tabService,
      configureService, metadataService) {
      console.debug('configure SourceCtrl');

      // Handle resetting tabs on "back" button
      if (tabService.selectedTab.label != 'Sources') {
        tabService.setSelectedTabByLabel('Sources');
      }

      // 
      // Local variables
      // 
      $scope.isSnomedLoaded = null;
      $scope.sourceData = null;
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
        console.log('Initializing source data controller');
        gpService.increment();

        var pfs = {
          startIndex : 0,
          maxResults : 1,
          sortField : null,
          queryRestriction : null
        };
        $http.post(contentUrl + 'cui/SNOMEDCT/latest?query=', pfs).then(function(response) {
          gpService.decrement();
          if (response.count > 0) {
            $scope.isSnomedLoaded = true;
          }
          $scope.getSourceData();
        }, function(response) {
          utilService.handleError(response);
          gpService.decrement()
        });
      };

      //
      // Load, remove, cancel functions
      //
      // start load and initiate polling
      $scope.loadData = function() {
        if (!$scope.sourceData) {
          console.error('no source data to load data for');
          return;
        }
        sourceDataService.loadFromSourceData($scope.sourceData).then(function() {
          $scope.startPolling($scope.sourceData);
        });
        $scope.sourceData.status = 'LOADING';
      };

      $scope.cancel = function() {
        sourceDataService.cancelSourceDataProcess($scope.sourceData).then(function() {
          $scope.getSourceData();
        });
      };

      // destroy the database
      $scope.destroy = function() {
        console.log('Destroy database request received');

        // first clear all source datas
        // intended to allow detection of discrepancies where
        // a terminology is loaded but no source datas exist
        // (i.e. incomplete, failed, or cancelled database clear
        sourceDataService.getSourceDatas().then(function(data) {
          console.debug('Destroying: deleting source datas', data.sourceDatas);
          var delCt = 0;
          angular.forEach(data.sourceDatas, function(sourceData) {
            sourceDataService.removeSourceData(sourceData.id).then(function() {

              // after last source data is deleted, destroy and recreate the database
              if (++delCt == data.sourceDatas.length) {
                console.log('Destroying database');
                return;
                configureService.destroy().then(function() {
                  $scope.isSnomedLoaded = false;
                  $scope.sourceData = null;

                  // factory reset and users cleared, force relogin
                  $location.path('/login');
                });
              }
            });
          });
        });

      }

      // TODO Add remove/clear

      // TODO watch for debug, remove later
      $scope.$watch('sourceData', function() {
        console.debug('current source data', $scope.sourceData);
      })

      //
      // Utility Functions
      //

      $scope.resetSourceData = function() {
        if (!$scope.sourceData || !$scope.sourceData.id) {
          console.error('Cannot remove source data (null or no id)', $scope.sourceData);

        } else {
          sourceDataService.removeSourceData($scope.sourceData.id).then(function() {
            $scope.sourceData = null;
          });
        }
      };

      $scope.refreshFilesTable = function() {
        var files = [];
        files = $scope.sourceData.sourceDataFiles;
        //console.debug(files);

        $scope.tpSourceDataFiles = new NgTableParams({}, {
          dataset : files,
          counts : []
        // hides page sizes
        });
      }

      $scope.setSourceData = function(sourceData) {
        $scope.sourceData = sourceData;
        if (!$scope.sourceData) {
          console.error('Attempted to set null source data');
          return;
        }
        if (!$scope.sourceData.status || $scope.sourceData.status === 'NEW') {
          console.debug('New status detected')

          // if source data has no files, delete it and reset
          if (!$scope.sourceData.sourceDataFiles || $scope.sourceData.sourceDataFiles.length == 0) {
            $scope.resetSourceData();
          }
        } else if ($scope.sourceData.status === 'LOADING') {
          console.debug('Loading status detected');
          $scope.startPolling($scope.sourceData);
        } else if ($scope.sourceData.status === 'LOADING_COMPLETE') {
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
        if (!$scope.sourceData) {
          sourceDataService.getSourceDatas().then(function(response) {
            console.debug('All source datas', response);

            if (response.count > 0) {
              // sort by descending modification (want most recent)
              var sds = response.sourceDatas.sort(function(a, b) {
                return a.lastModified < b.lastModified;
              });
              $scope.setSourceData(sds[0]);
            }

            // no source datas + terminology loaded -> database fail
            else if ($scope.isSnomedLoaded) {
              $scope.sourceData = {
                status : 'BAD_DATABSE'
              };
            }

          })
        } else {

          sourceDataService.getSourceData($scope.sourceData.id).then(function(response) {
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
        var id = $scope.sourceData.id;
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
          $scope.sourceData = newSourceData;

          // dynamically set the upload url with the unzip flag
          item.url = sourceDataUrl + '/upload/' + $scope.sourceData.id + '?unzip=true';
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

      uploader.onBeforeUploadItem = function(item) {
        utilService.clearError();
      };

      // NOTE: Glass pane decrement and source data removal handled in onCompleteAll
      uploader.onErrorItem = function(fileItem, response, status, headers) {

        console.info('onErrorItem', fileItem, response, status, headers);
        utilService.handleError({
          data : response ? response : "Error uploading source data",
          status : status,
          headers : headers
        });
      };

      // NOTE: Fires when single item upload either succeeds or fails
      uploader.onCompleteAll = function() {
        console.debug('onCompleteAll');
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
        case 'LOADING_FAILED':
        case 'REMOVAL_COMPLETE':
        case 'REMOVAL_FAILED':
          $scope.stopPolling(polledSourceData);
          $scope.setSourceData(polledSourceData);
          break;
        default:
          // update poll status from data
          if ($scope.polls.hasOwnProperty(polledSourceData.id)) {
            $scope.polls[polledSourceData.id].status = polledSourceData.status;
          }
        }

      };

    });