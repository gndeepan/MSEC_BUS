package com.gn.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.*;
import android.location.Criteria;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.util.*;

import java.util.HashMap;
import android.widget.LinearLayout;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.widget.TextView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;

import java.util.Objects;
import java.util.TimerTask;
import java.util.Calendar;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.view.View;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainmapActivity extends  AppCompatActivity  { 
	

	private final FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

	private FloatingActionButton fab2;
	private double lat ;
	private double lng  ;

	private HashMap<String, Object> data = new HashMap<>();
	private String lati = "9.2346424d";
	private String lang = "78.7596154d";

	private String timee = "4:30";
	private String speed = "0";

	private HashMap<String, Object> mm = new HashMap<>();
	private String version = "";
	private String update_link = "";
	private String app_version = "";

	private MapView mapview1;
	private GoogleMapController _mapview1_controller;
	private TextView textview6;
	private TextView textview2;
	private TextView textview4;
	private String result="";
	
	private final DatabaseReference map = _firebase.getReference("map");
	private TimerTask time;
	private final Calendar cal = Calendar.getInstance();
	private final DatabaseReference db = _firebase.getReference("db");
	private ChildEventListener _db_child_listener;
	private final Intent it = new Intent();
	private RequestNetwork wifi;
	private RequestNetwork.RequestListener _wifi_request_listener;
	private final Intent error = new Intent();
	private final Intent returnn = new Intent();
	private LocationManager gps;
	private LocationListener _gps_location_listener;
	private GoogleMap mMap;
	private Integer dis;
	private MqttAndroidClient client;
	private String json;
	private String late;
	private String lon;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.mainmap);
		initialize(_savedInstanceState);
		com.google.firebase.FirebaseApp.initializeApp(this);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
		}
		else {
			initializeLogic();
		}




	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {

		FloatingActionButton _fab = (FloatingActionButton) findViewById(R.id._fab);
		fab2 = (FloatingActionButton) findViewById(R.id._fab2);

		mapview1 = (MapView) findViewById(R.id.mapview1);
		mapview1.onCreate(_savedInstanceState);
		
		textview6 = (TextView) findViewById(R.id.textview6);

		textview2 = (TextView) findViewById(R.id.textview2);
		textview4 = (TextView) findViewById(R.id.textview4);
		wifi = new RequestNetwork(this);
		gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);






		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
		boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(statusOfGPS){

		}

		else{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);

		}
		_mapview1_controller = new GoogleMapController(mapview1, new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap _googleMap) {
				mMap = _googleMap;


				try {
					boolean success = _googleMap.setMapStyle(
							MapStyleOptions.loadRawResourceStyle(
							MainmapActivity.this, R.raw.style));
					if (!success) {
						Log.e("MainmapActivity", "Style parsing failed.");
					}
				} catch (Resources.NotFoundException e) {
					Log.e("MainmapActivity", "Can't find style. Error: ", e);
				}
              	_mapview1_controller.setGoogleMap(mMap);
				_mapview1_controller.addMarker("college", 9.2346424d, 78.7596154d);
				_mapview1_controller.setMarkerInfo("college", "Mohammad sathak engineering college", "Our college");
				_mapview1_controller.setMarkerIcon("college", R.drawable.collafe);
			}
		});
		
		_fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_mapview1_controller.moveCamera(Double.parseDouble(lati), Double.parseDouble(lang));
                _mapview1_controller.zoomTo(16);
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
				mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
				LatLng mountainView = new LatLng(Double.parseDouble(lati), Double.parseDouble(lang));
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(mountainView )
						.zoom(18)
						.bearing(180)
						.tilt(55)
						.build();
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				Utilize.showMessage(getApplicationContext(), "Bus distance", "Last Update".concat(" ".concat(timee)));
			}
		});


		ChildEventListener _map_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
				};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				assert _childValue != null;
				lati = Objects.requireNonNull(_childValue.get("lat")).toString();
				lang = Objects.requireNonNull(_childValue.get("lang")).toString();
				timee = Objects.requireNonNull(_childValue.get("currentTime")).toString();
				speed = Objects.requireNonNull(_childValue.get("speed")).toString();
				_mapview1_controller.addMarker("b", Double.parseDouble(lati), Double.parseDouble(lang));
				_mapview1_controller.setMarkerIcon("b", R.drawable.map);
				_mapview1_controller.moveCamera(Double.parseDouble(lati), Double.parseDouble(lang));
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
				mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
				LatLng mountainView = new LatLng(Double.parseDouble(lati), Double.parseDouble(lang));
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(mountainView)      // Sets the center of the map to Mountain View
						.zoom(15)                   // Sets the zoom
						.bearing(90)                // Sets the orientation of the camera to east
						.tilt(55)                   // Sets the tilt of the camera to 30 degrees
						.build();                   // Creates a CameraPosition from the builder
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				_mapview1_controller.addMarker("gps", lat, lng);
				_mapview1_controller.setMarkerIcon("gps", R.drawable.man);
				_mapview1_controller.setMarkerInfo("b", "Last Update", timee);
				textview2.setText(timee);
				textview4.setText(String.valueOf((long) (Double.parseDouble(speed))));
			}

			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
				};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				assert _childValue != null;
				lati = Objects.requireNonNull(_childValue.get("lat")).toString();
				lang = Objects.requireNonNull(_childValue.get("lang")).toString();
				timee = Objects.requireNonNull(_childValue.get("currentTime")).toString();
				speed = Objects.requireNonNull(_childValue.get("speed")).toString();
				_mapview1_controller.setMarkerPosition("b", Double.parseDouble(lati), Double.parseDouble(lang));
				_mapview1_controller.setMarkerIcon("b", R.drawable.map);
				_mapview1_controller.moveCamera(Double.parseDouble(lati), Double.parseDouble(lang));
				_mapview1_controller.zoomTo(20);
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
				// Zoom out to zoom level 10, animating with a duration of 2 seconds.
				mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
				LatLng mountainView = new LatLng(Double.parseDouble(lati), Double.parseDouble(lang));
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(mountainView)      // Sets the center of the map to Mountain View
						.zoom(17)                   // Sets the zoom
						.bearing(100)                // Sets the orientation of the camera to east
						.tilt(60)                   // Sets the tilt of the camera to 30 degrees
						.build();                   // Creates a CameraPosition from the builder
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				_mapview1_controller.setMarkerInfo("b", "Last Update", timee);
				textview2.setText(timee);
				textview4.setText(String.valueOf((long) (Double.parseDouble(speed))));
			}

			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}


		};
		map.addChildEventListener(_map_child_listener);
		
		_db_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(@NonNull DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				assert _childKey != null;
				if (_childKey.equals("version update")) {
					assert _childValue != null;
					version = Objects.requireNonNull(_childValue.get("version")).toString();
					update_link = Objects.requireNonNull(_childValue.get("link")).toString();
					if (version.equals(app_version)) {
					}
					else {
						final AlertDialog sucess = new AlertDialog.Builder(MainmapActivity.this).create();
						LayoutInflater inflater = getLayoutInflater();
						View convertView = (View) inflater.inflate(R.layout.dialog, null);
						sucess.setView(convertView);
						sucess.requestWindowFeature(Window.FEATURE_NO_TITLE);
						sucess.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
						LinearLayout lin1 = (LinearLayout) convertView.findViewById(R.id.linear1);
						TextView txt2 = (TextView) convertView.findViewById(R.id.textview2);
						TextView txt3 = (TextView) convertView.findViewById(R.id.textview3);
						ImageView b_img = (ImageView) convertView.findViewById(R.id.imageview1);
						android.graphics.drawable.GradientDrawable a = new android.graphics.drawable.GradientDrawable();
						a.setColor(Color.parseColor("#000000"));
						a.setCornerRadius(50);
						lin1.setBackground(a);
						b_img.setElevation(5);
						sucess.show();
						txt2.setOnClickListener(new View.OnClickListener(){
								public void onClick(View v){
								sucess.dismiss();
							}});
						txt3.setOnClickListener(new View.OnClickListener(){
								public void onClick(View v){
								it.setAction(Intent.ACTION_VIEW);
								it.setData(Uri.parse(update_link));
								startActivity(it);
							}});
					}
				}
		}

			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

			}

			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}


		};
		db.addChildEventListener(_db_child_listener);
		_wifi_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
			}
		};

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setAltitudeRequired(true);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);
		criteria.setBearingRequired(true);

//API level 9 and up
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);


		





	}



	private void initializeLogic() {
		textview4.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/produc_sans_old.ttf"), Typeface.BOLD);
		textview6.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/produc_sans_old.ttf"), Typeface.NORMAL);
		textview2.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/produc_sans_old.ttf"), Typeface.NORMAL);
		wifi.startRequestNetwork(RequestNetworkController.GET, "https://www.google.com", "A", _wifi_request_listener);
		if (ContextCompat.checkSelfPermission(MainmapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, _gps_location_listener);
		}
		if (ContextCompat.checkSelfPermission(MainmapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			gps.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, _gps_location_listener);
		}
		String urc = "com.gn.map";

		
		android.content.pm.PackageManager pm = getPackageManager(); 
		try { android.content.pm.PackageInfo pInfo = pm.getPackageInfo(urc, android.content.pm.PackageManager.GET_ACTIVITIES);
			app_version = pInfo.versionName;
		} catch (android.content.pm.PackageManager.NameNotFoundException ignored) { }
		db.addChildEventListener(_db_child_listener);






	}


	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapview1.onDestroy();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mapview1.onStart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mapview1.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
		mapview1.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mapview1.onStop();
	}
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	@Deprecated
	public int getLocationX(View _v) {
		int[] _location = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	@Deprecated
	public int getLocationY(View _v) {
		int[] _location = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
}
