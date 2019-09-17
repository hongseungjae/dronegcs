package com.example.mygcs;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.o3dr.services.android.lib.coordinate.LatLong;

import org.droidplanner.services.android.impl.core.polygon.Polygon;
import org.droidplanner.services.android.impl.core.survey.grid.Grid;
import org.droidplanner.services.android.impl.core.survey.grid.GridBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.example.mygcs.MapFragmentActivity.*;
import static com.example.mygcs.MapFragmentActivity.markersize;

public class intervalmode {

    List<LatLong> polinearr;
    ArrayList<LatLng> polgonarr;
    ArrayList markerarr;
    PolylineOverlay polyline;
    NaverMap naverMapall;
    PolygonOverlay polygon;
    ArrayList arr2;
    ArrayList markerarrmin;

    intervalmode(List<LatLong> polinearr, ArrayList<LatLng> polgonarr, ArrayList markerarr, PolylineOverlay polyline,PolygonOverlay polygon,ArrayList arr2,ArrayList markerarrmin,NaverMap naverMapall){

        this.polinearr = polinearr;
        this.polgonarr = polgonarr;
        this.markerarr = markerarr;
        this.polyline = polyline;
        this.naverMapall = naverMapall;
        this.polygon = polygon;
        this.arr2 = arr2;
        this.markerarrmin = markerarrmin;

    }




    void three_down_set(LatLng latLng3,LatLong latLng33){

        Marker ma2 = new Marker();
        ma2.setTag(markercount);
        ma2.setPosition(latLng3);
        ma2.setMap(naverMapall);
        ma2.setWidth(markersize);
        ma2.setHeight(markersize);
        polinearr.add(latLng33);
        markerarr.add(ma2);
        polgonarr.add(latLng3);
        ma2.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay o) {
                //Toast.makeText(context, o+"오버레이 클릭됨", Toast.LENGTH_SHORT).show();
                centermarker.setVisibility(View.VISIBLE);

                o.setMap(null);
                overid = o.getTag() + "";
                overboo = true;
                return true;
            }
        });


        markercount++;

    }


    void three_up_set(String st,String st2){



            arr2.clear();

            for (int i = 0; i < markerarrmin.size(); i++) {
                Marker a = (Marker) markerarrmin.get(i);
                a.setMap(null);
            }

            markerarrmin.clear();


            Polygon polygon2 = new Polygon();
            polygon2.addPoints(polinearr);


            double distance = Integer.parseInt(st);
            double angle = Integer.parseInt(st2);

            Grid grid = null;
            GridBuilder gridBuilder = new GridBuilder(polygon2, angle, distance, new LatLong(0, 0));

            try {
                polygon2.checkIfValid();
                grid = gridBuilder.generate(false);


            } catch (Exception e) {
                e.printStackTrace();

            }


            for (int i = 0; i < grid.gridPoints.size(); i++) {
                LatLong lat = grid.gridPoints.get(i);

                LatLng lat2 = new LatLng(lat.getLatitude(), lat.getLongitude());

                arr2.add(lat2);

                Marker m = new Marker();
                m.setPosition(lat2);
                m.setWidth(10);
                m.setHeight(10);
                m.setCaptionText(i + "");
                m.setCaptionTextSize(8);
                m.setMap(naverMapall);
                markerarrmin.add(m);
            }




            polyline.setCoords(arr2);
            polyline.setMap(naverMapall);
            polygon.setCoords(polgonarr);
            polygon.setMap(naverMapall);

       //if문 마커 3개 이상




    } // else문 A지점 이후


    }



