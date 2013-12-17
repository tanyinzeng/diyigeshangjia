package com.manong.wodi.util;

public class StringUtil {
	public static boolean isEmpty(String str){
		if(null == str || str.length() == 0){
			return true;
		}
		return false;
	}
}
