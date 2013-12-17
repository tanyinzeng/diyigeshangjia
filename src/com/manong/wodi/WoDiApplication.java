package com.manong.wodi;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

public class WoDiApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		 JPushInterface.setDebugMode(true);
         JPushInterface.init(this);
	}
}
