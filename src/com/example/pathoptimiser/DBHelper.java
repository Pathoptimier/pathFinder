package com.shortroute.pathoptimizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SaveMap";
    SQLiteDatabase sqLiteDB;
 
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String TABLE = "CREATE TABLE OffMap ( " +
                "title TEXT, "+
                "mapcode TEXT )";
        db.execSQL(TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS OffMap");
        this.onCreate(db);
	}
	
	public void open(){
		try {
			sqLiteDB = this.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeDB(){
		try {
			sqLiteDB = this.getReadableDatabase();
			sqLiteDB.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean addOfflinemap(String title,String mapcode) {
//		  sqLiteDB = this.getWritableDatabase();
		  ContentValues values = new ContentValues();
		  values.put("title", title);
		  values.put("mapcode", mapcode);
		  sqLiteDB.insert("OffMap", null, values);
//		  database.close();
		  return true;
		}
	
	public String[][] getOfflinemap() {
		  
//		  sqLiteDB = this.getReadableDatabase();
		  String[][] omap=new String[25][2];
		  int i=0;
		  String selectQuery = "SELECT * FROM OffMap";
		  Cursor cur = sqLiteDB.rawQuery(selectQuery, null);
		  cur.moveToFirst();
		  while (cur.isAfterLast() == false) 
		  {
		      omap[i][0]  = cur.getString(0);
		      omap[i][1]  = cur.getString(1);
		      i++;
		      cur.moveToNext();
		  }  
		  Log.i("==", "--"+omap[0][0]);
		  return omap;
		} 
	
	public void deleterow(String title) {
		Log.i("==", "qaws");
//		  sqLiteDB = this.getWritableDatabase();  
		  String deleteQuery = "DELETE FROM  OffMap where title='"+ title +"'";   
		  sqLiteDB.execSQL(deleteQuery);
		  Log.i("==", "ppp");
		}
	
	public void deleteall() {
//		  sqLiteDB = this.getWritableDatabase();  
		  String deleteQuery = "DELETE FROM  OffMap";   
		  sqLiteDB.execSQL(deleteQuery);
		}
}
