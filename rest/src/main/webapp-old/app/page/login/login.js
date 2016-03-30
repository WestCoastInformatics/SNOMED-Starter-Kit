// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/login', {
    controller : 'LoginCtrl',
    templateUrl : 'app/page/login/login.html'
  });
});

// Controller
ttApp.controller('LoginCtrl', [ '$scope', '$http', '$location', 'securityService', 'gpService',
  'utilService', 'tabService',
  function($scope, $http, $location, securityService, gpService, utilService, tabService) {
    console.debug('configure LoginCtrl');

    // clear any cached user information
    securityService.clearUser();

    // Login function
    $scope.login = function(name, password) {
      if (!name) {
        alert('You must specify a user name');
        return;
      } else if (!password) {
        alert('You must specify a password');
        return;
      }

      // login
      gpService.increment();
      return $http({
        url : securityUrl + '/authenticate/' + name,
        method : 'POST',
        data : password,
        headers : {
          'Content-Type' : 'text/plain'
        }
      }).then(
      // success
      function(response) {
        utilService.clearError();
        console.debug('user = ', response.data);
        securityService.setUser(response.data);
        tabService.initializeTabsForUser(response);
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

    // end
  } ]);