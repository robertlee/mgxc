本项目采用grunt压缩js及css文件，并替换到html中
使用grunt需先安装npm
1.安装npm,请自行参阅相关文档

2.安装完npm后安装grunt cli
npm install -g grunt-cli

3.进入grunt工程所在目录，安装grunt
cd ./resources/static
npm install grunt --save-dev

4.安装需用到的grunt插件，
    "grunt-contrib-clean": "^0.6.0",  \\清除文件
    "grunt-contrib-concat": "~0.5.1", \\合并js
    "grunt-contrib-cssmin": "~0.12.3", \\合并压缩css
    "grunt-contrib-imagemin": "^0.9.4", \\压缩图片，未使用    
    "grunt-contrib-uglify": "~0.5.0", \\压缩js
    "grunt-processhtml": "^0.3.8"  \\处理html，替换为压缩的js
    
    npm install grunt-contrib-clean --save-dev
    npm install grunt-contrib-concat --save-dev
    npm install grunt-contrib-cssmin --save-dev
    npm install grunt-contrib-uglify --save-dev
    npm install grunt-processhtml --save-dev
    
5.标识哪些html中的js需要替换
  在需要替换的js上用以下注释包围
  <!-- build:js default.min.js -->
  <!-- /build -->

5.执行grunt
在grunt工程所在的目录,运行grunt

6.压缩后的文件和替换的Html文件 在./resource/static/dest目录下

    
    
    
