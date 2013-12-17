package com.manong.wodi.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DeviceUtil
{
    public static boolean isNetworkConnected(Context context)
    {
        if (context != null)
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null)
            {
            	LogUtil.log("isAvailable = "+mNetworkInfo.isAvailable());
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    
    /**
     * 获取bitmap 长度
     * @param bitmap
     * @return
     */
    public static long getBitmapLen(Bitmap bitmap)
    {
        if(null == bitmap)
        {
            return 0;
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] imageInByte = stream.toByteArray();

        return imageInByte.length;

    }
    
    /**
     * 释放bitmap
     * @param bitmap
     * @return
     */
    public static void recycleBitmap(Bitmap bitmap)
    {
        if (null != bitmap && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
