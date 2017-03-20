ultcrm.controller('coachlistCtrl', function($scope,$http,$location,$state,$timeout,$ionicPopup,customerData) {
		//初始化数据
		var count = 1;
		var iContent1 = 0;
		var iContent2 = 0;
		$scope.div1 = true;//显示
		$scope.div2 = false;//隐藏
		$scope.siteDist =  ["南山区训练场", "福田区训练场", "宝安区训练场", "龙华新区训练场", "龙岗区训练场", "坪山新区训练场"];
		$scope.title1 = "title1 change1";
		$scope.title2 = "title2 change2";
		angular.element(document.querySelector('#imagesClass1')).addClass('active');
		angular.element(document.querySelector('#smallImgClass1')).addClass('trigger current');
		$http.get('/getCoachAllList/null').success(function(result) {
        	//先清空原始数据
        	$scope.divContent1 = new Array();
        	$scope.divContent2 = new Array();
        	if(result != null)
	        {
				for (var i = 0 ; i < result.length; i ++) {
					if(result[i].desc != "C1" ){					
						$scope.divContent2[iContent2]=result[i];
						iContent2 = iContent2+1;
					}
					else{										
						$scope.divContent1[iContent1]=result[i];
						iContent1 = iContent1+1;
					}									
				}
	        }
        }).
        error(function() {
        	$scope.divContent1 = null;
        	$scope.divContent2 = null;
        });
		
		
		$scope.selectSite=function(selectedSite){			
			$http.get('/getCoachAllList/'+selectedSite).success(function(result) {
				//先清空原始数据
				$scope.divContent1 = new Array();
				$scope.divContent2 = new Array();
				iContent1 = 0;
				iContent2 = 0;			
				if(result != null)
				{
					for (var i = 0 ; i < result.length; i ++) {
						if(result[i].desc != "C1" ){					
							$scope.divContent2[iContent2]=result[i];
							iContent2 = iContent2+1;
						}
						else{										
							$scope.divContent1[iContent1]=result[i];
							iContent1 = iContent1+1;
						}									
					}
				}
			}).
			error(function() {
				$scope.divContent1 = null;
				$scope.divContent2 = null;
			});			
		
		};		

		//导航切换函数
		$scope.switchNav=function(index){
			if(index == "navon1"){
				$scope.div1 = true;//显示
				$scope.div2 = false;//隐藏
				$scope.title1 = "title1 change1";
				$scope.title2 = "title2 change2";
			}
			else if(index == "navon2"){
				$scope.div1 = false;//隐藏
				$scope.div2 = true; //显示
				$scope.title1 = "title1 change2";
				$scope.title2 = "title2 change1";
			}
		};
		$scope.toTimeSelect=function(coachId,$event){			
			$event.stopPropagation();
			var openId = customerData.openid;
			 $http.get("/getCoachById/"+coachId).success(function(data){
				 if(data.code == 'existorder') {
	                 //如果存在相似的预约单
			  } else {
				  //如果不存在相似预约单，则创建
				  $state.go('index.timesegment',{coachid:coachId},{reload:true});
			  }	 
			 });			
		};
		//展示教练详情
		$scope.showCoachDetail=function(id){
			$state.go('index.coachDetail',{id:id},{reload:true});
		};
		//展示课程详情
		$scope.showCourseDetail=function(index){
			$state.go('index.courseDetail',{index:index},{reload:true});
		};
		$scope.toServiceStore=function(){
			$state.go('index.serviceStore',{},{reload:true});
		};			
		
		
});

