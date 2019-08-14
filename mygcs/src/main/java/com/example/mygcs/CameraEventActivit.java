package com.example.mygcs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import com.naver.maps.map.overlay.Marker;

public class CameraEventActivit extends AppCompatActivity implements OnMapReadyCallback {
    private static final LatLng COORD_1 = new LatLng(35.1798159, 129.0750222);
    private static final LatLng COORD_2 = new LatLng(35.945021, 126.682829);

    private boolean positionFlag;
    private boolean moving;
    NaverMap naverMapall;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_event);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMapall = naverMap;
        Marker marker1 = new Marker();
        marker1.setPosition(COORD_1);
        marker1.setMap(naverMapall);

        Marker marker2 = new Marker();
        marker2.setPosition(COORD_2);
        marker2.setMap(naverMapall);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moving) {
                    naverMapall.cancelTransitions();
                } else {
                    LatLng coord = positionFlag ? COORD_2 : COORD_1;

                    naverMapall.moveCamera(CameraUpdate.scrollTo(coord)
                            .animate(CameraAnimation.Fly, 5000)
                            .cancelCallback(new CameraUpdate.CancelCallback() {
                                @Override
                                public void onCameraUpdateCancel() {
                                    moving = false;
                                    //   fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                    //    Toast.makeText(CameraEventActivity.this, R.string.camera_update_cancelled, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .finishCallback(new CameraUpdate.FinishCallback() {
                                @Override
                                public void onCameraUpdateFinish() {
                                    moving = false;
                                    //      fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                    //     Toast.makeText(CameraEventActivity.this, R.string.camera_update_finished, Toast.LENGTH_SHORT).show();
                                }
                            }));

                    moving = true;
                    // fab.setImageResource(R.drawable.ic_stop_black_24dp);

                    positionFlag = !positionFlag;
                }
            }
        });

        TextView cameraChange = findViewById(R.id.camera_change);
        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {
                CameraPosition position = naverMapall.getCameraPosition();
             //   cameraChange.setText(CameraEventActivity.this.getString(R.string.format_camera_position,
              //          position.target.latitude, position.target.longitude, position.zoom, position.tilt, position.bearing));
                Log.d("aabb",position.target.latitude + " " + position.target.longitude);
            }
        });

        TextView cameraIdle = findViewById(R.id.camera_idle);
        naverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
            //    Log.d("aabb",position.target.latitude + " " + position.target.longitude);
               CameraPosition position = naverMapall.getCameraPosition();
                Log.d("aabbcc",position.target.latitude + " " + position.target.longitude);
            //    cameraIdle.setText(CameraEventActivity.this.getString(R.string.format_camera_position,
              //          position.target.latitude, position.target.longitude, position.zoom, position.tilt, position.bearing));
            }
        });
    }
}