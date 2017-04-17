ultcrm.controller('timesegmentCtrl', function($scope,$state,$ionicPopup,$http,$interval,$stateParams,$ionicSlideBoxDelegate,$timeout,$ionicModal,appointmentService,customerDataService,customerData) {	

	$scope.hasOrder = false;
	$scope.clicked = false;
	$scope.showAppoint = false;
	$scope.showPayAndAppoint = false;
	$scope.selectedTimesegmentlist=[];
	$scope.coachid = $stateParams.coachid;
	$scope.busitypeid = $stateParams.busiTypeId;
	if($scope.busitypeid>11||$scope.busitypeid<10){
		$scope.showAppoint = true;
	}else{
		$scope.showPayAndAppoint = true;
	}
	$scope.cost = 0;
	$scope.needPay = 0;
	console.log($scope.coachid);
	console.log($scope.busitypeid);
	
	$scope.init = function() {
		$http.get("/findExistOrder/"+customerDataService.getCustomerId()).success(function(result){
			console.log(result);
			if(result == ''){
				$scope.hasOrder = false;
				alert("您还没有报名,请先报名后再预约");
			}else{
				$scope.hasOrder = true;
			}
		});
		var id=0;
		if($scope.busitypeid==10||$scope.busitypeid==1){
			id = 40;
		}else if($scope.busitypeid==11||$scope.busitypeid==2){
			id = 41;
		}
		//获取对应学时学车的单价
		$http.get("/getBusinessById/"+id).success(function(result){
			console.log(result);
			$scope.cost = result.cost;
			$scope.businessType = result;
		});
		
		$http.get("/timesegment/store/" + $scope.coachid).success(function(data){
			//转换今天/明天/后天
			console.log(data);
			var datanew = data.slice(2,5);
			datanew[0].pdateas = " ";
			datanew[0].cdateas = "后天";
			datanew[0].ndateas = datanew[0].ndate.substring(5);
			$scope.segmenttimes = datanew;
			for(var i=1; i < 3;i++){
				datanew[i].pdateas = datanew[i].pdate.substring(5);
				datanew[i].cdateas = datanew[i].date.substring(5);
				if(datanew[i].ndate != null){
					datanew[i].ndateas = datanew[i].ndate.substring(5);
				}
			}
		});
	}
	$scope.FailedOrder=function(orderId){
		if (orderId!=null)
		{
			$http.get('/delOrder/' + orderId).success(function(result) {
			});		      
		};
	}
	$scope.isUsedAll = function(dateIndex, timeIndex){
		var date = $scope.segmenttimes[dateIndex];
		var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		if(segment.usedAll){
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
		} else {
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
	
	//切换了日期,清空选择的时间点并初始化按钮样式
	$scope.slideHasChanged = function($index){
		console.log('改变了'+$index);
		$scope.selectedTimesegmentlist =[];
		for (var i = 0 ; i < $scope.segmenttimes.length; i ++) {
			var date = $scope.segmenttimes[i];
			for (var j = 0 ; j < date.segments.length; j ++) {
				var segment = date.segments[j];
				if(segment.choose == true){
					segment.choose = false;
					$("#"+i+(j+6)).attr("class", "button button-block button-light");
				}
			}
		}
	};
	
	$scope.gotoTimeSlide = function($index){
		console.log($index);
		$ionicSlideBoxDelegate.slide($index);
	};
	
	$scope.updateChooseByTimeIndex = function(dateIndex,timeIndex) {
		var date = $scope.segmenttimes[dateIndex];
		for (var i = 0 ; i < date.segments.length; i ++) {
			var segment = date.segments[i];
			if (segment.time == timeIndex) {
				if(segment.choose == true){
					segment.choose = false;
				}else{
					segment.choose = true;
				}
				//$scope.segmenttimes[dateIndex] = date;
			}
		}
                  };
		
	$scope.selectSegment = function(dateIndex, timeIndex){
		console.log(segment);
		var date = $scope.segmenttimes[dateIndex];
		var segment = $scope.getTimeByTimeIndex(date,timeIndex);
		if (segment.enable) {
			$scope.updateChooseByTimeIndex(dateIndex,timeIndex)
			$scope.segmenttime = dateIndex+''+timeIndex;
			console.log($scope.segmenttimes[dateIndex].segments[timeIndex-6].choose);
			if($scope.segmenttimes[dateIndex].segments[timeIndex-6].choose == true){
				$("#"+dateIndex+timeIndex).attr("class", "button button-block bg-blue time-selected");
				$scope.selectedTimesegmentlist.push(dateIndex+''+timeIndex);
				
			}else{
				$("#"+dateIndex+timeIndex).attr("class", "button button-block button-light");
				$scope.deleteElementByIndex($scope.selectedTimesegmentlist,dateIndex+''+timeIndex);
			}
			console.log($scope.selectedTimesegmentlist);
			console.log($scope.selectedTimesegmentlist.length);
		}
		$scope.needPay = $scope.cost * $scope.selectedTimesegmentlist.length;
	};
	//获取数组中某个元素的下标
	$scope.getElementIndex = function(array,element){
		for (var i = 0; i < array.length; i++) {
			if (array[i] == element){
				return i;
			} 
		}
		return -1;
	}
	//删除数组中某个元素
	$scope.deleteElementByIndex = function(array,element){
		var index = $scope.getElementIndex(array,element);
		if (index > -1) {
			array.splice(index, 1);
		}
	}
	//对数组进行排序
	$scope.sortArray = function(array){
		for (var i = 0; i < array.length-1; i++) {
			for(var j = 0; j < array.length; j++){
				if (array[j+1] < array[j]){
					var temp;
					temp = array[j+1];
					array[j+1] = array[j];
					array[j] = temp;
				} 
			}
			
		}
	}
	//提交预约按钮对应的方法
	$scope.submitSegment = function(){
		var tempArray = [];
		console.log("enter submitSegment......");
		
		for(var i=0;i<$scope.selectedTimesegmentlist.length;i++){
			var element = Number($scope.selectedTimesegmentlist[i].substring(1));
			tempArray.push(element);
			if(i == $scope.selectedTimesegmentlist.length -1){
				if(tempArray.length>1){
					$scope.sortArray(tempArray);
					for(var j=0;j<tempArray.length;j++){
						if(j<tempArray.length-1){
							if(tempArray[j+1]-tempArray[j]!=1){
								alert("请选择连续的时间段");
								return;
							}
						}
						
					}
				}
				
			}
		}
		
		//获取选择到的日期索引
		$scope.dateIndex = $scope.selectedTimesegmentlist[0].substring(0,1);
		//获取选择到的日期
		$scope.segmentDate = $scope.segmenttimes[$scope.dateIndex].date;
		//获取预约的开始时间点
		$scope.segmentTime = tempArray[0];
		//预约时长
		$scope.count = $scope.selectedTimesegmentlist.length;
	
		console.log($scope.dateIndex);
		console.log($scope.segmentDate);
		console.log($scope.segmentTime);
		console.log($scope.count);
		if($scope.busitypeid==11 || $scope.busitypeid==10){
			var str = "{classId:" + $scope.businessType.id 
				+ ",className:'" + $scope.businessType.name + "',price:" + $scope.needPay
				+ ",contactphone:'" + customerData.phone + "',totalPrice:" + $scope.needPay 
				+ ",openId:'" + customerData.openid + "',payType:'appointment'}";
			$http.get('/createPayOrder/' + str).success(function(data) {
                if(data.code == "error"){
                    alert(data.msg);
                    return;
                }
				var orderId=data.orderId;
                if(data.charge != null){
                    pingpp.createPayment(data.charge, function(result, err) {                        
						if (result=="success") {
                            //发送通知
                            //$scope.SendOrderMessage(orderId,str);
							$http.post('/createTimeSegmentByOrder',{"coachid":$scope.coachid,
									"customerid": customerDataService.getCustomerId(),"date_segment":$scope.segmentDate,
									"timeSegment":$scope.segmentTime,"count":$scope.count,"orderId":orderId}).success(function(data){

							//$http.post('/createTimeSegmentByOrder',{"coachid":coachid,"customerid": customerId,
                            //                        "date_segment":segmentDate,"timeSegment":segmentTime,"count":count,"orderId":orderId
                            //                        })
                            //            .success(function(data){
                                                  if(data.code == '200'){
                                                  console.log(data.orderId);
                                                  console.log(data.hasCard);
                                                  $state.go("index.appointmentok",{orderId:data.orderId,hasCard:data.hasCard}, {reload: true});
                                                  }
                                                  else if(data.code == 'error'){
                                                  $scope.FailedOrder(orderId);
                                                  alert(data.msg);
                                                  }
                                                  })
                                         .error(function(data,status,headers,config) {
                                                alert("预约失败,请联系客服!");
                                                });
                        }
                        else {
                            alert(className + "支付失败");
                            clickNum = 0;
                            $scope.FailedOrder(orderId);
                            //发送通知
                            //$scope.sendMsg(orderId,openId,className,roomName,'2016-7-1');
                            //Robert Lee  Debug Basic Information
                        }
                    }, data.signature, false);
                }
                else{
                    alert(className + "支付失败");
                    $scope.FailedOrder(orderId);
                    //Robert Lee  Debug Basic Information
                }
			});
		}
        else{
            $http.post('/createTimeSegment',{"coachid":$scope.coachid,
            				"customerid": customerDataService.getCustomerId(),"date_segment":$scope.segmentDate,
            				"timeSegment":$scope.segmentTime,"count":$scope.count}).success(function(data){
            				if(data.code == '200'){
            					console.log(data.orderId);
            					console.log(data.hasCard);
            					$state.go("index.appointmentok",{orderId:data.orderId,hasCard:data.hasCard}, {reload: true});
            				} else if(data.code == 'error'){
            					alert(data.msg);
            				}
            			}).error(function(data,status,headers,config) {
            				alert("预约失败");
            			});
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
		if ($scope.selectedTimesegmentlist.length == 0) {
			//如果没有选择任何时间段,不可以点击预约按钮
			return true;
		} else {
			if ($scope.hasOrder) {
				//如果有订单,可以点击预约按钮
				return false;
			}else {
				//如果没有订单,不可以点击预约按钮
				return true;
			}
		}
	}
	
});