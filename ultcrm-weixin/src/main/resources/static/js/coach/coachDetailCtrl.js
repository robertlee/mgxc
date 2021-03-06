//home页面的控制器
ultcrm.controller('coachDetailCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentStore,customerDataService,defaultURL) {
	
	$scope.id=$stateParams.id;
	console.log($scope.id);
	
	$http.get('/getCoachById/'+$scope.id).success(function(result) {    
		console.log(result);
		$scope.coachDetail = result;
		
		$http.get('/getStoreById/'+$scope.id).success(function(result) {    
			console.log(result);
			$scope.store = result;
			
			
			
		}).
			error(function() {
			$scope.store = [];//清空select
		});	;			
		
		
    }).
	    error(function() {
    	$scope.coachDetail = [];//清空select
    });	;	
	
	
	$scope.goBack = function(){
		$state.go("index.coachlist"); 
	}
	$scope.toServicePage = function(id){
		$state.go("index.timesegment",{id:id},{reload:true}); 
	}	
	$scope.toLocation = function(id){
		//$state.go('index.coachlist',{},{reload:true});
		//itemType为2表示训练场跳至导航页面
		$state.go("index.location",{itemType:2,id:id},{reload:true}); 
	}	
});

