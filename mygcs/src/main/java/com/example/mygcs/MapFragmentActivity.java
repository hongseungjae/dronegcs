package com.example.mygcs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
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
import com.o3dr.services.android.lib.drone.mission.item.command.DoJump;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import static com.naver.maps.geometry.GeoConstants.EARTH_RADIUS;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MapFragmentActivity extends FragmentActivity
        implements OnMapReadyCallback, Button.OnClickListener, NaverMap.OnMapLongClickListener, CompoundButton.OnCheckedChangeListener, DroneListener, TowerListener, LinkListener ,NaverMap.OnMapClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    NaverMap naverMapall;
    PolylineOverlay polyline;
    Context context;
    boolean mapgps;
    ArrayList<LatLng> latlngarr = new ArrayList<LatLng>();
    ArrayList<LatLng> polgonarr = new ArrayList<LatLng>();
    ArrayList polinearr = new ArrayList();
    ArrayList difpolinearr = new ArrayList();

    ArrayList bearingarr = new ArrayList();
    ArrayList markerarr = new ArrayList();
    int markercount = 1;

    HashMap map = new HashMap();


    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private CheckBox cb6;
    Marker premarker;

    boolean onmap;

    LatLng lat2;
    LatLong latl2;

    ImageView centermarker;
    TextView volt;
    //Spinner mode;
    TextView alt;
    TextView speed;
    TextView YAW;
    TextView sate;
    LatLng Alat;
    LatLng Blat;
    double ABangle;
    String overid;
    boolean overboo;

    int droneType = Type.TYPE_UNKNOWN;



    Spinner modeSelector;
    Button clear;
    Button layer;
    Button connect;
    Button btnmapgps;
    LinearLayout checkboxlinearLayout;
    LinearLayout linear3;
    Button button11;
    Button button12;
    Button button13;
    Button button7;
    Button button2;
    Button button3;
    TextView textView4;
    int takeoffalt;
    PolygonOverlay polygon;
    Marker ma;
    private Drone drone;
    private ControlTower controlTower;
    private final Handler handler = new Handler();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ArrayList singModels = new ArrayList<>();
    Queue q = new LinkedList();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        //setData();
        setRecyclerView();
        takeoffalt = 3;
        centermarker = (ImageView)findViewById(R.id.centermarker);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        ma = new Marker();
       // final Context context = getApplicationContext();
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



        //context = getApplicationContext();
        mapFragment.getMapAsync(this);

     //  altitudeValueTextView = (TextView)findViewById(R.id.altitudeValueTextView);
        //   distanceValueTextView = (TextView)findViewById(R.id.distanceValueTextView);

        volt = (TextView)findViewById(R.id.volt);
    //    mode = (Spinner)findViewById(R.id.modeSelect);
        alt = (TextView)findViewById(R.id.alt);
        speed = (TextView)findViewById(R.id.speed);
        YAW = (TextView)findViewById(R.id.YAW);
        sate = (TextView)findViewById(R.id.sate);


         button11 = (Button)findViewById(R.id.button11);
         button12 = (Button)findViewById(R.id.button12);
         button13 = (Button)findViewById(R.id.button13);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        textView4 = (TextView)findViewById(R.id.textView4);

         button7 = (Button)findViewById(R.id.button7);
        linear3 = (LinearLayout)findViewById(R.id.linear3);

        polyline = new PolylineOverlay();
        Button button = (Button)findViewById(R.id.button);
         layer = (Button)findViewById(R.id.layer);
        connect = (Button)findViewById(R.id.connect);
        checkboxlinearLayout = (LinearLayout)findViewById(R.id.checkboxlinearLayout);
        btnmapgps = (Button)findViewById(R.id.btnmapgps);
        clear = (Button)findViewById(R.id.clear);


        button.setOnClickListener(this);
        layer.setOnClickListener(this);
        btnmapgps.setOnClickListener(this);
        clear.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        button7.setOnClickListener(this);


        button2.setOnClickListener(this);
        button3.setOnClickListener(this);


      //  mode.setOnClickListener(this);


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

    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(singModels);


        recyclerView.setAdapter(adapter);
    }
    void setData(){

      /*  int size = q.size();
       singModels.clear();
       for(int i = 0; i < size; i++){
           String str = q.poll()+"";
           singModels.add(new RecyclerItem(str));
           q.add(str);
       }



        adapter.notifyDataSetChanged();*/


    }

    void setadd(String temp){
        if(q.size() >= 4){
            q.poll();
            q.add(temp);
        }else{
            q.add(temp);
        }
        //setData();

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

            polyline.setCoords(latlngarr);
            polyline.setMap(naverMapall);



            //alertUser("Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());
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

    protected void updateDistanceFromHome() {
    /*    TextView distanceTextView = (TextView) findViewById(R.id.distanceValueTextView);
        Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();



        double distanceFromHome = 0;

        if (droneGps.isValid()) {
            LatLongAlt vehicle3DPosition = new LatLongAlt(vehiclePosition.getLatitude(), vehiclePosition.getLongitude(), vehicleAltitude);
            Home droneHome = this.drone.getAttribute(AttributeType.HOME);
            distanceFromHome = distanceBetweenPoints(droneHome.getCoordinate(), vehicle3DPosition);
        } else {
            distanceFromHome = 0;
        }

        distanceTextView.setText(String.format("%3.1f", distanceFromHome) + "m");*/
    }

    protected void updateAttribute() {

        Attitude droneAttribute = this.drone.getAttribute(AttributeType.ATTITUDE);
        double y =droneAttribute.getYaw();
        //YAW.setText(String.format("%3.1f", y) + "deg");
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
        //TextView altitudeTextView = (TextView) findViewById(R.id.altitudeValueTextView);
        Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);



        alt.setText(String.format("%3.1f", droneAltitude.getAltitude()) + "m");
       // altitudeTextView.setText(String.format("%3.1f", droneAltitude.getAltitude()) + "m");
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
                setadd("MISSION_SENT");
                break;
            case AttributeEvent.MISSION_UPDATED:
                setadd("MISSION_UPDATED");
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
                updateDistanceFromHome();
                break;

            case AttributeEvent.BATTERY_UPDATED:
                updatebattery();
                break;

            case AttributeEvent.SPEED_UPDATED:
                updateSpeed();
                break;

            default:
                // Log.i("DRONE_EVENT", event); //Uncomment to see events from the drone
                break;
        }
    }






    public void onArmButtonTap(View view) {

        Log.d("aabb","Aaazzx");

        State vehicleState = this.drone.getAttribute(AttributeType.STATE);

        if (vehicleState.isFlying()) {
            // Land
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
            // Take off
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

     /*   if (!this.drone.isConnected()) {
            armButton.setVisibility(View.INVISIBLE);
        } else {
            armButton.setVisibility(View.VISIBLE);
        } */

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

    protected double distanceBetweenPoints(LatLongAlt pointA, LatLongAlt pointB) {
        if (pointA == null || pointB == null) {
            return 0;
        }
        double dx = pointA.getLatitude() - pointB.getLatitude();
        double dy = pointA.getLongitude() - pointB.getLongitude();
        double dz = pointA.getAltitude() - pointB.getAltitude();
        return sqrt(dx * dx + dy * dy + dz * dz);
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

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;


        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


      double bearing2(double lat1, double lon1, double lat2, double lon2){

        double longDiff= lon2-lon1;
        double y = sin(longDiff)* cos(lat2);
        double x = cos(lat1)* sin(lat2)- sin(lat1)* cos(lat2)* cos(longDiff);

        return ( toDegrees(atan2(y, x)) + 360 ) % 360;
    }




    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / PI);
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


    public static LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= EARTH_RADIUS;
        heading = toRadians(heading);
        // http://williams.best.vwh.net/avform.htm#LL
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

    public static LatLng computeOffsetOrigin(LatLng to, double distance, double heading) {
        heading = toRadians(heading);
        distance /= EARTH_RADIUS;
        // http://lists.maptools.org/pipermail/proj/2008-October/003939.html
        double n1 = cos(distance);
        double n2 = sin(distance) * cos(heading);
        double n3 = sin(distance) * sin(heading);
        double n4 = sin(toRadians(to.latitude));
        // There are two solutions for b. b = n2 * n4 +/- sqrt(), one solution results
        // in the latitude outside the [-90, 90] range. We first try one solution and
        // back off to the other if we are outside that range.
        double n12 = n1 * n1;
        double discriminant = n2 * n2 * n12 + n12 * n12 - n12 * n4 * n4;
        if (discriminant < 0) {
            // No real solution which would make sense in LatLng-space.
            return null;
        }
        double b = n2 * n4 + sqrt(discriminant);
        b /= n1 * n1 + n2 * n2;
        double a = (n4 - n2 * b) / n1;
        double fromLatRadians = atan2(a, b);
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            b = n2 * n4 - sqrt(discriminant);
            b /= n1 * n1 + n2 * n2;
            fromLatRadians = atan2(a, b);
        }
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            // No solution which would make sense in LatLng-space.
            return null;
        }
        double fromLngRadians = toRadians(to.longitude) -
                atan2(n3, n1 * cos(fromLatRadians) - n2 * sin(fromLatRadians));
        return new LatLng(toDegrees(fromLatRadians), toDegrees(fromLngRadians));
    }


    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        PointF screenPt = naverMapall.getProjection().toScreenLocation(latLng);
        LatLng latLng3 = naverMapall.getProjection().fromScreenLocation(screenPt);
        Log.d("qqwwee","들어옴");
        LatLng naverlat = latLng3;


        if(!onmap){
            Alat = latLng3;
            Marker ma3 = new Marker();
            ma3.setIconTintColor(Color.RED);
            ma3.setPosition(new LatLng(Alat.latitude,Alat.longitude));
            ma3.setMap(naverMapall);
            ma3.setTag(markercount);

            polinearr.add(Alat);
            ma3.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay o) {
                    centermarker.setVisibility(View.VISIBLE);
                    //Toast.makeText(context, "오버레이 클릭됨", Toast.LENGTH_SHORT).show();
                    Log.d("ddeeff",o.getTag()+ "오버레이 클릭됨");
                    o.setMap(null);
                    overid = o.getTag()+"";
                    overboo = true;
                    return true;
                }
            });
            map.put(Double.parseDouble(markercount+""),Alat);
            markercount++;



        }else {

            polinearr.clear();




            map.put(Double.parseDouble(markercount + ""), naverlat);

            Marker ma2 = new Marker();
            ma2.setTag(markercount);
            ma2.setPosition(naverlat);
            ma2.setMap(naverMapall);
            polinearr.add(naverlat);

            ma2.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay o) {
                    //Toast.makeText(context, o+"오버레이 클릭됨", Toast.LENGTH_SHORT).show();
                    centermarker.setVisibility(View.VISIBLE);
                    Log.d("ddeeff", o.getTag() + "오버레이 클릭됨 두번째");
                    o.setMap(null);
                    overid = o.getTag() + "";
                    overboo = true;
                    return true;
                }
            });


            markercount++;

            TreeMap<Double, LatLng> tm = new TreeMap(map);
            Set keyset = map.keySet();
            Iterator keyiterator = tm.keySet().iterator();


            while (keyiterator.hasNext()) {

                Double d = (Double) keyiterator.next();
                Object v = tm.get(d);
                LatLng lat2 = (LatLng) v;
                if(d==0){
                    Alat = lat2;
                    polinearr.add(Alat);

                }else {
                    polinearr.add(lat2);
                }


                System.out.println("키 : " + d + " ---> 값 : " + lat2);

            }


            polinearr.add(Alat);
            polyline.setCoords(polinearr);
            polyline.setMap(naverMapall);


            polygon.setColor(Color.argb(30, 0, 0, 0));
            polygon.setCoords(polinearr);
            polygon.setMap(naverMapall);


            if(markercount >= 5){   // 일단 마커가 4개 이상 찍힐 경우

                // 마커 1과 4의 각도계산
                // 마커 2와 3의 각도계산
                // 2와 3 사이의 점 하나 찍고
                // 1과 4사이의 점 하나 찍고
                // 둘이 연결




                Object obj1 = map.get(1.0);
                LatLng lat1 = (LatLng) obj1;


                Object obj4 = map.get(4.0);
                LatLng lat4 = (LatLng) obj4;

                double bear1_4 = bearing(lat1.latitude,lat1.longitude,lat4.latitude,lat4.longitude);
                double distance1_4 = distance(lat1.latitude,lat1.longitude,lat4.latitude,lat4.longitude,"meter");


                Object obj2 = map.get(2.0);
                LatLng lat2 = (LatLng) obj2;

                Object obj3 = map.get(3.0);
                LatLng lat3 = (LatLng) obj3;
                double bear2_3 = bearing(lat2.latitude,lat2.longitude,lat3.latitude,lat3.longitude);
                double distance2_3 = distance(lat2.latitude,lat2.longitude,lat3.latitude,lat3.longitude,"meter");

                ArrayList Alatarr = new ArrayList();
                ArrayList Blatarr = new ArrayList();

                Log.d("qwerqwer", distance1_4+"1_4");
                Log.d("qwerqwer", distance2_3+"2_3");

                ArrayList latall = new ArrayList();

                LatLng latLng5 =  computeOffset(lat2,10,bear2_3);
                LatLng latLng6 =  computeOffset(lat2,10,bear2_3);
                latall.add(latLng5);
                latall.add(latLng6);

                for(int i = 10; i < distance2_3; i = i + 10) {

                    LatLng latLng10 =  computeOffset(lat2,i,bear2_3);  // 2,3
                    Alatarr.add(latLng10);

                }

                for(int i = 10; i < distance1_4; i = i + 10) {

                    LatLng latLng10 =  computeOffset(lat1,i,bear1_4);  // 1,4
                    Blatarr.add(latLng10);

                }









                for(int i = 0 ; i < Alatarr.size()-1; i++){

                    if(i % 2 ==0) {
                        latall.add(Alatarr.get(i));
                        latall.add(Alatarr.get(i + 1));
                    }else{
                        latall.add(Blatarr.get(i));
                        latall.add(Blatarr.get(i + 1));
                    }

                }

                PolylineOverlay polyline = new PolylineOverlay();
                polyline.setCoords(latall);
                polyline.setMap(naverMapall);


               // difpolinearr.add(newlat);
            }





        }

        onmap = true;

        /*


            Mission mission = this.drone.getAttribute(AttributeType.MISSION);
            mission.clear();

            Waypoint waypoint2=new Waypoint();

            waypoint2.setCoordinate(new LatLongAlt( 35.945021, 126.682829,5));
            waypoint2.setDelay(1);

            mission.addMissionItem(waypoint2);
            MissionApi.getApi(this.drone).setMission(mission,true);


*/



        }





    @Override
    public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

        PointF screenPt = naverMapall.getProjection().toScreenLocation(latLng);
        LatLng latLng3 = naverMapall.getProjection().fromScreenLocation(screenPt);

      //  LatLng naverlat = latLng3;


        LatLng latLng5 = new LatLng(35.945021, 126.682829);
        double x = latLng3.latitude;
        double y = latLng3.longitude;

        lat2 = new LatLng(x,y);
        latl2 = new LatLong(x,y);
        Toast.makeText(context,x+ "  "+y,Toast.LENGTH_LONG).show();


        Marker ma3 = new Marker();
        ma3.setPosition(new LatLng(x, y));
        ma3.setMap(naverMapall);

        double angle = bearing(latLng5.latitude,latLng5.longitude,x,y);
        double angletemp = angle;
        if(angletemp  < 90){
            angletemp  = angle + 90;
        }else{
            angletemp  = angle - 90;
        }


        Log.d("ccddee",bearing(35.945021, 126.682829, x,y) +"  bearing");
        Log.d("ccddee",angletemp+"  angle");

       // LatLng latLng4 =  computeOffsetOrigin(latLng5,100,90);
        LatLng latLng4 =  computeOffset(latLng5,100,angletemp);
        Marker ma4 = new Marker();
        ma4.setPosition(new LatLng(latLng4.latitude, latLng4.longitude));
        ma4.setMap(naverMapall);

        LatLng latLng6 =  computeOffset(latLng3,100,angletemp);
        Marker ma5 = new Marker();
        ma5.setPosition(new LatLng(latLng6.latitude, latLng6.longitude));
        ma5.setMap(naverMapall);





        State vehicleState = this.drone.getAttribute(AttributeType.STATE);
        VehicleMode vehicleMode = vehicleState.getVehicleMode();



        String temp = vehicleMode+"";

        if(temp.equals("Guided")){ // 가이드모드
            Log.d("aabbccdd",vehicleMode+"");


            ma.setPosition(lat2);
            ma.setMap(naverMapall);

            ControlApi.getApi(drone).goTo(latl2, true,new AbstractCommandListener() {

                @Override
                public void onSuccess() {
                    alertUser("goto success");
                    setadd("goto success");
                }

                @Override
                public void onError(int i) {
                    alertUser("goto error");
                    setadd("goto error");
                }

                    @Override
                    public void onTimeout() {
                    alertUser("goto timeout");
                    setadd("goto timeout");
                }
            });

        }else{ //가이드모드 X


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("가이드 모드");
            builder.setMessage("진행하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();

                            VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_GUIDED, new SimpleCommandListener() {
                                @Override
                                public void onError(int executionError) {
                                    alertUser("Unable to Guided");
                                    setadd("Unable to Guided");
                                }

                                @Override
                                public void onTimeout() {
                                    alertUser("Unable to Guided");
                                    setadd("Unable to Guided");
                                }
                            });


                            ma.setPosition(lat2);
                            ma.setMap(naverMapall);

                            ControlApi.getApi(drone).goTo(latl2, true,new AbstractCommandListener() {


                                @Override
                                public void onSuccess() {
                                    alertUser("goto success");
                                    setadd("goto success");
                                }

                                @Override
                                public void onError(int i) {
                                    alertUser("goto error");
                                    setadd("goto error");
                                }

                                @Override
                                public void onTimeout() {
                                    alertUser("goto timeout");
                                    setadd("goto timeout");
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



                     //   CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(lat, 16);
                   //     naverMapall.moveCamera(cameraUpdate);


                        premarker.setPosition(lat);
                        premarker.setMap(naverMapall);

                        alertUser("Latitude : "+droneGps.getPosition().getLatitude() + ",  Longitude : "+droneGps.getPosition().getLongitude()+",  SatellitesCount : "+droneGps.getSatellitesCount());
                        // alertUser(droneGps.toString());
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
                }else{
                    mapgps =true;
                    alertUser("맵 비활성화");
                    btnmapgps.setText("맵 비활성화");
                }
                break;

            case R.id.clear:

                latlngarr.clear();
             //   polyline.setCoords(latlngarr);
             //   polyline.setMap(naverMapall);

                break;

            case R.id.button7:

                if(linear3.getVisibility() == View.VISIBLE){
                    linear3.setVisibility(View.GONE);
                }else{
                    linear3.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.button11:

                button11.setBackgroundResource(R.color.orange);
                button12.setBackgroundResource(R.color.black);
                button13.setBackgroundResource(R.color.black);
                button7.setText("위성지도");
                naverMapall.setMapType(NaverMap.MapType.Satellite);
                linear3.setVisibility(View.GONE);

                break;

            case R.id.button12:

                button12.setBackgroundResource(R.color.orange);
                button11.setBackgroundResource(R.color.black);
                button13.setBackgroundResource(R.color.black);
                button7.setText("지형도");
                naverMapall.setMapType(NaverMap.MapType.Terrain);
                linear3.setVisibility(View.GONE);


                break;

            case R.id.button13:

                button13.setBackgroundResource(R.color.orange);
                button12.setBackgroundResource(R.color.black);
                button11.setBackgroundResource(R.color.black);
                button7.setText("일반지도");
                naverMapall.setMapType(NaverMap.MapType.Basic);
                linear3.setVisibility(View.GONE);

                break;



            case R.id.button2: //이륙고도 up

                takeoffalt = takeoffalt + 1;
                textView4.setText(takeoffalt + " m");

                break;


            case R.id.button3:  //이륙고도 down
                takeoffalt = takeoffalt - 1;
                textView4.setText(takeoffalt + " m");


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
    //    latlngarr.add(new LatLng(35.945021, 126.682829));
    //    latlngarr.add(new LatLng( 35.967652, 126.736895));
   //     latlngarr.add(new LatLng( 35.970483, 126.954788));
    //    latlngarr.add(new LatLng( 35.844426, 127.129364));

        polygon = new PolygonOverlay();
        polyline = new PolylineOverlay();
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(35.945021, 126.682829), 15);

        premarker = new Marker();
     //   premarker.setAnchor(new PointF(1, 1));

     //   premarker.setWidth(110);
     //   premarker.setHeight(110);

        premarker.setPosition(new LatLng(35.945021, 126.682829));
        // premarker.setIcon(OverlayImage.fromResource(R.drawable.pix4));
     //   premarker.setMap(naverMap);











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






        naverMap.setOnMapLongClickListener(this);
        naverMap.setOnMapClickListener(this);

        naverMapall = naverMap;

        naverMap.moveCamera(cameraUpdate);

        //naverMap.getLocationOverlay().setOnClickListener();


        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {
                CameraPosition position = naverMapall.getCameraPosition();
                //   cameraChange.setText(CameraEventActivity.this.getString(R.string.format_camera_position,
                //          position.target.latitude, position.target.longitude, position.zoom, position.tilt, position.bearing));
                Log.d("aabb",position.target.latitude + " " + position.target.longitude);
            }
        });


        naverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //    Log.d("aabb",position.target.latitude + " " + position.target.longitude);
                CameraPosition position = naverMapall.getCameraPosition();
                Log.d("aabbcc",position.target.latitude + " " + position.target.longitude);
                if(overboo){
                    Log.d("ddeeff","overboo들어옴");
                    polinearr.clear();


                //    Object over =  map.get(Double.parseDouble(overid));
              //      LatLng overlat = (LatLng) over;
               //     Log.d("ddeeff",overlat.latitude + "overlat");

                    map.put(Double.parseDouble(overid),new LatLng(position.target.latitude, position.target.longitude));

                    TreeMap<Double,LatLng> tm = new TreeMap(map);
                    Set keyset = map.keySet();
                    Iterator keyiterator = tm.keySet().iterator();


               //     TreeMap<Double,LatLng> tm2 = new TreeMap(map);
               //     Set keyset2 = map.keySet();
                //    Iterator keyiterator2 = tm2.keySet().iterator();




                    while(keyiterator.hasNext()) {

                        Double d = (Double)keyiterator.next();
                        Object v = tm.get(d);
                        LatLng lat2 = (LatLng) v;
                        if(d==0){
                            Alat = lat2;
                            polinearr.add(Alat);

                        }else {
                            polinearr.add(lat2);
                        }


                        System.out.println("키 : "+d+" ---> 값 : "+lat2);

                    }

                    Marker ma2 = new Marker();
                    ma2.setTag(overid);
                    ma2.setPosition(new LatLng(position.target.latitude, position.target.longitude));
                    ma2.setMap(naverMapall);
                    ma2.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay o) {
                            centermarker.setVisibility(View.VISIBLE);
                            //Toast.makeText(context, o+"오버레이 클릭됨", Toast.LENGTH_SHORT).show();
                            Log.d("ddeeff",o.getTag()+ "오버레이 클릭됨 두번째");
                            o.setMap(null);
                            overid = o.getTag()+"";
                            overboo = true;
                            return true;
                        }
                    });



                    polinearr.add(Alat);
                    polyline.setCoords(polinearr);
                    polyline.setMap(naverMapall);


                    polygon.setColor(Color.argb(30,0,0,0));
                    polygon.setCoords(polinearr);
                    polygon.setMap(naverMapall);

                    overboo = false;

                    centermarker.setVisibility(View.GONE);

                }
                //    cameraIdle.setText(CameraEventActivity.this.getString(R.string.format_camera_position,
                //          position.target.latitude, position.target.longitude, position.zoom, position.tilt, position.bearing));
            }
        });



        //스피너추가

     //   Spinner s1 = (Spinner)findViewById(R.id.modeSelect);

     /*   s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/



    } // onMapReady



}