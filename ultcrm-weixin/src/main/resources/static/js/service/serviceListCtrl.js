/*报名页面的控制器

*/
ultcrm.controller('serviceListCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentService,customerData,defaultURL) 
{	
	$scope.defaultURL = defaultURL.url+":"+defaultURL.port+defaultURL.path;
	$http.get('/getBusinessAllList').success(function(result) {    
		$scope.businessList=result;    	
		console.log(result);
    }).
    error(function() {
    	$scope.businessList = [];//清空select
    });	
	
	$scope.click_buy=function(index,$event){
		
				$event.stopPropagation();
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
	$scope.click_order=function(){
		$event.stopPropagation();
		$state.go('index.coachlist',{},{reload:true});
	};			
		
	//跳至详细报名服务页面
	$scope.goPaperService = function(id){
		if(id==10 || id==11){
			$state.go('index.paperService01',{id:id},{reload:true});
		}else if(id==20 || id==21){
			$state.go('index.paperService23',{id:id},{reload:true});
		}else if(id==30 || id==31){
			$state.go('index.paperService45',{id:id},{reload:true});
		}else if(id==40 || id==41){
			$state.go('index.paperService67',{id:id},{reload:true});
		}
		
	}
});


