---
layout: post
title: Bootstrap
category: HTML5
keywords: bootstrap
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

[参考bootstrap起步文档](http://v3.bootcss.com/getting-started/#download)

## 下载bootstrap并解压

## 安装编译系统Grunt

首先下载并安装 node.js，之前安装cordova时安装过了，省略。

按装grunt-cli ：

	npm install -g grunt-cli

进入 /bootstrap/ 根目录，然后执行 npm install 命令。npm 将读取 package.json 文件并自动安装此文件中列出的所有被依赖的扩展包。

如果在安装依赖包或者运行 Grunt 命令时遇到了问题，请首先删除 npm 自动生成的 /node_modules/ 目录，然后，再次运行 npm install 命令（这一步需要翻墙不然会有如下错误）。

	npm ERR! Windows_NT 10.0.14393
	npm ERR! argv "F:\\programer2\\nodejs\\node.exe" "F:\\programer2\\nodejs\\node_modules\\npm\\bin\\npm-cli.js" "install"
	npm ERR! node v6.10.0
	npm ERR! npm  v3.10.10
	npm ERR! code ELIFECYCLE
	
	npm ERR! phantomjs@1.9.20 install: `node install.js`
	npm ERR! Exit status 1
	npm ERR!
	npm ERR! Failed at the phantomjs@1.9.20 install script 'node install.js'.
	npm ERR! Make sure you have the latest version of node.js and npm installed.
	npm ERR! If you do, this is most likely a problem with the phantomjs package,
	npm ERR! not with npm itself.
	npm ERR! Tell the author that this fails on your system:
	npm ERR!     node install.js
	npm ERR! You can get information on how to open an issue for this project with:
	npm ERR!     npm bugs phantomjs
	npm ERR! Or if that isn't available, you can get their info via:
	npm ERR!     npm owner ls phantomjs
	npm ERR! There is likely additional logging output above.
	
	npm ERR! Please include the following file with any support request:
	npm ERR!     F:\programer2\bootstrap\bootstrap-3.3.7\npm-debug.log

grunt命令运行却没问题

	grunt dist （仅编译 CSS 和 JavaScript 文件）
	grunt watch （监测文件的改变，并运行指定的 Grunt 任务）
	grunt test （运行测试用例）
	grunt docs （编译并测试文档中的资源文件）
	grunt （重新构建所有内容并运行测试用例）

可以编译javascript和css,文件夹下dest文件夹为生成的直接可以在生产环境中使用的js和css代码

当然，这个dest文件夹里面的内容也可以直接在官网上下载。

## bootstrap基本模板

	<!DOCTYPE html>
	<html lang="zh-CN">
	  <head>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
	    <title>Bootstrap 101 Template</title>
	
	    <!-- Bootstrap -->
	    <link href="css/bootstrap.min.css" rel="stylesheet">
	
	    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	    <!--[if lt IE 9]>
	      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
	      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
	  </head>
	  <body>
	
	    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	    <script src="js/jquery-3.2.0.min.js"></script>
	    <!-- Include all compiled plugins (below), or include individual files as needed -->
	    <script src="js/bootstrap.min.js"></script>
	  </body>
	</html>

## bootstrap控件实例学习

[参考中文文档](http://v3.bootcss.com/components/)

学习最方便的途径就是做自己的东西，所以我这里写一个介绍某某平台产品的页面

[例子URL](../../../html5/bootstrap.html)

## 为什么需要编译bootstrap

我们知道，js和css和Java代码不同是不需要编译的，但是bootstrap的编译主要是为了生成javascript和css代码；存储bootstrap样式的代码主要写在less中，通过编译可以生成对应的css代码，从而使更改样式变得简单。