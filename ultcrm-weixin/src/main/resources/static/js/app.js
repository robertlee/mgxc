// Ionic Starter App
// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('ultcrm', ['ionic','ultcrm.controllers', 'ultcrm.services','ultcrm.values','ultcrm.constants','ultcrm.filters','ngSanitize'])
.run(function($ionicPlatform,$location,$state) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)	  
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);
    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleLightContent();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider,$ionicConfigProvider) {
	$ionicConfigProvider.tabs.position("bottom");
	//$ionicConfigProvider.views.transition("none");
                                                                                
  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider
  //setup an abstract state for the tabs directive	  
	    .state('index', {
	        url: '/index',
	        abstract: true,
	        templateUrl: 'tpl/common/foottabs.html'
	      })
	     .state('weixin', {
	        url: '/weixin',
	        templateUrl: 'tpl/common/weixin.html',
	        controller: 'weixinCtrl'
          })
	      // Each tab has its own nav history stack:
	      .state('index.home', {
	        url: '/home',
	        cache:false, 
	        views: {
	          'index-home': {
	            templateUrl: 'tpl/home/home.html',
	            controller: 'homeCtrl'
	          }
	        },
	        resolve: {
	            customerData: function($location,customerDataService) {
	            	console.log("into resolve customer index data.....");
	            	var searchObject = $location.search();
	        		var code = searchObject['code'];
	        		var uid = searchObject['uid'];
	            	return customerDataService.find(uid,code);
	            }
	          }
	      })

	      //服务首页
	      .state('index.serviceList', {
	        url: '/serviceList',
	        cache:false, 
	        params:{'techlevelno':null,'courseName':null},
	        views: {
	          'index-serviceList': {
	            templateUrl: 'tpl/service/serviceList.html',
	            controller: 'serviceListCtrl'
	          }
	        },
	        resolve: {
	            customerData: function($location,customerDataService) {
	            	console.log("into resolve customer data.....");
	            	var searchObject = $location.search();
	        		var code = searchObject['code'];
	        		var uid = searchObject['uid'];
	            	return customerDataService.find(uid,code);
	            }
	          }
	      })

	      //课程详情展示
	      .state('index.courseDetail', {
	        url: '/courseDetail/:index',
	        cache:false,
	        params:{'index':null},
	        views: {
	          'index-service': {
	            templateUrl: 'tpl/service/courseDetail.html',
	            controller: 'courseDetailCtrl'
	          }
	        }
	      })
	      //教练详情展示
	      .state('index.coachDetail', {
	        url: '/coachDetail/:id',
	        cache:false,
	        params:{'id':null},
	        views: {
	          'index-coachlist': {
	            templateUrl: 'tpl/coach/coachDetail.html',
	            controller: 'coachDetailCtrl'
	          }
	        }
	      })
	       //课程安排选择
	      .state('index.serviceStore', {
	        url: '/serviceStore',
	        cache:false, 
	        params:{},
	        views: {
	          'index-serviceList': {
	            templateUrl: 'tpl/service/serviceStore.html',
	            controller: 'serviceStoreCtrl'
	          }
	        },
	        resolve: {
	            customerData: function($location,customerDataService) {
	            	console.log("into resolve customer data.....");
	            	var searchObject = $location.search();
	        		var code = searchObject['code'];
	        		var uid = searchObject['uid'];
	            	return customerDataService.find(uid,code);
	            }
	          }
	      })
	      .state('index.servicePay', {
	        url: '/servicePay/:jsonStr',
	        cache:false,
	        params:{'jsonStr':null},
	        	views: {
					  'index-serviceList':{
						  templateUrl: 'tpl/service/servicePay.html',
						  controller: 'servicePayCtrl'
					  }
				  },	       
				resolve: {
	            customerData: function($location,customerDataService) {
	            	console.log("into resolve customer data.....");
	            	var searchObject = $location.search();
	        		var code = searchObject['code'];
	        		var uid = searchObject['uid'];
	            	return customerDataService.find(uid,code);
	            }
	          }

	      })		  	     

		  .state('index.timesegment', {
			  url: '/timesegment/:coachid/:busiTypeId',
			  cache:false, 
			  params: {'coachid': null,'busiTypeId':null},
			  views: {
				  'index-coachlist':{
					  templateUrl: 'tpl/home/timesegment.html',
					  controller: 'timesegmentCtrl'
				  }
			  },
		        resolve: {
		            customerData: function($location,customerDataService) {
		            	console.log("into resolve customer data.....");
		            	var searchObject = $location.search();
		        		var code = searchObject['code'];
		        		var uid = searchObject['uid'];
		            	return customerDataService.find(uid,code);
		            }
		          }
		  })

		  .state('index.appointmentok', {
		        url: '/appointmentok',
		        cache:false, 
		        params: {'data': null, 'orderId': null,"hasCard":null},
		        views: {
		        	'index-coachlist':{
		        		templateUrl: 'tpl/home/appointmentok.html',
		        		controller: 'appointmentOkCtrl'
		        	}
		        }
		   })
		   .state('index.getCard', {
			   url: '/getCard',
			   cache:false, 
			   params: {'data': null},
			   views: {
				   'index-home':{
					   templateUrl: 'tpl/home/getCard.html',
					   controller: 'getCardCtrl'
				   }
			   }
		   })
		   .state('index.myindex', {
	        url: '/myindex',
	        cache:false, 
	        views: {
	        	'index-my':{
	        		templateUrl: 'tpl/my/myindex.html',
	        		controller: 'myIndexCtrl'
	        	}
	        },
	        resolve: {
	            customerData: function($location,customerDataService) {
	            	console.log("into resolve customer data.....");
	            	var searchObject = $location.search();
	        		var code = searchObject['code'];
	        		var uid = searchObject['uid'];
	            	return customerDataService.find(uid,code);
	            }
	          }
	      })  
			.state('index.myorderList', {
				url: '/myorderList/:viewType',
				cache:false,
				params: {'viewType': null},
				views: {
					'index-my':{
						templateUrl: 'tpl/my/orderList.html',
						controller: 'orderListCtrl'
					}
				}
			})
			.state('index.orderOrderList', {
		        url: '/orderOrderList/:viewType',
		        cache:false,
		        params: {'viewType': null},
		        views: {
		        	'index-order':{
		        		templateUrl: 'tpl/my/orderList.html',
		        		controller: 'orderListCtrl'
		        	}
		        }
		    })
			.state('index.myorderComment', {
		      url: '/myorderComment',
		      cache:false, 
		      params: {'orderId': null},
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/orderComment.html',
		      		controller: 'orderCommentCtrl'
		      	}
		      }
		    })			
			.state('index.orderInfo', {
			  url: '/orderInfo/:orderId',
			  cache:false, 
			  params: {'orderId': null},
			  views: {
				  'index-my':{
					  templateUrl: 'tpl/my/orderInfo.html',
					  controller: 'orderInfoCtrl'
				  }
			  }
		    })					
			.state('index.myorderProcessDetail', {
		      url: '/myorderProcessDetail',
		      cache:false, 
		      params: {'orderId': null},
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/orderProcessDetail.html'
		      	}
		      }
		    })
		    .state('index.myprofileList', {
		      url: '/myprofileList',
		      cache:false, 
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/profileList.html',
		      		controller: 'profileListCtrl'
		      	}
		      },
		        resolve: {
		            customerData: function($location,customerDataService) {
		            	console.log("into resolve customer data.....");
		            	var searchObject = $location.search();
		        		var code = searchObject['code'];
		        		var uid = searchObject['uid'];
		            	return customerDataService.find(uid,code);
		            }
		          }
		    })
		    .state('index.modifyName', {
		      url: '/modifyName',
		      cache:false, 
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/modifyName.html',
		      		controller: 'modifyNameCtrl'
		      	}
		      }
		    })
		    .state('index.modifySex', {
		      url: '/modifySex',
		      cache:false, 
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/modifySex.html',
		      		controller: 'modifySexCtrl'
		      	}
		      }
		    })
		    .state('index.modifyPhone', {
		      url: '/modifyPhone/:customerId/:jsonStr',
		      cache:false, 
			  params:{'customerId':null,'str':null,'jsonStr':null},
		      views: {
		      	'index-my':{
		      		templateUrl: 'tpl/my/modifyPhone.html',
					controller: 'modifyPhoneCtrl'
		      	}
		      }
		    })

		   .state('index.myChildren', {
	        url: '/myChildren/:customerId',
	        cache:false,
	        params: {'customerId': null},
	        views: {
	          'index-my': {
	            templateUrl: 'tpl/my/myChildren.html',
	            controller: 'myChildrenCtrl'
	          }
	        }
	      })
			.state('index.newChild', {
				  url: '/newChild/:customerId/:jsonStr',
				  cache:false, 
				  params:{'customerId':null,'str':null,'jsonStr':null},
				  views: {
					  'index-my':{
						  templateUrl: 'tpl/my/newChild.html',
						  controller: 'newChildCtrl'
					  }
				  }
			})
		    .state('index.cardList', {
			  url: '/cardList',
			  cache:false,
			  views: {
				  'index-my':{
					  templateUrl: 'tpl/my/cardList.html',
					  controller: 'cardListCtrl'
				  }
			  }
		    })
		    .state('index.cardDetail', {
			  url: '/cardDetail/:cardId',
			  cache:false,
			  params:{'cardId':null},
			  views: {
				  'index-my':{
					  templateUrl: 'tpl/my/cardDetail.html',
					  controller: 'cardDetailCtrl'
				  }
			  }
		    })		    
		    .state('index.couponDetail', {
				  url: '/couponDetail',
				  cache:false, 
				  views: {
					  'index-my':{
						  templateUrl: 'tpl/my/couponDetail.html',
						  controller: 'couponDetailCtrl'
					  }
				  }
			 })
		    .state('index.scoreDetail', {
			  url: '/scoreDetail',
			  cache:false, 
			  views: {
				  'index-my':{
					  templateUrl: 'tpl/my/scoreDetail.html',
					  controller: 'scoreDetailCtrl'
				  }
			  }
		    })
		    .state('index.direct', {
			  url: '/direct',
			  cache:false, 
			  views: {
				  'index-my':{
					  templateUrl: 'tpl/my/direct.html',
					  controller: 'directCtrl'
				  }
			  }
		    })
			.state('index.cardCouponBatch', {
				  url: '/cardCouponBatch/:orderId',
				  cache:true, 
				  params:{'orderId':null},
				  views: {
					  'index-my':{
						  templateUrl: 'tpl/my/cardCouponBatch.html',
						  controller: 'cardCouponBatchCtrl'
					  }
				  }
			})
			
			.state('index.cardCouponDetail', {
				  url: '/cardCouponDetail/:type/:id/:orderId',
				  cache:true, 
				  params:{'orderId':null,'type':null,'id':null},
				  views: {
					  'index-my':{
						  templateUrl: 'tpl/my/cardCouponDetail.html',
						  controller: 'cardCouponDetailCtrl'
					  }
				  }
			})
			.state('index.coachlist', {
				url: '/coachlist',
				cache:false,				
				views: {
				  'index-coachlist': {
					templateUrl: 'tpl/coach/coachlist.html',
					controller: 'coachlistCtrl'
				  }
				},
				resolve: {
					customerData: function($location,customerDataService) {
						console.log("into resolve customer data.....");
						var searchObject = $location.search();
						var code = searchObject['code'];
						var uid = searchObject['uid'];
						return customerDataService.find(uid,code);
					}
				  }
			})
			.state('index.process', {
				url: '/process',
				cache:false, 
				views: {
					'index-home': {
						templateUrl: 'tpl/home/process.html',
						controller: 'processCtrl'
					}
				}
			})
			.state('index.serviceSite', {
				url: '/serviceSite',
				cache:false, 
				views: {
					'index-home': {
						templateUrl: 'tpl/home/serviceSite.html',
						controller: 'serviceSiteCtrl'
					}
				}
			})
			.state('index.about', {
				url: '/about',
				cache:false, 
				views: {
					'index-home': {
						templateUrl: 'tpl/home/about.html',
						controller: 'aboutCtrl'
					}
				}
			})
			.state('index.help', {
				url: '/help',
				cache:false, 
				views: {
					'index-home': {
						templateUrl: 'tpl/home/help.html',
						controller: 'helpCtrl'
					}
				}
			})
			.state('index.guarantee', {
				url: '/guarantee',
				cache:false, 
				views: {
					'index-home': {
						templateUrl: 'tpl/home/guarantee.html',
						controller: 'guaranteeCtrl'
					}
				}
			})
			.state('index.location', {
				url: '/location/:itemType/:id',
				cache:false, 
				param: {itemType:null,'id':null},
				views: {
					'index-serviceList': {
						templateUrl: 'tpl/service/location.html',
						controller: 'locationCtrl'
					}
				}
			})
			.state('index.paperService01', {
				url: '/paperService/:id',
				cache:false, 
				param: {'id':null},
				views: {
					'index-serviceList': {
						templateUrl: 'tpl/service/service.0.1.html',
						controller: 'service01Ctrl'
					}
				}
			})
			.state('index.paperService23', {
				url: '/paperService/:id',
				cache:false, 
				param: {'id':null},
				views: {
					'index-serviceList': {
						templateUrl: 'tpl/service/service.2.3.html',
						controller: 'service23Ctrl'
					}
				}
			})
			.state('index.paperService45', {
				url: '/paperService/:id',
				cache:false, 
				param: {'id':null},
				views: {
					'index-serviceList': {
						templateUrl: 'tpl/service/service.4.5.html',
						controller: 'service45Ctrl'
					}
				}
			})
			.state('index.paperService67', {
				url: '/paperService/:id',
				cache:false, 
				param: {'id':null},
				views: {
					'index-serviceList': {
						templateUrl: 'tpl/service/service.6.7.html',
						controller: 'service67Ctrl'
					}
				}
			})
			.state('index.updatetimesegment', {
				url: '/updatetimesegment/:orderId',
				cache:false, 
				params: {'orderId': null},
				views: {
					'index-coachlist':{
						templateUrl: 'tpl/home/updatetimesegment.html',
						controller: 'updateTimesegmentCtrl'
					}
				},
				resolve: {
					customerData: function($location,customerDataService) {
						console.log("into resolve customer data.....");
						var searchObject = $location.search();
						var code = searchObject['code'];
						var uid = searchObject['uid'];
		            	return customerDataService.find(uid,code);
		            }
		          }
		  })

			;
	      // if none of the above states are matched, use this as the fallback
	      $urlRouterProvider.otherwise('/index/home?uid=1&status=home');

});
