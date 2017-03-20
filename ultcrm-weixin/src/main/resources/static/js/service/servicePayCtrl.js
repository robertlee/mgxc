ultcrm.controller('servicePayCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentService,customerDataService) {
	var clickNum = 0;
	var classId = 0;
	var jsonStr = null;
	var customerId = 0;
	var openId = null;
	$scope.contactPhone ="电话号码";
	
// 获取订单信息
	$scope.getData=function(){
	    jsonStr = $stateParams.jsonStr;
		if(jsonStr != null){
			var jsonObj = eval("(" + jsonStr + ")");
			classId = (jsonObj["classId"]);
			var className = (jsonObj["className"]);			
			var price = (jsonObj["price"]);
			var phone = (jsonObj["phone"]);
			openId = jsonObj["openId"];
			if(className != null && className != "undefined"){
				$scope.className = className;
			}
			if(price != null && price != "undefined"){
				$scope.priceOne = "￥" + price;
				$scope.totalPrice = "￥" + price;
				$scope.actualPrice = "￥" + price;
				$scope.contactPhone= phone;
			}

		}
	};
	//支付失败时删除订单
	$scope.FailedOrder=function(orderId){
		if (orderId!=null)
		{
			$http.get('/delOrder/' + orderId + '/' + seatId).success(function(result) {
			});		      
		};
	};
	$scope.SendOrderMessage=function(orderId,str){
		if (orderId!=null)
		{
			$http.get('/getMessageTemplate/' + orderId + '/' + str).success(function(result) {
			}		      
			).error(function() {											
				alert("报名订单模板消息发送失败");	
			});
		};
	};	
	

	$scope.VerifyOrderDataStatus=function(orderId){	
		$http.get('/getOrderInfo/'+orderId).success(function(data, status, headers, config){
			$scope.orderInfo.orderId = data.orderId;			
			$scope.orderInfo.classname = data.classname;								
			}).error(function(data, status, headers, config) {
			//发生错误，返回订单列表
			alert("获取课程订单信息失败");
				
		});
	};
	//立即支付
	 function pay(){		
		//禁用支付按钮
		var jsonObj = eval("(" + jsonStr + ")");
		var classId = jsonObj["classId"];//课程编号
		var className = jsonObj["className"];//课程名称
		var price = jsonObj["price"];//单价
		var contactphone = $scope.contactPhone
		var str = "{classId:" + classId 
				   + ",className:'" + className 
		           + "',price:" + price
				   + ",contactphone:'"  +contactphone 
				   + "',totalPrice:" + price + ",openId:'" + openId + "'}";
		$http.get('/createPayOrder/' + str).success(function(data) {
			var orderId = data.orderId;		
			if(data.charge != null){
				pingpp.createPayment(data.charge, function(result, err) {
		    	    if (result=="success") {
		    	    	//发送通知						
						$scope.SendOrderMessage(orderId,str);
		    	    	$state.go('index.myorderList',{'viewType':'new'},{reload:true});												
		    	    } else {						
		    	    	$scope.FailedOrder(orderId);
		    	        alert(className+"支付失败");
		    	        //发送通知
						//$scope.sendMsg(orderId,openId,className,roomName,'2016-7-1');
                        //Robert Lee  Debug Basic Information
						clickNum = 0;						
		    	    }
				}, data.signature, false);
			}
			else{
				$scope.FailedOrder(orderId);				
				alert("支付结果：" + data.msg);
				//Robert Lee  Debug Basic Information 
			}
        }).
        error(function() {
			$scope.FailedOrder(orderId);
        	alert("下单失败");
			clickNum = 0;
			//Robert Lee  Debug Basic Information         	
        });
	};
	//发送通知
	$scope.sendMsg=function(id,openId,className,classAddr,datatime){
           //notifycationSuccess(orderId,openId,className,SeatSpec,startTime);		
	};
	$scope.toMy=function(){	
		clickNum = 0;	
		$state.go('index.modifyPhone',{customerId:customerId,str:null,jsonStr:jsonStr}, {reload: true}); 
	};
	$scope.toPay=function(){
		clickNum += 1;
		if(clickNum > 1){			
			return;
		}
		else{		
			//若手机号为空或者未选择学院信息，停止定时器
			if($scope.contactPhone == null || $scope.contactPhone == ""){
				if($scope.contactPhone == null || $scope.contactPhone == ""){
					alert("请绑定联系电话");
				}								
				clickNum = 0;				
			}
			else{
				clickNum = 1;	
				pay();
				clickNum = 0;	
				//开启支付
			}			
		}
	};
	$scope.getData();

});

