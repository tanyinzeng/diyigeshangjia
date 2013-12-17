package com.manong.wodi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.luckyasdf.Dianle;
import com.manong.wodi.adapter.WanJiaAdapter;
import com.manong.wodi.adapter.ZhuChiListAdapter;
import com.manong.wodi.entity.CardEntity;
import com.manong.wodi.entity.WanJiaEntity;
import com.manong.wodi.logic.MediaCenter;
import com.manong.wodi.util.Constants;
import com.manong.wodi.util.DataParseUtil;
import com.manong.wodi.util.LogUtil;
import com.manong.wodi.util.MyHttpUtil;
import com.manong.wodi.util.SQLiteDataBaseHelper;
import com.manong.wodi.util.UMengShareUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnClickListener {
	private Button btnBack, btnSure, btnDeal;
	private TextView btnStart;
	private RelativeLayout titleLayout;
	private EditText etTotalNum;
	private TextView tvWoDi, tvBaiBan;
	private RelativeLayout woDiLayout, baiBanLayout, secondLayout, thirdLayout;
	private ScrollView firstLayout;
	private int totalNum, woDiNum, baiBanNum;
	private GridView wanJiaGv;
	private WanJiaAdapter wanJiaAdapter;
	private List<WanJiaEntity> wanJiaEntitys = new ArrayList<WanJiaEntity>();
	private TextView tvChouQianShu, tvZhuangTai, tvKaiShi, tvKaiShiTitle,
			tvChouQinTitle;
	private int chouQianNum = 1;
	private int openNum = 0;
	private WanJiaEntity currentWanJiaEntity;
	private ImageView ivZhuChi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Dianle.initDianleContext(this, "c9b6a4c24d701748bf6a68fbc3c142b7");
		Dianle.setCustomActivity("com.luckyasdf.DianleOfferActivity");
		Dianle.setCustomService("com.luckyasdf.DianleOfferHelpService");
		initview();
		initData();
		initHandler();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.log("onResume");
		if(MediaCenter.getIns().isPoint()){
			Intent intent = new Intent(MainActivity.this, PointActivity.class);
			startActivity(intent);
			MediaCenter.getIns().setPoint(false);
		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.MSG_STATUS.UPDATE_DATA_SUCCESS:
					disProgress();
					String result = (String) msg.obj;
					List<CardEntity> cards = DataParseUtil.xmlParseCard(result);
					SQLiteDataBaseHelper helper = new SQLiteDataBaseHelper(
							MainActivity.this);
					helper.deleteCardEntityAll();
					for (int i = 0; i < cards.size(); i++) {
						CardEntity entity = cards.get(i);
						helper.insertCardEntity(entity);
					}
					break;
				// ��������
				case DOWNLOAD:
					// ���ý�����λ��
					mProgress.setProgress(progress);
					break;
				case DOWNLOAD_FINISH:
					// ��װ�ļ�
					installApk();
					break;
				case Constants.MSG_STATUS.UPDATE_VERSION_SUCCESS:
					disProgress();
					InputStream ins = (InputStream) msg.obj;
					showNoticeDialog(ins);
					break;
				case Constants.MSG_STATUS.UPDATE_VERSION_NO:
					disProgress();
					Toast.makeText(MainActivity.this, "��ǰ��Ϊ���°汾",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.MSG_STATUS.MSG_SEND_REQUEST:
					showProgress(MainActivity.this);
					break;
				case Constants.MSG_STATUS.MSG_TIME_OUT:
					disProgress();
					Toast.makeText(MainActivity.this, "���ӳ�ʱ",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.MSG_STATUS.NO_NET_WORK:
					disProgress();
					Toast.makeText(MainActivity.this, "û������",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
	}

	private ProgressDialog progressDialog;

	private void showProgress(Context ctx) {
		try {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(ctx);
			}

			progressDialog.setTitle("��ʾ");
			progressDialog.setMessage("���ڷ�������");
			progressDialog.setCancelable(true);
			progressDialog.show();

		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	private void disProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * ��ʾ������¶Ի���
	 */
	private void showNoticeDialog(final InputStream is) {
		// ����Ի���
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getResources().getString(R.string.app_name));
		builder.setMessage("�Ƿ���£�");
		// ����
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// ��ʾ���ضԻ���
				showDownloadDialog(is);
			}
		});
		// �Ժ����
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private ProgressBar mProgress;
	private Dialog mDownloadDialog;

	/**
	 * ��ʾ������ضԻ���
	 */
	public void showDownloadDialog(InputStream is) {
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle(getResources().getString(R.string.app_name));
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// ����ȡ��״̬
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// �����ļ�
		downloadApk(is);
	}

	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* �Ƿ�ȡ������ */
	private boolean cancelUpdate = false;

	/**
	 * ����apk�ļ�
	 */
	private void downloadApk(final InputStream is) {
		// �������߳��������
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// ��ô洢����·��
						String sdpath = Environment
								.getExternalStorageDirectory() + "/";
						mSavePath = sdpath + "download";

						File file = new File(mSavePath);
						// �ж��ļ�Ŀ¼�Ƿ����
						if (!file.exists()) {
							file.mkdir();
						}
						File apkFile = new File(mSavePath, "wodi.apk");
						FileOutputStream fos = new FileOutputStream(apkFile);
						int count = 0;
						// ����
						byte buf[] = new byte[1024];
						// д�뵽�ļ���
						do {
							int numread = is.read(buf);
							count += numread;
							// ���������λ��
							progress = (int) (((float) count / 1000000000) * 100);
							// ���½���
							mHandler.sendEmptyMessage(DOWNLOAD);
							if (numread <= 0) {
								// �������
								mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
								break;
							}
							// д���ļ�
							fos.write(buf, 0, numread);
						} while (!cancelUpdate);// ���ȡ����ֹͣ����.
						fos.close();
						is.close();
					} else {
						Toast.makeText(MainActivity.this, "SD�������ڻ����޶�дȨ�ޣ�",
								Toast.LENGTH_LONG).show();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// ȡ�����ضԻ�����ʾ
				mDownloadDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, "wodi.apk");
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
	}

	private void initview() {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		mController.getConfig().setSsoHandler(new QZoneSsoHandler(this));
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN);
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		btnBack = (Button) titleLayout.findViewById(R.id.title_back);
		btnSure = (Button) titleLayout.findViewById(R.id.title_save);
		btnStart = (TextView) findViewById(R.id.start);
		btnStart.setOnClickListener(this);
		btnSure.setText(R.string.kai_shi);
		btnBack.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		etTotalNum = (EditText) findViewById(R.id.wan_jia_zong_shu);
		tvWoDi = (TextView) findViewById(R.id.wo_di_shu_tv);
		tvBaiBan = (TextView) findViewById(R.id.bai_ban_tv);
		woDiLayout = (RelativeLayout) findViewById(R.id.wo_di_shu);
		baiBanLayout = (RelativeLayout) findViewById(R.id.bai_ban_shu);
		woDiLayout.setOnClickListener(this);
		baiBanLayout.setOnClickListener(this);
		etTotalNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				tvWoDi.setText("0");
				tvBaiBan.setText("0");
			}
		});
		firstLayout = (ScrollView) findViewById(R.id.first_layout);
		secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
		wanJiaGv = (GridView) findViewById(R.id.list_gridView);
		tvChouQianShu = (TextView) findViewById(R.id.you_xi_chou_qian_shu);
		wanJiaAdapter = new WanJiaAdapter(MainActivity.this, wanJiaEntitys);
		wanJiaGv.setAdapter(wanJiaAdapter);
		tvChouQianShu.setText(chouQianNum + "");
		tvZhuangTai = (TextView) findViewById(R.id.you_xi_status_tv);
		tvKaiShi = (TextView) findViewById(R.id.ji_hao_fa_yan);
		tvKaiShiTitle = (TextView) findViewById(R.id.ji_hao_fa_yan_title);
		tvChouQinTitle = (TextView) findViewById(R.id.you_xi_chou_qian_shu_title);
		wanJiaGv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				currentWanJiaEntity = wanJiaEntitys.get(arg2);
				if (isZhuChi == 1) {
					if (arg2 + 1 < wanJiaEntitys.size()) {
						if (chouQianNum <= totalNum
								&& currentWanJiaEntity.getIsOpen() == 0) {
							Dialog dlg = new AlertDialog.Builder(
									MainActivity.this)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setTitle(
											getResources().getString(
													R.string.app_name))
									.setPositiveButton(android.R.string.ok,
											null)
									.setMessage(
											"�������ǣ�"
													+ currentWanJiaEntity
															.getCard())
									.create();
							dlg.show();
							currentWanJiaEntity.setIsOpen(1);
							wanJiaAdapter.notifyDataSetChanged();
							chouQianNum++;
							tvChouQianShu.setText(chouQianNum + "");
							if ((chouQianNum - 1) == totalNum) {
								tvChouQianShu.setVisibility(View.GONE);
								tvZhuangTai.setText(getResources().getString(
										R.string.you_xi_zhong));
								tvKaiShi.setVisibility(View.VISIBLE);
								Random ran = new Random();
								tvKaiShi.setText((ran.nextInt(totalNum) + 1)
										+ "");
								tvKaiShiTitle.setVisibility(View.VISIBLE);
								tvChouQinTitle.setVisibility(View.GONE);
							}

						} else if (chouQianNum > totalNum
								&& currentWanJiaEntity.getIsOpen() == 1) {
							Dialog dlg = new AlertDialog.Builder(
									MainActivity.this)
									// .setIcon(android.R.drawable.ic_dialog_alert)
									.setTitle(
											getResources().getString(
													R.string.app_name))
									.setMessage("ȷ��ɱ����ѡ����")
									.setPositiveButton(
											"ȷ��",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													currentWanJiaEntity
															.setIsOpen(2);
													openNum++;
													if (currentWanJiaEntity
															.isWoDi()) {
														if (openNum + 2 < totalNum) {
															for (int i = 0; i < wanJiaEntitys
																	.size(); i++) {
																WanJiaEntity entity = wanJiaEntitys
																		.get(i);
																if (entity
																		.getIsOpen() == 1) {
																	entity.setIsOpen(2);
																	entity.setSucc(true);
																} else {
																	entity.setSucc(false);
																}
															}
														}

													} else {
														currentWanJiaEntity
																.setSucc(false);
													}
													wanJiaAdapter
															.notifyDataSetChanged();
												}
											})
									.setNegativeButton(
											"ȡ��",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).setCancelable(false).create();

							dlg.show();
						}
					} else {
						Dialog dlg = new AlertDialog.Builder(MainActivity.this)
								// .setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle(
										getResources().getString(
												R.string.app_name))
								.setMessage("ֻ�������˲��ܿ�")
								.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												secondLayout
														.setVisibility(View.GONE);
												thirdLayout
														.setVisibility(View.VISIBLE);
												zhuChiAdapter
														.notifyDataSetChanged();
											}
										})
								.setNegativeButton("ȡ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).setCancelable(false).create();

						dlg.show();
					}
				} else {
					if (chouQianNum <= totalNum
							&& currentWanJiaEntity.getIsOpen() == 0) {
						Dialog dlg = new AlertDialog.Builder(MainActivity.this)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle(
										getResources().getString(
												R.string.app_name))
								.setPositiveButton(android.R.string.ok, null)
								.setMessage(
										"�������ǣ�" + currentWanJiaEntity.getCard())
								.create();
						dlg.show();
						currentWanJiaEntity.setIsOpen(1);
						wanJiaAdapter.notifyDataSetChanged();
						chouQianNum++;
						tvChouQianShu.setText(chouQianNum + "");
						if ((chouQianNum - 1) == totalNum) {
							tvChouQianShu.setVisibility(View.GONE);
							tvZhuangTai.setText(getResources().getString(
									R.string.you_xi_zhong));
							tvKaiShi.setVisibility(View.VISIBLE);
							Random ran = new Random();
							tvKaiShi.setText((ran.nextInt(totalNum) + 1) + "");
							tvKaiShiTitle.setVisibility(View.VISIBLE);
							tvChouQinTitle.setVisibility(View.GONE);
						}

					} else if (chouQianNum > totalNum
							&& currentWanJiaEntity.getIsOpen() == 1) {
						Dialog dlg = new AlertDialog.Builder(MainActivity.this)
								// .setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle(
										getResources().getString(
												R.string.app_name))
								.setMessage("ȷ��ɱ����ѡ����")
								.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												currentWanJiaEntity
														.setIsOpen(2);
												openNum++;
												if (currentWanJiaEntity
														.isWoDi()) {
													if (openNum + 2 < totalNum) {
														for (int i = 0; i < wanJiaEntitys
																.size(); i++) {
															WanJiaEntity entity = wanJiaEntitys
																	.get(i);
															if (entity
																	.getIsOpen() == 1) {
																entity.setIsOpen(2);
																entity.setSucc(true);
															} else {
																entity.setSucc(false);
															}
														}
													}

												} else {
													currentWanJiaEntity
															.setSucc(false);
												}
												wanJiaAdapter
														.notifyDataSetChanged();
											}
										})
								.setNegativeButton("ȡ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).setCancelable(false).create();

						dlg.show();
					}
				}

			}
		});
		btnDeal = (Button) findViewById(R.id.user_deal);
		btnDeal.setOnClickListener(this);
		ivZhuChi = (ImageView) findViewById(R.id.zhu_chi_icon);
		ivZhuChi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isZhuChi == 0) {
					ivZhuChi.setImageResource(R.drawable.select);
					isZhuChi = 1;
				} else {
					ivZhuChi.setImageResource(R.drawable.unselect);
					isZhuChi = 0;
				}
			}
		});
		thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
		zhuChiLv = (ListView) findViewById(R.id.list_wan_jia_name);
		zhuChiAdapter = new ZhuChiListAdapter(this, wanJiaEntitys);
		zhuChiLv.setAdapter(zhuChiAdapter);
	}

	private ListView zhuChiLv;
	private ZhuChiListAdapter zhuChiAdapter;
	private int isZhuChi = 0;

	private void initData() {
		SQLiteDataBaseHelper helper = new SQLiteDataBaseHelper(
				MainActivity.this);
		Cursor cursor = helper.getCardEntity();
		if (cursor != null && cursor.getCount() > 0) {
			LogUtil.log("getCount = " + cursor.getCount());
			while (cursor.moveToNext()) {
				CardEntity entity = new CardEntity();
				entity.setPingMing(cursor.getString(cursor
						.getColumnIndex("pingMing")));
				entity.setWoDi(cursor.getString(cursor.getColumnIndex("woDi")));
				MediaCenter.getIns().addCardEntity(entity);
			}
		} else {
			DataParseUtil.xmlParseCardEntity(this);
		}
		cursor.close();
		helper.close();
		Log.i("info", "size = " + MediaCenter.getIns().getCardEntitys().size());
	}

	private String[] numList = null;

	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	private String title[] = { "�汾����", "���ݸ���", "�������", "���˵��", "׬����" };
	// ����ƽ̨��Controller, �����������SDK�����á������ȴ���
	private UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share.coo", RequestType.SOCIAL);

	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(
				R.layout.dialog, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.text, R.id.tv_text, title));

		popupWindow = new PopupWindow(MainActivity.this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 3);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown��������view��Ϊ���������Ҫ������Ļparent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(findViewById(R.id.main), Gravity.RIGHT
				| Gravity.TOP, x, y);// ��Ҫָ��Gravity��Ĭ�������center.

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				popupWindow.dismiss();
				popupWindow = null;
				switch (arg2) {
				case 0:
					try {
						PackageManager packageManager = getPackageManager();
						// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
						PackageInfo packInfo = packageManager.getPackageInfo(
								getPackageName(), 0);
						int version = packInfo.versionCode;
						LogUtil.log("version = " + version);
						MyHttpUtil.getIns().updateVersion(MainActivity.this,
								version + "");
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case 1:
					MyHttpUtil.getIns().updateCardData(MainActivity.this);
					break;
				case 2:
					UMengShareUtil.getIns().addWXQQPlatform(
							mController,
							MainActivity.this,
							"˭���Ե������Ϸ������������",
							getResources().getString(R.string.app_name),
							"http://www.anzhi.com/soft_1225757.html",
							BitmapFactory.decodeResource(getResources(),
									R.drawable.erweima));
					break;
				case 3:
					Intent intent = new Intent(MainActivity.this,
							AboutActivity.class);
					startActivity(intent);
					break;
				case 4:
					intent = new Intent(MainActivity.this, PointActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_deal:
			int y = btnDeal.getBottom() * 2;
			int x = getWindowManager().getDefaultDisplay().getWidth() / 15;

			showPopupWindow(x, y);
			break;
		case R.id.title_back:
			doBack();
			break;
		case R.id.start:
			if (isEmpty(etTotalNum.getText().toString())) {
				Toast.makeText(MainActivity.this, "���������������", Toast.LENGTH_LONG)
						.show();
				etTotalNum.requestFocus();
				return;
			}
			totalNum = Integer.parseInt(etTotalNum.getText().toString());
			if (isZhuChi == 1) {
				totalNum = totalNum - 1;
			}
			if (totalNum < 4) {
				Toast.makeText(MainActivity.this, "�����������С��4",
						Toast.LENGTH_LONG).show();
				etTotalNum.requestFocus();
				return;
			}
			woDiNum = Integer.parseInt(tvWoDi.getText().toString());
			if (woDiNum == 0) {
				Toast.makeText(MainActivity.this, "��ѡ���Ե���", Toast.LENGTH_LONG)
						.show();
				tvWoDi.requestFocus();
				return;
			}
			btnSure.setText(R.string.chong_xin_kai_shi);
			btnBack.setVisibility(View.VISIBLE);
			btnDeal.setVisibility(View.GONE);
			btnSure.setVisibility(View.VISIBLE);
			tvChouQianShu.setText(chouQianNum + "");
			closeKeyboard();
			firstLayout.setVisibility(View.GONE);
			secondLayout.setVisibility(View.VISIBLE);
			wanJiaEntitys.clear();
			Random ran = new Random();
			int tiMuIndex = ran.nextInt(MediaCenter.getIns().getCardEntitys()
					.size());
			CardEntity cardEntity = MediaCenter.getIns().getCardEntitys()
					.get(tiMuIndex);
			int woDiNum = Integer.parseInt(tvWoDi.getText().toString());
			int baiBanNum = Integer.parseInt(tvBaiBan.getText().toString());
			ArrayList list = new ArrayList();
			Random rand = new Random();
			boolean[] bool = new boolean[totalNum];

			for (int i = 0; i < totalNum; i++) {
				WanJiaEntity entity = new WanJiaEntity();
				entity.setCard(cardEntity.getPingMing());
				entity.setBaiBan(false);
				entity.setIsOpen(0);
				entity.setSucc(false);
				entity.setWoDi(false);
				wanJiaEntitys.add(entity);
			}
			int num = 0;
			for (int i = 0; i < woDiNum; i++) {
				do {
					// �������������ͬ����ѭ��
					num = rand.nextInt(totalNum);
				} while (bool[num]);

				bool[num] = true;
				WanJiaEntity entity = wanJiaEntitys.get(num);
				entity.setCard(cardEntity.getWoDi());
				entity.setWoDi(true);
			}
			for (int i = 0; i < baiBanNum; i++) {
				do {
					// �������������ͬ����ѭ��
					num = rand.nextInt(totalNum);
				} while (bool[num]);

				bool[num] = true;
				WanJiaEntity entity = wanJiaEntitys.get(num);
				entity.setCard("�װ�");
				entity.setBaiBan(true);
			}
			if (isZhuChi == 1) {
				WanJiaEntity entity = new WanJiaEntity();
				entity.setBaiBan(false);
				entity.setIsOpen(0);
				entity.setSucc(false);
				entity.setWoDi(false);
				wanJiaEntitys.add(entity);
			}
			wanJiaAdapter.notifyDataSetChanged();
			break;
		case R.id.title_save:
			chouQianNum = 1;
			openNum = 0;
			tvChouQianShu.setText(chouQianNum + "");
			tvChouQianShu.setVisibility(View.VISIBLE);
			tvKaiShi.setVisibility(View.GONE);
			tvKaiShiTitle.setVisibility(View.GONE);
			tvChouQinTitle.setVisibility(View.VISIBLE);
			wanJiaEntitys.clear();
			ran = new Random();
			tiMuIndex = ran.nextInt(MediaCenter.getIns().getCardEntitys()
					.size());
			cardEntity = MediaCenter.getIns().getCardEntitys().get(tiMuIndex);
			woDiNum = Integer.parseInt(tvWoDi.getText().toString());
			baiBanNum = Integer.parseInt(tvBaiBan.getText().toString());
			list = new ArrayList();
			rand = new Random();
			if (isZhuChi == 1) {
				totalNum = totalNum +1;
			}

			for (int i = 0; i < totalNum; i++) {
				WanJiaEntity entity = new WanJiaEntity();
				entity.setCard(cardEntity.getPingMing());
				entity.setBaiBan(false);
				entity.setIsOpen(0);
				entity.setSucc(false);
				entity.setWoDi(false);
				wanJiaEntitys.add(entity);
			}
			num = 0;
			if (isZhuChi == 1) {
				totalNum = totalNum - 1;
			}
			bool = new boolean[totalNum];
			for (int i = 0; i < woDiNum; i++) {
				do {
					// �������������ͬ����ѭ��
					num = rand.nextInt(totalNum);
				} while (bool[num]);

				bool[num] = true;
				WanJiaEntity entity = wanJiaEntitys.get(num);
				entity.setCard(cardEntity.getWoDi());
				entity.setWoDi(true);
			}
			for (int i = 0; i < baiBanNum; i++) {
				do {
					// �������������ͬ����ѭ��
					num = rand.nextInt(totalNum);
				} while (bool[num]);

				bool[num] = true;
				WanJiaEntity entity = wanJiaEntitys.get(num);
				entity.setCard("�װ�");
				entity.setBaiBan(true);
			}
			wanJiaAdapter.notifyDataSetChanged();
			break;
		case R.id.wo_di_shu:
			tvWoDi.setText("0");
			if (isEmpty(etTotalNum.getText().toString())) {
				Toast.makeText(MainActivity.this, "���������������", Toast.LENGTH_LONG)
						.show();
				etTotalNum.requestFocus();
				return;
			}
			totalNum = Integer.parseInt(etTotalNum.getText().toString());
			if (totalNum < 4) {
				Toast.makeText(MainActivity.this, "�����������С��4",
						Toast.LENGTH_LONG).show();
				etTotalNum.requestFocus();
				return;
			}

			if (totalNum >= 4 && totalNum < 6) {
				numList = new String[1];
				numList[0] = "1";
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("ѡ��");
				builder.setSingleChoiceItems(numList, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								tvWoDi.setText(numList[whichButton]);
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else {
				int size = 1 + (totalNum - 6) / 2;
				numList = new String[size];
				for (int i = 0; i < size; i++) {
					numList[i] = (1 + i) + "";
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("ѡ��");
				builder.setSingleChoiceItems(numList, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								tvWoDi.setText(numList[whichButton]);
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
			break;
		case R.id.bai_ban_shu:
			tvBaiBan.setText("0");
			if (isEmpty(etTotalNum.getText().toString())) {
				Toast.makeText(MainActivity.this, "���������������", Toast.LENGTH_LONG)
						.show();
				etTotalNum.requestFocus();
				return;
			}
			totalNum = Integer.parseInt(etTotalNum.getText().toString());
			if (totalNum < 4) {
				Toast.makeText(MainActivity.this, "�����������С��4",
						Toast.LENGTH_LONG).show();
				etTotalNum.requestFocus();
				return;
			}
			woDiNum = Integer.parseInt(tvWoDi.getText().toString());
			if (woDiNum == 0) {
				Toast.makeText(MainActivity.this, "��ѡ���Ե���", Toast.LENGTH_LONG)
						.show();
				tvWoDi.requestFocus();
				return;
			}
			if (totalNum >= 4 && totalNum < 8) {
				numList = new String[2];
				numList[0] = "0";
				numList[1] = "1";
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("ѡ��");
				builder.setSingleChoiceItems(numList, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								tvBaiBan.setText(numList[whichButton]);
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else if (totalNum < 4) {
				Toast.makeText(MainActivity.this, "�������ðװ�", Toast.LENGTH_LONG)
						.show();
			} else {
				int size = 1 + (totalNum - 6) / 2;
				numList = new String[size + 1];
				numList[0] = "0";
				for (int i = 0; i < size; i++) {
					numList[i + 1] = (1 + i) + "";
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("ѡ��");
				builder.setSingleChoiceItems(numList, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								tvBaiBan.setText(numList[whichButton]);
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
			break;
		}
	}

	public void doBack() {
		if (secondLayout.getVisibility() == View.VISIBLE) {
			secondLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			chouQianNum = 1;
			openNum = 0;
			tvChouQianShu.setVisibility(View.VISIBLE);
			tvKaiShi.setVisibility(View.GONE);
			tvKaiShiTitle.setVisibility(View.GONE);
			tvChouQinTitle.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.INVISIBLE);
			btnDeal.setVisibility(View.VISIBLE);
			btnBack.setVisibility(View.INVISIBLE);
		} else if (thirdLayout.getVisibility() == View.VISIBLE) {
			thirdLayout.setVisibility(View.GONE);
			secondLayout.setVisibility(View.VISIBLE);
		} else {
			finish();
			// AppConnect.getInstance(
			// "065bc3d6c55c3197b0ccf3b37aaf3e84", "mumayi", this).close();
		}
	}

	@Override
	public void onBackPressed() {
		doBack();
	}

	private static Handler mHandler;

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	public void closeKeyboard() {
		try {
			InputMethodManager im = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isEmpty(String str) {
		if (null == str || "".equals(str.toString().trim())) {
			return true;
		}

		return false;
	}

}
