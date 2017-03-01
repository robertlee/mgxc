ultcrm.controller('homeCtrl', function($scope,$http,$location,$state,$stateParams,$ionicPopup) {
	//初始化数据

	//导航切换函数

	$scope.click_service=function(){
		
		$state.go('index.service',{},{reload:true});
	};
	//展示教练详情
	$scope.click_teacher=function(id){
		if(id==0)
		{
			$state.go('index.coachlist',{},{reload:true});
		}
		else
		{
			$state.go('index.teacherDetail',{id:id},{reload:true});
		}
		
	};
	//微信内部跳转
	$scope.jumpToUrl = function(path) {
		$location.path(path);
		var curUrl = $location.absUrl();	
	};
	//展示课程详情
	$scope.showCourseDetail=function(index){
		$state.go('index.courseDetail',{index:index},{reload:true});
	};	
});