package com.example.busalarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ClipData.Item;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;

public class MyItemizedOverlay extends ItemizedOverlay implements
		OnFocusChangeListener, OnClickListener {
	private static final String TAG = "MyItemizedOverlay";
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();

	private GeoPoint point = null;
	private String desc = "";
	private String car_title = "";
	private int layout_x = 0; // 用于设置popview 相对某个位置向x轴偏移
	private int layout_y = -30; // 用于设置popview 相对某个位置向x轴偏移
	
	
	private MapView mMapView;
	private MapController mMapCtrl;
	private View mPopView;
	
	private Drawable itemDrawable;
	private Drawable itemSelectDrawable;
	private OverlayItem selectItem;
	private OverlayItem lastItem;
	private MapSelectActivity mContext;
	
	public void setItemSelectDrawable(Drawable itemSelectDrawable) {
		this.itemSelectDrawable = itemSelectDrawable;
	}
	
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView, View popView, MapController mapCtrl) {
		super(boundCenterBottom(defaultMarker));
		itemDrawable = defaultMarker;
		itemSelectDrawable = defaultMarker;
		mContext = (MapSelectActivity) context;
		setOnFocusChangeListener(this);
		layout_x = itemDrawable.getBounds().centerX();
		layout_y = - itemDrawable.getBounds().height();
		mMapView =  mapView;
		mPopView = popView;
		mMapCtrl = mapCtrl;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}

	@Override
	public void onFocusChanged(ItemizedOverlay arg0overlay, OverlayItem newFocus) {
		// TODO Auto-generated method stub
		Log.d(TAG , "item focus changed!");
		if (null != newFocus) {
			Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() + "; centerX :" + itemDrawable.getBounds().centerX());
			Log.d(TAG , " height : " + itemDrawable.getBounds().height());
			MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
			params.x = this.layout_x;//Y杞村亸绉�
			params.y = this.layout_y;//Y杞村亸绉�
			point = newFocus.getPoint();
			params.point = point;
			mMapCtrl.animateTo(point);
			
			Geocoder gc=new Geocoder(mContext);
			List<Address> listAddress = null;
			try {
				listAddress=gc.getFromLocation(point.getLatitudeE6()/1E6,point.getLongitudeE6()/1E6,1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(listAddress!=null && listAddress.size()==1){
				TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
				Address currentAddress = listAddress.get(0);
				String roomNum = currentAddress.getSubThoroughfare();
				if (roomNum == null){
					roomNum = "";
				}
				title_TextView.setText(roomNum +" "+currentAddress.getThoroughfare());
				TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
				if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
					desc_TextView.setVisibility(View.GONE);
				}else{
					desc = newFocus.getSnippet();
					desc_TextView.setText(desc);
					desc_TextView.setVisibility(View.VISIBLE);
				}
				RelativeLayout button = (RelativeLayout) mPopView.findViewById(R.id.map_bubblebtn);
				button.setOnClickListener(this);
				mMapView.updateViewLayout(mPopView, params);
				mPopView.setVisibility(View.VISIBLE);
			}

			selectItem = newFocus;
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return overlays.size();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return overlays.get(i);
	}
	
	public void addOverlay(OverlayItem item) {
		overlays.add(item);
		populate();
	}
	
	public void removeOverlay(int location) {
		overlays.remove(location);
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return super.onTap(p, mapView);
	}

	@Override
	protected boolean onTap(int index) {
		return super.onTap(index);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

}
