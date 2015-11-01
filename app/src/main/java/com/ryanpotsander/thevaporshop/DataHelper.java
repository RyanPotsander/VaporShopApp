package com.ryanpotsander.thevaporshop;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Home on 10/17/2015.
 */
public class DataHelper {
    private static final String TAG = DataHelper.class.getSimpleName();

    ActivityCallback mCallback;

    private HashMap<ItemKey, ItemHelper> mItems;

    private ArrayList<ItemKey> mKeys;

    Cursor[] placeholder;

    public DataHelper(ActivityCallback callback){
        mItems = new HashMap<>();
        mKeys = new ArrayList<>();
        this.mCallback = callback;
        placeholder = new Cursor[2];

        DefaultCursor task = new DefaultCursor();
        task.execute("");
    }

    public ItemHelper getItem(int position){
        return mItems.get(getKeyForPosition(position));
    }

    public ItemKey getKeyForPosition(int position){
        return mKeys.get(position);
    }

    public ArrayList<ItemHelper> getDefaultList(){
        ArrayList<ItemHelper> list = new ArrayList<>();

        if (placeholder[0] != null){
            while (placeholder[0].moveToNext()){
                ItemHelper helper = new ItemHelper(ItemKey.getKeyFromCursor(placeholder[0], false));

            }

        }
        return list;
    }

    public class DefaultCursor extends AsyncTask<String, Void, Cursor[]> {

        @Override
        protected Cursor[] doInBackground(String... params) {
            Cursor historyCursor;
            Cursor mainCursor;

            String currentMonth = "'" + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + "'";
            String selection = "SELECT * FROM " + Contract.MainTable.TABLE_NAME + " WHERE " + Contract.MainTable.COLUMN_BIRTH_MONTH + "=" + currentMonth;
            mainCursor = DbConnection.getCursor(selection);

            if (mainCursor.getCount() == 0){
                String altSelection = "SELECT * FROM " + Contract.MainTable.TABLE_NAME;
                mainCursor = DbConnection.getCursor(altSelection);
            }

            String middle = "";
            while (mainCursor.moveToNext()){
                int id = mainCursor.getInt(mainCursor.getColumnIndex(Contract.MainTable._ID));
                String stringValue = "'" + id + "'";

                if (mainCursor.isLast()){
                    middle = middle + stringValue;
                    break;
                }else{
                    middle = middle + stringValue + ", ";
                }
            }

            String inString = "(" + middle + ")";
            Log.d(TAG, "inString" + inString);

            String historySelection = "SELECT * FROM " + Contract.HistoryTable.TABLE_NAME + " WHERE " + Contract.HistoryTable.COLUMN_MAIN_TABLE_ID + " IN " + inString;
            historyCursor = DbConnection.getCursor(historySelection);

            Cursor[] cursors = new Cursor[2];
            cursors[0] = mainCursor;
            cursors[1] = historyCursor;
            return cursors;
        }

        @Override
        protected void onPostExecute(Cursor[] result) {
            placeholder[0] = result[0];
            placeholder[1] = result[1];
        }
    }
}
