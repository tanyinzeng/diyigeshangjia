package com.manong.wodi;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.manong.wodi.adapter.TestLvAdapter;
import com.manong.wodi.entity.ProduceEntity;
import com.manong.wodi.util.DataParseUtil;
import com.manong.wodi.util.Md5Encode;
import com.manong.wodi.util.MyHttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class TestActivity extends Activity {
	private ListView lv;
	private TestLvAdapter lvAdapter;
	List<ProduceEntity> entitys = new ArrayList<ProduceEntity>();
	private Button btn2;
	private ProduceEntity currentEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_btn);
		Button btn = (Button) findViewById(R.id.btn);
		lv = (ListView) findViewById(R.id.lv);
		lvAdapter = new TestLvAdapter(TestActivity.this, entitys);
		lv.setAdapter(lvAdapter);
		initHandler();
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("partner", "11583"));
					param.add(new BasicNameValuePair("type", "1"));
					Log.i("info",
							"Md5Encode = "
									+ Md5Encode
											.MD5Encode("partner=11583&type=0&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T"));
					param.add(new BasicNameValuePair(
							"sign",
							Md5Encode
									.MD5Encode("partner=11583&type=1&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T")));
					MyHttpUtil.sendTestRequest(param,
							"http://api.99huafei.com/direct/products.aspx", 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "getId = " + currentEntity.getId());

				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("partner", "11583"));
				param.add(new BasicNameValuePair("out_trade_id", "tyztyl"));
				// account
				param.add(new BasicNameValuePair("account", "18605887965"));
				param.add(new BasicNameValuePair("product_id", currentEntity
						.getId()));
				param.add(new BasicNameValuePair("quantity", "1"));
				param.add(new BasicNameValuePair("notify_url",
						"http://blog.sina.com.cn/u/2800385944"));
				Log.i("info",
						"Md5Encode = "
								+ Md5Encode
										.MD5Encode("partner=11583&out_trade_id=tyztyl&account=18605887965"
												+ "&product_id="
												+ currentEntity.getId()
												+ "&quantity=1&notify_url=http://blog.sina.com.cn/u/2800385944&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T"));
				param.add(new BasicNameValuePair(
						"sign",
						Md5Encode
								.MD5Encode("partner=11583&out_trade_id=tyztyl&account=18605887965"
										+ "&product_id="
										+ currentEntity.getId()
										+ "&quantity=1&notify_url=http://blog.sina.com.cn/u/2800385944&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T")));
				MyHttpUtil.sendTestRequest(param,
						"http://api.99huafei.com/direct/fill.aspx", 1);

			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// lv.setVisibility(View.GONE);
				// btn2.setVisibility(View.VISIBLE);
				// currentEntity = entitys.get(arg2);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (btn2.getVisibility() == View.VISIBLE) {
			btn2.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
		}
	}

	private static Handler mHandler;

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					entitys.clear();
					String result = (String) msg.obj;
					entitys = DataParseUtil.xmlParse(result);
					Log.i("info", "entitys.size() = " + entitys.size());
					lvAdapter = new TestLvAdapter(TestActivity.this, entitys);
					lv.setAdapter(lvAdapter);
					lvAdapter.notifyDataSetChanged();
					// http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=13705721820&userId=

					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("mobileCode",
							"18605887965"));
					param.add(new BasicNameValuePair("userId", ""));
					MyHttpUtil
							.sendTestRequest(
									param,
									"http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo",
									11);

					break;
				case 11:
					String resut = (String) msg.obj;
					Log.i("info", "resut = " + resut);
					String ss = DataParseUtil.xmlParsePhone(resut);
					Log.i("info", "DataParseUtil = "+ss);
					String[] strs = ss.split(" ");
					Log.i("info", "length = "+strs.length+" , strs = "+strs[strs.length-1]);
					String guiShu = strs[strs.length-1];
					List<ProduceEntity> procs = new ArrayList<ProduceEntity>();
					for(int i = 0;i<entitys.size();i++){
						ProduceEntity en = entitys.get(i);
						// && en.getName().contains("1Ԫ")
						if(en.getName().contains(guiShu.substring(0, 4))){
							procs.add(en);
						}
					}
					Log.i("info", "procs.size() = "+procs.size());
					lvAdapter = new TestLvAdapter(TestActivity.this, procs);
					lv.setAdapter(lvAdapter);
					lvAdapter.notifyDataSetChanged();
					break;
				case 1:
					String resu = (String) msg.obj;
					Log.i("info", "resu = " + resu);
					break;
				}
			}
		};
	}

}
