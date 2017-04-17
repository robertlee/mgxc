ultcrm.controller('serviceSiteCtrl', function($scope,$http,$location,$state,$stateParams,$ionicPopup) {
	
	$http.get("/getAddressList").success(function(res){
		$scope.addressList = res;
		console.log(res);
	});
	
	//前往首页页面
	$scope.goHomePage = function(){
		$state.go('index.home',{},{reload:true});
	}
	
	$scope.goLocation = function(id){
		//itemType为1表示报名网点跳至导航页面
		$state.go("index.location",{itemType:1,id:id},{reload:true}); 
	}
});