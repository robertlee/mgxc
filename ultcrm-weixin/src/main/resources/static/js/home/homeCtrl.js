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
	
	//前往学车流程页面
	$scope.goProcess = function(){
		$state.go('index.process',{},{reload:true});
	}
	//前往线下报名点页面
	$scope.goServiceSite = function(){
		$state.go('index.serviceSite',{},{reload:true});
	}
	//前往关于芒果页面
	$scope.goAbout = function(){
		$state.go('index.about',{},{reload:true});
	}
	//前往帮助问答页面
	$scope.goHelp = function(){
		$state.go('index.help',{},{reload:true});
	}
	//前往关于芒果页面
	$scope.goGuarantee = function(){
		$state.go('index.guarantee',{},{reload:true});
	}
});