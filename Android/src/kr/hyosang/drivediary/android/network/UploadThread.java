package kr.hyosang.drivediary.android.network;

import kr.hyosang.drivediary.android.BaseUtil;
import kr.hyosang.drivediary.android.Definition;
import kr.hyosang.drivediary.android.database.DbHelper;
import kr.hyosang.drivediary.android.database.LogDataSet;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class UploadThread extends Thread implements BaseUtil {
	private Context mContext = null;
	private static Object mLock = null;
	private Handler mHandler = null;
	
	public UploadThread(Context context) {
		mContext = context;
		if(mLock == null) {
			mLock = new Object();
		}
	}
	
	public UploadThread setLooper(Looper looper) {
		mHandler = new Handler(looper);
		return this;
	}
	
	@Override
	public void run() {
		synchronized(mLock) {
			DbHelper mDb = new DbHelper(mContext);
			LogDataSet dataset = mDb.getUploadData();
			if(dataset != null && dataset.getCount() > 0) {
				log("Data : " + dataset.logData);
				log("TimeKey : " + dataset.timeKey);
				log("seq : " + dataset.keyList);
				
				showToast(String.format("업로드 시작 : %d건", dataset.getCount()));
				
				NetworkManager net = NetworkManager.getInstance();
				int res = net.uploadLog(dataset);
				
				log("Response : " + res);
				showToast("업로드 : " + res + ", 내부 데이터 삭제");
				
				if(res == 200) {
					//OK
					int cnt = 0;
					if(!Definition.IS_LOCAL) {
						//delete local db
						cnt = mDb.deleteRows(dataset.keyList);
						log("Delete " + cnt + " rows");
					}

					showToast("업로드 완료 : " + cnt + "건");
				}else {
					showToast("업로드 실패 : " + res);
				}
				
			}else {
				log("No data to upload!");
				showToast("업로드할 데이터 없음");
			}
		}
	}
	
	public void showToast(final String msg) {
		if(mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();							
				}
			});
		}
	}

	@Override
	public String getTag() {
		return "UploadThread";
	}

	@Override
	public void log(String log) {
		Log.d(getTag(), log);
	}

}
