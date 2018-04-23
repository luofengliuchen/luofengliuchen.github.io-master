package com.gps;


/**
 * Created by luofe on 2017/9/13.
 * ��ͼ����ͼ�μ���ʱ��Ҫ����ͼת����ƽ�������㣬������arcgisһ����GPS����ת����ī����ͶӰ����������
 * ƽ������ĵ�λ��m,��������Ƕȷ�����ô�ͻ���GPS���ֻ꣬�������ܼӹ�ƫ
 */
public class GPSUtils {


    public static final double PI = 3.14159265358979324;

    //
    // Krasovsky 1940
    //
    // a = 6378245.0, 1/f = 298.3
    // b = a * (1 - f)
    // ee = (a^2 - b^2) / a^2;
    public static final  double a = 6378245.0;
    public static final  double ee = 0.00669342162296594323;

    //
    // World Geodetic System ==> Mars Geodetic System
    /**
     * GPS����תΪ��������1��ͬturnGpsToGoogle�õ������ͬ
     * */
    public static double[] wgs84ToGCJ02(double wgLat, double wgLon){
        double[] mg = new double[2];
        if (outOfChina(wgLat, wgLon)){
            mg[0] = wgLat;
            mg[1] = wgLon;
            return mg;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        mg[0] = wgLat + dLat;
        mg[1] = wgLon + dLon;
        return mg;
    }

    public static boolean outOfChina(double lat, double lon){
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double transformLat(double x, double y){
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }


    private static double transformLon(double x, double y){
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }



    public static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;


    /**
     * ��������ת�ٶ�����
     * */
    public static double[] bd_encrypt(double gg_lat, double gg_lon){
        double[] result = new double[2];
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        result[0] = z * Math.cos(theta) + 0.0065;
        result[1] = z * Math.sin(theta) + 0.006;
        return result;
    }

    /**�ٶ�����ת��������*/
    public static double[] bd_decrypt(double bd_lat, double bd_lon){
        double[] result = new double[2];
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        result[0] = z * Math.cos(theta);
        result[1] = z * Math.sin(theta);
        return result;
    }


    /**ͨ�����ַ�����������ת��Ϊgps����*/
    public static double[]  googleTurnGps(double gcjLat, double gcjLon){
        double[] location;
        double[] result = new double[2];
        double initDelta = 0.01;
        double threshold = 0.000000001;
        double dLat = initDelta, dLon = initDelta;
        double mLat = gcjLat - dLat, mLon = gcjLon - dLon;
        double pLat = gcjLat + dLat, pLon = gcjLon + dLon;
        double wgsLat, wgsLon, i = 0;
        while(true){
            wgsLat = (mLat + pLat) / 2;
            wgsLon = (mLon + pLon) / 2;
            location = gcj_encrypt(wgsLat, wgsLon);
            dLat = location[0] - gcjLat;
            dLon = location[1] - gcjLon;
            if((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
                break;
            if (dLat > 0) pLat = wgsLat; else mLat = wgsLat;
            if (dLon > 0) pLon = wgsLon; else mLon = wgsLon;
            if (++i > 10000) break;
        }
        result[0] = wgsLat;
        result[1] = wgsLon;
        return result;
    }
    
    
	/**GPS����ת��������2*/
	public static double[] turnGpsToGoogle(double wgLat,double wgLon){
		 double dLat = turnLat(wgLon - 105.0, wgLat - 35.0);
		 double dLon = turnLon(wgLon - 105.0, wgLat - 35.0);
		 double radLat = wgLat / 180.0 * PI;
		 double magic = Math.sin(radLat);
         magic = 1 - ee * magic * magic;
         double sqrtMagic = Math.sqrt(magic);
         dLat = (dLat * 180.0)/((a * (1 - ee)) / (magic * sqrtMagic) * PI);
         dLon = (dLon * 180.0)/(a/sqrtMagic * Math.cos(radLat) * PI);
         double lat = dLat + wgLat;
         double lon = dLon + wgLon;
         double JWDarray[] = new double[2];
         JWDarray[0] = lat;
         JWDarray[1] = lon;
         return JWDarray;
	}
	//gosγ������תGoogleγ������
	public static double turnLat(double x,double y){
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
	}
	//GPS��������תGoogle��������
	public static double turnLon(double x,double y){
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
	}
    
    


    public static double[]  gcj_encrypt(double wgsLat,double wgsLon){
        double[] location = new double[2];
        double[] d = delta(wgsLat, wgsLon);
        location[0] += d[0];
        location[1] += d[1];
        return location;
    }

    public static double[]  delta(double lat,double lon){
        double[] d = new double[2];
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        double dLat = transformLat(lon - 105.0,lat - 35.0);
        double dLon = transformLon(lon - 105.0,lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        d[0] = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        d[1] = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        return d;
    }


    /** ����뾶����λ����� */
    public final static double EARTH_RADIUS_KM = 6378137;

    /**
     * ���ݾ�γ�ȼ�����������������ľ��루�������磩
     * @param lng1
     *            ��㾭��
     * @param lat1
     *            ���γ��
     * @param lng2
     *            �յ㾭��
     * @param lat2
     *            �յ�γ��
     * @return ������루��λ��ǧ�ף�
     */
    public static double getDistance(double lng1, double lat1, double lng2,
                               double lat2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double radLng1 = Math.toRadians(lng1);
        double radLng2 = Math.toRadians(lng2);
        double deltaLat = radLat1 - radLat2;
        double deltaLng = radLng1 - radLng2;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(
                Math.sin(deltaLat / 2), 2)
                + Math.cos(radLat1)
                * Math.cos(radLat2)
                * Math.pow(Math.sin(deltaLng / 2), 2)));
        distance = distance * EARTH_RADIUS_KM;
        distance = Math.round(distance * 10000) / 10000;
        return distance;
    }

    /**
     * �����������֮��ľ���,���ݽ�����ģ��Ϊ����ͨ�������㷨���
     * */
    public static double getEarthDistance(double lat1,double lon1,
                                 double lat2, double lon2){
        double ��1 = Math.toRadians(lat1);
        double ��2 = Math.toRadians(lat2);
        double ���� = Math.toRadians(lat2-lat1);
        double ���� =  Math.toRadians(lon2-lon1);

        double a = Math.sin(����/2) * Math.sin(����/2) +
                Math.cos(��1) * Math.cos(��2) *
                        Math.sin(����/2) * Math.sin(����/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = EARTH_RADIUS_KM * c;
        return d;
    }

    /**��ȡ�㵽�߶εľ���*/
    public double getDistanceBetweenPointAndLine(){
        return 0;
    }


  
}
