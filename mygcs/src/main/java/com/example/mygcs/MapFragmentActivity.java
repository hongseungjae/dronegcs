package com.example.mygcs;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.MissionApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.LinkListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.mission.Mission;
import com.o3dr.services.android.lib.drone.mission.item.spatial.Waypoint;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Attitude;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.gcs.link.LinkConnectionStatus;
import com.o3dr.services.android.lib.model.AbstractCommandListener;
import com.o3dr.services.android.lib.model.SimpleCommandListener;
import org.droidplanner.services.android.impl.core.polygon.Polygon;
import org.droidplanner.services.android.impl.core.survey.grid.Grid;
import org.droidplanner.services.android.impl.core.survey.grid.GridBuilder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import static com.naver.maps.geometry.GeoConstants.EARTH_RADIUS;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MapFragmentActivity extends FragmentActivity
        implements OnMapReadyCallback, Button.OnClickListener, NaverMap.OnMapLongClickListener, CompoundButton.OnCheckedChangeListener, DroneListener, TowerListener, LinkListener ,NaverMap.OnMapClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;


    NaverMap naverMapall;
    PolylineOverlay polyline2;
    PolylineOverlay polyline;
    PolygonOverlay polygon;
    Context context;
    areamode areamode;
    intervalmode intervalmode;
    ArrayList<LatLng> latlngarr = new ArrayList<LatLng>();
    ArrayList<LatLng> polgonarr = new ArrayList<LatLng>();
    ArrayList singModels = new ArrayList<>();
    ArrayList arr2 = new ArrayList();
    ArrayList markerarr = new ArrayList();
    ArrayList markerarrmin = new ArrayList();

    List<LatLong> polinearr = new ArrayList();
    Mission mission;
    mapsetting ms;
    guidemode guidemode;
    uimanage uimanage;

    boolean mapgps;
    int dronemode = 1; // 일반모드 , 면적모드 , 간격모드

    static ImageView centermarker;
    static int markercount = 0;
    static int markersize = 100;
    static String overid;
    static boolean overboo;

    int droneType = Type.TYPE_UNKNOWN;

    double mRecentAltitude = 0;

    boolean onmap;


    Grid grid;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    CheckBox cb4;
    CheckBox cb5;
    CheckBox cb6;
    Marker premarker;


    LatLng latt2;
    LatLong latl2;

    TextView volt;
    TextView alt;
    TextView speed;
    TextView YAW;
    TextView sate;
    TextView textView2;


    LocationOverlay locationOverlay;

    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;

    Spinner modeSelector;
    LinearLayout checkboxlinearLayout;
    LinearLayout linear3;
    Button button;
    Button clear;
    Button layer;
    Button connect;
    Button btnmapgps;
    Button button8;
    Button button11;
    Button button12;
    Button button13;
    Button button14;
    Button button15;
    Button button16;
    Button home;
    Button location;
    Button button7;
    Button button2;
    Button button3;
    Button clear2;
    int takeoffalt;
    AlertDialog.Builder builder;

    Marker ma;
    private Drone drone;
    private ControlTower controlTower;
    private final Handler handler = new Handler();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Queue q = new LinkedList();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        centermarker = (ImageView)findViewById(R.id.centermarker);
        setRecyclerView();
        takeoffalt = 3;



        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        ma = new Marker();
        context = getApplicationContext();
        this.controlTower = new ControlTower(context);
        this.drone = new Drone(context);
        this.modeSelector = (Spinner) findViewById(R.id.modeSelect);
        this.modeSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onFlightModeSelected(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }




        mapFragment.getMapAsync(this);

        volt = (TextView)findViewById(R.id.volt);
        alt = (TextView)findViewById(R.id.alt);
        speed = (TextView)findViewById(R.id.speed);
        YAW = (TextView)findViewById(R.id.YAW);
        sate = (TextView)findViewById(R.id.sate);
        textView2 = (TextView)findViewById(R.id.textView2);


        home = (Button)findViewById(R.id.home);
        location = (Button)findViewById(R.id.location);
        clear2= (Button)findViewById(R.id.clear2);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button7 = (Button)findViewById(R.id.button7);
        button8 = (Button)findViewById(R.id.button8);
        button11 = (Button)findViewById(R.id.button11);
        button12 = (Button)findViewById(R.id.button12);
        button13 = (Button)findViewById(R.id.button13);
        button14 = (Button)findViewById(R.id.button14);
        button15 = (Button)findViewById(R.id.button15);
        button16 = (Button)findViewById(R.id.button16);
        editText = (EditText)findViewById(R.id.editText);
        editText2 = (EditText)findViewById(R.id.editText2);
        editText3 = (EditText)findViewById(R.id.editText3);
        editText4 = (EditText)findViewById(R.id.editText4);
        linear3 = (LinearLayout)findViewById(R.id.linear3);
        button = (Button)findViewById(R.id.button);
        layer = (Button)findViewById(R.id.layer);
        connect = (Button)findViewById(R.id.connect);
        checkboxlinearLayout = (LinearLayout)findViewById(R.id.checkboxlinearLayout);
        btnmapgps = (Button)findViewById(R.id.btnmapgps);
        clear = (Button)findViewById(R.id.clear);

        cb1 = (CheckBox)findViewById(R.id.checkBox1);
        cb2 = (CheckBox)findViewById(R.id.checkBox2);
        cb3 = (CheckBox)findViewById(R.id.checkBox3);
        cb4 = (CheckBox)findViewById(R.id.checkBox4);
        cb5 = (CheckBox)findViewById(R.id.checkBox6);
        cb6 = (CheckBox)findViewById(R.id.checkBox4);


        button.setOnClickListener(this);
        layer.setOnClickListener(this);
        btnmapgps.setOnClickListener(this);
        clear.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        button14.setOnClickListener(this);
        button15.setOnClickListener(this);
        button16.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        home.setOnClickListener(this);
        location.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        clear2.setOnClickListener(this);


        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);


    }

    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(singModels);


        recyclerView.setAdapter(adapter);
    }


    void setadd(String temp){

        if(q.size() >= 4){
            q.poll();
            q.add(temp);
        }else{
            q.add(temp);
        }

        int size = q.size();
        singModels.clear();
        for(int i = 0; i < size; i++){
            String str = q.poll()+"";
            singModels.add(new RecyclerItem(str));
            q.add(str);
        }
        adapter.notifyDataSetChanged();

    }





    public void onFlightModeSelected(View view) {
        VehicleMode vehicleMode = (VehicleMode) this.modeSelector.getSelectedItem();

        VehicleApi.getApi(this.drone).setVehicleMode(vehicleMode, new AbstractCommandListener() {
            @Override
            public void onSuccess() {
                alertUser("Vehicle mode change successful.");
                setadd("Vehicle mode change successful.");

            }

            @Override
            public void onError(int executionError) {
                alertUser("Vehicle mode change failed: " + executionError);
                setadd("Vehicle mode change failed: " + executionError);
            }

            @Override
            public void onTimeout() {
                alertUser("Vehicle mode change timed out.");
                setadd("Vehicle mode change timed out.");

            }
        });
    }

    protected void updateVehicleModesForType(int droneType) {

        List<VehicleMode> vehicleModes = VehicleMode.getVehicleModePerDroneType(droneType);
        ArrayAdapter<VehicleMode> vehicleModeArrayAdapter = new ArrayAdapter<VehicleMode>(this, android.R.layout.simple_spinner_item, vehicleModes);
        vehicleModeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.modeSelector.setAdapter(vehicleModeArrayAdapter);
    }

    protected void updateVehicleMode() {
        State vehicleState = this.drone.getAttribute(AttributeType.STATE);
        VehicleMode vehicleMode = vehicleState.getVehicleMode();
        ArrayAdapter arrayAdapter = (ArrayAdapter) this.modeSelector.getAdapter();
        this.modeSelector.setSelection(arrayAdapter.getPosition(vehicleMode));
    }



    protected void updatedronePosition(){


        Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();


        if (droneGps.isValid()) {


            LatLng lat = new LatLng( vehiclePosition.getLatitude(),vehiclePosition.getLongitude());

            latlngarr.add(lat);

            polyline2.setCoords(latlngarr);
            polyline2.setMap(naverMapall);


            Log.d("aabb","Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());


            if(!mapgps) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(lat, 20);
                naverMapall.moveCamera(cameraUpdate);
            }

            premarker.setPosition(lat);
            premarker.setMap(naverMapall);
            sate.setText(droneGps.getSatellitesCount()+"");

        }


    }



    protected void updateAttribute() {

        Attitude droneAttribute = this.drone.getAttribute(AttributeType.ATTITUDE);
        double y =droneAttribute.getYaw();
        Log.d("aabbcc", "pitch : "+droneAttribute.getPitch() + " , roll : "+droneAttribute.getRoll() + " , yaw : "+droneAttribute.getYaw());
        int yy = (int)y;
        yy = yy +90;

        if(y >= 0){


            premarker.setAngle(yy);
            Log.d("aabbcc", "deg : " + yy);
            YAW.setText(yy + "deg");
        }{
            yy = 360+yy;

            premarker.setAngle(yy);
            Log.d("aabbcc", "-deg : " + yy);
            YAW.setText(yy + "deg");

        }

    }

    protected void updateAltitude() {

        Altitude currentAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
        mRecentAltitude = currentAltitude.getRelativeAltitude();
        mRecentAltitude = Double.parseDouble(String.format("%.1f", mRecentAltitude));


        alt.setText(mRecentAltitude+" m");

    }

    @Override
    public void onStart() {
        super.onStart();
        this.controlTower.connect(this);
        updateVehicleModesForType(this.droneType);
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
                setadd("Drone Connected");
                updateConnectedButton(this.drone.isConnected());
                updateArmButton();
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone Disconnected");
                setadd("Drone Disconnected");
                updateConnectedButton(this.drone.isConnected());
                updateArmButton();
                break;

            case AttributeEvent.TYPE_UPDATED:
                Type newDroneType = this.drone.getAttribute(AttributeType.TYPE);
                if (newDroneType.getDroneType() != this.droneType) {
                    this.droneType = newDroneType.getDroneType();
                    updateVehicleModesForType(this.droneType);
                }
                break;

            case AttributeEvent.MISSION_DRONIE_CREATED:
                setadd("MISSION_DRONIE_CREATED");
                break;

            case AttributeEvent.MISSION_ITEM_REACHED:
                setadd("MISSION_ITEM_REACHED");
                break;

            case AttributeEvent.MISSION_ITEM_UPDATED:
                setadd("MISSION_ITEM_UPDATED");
                break;

            case AttributeEvent.MISSION_RECEIVED:
                setadd("MISSION_RECEIVED");
                break;
            case AttributeEvent.MISSION_SENT:
                button8.setText("미션 수행");

                setadd("MISSION_SENT");
                break;
            case AttributeEvent.MISSION_UPDATED:
               // alertUser("MISSION_UPDATED");
                //setadd("MISSION_UPDATED");
                break;

            case AttributeEvent.STATE_ARMING:
                updateArmButton();
                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                updateVehicleMode();
                break;



            case AttributeEvent.GPS_POSITION:
                updatedronePosition();
                break;

            case AttributeEvent.ATTITUDE_UPDATED:
                updateAttribute();
                break;


            case AttributeEvent.ALTITUDE_UPDATED:
                updateAltitude();

                break;

            case AttributeEvent.HOME_UPDATED:

                break;

            case AttributeEvent.BATTERY_UPDATED:
                updatebattery();
                break;

            case AttributeEvent.SPEED_UPDATED:
                updateSpeed();
                break;

            default:

                break;
        }
    }






    public void onArmButtonTap(View view) {

        Log.d("aabb","Aaazzx");

        State vehicleState = this.drone.getAttribute(AttributeType.STATE);

        if (vehicleState.isFlying()) {

            VehicleApi.getApi(this.drone).setVehicleMode(VehicleMode.COPTER_LAND, new SimpleCommandListener() {
                @Override
                public void onError(int executionError) {
                    alertUser("Unable to land the vehicle.");
                    setadd("Unable to land the vehicle.");
                }

                @Override
                public void onTimeout() {
                    alertUser("Unable to land the vehicle.");
                    setadd("Unable to land the vehicle.");
                }
            });
        } else if (vehicleState.isArmed()) {

            ControlApi.getApi(this.drone).takeoff(takeoffalt, new AbstractCommandListener() {

                @Override
                public void onSuccess() {
                    alertUser("Taking off...");
                    setadd("Taking off...");
                }

                @Override
                public void onError(int i) {
                    setadd("Unable to take off.");
                    alertUser("Unable to take off.");
                }

                @Override
                public void onTimeout() {
                    setadd("Unable to take off.");
                    alertUser("Unable to take off.");
                }
            });
        } else if (!vehicleState.isConnected()) {
            // Connect
            alertUser("Connect to a drone first");
            setadd("Connect to a drone first");
        } else {
            // Connected but not Armed

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ARM");
            builder.setMessage("진행하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                            VehicleApi.getApi(drone).arm(true, false, new SimpleCommandListener() {
                                @Override
                                public void onError(int executionError)
                                {
                                    alertUser("Unable to arm vehicle.");
                                    setadd("Unable to arm vehicle.");
                                }

                                @Override
                                public void onTimeout()
                                {
                                    alertUser("Arming operation timed out.");
                                    setadd("Arming operation timed out.");
                                }
                            });


                        }
                    });
            builder.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();


                        }
                    });
            builder.show();


            //

        }
    }


    protected void updateArmButton() {
        State vehicleState = this.drone.getAttribute(AttributeType.STATE);
        Button armButton = (Button) findViewById(R.id.ARM);


        if (vehicleState.isFlying()) {
            // Land
            armButton.setText("LAND");

        } else if (vehicleState.isArmed()) {
            // Take off
            armButton.setText("TAKE OFF");
        } else if (vehicleState.isConnected()) {
            // Connected but not Armed
            armButton.setText("ARM");
        }

    }


    void updatebattery(){

        Battery dronebattery=  this.drone.getAttribute(AttributeType.BATTERY);
        //volt.setText(dronebattery.getBatteryVoltage()+"V");
        volt.setText(String.format("%3.1f", dronebattery.getBatteryVoltage()) + "V");
    }

    protected void updateSpeed() {

        Speed droneSpeed = this.drone.getAttribute(AttributeType.SPEED);
        speed.setText(String.format("%3.1f", droneSpeed.getGroundSpeed()) + "m/s");
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
                setadd("Connection Failed:" + msg);
                break;
        }   }

    @Override
    public void onTowerConnected() {
        alertUser("DroneKit-Android Connected");
        setadd("DroneKit-Android Connected");
        this.controlTower.registerDrone(this.drone, this.handler);
        this.drone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {
        alertUser("DroneKit-Android Interrupted");
        setadd("DroneKit-Android Interrupted");
    }

    protected void alertUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 체크박스를 클릭해서 상태가 바꾸었을 경우 호출되는 콜백 메서드


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



    void areaexec(LatLng latLng3){ //면적모드 실행
        if(!onmap){ // A 포인트 찍기 전
            if(areamode==null) { // 면적모드 객체 생성 X
                areamode = new areamode(polgonarr, markerarr, polyline, polygon, naverMapall);
                areamode.Aset(latLng3); // A 포인트 찍기
            }else{ // 면적모드 객체 생성 O
                areamode.Aset(latLng3);
            }
            onmap = true;
        }else{ // B 포인트 찍기
            String alldistance = editText.getText()+"";
            String distance = editText2.getText()+"";
            areamode.Bset(latLng3,alldistance,distance);
        }
    }

    void intervalexec( LatLng latLng3, LatLong latLng33){ //간격모드 실행
        if (intervalmode == null) {  // 간격모드 객체 생성 X
            intervalmode = new intervalmode(polinearr, polgonarr, markerarr, polyline, polygon, arr2, markerarrmin, naverMapall);
            intervalmode.three_down_set(latLng3, latLng33); // 마커찍기
        } else { // 간격모드 객체 생성 O
            intervalmode.three_down_set(latLng3, latLng33); // 마커찍기
        }
        if(markercount >= 3) { // 마커가 3개 이상일 경우
            String distance = editText3.getText() + "";
            String angle = editText4.getText() + "";
            intervalmode.three_up_set(distance, angle);
        }
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) { // 맵 클릭 이벤트
        PointF screenPt = naverMapall.getProjection().toScreenLocation(latLng);
        LatLng latLng3 = naverMapall.getProjection().fromScreenLocation(screenPt);
        LatLong latLng33 = new LatLong(latLng3.latitude,latLng3.longitude);

        if(dronemode == 2) { // 면적모드
            areaexec(latLng3);
        }
        if(dronemode == 3) { //간격모드
            intervalexec(latLng3,latLng33);
         }
    }


    void guideselect(){ // 가이드 모드 예, 아니오 선택

        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();

                        setadd(guidemode.guidechange()); // 가이드 모드로 전환
                        setadd(guidemode.guideexec(latt2,latl2)); // 가이드 모드 실행


                    }
                });

        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
                    }
                });

        builder.show();

    }


    @Override
    public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

        PointF screenPt = naverMapall.getProjection().toScreenLocation(latLng);
          latt2 = naverMapall.getProjection().fromScreenLocation(screenPt);
         latl2 = new LatLong(latt2.latitude,latt2.longitude);

         if(dronemode == 1) {    //일반모드일떄
             if(guidemode == null){
                 guidemode = new guidemode(ma,naverMapall,drone);
                 builder = new AlertDialog.Builder(this);
                 builder.setTitle("가이드 모드");
                 builder.setMessage("진행하시겠습니까?");
             }

             State vehicleState = this.drone.getAttribute(AttributeType.STATE);
             VehicleMode vehicleMode = vehicleState.getVehicleMode();
             String temp = vehicleMode + "";

             if (temp.equals("Guided")) { // 가이드모드

                 setadd(guidemode.guideexec(latt2,latl2)); // 가이드 모드 실행

             } else { //가이드모드 X

                 guideselect(); // 예 아니오 선택

             }

         }

    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.button:
            //    if(!check) {
                    Gps droneGps = this.drone.getAttribute(AttributeType.GPS);


                    LatLong vehiclePosition = droneGps.getPosition();

                    if (droneGps.isValid()) {

                        LatLng lat = new LatLng( vehiclePosition.getLatitude(),vehiclePosition.getLongitude());
                        latlngarr.add(lat);
                        polyline.setCoords(latlngarr);
                        polyline.setMap(naverMapall);


                        alertUser("Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());
                        Log.d("aabb","Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());



                        premarker.setPosition(lat);
                        premarker.setMap(naverMapall);

                        alertUser("Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());

                    }else{
                        alertUser("SatellitesCount : "+droneGps.getSatellitesCount());
                    }


                break;

            case R.id.layer:
                if(checkboxlinearLayout.getVisibility() == View.VISIBLE){
                    checkboxlinearLayout.setVisibility(View.GONE);
                }else{
                    checkboxlinearLayout.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.btnmapgps:

                if(mapgps){
                    mapgps = false;
                    btnmapgps.setText("맵 활성화");
                    alertUser("맵 활성화");
                }else {
                    mapgps = true;
                    alertUser("맵 비활성화");
                    btnmapgps.setText("맵 비활성화");
                }

                break;

            case R.id.clear:
                latlngarr.clear();
                break;

            case R.id.button7:
                uimanage.mapselect();
                break;

            case R.id.button11: // 위성지도
                uimanage.satellite();
                break;

            case R.id.button12: // 지형도
                uimanage.terrain();
                break;

            case R.id.button13: // 일반지도
                uimanage.basic();
                break;

            case R.id.button2: //이륙고도 up
                takeoffalt = takeoffalt + 1;
                textView2.setText("이륙고도 : "+takeoffalt + " m");
                break;

            case R.id.button3:  //이륙고도 down
                takeoffalt = takeoffalt - 1;
                textView2.setText("이륙고도 : "+takeoffalt + " m");
                break;

            case R.id.button14:  // 일반모드
                uimanage.normal();
                break;

            case R.id.button15: // 면적모드
                uimanage.area();
                break;

            case R.id.button16: // 간격모드
                uimanage.interval();
                break;
            case R.id.clear2:  // 면적 간격 clear
                onmap = false;
                arr2.clear();

                mission = this.drone.getAttribute(AttributeType.MISSION);
                mission.clear();

                polygon.setMap(null);
                polyline.setMap(null);
                polgonarr.clear();
                polinearr.clear();

                markercount = 0;

                for(int i = 0 ; i < markerarr.size(); i++){
                    Marker a = (Marker) markerarr.get(i);
                    a.setMap(null);

                }
                for(int i = 0 ; i < markerarrmin.size(); i++){
                    Marker a = (Marker) markerarrmin.get(i);
                    a.setMap(null);

                }
                break;

            case R.id.button8: // 미션 전달, 미션 수행, 미션 중지

                String missionstate = button8.getText()+"";
                Log.d("qqwwee",missionstate+"");
                if(missionstate.equals("미션 전달")){

                mission = this.drone.getAttribute(AttributeType.MISSION);
                mission.clear();

                String stalt = alt.getText()+"";
                String alttarr[] = stalt.split(" ");

                    double altt = Double.parseDouble(alttarr[0]);


                    if(0 >= altt || altt <= 1){
                        altt = takeoffalt;
                    }

                    alertUser(altt+"");

                for (int i = 0; i < arr2.size(); i++) {
                    Waypoint waypoint2 = new Waypoint();
                    LatLng lng = (LatLng)arr2.get(i);
                    LatLong lat = new LatLong(lng.latitude,lng.longitude);

                    waypoint2.setCoordinate(new LatLongAlt(lat, altt));
                    waypoint2.setDelay(1);
                    mission.addMissionItem(waypoint2);
                }
                    MissionApi.getApi(this.drone).setMission(mission, true);

                }else if(missionstate.equals("미션 수행")) {

                    VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_AUTO, new SimpleCommandListener() {
                        @Override
                        public void onError(int executionError) {
                            alertUser("Unable to COPTER_AUTO");
                            setadd("Unable to COPTER_AUTO");
                        }

                        @Override
                        public void onTimeout() {
                            alertUser("Unable to COPTER_AUTO");
                            setadd("Unable to COPTER_AUTO");
                        }
                    });


                    button8.setText("미션 중지");
                }else if(missionstate.equals("미션 재시작")) {

                    VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_AUTO, new SimpleCommandListener() {
                        @Override
                        public void onError(int executionError) {
                            alertUser("Unable to COPTER_AUTO");
                            setadd("Unable to COPTER_AUTO");
                        }

                        @Override
                        public void onTimeout() {
                            alertUser("Unable to COPTER_AUTO");
                            setadd("Unable to COPTER_AUTO");
                        }
                    });

                    button8.setText("미션 중지");

                }else{ // 미션 중지

                    VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_LOITER, new SimpleCommandListener() {
                        @Override
                        public void onError(int executionError) {
                            alertUser("Unable to LOITER");
                            setadd("Unable to LOITER");
                        }

                        @Override
                        public void onTimeout() {
                            alertUser("Unable to LOITER");
                            setadd("Unable to LOITER");
                        }
                    });
                    setadd("mission stop");
                    //미션 중지
                    button8.setText("미션 재시작");
                }

                break;

            case R.id.home:
                if(guidemode == null){
                    guidemode = new guidemode(ma,naverMapall,drone);
                    builder = new AlertDialog.Builder(this);
                    builder.setTitle("가이드 모드");
                    builder.setMessage("진행하시겠습니까?");
                }

                LatLng templat = naverMapall.getLocationOverlay().getPosition();
                LatLong lata = new LatLong(templat.latitude,templat.longitude);

                setadd(guidemode.guidechange());
                setadd(guidemode.Homeexec(lata));

                break;

            case R.id.location:
                break;


        } // switch

    }   // onclick

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    void objectsetting(){ // 필요 객체 생성
        polygon = new PolygonOverlay();
        premarker = new Marker();
        polyline = new PolylineOverlay();
        polyline2= new PolylineOverlay();
    }

    void mapevent(){ // 맵 이벤트 셋팅
        naverMapall.setLocationSource(locationSource);
        naverMapall.setOnMapLongClickListener(this);
        naverMapall.setOnMapClickListener(this);
        naverMapall.setLocationTrackingMode(LocationTrackingMode.NoFollow);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {


        objectsetting(); // 필요 객체 생성

        uimanage = new uimanage(button11,button12,button13,button14,button15,button16,button7,button8,editText,editText2,  editText3,editText4, mission,  naverMapall, linear3,onmap,context,dronemode,drone);

        ms = new mapsetting(polyline2, polyline, polygon, locationOverlay, premarker, naverMap);
        ms.settting(); // 맵세팅

        naverMapall = naverMap;
        mapevent(); // 맵 이벤트 추가

        naverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() { // 마커 미세조정
                String distance = editText3.getText() + "";
                String angle = editText4.getText() + "";
                ms.mapcamera(dronemode,markerarrmin,arr2,polgonarr, markerarr, polinearr,distance,angle);

            }
        });
    }

}