package com.manong.wodi;

import com.manong.wodi.logic.MediaCenter;
import com.manong.wodi.util.LogUtil;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            //send the Registration Id to your server...
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
          //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String title = bundle.getString("cn.jpush.android.ALERT");
            LogUtil.log("接收到推送下来的通知的  title = "+title);
            if(title.contains("积分")){
            	MediaCenter.getIns().setPoint(true);
            }else{
            	MediaCenter.getIns().setPoint(false);
            }
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            
        	//打开自定义的Activity
        	Intent i = new Intent(context, MainActivity.class);
        	i.putExtras(bundle);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
        	
        } else {
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
