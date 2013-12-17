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
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	public void addWXQQPlatform(UMSocialService mController, Activity activity,
			String content,  String title, String targetUrl,Bitmap bmp) {

		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wxaae99b9ec94fc596";
		// 微信图文分享必须设置一个url
		String contentUrl = "http://www.anzhi.com/soft_1225757.html";
		// 添加微信平台
		mController.getConfig().supportWXPlatform(activity, appId, targetUrl);

		// 添加QQ支持, 并且设置QQ分享内容的target url
		mController.getConfig().supportQQPlatform(activity, false, targetUrl);

		
		UMImage mUMImgBitmap = new UMImage(activity,bmp);
//
		mUMImgBitmap.setTitle(title);
//		// target url 必须填写
		mUMImgBitmap.setTargetUrl(targetUrl);

		// 设置分享文字内容、图片内容
		mController.setShareContent(content);
		mController.setShareMedia(mUMImgBitmap);
		// 支持微信朋友圈
		mController.getConfig().supportWXCirclePlatform(activity, appId,
				targetUrl);

		mController.openShare(activity, false);

	}
}
