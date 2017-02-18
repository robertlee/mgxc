 module.exports = function(grunt) {   	
  //配置参数
  grunt.initConfig({
     pkg: grunt.file.readJSON('package.json'),
	 clean: {
		build: {
			src: ["dest/js/uletian.js", "dest/js/uletian.min.js","dest/css/uletian.css"]
		}
	},
     concat: {
         options: {
             separator: ';',
             stripBanners: true
         },
         dist: {
             src: [
				 "js/app.js",
				 "js/constants.js",
				 "js/controllers.js",
				 "js/filters.js",				 
				 "js/jweixin.js",
				 "js/md5.js",
				 "js/pingpp.js",				 
				 "js/services.js",
				 "js/values.js",
				 "js/common/*.js",
				 "js/home/*.js",
				 "js/my/*.js",
				 "js/service/*.js"
             ],
             dest: "dest/js/uletian.js"
         }
     },
     uglify: {
         options: {
         },
         dist: {
             files: {
                 'dest/js/uletian.min.js': 'dest/js/uletian.js'
             }
         }
     },
	 processhtml: {    
		dist: {
			files: {
			'dest/index.html': ['index.html']			
			}
		}
	}
  });

  //载入concat和uglify插件，分别对于合并和压缩
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-processhtml');

  //注册任务
  grunt.registerTask('default', ['clean','concat', 'uglify','processhtml']);
}