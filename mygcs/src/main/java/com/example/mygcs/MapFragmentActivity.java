package com.example.mygcs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.LinkListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MapFragmentActivity extends FragmentActivity
        implements OnMapReadyCallback, Button.OnClickListener, NaverMap.OnMapLongClickListener, CompoundButton.OnCheckedChangeListener, DroneListener, TowerListener, LinkListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    NaverMap naverMapall;
    PolylineOverlay polyline;
    Context context;
    boolean mapgps;
    ArrayList<LatLng> latlngarr = new ArrayList<LatLng>();

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private CheckBox cb6;
    Marker premarker;


    LatLng lat2;
    LatLong latl2;

    TextView volt;
    //Spinner mode;
    TextView alt;
    TextView speed;
    TextView YAW;
    TextView sate;


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
         button7 = (Button)findViewById(R.id.button7);
        linear3 = (LinearLayout)findViewById(R.id.linear3);


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
            ControlApi.getApi(this.drone).takeoff(3, new AbstractCommandListener() {

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
        LatLng latLng3 = naverMapall.getProjection().fromScreenLocation(screenPt);

        double x = latLng3.latitude;
        double y = latLng3.longitude;

        lat2 = new LatLng(x,y);
        latl2 = new LatLong(x,y);
        Toast.makeText(context,x+ "  "+y,Toast.LENGTH_LONG).show();



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


        polyline = new PolylineOverlay();
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(35.945021, 126.682829), 16);

        premarker = new Marker();

        premarker.setWidth(110);
        premarker.setHeight(110);



        premarker.setPosition(new LatLng(35.945021, 126.682829));
        premarker.setIcon(OverlayImage.fromResource(R.drawable.pix));
        premarker.setMap(naverMap);

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