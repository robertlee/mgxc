ultcrm.controller('homeCtrl', function($scope,$http,$location,$state,$stateParams,$ionicPopup,defaultURL) {
	//初始化数据
	$scope.defaultURL = defaultURL.url+":"+defaultURL.port+defaultURL.path;
	
	//导航切换函数
	$http.get('/getCoachFirst3').success(function(result) {
        	//先清空原始数据
        	if(result != null)
	        {
				$scope.coachlist=result;					
	        }
        }).
        error(function() {
        	$scope.coachlist = null;
        });
	$scope.click_service=function(){
		
		$state.go('index.serviceList',{},{reload:true});
	};
	//展示教练详情
	$scope.click_ShowAllCoach=function(){
		$state.go('index.coachlist',{},{reload:true});
	};
	//展示课程详情
	$scope.showCoachDetail=function(id){
		$state.go('index.coachDetail',{id:id},{reload:true});
	};	
	$scope.click_store=function(){
		$state.go('index.serviceStore',{},{reload:true});
	};	
	
});