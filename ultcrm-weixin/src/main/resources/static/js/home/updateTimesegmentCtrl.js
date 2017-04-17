ultcrm.controller('updateTimesegmentCtrl', function($scope,$state,$ionicPopup,$http,$interval,$stateParams,$ionicSlideBoxDelegate,$timeout,$ionicModal,appointmentService,customerDataService,customerData) {	

	$scope.hasOrder = false;
	$scope.clicked = false;
	$scope.selectedTimesegmentlist=[];
	$scope.coachid = 1 ;//$stateParams.coachid;
	$scope.busitypeid = 10;//$stateParams.busiTypeId;
	$scope.orderId = $stateParams.orderId;
	$scope.cost = 0;
	$scope.needPay = 0;
	console.log($scope.coachid);
	console.log($scope.busitypeid);
	$scope.init = function() {
		
		var id;
		if($scope.busitypeid=10){
			id = 40;
		}else if($scope.busitypeid=11){
			id = 41;
		}
		//获取对应学时学车的单价
		$http.get("/getBusinessById/"+id).success(function(result){
			console.log(result);
			$scope.cost = result.cost;
			$scope.businessType = result;
		});
		
		$http.get("/searchTimesegmentsByOrderId/" + $scope.orderId).success(function(data){
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
	}
		
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
		
		$http.post('/updateTimeSegment',{"orderid":$scope.orderId,
			"date_segment":$scope.segmentDate,
			"timeSegment":$scope.segmentTime,
			"count":$scope.count}).success(function(data){
				if(data.code == '200'){
					$state.go('index.myorderList',{'viewType':'new'},{reload:true});
				} else if(data.code == 'error'){
					alert(data.msg);
				}
			}).error(function(data,status,headers,config) {
				alert("修改失败");
			});	
		
		console.log(" go end submitSegment!")
	}
	
	$scope.isBtnDisable = function() {	
		if ($scope.selectedTimesegmentlist.length == 0) {
			//如果没有选择任何时间段,不可以点击预约按钮
			return true;
		} else {
			return false;
		}
	}
	
});
