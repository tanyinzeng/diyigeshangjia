package com.manong.wodi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectListing;
import com.baidu.inf.iis.bcs.model.ObjectSummary;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.ListObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.manong.wodi.MainActivity;
import com.manong.wodi.PointActivity;

public class MyHttpUtil {
	private String host = "bcs.duapp.com";
	private String accessKey = "2hFqbKSqT8hitZaIRefCYD49";
	private String secretKey = "BftxmIBbqVOlO7Pi8sHpIko6LOYq7mP7";
	private String bucket = "wodi-data-tyz";

	private static MyHttpUtil ins;

	private MyHttpUtil() {
	}

	public static MyHttpUtil getIns() {
		if (ins == null) {
			ins = new MyHttpUtil();
		}
		return ins;
	}

	public void updateVersion(Context context, final String osVersion) {
		if (!DeviceUtil.isNetworkConnected(context)) {
			MainActivity.sendHandlerMessage(Constants.MSG_STATUS.NO_NET_WORK,
					null);
			return;
		}
		MainActivity.sendHandlerMessage(Constants.MSG_STATUS.MSG_SEND_REQUEST,
				null);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BCSCredentials credentials = new BCSCredentials(accessKey,
							secretKey);
					BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
					// baiduBCS.setDefaultEncoding("GBK");
					baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
					ListObjectRequest listObjectRequest = new ListObjectRequest(
							bucket);
					listObjectRequest.setStart(0);
					listObjectRequest.setLimit(20);
					BaiduBCSResponse<ObjectListing> response = baiduBCS
							.listObject(listObjectRequest);
					for (ObjectSummary os : response.getResult()
							.getObjectSummaries()) {
						String name = os.getName();
						if (name.contains("apk")) {
							String[] str = name.split("#");
							String version = str[0].substring(1);
							LogUtil.log("version = "+version);
							if (osVersion.equals(version)) {
								MainActivity.sendHandlerMessage(
										Constants.MSG_STATUS.UPDATE_VERSION_NO,
										null);
							} else if(Integer.parseInt(osVersion)<Integer.parseInt(version)){
								BaiduBCSResponse<DownloadObject> responsea = baiduBCS
										.getObject(new GetObjectRequest(bucket,
												name));
								InputStream ins = responsea.getResult()
										.getContent();
								MainActivity
										.sendHandlerMessage(
												Constants.MSG_STATUS.UPDATE_VERSION_SUCCESS,
												ins);
							}else{
								MainActivity.sendHandlerMessage(
										Constants.MSG_STATUS.UPDATE_VERSION_NO,
										null);
							}
						}
					}

					// BaiduBCSResponse<DownloadObject> responsea = baiduBCS
					// .getObject(new GetObjectRequest(bucket,
					// "/cardentity.xml"));
					// InputStream ins = responsea.getResult().getContent();
					// BufferedReader br = new BufferedReader(new
					// InputStreamReader(ins),
					// 10 * 1024);
					//
					// StringBuilder builder = new StringBuilder();
					// String line;
					// while ((line = br.readLine()) != null) {
					// System.out.println(line);
					// builder.append(line);
					// }
					// String result = builder.toString();
				} catch (Exception e) {
					System.out.println("getMessage = " + e.getMessage());
				}
			}
		}).start();
	}

	public void updateCardData(Context context) {
		if (!DeviceUtil.isNetworkConnected(context)) {
			MainActivity.sendHandlerMessage(Constants.MSG_STATUS.NO_NET_WORK,
					null);
			return;
		}
		MainActivity.sendHandlerMessage(Constants.MSG_STATUS.MSG_SEND_REQUEST,
				null);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BCSCredentials credentials = new BCSCredentials(accessKey,
							secretKey);
					BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
					// baiduBCS.setDefaultEncoding("GBK");
					baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8

					BaiduBCSResponse<DownloadObject> responsea = baiduBCS
							.getObject(new GetObjectRequest(bucket,
									"/cardentity.xml"));
					InputStream ins = responsea.getResult().getContent();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(ins), 10 * 1024);

					StringBuilder builder = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						builder.append(line);
					}
					String result = builder.toString();
					MainActivity.sendHandlerMessage(
							Constants.MSG_STATUS.UPDATE_DATA_SUCCESS, result);
				} catch (Exception e) {
					System.out.println("getMessage = " + e.getMessage());
				}
			}
		}).start();
	}

	private File createSampleFile(String name, String pwd) {
		try {
			File file = File.createTempFile("java-sdk-", ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("pwd", pwd);
			// 步骤一:先获取原图
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bitmap1 = BitmapFactory.decodeFile(
					Environment.getExternalStorageDirectory() + "/temp.jpg",
					options);

			// 步骤二：获取原图大小
			long len1 = DeviceUtil.getBitmapLen(bitmap1);

			int sampleSize = 0;
			int qua = 80;
			// 步骤三：依据图片大小相应压缩 备注：目前分档： 2.5M--8、60 2M-6、60 1.5M-4、60 1M-3、80
			// 0.5M-2、80（压缩比例 和 图片质量）
			if (len1 > 2500 * 1024) {
				sampleSize = 8;
				qua = 60;
			} else if (len1 > 2000 * 1024) {
				sampleSize = 6;
				qua = 60;
			} else if (len1 > 1500 * 1025) {
				sampleSize = 5;
				qua = 70;
			} else if (len1 > 1000 * 1024) {
				sampleSize = 4;
				qua = 80;
			} else if (len1 > 500 * 1024) {
				sampleSize = 2;
				qua = 80;
			}

			Bitmap bitmap2 = null;
			if (sampleSize > 0) {
				// 释放原图
				DeviceUtil.recycleBitmap(bitmap1);

				// 压缩图片
				options.inSampleSize = sampleSize;
				options.inJustDecodeBounds = false;
				options.inInputShareable = true;
				options.inPurgeable = true;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				bitmap2 = BitmapFactory.decodeFile(
						Environment.getExternalStorageDirectory()
								+ "/image.jpg", options);
			} else {
				bitmap2 = bitmap1;
			}

			// // 旋转图片
			// int degree = PictureEdit.readPictureDegree(Environment
			// .getExternalStorageDirectory() + "/image.jpg");
			// if (degree == 0) { // 未旋转的情况
			//
			// } else {
			// Matrix matrix = new Matrix();
			// matrix.setRotate(degree);
			// bitmap2 = Bitmap.createBitmap(bitmap2, 0, 0,
			// bitmap2.getWidth(), bitmap2.getHeight(), matrix,
			// true);
			// }
			// ByteArrayOutputStream os = new ByteArrayOutputStream();
			// bitmap2.compress(CompressFormat.JPEG, 80, os);// 压缩图片
			// byte[] bytes = os.toByteArray();
			// ByteArrayBody arrayBody = new ByteArrayBody(bytes,
			// System.currentTimeMillis() + ".jpg");
			json.putOpt("bmp", bitmap2);
			writer.write(json + "\n");
			writer.close();

			return file;
		} catch (Exception e) {
			LogUtil.log("tmp file create failed.");
			return null;
		}
	}

	public static void sendTestRequest(
			final List<? extends NameValuePair> list, String uri, final int type) {
		try {
			final String url = uri.replaceAll(" ", "%20");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();
						// 创建post方式请求对象
						HttpPost request = new HttpPost(url);
						if (list != null) {
							// 将数据封装在request中
							request.setEntity(new UrlEncodedFormEntity(list,
									HTTP.UTF_8));
						}
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(request);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());

							PointActivity.sendHandlerMessage(type, result);
						} else {
							Log.i("info", "getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						Log.i("info", "e.getMessage() = " + e.getMessage());
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

}
