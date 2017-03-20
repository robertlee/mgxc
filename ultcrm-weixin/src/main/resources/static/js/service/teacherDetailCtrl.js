//home页面的控制器
ultcrm.controller('teacherDetailCtrl', function($scope,$http,$state,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentStore,customerDataService) {
	
	$scope.storeList = new Array();
	
	// 获取门店信息
	$http({method:"GET",url:"/getStoreList", cache: true}).success(function(data){
		$scope.storeList = data;
		console.log(data);
	});
	
	$scope.selectStore = function(item){
		// 返回到服务页面
		appointmentStore.id = item.id;
		appointmentStore.name = item.name;
	//	$state.go('index.coachlist',{},{reload:true});
	}
	// 导航
	$scope.toDirect = function(){
		$state.go("index.direct"); 
	}
		
});

