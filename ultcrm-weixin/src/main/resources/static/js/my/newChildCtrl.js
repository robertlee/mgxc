ultcrm.controller('newChildCtrl', function($scope,$state,$stateParams,$http) {
	//传递参数
	var str = $stateParams.str;	
	$scope.data = {
				mobilephone:'',
				childName:''};	
	if(str != null && str != "undefined"){
		var jsonObj = eval("(" + str + ")");
		$scope.hiddenChildId = jsonObj["childId"];
		type = jsonObj["type"];
		$scope.data.mobilephone= jsonObj["mobilephone"];
		$scope.data.childName= jsonObj["childName"];
	}
	else
	{
		var type = "new";
	}
	var customerId = $stateParams.customerId;
	$scope.reBack = function() {
		$state.go('index.myindex',{},{reload:true});
	};	
    $scope.resetPhone=function(){
    	$scope.data.mobilephone = "";

    };	
	// 保存数据并且返回
	$scope.saveData = function() {


		var childName = $scope.data.childName 
		var mobilephone = $scope.data.mobilephone

		
		if (childName == null || childName == ""||mobilephone==""||mobilephone==null) {
			alert("没有教练姓名或者电话号码，请重新输入！");
			return;
		}
		
		var childId = $scope.hiddenChildId;
		if(childId == null){
			childId = 0;
		}
		$http.get("/createChildForCustomer/" + customerId +"/" + childId + "/" 
				+ childName + "/" + mobilephone + "/" + type).success(function(data){
			// 如果返回了空的字符串， 表示没有添加成功，有重复的添加行为
		    if (mobilephone == "" || mobilephone == null) {
		    	alert("该教练信息输入错误，请确认！");
		    	$scope.clicking = false;
		    	return;
		    }
		    else if(mobilephone == "0"){
		    	if(type == "new"){
		    		alert("新增失败");
					console.error("Add Child failed!");
		    	}
		    	else if(type == "edit"){
		    		alert("编辑失败");
					console.error("Edit Child failed!");
		    	}
		    }
		    else if(data == "1"){
		    	if(type == "new"){
		    		//alert("新增成功");
					console.log("Add Child successfully!");
		    	}
		    	else if(type == "edit"){
					//alert("编辑成功");
					console.log("Edit Child successfully!");
		    		
		    	}
		    }
			if ($stateParams.jsonStr==null)
			{
				$state.go('index.myChildren',{customerId:customerId},{reload:true});
			}
			else
			{
				$state.go('index.servicePay',{jsonStr:$stateParams.jsonStr},{reload:true});	
			}

		    
		});
	};
	
});