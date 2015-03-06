package kr.hyosang.drivediary.android.service;

import kr.hyosang.drivediary.android.BaseUtil;
import kr.hyosang.drivediary.android.Definition;
import kr.hyosang.drivediary.android.MainActivity;
import kr.hyosang.drivediary.android.R;
import kr.hyosang.drivediary.android.SettingActivity;
import kr.hyosang.drivediary.android.database.DbHelper;
import kr.hyosang.drivediary.android.network.UploadThread;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class GpsService extends Service implements BaseUtil {
    
    @Deprecated
	private LocationClient mLocationClient;
    private LocationManager mLocationManager;
	private Messenger mViewListener;
	private boolean mIsLogging = false;
	private DbHelper mDb = null;
	
	private long mTrackTimestamp = 0;
	private int mTrackSeq = 0;
	private long mTrackTimeKey = 0;
	private boolean bZeroLogged = false;
	
	private int mUploadTick = 0;
	private long mLastLoggedTime = 0;
	private Location mLastLocation = null;
	
	NotificationManager mNotiManager;
	
	private int mStopTickTimer = 30;	//30min
	
	private enum NotificationType {
	    SERVICE_STARTED,
	    GPS_SEARCHING,
	    GPS_RECEIVED,
	    DATA_UPLOADING
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mService.asBinder();
	}

	@Override
	public String getTag() {
		return "GpsService";
	}

	@Override
	public void log(String log) {
		Log.d(getTag(), log);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		log("onCreate");
		
		SettingActivity.loadPreferences(this);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
		registerReceiver(mTickListener, filter);
		
		mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		updateNotification(NotificationType.SERVICE_STARTED);
		
		log("Service started");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		log("onStartCommand");
		
		if(intent != null && intent.getExtras() != null) {
			Object obj = intent.getExtras().get(Definition.EXTRA_MESSENGER);
			if(obj != null && obj instanceof Messenger) {
				mViewListener = (Messenger)obj;
				log("Listener Registered");
			}
		}
		
		//초기화가 되어있는 경우 재 생성 하지 않음.
		if(mLocationManager == null) {
		    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    
		    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		            (long) SettingActivity.sIntervalTime,
		            (float) SettingActivity.sIntervalDist,
		            mLocationListener2);
		    
		    updateNotification(NotificationType.GPS_SEARCHING);
		}
		
		if(mDb == null) {
			mDb = new DbHelper(this);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mTickListener);
		
		if(mLocationManager != null) {
		    mLocationManager.removeUpdates(mLocationListener2);
		}
		
		mNotiManager.cancelAll();
		
		log("onDestroy");
	}
	
	public void startLog() {
		log("start log..");
		
		mTrackTimestamp = mTrackTimeKey = System.currentTimeMillis();
		mTrackSeq = (int)mDb.insertNewTrack("New Track", mTrackTimestamp);
		
		bZeroLogged = false;
		
		setLogging(true);
	}
	
	public void stopLog(boolean bUpload) {
		setLogging(false);
		
		if(bUpload) {
			requestUpload();
		}
	}
	
	public void requestUpload() {
	    //updateNotification(NotificationType.DATA_UPLOADING);
		(new UploadThread(this)).setLooper(Looper.getMainLooper()).start();
	}
	
	private synchronized void insertLog(Location loc) {
		mDb.insertLocation(mTrackSeq, mTrackTimeKey, loc);
		
		mLastLoggedTime = System.currentTimeMillis();
	}
	
	private synchronized void setLogging(boolean bLog) {
		mIsLogging = bLog;
		
		sendMessage(Definition.Event.LOG_STATE_CHANGED, null);
		
	}
	
	private long getRecordCount(long tkey) {
		return mDb.getRecordCount(tkey);
	}
	
	private void sendMessage(int arg1, Object obj) {
		if(mViewListener != null) {
			try {
				Message msg = Message.obtain();
				msg.arg1 = arg1;
				msg.obj = obj;
				
				mViewListener.send(msg);
			}catch(RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private BroadcastReceiver mTickListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mUploadTick++;
			mStopTickTimer--;
			
			if(mStopTickTimer < 0) {
				//기록 중지 및 업로드
				stopLog(true);
				sleep();
			}else if(mUploadTick > SettingActivity.sIntervalUpload) {
				//업로드 처리
				mUploadTick = mUploadTick % SettingActivity.sIntervalUpload;
				
				requestUpload();
			}
		}
	};
	
	private ConnectionCallbacks mLocationCallback = new ConnectionCallbacks() {

		@Override
		public void onConnected(Bundle connectionHint) {
			log("LocationService Connected");
			
			updateNotification(NotificationType.GPS_RECEIVED);
			
			//최종 위치 send.
			Location lastLoc = mLocationClient.getLastLocation();
			sendMessage(Definition.Event.LAST_LOCATION, lastLoc);
			
			LocationRequest locReq = LocationRequest.create();
			locReq.setFastestInterval(1000);
			locReq.setInterval(1000);
			locReq.setSmallestDisplacement(SettingActivity.sIntervalDist);
			locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationClient.requestLocationUpdates(locReq, mLocationListener);
		}

		@Override
		public void onDisconnected() {
			log("LocationService Disconnected");
		}
	};
	
	private void sleep() {
	    //앱 종료함
	    sendMessage(Definition.Event.TERMINATE_APP, null);
	}
	
	private void updateNotification(NotificationType type) {
	    updateNotification(type, null);
	}
	
	private void updateNotification(NotificationType type, String customContent) {
	    int icon = R.drawable.ic_launcher;
	    String title = "DriveDiary Service";
	    String content = "...";
	    
	    switch(type) {
	    case SERVICE_STARTED:
	        icon = R.drawable.ic_launcher;
	        content = "Service Started";
	        break;
	        
	    case GPS_SEARCHING:
	        icon = android.R.drawable.ic_menu_zoom;
	        content = "Searching GPS...";
	        break;
	        
	    case GPS_RECEIVED:
	        icon = android.R.drawable.ic_dialog_map;
	        content = "Tracking GPS...";
	        break;
	        
	    case DATA_UPLOADING:
	        icon = android.R.drawable.ic_menu_upload;
	        content = "Uploading data...";
	        break;
	    }
	    
	    if(customContent != null) {
	        content = customContent;
	    }
	        
	    PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
	     
	    Notification noti = (new Notification.Builder(this))
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .getNotification();
        mNotiManager.notify(1, noti);
	}
	
	private android.location.LocationListener mLocationListener2 = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            float speedKm = location.getSpeed() * 3.6f;
            
            if(mIsLogging) {
                if(location.getAccuracy() < SettingActivity.sValidAccuracy) {
                    if(location.hasBearing()) {
                        //방향정보 갖고있나?
                        if(location.hasSpeed()) {
                            //속도정보 있나?
                            if((System.currentTimeMillis() - mLastLoggedTime) > (SettingActivity.sIntervalTime * 1000)) {
                                //로깅 인터벌 시간 경과했나?
                                insertLog(location);
                                bZeroLogged = false;
                            }
                        }
                    }
                
                    if(!bZeroLogged && location.getSpeed() == 0) {
                        //속도0 기록되었나?
                        insertLog(location);
                        bZeroLogged = true;
                    }
                    
                    //gps로그가 튀는 현상이 있음. 5km/h이상일 경우에만 지속시킴.
                    if(speedKm > 5) {
                        mStopTickTimer = 30;
                    }
                }
            }else {
                //기록 개시 속도에 도달했는지 확인
                if(speedKm > SettingActivity.sTriggerSpeed) {
                    //기록 개시
                    startLog();
                    insertLog(location);
                }
            }
            
            mLastLocation = location;
            sendMessage(Definition.Event.LOCATION_UPDATED, location);            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String st = "GPS Status changed : ";
            
            switch(status) {
            case LocationProvider.OUT_OF_SERVICE:
                st += "OUT OF SERVICE";
                break;
                
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                st += "TEMPORARILY_UNAVAILABLE";
                break;
                
            case LocationProvider.AVAILABLE:
                st += "AVAILABLE";
                break;
            }
            
            
            updateNotification(NotificationType.GPS_RECEIVED, st);
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            
        }
	};
	
	
	@Deprecated
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location loc) {
			float speedKm = loc.getSpeed() * 3.6f;
			
			if(mIsLogging) {
				if(loc.getAccuracy() < SettingActivity.sValidAccuracy) {
					if(loc.hasBearing()) {
						//방향정보 갖고있나?
						if(loc.hasSpeed()) {
							//속도정보 있나?
							if((System.currentTimeMillis() - mLastLoggedTime) > (SettingActivity.sIntervalTime * 1000)) {
								//로깅 인터벌 시간 경과했나?
								insertLog(loc);
								bZeroLogged = false;
							}
						}
					}
				
					if(!bZeroLogged && loc.getSpeed() == 0) {
						//속도0 기록되었나?
						insertLog(loc);
						bZeroLogged = true;
					}
					
					//gps로그가 튀는 현상이 있음. 5km/h이상일 경우에만 지속시킴.
					if(speedKm > 5) {
						mStopTickTimer = 30;
					}
				}
			}else {
				//기록 개시 속도에 도달했는지 확인
				if(speedKm > SettingActivity.sTriggerSpeed) {
					//기록 개시
					startLog();
					insertLog(loc);
				}
			}
			
			mLastLocation = loc;
			sendMessage(Definition.Event.LOCATION_UPDATED, loc);
		}
	};
	
	private OnConnectionFailedListener mLocationFailed = new OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			log("LocationService Connection failed : " + arg0.getErrorCode());			
		}
	};
	
	private IGpsService mService = new IGpsService.Stub() {

		@Override
		public void stopLog() throws RemoteException {
			GpsService.this.stopLog(false);			
		}

		@Override
		public void stopLogAndUpload() throws RemoteException {
			GpsService.this.stopLog(true);
		}

		@Override
		public boolean isLogging() throws RemoteException {
			return GpsService.this.mIsLogging;
		}

		@Override
		public void startLog() throws RemoteException {
			GpsService.this.startLog();
		}

		@Override
		public long getRecordCount(long tKey) throws RemoteException {
			return GpsService.this.getRecordCount(tKey);
		}

		@Override
		public int getCurrentTrackSeq() throws RemoteException {
			return GpsService.this.mTrackSeq;
		}
		
		@Override
		public long getCurrentTimeKey() throws RemoteException {
			return GpsService.this.mTrackTimeKey;
		}

		@Override
		public void requestUpload() throws RemoteException {
			GpsService.this.requestUpload();
		}

		@Override
		public Location getLastPosition() throws RemoteException {
			return GpsService.this.mLastLocation;
		}

	};
}
