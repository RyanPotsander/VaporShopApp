package com.ryanpotsander.thevaporshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Home on 8/5/2015.
 */
public class DbConnection {
    private static final String TAG = DbConnection.class.getSimpleName();
    private static DbHelper dbHelper = null;
    private static SQLiteDatabase db = null;
    private static Context context = null;

    private DbConnection(){}

    public static void init(Context c){
        if(dbHelper == null){
            dbHelper = new DbHelper(c);
        }

        if(DbConnection.context == null){
            DbConnection.context = c;
        }
    }

    public static void openReadable(){
        if(db==null){
            db = dbHelper.getReadableDatabase();
        }
    }

    public static void openWritable(){
        db = dbHelper.getWritableDatabase();
    }

    public static void close(){
        if (db != null){
            db.close();
            db = null;
        }
    }

    public static Cursor insertPurchase(ItemHelper.ChildItemParams item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.HistoryTable.COLUMN_AMOUNT, item.amount);
        values.put(Contract.HistoryTable.COLUMN_MAIN_TABLE_ID, item.foreignKey);
        values.put(Contract.HistoryTable.COLUMN_DAY, item.day);
        values.put(Contract.HistoryTable.COLUMN_MONTH, item.month);

        long newRowId = db.insert(Contract.HistoryTable.TABLE_NAME, null, values);

        String select = "SELECT * FROM " + Contract.HistoryTable.TABLE_NAME + " WHERE " +
                Contract.HistoryTable.COLUMN_MAIN_TABLE_ID + " = " + item.foreignKey;
        return db.rawQuery(select, null);
    }

    public static Cursor getChildCursor(long mainTableId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "SELECT * FROM " + Contract.HistoryTable.TABLE_NAME + " WHERE " +
                Contract.HistoryTable.COLUMN_MAIN_TABLE_ID + " = " + mainTableId;
        return db.rawQuery(selection, null);
    }

    public static Cursor insertCustomer(ItemHelper.ParentItemParams item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.MainTable.COLUMN_FIRST_NAME, item.firstName);
        values.put(Contract.MainTable.COLUMN_LAST_NAME, item.lastName);
        values.put(Contract.MainTable.COLUMN_EMAIL, item.email);
        values.put(Contract.MainTable.COLUMN_BIRTH_DAY, item.birthDay);
        values.put(Contract.MainTable.COLUMN_BIRTH_MONTH, item.birthMonth);
        values.put(Contract.MainTable.COLUMN_TOTAL, item.total);

        long id = db.insert(Contract.MainTable.TABLE_NAME, null, values);

        String selection = "SELECT * FROM " + Contract.MainTable.TABLE_NAME + " WHERE " +
                Contract.MainTable._ID + " = " + id;

        return db.rawQuery(selection, null);

        //TODO return id so recyclerView is scrolled to position
    }

    public static Cursor getCursor(String selection){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(selection, null);
    }

    public static Cursor[] deleteRecord(long itemId){
        Log.d(TAG, "item id is " + itemId);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String childSelection = Contract.HistoryTable.COLUMN_MAIN_TABLE_ID + " = " + itemId;
        String selection = Contract.MainTable._ID + " = " + itemId;

        db.delete(Contract.HistoryTable.TABLE_NAME, childSelection, null);
        db.delete(Contract.MainTable.TABLE_NAME, selection, null);

        Cursor[] cursors = new Cursor[2];
        cursors[0] = db.rawQuery("SELECT * FROM " + Contract.MainTable.TABLE_NAME, null);
        cursors[1] = db.rawQuery("SELECT * FROM " + Contract.HistoryTable.TABLE_NAME, null);
        return cursors;
    }

     public static Cursor getSearchedCursor(String selectionArgs){
     SQLiteDatabase db = dbHelper.getReadableDatabase();
     String selection = Contract.MainTable.COLUMN_LAST_NAME + " LIKE ?";

     return db.rawQuery("SELECT * FROM " + Contract.MainTable.TABLE_NAME + ", " + Contract.HistoryTable.TABLE_NAME, null);
     }

    public static Cursor getMainTableCursor(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String select = "SELECT * FROM " + Contract.MainTable.TABLE_NAME;
        return db.rawQuery(select, null);
    }

    public static Cursor getHistoryCursor(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String select = "SELECT * FROM " + Contract.HistoryTable.TABLE_NAME;
        return db.rawQuery(select, null);
    }

    private static Cursor[] createCursorArray(Cursor cursor, Cursor childCursor){
        Cursor[] cursors = new Cursor[2];
        cursors[0] = cursor;
        cursors[1] = childCursor;
        return cursors;
    }

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Contract.MainTable.SQL_CREATE_ENTRIES);
            db.execSQL(Contract.HistoryTable.SQL_CREATE_ENTRIES);
            Log.d(TAG, "db oncreate ok");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(Contract.MainTable.SQL_DELETE_ENTRIES);
            db.execSQL(Contract.HistoryTable.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
