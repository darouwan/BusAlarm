package com.example.busalarm;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;


import com.example.busalarm.MapSelectActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class LongPressOverlay extends Overlay implements OnDoubleTapListener,
		OnGestureListener {
	private MapSelectActivity mContext;
	private MapView mMapView;
	private Handler mHandler;
	private MapController mMapCtrl;
	private GestureDetector gestureScanner = new GestureDetector(this);
	private int level = 0;
	
	public LongPressOverlay(MapSelectActivity context, MapView mapView, Handler handler,MapController mapCtrl){
		mContext = context;
		mMapView = mapView;
		mHandler = handler;
		mMapCtrl = mapCtrl;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return gestureScanner.onTouchEvent(event);
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		mContext.locPoint = mMapView.getProjection().fromPixels((int) e.getX(),
				(int) e.getY());
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_LONGPRESS);
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if(++level % 3 == 0){
			mMapCtrl.zoomIn();
			level = 0;
		}
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
