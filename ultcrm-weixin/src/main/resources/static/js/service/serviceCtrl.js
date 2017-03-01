/*报名页面的控制器
[ {
  "id" : 1,
  "createTime" : "2016-08-07T15:23:26.000+0000",
  "createUserId" : 1,
  "lastUpdateTime" : null,
  "lastUpdateUserid" : null,
  "linkurl" : "",
  "pic" : "/img/activity/ad_0.jpg",
  "tip" : "南昌乐天教育培训中心",
  "title" : "2016年秋季快乐语文报纸一",
  "typeid" : null,
  "picType" : null
}, {
  "id" : 2,
  "createTime" : "2016-08-08T12:12:49.000+0000",
  "createUserId" : 1,
  "lastUpdateTime" : null,
  "lastUpdateUserid" : null,
  "linkurl" : "",
  "pic" : "/img/activity/ad_1.jpg",
  "tip" : "南昌乐天教育培训中心",
  "title" : "2016年秋季快乐语文报纸二",
  "typeid" : null,
  "picType" : null
} ]
*/
ultcrm.controller('serviceCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentService,customerDataService) 
{	
	$http.get('/getAdvertiseList').success(function(result) {    
		$scope.activityList=result;    	
    }).
    error(function() {
    	$scope.activityList = [];//清空select
    });	
	$scope.toActivityDetail=function(index){
		console.log(index);
		//$state.go('index.activityDetail',{id:index},{reload:true});
	};
	

});


