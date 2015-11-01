package com.ryanpotsander.thevaporshop;

import android.database.Cursor;

/**
 * Created by Home on 9/11/2015.
 */
public class ItemKey {
    long longKey;

    public ItemKey(long key){
        this.longKey = key;
    }

    public long getLongKey(){
        return longKey;
    }

    public static ItemKey getKeyFromCursor(Cursor cursor, boolean isChildItem){
        long itemKey;
        if (!isChildItem) {
            itemKey = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.MainTable._ID));
        }else{
            itemKey = cursor.getInt(cursor.getColumnIndex(Contract.HistoryTable.COLUMN_MAIN_TABLE_ID));
        }


        return new ItemKey(itemKey);
    }
}
