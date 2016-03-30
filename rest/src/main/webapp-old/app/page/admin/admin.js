// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/admin', {
    controller : 'AdminCtrl',
    templateUrl : 'app/page/admin/admin.html'
  });
});

ttApp.controller('AdminCtrl',
  function($scope, $filter, sourceDataService, FileUploader, NgTableParams) {
    console.debug('configure AdminCtrl');
    
    // n/a
    
  });