var App = function() {
	this.env = 'pro'; // 打包前设置
  window.onerror = function(a,b,c) {
	 // alert('Error: ' + arguments.length + '::' + a + b + c);
	};
	this.LBS_key = 'VSRBZ-AFXHU-X3GVF-4ONWH-7FUDQ-MUBIO';
	// 用来跳转
	var Config = {		
		'pro': {
			'api' : 'http://api.juzixueche.com/api/web/WebRequest',
			'appid' : 'wx1ed2433011bfd932',
			'redirect_uri' : 'http://weixin.juzixueche.com/home/oauth2/'
		}
	},
	// RegExp 正则与提示
	regpack = {
		'Mobile' : [/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/,	
		 '貌似不对哦'],
		'Password':[/^\S{6,16}$/,
		'长度不够'],
		'SmsCode':[/^\d{6}$/,'为六位数字'],
		'Email' : [/^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\.][a-z]{2,3}([\.][a-z]{2})?$/i,'格式不对哦']
	},
	// 字段翻译
	fieldPack = {
		'Mobile' : '手机号',
		'Password' : '密码',
		'CurrentPassword' : '当前密码',
		'SmsCode' : '短信验证码',
		'NewPassword' : '新密码',
		'NewPassword2': '确认密码',
		'InviteCode' : '邀请码',
		'Email' : '邮箱',
		'Nickname' : '名字'
	};
	this.__ = function(_) {
		return fieldPack[_];
	};

	/* 使用openid 登录*/
	this.loginByRice = function (str, fn) {
		var me = this,
				api = this.platform.apicode==1?'InstructorLogin':'MemberLogin';
		app.showTip('自动登录中...');
		app.req(api, {'OpenID' : str,'Mobile':'','Password':''}, function(resp) {
				if('ReturnValue' in resp) {
					app.hideTip();
					App.Storage.set('SID', resp.ReturnValue);
					if(fn) {
						fn(resp);
					} else {
						var url = location.href.replace(/(\?|&)Rice=\w+$/,'');
						location.href = url;
					}
				}
		});
	};

	/* 进入微信登录 */
	this.gotoWxLogin = function(type) {
			var _temp = _.template('https://open.weixin.qq.com/connect/oauth2/authorize?appid=<%=appid%>&redirect_uri=<%=redirect_uri%>&response_type=code&scope=snsapi_base&state=main#wechat_redirect');
			var _join = location.href.indexOf('?')>0?'&':'?';
			var redirect_uri = Config[this.env].redirect_uri + '?type=' + type +'&backurl=' + encodeURIComponent(location.href+_join+'Rice={0}');
      var url = _temp({redirect_uri:encodeURIComponent(redirect_uri),appid:Config[this.env].appid});
			location.href = url;	
	};
	
	// 字段检查
	this.checkField  = function(key, v) {
		if(v == '')
			return {res:false, msg:this.__(key) + '不能为空!'};
		if(key in regpack) {
			if(regpack[key][0].test(v)) {
				return {res:true};
			} else {
				return {res:false, msg:this.__(key) + regpack[key][1] + '!'};
			}
		} else {
			return 	{res:true};
		}
	};
	// 判断当前平台 教练还是学员端
	if(location.href.indexOf('/juzi/coach') > 0) {
		var _o =  {apicode:1,loginURL:'/juzi/coach/login'}
	}

	if(location.href.indexOf('/juzi/custom') > 0) {
		var _o =  {apicode:2,loginURL:'/juzi/custom/login'}
	}

	this.platform = _o;

	// 统一请求 抽象
	this.req = function(act, param, cb, ccb) {
		var me = this,okey = me.platform.apicode==1?'iRice':'mRice',
        reqdata = {
          method:act,
				  msg:JSON.stringify(param),
				  apicode:_o.apicode
        };
    if('MemberServiceApply'== act) {
        _.extend(reqdata, {
          'version':'v1.1'
        });
    }
    if('GetSpreadImg' == act) {
    		_.extend(reqdata, {
          'apicode':5
        });
    }
		$.ajax({
			url:Config[this.env].api,
			type:'post',
			dataType:'json',
			data:reqdata,
			complete: function() {
				ccb&&ccb();
			},
			success: function(resp) {
				if('Status' in resp) {
					if(resp.Status.StateValue !== 'M00') {
						me.showTip(resp.Status.StateDesc, 2000);
						if(resp.Status.StateValue === 'M02') {
							App.Storage.remove('SID');
							if(App.Storage.has(okey)) {
								app.showTip('会话过期，重新登录中...', 2000);
								me.loginByRice(App.Storage.get(okey));
							} else {
								me.gotoWxLogin(me.platform.apicode);
							}
						}
						if(resp.Status.StateValue == 'M09') {
							me.gotoWxLogin(me.platform.apicode);
						}
					} else {
						cb&&cb(resp);
					}
				}
			},
			error: function(err) {
				console.log('接口出错，请稍候再试！');
			}
		});
	};

	this.hideTip = function() {
		tip_div.removeClass('show-loading');
	};

	this.openLocation = function(data) {
		var coor = data.PlaceCoordinate.split(',').reverse().join(',');
		$.ajax({
			url:'http://apis.map.qq.com/ws/coord/v1/translate',
			dataType:'jsonp',
			data:{key:this.LBS_key,locations:coor,type:3,output:'jsonp'},
			success: function(loc) {
				wx.openLocation({
						latitude : loc.locations[0].lat,
						longitude: loc.locations[0].lng,
						name:data.PlaceName,
						address:data.PlaceAddress,
						scale : 20
				});
			}
		})
	};
	var me = this;
  // 接收位置信息，用户选择确认位置点后选点组件会触发该事件，回传用户的位置信息
	window.addEventListener('message', function(event) {
    var loc = event.data,
    		url = 'http://api.map.baidu.com/geoconv/v1/';
				$.ajax({
					url:url,
					dataType:'jsonp',
					data:{ak:'GYMCwAPcBOxYUd7P68KGS7gW', coords:loc.latlng.lng + ',' + loc.latlng.lat,from:3},
					success:function(resp) {
						var
						coor = resp.result[0].x.toFixed(8) + ',' +resp.result[0].y.toFixed(8),
						pdata = {
							'InstructorID' : App.getHttpParams('InstructorID'),
							'Coordinate'   : coor
						};
				    $('.pick_address').data('coord',coor);
				    me.updateShttleServie(pdata, loc.poiname);
					}
				})
    $('.pick_address .addr').html(loc.poiname)
    $('#mapPage').remove();
	}, false);	
  
  // 拾取地点
	this.pickLocation = function(fn) {
		var div = '<div id="mapPage"><a class="back" href="javascript:$(\'#mapPage\').remove();"></a><iframe frameborder="0" src="http://apis.map.qq.com/tools/locpicker?search=1&total=10&type=1&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77&referer=juzi"></iframe></div>';
		$(document.body).append(div); 
	};


  this.getScript = function(url, callback) {
     var head = document.getElementsByTagName('head')[0],
         js = document.createElement('script');

     js.setAttribute('type', 'text/javascript'); 
     js.setAttribute('src', url); 

     head.appendChild(js);

     //执行回调
     var callbackFn = function(){
             if(typeof callback === 'function'){
                 callback();
             }
         };

     if (document.all) { //IE
         js.onreadystatechange = function() {
             if (js.readyState == 'loaded' || js.readyState == 'complete') {
                 callbackFn();
             }
         }
     } else {
         js.onload = function() {
             callbackFn();
         }
     }
 };

  
  // 使用微信API 获取百度坐标
	this.getLocation = function(fn) {

        app.getScript("http://api.map.baidu.com/getscript?v=2.0&ak=lsb0bkefT7off79Y6hZ5QgLN9zNqLFKl&services=&t=20161227185926",function(){
        	var geolocation = new BMap.Geolocation();
		    geolocation.getCurrentPosition(function (r) {
		        if (this.getStatus() == BMAP_STATUS_SUCCESS) {		           
		            fn(r)
		        }
		        else {
		            alert('failed' + this.getStatus());
		        }
		    }, { enableHighAccuracy: true })
        });


              /*
				wx.getLocation({
					type: 'wgs84',
					success: function(res) {
						var url = 'http://api.map.baidu.com/geoconv/v1/'
						$.ajax({
							url:url,
							dataType:'jsonp',
							data:{ak:'GYMCwAPcBOxYUd7P68KGS7gW', coords:res.longitude + ',' + res.latitude,from:1},
							success:function(resp) {
								fn(resp)
							}
						})
					},
					cancel: function (res) {
				        app.hideTip();
				        app.showTip('温馨提示：桔子学车使用过程需要获取您的地理位置。',2000);
				    }
				});
				*/
	};

	this.infinite = false;
	this.infiniteCount = 2; // 页码
  
  this.checkLogin = function(fn) {
    var okey = me.platform.apicode==1?'iRice':'mRice';
		if(App.getHttpParams('Rice')) {
			App.Storage.set(okey, App.getHttpParams('Rice'));
		}
		if(App.Storage.has('SID')) {
			return {
				'SessionID' : App.Storage.get('SID')
			};
		} else {
			if(App.Storage.has(okey)) {
				me.loginByRice(App.Storage.get(okey), fn)
			} else {
				me.gotoWxLogin(me.platform.apicode);
			}
		}
  };
  
	// 增加可点击区域
 	$('body').on('tap', '.box', function(e) {
      e.preventDefault();
      var url = $(this).find('.more').attr('href');
      if(url && url.indexOf('/') > -1) {
          location.href = url;
      }
  });
  // 增加提示层  
	$('body').append('<div class="loading"><span class="txt"></span></div>');
  
  // 滚动监听
	$(document).on('scroll',function(e) {
 	 	var avliHeight = document.body.scrollHeight - window.innerHeight,
	 	scrollTop = $('body').scrollTop();
	 	if(avliHeight - scrollTop < 200) {
	 		me.infinite && me.infinite(me.infiniteCount++);
	 	}
	});
  
  //  展示提示
	var tip_div = $('.loading'),Timer = false;
	this.showTip= function(str,timer) {
		Timer>0&&clearTimeout(Timer);
		Timer = false;
		if(timer && timer>0) {
			Timer = setTimeout(function() {
				tip_div.removeClass('show-loading');
			}, timer);
		}
		tip_div.addClass('show-loading').find('.txt').text(str);
	};

	this.resetPass = function () {
		var form = $('.c-form'),
				me   = this;

		var CountDown = function () {
      Count--;
      if(Count <0) {
        Btn9.removeClass('disable').html('获取验证码')
        clearTimeout(TT);
        Count = 60
        return;
      }
      Btn9.html(Count + '秒后重发');
      TT = setTimeout(CountDown, 1000)
    }, Count = 60, TT, Btn9;
			form.on('tap','.btn-mini', function(e) {
				var t = $('[name="Mobile"]');
				Btn9 = $(this);

				var res = app.checkField('Mobile',t.val());
					if(res.res) {
						var d = {
							Mobile:t.val(),
							BizType : me.platform.apicode==2?0:2
						};
						app.req('SendVerifyCode',d, function(resp) {
							app.showTip('验证码发送成功！', 1500);
							Btn9.addClass('disable');CountDown()
						})
					} else {
						app.showTip(res.msg, 1500);
					}
			});
			form.on('submit', function(e) {
				e.preventDefault();
				var pdata = form.serializeArray(),
						odata = {};
				$.each(pdata, function(i,e) {
					var q = me.checkField(e.name, e.value);
					if(q.res) {
						odata[e.name] = e.value;
					} else {
						me.showTip(q.msg, 1500);
						$('[name='+e.name+']').trigger('focus');
						return false;
					}
				});
				if(_.size(odata) == 3) {
	        var mets =  me.platform.apicode==2?'MemberForgetPassword':'InstructorForgetPassword';
					app.req(mets, odata, function(resp) {
						app.showTip('密码重置成功,正在跳转...',1500);
						setTimeout(function(){
							location.href = '/juzi/' + (me.platform.apicode==2?'custom':'coach') + '/login';
						}, 1500);
					});
				}
			});
	};
};
$.extend(App, {
	'isReady' : false, // SDK 准备就绪
	'initSDK' : function(fn) {
		var me = this;
		if(App.isReady) {
			return fn&&fn();
		}
		if(wx.config) {
  		var isDebug = false,
      	jsApiList 
      = 'onMenuShareTimeline|onMenuShareAppMessage|onMenuShareQQ|onMenuShareWeibo|onMenuShareQZone|chooseImage|previewImage|uploadImage|downloadImage|closeWindow|scanQRCode|chooseWXPay|getNetworkType|openLocation|getLocation';
		  
		  var url = location.href.split('#')[0];
		  $.ajax({
		    url : '/juzi/wxgetconfig',
		    data : {url:url},
		    dataType:'json',
		    success : function(resp) {
		      var conf = _.extend({debug:isDebug, jsApiList : jsApiList.split('|')}, _.omit(resp, 'url','jsapi_ticket'));
		      wx.config(conf);
		    },
		    error : function(err) {
		    	me.showTip('SDK 初始化失败!',1500);
		    }
		  });
		  wx.error(function(err) {
		  	app.hideTip();
		  	if(err.errMsg === 'config:require subscribe') {
		  		$('#subscribebox').removeClass('hide')
		  	}
		  })
		}

		if(wx.ready) {
			wx.ready(function() {
				App.isReady = true;
				fn&&fn();
			})
		}
	},
	'size' : function(o) {
		if('length' in o) {
			return o.length;
		} else {
			return Object.keys(o).length;
		}
	},
	'format_date' : function(str) {
		var d = new Date(str);
		return d.getFullYear() + '年' + (d.getMonth()+1) + '月' + d.getDate() + '日';
	},
	'getHttpParams' : function(name) {
      var r = new RegExp("(\\?|#|&)"+name+"=([^&#]*)(&|#|$)");
      var m = location.href.match(r);
      return decodeURIComponent(!m?"":m[2]);
   },
	'Storage' : {
		'set' : function(key,val) {
			localStorage.setItem(key, val);
		},
		'has' : function(key) {
			return localStorage.getItem(key) !== null;
		},
		'get' : function(key) {
			var tmp = localStorage.getItem(key);
			if(tmp !==null && (tmp.charAt(0) === '{' || tmp.charAt(0) === '[')) {
				return JSON.parse(tmp);
			}
			return tmp;
		},
		'remove' : function (key) {
			localStorage.removeItem(key);
		}
	}
});