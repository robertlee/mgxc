ultcrm.controller('timesegmentCtrl', function($scope,$state,$ionicPopup,$http,$interval,$stateParams,$ionicSlideBoxDelegate,$timeout,$ionicModal,appointmentService,customerDataService) {	
	// 将appointmentService 设置值
	//appointmentService.setBusinessTypeId(appointmentBusinessType.id);
	//appointmentService.setCourseId(appointmentTech.courseId);
	//appointmentService.setCourseName(appointmentTech.courseName);
	//appointmentService.setSeryName(appointmentTech.seryName);
	//appointmentService.setModelId(appointmentTech.modelId);
	//appointmentService.setModelName(appointmentTech.modelName);
	//appointmentService.setStoreId(appointmentStore.id);
	//appointmentService.setStoreName(appointmentStore.name);
	//appointmentService.setTechId(appointmentTech.id);
	$scope.selectdatetime = '2017-03-12';
	$scope.segmenttime = '';
	$scope.clicking = false;
	$scope.selectedTimesegmentlist=[];
	$scope.selectedCount=0;
	$scope.bCountStart=false;
	
	$scope.init = function() {	    
		var busiTypeId = 40;

		$scope.updateTimeSegment = null;
	    $scope.updateDateSegment = null;
		//初始化时， 根据前几步选择的值加载项目， 只有一个项目	
		var coachid = $stateParams.coachid;		
		$http.get("timesegment/store/1/" + coachid).success(function(data){		
			//转换今天/明天/后天
			
			var datanew = data.slice(2,4);
			datanew[0].pdateas = "明天";
			datanew[0].cdateas = "后天";			
			datanew[0].ndateas = datanew[0].ndate.substring(5);
			$scope.segmenttimes = datanew;	
			for(var i=1; i < 5;i++)
			{
			    datanew[i].pdateas = datanew[i].pdate.substring(5);
			    datanew[i].cdateas = datanew[i].date.substring(5);
			    if(datanew[i].ndate != null)
			    	{
			    	datanew[i].ndateas = datanew[i].ndate.substring(5);
			    	}
			}
		});
	  }
	
	$scope.getTimeClass = function(segmenttimeIndex,dateIndex, timeIndex ) {
		var date = $scope.segmenttimes[dateIndex];

		var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		
		if( $scope.updateTimeSegment != null)
		{
		    if($scope.updateDateSegment == date.date && $scope.updateTimeSegment == segment.time)
		    {
		    	return 'button button-block button-assertive';
		    }
		}
		if (segment.enable) {
			/*增加了time-selected，用于自定义选中状态css*/
			if($scope.bCountStart==true)
			{	var IndexVal=$scope.selectedTimesegmentlist.indexOf((dateIndex+timeIndex));
				if (segmenttimeIndex == (dateIndex+timeIndex)||(IndexVal>=0))
				{
					if (IndexVal>=0)
					{
						return 'button button-block bg-blue time-selected';
					}
					else{
						$scope.selectedTimesegmentlist[$scope.selectedCount]=dateIndex+timeIndex;
						$scope.selectedCount=$scope.selectedCount+1;
						return 'button button-block bg-blue time-selected';						
					}					
				}
				else
				{
					return'button button-block button-light'
				}
			}
			else
			
			{
			//return 'button button-block bg-blue time-selected';
				return segmenttimeIndex == (dateIndex+timeIndex)?'button button-block bg-blue time-selected':'button button-block button-light';
			}
		}
		else {
			return 'button button-block button-light';
		}
	}	
	$scope.isUsedAll = function(dateIndex, timeIndex)
	{
		 var date = $scope.segmenttimes[dateIndex];
		 var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		 if(segment.usedAll)
			 {
			     return true;
			 }else{
				 return false;
			 }
	}
		
	$scope.isDisable = function(dateIndex, timeIndex) {
		 var date = $scope.segmenttimes[dateIndex];
		 var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		 if (segment.enable) {
			 return false;
		 }
		 else {
			 return true;
		 }
	}
	
	$scope.getTimeByTimeIndex = function(date,timeIndex) {
		for (var i = 0 ; i < date.segments.length; i ++) {
			var segment = date.segments[i];
			if (segment.time == timeIndex) {
				return segment;
			}
		}
	}
	
	$scope.slideHasChanged = function($index){
	    if($index === 0){
		    // first box
	    }
	};
	
	$scope.gotoTimeSlide = function($index){
		  $ionicSlideBoxDelegate.slide($index);
	};
		
	$scope.selectSegment = function(dateIndex, timeIndex){
		console.log(segment);
		 var date = $scope.segmenttimes[dateIndex];
		 var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		 if (segment.enable) {
			 $scope.segmenttime = dateIndex+''+timeIndex;
			 
			 $scope.bCountStart=true;
		 }
	};
	
	
	//提交预约按钮对应的方法
	$scope.submitSegment = function(){
		console.log("enter submitSegment......");
		if($scope.segmenttime == '')
		{
		  	var alertPopup = $ionicPopup.show({
			   		cssClass: 'popup-custom popup-show',
			   		title: '<span class="iconfont icon-alert orange"></span>请选择一个时间段！'
		   });
		   $timeout(function() {
			   alertPopup.close(); 
			}, 1500);
			alertPopup.then(function(res) {
					  console.log('select timesegment is alterPop then ');
			});
		}
		
		$scope.clicking = true;
		
		var dateIndex = $scope.segmenttime.substring(0,1);//获取选择到的日期索引	
		var segmentDate = $scope.segmenttimes[dateIndex].date;//获取选择到的日期		
		var segmentTime = $scope.segmenttime.substring(1);  //获取时段
		appointmentService.setSegmentDate(segmentDate);
		appointmentService.setSegmentTime(segmentTime);	    		
		var customer = customerDataService.getCustomer();
		
			
		if (!customer.phone) {
			$scope.toModifyPhone();
		}else{
			$scope.createAppointment();
			
		}
		
		console.log(" go end submitSegment!")

	}
	
	$scope.modalModifyPhone = null;

	$ionicModal.fromTemplateUrl('tpl/my/modifyPhone.html', {
		  scope: $scope,
		  animation: 'slide-in-up',
		  hardwareBackButtonClose: true
		}).then(function (modal) {
		  $scope.modalModifyPhone = modal;
		});
	
	/**
	 * 校验手机号码的模块
	 */
	$scope.toModifyPhone = function(){
		
		  $scope.modalModifyPhone.show();
	};
		
	$scope.modifyPhoneOk = function(){
		alert('for test modify phone ok!');
	}
	
	//Cleanup the modal when we're done with it!
	$scope.$on('$destroy', function () {
		if($scope.modalModifyPhone != null)
			{
			    $scope.modalModifyPhone.remove();
			}
	});
	
	$scope.addTimeSegmentCount = function(segmentDate,segmentTime) {
		$http.post("/addTimeCount",{"storeId":"6010","dateSegment":segmentDate,"timeSegment":segmentTime}).success(function(data){
			console.log("add success!");
		});
	}	
	//确认预约，写到数据库中
    $scope.createAppointment= function() {    	
		var customer = customerDataService.getCustomer();
		var appointData = appointmentService.getAppointData();
		var techId = appointmentService.getTechId();
		var busiTypeId = appointmentService.getBusinessTypeId();
		$http.post('/createAppointment',{"customerId":customer.id,
			"busiTypeId": 40,
			"techId":30,
			"courseId":1,
			"modelId":1,
			"discountTotalPrice":120,
			"totalPrice":120,
			"typeId":"1",
			"segmentDate":appointData.segmentDate,
			"segmentTime":appointData.segmentTime,
			"packageIds":"",
			"storeId":"1"}).success(function(data,status,headers,config){
			if(data.code == 'appook')
			{
				console.log("add appointment success!");
				

				$state.go("index.appointmentok",{orderId:data.orderId,hasCard:data.hasCard}, {reload: true});
				
			
			} else if(data.code == 'appoexist')
			{
			  	var alertPopup = $ionicPopup.show({
			   		cssClass: 'popup-custom popup-show',
			   		title: '<span class="iconfont icon-alert orange"></span>不能重复预约！'
			  	});
			   $timeout(function() {
				   alertPopup.close(); 
				}, 1500);
				alertPopup.then(function(res) {
				    console.log('Thank you for new appointment');
										    });
			}
		}).error(function(data,status,headers,config) {
			$scope.clicking = true;		        	
		});			  
	
	};
	

    //跳转到确认预约
	$scope.toOk = function (data) {	
	    $state.go("index.appointmentok",{}, {reload: true}); 
	};
	$scope.isBtnDisable = function() {	
		if (!$scope.segmenttime) {
			return true;
		}
		else {
			if ($scope.clicking) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
});
