package com.manong.wodi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.luckyasdf.Dianle;
import com.luckyasdf.GetTotalMoneyListener;
import com.luckyasdf.SpendMoneyListener;
import com.manong.wodi.entity.ProduceEntity;
import com.manong.wodi.util.DataParseUtil;
import com.manong.wodi.util.LogUtil;
import com.manong.wodi.util.Md5Encode;
import com.manong.wodi.util.MyHttpUtil;
import com.manong.wodi.util.StringUtil;

public class PointActivity extends Activity implements OnClickListener,
		GetTotalMoneyListener {
	private Button btnGet, btnRefresh, btnSend;
	private TextView tvPoint;
	private EditText etPhone;

	private int pointTotal = 0;
	private int oldTotal = 0;
	private int needPoint = 1200;
	String[] xulieShu = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	// 暂不支持浙江移动5元充值

	private static Handler mHandler;
	private List<ProduceEntity> entitys = new ArrayList<ProduceEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main1);
		findViewById(R.id.title_save).setVisibility(View.VISIBLE);
		findViewById(R.id.user_deal).setVisibility(View.INVISIBLE);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		btnGet = (Button) findViewById(R.id.get_point);
		tvPoint = (TextView) findViewById(R.id.total_point);
		btnGet.setOnClickListener(this);
		btnRefresh = (Button) findViewById(R.id.title_save);
		btnRefresh.setText("刷新");
		findViewById(R.id.title_back).setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnSend = (Button) findViewById(R.id.sure_send);
		btnSend.setOnClickListener(this);
		etPhone = (EditText) findViewById(R.id.et_phone);
		initHandler();
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("partner", "11583"));
		param.add(new BasicNameValuePair("type", "1"));
		param.add(new BasicNameValuePair(
				"sign",
				Md5Encode
						.MD5Encode("partner=11583&type=1&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T")));
		MyHttpUtil.sendTestRequest(param,
				"http://api.99huafei.com/direct/products.aspx", 0);
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
					break;
				case 11:
					String resut = (String) msg.obj;
					Log.i("info", "resut = " + resut);
					String ss = DataParseUtil.xmlParsePhone(resut);
					Log.i("info", "DataParseUtil = " + ss);
					String[] strs = ss.split(" ");
					Log.i("info", "length = " + strs.length + " , strs = "
							+ strs[strs.length - 1]);
					String guiShu = strs[strs.length - 1];
					ProduceEntity procs = new ProduceEntity();
					for (int i = 0; i < entitys.size(); i++) {
						ProduceEntity en = entitys.get(i);
						if (en.getName().contains(guiShu.substring(0, 4))
								&& en.getName().contains("5元")) {
							procs = en;
						}
					}
					if (procs.getName() != null && procs.getName().length() > 0) {

						Dianle.spendMoney(1200, new SpendMoneyListener() {
							@Override
							public void spendMoneySuccess(long arg0) {
								pointTotal = (int) arg0;
								tvPoint.setText(pointTotal + "");
							}

							@Override
							public void spendMoneyFailed(String arg0) {

							}
						});

						List<NameValuePair> param = new ArrayList<NameValuePair>();
						param.add(new BasicNameValuePair("partner", "11583"));
						int num = 0;
						String outTradeId = "";
						Random rand = new Random();
						boolean[] bool = new boolean[xulieShu.length];
						for (int i = 0; i < 6; i++) {
							do {
								// 如果产生的数相同继续循环
								num = rand.nextInt(xulieShu.length);
							} while (bool[num]);

							bool[num] = true;
							outTradeId += xulieShu[num];
						}
						param.add(new BasicNameValuePair("out_trade_id",
								outTradeId));
						// account
						param.add(new BasicNameValuePair("account", etPhone
								.getText().toString()));
						param.add(new BasicNameValuePair("product_id", procs
								.getId()));
						param.add(new BasicNameValuePair("quantity", "1"));
						param.add(new BasicNameValuePair("notify_url",
								"http://blog.sina.com.cn/u/2800385944"));
						param.add(new BasicNameValuePair(
								"sign",
								Md5Encode
										.MD5Encode("partner=11583&out_trade_id="
												+ outTradeId
												+ "&account="
												+ etPhone.getText().toString()
														.trim()
												+ "&product_id="
												+ procs.getId()
												+ "&quantity=1&notify_url=http://blog.sina.com.cn/u/2800385944&mZIHpqVAlhLM0OhM3huIvpavNCm4ZESn6uFFSzYxKR8lYTlZFP20K55jNLCt0u8T")));
						MyHttpUtil.sendTestRequest(param,
								"http://api.99huafei.com/direct/fill.aspx", 1);
					} else {
						Toast.makeText(PointActivity.this,
								"暂不支持此城市5元充值，功能下次开发", Toast.LENGTH_SHORT)
								.show();
					}
					Log.i("info", "procs.size() = " + procs.getName());
					break;
				case 1:
					String chongZhiChengGong = (String) msg.obj;
					int status = DataParseUtil
							.xmlParseChongZhi(chongZhiChengGong);
					if (status == 0) {
						Toast.makeText(PointActivity.this, "充值成功，请注意查收短信",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PointActivity.this,
								"如果积分已扣掉，充值失败，请联系我们", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		Dianle.getTotalMoney(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			LogUtil.log("title_back");
			finish();
			break;
		case R.id.get_point:
			Dianle.showOffers();
			break;
		case R.id.title_save:
			Dianle.getTotalMoney(this);
			break;
		case R.id.sure_send:
			if (StringUtil.isEmpty(etPhone.getText().toString())) {
				Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
				return;
			}
			this.oldTotal = pointTotal;
			int totalPoint = oldTotal;
			if (totalPoint >= needPoint) {
				Dialog dlg = new AlertDialog.Builder(PointActivity.this)
						.setTitle(R.string.app_name)
						.setMessage("请检查手机号是否正确")
						.setPositiveButton("正确",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										checkPhone();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).setCancelable(true).create();

				dlg.show();

			} else {
				Toast.makeText(this, "请多多下载应用喔，等积分够了再来换话费喔", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}
	}

	public void checkPhone() {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("mobileCode", etPhone.getText()
				.toString()));
		param.add(new BasicNameValuePair("userId", ""));
		MyHttpUtil
				.sendTestRequest(
						param,
						"http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo",
						11);
	}

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	@Override
	public void getTotalMoneyFailed(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getTotalMoneySuccessed(String name, long amount) {
		Log.i("info", "name = " + name + " , amount = " + amount);
		this.pointTotal = (int) amount;
		tvPoint.setText("" + amount);
	}

}
