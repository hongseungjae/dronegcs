package com.example.mygcs;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.LinkListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Home;
import com.o3dr.services.android.lib.gcs.link.LinkConnectionStatus;

import java.util.ArrayList;

public class MapFragmentActivity extends FragmentActivity
        implements OnMapReadyCallback, Button.OnClickListener, NaverMap.OnMapLongClickListener, CompoundButton.OnCheckedChangeListener, DroneListener, TowerListener, LinkListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    NaverMap naverMapall;
    PolylineOverlay polyline;
    Context context;
    InfoWindow infoWindow;
    String test;
    String temp;
    boolean check;
    ArrayList<LatLng> latlngarr = new ArrayList<LatLng>();
    ArrayList<Marker> markerarr = new ArrayList<Marker>();
    ArrayList<InfoWindow> infowindowarr = new ArrayList<InfoWindow>();
    ArrayList<String> locationname= new ArrayList<String>();
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private CheckBox cb6;
    Marker premarker;
    Marker postmarker;

    TextView altitudeValueTextView;
    TextView distanceValueTextView;

    Button layer;
    Button connect;
    LinearLayout checkboxlinearLayout;

    private Drone drone;
    private ControlTower controlTower;
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

       // final Context context = getApplicationContext();
        context = getApplicationContext();
        this.controlTower = new ControlTower(context);
        this.drone = new Drone(context);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }



        //context = getApplicationContext();
        mapFragment.getMapAsync(this);

        altitudeValueTextView = (TextView)findViewById(R.id.altitudeValueTextView);
        distanceValueTextView = (TextView)findViewById(R.id.distanceValueTextView);

        Button button = (Button)findViewById(R.id.button);
         layer = (Button)findViewById(R.id.layer);
        connect = (Button)findViewById(R.id.connect);
        checkboxlinearLayout = (LinearLayout)findViewById(R.id.checkboxlinearLayout);

        button.setOnClickListener(this);
        layer.setOnClickListener(this);



        cb1 = (CheckBox)findViewById(R.id.checkBox1);
        cb2 = (CheckBox)findViewById(R.id.checkBox2);
        cb3 = (CheckBox)findViewById(R.id.checkBox3);
        cb4 = (CheckBox)findViewById(R.id.checkBox4);
        cb5 = (CheckBox)findViewById(R.id.checkBox6);
        cb6 = (CheckBox)findViewById(R.id.checkBox4);

        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);


    }

    protected void updateDistanceFromHome() {
        TextView distanceTextView = (TextView) findViewById(R.id.distanceValueTextView);
        Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();

        double distanceFromHome = 0;
        //Log.d("aabb",vehiclePosition+"");


        if (droneGps.isValid()) {


            LatLongAlt vehicle3DPosition = new LatLongAlt(vehiclePosition.getLatitude(), vehiclePosition.getLongitude(), vehicleAltitude);
            alertUser(droneGps.toString());

            CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latlngarr.get(0), 16);
             naverMapall.moveCamera(cameraUpdate);


            premarker.setPosition(new LatLng( vehiclePosition.getLatitude(),vehiclePosition.getLongitude()));
            premarker.setMap(naverMapall);

            Home droneHome = this.drone.getAttribute(AttributeType.HOME);
            distanceFromHome = distanceBetweenPoints(droneHome.getCoordinate(), vehicle3DPosition);
        } else {
            distanceFromHome = 0;
        }

        distanceTextView.setText(String.format("%3.1f", distanceFromHome) + "m");
    }

    protected void updateAltitude() {
        TextView altitudeTextView = (TextView) findViewById(R.id.altitudeValueTextView);
        Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
        altitudeTextView.setText(String.format("%3.1f", droneAltitude.getAltitude()) + "m");
    }

    @Override
    public void onStart() {
        super.onStart();
        this.controlTower.connect(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.drone.isConnected()) {
            this.drone.disconnect();
            updateConnectedButton(false);
        }

        this.controlTower.unregisterDrone(this.drone);
        this.controlTower.disconnect();
    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                alertUser("Drone Connected");
                updateConnectedButton(this.drone.isConnected());
                //updateArmButton();
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone Disconnected");
                updateConnectedButton(this.drone.isConnected());
                //updateArmButton();
                break;

            case AttributeEvent.ALTITUDE_UPDATED:
                updateAltitude();
                break;

            case AttributeEvent.HOME_UPDATED:
                updateDistanceFromHome();
                break;

            default:
                // Log.i("DRONE_EVENT", event); //Uncomment to see events from the drone
                break;
        }
    }

    protected void updateConnectedButton(Boolean isConnected) {
        Button connectButton = (Button) findViewById(R.id.connect);
        if (isConnected) {
            connectButton.setText(getText(R.string.button_disconnect));
        } else {
            connectButton.setText(getText(R.string.button_connect));
        }
    }

    public void onBtnConnectTap(View view) {
        if (this.drone.isConnected()) {
            this.drone.disconnect();
        } else {
            ConnectionParameter params = ConnectionParameter.newUdpConnection(null);
            this.drone.connect(params);
        }
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {

    }

    @Override
    public void onLinkStateUpdated(@NonNull LinkConnectionStatus connectionStatus) {
        switch (connectionStatus.getStatusCode()) {
            case LinkConnectionStatus.FAILED:
                Bundle extras = connectionStatus.getExtras();
                String msg = null;
                if (extras != null) {
                    msg = extras.getString(LinkConnectionStatus.EXTRA_ERROR_MSG);
                }
                alertUser("Connection Failed:" + msg);
                break;
        }
    }

    @Override
    public void onTowerConnected() {
        alertUser("DroneKit-Android Connected");
        this.controlTower.registerDrone(this.drone, this.handler);
        this.drone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {
        alertUser("DroneKit-Android Interrupted");
    }

    protected void alertUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }

    protected double distanceBetweenPoints(LatLongAlt pointA, LatLongAlt pointB) {
        if (pointA == null || pointB == null) {
            return 0;
        }
        double dx = pointA.getLatitude() - pointB.getLatitude();
        double dy = pointA.getLongitude() - pointB.getLongitude();
        double dz = pointA.getAltitude() - pointB.getAltitude();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 체크박스를 클릭해서 상태가 바꾸었을 경우 호출되는 콜백 메서드

        String result = ""; // 문자열 초기화는 빈문자열로 하자


        if(cb1.isChecked()){
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, false);
        }
        if(cb2.isChecked()) {
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false);
        }
        if(cb3.isChecked()) {
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
        }
        if(cb4.isChecked()) {
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false);
        }if(cb5.isChecked()) {
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false);
        }if(cb6.isChecked()) {
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true);
        }else{
            naverMapall.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, false);
        }


    }




    @Override
    public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        PointF screenPt = naverMapall.getProjection().toScreenLocation(latLng);

            Toast.makeText(context,screenPt.x + "  "+screenPt.y,Toast.LENGTH_LONG).show();


    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.button:
                if(!check) {
                    Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
                    Home h = this.drone.getAttribute(AttributeType.HOME);

                    TextView distanceTextView = (TextView) findViewById(R.id.distanceValueTextView);
                    Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
                    double vehicleAltitude = droneAltitude.getAltitude();

                    LatLong vehiclePosition = droneGps.getPosition();
                    if (droneGps.isValid()) {

                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latlngarr.get(0), 16);
                        naverMapall.moveCamera(cameraUpdate);


                        premarker.setPosition(new LatLng( vehiclePosition.getLatitude(),vehiclePosition.getLongitude()));
                        premarker.setMap(naverMapall);

                        Log.d("aabb", droneGps.toString());
                        Log.d("aabb", vehiclePosition.toString());
                        alertUser(droneGps.toString());
                    }

                    polyline.setMap(null);
                    for(int i = 0 ; i < latlngarr.size(); i++){
                        markerarr.get(i).setMap(null);
                        infowindowarr.get(i).setMap(null);
                    }

                    check = true;
                }
                else{

                    polyline.setMap(naverMapall);
                    for(int i = 0 ; i < latlngarr.size(); i++){
                        markerarr.get(i).setMap(naverMapall);
                        infowindowarr.get(i).open(markerarr.get(i));
                    }

                    check = false;
                }

                break;

            case R.id.layer:
                if(checkboxlinearLayout.getVisibility() == View.VISIBLE){
                    checkboxlinearLayout.setVisibility(View.GONE);
                }else{
                    checkboxlinearLayout.setVisibility(View.VISIBLE);
                }
                break;

       /*     case R.id.connect:

                if (this.drone.isConnected()) {
                    this.drone.disconnect();
                } else {
                    ConnectionParameter params = ConnectionParameter.newUdpConnection(null);
                    this.drone.connect(params);
                }

                break;*/
        } // switch

    }   // onclick


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {


        // 위치 추가
        latlngarr.add(new LatLng(35.945021, 126.682829));
        latlngarr.add(new LatLng( 35.967652, 126.736895));
        latlngarr.add(new LatLng( 35.970483, 126.954788));
        latlngarr.add(new LatLng( 35.844426, 127.129364));



        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latlngarr.get(0), 9);

        premarker = new Marker();

    /*    // 마커 추가
        for(int i = 0 ; i < latlngarr.size(); i++){
            markerarr.add(new Marker());
        }

        //마커 표시
        for(int i = 0 ; i < latlngarr.size(); i++){
            Marker marker = markerarr.get(i);
            marker.setPosition(latlngarr.get(i));
            marker.setMap(naverMap);
        }

        // 폴리라인 생성
        polyline = new PolylineOverlay();
        polyline.setCoords(latlngarr);
        polyline.setMap(naverMap);


        //위치 이름 표시
        locationname.add("군산대");
        locationname.add("군산시청");
        locationname.add("원광대");
        locationname.add("전북대");

        //설명 추가
        for(int i =0; i < locationname.size(); i++){

             temp = locationname.get(i);
            infoWindow = new InfoWindow();
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return temp;
                }
            });

            infowindowarr.add(infoWindow);
            infoWindow.open(markerarr.get(i));
        }
*/

        naverMap.moveCamera(cameraUpdate);





        naverMap.setOnMapLongClickListener(this);

        naverMapall = naverMap;

        //스피너추가
        Spinner s = (Spinner)findViewById(R.id.spinner1);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //tv.setText("position : " + position +
                //      parent.getItemAtPosition(position));


                if(position == 0){
                    naverMapall.setMapType(NaverMap.MapType.Basic);
                }else if(position == 1){
                    naverMapall.setMapType(NaverMap.MapType.Navi);
                }else if(position == 2){
                    naverMapall.setMapType(NaverMap.MapType.Satellite);
                }else if(position == 3){
                    naverMapall.setMapType(NaverMap.MapType.Hybrid);
                }else if(position == 4){
                    naverMapall.setMapType(NaverMap.MapType.Terrain);
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    } // onMapReady



}