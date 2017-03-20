$.extend(App.prototype, {
	'cityList' : function(fn) {
		app.req('PlatformCity', {}, function(resp) {
				if('Rows' in resp && resp.Rows.length > 0) {
					fn(resp.Rows);
				}
		});
	},
	/* 学员用户中心 */
	'Cus_center' : function() {
		var pdata = this.checkLogin();
				me = this,
				_temp = _.template('己学车<%=obj.LearnDays%>天 / 己练车<%=obj.LearnHours%>小时');
			pdata && app.req('MemberCenterInfo', pdata, function(resp) {
					var data = resp.mapdata;
					data.LearnTips = _temp(data);
					$('[data-key]').each(function(i,e) {
						var tmp = data[e.dataset.key], t = $(e);
						if(e.nodeName.toLowerCase() === 'img') {
							tmp && t.attr('src', tmp);
						} else {
							if(e.dataset.key=='Balance') tmp += '元';
							t.html(tmp);
						}
					});
			})
	},
	/* 学员资料 */
	'Cus_profile' : function() {
		var pdata = this.checkLogin(),me = this;
		function uploadImage(localId) {
			wx.uploadImage({
			    localId: localId.toString(),
			    isShowProgressTips: 1,
			    success: function (res) {
			    		var obj = _.extend({Value:res.serverId, DataType:2}, me.checkLogin());
							app.req('MemberUpdate', obj, function(resp) {
									me.showTip('设置头像成功！', 1500);
							});
			    },
					fail: function(err) {
						alert(JSON.stringify(err));
					}
			});
		}
		pdata && app.req('MemberInfo', pdata, function(resp) {
			$('.u-info').on('tap', '.box',function(e) {
				var t = $(this),
						img = t.find('img');
					wx.chooseImage({
						count:1,
						sizeType:['compressed'],
						sourceType:['album','camera'],
						success:function(res) {
							var localIds = res.localIds; 
							img.attr('src',localIds);
							setTimeout(function() {
								uploadImage(localIds);															
							},100);
						}
					})
			});
      
      // 设置性别
      $('.sex-sel').on('change', function(e){
         var t = $(this),
            val = t.val(),
            vals = ['未知','男','女'];
           
            $('span[data-key="Sex"]').html(vals[val]);
            pdata.DataType = 0;
            pdata.Value = val;
            app.req('MemberUpdate', pdata, function(resp){
              me.showTip('性别更新成功！',1500)
            });
      });
      
      // 数据渲染
			$('[data-key]').each(function(i,e) {
				var key = e.dataset.key;
				App.Storage.set('CityID',resp.mapdata.CityID);
				if(resp.mapdata.Email == '') {
					resp.mapdata.Email = '请修改';
				}
				if(key in resp.mapdata) {
					var tmp =  resp.mapdata[key];
					if(this.nodeName.toLowerCase() == 'img') {
						tmp&&$(this).attr('src', tmp);
					} else {
						$(this).html(tmp);
					}
				}
			});
      
      $('.btn-white').on('tap', function(){
        var Rice = App.Storage.get('mRice'),
            pdata = me.checkLogin();
         
         if(confirm('确定要退出登录吗？')) {
           pdata.OpenID = Rice;
           app.req('Logout', pdata, function(){
             location.href='/juzi/custom/login'
           })
         }
      })
		});
	},
	/* 学员登录  */
	'Cus_login':function() {
		var me = this;
		var openid =  App.getHttpParams('Rice');
		if(openid) {
			var btn_reg = $('#btn_reg'),
					old_url = btn_reg.attr('href');
					btn_reg.attr('href',   old_url+'?Rice='+ openid);
		}
		var form = $('.c-form').on('submit', function(e) {
				e.preventDefault();
				var pdata={}, data = form.serializeArray();
				$.each(data, function(i,e) {
					var q = me.checkField(e.name, e.value);
					if(q.res) {
						pdata[e.name] = e.value;
					} else {
						me.showTip(q.msg, 1500);
						$('[name='+e.name+']').trigger('focus');
						return false;
					}
				});
				if(App.size(pdata) == 2) {
					pdata.OpenID = openid;
					var btn_sub = $('#btn_sub').val('登录中...');
					app.req('MemberLogin', pdata, function(resp) {
						if('ReturnValue' in resp) {
							openid && App.Storage.set('mRice', pdata.OpenID);
							App.Storage.set('SID', resp.ReturnValue);
						  location.href = App.getHttpParams('backurl') || '/juzi/custom/center';
						}
					}, function() {
						btn_sub.val('登录');
					});
				}
		});
	},
	/* 学员注册 */
	'Cus_register' : function() {
			var me = this,
			openid =  App.getHttpParams('Rice'),
		  $overlay = $('#alert_box');

		  $('#show_terms').on('tap', function(e){
          $overlay.removeClass('hide');
      });
      $overlay.on('tap', '.close', function(e){
          $overlay.addClass('hide');
      });

			// 取城市列表 
			app.cityList(function(res) {
				// res.push({CityID:100, Name:'广州市'});
				var _temp = _.template('<%_.each(obj, function(e,i,o){%> <option value="<%=e.CityID%>"><%=e.Name%></option><%})%>')
				var sel = $('[name="CityID"]').html(_temp(res));
				var cityname = $('.cityname').html(res[0].Name);
				sel.on('change', function() {
					cityname.html(res[this.selectedIndex].Name)
				});
			});

			var form = $('.c-form');
			form.on('submit', function(e){
				e.preventDefault();
				var pdata={}, data = form.serializeArray();

				$.each(data, function(i,e) {
					var q = me.checkField(e.name, e.value);
					if(q.res || e.name ==  'InviteCode') {
						pdata[e.name] = e.value;
					} else {
						me.showTip(q.msg, 1500);
						$('[name='+e.name+']').trigger('focus');
						return false;
					}
				});

				if(App.size(pdata) === 5) {
					pdata.OpenID = openid;
					app.req('MemberRegister', pdata, function(resp) {
							// 如果提供了openID 直接进入中心
							me.showTip('注册成功，正在登录...', 1500);
							openid && App.Storage.set('mRice', pdata.OpenID);
							openid && me.loginByRice(openid,function(){
								location.href='/juzi/custom/center';
							});
					})
				}
			})
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

			form.on('tap', '.btn-mini', function(e) {
				var t = $('[name="Mobile"]');
            Btn9 = $(this);
        if(Btn9.hasClass('disable')) return false;
				var res = app.checkField('Mobile',t.val());
					if(res.res) {
						var d = {
							Mobile:t.val(),
							BizType : 1
						};
						app.req('SendVerifyCode',d, function(resp) {
							app.showTip('验证码发送成功！', 1500);
              Btn9.addClass('disable');CountDown()
						})
					} else {
						app.showTip(res.msg, 1500);
					}
		});
	},

	'Cus_address' : function() {
			var id = App.getHttpParams('id'),
					pdata = {
						PlaceID : id,
						Coordinate : App.Storage.get('Coordinate'),
						PageIndex : 1,
						PageSize : 50
					};
				var _temp = _.template($('#temp-coachlist').html());
				var _temp2 =  _.template($('#temp-address').html());
				app.req('InstructorsByPlace', pdata, function(resp){
						$('.coachItemlist').html(_temp(resp.listdata.rows));
						$('.box').html(_temp2(resp.mapdata));
						$('.uc-center').html(resp.mapdata.PlaceName);
						// 显示地图
					$('.box').on('tap', function(e) {
							resp.mapdata.PlaceCoordinate = resp.mapdata.Coordinate;
							app.openLocation(resp.mapdata);
					});
				});
	},
  'Cus_mycoupon' : function() {
    var me = this,
        pdata = me.checkLogin();
    
    app.req('MemberCouponList', pdata, function(resp) {
      $('.mvt1 i').html(resp.mapdata.InviteCode);
    }) 
  },
	'Cus_coachlist' : function() {
		var pdata = {
			'Coordinate' : App.Storage.get('Coordinate'),
			'CityID'     : App.Storage.get('CityID'),
			'PageIndex'  : 1,
			'PageSize'   : 10
		},
		me = this,
		index = 0,
		Mets = ['InstructorListByRecommended','InstructorListByCarType','InstructorListByCarType'];
    
    function initD() {
            // 初始化
            $('.infinite-scroll-preloader').removeClass('hide');
            isEnd = false;
            $('.coachItemlist').html('');
            me.infiniteCount = 2;
            me.infinite(1);
     }
		
		$('.areaItem').on('tap', 'li', function(e) {
				var t = $(this);
				index= t.index();
				t.addClass('cur').siblings().removeClass('cur');
				if(index>0) {
					pdata.CarType = index;
				} else {
					delete pdata.CarType;
				}
				initD();
		});
		var isLoading = false,isEnd = false;
		this.infinite = function(i) {
			pdata.PageIndex = i;
			if(isLoading||isEnd) return;
			var isLoading = true;
			app.req(Mets[index], pdata, function(resp) {
			var _temp = _.template($('#temp-coachlist').html());
				if(resp.Rows.length>0) {
						$('.coachItemlist').append(_temp(resp.Rows));
				}
				if(resp.Rows.length<10) {
						isEnd = true
						$('.infinite-scroll-preloader').addClass('hide');
				}
			},function(){isLoading = false;});
		};
		initD();
	},
	'Cus_service' : function() {

		var pdata  = {
			CityID : App.Storage.get('CityID'),
            ServiceType : 31
		};
		var _temp = _.template($('#temp-service').html());
	    function render () {
	      app.req('PlatformServiceList', pdata, function(resp) {
	        if('listdata' in resp) {
	          $('.jzlist').html(_temp(resp.listdata));
	        }
	      },function(){$('.infinite-scroll-preloader').addClass('hide');});
	    }


	    var pdata_ts  = {
	   				CityID : App.Storage.get('CityID'),
		            ServiceType : 32
				};
		var _temp_ts = _.template($('#temp-ts-service').html());
	    function render_ts () {
	      app.req('PlatformServiceList', pdata_ts, function(resp) {
	        if('listdata' in resp) {
	          $('.jzlist').html(_temp_ts(resp.listdata));
	        }
	      },function(){$('.infinite-scroll-preloader').addClass('hide');});
	    }

	    setTimeout(function () {
	        render();
	    }, 300);

       $('.tab-bar').on('tap','a', function(){
	      var t = $(this);
	       t.addClass('current').siblings().removeClass('current');	 
	      i = t.index();
          if(i === 0)
          {          	 
             render();
          }
	     if(i === 1)
	      {         	         
		      render_ts();
	      }
	    })
	},

	'Cus_coach' : function() {
    var pdata = {
			'Coordinate' : App.Storage.get('Coordinate'),
			'CityID'     : App.Storage.get('CityID'),
			'PageIndex'  : 1,
			'PageSize'   : 10
		},isLoading = false,
    slideR = {
      'CityID'     : App.Storage.get('CityID'),
      'ServiceType' : 31
    },
    _temps = _.template($('#temp-slider').html());
    app.req('PlatformServiceList', slideR, function(resp){
        var listData = resp.listdata
        $('#banner').html(_temps(listData))
          TouchSlide({
            slideCell:"#banner",
            titCell:".hd",
            mainCell:".bd",
            effect:"left",
            autoPlay:true,
            autoPage:'<li>&nbsp;1</li>',
            interTime : 3000,
            delayTime : 800
          });
    });
		this.infinite = function(i) {
			pdata.PageIndex = i;
			if(isLoading) return;
			isLoading = true;
			app.req('InstructorListByRecommended', pdata, function(resp) {
				var _temp = _.template($('#temp-coachlist').html()),
						_temp2 = _.template('<a href="/juzi/custom/coach_list">查看全部</a> <%=obj.ReturnValue%>位教练供您选择')
					if(resp.Rows.length<10) {
						isLoading = true;
						$('.infinite-scroll-preloader').addClass('hide');
					}
					if(resp.Rows.length>0) {
							$('.coachItemlist').append(_temp(resp.Rows));
					}
					if(pdata.PageIndex <2)
						$('.manyCoach').html(_temp2(resp));
			}, function(){isLoading = false});
		};
		this.infinite(1);
	},
	'Cus_mycoach' : function(e) {
			var me = this,
					pdata = this.checkLogin(),
					overlay = $('#alert_box');
					_.extend(pdata, {
						'PageIndex' :1,
						'PageSize' : 20
					});

			var
			_met = 'MemberOrderList',
			_temp = _.template($('#temp-mycoach').html()),
		 	renderList = function(met) {
				app.req(met, pdata, function(resp) {
					$('.mycoach').html(_temp(resp.Rows))
				});
			};

			renderList(_met);

			$('.tab-bar').on('tap', 'a', function(e){
					var t= $(this);
					_met = t.data('met');
					t.addClass('current').siblings().removeClass('current');
					renderList(_met);
			});
			
			var cdata = {}; // 取消订单的数据
			overlay.on('tap', '.btn-gray,.close', function(e) {
				overlay.addClass('hide');
			});
			overlay.on('tap', '.btn-sure',function(e) {
					 _.extend(cdata, me.checkLogin());
					 app.req('MemberOrderCancel', cdata, function(resp) {
						 overlay.addClass('hide');	
						 if(resp.Status.StateValue !== 'M25') {
								renderList(_met);
						 }
					 });
			});
			$('.mycoach').on('tap',  '.cancel_btn', function(e) {
				var t = $(this);
				if(t.hasClass('disable')) return false;
				cdata['OrderID'] = this.dataset.id;
				overlay.removeClass('hide');
			}).on('tap', '.paybtn', function(e){
        var t = $(this),
           paystr = t.data('payparam');
          App.Storage.set('wxPayinfo', JSON.stringify(paystr));
          App.Storage.set('wxBack', '/juzi/custom/mycoach');
          //location.href = '/juzi/wxpay/';
           location.href = '/juzi/custom/orderbuyconfirm?learnFee='+paystr; 
      })

	},
  'Cus_process' : function() {
    var me = this,
        pdata = me.checkLogin(),
        maps = [
          'Enter',
          'Course1',
          'Course2',
          'LongTraining',
          'Course3',
          'Course4',
          'Graduation'
        ],
        tims = [
          'top',
          'not',
          'not',
          'not',
          'not',
          'not',
          'end'
        ];
    var lis = $('.prolist li');
    app.req('MemberSchedule', pdata, function(resp){
      var mapdata = resp.mapdata,
          curindex = mapdata.Current;
      var spans = $('.placeholder').html(function(i){
            return mapdata[maps[i]];
          });
        if(curindex == -1) {
          tims[0] = 'top';
        } else {
          tims[0] = 'top1';
          _.times(curindex, function(i) {
            tims[i+1]='not1';
          });
          if(curindex<5)
            tims[curindex+1] = 'cur';
          else
            tims[curindex+1] = 'end' 
          if(curindex == 6) {
            tims[6] = 'end1';
          }       
        }
      lis.attr('class',function(e){
        return tims[e];
      })
      
    });
    
    $('.prolist').on('change','input', function(e){
      var t  = $(this),
         date = t.val(),
         item = t.data('item');
         _.extend(pdata,{
           date:date,
           Item:item
         });
         app.req('MemberUpdateSchedule', pdata, function(resp) {
            me.showTip('日期设置成功！',1500);
         })
    });
  },
	'Cus_Addlist' : function() {
		var me = this,
				sublist = $('.sublist'),
		    height = sublist.height();

		  $('.subarea').on('tap', 'li', function() {
				$(this).addClass('cur').siblings().removeClass('cur');
				var index=$(this).index();
				$('#areaInbox .subareamore').eq(index).show().siblings().hide();
			})

		  var Mets= ['PlacesByNear', 'PlacesByArea', 'PlacesOrderByStarLevel'],
		  		index = 0,
		  		pdata = {
		  				'Coordinate' : App.Storage.get('Coordinate'),
		  				'CityID' :   App.Storage.get('CityID'),
		  				'PageIndex' : 1,
		  				'PageSize'  : 10
		  		};

		  var _temp2 = _.template($('#temp-street').html());

		  app.req('PlatfromAreaList', {'CityID' :   App.Storage.get('CityID')}, function(resp) {
		  		sublist.html(_temp2({ListData:resp.listdata, CityID:App.Storage.get('CityID'),CityName:'深圳市'}));
		  });

		  sublist.on('tap', '.subarea li', function() {
		  	var t = $(this),
		  			code = t.data('id'),
		  			index = t.index();
		  		t.addClass('cur').siblings().removeClass('cur');
		  		$('#areaInbox .subareamore').hide().eq(index).show();
		  });
      
      function initD() {
            // 初始化
            $('.infinite-scroll-preloader').removeClass('hide');
            isEnd = false;
            $('.Practice').html('');
            me.infiniteCount = 2;
            me.infinite(1);
      }

		  sublist.on('tap', 'li', function(e) {
		  	var t = $(this),
		  			tx = t.text(),
		  			key = t.data('key'),
		  			val = t.data('id');
		  		pdata.CityID = 0;
		  		pdata.AreaID = 0;
		  		pdata.StreetID = 0;
		  		if(key) {
		  			sublist.addClass('hide');
		  			pdata[key] = val;
		  			tx.charAt(0)=='全'&&(tx=tx.slice(1));
		  			$('#area').html(tx);
            initD();
		  		}
		  });
     

		  $('.areaItem').on('tap', 'li', function(e) {
		  	var t = $(this);
		  	index = t.index();
		  	t.addClass('cur').siblings().removeClass('cur');
		  	if(index == 1){
					sublist.toggleClass('hide');
					var hh = sublist.height();
					if(hh >100) sublist.height(hh);
		  	} else {
		  		sublist.addClass('hide');
		  		pdata.CityID = App.Storage.get('CityID');
		  		delete pdata.AreaID;
		  		delete pdata.StreetID;
          initD();
         }
		  });
      
      var _temp3 = _.template($('#temp-additem').html()),
      isEnd = false, isLoading = false;
      
      me.infinite = function(i) {
        
        pdata.PageIndex = i;
        if(isLoading||isEnd) return;
        var isLoading = true;
        
        app.req(Mets[index], pdata, function(resp) {
            if('Rows' in resp) {
                $('.Practice').append(_temp3(resp.Rows));
                if(resp.Rows.length<10) {
                    isEnd = true
                    $('.infinite-scroll-preloader').addClass('hide');
                }
            }  				
          },function(){isLoading = false;}); 
      };
      
		  me.infinite(1);
	},
	'Cus_Proupdate' : function() {
		var me = this,
				pdata = me.checkLogin();
		var form = $('.c-form').on('submit', function(e) {
				e.preventDefault();
				var dtype = $('[name="DataType"]').val();
				var val = $('[name="Value"]').val();
				var res = me.checkField(dtype==='1'?'Email':'Nickname', val);
				if(res.res) {
					pdata.DataType = dtype;
					pdata.Value = val;
					app.req('MemberUpdate', pdata, function(resp){
						me.showTip('修改成功',1500);
					});
				} else{
					me.showTip(res.msg, 1500);
				}
		});
	},
	'Cus_book' : function() {
			var me = this,
					checkList = {},// 选中的时间段
					renobj ;
			var _temp1 = _.template('<a class="bnt <%if(obj.hour==""){%>disable<%}%>" >现在预约</a><span class="spant2">&yen;</span><span class="spant1"><%=obj.money%>.</span>.00元/<%=obj.hour%>小时');
			var table2 = $('.datetable').on('tap', 'td', function() {
				var t = $(this),
						price = t.data('price'),
						time  = t.data('time'), 
						c = t.attr('class');
					if(c!=='gray') {
						if(t.hasClass('cur')) { // 已经选中，去掉选中态
							var keys = _.keys(checkList),len = _.size(checkList);
									_i  = _.indexOf(keys, time+'');
								if(!_i||_i==len-1) {
									t.removeClass('cur');
									delete checkList[time];
								} else {
									me.showTip('只能选择连续的时段哦！',1000);
								}
						} else { // 没有选中，加上选中态
								if(!_.size(checkList) || (time-1) in checkList || (time+1) in checkList) {
									t.addClass('cur');
									checkList[time] = price;
								} else {
									me.showTip('只能选择连续的时段哦！',1000);
								}
						}
					}
					var money = 0;
					if(!_.size(checkList)) {
						renobj = {'money':120, 'hour':''};
					} else {
						_.each(checkList,function(e,i){
							money += e;
						});
						renobj = {'money':money, 'hour':_.size(checkList)}
					}
					$('.jzbox').html(_temp1(renobj));
					
			});

			$('.jzbox').on('tap','.bnt', function(e) {
					e.preventDefault();
					var t = $(this);
					if(t.hasClass('disable')) {
            setTimeout(function(){
              app.showTip('请先选择练车时间!', 1500);
            },500);
            return true;
          }
					var times = _.keys(checkList).sort(function(a,b){return a-b}),
							_tp2 = _.template('/juzi/custom/confirm_book?InstructorID=<%=obj[0]%>&startTime=<%=obj[1]%>&endTime=<%=obj[2]%>&date=<%=obj[3]%>');
                                         if(App.getHttpParams('InstructorID') == 'd9b1ba8e-0755-4833-b40e-da2717886fee') { 

            // alert('ok');
 }
					if(me.checkLogin()) {
						location.href = _tp2([App.getHttpParams('InstructorID'), _.first(times), _.last(times), curDate]);
					}
			});


			var pdata = {
				'InstructorID' : App.getHttpParams('InstructorID'),
				'Coordinate'   : App.Storage.get('Coordinate')
			};
			var render = function(str,data) {
					var cont = $(str),
							tmp = cont.data('temp'),
							_temp = _.template($('#'+tmp).html());
						cont.html(_temp(data));
			};

			var uls2 = $('.percontent ul').on('tap', 'li', function(e,i){
					var t = $(this);
					curDate = t.data('date');
					t.addClass('cur').siblings().removeClass('cur');
					table2.find('td').attr('class','');
					if(hasForbid) {
						 _.each(forbid[curDate], function(e,i){
						 		table2.find('[data-time="'+e.Time+'"]').addClass('gray');
						 });
					}
					renobj = {'money':120, 'hour':''};
					checkList = {};
					$('.jzbox').html(_temp1(renobj));
			});
			var curDate = '';
			var hasForbid = false,forbid=null;
			app.req('InstructorInfo', pdata, function(resp) {
					render('.stbanner', resp.mapdata);
					render('.stlist', resp.mapdata);
					render('#PlaceCont', resp.mapdata);
					var _temp = _.template('<%=obj.Name%>(<%=obj.Car%>)');
					$('.uc-center').html(_temp(resp.mapdata));					
					var _temp2 = _.template($('#temp-prevList').html());
          
          resp.mapdata.InstructorID = pdata.InstructorID;

					$('.perlistLast').html(_temp2(resp.mapdata));
					// 显示地图
					$('.peraddress').on('tap', '.place_pick', function(e) {
							app.openLocation(resp.mapdata)
					});
					// 可用日期
					// console.log('DateList: ', resp.mapdata.DateList);
					var _temp3 = _.template('<%_.each(obj, function(e,i,o){%><li <%if(i==0){%>class="cur"<%}%> data-date="<%=e.date%>"><%=e.Title%>(<%=e.date.slice(5)%>)</li><%})%>');
					$('.percontent ul').html(_temp3(resp.mapdata.DateList))
					curDate = resp.mapdata.DateList[0].date;
					// 可用时间点
					var tList = [];
					_.each(resp.mapdata.LearnList, function(e,i,o) {
						var price = e.Price,
								range = _.range(e.StartTime, e.EndTime+1),
								k = [];
						_.each(range, function(_e,_i) {
							if(_i%4==0) {
								k = [];
							} 
							k.push({time:_e, price:price});
							if(k.length == 4) {
								tList.push(k);
							}
						})
					});
					var _temp4 = _.template($('#temp-timelist').html());
					$('.datetable').html(_temp4(tList));
					
					// 已经被约的时间点
					if(resp.mapdata.ForbidList !== null) {
							hasForbid = true;
							forbid = _.groupBy(resp.mapdata.ForbidList, function(e,i) {
								return e.Date;
							});
					}

					uls2.find('li').eq(0).trigger('tap');
					
			});
	},
  'Cus_commlist': function() {
     var pdata = {
       'InstructorID' : App.getHttpParams('InstructorID'),
       'PageIndex' : 1,
       'PageSize' : 20
     },
     _tempList = _.template($('#temp-commList').html());
     app.req('InstructoEvaluationList', pdata, function(resp) {
        $('.perlistItem').html(_tempList(resp.mapdata));
        $('.uc-center').html('对/' + resp.mapdata.InstructorName + '/的评价')
     });
  },
	/*
	 * 确认订单
	 */
'Cus_confirm_book' : function() {
	var pdata = {
		'InstructorID' : App.getHttpParams('InstructorID'),
		'Coordinate'   : App.Storage.get('Coordinate')
	},
	me = this,
	preData = {
		'StartTime' : +App.getHttpParams('startTime'),
		'EndTime'   : +App.getHttpParams('endTime'),
		'LearnDate'   : App.getHttpParams('date')
	};
	var sess = this.checkLogin();
	var _temp1 = _.template($('#temp-myorder').html()),
		 _temp2 = _.template($('#temp-perList').html()),
		 _temp3 = _.template($('#temp-shuttle').html());

	// 获取接送设置
	me.updateShttleServie = function (d, addr) {
	if(!addr) {
		var tp_coor = d.Coordinate.split(',').reverse().join(',');
    $.ajax({ // 设置地理位置名称 
        url:'http://apis.map.qq.com/ws/geocoder/v1',
        dataType:'jsonp',
        data:{key:me.LBS_key, coord_type:3,location:tp_coor,output:'jsonp'},
        success:function(res) {
          if(!res.status) {
            $('.addr').html(res.result.formatted_addresses.recommend);
          }
		}
	}); 
	} else {
        pdata.Coordinate = d.Coordinate;
		subData.ShuttleAddress = addr;
	}
    // 匹配当前教练的接送设置
	app.req('InstructorShuttle', d, function(res) {

	   if('ShuttleFee' in res.mapdata) {
    	  var sFee = res.mapdata.ShuttleFee;
    	  $('.price').html(sFee+'元');

    	  $('.shuttle_cont').html(_temp3(res.mapdata));

    	  $('.tprice,.spant1').html(LearnFee + sFee);
    	  
          subData.ShuttleCoordinate = d.Coordinate;
          subData.ShuttleDistance = res.mapdata.Distance;
          subData.ShuttleServiceID = res.mapdata.ShuttleServiceID;
          subData.ShuttleFee = sFee;

      var blance_pay = $('.balance_pay input'),
          weixin_pay = $('.weixin_pay input');
       
        // 无法接送，太远 ，或不支持
  		if(!subData.ShuttleServiceID) {
  			subData.IsRequireShuttle = false;
            updatePayCheck()
  		} else {
            subData.IsRequireShuttle = true;
            if(tm.bData.Balance>0) {
              var 
              ovl = +blance_pay.val(), // 现在要支付的
              imb = blance_pay.data('remind') - ovl;// 可支付的
              if(ovl ==0) {
                  blance_pay.val(imb).prop('checked',true);
              }
              if(imb >= sFee) {
                blance_pay.val(ovl+ sFee).prop('checked',true);
              } else {
                var ovl1 = + weixin_pay.val();
                weixin_pay.val(sFee - imb + ovl1).prop('checked',true);
              }
            } else {
              var ovl1 = + weixin_pay.val();
              weixin_pay.val(sFee + ovl1).prop('checked',true);
            }
      }
      
	}
		});
	}
	// 学车费用 
	var 
    LearnHour = preData.EndTime - preData.StartTime + 1,
    LearnFee  = LearnHour*120,
	form      = $('.perlistLast'),
    subData   = {},
    tm;
      // 更新 支付 Checkbox 的状态
      function updatePayCheck() {
        var period_pay = $('.period_pay input'),
            balance_pay = $('.balance_pay input'),
            weixin_pay = $('.weixin_pay input').prop('checked',false).val(0);
          if(tm.bData.PeriodCount<LearnHour) {
            var remind2 = (LearnHour - tm.bData.PeriodCount)*120;            
            period_pay.val(tm.bData.PeriodCount);
            if(tm.bData.Balance>0) {
              var input1 = balance_pay.prop('checked', true);
              if(tm.bData.Balance < remind2) {
                var remind3 = remind2 - tm.bData.Balance;
                input1.val(tm.bData.Balance)
                weixin_pay.prop('checked', true).val(remind3)
              } else {
                input1.val(remind2);
              }
            } else {
              weixin_pay.prop('checked', true).val(remind2)
            }
          } else {
            balance_pay.val(0).prop('checked',false)
            period_pay.val(LearnHour);
          }
      }
      // 请求教练信息
			app.req('InstructorInfo', pdata, function(resp) {
				// 学员余额
				app.req('MemberAccount', sess, function(resp2) {
					tm = resp.mapdata;
					tm.bData = resp2.mapdata; // 账号信息
					tm.preData = preData;

					$('.myorder').html(_temp1(tm));
					$('.perlistLast').html(_temp2(tm));
                    $('.payments').on('touchstart', 'li',function(e){
                        e.preventDefault();
                    })

					subData.IsRequireShuttle = tm.IsShuttleService;
					subData.ShuttleCoordinate = pdata.Coordinate;
					subData.LearnFee = LearnFee;
					if(tm.IsShuttleService) {
							me.updateShttleServie(pdata);
					}
          
                    // 更新支付组合,没有学时或学时不够,或者余额不够支付，未付学时
                    updatePayCheck();
          
					// 点击是否需要接送
					$('.shuttle_cont').on('change', 'input', function() {
						subData.IsRequireShuttle = this.checked;
						var t = $(this),
						fee = t.data('fee'),
						id = t.data('id'),
						distance = t.data('distance');
						if(this.checked) {
						    subData.ShuttleDistance = distance;
				  		    subData.ShuttleServiceID = id;
				  		    subData.ShuttleFee = fee;
                            me.updateShttleServie(pdata);
						} else {
							subData.ShuttleDistance = 0;
				  		    subData.ShuttleServiceID = 0;
				  		    subData.ShuttleFee = 0;
                            updatePayCheck();
						}
						$('.price').last().html(subData.ShuttleFee + '元')
						$('.tprice,.spant1').html(LearnFee + subData.ShuttleFee);
            
					})

					// 查看训练场位置
					$('.tlist').on('tap', function(e) {
							app.openLocation(tm);
					});
					
					// 拾取接送地址
					$('.ordlist').on('tap', '.pick_address', function() {
							app.pickLocation();
					});

					// 设置应付总额
					$('.spant1').html(LearnFee);

					// 设置练车类型
					$('.ordlist').on('tap', '.topic', function(e) {
						 var t = $(this),
						 id = t.data('id');
						 t.addClass('cur').siblings().removeClass('cur');
						 t.siblings('input').val(id);
					});

					// 输入交易密码
					var lay = $('.overlay').on('tap', '.close', function(e) {
							lay.addClass('hide');
					});

					lay.on('keyup', '#passinput', function(e) {
							var t = $(this),
									v = t.val();
							var q = v.replace(/\d/g,'●').split('');
							_.times(6-q.length, function(e){
									q = q.concat('')
							});
							$('#moli_dot').html('<span>' + q.join('</span><span>') + '</span>');
					});
          
					// 调用支付
					lay.on('tap', '.zfItembtn', function(e) {
						var input = lay.find('#passinput'),
								tradePass = input.val();
							if(/^\d{6}$/.test(tradePass)) {
								subData.TradePassword = tradePass;
								app.req('MemberOrder', subData, function(resp) {
                  if(resp.ReturnValue){
                    App.Storage.set('wxPayinfo', resp.ReturnValue);
                    App.Storage.set('wxBack', '/juzi/custom/mycoach');
                    location.href = '/juzi/wxpay/';
                  } else {
                    location.href = '/juzi/custom/mycoach';
                  }
								});
							} else {
								me.showTip('密码为六位数字', 1500);
							}
					});
					// 确定按钮
				  var bnt = $('.bnt').on('tap', function(e) {
				  	e.preventDefault();
				  	var t = $(this);
				  	if(t.hasClass('disable')) return false;
				  	var data2 = form.serializeArray();
				  	_.extend(subData, preData, pdata, me.checkLogin());
				  	
                    delete subData.Coordinate;
            
				  	var ptym = subData.PaymentOptions = [];
            
            // 支付方式映射
				  	var paymet = {
				  		'balance': 0,
				  		'weixin' : 1,
				  		'period' : 2
				  	};
				  	_.each(data2, function(e,i) {
							var am = 0;
              if(e.name in paymet) {
                  ptym.push({
                    Option:paymet[e.name],
                    Amount:+e.value
                  });
              } else {
                subData[e.name] = e.value;
              }
				  	});	
             // ptym.length == 1 && ptym[0].Option==1
				  	if( true ) {
				  		subData.TradePassword = '';
				  		me.showTip('正在创建订单，请稍候...');
				  		bnt.addClass('disable');
				  		app.req('MemberOrder', subData, function(resp) {
				  				me.hideTip();
				  				// 跳转到统一支付
				  				if(resp.ReturnValue) {
				  					App.Storage.set('wxPayinfo', resp.ReturnValue);
                    App.Storage.set('wxBack', '/juzi/custom/mycoach');
				  					// location.href = '/juzi/wxpay/'; 
				  					location.href = '/juzi/custom/orderbuyconfirm?learnFee='+resp.ReturnValue; 
 				  				} else {
                    location.href = '/juzi/custom/mycoach';
                  }
							}, function() {
								bnt.removeClass('disable');
							});
				  	} else {
					  	lay.removeClass('hide');
					  	setTimeout(function() {
					  		lay.find('#passinput').trigger('focus');
					  	}, 500);
				  	}
				  });
				});
			})
	},
	/* 修改密码 */
	'Cus_setPass' : function() {
		var pdata = this.checkLogin(),me = this;
			var form = $('.c-form').on('submit', function(e){
				e.preventDefault();
				var arr = form.serializeArray();
				$.each(arr, function(i,e) {
				var q = me.checkField(e.name, e.value);
					if(q.res) {
						pdata[e.name] = e.value;
					} else {
						me.showTip(q.msg, 1500);
						$('[name='+e.name+']').trigger('focus');
						return false;
					}
				});
				if(App.size(pdata) == 5) {
					if(pdata.NewPassword2 === pdata.NewPassword) {
						delete pdata.NewPassword2;
						app.req('MembeUpdaterPassword', pdata, function(resp) {
							me.showTip('密码修改成功!', 1500);
							form.trigger('reset');
						});
					} else {
						me.showTip('密码两次输入不一致!', 1500);
					}
				} 
			});
	},
  'Cus_initAccount' : function() {
      var me = this,
          pdata = me.checkLogin();
      _.extend(pdata, {
        'FlowType' : 1,
        'PageIndex' : 1,
        'PageSize' : 20
      });
      var _temp = _.template($('#temp-account_list').html()),
          _cont = $('.tab-cont');
          
      function renderData(type) {
        pdata.FlowType = type;
        app.req('MemberFlowList', pdata, function(resp) {
          var rdata = _.groupBy(resp.Rows, function(a){
            return a.CreateOn.slice(0,10);
          });
          _cont.html(_temp(rdata));
        })
      }
      
      renderData(0);
      
      $('.u-info').on('tap', 'a', function(e){
        var t = $(this),i =t.index();
        t.addClass('current').siblings().removeClass('current');
        renderData(i);
      })
      
      
  },
	'Cus_myservice' : function() {
			var me = this,
					pdata = me.checkLogin();
      
			var temp1 = _.template($('#temp-serviceList').html());


		 	 function render () {

		 	 	 pdata.ServiceType = 31; // 筛选考证服务
				app.req('MemberServiceList',pdata, function(resp){
						if('listdata' in resp) {
							$('.jzlist').html(temp1(resp.listdata));
						}
				});
			  }

			 function render_ts () {

		 	    pdata.ServiceType = 32; // 筛选考证服务
				app.req('MemberServiceList',pdata, function(resp){
						if('listdata' in resp) {
							$('.jzlist').html(temp1(resp.listdata));
						}
				});
			  }

			  render();

		      $('.tab-bar').on('tap', 'a', function(){
		          var t = $(this);
		          t.addClass('current').siblings().removeClass('current');
		           i = t.index();
		          if (i === 0) {		            
		            render();
		          }  
		          if (i === 1) {		            
		            render_ts();
		          }
		      });



	},
	'Cus_account' : function(){
			var me = this,
					pdata = me.checkLogin();
			var temp1 = _.template($('#temp-account3').html()),
			temp2 = _.template($('#temp-chargeList').html());
			app.req('MemberAccount', pdata,function(resp) {
				if('mapdata' in resp) {
					$('.u-info').html(temp1(resp.mapdata));
				}
			});
			app.req('PlatfromRechargeList', pdata, function(resp){
				if('listdata' in resp) {
					$('.charge').html(temp2(resp.listdata));
				}
			});
      
      $('.charge').on('tap','.btn-mini', function(e) {
         var t = $(this),
             id = t.data('id');
          _.extend(pdata, {RechargeID: id});
          me.showTip('正在创建订单...');
          app.req('MemberCreateRecharge', pdata, function(resp){
              App.Storage.set('wxPayinfo', resp.ReturnValue);
              App.Storage.set('wxBack', '/juzi/custom/account');
              location.href = '/juzi/wxpay/';
          });
      });
	},
	'Cus_remark' : function() {
		var pdata = {
			'OrderID': App.getHttpParams('OrderID')
		},me = this;
		var SenseIDs = [], InstructorID = '';
		var temp1 = _.template($('#temp-headerinfo').html()),
				temp2 = _.template($('#temp-listData').html());
		// 评价教练页面信息
		app.req('PrepareEvaluateOrder', pdata, function(resp) {
			$('.eval').html(temp1(resp.mapdata));
			$('.evalinfo').html(temp2(resp.listdata));
      InstructorID = resp.mapdata.InstructorID;
		});
		
	  var evalinfo = $('.evalinfo').on('tap', 'span', function() {
				var t = $(this);
				if(!t.hasClass('cur') && SenseIDs.length>=3) {
				 		return false;
				}
				t.toggleClass('cur');
				SenseIDs = evalinfo.find('.cur').toArray().map(function(e){return e.dataset.id});
				pdata.SenseID = SenseIDs.join(',');
		});
		var Messages = [
			'1星,非常差',
			'2星,不太好',
			'3星,感觉还凑合',
			'4星,很满意',
			'5星,超级棒!'
		];
		
		var Listspan = $('.evaluate-star .star'),
				text = $('.evaluate-star .text');
		$('.evaluate-star').on('tap', '.star', function(e) {
			var t = $(this),
					m = i = t.index();
			t.toggleClass('red-star');
			if(t.hasClass('red-star')){
				Listspan.addClass('red-star').slice(i+1).removeClass('red-star')	
			} else {
				m--;
				Listspan.removeClass('red-star').slice(0,i).addClass('red-star')
			}
			text.html(Messages[m]);
			$('#score').val(m+1);
		});
		
		var form = $('.c-form').on('submit', function(e) {
			e.preventDefault();
			var fodata = {};
			$.each(form.serializeArray(), function(i,e) {
				if(e.value !== '') {
					fodata[e.name] = e.value
				} else {
					return false;
				}
			});
			if(_.size(fodata) == 2) {
				var subData = _.extend(pdata, me.checkLogin(),fodata);
				app.req('MemberEvaluateOrder', subData, function(resp) {
						me.showTip('评价成功！', 1500);
            setTimeout(function() {
              location.href ='/juzi/custom/comm_list?InstructorID=' + InstructorID;
            }, 1500);
				});
			}
		});
	},
  'Cus_buyzxservice' : function() {
     var me  = this,
        serData = {ServiceID : App.getHttpParams('ServiceID') };

      var DepositAmount = 0,stype; // 定金
      app.req('PlatformServiceInfo', serData, function(resp) {
          var options = resp.mapdata.OptionList;
          stype = resp.mapdata.info.Type;
          DepositAmount = resp.mapdata.info.DepositAmount;
      });

      var 
      form = $('.c-form'),
      firstPrice = 200,
      _temp = _.template('基础服务 200元 + 自选服务 <%=obj%>元');
      form[0].reset();
      var checkLength = 0;
      
      form.on('click','input', function() {
        var data = form.serializeArray(),
            price = 0;
        $.each(data, function(i,e) {
          checkLength++;
          price += +e.value;
        });

        firstPrice = 200 + price;
        $('.price strong').html(firstPrice);
        $('.t1').html(_temp(price))
      });

      $('.btn').on('tap', function(e) {
        var tmp = [{OptionID:23}];
        $('.table input').each(function(e,i) {
            if(this.checked) {
              var id = +this.id.slice(1);
              tmp.push({OptionID:id})
            }
        });
        var pdata = me.checkLogin();
        _.extend(pdata, serData);
        pdata.TradePassword = '';
        pdata.ServiceOptions = tmp;
        pdata.PaymentOptions = [];

        app.showTip('正在创建订单，请稍候')
        
        // 订制套餐提交
        app.req('MemberServiceApply', pdata, function(resp) {
            var mapdata = resp.mapdata;
            setTimeout(function(){
              location.href = '/juzi/custom/buyconfirm?stype=' + stype
               + '&memSid=' + mapdata.MemberServiceID +'&Status=7';
            }, 1000)
        });
      });

  },
  'Cus_buyszservice' : function() {
      var me  = this,
          serData = {ServiceID : App.getHttpParams('ServiceID')};

      var form = $('.c-form'),
          _temp = _.template('基础服务900元+自选服务<%=obj[0]%>元-学费减免<%=obj[1]%>元'),
          prices = [3400,0],
          // form2 = $('.d-form'),
          firstPrice = 4300; // 总价
      form[0].reset();
      // form2[0].reset();
      var checkLength = 0; // 自选方案提交校验 4
      // 在表单1点击了 就到自选方案
      form.on('change','input', function() {
        var data = form.serializeArray(),
            price = 0;
        checkLength = 0
        $.each(data, function(i,e) {
          checkLength++;
          price += +e.value;
        });
        prices[0] = price;
        $('.menu .t1').html(_temp(prices));
        firstPrice = 900 + prices[0] - prices[1];
        $('.price strong').html(firstPrice);
      });

      var PrevId ;
      // 在表单2点击了，修改减免
      /*
      form2.on('click', 'input', function() {
        var t = $(this),
            chid = t.attr('id'),
            val = +t.val();

          if(chid === PrevId) {
            t.prop('checked', false)
          }
          PrevId = form2.find('input:checked').attr('id');
          if(!t.prop('checked')) {
              val = 0
          } 
          prices[1] = Math.abs(val);

          $('.menu .t1').html(_temp(prices));
          $('.price strong').html(firstPrice + val);
      });
      */

      var DepositAmount = 0,stype = 0;
      app.req('PlatformServiceInfo', serData, function(resp) {
          var options = resp.mapdata.OptionList;
         DepositAmount = resp.mapdata.info.DepositAmount;
         stype = resp.mapdata.info.Type;
      });

      $('.btn').on('tap', function(e) {
        var tmp = [{OptionID:1},{OptionID:2}],
            pdata = me.checkLogin();
        $('.table input').each(function(e,i) {
            if(this.checked) {
              var id = +this.id.slice(1);
              tmp.push({OptionID:id})
            }
        });
        _.extend(pdata, serData);
        pdata.TradePassword = '';
        pdata.ServiceOptions = tmp;
        pdata.PaymentOptions = [];
        // 订制套餐提交
        app.showTip('正在创建订单')
        app.req('MemberServiceApply', pdata, function(resp){
            var mapdata = resp.mapdata;
            setTimeout(function() {
              location.href = '/juzi/custom/buyconfirm?stype=' + stype
              + '&memSid=' + mapdata.MemberServiceID +'&Status=7';
            }, 1000);
        });
      });
  }, 
  // 购买标准套餐
  'Cus_tcinit' : function() {
   var me  = this;
      var serdata = {ServiceID : App.getHttpParams('ServiceID')};

      // 标准套餐映射
      var service2IDS = {
        '34' : [1,2,3,6,11,13], // 深圳学车服务  
        '33' : [23,25,31,33],    // 深圳自学直考
        '30': [37, 38, 39],    // 深圳计时直考
        '29': [44],             //购买深圳学时
        '28': [47,48,49,50,51,52,53,54,55,56,57,58,59,60]             //购买深圳无忧至尊学车
      },
      priceMaps = {
        '28': 12800,
        '29': 120,
        '30' : 2980,
        '33' : 3480,
        '34': 6480
      };

      $('.tc-img a').attr('href', function() {
        return this.href + '?ServiceID=' + serdata.ServiceID; 
      });
      var ids=[],stype; // 定金,套餐组合

      $('.tc-ltype').on('tap','a', function(e) {
          $(this).addClass('active').siblings().removeClass('active');
          var 
          id = $(this).data('id');
          ids = _.clone(service2IDS[stype]);
          var
          initPrice = priceMaps[stype];
          if(id>0) ids.push(id);
          $('.gr2 strong').html(initPrice + (!id?0:500));
      });

      var _temp = _.template('<div class="b1">10人成团 / 2016年第<%=obj.PeriodDesc%>期</div><div class="b2">已购买<%=obj.ApplyCount%>人 / 团购<%if(obj.ApplyCount<10){%>还未<%}else{%>已<%}%>成功</div>');

      app.req('PlatformServiceInfo', serdata, function(resp) {
          var options = resp.mapdata.OptionList,
              info   = resp.mapdata.info;

          $('.appcount').html(0 + info.ApplyCount);
          $('.topline').html(_temp(info));
          stype = info.Type;
          ids = service2IDS[stype];
      });
      var tmp = [];
      // 下单吧
      $('.bnt').on('tap', function(e) {
        var pdata = me.checkLogin();
          tmp.length = 0;
         _.each(ids, function(e,i) {
            tmp.push({OptionID:e});
         });
        _.extend(pdata, serdata);
        pdata.TradePassword = '';
        pdata.ServiceOptions = tmp;
        pdata.PaymentOptions = []; // 仅提交不支付
        // 订制套餐提交
        app.showTip('正在创建订单,请稍候')
        app.req('MemberServiceApply', pdata, function(resp) {
          var mapdata = resp.mapdata;
          setTimeout(function() {
               location.href = '/juzi/custom/buyconfirm?stype=' + stype
              + '&memSid=' + mapdata.MemberServiceID +'&Status=0';                   
          }, 1000);
        });
      });
  },

// 购买标准套餐
  'Cus_tsinit' : function() {
   var me  = this;
      var serdata = {ServiceID : App.getHttpParams('ServiceID')};

      // 标准套餐映射
      var service2IDS = {
        '34' : [1,2,3,6,11,13], // 深圳学车服务  
        '33' : [23,25,31,33],    // 深圳自学直考
        '30': [37, 38, 39],    // 深圳计时直考
        '29': [44],             //购买深圳学时
        '28': [47,48,49,50,51,52,53,54,55,56,57,58,59,60]             //购买深圳无忧至尊学车
      },
      priceMaps = {
        '28': 12800,
        '29': 120,
        '30' : 2980,
        '33' : 3480,
        '34': 6480
      };    
      var ids=[],stype; // 定金,套餐组合
      app.req('PlatformServiceInfo', serdata, function(resp) {
          var options = resp.mapdata.OptionList,
              info   = resp.mapdata.info;

          $('.appcount').html(0 + info.ApplyCount);

          stype = info.Type;
          ids = service2IDS[stype];
      });
      var tmp = [];
      // 下单吧
      $('.bnt').on('tap', function(e) {
        var pdata = me.checkLogin();
          tmp.length = 0;
         _.each(ids, function(e,i) {
            tmp.push({OptionID:e});
         });
        _.extend(pdata, serdata);
        pdata.TradePassword = '';
        pdata.ServiceOptions = tmp;
        pdata.PaymentOptions = []; // 仅提交不支付
        // 订制套餐提交
        app.showTip('正在创建申请,请稍候')
        app.req('MemberServiceApply', pdata, function(resp) {
          var mapdata = resp.mapdata;
          setTimeout(function() {
               location.href = '/juzi/custom/ts_buyconfirm?stype=' + stype
              + '&memSid=' + mapdata.MemberServiceID +'&Status=0';
          }, 1000);
        });
      });
  },

  'CUS_orderbuyconfirm' : function()
  {
     var learnFee = App.getHttpParams('learnFee');

  },

  // 确认支付页面
  'Cus_buyconfirm' : function() {
      var me = this,
          pdata = me.checkLogin();
      var sid = App.getHttpParams('memSid'),
          stype = App.getHttpParams('stype');

      pdata.MemberServiceID = sid; // 取订单细节
      var fdata,needPay = 0,mapData,
       _temp = _.template($('#temp-buyconfirm').html()),
       _temp2 = _.template($('#temp-buyconfirm2').html());
      app.req('GetMemberServiceInfo', pdata,function(resp) {
        mapData = resp.mapdata;
        fdata = resp.mapdata.OptionList;
        var S = resp.mapdata.Status;
        if(stype==='35') {
          fdata = [
            {OptionName:'1人1车，优选教练1对1教学',TypeName:'练车服务',Price:'',Type:1},
            {OptionName:'深圳练车服务(不限时)',TypeName:'练车服务',Price:'',Type:1},
            {OptionName:'外地考场练车费(3人一车)',TypeName:'练车服务',Price:'',Type:1},
            {OptionName:'深圳至外地来返的交通费(首选火车卧铺)',TypeName:'食宿服务',Price:'',Type:2},
            {OptionName:'在外地所有的住宿费',TypeName:'食宿服务',Price:'',Type:2},
            {OptionName:'在外地所有的餐饮费',TypeName:'食宿服务',Price:'',Type:2},
            {OptionName:'保险费用',TypeName:'其它',Price:'',Type:3},
            {OptionName:'考试费用',TypeName:'基础费用',Price:'',Type:4}
          ];
        }

        if(S ===7) {
          needPay = resp.mapdata.DepositAmount;
        } else if(S === 8) {
          needPay = resp.mapdata.Price - resp.mapdata.DepositAmount;
        }
        // 服务名
        $('.sername').html(mapData.ServiceName);
        var Aprice = [];
          _.each(fdata, function(e,i) {
            if(e.Price>0)
              Aprice.push('+'+e.Price);
            else if(e.Price<0)
              Aprice.push(e.Price)
          });
          // 服务细节 
          $('.confirm-cont').html(_temp({
            fdata:fdata,
            Aprice:Aprice,
            DepositAmount : mapData.DepositAmount,
            Rprice: mapData.Price,
            StageInfo:mapData.StageInfo
          }));


          if(S===0 ||S===7 || S===8) {
              // 支付情况
              $('.menu').html(_temp2({
                DepositAmount : mapData.DepositAmount,
                Rprice: mapData.Price,
                needPay : needPay,
                Status: mapData.Status
              }));

              $('body').scrollTop(400)

              $('.menu').on('tap', '.btn', function(e){
                var t = $(this),
                    amount = t.data('needpay');
                pdata.TradePassword = '';
                pdata.PaymentOptions = [{Option:1,Amount:amount}];
                app.req('MemberServicePay', pdata, function(resp) {
                    App.Storage.set('wxPayinfo', resp.ReturnValue);
                    App.Storage.set('wxBack', '/juzi/custom/myservice');
                    location.href = '/juzi/wxpay/';
                });
              });
          } else {
            $('.menu').remove();
          }
      });

  }
});