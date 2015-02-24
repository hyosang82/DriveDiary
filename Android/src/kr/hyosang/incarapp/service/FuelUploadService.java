package kr.hyosang.incarapp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import kr.hyosang.incarapp.Definition;
import kr.hyosang.incarapp.SettingActivity;
import kr.hyosang.incarapp.network.NetworkManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class FuelUploadService extends IntentService {
	private static final String SERVICE_NAME = "FuelUploadService";
	
	public static final String EXTRA_POS = "extra_pos";
	public static final String EXTRA_ODO = "extra_odo";
	public static final String EXTRA_UNIT_PRICE = "extra_unit_price";
	public static final String EXTRA_TOTAL_PRICE = "extra_total_price";
	public static final String EXTRA_LITER = "extra_liter";
	public static final String EXTRA_ISFULL = "extra_isfull";

	public FuelUploadService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String pos = intent.getStringExtra(EXTRA_POS);
		String odo = intent.getStringExtra(EXTRA_ODO);
		String unitPrice = intent.getStringExtra(EXTRA_UNIT_PRICE);
		String totalPrice = intent.getStringExtra(EXTRA_TOTAL_PRICE);
		String liter = intent.getStringExtra(EXTRA_LITER);
		String isfull = intent.getStringExtra(EXTRA_ISFULL);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("inputVehicle", SettingActivity.sVin);
		data.put("inputOdo", odo);
		data.put("inputPrice", unitPrice);
		data.put("inputTotalPrice", totalPrice);
		data.put("inputVolume", liter);
		data.put("inputIsFull", isfull);
		data.put("inputDate", sdf.format(new Date(System.currentTimeMillis())));
		data.put("inputStation", pos);
		
		final Context ctx = this;
		final int res = NetworkManager.getInstance().sendData(Definition.UPLOAD_FUEL, data);
		
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ctx, 
						(res == 200) ? "업로드 완료" : "업로드 실패 : " + res,
								Toast.LENGTH_LONG).show();				
			}
		
		});

	}

}
