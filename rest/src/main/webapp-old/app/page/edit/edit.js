// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/edit', {
    controller : 'EditCtrl',
    templateUrl : 'app/page/edit/edit.html'
  });
});

ttApp.controller('EditCtrl',
  function($scope, $filter, sourceDataService, FileUploader, NgTableParams) {
    console.debug('configure EditCtrl');
    
    // n/a
  });