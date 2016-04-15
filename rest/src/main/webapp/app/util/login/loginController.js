

// Login controller
tsApp.controller('LoginCtrl', [ '$scope', '$http', '$location', 'securityService', 'gpService',
  'utilService', 'projectService', 'configureService',
  function($scope, $http, $location, securityService, gpService, utilService, projectService, configureService) {
    console.debug('configure LoginCtrl');

    
    // Login function
    $scope.login = function(name, password) {
 
      // login
      gpService.increment();
      return $http({
        url : securityUrl + 'authenticate/' + name,
        method : 'POST',
        data : password,
        headers : {
          'Content-Type' : 'text/plain'
        }
      }).then(
      // success
      function(response) {
        utilService.clearError();
        securityService.setUser(response.data);

        // set request header authorization and reroute
        $http.defaults.headers.common.Authorization = response.data.authToken;
        
        metadataService.initTerminologies().then(function(response) {
          console.debug('login terminologies response', response.count, resopnse);
          if (response && response.count > 0) {
            $location.path('/content');
          } else {
            $location.path('/source');
          }
        })
        gpService.decrement();
      },

      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
      });
    };

    // Logout function
    $scope.logout = function() {
      securityService.logout();
    };
    
    //
    // Initialization: Check that application is configured
    //
    
    $scope.initialize = function() {
      // Clear user info
      securityService.clearUser();

      // TODO Get rid of this file once the proper security implementation is set up
      $scope.login('admin', 'admin');
    }
    configureService.isConfigured().then(function(isConfigured) {
      console.debug('login configured check: ', isConfigured);
      if (!isConfigured) {
        console.debug('routing to configure');
        $location.path('/configure');
      } else {
        //$scope.initialize();
      }
    });

  } ]);