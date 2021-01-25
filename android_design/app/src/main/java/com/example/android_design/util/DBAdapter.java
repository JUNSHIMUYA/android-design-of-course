package com.example.android_design.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android_design.db.Addcity;
import com.example.android_design.db.CityCode;

public class DBAdapter {
    private static final String DB_NAME = "add_city.db";
	private static final String DB_TABLE = "add_city";
	private static final int DB_VERSION = 3;

	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";


	private SQLiteDatabase db;
	private final Context context;
	private MyDBOpenHelper dbOpenHelper;

	private  static class MyDBOpenHelper extends SQLiteOpenHelper
    {
        private static final String DB_CREATE = "create table " +DB_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_NAME+ " text not null"+" );";


        public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DB_CREATE);
            db.execSQL("create table if not EXISTS cityandcode(id integer primary key autoincrement,name text not null,code text not null);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + "cityandcode");
            Log.v("dblength","ss");
            onCreate(db);
        }
    }

	public DBAdapter(Context context)
    {
        this.context=context;
    }

    public void open()throws SQLiteException {
        dbOpenHelper = new MyDBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close()
    {
        if (db != null){
            db.close();
            db = null;
        }
    }

    public long insert(Addcity addcity)
    {
        ContentValues newValues=new ContentValues();
        newValues.put(KEY_NAME,addcity.name);
        return db.insert(DB_TABLE,null,newValues);
    }

    public long insertCaC(CityCode cityCode)
    {
        ContentValues newValues=new ContentValues();
        newValues.put("name",cityCode.name);
        newValues.put("code",cityCode.code);
        return db.insert("cityandcode",null,newValues);
    }

    public Addcity []quertAllAddcity()
    {
        Cursor results = db.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME},
                null, null, null, null,
                null);//查询条件为空则返回所有数据
        return ConvertToPeople(results);
    }

    public CityCode[] quertCode(String s)
    {
        Cursor results = db.query("cityandcode", new String[] { "name", "code"},
                "name="+"'"+s+"'", null, null, null, null);//查询条件为空则返回所有数据
        return ConvertToCityCode(results);
    }

    private Addcity[] ConvertToPeople(Cursor cursor)
    {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        cursor.moveToFirst();
        Addcity[] addcities = new Addcity[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            addcities[i] = new Addcity();
            addcities[i].id= cursor.getInt(0);
            addcities[i].name =cursor.getString(1);
            cursor.moveToNext();
        }
        return addcities;
    }

    private CityCode[] ConvertToCityCode(Cursor cursor)
    {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        cursor.moveToFirst();
       CityCode[] cityCodes = new CityCode[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            cityCodes[i] = new CityCode();
            cityCodes[i].code= cursor.getString(2);
            cityCodes[i].name =cursor.getString(1);
            cursor.moveToNext();
        }
        return cityCodes;
    }

    public long deleteData(String name)
    {
        return db.delete(DB_TABLE, KEY_NAME+"="+"'"+name+"'", null);
    }



    }


