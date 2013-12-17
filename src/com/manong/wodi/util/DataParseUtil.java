package com.manong.wodi.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.manong.wodi.MainActivity;
import com.manong.wodi.R;
import com.manong.wodi.entity.CardEntity;
import com.manong.wodi.entity.ProduceEntity;
import com.manong.wodi.logic.MediaCenter;

import android.content.Context;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.util.Xml;

public class DataParseUtil {

	/**
	 * XML Pull½âÎö
	 * 
	 * @param result
	 * @return
	 */
	public static List<ProduceEntity> xmlParse(String result) {
		List<ProduceEntity> entitys = new ArrayList<ProduceEntity>();

		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					result.getBytes()));

			XmlPullParser parser = Xml.newPullParser();
			ProduceEntity currentEntity = null;
			parser.setInput(dis, "UTF-8");
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if (tagName != null && tagName.equals("product")) {
						currentEntity = new ProduceEntity();
						String id = parser.getAttributeValue(0);
						;
						currentEntity.setId(id);
						currentEntity.setName(parser.getAttributeValue(1));
						currentEntity.setPrice(parser.getAttributeValue(2));
						currentEntity.setInfo(parser.getAttributeValue(3));
					}

					if (tagName != null && tagName.equals("name")) {
						String name;
						try {
							name = parser.nextText();
							currentEntity.setName(name);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					if (tagName != null && tagName.equals("price")) {
						currentEntity.setPrice(parser.nextText());
					}

					if (tagName != null && tagName.equals("info")) {
						currentEntity.setInfo(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("product")) {
						entitys.add(currentEntity);
					}
					break;
				}
				nodeType = parser.next();
			}
			dis.close();

		} catch (Exception e) {
		}

		return entitys;
	}
	
	
	public static List<CardEntity> xmlParseCard(String result) {
		List<CardEntity> entitys = new ArrayList<CardEntity>();

		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					result.getBytes()));

			XmlPullParser parser = Xml.newPullParser();
			CardEntity currentEntity = null;
			parser.setInput(dis, "UTF-8");
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if (tagName != null && tagName.equals("card")) {
						currentEntity = new CardEntity();
					}

					if (tagName != null && tagName.equals("pingming")) {
						String pingMing = parser.nextText();
						currentEntity.setPingMing(pingMing);
						Log.i("info", "pingMing = " + pingMing);
					}

					if (tagName != null && tagName.equals("wodi")) {
						currentEntity.setWoDi(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("card")) {
						MediaCenter.getIns().addCardEntity(currentEntity);
					}
					break;
				}
				nodeType = parser.next();
			}
			dis.close();

		} catch (Exception e) {
		}

		return entitys;
	}
	
	public static int xmlParseChongZhi(String result){
		int status = 0;
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					result.getBytes()));

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(dis, "UTF-8");
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if (tagName != null && tagName.equals("result")) {
						status = Integer.parseInt(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					
					break;
				}
				nodeType = parser.next();
			}
			dis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return status;
	}
	
	public static String xmlParsePhone(String result){
		String res = "";
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					result.getBytes()));

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(dis, "UTF-8");
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if (tagName != null && tagName.equals("string")) {
						res = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					
					break;
				}
				nodeType = parser.next();
			}
			dis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	public static void xmlParseCardEntity(Context context) {

		try {
			SQLiteDataBaseHelper helper = new SQLiteDataBaseHelper(
					context);
			helper.deleteCardEntityAll();
			XmlPullParser parser = context.getResources().getXml(
					R.xml.cardentity);
			parser.next();
			CardEntity currentEntity = null;
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if (tagName != null && tagName.equals("card")) {
						currentEntity = new CardEntity();
					}

					if (tagName != null && tagName.equals("pingming")) {
						String pingMing = parser.nextText();
						currentEntity.setPingMing(pingMing);
						Log.i("info", "pingMing = " + pingMing);
					}

					if (tagName != null && tagName.equals("wodi")) {
						currentEntity.setWoDi(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("card")) {
						helper.insertCardEntity(currentEntity);
						MediaCenter.getIns().addCardEntity(currentEntity);
					}
					break;
				}
				nodeType = parser.next();
			}

		} catch (Exception e) {
		}
	}

}
