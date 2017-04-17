ultcrm.controller('helpCtrl', function($scope,$http,$location,$state,$stateParams,$ionicPopup) {
	//前往首页页面
	$scope.goHomePage = function(){
		$state.go('index.home',{},{reload:true});
	}
});