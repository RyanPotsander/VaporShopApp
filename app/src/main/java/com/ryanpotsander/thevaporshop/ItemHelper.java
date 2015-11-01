package com.ryanpotsander.thevaporshop;

import android.database.Cursor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Home on 9/10/2015.
 */
public class ItemHelper {
    private ItemKey key;
    private ParentItemParams parentItemParams;
    private ArrayList<ChildItemParams> list;

    private long id;
    private String firstName;
    String lastName;
    int birthDay;
    String birthMonth;
    String email;
    String total;
    float floatTotal;

    public ItemHelper(ItemKey key){
        this.key = key;
        list = new ArrayList<>();
    }

    private ItemHelper(){
    }

    public static ItemHelper getTempHelper(){
        return new ItemHelper();
    }

    public void setParentItemFromCursor(Cursor cursor) {
        ParentItemParams item = new ParentItemParams();
        item.id = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.MainTable._ID));
        item.firstName = cursor.getString(cursor.getColumnIndexOrThrow(Contract.MainTable.COLUMN_FIRST_NAME));
        item.lastName = cursor.getString(cursor.getColumnIndexOrThrow(Contract.MainTable.COLUMN_LAST_NAME));
        item.birthDay = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.MainTable.COLUMN_BIRTH_DAY));
        item.birthMonth = cursor.getString(cursor.getColumnIndexOrThrow(Contract.MainTable.COLUMN_BIRTH_MONTH));
        item.email = cursor.getString(cursor.getColumnIndexOrThrow(Contract.MainTable.COLUMN_EMAIL));

        item.key = new ItemKey(item.id);

        setParentItemParams(item);
    }

    public void addChildItemFromCursor(Cursor cursor) {
        ChildItemParams item = new ChildItemParams();
        item.id = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.HistoryTable._ID));
        item.amount = cursor.getFloat(cursor.getColumnIndexOrThrow(Contract.HistoryTable.COLUMN_AMOUNT));
        item.day = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.HistoryTable.COLUMN_DAY));
        item.month = cursor.getInt(cursor.getColumnIndex(Contract.HistoryTable.COLUMN_MONTH));
        item.year = cursor.getInt(cursor.getColumnIndex(Contract.HistoryTable.COLUMN_YEAR));
        item.foreignKey = cursor.getInt(cursor.getColumnIndex(Contract.HistoryTable.COLUMN_MAIN_TABLE_ID));

        item.key = new ItemKey(item.foreignKey);

        DecimalFormat format = new DecimalFormat("$###,##0.00");
        String formattedValue = format.format(item.amount);
        item.viewText = (item.month + "/" + item.day + "   " + formattedValue);

        list.add(item);
    }

    public void setParentItemParams(ParentItemParams item){
        this.parentItemParams = item;
    }

    public ParentItemParams getParentItemParams(){
        if (parentItemParams == null){
            return null;
        }
        setTotals(list);
        return parentItemParams;
    }

    public void add(ChildItemParams item){
        list.add(item);
    }

    public ArrayList<ChildItemParams> getChildItemList(){
        return list;
    }

    public ItemKey getKey(){
        return key;
    }


    public void setTotals(ArrayList<ChildItemParams> list){
        if(list.size() < 1){
            parentItemParams.floatTotal = 0;
            parentItemParams.total = "0.00";
            return;
        }

        parentItemParams.floatTotal = sumValuesFloat(list);
        parentItemParams.total = sumValuesString(list);
    }

    private String sumValuesString(ArrayList<ChildItemParams> list){
        Iterator<ChildItemParams> i = list.iterator();
        float sum = 0;
        while (i.hasNext()){
            sum = (sum + i.next().amount);
        }

        DecimalFormat format = new DecimalFormat("$###,##0.00");
        return format.format(sum);
    }

    private float sumValuesFloat(ArrayList<ChildItemParams> list){
        Iterator<ChildItemParams> i = list.iterator();
        float sum = 0;
        while (i.hasNext()){
            sum = (sum + i.next().amount);
        }

        return sum;
    }

    public ChildItemParams getChildItemHolder(){
        return new ChildItemParams();
    }

    public ParentItemParams getParentItemHolder(){
        return new ParentItemParams();
    }



    public class ParentItemParams {
        ItemKey key;
        long id;
        String firstName;
        String lastName;
        int birthDay;
        String birthMonth;
        String email;
        String total;
        float floatTotal;

        public ParentItemParams(){

        }
    }

    public class ChildItemParams {
        ItemKey key;
        String viewText;
        float amount;
        int day;
        int month;
        int year;
        long id;
        long foreignKey;
    }

    public class ItemHolder{

    }

}
