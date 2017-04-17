
ultcrm.controller('serviceStoreCtrl', function($scope,$http,$state,$stateParams,$window,$location,$sanitize,$ionicPopup,$ionicSlideBoxDelegate,appointmentService,appointmentStore,customerData,defaultURL) 
{	
	var iContent = 0;

	$scope.siteDist =  ["南山区训练场", "福田区训练场", "宝安区训练场", "龙华新区训练场", "龙岗区训练场", "坪山新区训练场"];
	$scope.defaultURL = defaultURL.url+":"+defaultURL.port+defaultURL.path;
	
	
	
	$scope.storeList = new Array();
	$http.get('/getStoreList').success(function(result) {    
		$scope.storeList=result;  
		console.log(result);  	
    }).
    error(function() {
    	$scope.storeList = [];//清空select
    });	

	$scope.selectSite=function(selectedSite){
		$http.get('/getStoreAllList/'+selectedSite).success(function(result) {
			console.log(result);
        	//先清空原始数据
        	$scope.storeList = new Array();        	
		    iContent = 0;	
        	if(result != null)
	        {
				for (var i = 0 ; i < result.length; i ++) {
					$scope.storeList[iContent]=result[i];
					iContent=iContent+1;								
				}
	        }
        }).
        error(function() {
        	$scope.storeList=null;	
        	
        });			
		
	};	
	//前往首页页面
	$scope.goHomePage = function(){
		$state.go('index.home',{},{reload:true});
	}
	
	$scope.selectStore = function(item){
		// 返回到服务页面
		appointmentStore.id = item.id;
		appointmentStore.name = item.name;
		$state.go('index.coachlist',{},{reload:true});
	}
	// 导航
	$scope.toLocation = function(id){
		//$state.go('index.coachlist',{},{reload:true});
		//itemType为2表示训练场跳至导航页面
		$state.go("index.location",{itemType:2,id:id},{reload:true}); 
	}
	
});

