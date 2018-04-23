---
layout: post
title: android svg动画
category: Android
keywords: android,svg
---



用过矢量绘图的人都应该知道svg是一种矢量图的格式，内容如下：

	<?xml version="1.0" encoding="utf-8"?>
	<!-- Generator: Adobe Illustrator 19.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  -->
	<svg version="1.1" id="图层_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
		 viewBox="0 0 595.3 841.9" style="enable-background:new 0 0 595.3 841.9;" xml:space="preserve">
	<style type="text/css">
		.st0{fill:none;stroke:#000000;stroke-miterlimit:10;}
		.st1{fill:url(#XMLID_18_);stroke:#131320;stroke-miterlimit:10;}
		.st2{fill:#E21B15;stroke:#131320;stroke-miterlimit:10;}
		.st3{fill:none;stroke:#131320;stroke-miterlimit:10;}
	</style>
	<line id="XMLID_1_" class="st0" x1="133.3" y1="145.4" x2="54.7" y2="344.4"/>
	<line id="XMLID_2_" class="st0" x1="183.3" y1="393.4" x2="297.6" y2="448.5"/>
	<line id="XMLID_3_" class="st0" x1="268" y1="203.6" x2="159.8" y2="274"/>
	<line id="XMLID_4_" class="st0" x1="94" y1="444.4" x2="202.7" y2="626.1"/>
	<line id="XMLID_5_" class="st0" x1="426.2" y1="256.7" x2="240.5" y2="330.1"/>
	<line id="XMLID_6_" class="st0" x1="285.3" y1="368.9" x2="437.4" y2="420.9"/>
	<line id="XMLID_7_" class="st0" x1="437.4" y1="256.7" x2="589.4" y2="308.7"/>
	<line id="XMLID_8_" class="st0" x1="589.4" y1="308.7" x2="461.9" y2="421"/>
	<linearGradient id="XMLID_18_" gradientUnits="userSpaceOnUse" x1="262.8693" y1="119.1227" x2="533.773" y2="121.9446">
		<stop  offset="0" style="stop-color:#FFFFFF"/>
		<stop  offset="1" style="stop-color:#000000"/>
	</linearGradient>
	<rect id="XMLID_9_" x="254.7" y="76.1" class="st1" width="270.9" height="88.8"/>
	<path id="XMLID_10_" class="st2" d="M420,525L291.5,775c-5.1-1-115.8-23.3-133.7-93.9c-19.5-77.1,87.3-151.4,154.1-178.6
		c55.7-22.7,154.6-41.6,183.7,0c29,41.5-14.9,138.2-115.3,233.7c-3.1,0.3-6.1,0.7-9.2,1"/>
	<path id="XMLID_11_" class="st2" d="M-165.7,640.3"/>
	<path id="XMLID_12_" class="st3" d="M365,333.4c21.5,0.8,43.1-0.7,64.2-4.6"/>
	<path id="XMLID_13_" class="st3" d="M428.6,292.9c-3.3,5.5-5.7,11.5-8.4,17.2c-9.1,19.4-22.3,37-38.4,51.1"/>
	<path id="XMLID_14_" class="st3" d="M398.7,334c4.2-1.5,8,2.8,10.4,6.6c6.3,10.2,12.7,20.3,19,30.5"/>
	<line id="XMLID_15_" class="st3" x1="36.4" y1="371.1" x2="-92.2" y2="621.1"/>
	<line id="XMLID_16_" class="st3" x1="-92.2" y1="631.2" x2="306.8" y2="198.5"/>
	</svg>


有点像js和xml,如果要在android上绘制，就需要先对它进行解析操作，再进行绘制。