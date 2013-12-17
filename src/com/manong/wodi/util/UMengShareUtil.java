package com.manong.wodi.util;

import android.app.Activity;
import android.graphics.Bitmap;

import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

public class UMengShareUtil {
	private static UMengShareUtil ins;

	private UMengShareUtil() {
	}

	public static UMengShareUtil getIns() {
		if (ins == null) {
			ins = new UMengShareUtil();
		}
		return ins;
	}

	/**
	 * @�������� : ���΢��ƽ̨����
	 * @return
	 */
	public void addWXQQPlatform(UMSocialService mController, Activity activity,
			String content,  String title, String targetUrl,Bitmap bmp) {

		// wx967daebe835fbeac������΢�ſ���ƽ̨ע��Ӧ�õ�AppID, ������Ҫ�滻����ע���AppID
		String appId = "wxaae99b9ec94fc596";
		// ΢��ͼ�ķ����������һ��url
		String contentUrl = "http://www.anzhi.com/soft_1225757.html";
		// ���΢��ƽ̨
		mController.getConfig().supportWXPlatform(activity, appId, targetUrl);

		// ���QQ֧��, ��������QQ�������ݵ�target url
		mController.getConfig().supportQQPlatform(activity, false, targetUrl);

		
		UMImage mUMImgBitmap = new UMImage(activity,bmp);
//
		mUMImgBitmap.setTitle(title);
//		// target url ������д
		mUMImgBitmap.setTargetUrl(targetUrl);

		// ���÷����������ݡ�ͼƬ����
		mController.setShareContent(content);
		mController.setShareMedia(mUMImgBitmap);
		// ֧��΢������Ȧ
		mController.getConfig().supportWXCirclePlatform(activity, appId,
				targetUrl);

		mController.openShare(activity, false);

	}
}
