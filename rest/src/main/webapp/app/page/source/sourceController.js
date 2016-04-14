// Controller
tsApp
  .controller(
    'SourceCtrl',
    function($scope, $location, $http, $q, $interval, NgTableParams, sourceDataService,
      utilService, securityService, gpService, FileUploader, tabService, configureService,
      metadataService) {
      console.debug('configure SourceCtrl');

      // Handle resetting tabs on "back" button
      if (tabService.selectedTab.label != 'Source') {
        tabService.setSelectedTabByLabel('Source');
      }

      // 
      // Local variables
      // 
      $scope.isSnomedLoaded = false;
      $scope.sourceDataSnapshot = null;
      $scope.sourceDataDelta = null;

      //
      // Utility Functions
      //

      function refreshSourceData() {
        console.debug('Refreshing sourceData');
        
        var deferred = $q.defer();

        sourceDataService.getSourceData($scope.currentSourceData.id).then(function(response) {
          $scope.currentSourceData = response;

          $scope.tpSourceDataFiles = new NgTableParams({}, {
            dataset : $scope.currentSourceData ? $scope.currentSourceData.sourceDataFiles : [],
            counts : []
          // hides page sizes
          });
          deferred.resolve();
        })
        return deferred.promise;
      }

      // scope to capitalize first initials only
      $scope.getHumanText = function(str) {
        if (!str) {
          return null;
        }
        return str.replace('_', ' ').replace(/\w\S*/g, function(txt) {
          return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        });

      }

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
        refreshSourceData();
      };

      uploader.onCompleteAll = function() {
        gpService.decrement();
        // re-retrieve source datas
        refreshSourceData();
      };

      $scope.initialize = function() {
        metadataService.initTerminologies().then(function(response) {
          $scope.isSnomedLoaded = response.totalCount > 0;
        });
      }

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

    });