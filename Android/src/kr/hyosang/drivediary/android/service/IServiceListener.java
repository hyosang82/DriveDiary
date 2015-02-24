package kr.hyosang.drivediary.android.service;

import android.location.Location;
import android.os.IBinder;

public interface IServiceListener extends IBinder {
	public void onLocationUpdate(Location loc);

}
