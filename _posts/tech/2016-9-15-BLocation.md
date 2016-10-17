---
layout: post
title: Android基站定位
category: 技术
keywords: android,Location
---

>定位是一项精细的活，现在GPS基本上已经成为了手机上的一项基本功能，但同时它也有诸多的限制，如启动慢，受环境影响明显等，所以后来又有诸多辅助定位的手段，如基站定位，和WiFi定位，不过两者却都是需要基于庞大的数据支持，也就是必须有基站和WiFi热点与真实GPS坐标映射的数据库才行。

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)



## 1. GSM / UMTS / LTE


	 MCC，Mobile Country Code，移动国家代码（中国的为460）；
	 MNC，Mobile Network Code，移动网络号码（00移动 01联通 11电信4G）； 
	 LAC/TAC(1~65535)，Location Area Code，位置区域码；
	 CID/CI( 2G(1~65535), 3G/4G(1~268435455))，Cell Identity，基站编号；

### android手机的获取:

	TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

     String operator = manager.getNetworkOperator();
     /**通过operator获取 MCC 和MNC */
     int MCC = Integer.parseInt(operator.substring(0, 3));
     int MNC = Integer.parseInt(operator.substring(3));

     GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();

     /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
     int LAC = location.getLac();
     int CID = location.getCid(); 


## 2. CDMA

	BID(1~65535):cdmaCellLocation.getBaseStationId()（基站ID,同GSM的CID）
	NID(0~65535):cdmaCellLocation.getNetworkId() (网络ID,同GSM的LAC)
	SID(0~32767):cdmaCellLocation.getSystemId() (同GSM的MNC)

### android手机中的获取


 		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        CdmaCellLocation location = (CdmaCellLocation)manager.getCellLocation();
        int BID = location.getBaseStationId();
        int NID = location.getNetworkId();
		int SID = location.getSystemId();


## 3. 手机信号的获取

Android可以通过以下代码获取到手机收到的所有信号

	TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operator = manager.getNetworkOperator();
        /**通过operator获取 MCC 和MNC */
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));

        List<CellInfo> allCellInfo =  manager.getAllCellInfo();
        String result = "";
        for (CellInfo info:allCellInfo) {

            if(info instanceof CellInfoCdma){
                CellIdentityCdma cellIdentityCdma = ((CellInfoCdma)info).getCellIdentity();
                result += "BaseStationId:"+cellIdentityCdma.getBasestationId()
                        +",NetworkId:"+cellIdentityCdma.getNetworkId()+",SystemId："
                        +cellIdentityCdma.getSystemId()+",signal"+((CellInfoCdma)info).getCellSignalStrength()+"\n";
            }else if(info instanceof CellInfoLte){
                CellIdentityLte cellIdentityLte = ((CellInfoLte)info).getCellIdentity();
                result += "Mnc:"+cellIdentityLte.getMnc()
                        +",Ci:"+cellIdentityLte.getCi()+",Tac："
                        +cellIdentityLte.getTac()+",signal"+((CellInfoLte)info).getCellSignalStrength()+"\n";
            }else if(info instanceof CellInfoGsm){
                CellIdentityGsm cellIdentityGsm = ((CellInfoGsm)info).getCellIdentity();
                result += "Mnc:"+cellIdentityGsm.getMnc()
                        +",Cid:"+cellIdentityGsm.getCid()+",Lac："
                        +cellIdentityGsm.getLac()+",signal"+((CellInfoGsm)info).getCellSignalStrength()+"\n";
            }else if(info instanceof CellInfoWcdma){
                CellIdentityWcdma cellIdentityWcdma = ((CellInfoWcdma)info).getCellIdentity();
                result += "Mnc:"+cellIdentityWcdma.getMnc()
                        +",Cid:"+cellIdentityWcdma.getCid()+",Lac："
                        +cellIdentityWcdma.getLac()+",signal"+((CellInfoWcdma)info).getCellSignalStrength()+"\n";
            }
        }

每个if条件中得到的结果前面三个大抵是每个基站的标识以及区域信息，就如上面的MNC,CID之类的，而最后一个是获取的信号强度SignalStrength，第一个CellInfoCdma能获取到这几个值

	private int mCdmaDbm;   // This value is the RSSI value
    private int mCdmaEcio;  // This value is the Ec/Io
    private int mEvdoDbm;   // This value is the EVDO RSSI value
    private int mEvdoEcio;  // This value is the EVDO Ec/Io
    private int mEvdoSnr;   // Valid values are 0-8.  8 is the highest signal to noise ratio

**RSSI**:Received Signal Strength Indication接收的信号强度指示，无线发送层的可选部分，用来判定链接质量，以及是否增大广播发送强度，可用来测算距离
[RSSI为什么是负值](http://www.cnblogs.com/lele/articles/2832885.html)

**Ec/Io**:这是一个反映手机端当前接收的导频信号（Pilot）的水平。手机开机首先做的事情就是搜索导频信号，如果搜索不到有用的导频信号，手机就无法正确识别网络。很多时候，手机经常会处在很多基站重叠覆盖的区域，也就是有很多导频的区域。各个导频之间也会相互干扰，形成导频污染。Ec表示手机当前接收到的可用导频信号强度，Io表示手机当前所接收到的所有干扰信号强度。所以，Ec/Io就表明手机当前所接收到的有用信号和干扰信号的比例。反映了手机在这一点上多路导频信号的整体覆盖水平。

第二个CellInfoLte能获取到的值：

	private int mSignalStrength;  //信号强度
    private int mRsrp;			  //表示LTE参考信号接收质量，这种度量主要是根据信号质量来对不同LTE候选小区进行排序。这种测量用作切换和小区重选决定的输入。
    private int mRsrq;            //表示发送信号质量
    private int mRssnr;
    private int mCqi;
    private int mTimingAdvance;

第三个CellSignalStrengthGsm和第四个CellSignalStrengthWcdma能获取到的值：

	private int mSignalStrength; // Valid values are (0-31, 99) as defined in TS 27.007 8.5
    private int mBitErrorRate;   // bit error rate (0-7, 99) as defined in TS 27.007 8.5

到这里可能会郁闷，lte,cdma,gsm这些是什么？这些是手机用到的通信技术，具体解释网上很多，而划分到三大运营商如下：

* 中国移动，2G：GSM，3G：TD-SCDMA ,4G:TD-LTE
* 中国联通，2G：GSM，3G：WCDMA ,4G:FDD-LTE
* 中国电信，2G：CDMA（实际上相当于2.5G），3G：CDMA 2000 ,4G:FDD-LTE、TD-LTE

在Android内部区分信号类型是按照如下规则:

	switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
            case NETWORK_TYPE_IWLAN:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }

这些信号类型是为了针对不同手机卡不同返回值做具体处理用的，但是返回的东西的用途确是一样的

* 基站标识，用来从已经知道的数据库中取出对应的卫星坐标，这个获取数据库是关键，网上有许多付费提供的接口，有些免费，却限制次数，但是所幸，我需要做基站定位的地方，区域有限，想取到这些位置可能覆盖到的基站信息可能并不难，只需要写一个程序，拿着手机在区域内到处走走，搜集到所有基站的信息，然后自己建个数据库即可（0），也可以用网上限制次数的免费接口，反正只用一次也足够，总之，方法很多。



* 当前位置距离基站的距离，这个对于我而言比较复杂，我能想到的有两种方法，一是通过电波到达时间来算距离，即TDOA定位，但是找不到获取对应信息的方法，只能放弃，二是通过信号强度来计算出自己距离基站的位置，而信号强度上面已经获取到了，只剩下计算的方法（1）（2）。获取到距离后通过三个或三个以上的基站就可以定位到当前位置。

（0）[基站查询](http://www.gpsspg.com/bs.htm)

（1）[比较详细的参考](http://www.cnblogs.com/magicboy110/archive/2010/12/10/1902741.html)

（2）[自由空间路径传播损耗](http://www.360doc.com/content/15/0725/11/8493019_487280425.shtml)

除了基站定位，还有WiFi定位，与基站定位原理一样，只要附近有WiFi，不需要连接上，只需获取其唯一的标识，然后通过数据库查询其对应的坐标即可，数据库的建立是问题，毕竟WiFi哪里都是，于是有人说，找几个出租，给些钱，装上GPS与WiFi连接对应设备到处跑，不是也很快吗，想想也是，一些看似庞大的工程，其实往往不难，只要用对地方，我们平时常用app，谁知道会不会把我们连接的WiFi和已经获取GPS坐标绑定上传呢。

以上是Android基站定位的原理，其实如果不执着于完全的自主开发，完全可以用第三方提供的服务如[百度定位](http://lbsyun.baidu.com/index.php?title=android-locsdk)，大致原理相同，但是很多算法不需要自己去考虑。