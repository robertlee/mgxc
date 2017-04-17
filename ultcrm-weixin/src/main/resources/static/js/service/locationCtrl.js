//home页面的控制器
ultcrm.controller('locationCtrl', function($scope,$stateParams,$http,$state,$window,$location) {
	//获取上个页面传过来门店id
	$scope.id = $stateParams.id;
	$scope.itemType = $stateParams.itemType;
	//获取门店位置信息
	$http.get("/location/getStoreLocation/"+ $scope.itemType +"/"+$scope.id).success(function(res){
		$scope.location = res;
		console.log(res);
	});
	
	$scope.goServiceStore = function(){
		console.log(1);
		$state.go('index.serviceStore',{},{reload:true});
	}
	
    var ep = $("#end_point").val().split(",");
    var map = new BMap.Map("l-map");
    var point = new BMap.Point(ep[0], ep[1]);
    map.centerAndZoom(point, 16);
 
    // 定位对象
    var geoc = new BMap.Geocoder();
    var geolocation = new BMap.Geolocation();
    //定位当前位置
    geolocation.getCurrentPosition(function(r){
    	console.log(this.getStatus());
        if(this.getStatus() == BMAP_STATUS_SUCCESS){
            //var mk = new BMap.Marker(r.point);
            //map.addOverlay(mk);
            //map.panTo(r.point);
        	
            $("#start_point").val(r.point.lng+','+r.point.lat);
            setLocation(r.point);
            showMap();
 
        }else {
            $("#start").attr("placeholder","请输入您的当前位置")
            alert('无法定位到您的当前位置，导航失败，请手动输入您的当前位置！'+this.getStatus());
        }
    },{enableHighAccuracy: true});
 
    $(".nav .nav-sub a").click(function(){
        $(".nav .nav-sub a").removeClass('cur');
        $(this).addClass('cur');
        searchRoute();
    })
 
    $("#reLocation").click(function(){
        reLocation();
    });
 
    $("#bus-search,#driver-search,#walk-search").click(function(){
        var id = $(this).attr("id");
        $(".nav .nav-sub a").removeClass('cur');
        if(id == "bus-search"){
            $(".nav .nav-sub a.bus").addClass('cur');
        }else if(id == "driver-search"){
            $(".nav .nav-sub a.driver").addClass('cur');
        }else if(id == "walk-search"){
            $(".nav .nav-sub a.walk").addClass('cur');
        }
        showMap();
    })
 
    function reLocation(){
        $("#search").show();
        $("#showMap").hide();
        map = new BMap.Map("l-map");
    }
 
    function showMap(){
        $("#srarch").hide();
        $("#showMap").show();
        searchRoute();
    }
 
    function setLocation(point){
        geoc.getLocation(point, function(rs){
            var addComp = rs.addressComponents;
            var result = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;
            $("#start").val(result);
            $("#start_location").val(result);
            searchRoute();
        });
    }
 
    function searchRoute(s_, e_){
        map = new BMap.Map("l-map");
        var cur = $(".nav .nav-sub a.cur");
        var type = "";
 
        if(cur.hasClass('bus')){
            type = "bus";
        }else if(cur.hasClass('driver')){
            type = "driver";
        }else if(cur.hasClass('walk')){
            type = "walk";
        }else{
            type = "driver";
        }
 
        var s_;
        var e_;
 
        var sl = $("#start_location").val();
        var s = $("#start").val();
        var sp = $("#start_point").val();
        var e = $("#end").val();
        var ep = $("#end_point").val();
 
        if(s != sl){// 如果用户修改了地址（与定位的位置不一致）则使用地址搜索
            s_ = s;
            e_ = e;
        }else if(sp){// 否则使用坐标搜索
            var ps = sp.split(",");
            var pe = ep.split(",");
            s_ = new BMap.Point(ps[0], ps[1]);
            e_ = new BMap.Point(pe[0], pe[1]);
        }
 
        if(type == "bus"){
            var transit = new BMap.TransitRoute(map, {renderOptions: {map: map, panel: "r-result", autoViewport: true}});
            transit.search(s_, e_);
        }else if(type == "driver"){
            var driving = new BMap.DrivingRoute(map, {renderOptions: {map: map, panel: "r-result", autoViewport: true}});
            driving.search(s_, e_);
        }else if(type == "walk"){
            var walking = new BMap.WalkingRoute(map, {renderOptions: {map: map, panel: "r-result", autoViewport: true}});
            walking.search(s_, e_);
        }
    }
});

