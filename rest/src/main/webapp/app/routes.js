// Route
tsApp.config(function config($routeProvider) {
  
  // TODO -- Change this to '/' once landing page complete
  $routeProvider.when('/configure', {
    templateUrl : 'app/page/configure/configure.html',
    controller : 'ConfigureCtrl',
    reloadOnSearch : false,
  });

  // Source Data Configurations
  $routeProvider.when('/source', {
    controller : 'SourceCtrl',
    templateUrl : 'app/page/source/source.html'
  });

  // Content -- Default Mode
  $routeProvider.when('/content', {
    templateUrl : function() {
      return 'app/page/content/content.html';
    },
    controller : 'ContentCtrl',
    reloadOnSearch : false
  });

  // Content with mode set (e.g. 'simple' for component report)
  $routeProvider.when('/content/:mode/:terminology/:version/:terminologyId', {
    templateUrl : function(urlAttr) {
      return 'app/page/content/' + urlAttr.mode + '.html';
    },
    controller : 'ContentCtrl',
    reloadOnSearch : false
  });

  // Metadata View

  $routeProvider.when('/metadata', {
    templateUrl : 'app/page/metadata/metadata.html',
    controller : 'MetadataCtrl',
    reloadOnSearch : false
  });
  
  $routeProvider.when('/', {
    redirectTo: '/login'
  });
  
  $routeProvider.when('/login', {
    templateUrl : 'app/page/login/login.html',
    controller : 'LoginCtrl',
    reloadOnSearch : false
  });
 
  $routeProvider.otherwise({
    redirectTo: '/content'
  });
  
});
