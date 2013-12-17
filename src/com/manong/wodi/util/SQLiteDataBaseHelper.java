package com.manong.wodi.util;


import com.manong.wodi.entity.CardEntity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "WoDiDatabase";
	private final static int DATABASE_VERSION = 1;
	//”√ªß
	private final String TABLENAME_CARD = "cardTable";
	private final static String PING_MING = "pingMing";
	public final static String WO_DI = "woDi";
	
	public SQLiteDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sqlCard = "CREATE TABLE  "
				+ TABLENAME_CARD
				+ " (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ "pingMing TEXT NOT NULL , woDi TEXT NOT NULL)";
		db.execSQL(sqlCard);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqlCard = " drop table " + TABLENAME_CARD;
		db.execSQL(sqlCard);
		onCreate(db);
	}
	public long insertCardEntity(CardEntity entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PING_MING, entity.getPingMing());
		values.put(WO_DI, entity.getWoDi());
		long rowId = db.insert(TABLENAME_CARD, null, values);
		LogUtil.log("getPingMing = "+entity.getPingMing()+" , rowId = "+rowId);
		return rowId;
	}
	

	public Cursor getCardEntity() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from " + TABLENAME_CARD, null);
		
		return cursor;
	}
	
	public void deleteCardEntityAll(){
		SQLiteDatabase db = getReadableDatabase();
		LogUtil.log("delete = "+db.delete(TABLENAME_CARD, "", null));
	}
}
