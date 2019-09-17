package com.example.mygcs;

import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.o3dr.services.android.lib.coordinate.LatLong;

import org.droidplanner.services.android.impl.core.polygon.Polygon;
import org.droidplanner.services.android.impl.core.survey.grid.Grid;
import org.droidplanner.services.android.impl.core.survey.grid.GridBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.mygcs.MapFragmentActivity.*;


public class mapsetting {

    PolylineOverlay polyline2;
    PolylineOverlay polyline;
    PolygonOverlay polygon;
    LocationOverlay locationOverlay;
    Marker premarker;
    NaverMap naverMapall;

    mapsetting(PolylineOverlay polyline2, PolylineOverlay polyline, PolygonOverlay polygon, LocationOverlay locationOverlay,Marker premarker,NaverMap naverMap){
        this.polyline2 = polyline2;
        this.polyline = polyline;
        this.polygon = polygon;
        this.locationOverlay = locationOverlay;
        this.premarker = premarker;
        this.naverMapall = naverMap;
    }

    void settting(){

        locationOverlay = naverMapall.getLocationOverlay();


        polygon.setColor(Color.argb(30,0,0,0));
        polyline.setColor(Color.YELLOW);
        polyline2.setColor(-65536);
        premarker.setAnchor(new PointF(0.5F, 0.5F));
        premarker.setWidth(110);
        premarker.setHeight(110);
        premarker.setPosition(new LatLng(35.945021, 126.682829));
        premarker.setIcon(OverlayImage.fromResource(R.drawable.pix15));
        locationOverlay.setPosition(new LatLng(35.945021, 126.682829));

        premarker.setMap(naverMapall);
        locationOverlay.setVisible(true);
    }



    void mapcamera(int dronemode,ArrayList markerarrmin,ArrayList arr2,ArrayList<LatLng> polgonarr, ArrayList markerarr, List<LatLong> polinearr,String st, String st2){

                if (dronemode == 3) {

                    double distance = Integer.parseInt(st);
                    double angle = Integer.parseInt(st2);
                    CameraPosition position = naverMapall.getCameraPosition();

                    if (overboo) {
                        for (int i = 0; i < markerarrmin.size(); i++) {

                            Marker a = (Marker) markerarrmin.get(i);
                            a.setMap(null);

                        }
                        markerarrmin.clear();
                        arr2.clear();
                        Marker ma2 = new Marker();
                        ma2.setTag(overid);
                        ma2.setWidth(markersize);
                        ma2.setHeight(markersize);
                        ma2.setPosition(new LatLng(position.target.latitude, position.target.longitude));
                        ma2.setMap(naverMapall);
                        ma2.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay o) {
                                centermarker.setVisibility(View.VISIBLE);


                                o.setMap(null);
                                overid = o.getTag() + "";
                                overboo = true;
                                return true;
                            }
                        });


                        int a = Integer.parseInt(overid);
                        markerarr.set(a, ma2);
                        polinearr.set(a, new LatLong(position.target.latitude, position.target.longitude));
                        polgonarr.set(a, new LatLng(position.target.latitude, position.target.longitude));

                        Polygon polygon2 = new Polygon();
                        polygon2.addPoints(polinearr);
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

                            Log.d("qqwwee",lat2+"");

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

                        overboo = false;

                        centermarker.setVisibility(View.GONE);

                    }

                }



    }



}
