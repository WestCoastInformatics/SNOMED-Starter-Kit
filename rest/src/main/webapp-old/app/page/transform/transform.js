// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/transform', {
    controller : 'TransformCtrl',
    templateUrl : 'app/page/transform/transform.html'
  });
});

// Controller
ttApp.controller('TransformCtrl', function($scope, $filter, NgTableParams, transformService,
  utilService, gpService) {
  console.debug('configure TransformCtrl');

  // Scope variables
  $scope.terminologyObj = {
    value : {
      name : 'RXNORM',
      version : '15AB_160104F'
    },
    values : [ {
      name : 'All',
      version : ''
    }, {
      name : 'RXNORM',
      version : '15AB_160104F'
    } ]
  };

  // Transform
  $scope.transform = function(inputStr, selectedTerminology) {
    // Setup data context
    var dataContext = {
      terminology : null,
      version : null,
      dataType : null,
      customer : null,
      semanticType : null,
      specialty : null
    };
    if ($scope.terminologyObj.value.name !== 'All') {
      dataContext.terminology = $scope.terminologyObj.value.name;
      dataContext.version = $scope.terminologyObj.value.version;
    }

    // Transform
    transformService.transform(inputStr, dataContext).then(function(response) {
      $scope.tpResults = new NgTableParams({}, {
        dataset : response.results
      });
    });
  };

  // end

});