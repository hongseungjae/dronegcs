package com.example.mygcs;

import android.graphics.Color;
import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.ArrayList;

import static com.example.mygcs.MapFragmentActivity.markersize;
import static com.naver.maps.geometry.GeoConstants.EARTH_RADIUS;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class areamode {

    ArrayList<LatLng> polgonarr;
    ArrayList markerarr;
    PolylineOverlay polyline;
    NaverMap naverMapall;
    PolygonOverlay polygon;


    areamode(ArrayList<LatLng> polgonarr, ArrayList markerarr, PolylineOverlay polyline,PolygonOverlay polygon,NaverMap naverMapall){

        this.polgonarr = polgonarr;
        this.markerarr = markerarr;
        this.polyline = polyline;
        this.naverMapall = naverMapall;
        this.polygon = polygon;

    }


    void Aset(LatLng latLng3){

        LatLng Alat = latLng3;
        Marker ma3 = new Marker();
        ma3.setWidth(markersize);
        ma3.setHeight(markersize);
        ma3.setIconTintColor(Color.RED);
        ma3.setPosition(new LatLng(Alat.latitude,Alat.longitude));
        ma3.setMap(naverMapall);
        polgonarr.add(Alat);
        markerarr.add(ma3);

    }

    void Bset(LatLng latLng3, String st , String st2){

        LatLng Alat = polgonarr.get(0);
        Marker ma2 = new Marker();
        ma2.setWidth(markersize);
        ma2.setHeight(markersize);
        ma2.setIconTintColor(Color.BLUE);
        LatLng Blat = latLng3;
        ma2.setPosition(new LatLng(latLng3.latitude,latLng3.longitude));
        ma2.setMap(naverMapall);
        polgonarr.add(Blat);
        markerarr.add(ma2);
        ArrayList Alatarr = new ArrayList();
        ArrayList Blatarr = new ArrayList();
        Alatarr.add(Alat);
        Blatarr.add(Blat);

        double angle = bearing(Alat.latitude,Alat.longitude,latLng3.latitude,latLng3.longitude);

        double angletemp;
        angletemp  = angle + 90;



        int distancetemp = Integer.parseInt(st);     // 전체거리
        int distancetemp2 = Integer.parseInt(st2);  //간격거리



        LatLng latLng4 =  computeOffset(Alat,distancetemp,angletemp);  // A 기준
        Marker ma4 = new Marker();
        ma4.setWidth(markersize);
        ma4.setHeight(markersize);
        ma4.setPosition(new LatLng(latLng4.latitude, latLng4.longitude));
        ma4.setMap(naverMapall);
        markerarr.add(ma4);


        for(int i = distancetemp2; i < distancetemp; i = i + distancetemp2) {

            LatLng latLng10 =  computeOffset(Alat,i,angletemp);  // A 기준
            Alatarr.add(latLng10);

        }

        Alatarr.add(latLng4);

        LatLng latLng6 =  computeOffset(latLng3,distancetemp,angletemp); // B 기준
        Marker ma5 = new Marker();
        ma5.setWidth(markersize);
        ma5.setHeight(markersize);
        ma5.setPosition(new LatLng(latLng6.latitude, latLng6.longitude));
        ma5.setMap(naverMapall);
        markerarr.add(ma5);
        polgonarr.add(new LatLng(latLng6.latitude, latLng6.longitude));
        polgonarr.add(new LatLng(latLng4.latitude, latLng4.longitude));
        polgonarr.add(Alat);

        for(int i = distancetemp2; i < distancetemp; i = i + distancetemp2){
            LatLng latLng10 =  computeOffset(latLng3,i,angletemp);  // A 기준
            Blatarr.add(latLng10);


        }
        Blatarr.add(latLng6);

        ArrayList latall = new ArrayList();
        latall.add(Alatarr.get(0));


        for(int i = 0 ; i < Alatarr.size()-1; i++){

            if(i % 2 ==0) {
                latall.add(Blatarr.get(i));
                latall.add(Blatarr.get(i + 1));

            }else{
                latall.add(Alatarr.get(i));
                latall.add(Alatarr.get(i + 1));

            }



        }

        for(int i =0; i < latall.size(); i++){
            LatLng lt = (LatLng)latall.get(i);
          //  arr2.add(lt);
            Marker m = new Marker();
            m.setPosition(lt);
            m.setWidth(10);
            m.setHeight(10);
            m.setCaptionText(i+"");
            m.setCaptionTextSize(8);
            m.setMap(naverMapall);
            markerarr.add(m);
        }

        Log.d("aabbccdd",Blatarr.get(Alatarr.size()-1)+"");
        latall.add(Blatarr.get(Alatarr.size()-1));
        Marker m = new Marker();
        m.setPosition((LatLng)Blatarr.get(Alatarr.size()-1));
        m.setWidth(10);
        m.setHeight(10);
        m.setCaptionText(latall.size()-1+"");
        m.setCaptionTextSize(8);
        m.setMap(naverMapall);
        markerarr.add(m);


        polyline.setCoords(latall);
        polyline.setMap(naverMapall);
        polygon.setColor(Color.argb(30,0,0,0));
        polygon.setCoords(polgonarr);
        polygon.setMap(naverMapall);


    }

    public LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= EARTH_RADIUS;
        heading = toRadians(heading);

        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }

    double bearing(double lat1, double lon1,double lat2,double lon2){

        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.

        double Cur_Lat_radian = lat1 * (3.141592 / 180);

        double Cur_Lon_radian = lon1 * (3.141592 / 180);

        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.

        double Dest_Lat_radian = lat2 * (3.141592 / 180);

        double Dest_Lon_radian = lon2 * (3.141592 / 180);

        // radian distance

        double radian_distance = 0;

        radian_distance = Math.acos(sin(Cur_Lat_radian) * sin(Dest_Lat_radian) + cos(Cur_Lat_radian) * cos(Dest_Lat_radian) * cos(Cur_Lon_radian - Dest_Lon_radian));

        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.

        double radian_bearing = Math.acos((sin(Dest_Lat_radian) - sin(Cur_Lat_radian) * cos(radian_distance)) / (cos(Cur_Lat_radian) * sin(radian_distance)));        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.



        double true_bearing = 0;

        if (sin(Dest_Lon_radian - Cur_Lon_radian) < 0)

        {

            true_bearing = radian_bearing * (180 / 3.141592);

            true_bearing = 360 - true_bearing;

        }

        else

        {

            true_bearing = radian_bearing * (180 / 3.141592);

        }

        return true_bearing;

    }

}
