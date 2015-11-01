package com.ryanpotsander.thevaporshop;

import android.provider.BaseColumns;

import java.lang.ref.SoftReference;

/**
 * Created by Home on 8/5/2015.
 */
public class Contract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CustomerData.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String REAL_TYPE = " REAL";
    public static final String COMMA_SEP = ",";

    public Contract() {}

    public static abstract class MainTable implements BaseColumns {
        public static final String TABLE_NAME = "mainTable";
        public static final String COLUMN_FIRST_NAME = "firstName";
        public static final String COLUMN_LAST_NAME = "lastName";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_BIRTH_DAY = "birthDay";
        public static final String COLUMN_BIRTH_MONTH = "birthMonth";
        public static final String COLUMN_TOTAL = "total";

        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
                        TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_BIRTH_DAY + INT_TYPE + COMMA_SEP +
                        COLUMN_BIRTH_MONTH + TEXT_TYPE + COMMA_SEP +
                        COLUMN_TOTAL + REAL_TYPE + " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MainTable.TABLE_NAME;
    }

    public static abstract class HistoryTable implements BaseColumns {
        public static final String TABLE_NAME = "historyTable";
        public static final String COLUMN_MAIN_TABLE_ID = "mainTableId";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_YEAR = "year";

        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
                TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," +
                COLUMN_DAY + INT_TYPE + COMMA_SEP +
                COLUMN_MONTH + INT_TYPE + COMMA_SEP +
                COLUMN_YEAR + INT_TYPE + COMMA_SEP +
                COLUMN_AMOUNT + REAL_TYPE + COMMA_SEP +
                COLUMN_MAIN_TABLE_ID + INT_TYPE + COMMA_SEP +
                "FOREIGN KEY(" + COLUMN_MAIN_TABLE_ID + ") REFERENCES " + MainTable.TABLE_NAME + "(" + MainTable._ID + "))";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + HistoryTable.TABLE_NAME;
    }
}
