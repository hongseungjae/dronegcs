package com.example.mygcs;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.model.AbstractCommandListener;
import com.o3dr.services.android.lib.model.SimpleCommandListener;

public class guidemode {

    Marker ma;
    NaverMap naverMapall;
    Drone drone;
    String temp = "";

    guidemode(Marker ma , NaverMap naverMapall, Drone drone){
        this.ma = ma;
        this.naverMapall = naverMapall;
        this.drone = drone;
    }

    void guideyes(){

    }


    String guidechange(){
        VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_GUIDED, new SimpleCommandListener() {
            @Override
            public void onError(int executionError) {
                temp = "Unable to Guided";

            }

            @Override
            public void onTimeout() {
                temp = "Guide timeout";

            }
        });
        return temp;
    }

    String guideexec(LatLng latt2, LatLong latl2) {
        ma.setPosition(latt2);
        ma.setMap(naverMapall);

        ControlApi.getApi(drone).goTo(latl2, true, new AbstractCommandListener() {

            @Override
            public void onSuccess() {
                temp = "goto success";

            }

            @Override
            public void onError(int i) {
                temp = "goto error";

            }

            @Override
            public void onTimeout() {
                temp = "goto timeout";

            }

        });
        return temp;
    }

    String Homeexec(LatLong latl2) {

        ControlApi.getApi(drone).goTo(latl2, true, new AbstractCommandListener() {

            @Override
            public void onSuccess() {
                temp = "goto success";

            }

            @Override
            public void onError(int i) {
                temp = "goto error";

            }

            @Override
            public void onTimeout() {
                temp = "goto timeout";

            }

        });
        return temp;
    }

}
