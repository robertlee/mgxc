/*报名页面的控制器

*/
ultcrm.controller('serviceListCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentService,customerData,defaultURL) 
{	
	$scope.defaultURL = defaultURL.url+":"+defaultURL.port+defaultURL.path;
	$http.get('/getBusinessAllList').success(function(result) {    
		$scope.businessList=result;    	
    }).
    error(function() {
    	$scope.businessList = [];//清空select
    });	
	
	$scope.click_buy=function(index){
		
			    var inform= $scope.businessList[index];
				 	
				var openId = customerData.openid;
				var phone =customerData.phone;
				var jsonStr = "{";
				jsonStr += "classId:" + inform.id +
						   ",className:'" + inform.name +	
							"',price:" + inform.cost +
							",phone:'" + phone +	
						   "',openId:'" + openId + 
						   "'}";						
				$state.go('index.servicePay',{jsonStr:jsonStr},{reload:true});	

			
		};

});


