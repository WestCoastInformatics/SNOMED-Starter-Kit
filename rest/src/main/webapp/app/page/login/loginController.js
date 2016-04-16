

// Login controller
tsApp.controller('LoginCtrl', [ '$scope', '$http', '$location', 'securityService', 'gpService',
  'utilService', 'projectService', 'configureService', 'metadataService',
  function($scope, $http, $location, securityService, gpService, utilService, projectService, configureService, metadataService) {
    console.debug('configure LoginCtrl');

    
    // Login function
    $scope.login = function(name, password) {
      console.debug('login called');
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
          console.debug('SNOMEDCT load detected? ', response.count > 0, response);
          if (response.count > 0) {
            $location.path('/content');
          } else {
            $location.path('/source');
          }
        }, function(error) {
          utilService.handleError(response);
          $location.path('/content');
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

      // Declare the user
      $scope.user = securityService.getUser();
      
   // TODO Temporarily hardwired to ensure user added in service layer
      $scope.login('admin', 'admin');
      

    }
    configureService.isConfigured().then(function(isConfigured) {
      console.debug('login configured check: ', isConfigured);
      if (!isConfigured) {
        console.debug('routing to configure');
        $location.path('/configure');
      } else {
        $scope.initialize();
      }
    });

  } ]);