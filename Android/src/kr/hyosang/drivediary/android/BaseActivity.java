package kr.hyosang.drivediary.android;

import android.app.Activity;
import android.util.Log;

public abstract class BaseActivity extends Activity implements BaseUtil {
	public void log(String log) {
		Log.d("InCarApp", String.format("[%s] %s", getTag(), log));
	}

}
