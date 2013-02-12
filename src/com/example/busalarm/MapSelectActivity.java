package com.example.busalarm;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MapSelectActivity extends MapActivity implements LocationListener,
		OnClickListener {
	protected static final String TAG = null;
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private Location currentLocation;
	GeoPoint currentPoint;
	public GeoPoint locPoint;
	public final int MSG_VIEW_LONGPRESS = 10001;
	public final int MSG_VIEW_ADDRESSNAME = 10002;

	private View popView;
	private Drawable myLocationDrawable;
	private Drawable mylongPressDrawable;
	private MyItemizedOverlay myLocationOverlay;// 鎴戠殑浣嶇疆 灞�
	private MyItemizedOverlay mLongPressOverlay; // 闀挎寜鏃堕棿灞�
	private List<Overlay> mapOverlays;
	private OverlayItem overlayitem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_select);

		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		// mapView.setStreetView(true);
		mapController = mapView.getController();

		myLocationDrawable = getResources().getDrawable(R.drawable.point_where);
		mylongPressDrawable = getResources()
				.getDrawable(R.drawable.point_start);
		mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
		initPopView();
		myLocationOverlay = new MyItemizedOverlay(myLocationDrawable, this,
				mapView, popView, mapController);
		mLongPressOverlay = new MyItemizedOverlay(mylongPressDrawable, this,
				mapView, popView, mapController);
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(new LongPressOverlay(this, mapView, mHandler,
				mapController));

		mapController.setZoom(17);
		getLastLocation();
		animateToCurrentLocation();
	}

	private void initPopView() {
		if (null == popView) {
			popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
			mapView.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView.setVisibility(View.GONE);
		}

	}

	private String getLocationAddress(GeoPoint point) {
		String add = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6,
					1);
			Address address = addresses.get(0);
			int maxLine = address.getMaxAddressLineIndex();
			if (maxLine >= 2) {
				add = address.getAddressLine(1) + address.getAddressLine(2);
			} else {
				add = address.getAddressLine(1);
			}
		} catch (IOException e) {
			add = "";
			e.printStackTrace();
		}
		return add;
	}

	Runnable getAddressName = new Runnable() {
		@Override
		public void run() {
			String addressName = "";
			while (true) {
				addressName = getLocationAddress(locPoint);
				//Log.d(TAG, "鑾峰彇鍦板潃鍚嶇О");
				if (!"".equals(addressName)) {
					break;
				}
			}
			Message msg = new Message();
			msg.what = MSG_VIEW_ADDRESSNAME;
			msg.obj = addressName;
			mHandler.sendMessage(msg);
		}
	};
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map_select, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getLastLocation() {
		String provider = getBestProvider();
		currentLocation = locationManager.getLastKnownLocation(provider);
		if (currentLocation != null) {
			setCurrentLocation(currentLocation);
		} else {
			Toast.makeText(this, "Location not yet acquired", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void animateToCurrentLocation() {

		if (currentPoint != null) {
			mapController.animateTo(currentPoint);
		}
	}

	public String getBestProvider() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		return bestProvider;
	}

	public void setCurrentLocation(Location location) {

		int currLatitude = (int) (location.getLatitude() * 1E6);
		int currLongitude = (int) (location.getLongitude() * 1E6);
		currentPoint = new GeoPoint(currLatitude, currLongitude);

		// No use?
		// currentLocation = new Location("");
		// currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
		// currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		setCurrentLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	// @Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		System.out.println("x:" + p.getLatitudeE6());
		System.out.println("y:" + p.getLongitudeE6());
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager
				.requestLocationUpdates(getBestProvider(), 1000, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_VIEW_LONGPRESS:// 澶勭悊闀挎寜鏃堕棿杩斿洖浣嶇疆淇℃伅
			{
				if (null == locPoint)
					return;
				new Thread(getAddressName).start();
				overlayitem = new OverlayItem(locPoint, "鍦板潃鍚嶇О",
						"姝ｅ湪鍦板潃鍔犺浇...");
				if (mLongPressOverlay.size() > 0) {
					mLongPressOverlay.removeOverlay(0);
				}
				popView.setVisibility(View.GONE);
				mLongPressOverlay.addOverlay(overlayitem);
				mLongPressOverlay.setFocus(overlayitem);
				mapOverlays.add(mLongPressOverlay);
				mapController.animateTo(locPoint);
				mapView.invalidate();
			}
				break;
			case MSG_VIEW_ADDRESSNAME:
				// 鑾峰彇鍒板湴鍧�悗鏄剧ず鍦ㄦ场娉′笂
				TextView desc = (TextView) popView
						.findViewById(R.id.map_bubbleText);
				desc.setText((String) msg.obj);
				popView.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}
