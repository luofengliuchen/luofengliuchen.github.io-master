---
layout: post
title: Google离线地图
category: 技术
keywords: google,map,api
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

### 1.初始化地图

	map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: -34.397, lng: 150.644},
          zoom: 8
        });

### 2.还可以添加地图选项：

	map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);
	 var mapOptions = {
          zoom: 16,
          center: myLatlng,
		  mapTypeControl: true,
          mapTypeControlOptions: {
			  style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
			mapTypeIds: [
			'localMap' ]  //定义地图类型
		  },
		  //平移控制器
		  panControl: false,
		  //缩放控制器
		  zoomControl: false,
		  //地图切换
		  mapTypeControl: true,
		  scaleControl: false,
		  //街道视图控制（小人）
		  streetViewControl: false,
		  //鹰眼图
		  overviewMapControl: true
        }

### 3.绑定本地地图

	map.mapTypes.set('localMap', localMapType);   //绑定本地地图类型

localMapType是返回的瓦片，可以这样获取：

	function LocalMapType() {}
  
        LocalMapType.prototype.tileSize = new google.maps.Size(256, 256);
        LocalMapType.prototype.maxZoom = 16;   //地图显示最大级别
        LocalMapType.prototype.name = "本地地图";
        LocalMapType.prototype.alt = "显示本地地图数据";
		//设置地图瓦片的边框和颜色，当前为不可见，因为width为0
        LocalMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
			var div = ownerDocument.createElement('div');
			div.innerHTML = '<img name="" src="./maptile/googlemaps/roadmap/' + zoom + '/' + coord.x + '/' + coord.y+'.jpg"/>'; 
			div.style.width = this.tileSize.width + 'px';
			div.style.height = this.tileSize.height + 'px';
			div.style.fontSize = '10';
			div.style.borderStyle = 'solid';
			div.style.borderWidth = '0px';
			div.style.borderColor = '#AAAAAA';
			//瓦片背景颜色
			div.style.backgroundColor = '#E5E3DF';
		  return div;
        };
      var localMapType = new LocalMapType();

### 5.基本的一个坐标点对象：

	var myLatlng = new google.maps.LatLng(39.97094, 116.37234);

### 6.指定显示本地地图

	map.setMapTypeId('localMap');  
  
### 7.设置可拖曳地图：

	map.setOptions({draggable: true});

### 8.获取地图边界：

	mapRangeBound = map.getBounds();

### 9.map上添加事件，如下为点击事件：

	map.addListener('click', function(event) {})

### 10.为map瓦块div添加点击事件：

	google.maps.event.addDomListener(mapDiv, 'click', function() {});

### 11.点击事件：

	google.maps.event.addListener

### 12.代码拖动地图：

	map.panTo(new google.maps.LatLng(39.97094, 116.37234));

### 13.拖动地图事件：

	map.addListener('center_changed', function() {
          // 3 seconds after the center of the map has changed, pan back to the
          // marker.
          window.setTimeout(function() {
            map.panTo(marker.getPosition());
          }, 3000);
        });

### 14.点击marker改变地图显示等级和中心：

	marker.addListener('click', function() {
          map.setZoom(8);
          map.setCenter(marker.getPosition());
        });

### 15.添加消息显示框：

	function attachSecretMessage(marker, secretMessage) {
        var infowindow = new google.maps.InfoWindow({
          content: secretMessage
        });
		
        marker.addListener('click', function() {
          infowindow.open(marker.get('map'), marker);
        });
      }

## 注意:

google.maps.event.addListener是为其他对象添加事件，而map.addListener是为map本身添加对象

google.maps.event是事件对象，可以通过获取到很多东西。如坐标还可能是图形对象，具体看情况，以下就是图形对象：

		google.maps.event.addListener(drawingManager, 'circlecomplete', function(circle) {
	  var radius = circle.getRadius();
	});
	
	google.maps.event.addListener(drawingManager, 'overlaycomplete', function(event) {
	  if (event.type == 'circle') {
	    var radius = event.overlay.getRadius();
	  }
	});

js

	设置位置没有加单位导致不能用，在地图上绘图时，google.maps.Map 事件（如 click 和 mousemove）将被禁用。

google地图的点击事件会有遮挡和屏蔽的效果。所以如果处于绘图模式map的点击等一系列效果会被屏蔽。



