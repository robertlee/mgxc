//home页面的控制器
ultcrm.controller('service23Ctrl', function($scope,$stateParams,$http,$state,$window,$location) {
	//获取上个页面传过来的id
	$scope.id = $stateParams.id;
	
	//获取业务信息
	$http.get("/getBusinessById/"+$scope.id).success(function(res){
		$scope.business = res;
		console.log(res);
	});
	$scope.goHomePage = function(){
		$state.go('index.home',{},{reload:true});
	}	
	$scope.goServiceStore = function(){
		console.log(1);
		$state.go('index.serviceStore',{},{reload:true});
	}
	
});

