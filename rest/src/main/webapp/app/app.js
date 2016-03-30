'use strict';

// Declare module
var ttApp = angular.module('ttApp',
  [ 'ngRoute', 'ui.bootstrap', 'ngCookies', 'ngTable', 'angularFileUpload' ]).config(
  function($rootScopeProvider) {

    // Set recursive digest limit higher to handle very deep trees.
    $rootScopeProvider.digestTtl(17);
  });

// Declare top level URL vars
var securityUrl = 'security';
var fileUrl = 'file';
var transformUrl = 'transform';

// Initialization of ttApp
ttApp.run([ '$rootScope', '$http', '$location', '$window', 'securityService', 'tabService',
  function($rootScope, $http, $location, $window, securityService, tabService) {

    // on load check for authToken
    console.log("Checking authentication credentials...");
    var user = securityService.getUser();
    console.debug("  user = ", user);

    // if authentication token found
    if (user.authToken) {

      // make test retrieval call witih this auth token
      securityService.getUserForAuthToken().then(function() {
        console.log("Authentication credentials found and valid.");
        tabService.initializeTabsForUser(user);
      }, function(error) {
        console.log("Authentication credentials found but invalid, routing to login page");
        $location.url('login');
      });

    } else {
      console.log("Not logged in, routing to login page");
      $location.url('login');
    }

  } ]);

// Route provider configuration
ttApp.config([ '$routeProvider', '$logProvider', function($routeProvider, $logProvider) {
  console.debug('configure $routeProvider');
  $logProvider.debugEnabled(true);

  // Configure route provider
  $routeProvider.when('/', {
    templateUrl : 'app/page/login/login.html',
    controller : 'LoginCtrl',
    reloadOnSearch : false
  });

  // $locationProvider.html5Mode(true);

} ]);

// Header controller
ttApp.controller('HeaderCtrl', [ '$scope', '$location', '$http', 'securityService',
  function($scope, $location, $http, securityService) {
    console.debug('configure HeaderCtrl');

    // Declare user
    $scope.user = securityService.getUser();

    // Logout method
    $scope.logout = function() {
      securityService.logout();
    };

    // Open help page dynamically
    $scope.goToHelp = function() {
      var path = $location.path();
      path = '/help' + path + '?authToken=' + $http.defaults.headers.common.Authorization;
      var currentUrl = window.location.href;
      var baseUrl = currentUrl.substring(0, currentUrl.indexOf('#') + 1);
      var newUrl = baseUrl + path;
      var myWindow = window.open(newUrl, 'helpWindow');
      myWindow.focus();
    };

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    };

  } ]);

// Footer controller
ttApp.controller('FooterCtrl', [ '$scope', 'gpService', 'securityService',
  function($scope, gpService, securityService) {
    console.debug('configure FooterCtrl');
    // Declare user
    $scope.user = securityService.getUser();

    // Logout method
    $scope.logout = securityService.logout;

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    };

  } ]);

// Tab controller
ttApp.controller('TabCtrl', [ '$scope', '$interval', '$timeout', 'securityService', 'tabService',
  function($scope, $interval, $timeout, securityService, tabService) {
    console.debug('configure TabCtrl');

    // Setup tabs
    // $scope.tabsViewed = tabService.getTabsForUser(securityService.getUser());

    // Set selected tab (change the view)
    $scope.setSelectedTab = function(tab) {
      tabService.setSelectedTab(tab);
    };

    // sets the selected tab by label
    // to be called by controllers when their
    // respective tab is selected
    this.setSelectedTabByLabel = function(label) {
      for (var i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].label === label) {
          this.selectedTab = this.tabs[i];
          break;
        }
      }
    };

    // Set 'active' or not
    $scope.tabClass = function(tab) {
      if (tabService.selectedTab == tab) {
        return 'active';
      } else {
        return '';
      }
    };

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    };

    // for ng-show
    $scope.isAdmin = function() {
      return securityService.isAdmin();
    };

    // for ng-show
    $scope.isUser = function() {
      return securityService.isUser();
    };

  } ]);

// Simple glass pane controller
ttApp.controller('GlassPaneCtrl', [ '$scope', 'gpService', function($scope, gpService) {
  console.debug('configure GlassPaneCtrl');

  $scope.glassPane = gpService.glassPane;

} ]);

// Simple error controller
ttApp.controller('ErrorCtrl', [ '$scope', 'utilService', function($scope, utilService) {
  console.debug('configure ErrorCtrl');

  $scope.error = utilService.error;

  $scope.clearError = function() {
    utilService.clearError();
  };

  $scope.setError = function(message) {
    utilService.setError(message);
  };

} ]);