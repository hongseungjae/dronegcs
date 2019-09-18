package com.example.mygcs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.*;

import com.naver.maps.map.NaverMap;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.mission.Mission;

public class uimanage {
    Button button11;
    Button button12;
    Button button13;
    Button button14;
    Button button15;
    Button button16;
    Button button7;
    Button button8;
    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    Mission mission;
    NaverMap naverMapall;
    LinearLayout linear3;
    boolean onmap;
    Context context;
    int dronemode;
    Drone drone;



    uimanage(Button button11,Button button12,Button button13,Button button14,Button button15,Button button16,Button button7,Button button8, EditText editText,EditText editText2,  EditText editText3,    EditText editText4,   Mission mission,  NaverMap naverMapall, LinearLayout linear3,boolean onmap,Context context,int dronemode, Drone drone){

        this.editText = editText;
        this.button7 = button7;
        this.button8 = button8;
        this.button11 = button11;
        this.button12 = button12;
        this.button13 = button13;
        this.button14 = button14;
        this.button15 = button15;
        this.button16 = button16;
        this.editText2 = editText2;
        this.editText3 = editText3;
        this.editText4 = editText4;
        this.mission = mission;
        this.naverMapall = naverMapall;
        this.linear3 = linear3;
        this.onmap=onmap;
        this.context = context;
        this.dronemode = dronemode;
        this.drone = drone;

    }

    void normal(){
        dronemode = 1;
        onmap = false;
        button14.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background2));
        button15.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button16.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button8.setVisibility(View.GONE);
        button8.setVisibility(View.GONE);
        editText2.setVisibility(View.GONE);
        editText3.setVisibility(View.GONE);
        editText4.setVisibility(View.GONE);
        button8.setText("미션 전달");
        mission = this.drone.getAttribute(AttributeType.MISSION);
        mission.clear();
    }

    void area(){
        dronemode = 2;
        onmap = false;
        button14.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button15.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background2));
        button16.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button8.setText("미션 전달");
        button8.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        editText2.setVisibility(View.VISIBLE);
        editText3.setVisibility(View.GONE);
        editText4.setVisibility(View.GONE);
        mission = this.drone.getAttribute(AttributeType.MISSION);
        mission.clear();
    }

    void interval(){
        dronemode = 3;
        onmap = false;
        button14.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button15.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        button16.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background2));
        button8.setText("미션 전달");
        button8.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
        editText2.setVisibility(View.GONE);
        editText3.setVisibility(View.VISIBLE);
        editText4.setVisibility(View.VISIBLE);
        mission = this.drone.getAttribute(AttributeType.MISSION);
        mission.clear();


    }

    void satellite(){

        button11.setBackgroundResource(R.color.orange);
        button11.setBackgroundResource(R.color.black);
        button13.setBackgroundResource(R.color.black);
        button7.setText("위성지도");
        naverMapall.setMapType(NaverMap.MapType.Satellite);
        linear3.setVisibility(View.GONE);

    }

    void terrain(){
        button11.setBackgroundResource(R.color.orange);
        button11.setBackgroundResource(R.color.black);
        button13.setBackgroundResource(R.color.black);
        button7.setText("위성지도");
        naverMapall.setMapType(NaverMap.MapType.Satellite);
        linear3.setVisibility(View.GONE);
    }

    void basic(){
        button11.setBackgroundResource(R.color.black);
        button12.setBackgroundResource(R.color.black);
        button13.setBackgroundResource(R.color.orange);
        button7.setText("일반지도");
        naverMapall.setMapType(NaverMap.MapType.Basic);
        linear3.setVisibility(View.GONE);
    }

    void mapselect(){
        if(linear3.getVisibility() == View.VISIBLE){
            linear3.setVisibility(View.GONE);
        }else{
            linear3.setVisibility(View.VISIBLE);
        }

    }




}
