package com.ryanpotsander.thevaporshop;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Home on 9/9/2015.
 */
public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.ViewHolder> {
    private static final String TAG = RecyclerViewCursorAdapter.class.getSimpleName();

    private static final int PARENT_CURSOR_KEY = 0;

    private static final int CHILD_CURSOR_KEY = 1;

    private CursorManager mCursorManager;

    private Context mContext;

    private ArrayList<ItemKey> mKeyList;

    private HashMap<ItemKey, ItemHelper> mItemHelpers;

    public RecyclerViewCursorAdapter(Context context) {
        mContext = context;
        mKeyList = new ArrayList<>();
        mItemHelpers = new HashMap<>();
        this.mCursorManager = new CursorManager();
    }

    public void changeCursor(Cursor cursor, int index){
        getCursorManager().changeCursor(cursor, index);
    }

    public CursorManager getCursorManager(){
        return mCursorManager;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        return mItemHelpers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        int childCount = getItemHelperForPosition(viewType).getChildItemList().size();
        for (int i = 0; i < childCount; i++ ){
            vh.childView.addView(new TextView(mContext), i);
        }
        return vh;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**
         if (!helper.isValid()) {
         throw new IllegalStateException("this should only be called when the cursor is valid");
         }
         if (helper.moveTo(position) == null) {
         throw new IllegalStateException("couldn't move cursor to position " + position);
         }
         */
        Log.d(TAG, "mKeyList " + mKeyList.toString());
        Log.d(TAG, "cursorCount = " + mCursorManager.mCursorHolders.size() + " item helpers = " + mItemHelpers.size() + " keylist = " + mKeyList.size());
        ItemHelper helper = getItemHelperForPosition(position);

        ItemHelper.ParentItemParams parentItemParams = helper.getParentItemParams();

        if(parentItemParams.floatTotal >= 200 && !holder.rewardImage.isShown()){
            holder.rewardImage.setVisibility(View.VISIBLE);
        }

        if (isBirthday(parentItemParams.birthMonth, parentItemParams.birthDay) && !holder.birthdayImage.isShown()){
            holder.birthdayImage.setVisibility(View.VISIBLE);
        }

        String name = parentItemParams.firstName + " " + parentItemParams.lastName;
        holder.nameView.setText(name);
        holder.emailView.setText(parentItemParams.email);
        holder.totalView.setText(parentItemParams.total);

        holder.parentId = parentItemParams.id;

        //see if children items exist
        ArrayList<ItemHelper.ChildItemParams> childItemParamses = helper.getChildItemList();
        //make sure list isn't null or empty
        if (childItemParamses == null || childItemParamses.size() <1) return;

        //get count of any existing views for reuse
        int existingViewCount = holder.childView.getChildCount();
        //count iterations to use against existingViewCount
        int loopCount = 0;

        //text to bind
        String transaction;

        Iterator<ItemHelper.ChildItemParams> i = childItemParamses.iterator();
        while (i.hasNext()){
            ItemHelper.ChildItemParams childItemParams = i.next();
            ++loopCount;

            int childIndex = loopCount - 1;

            TextView textView;



            transaction = childItemParams.month + "/" + childItemParams.day + "/" + childItemParams.year + "   $" + String.valueOf(childItemParams.amount);

            //if we've looped beyond number of existing views, create and add view
            if (loopCount > existingViewCount) {
                holder.childView.addView(new TextView(mContext), childIndex);
            }

            textView =(TextView)holder.childView.getChildAt(childIndex);
            textView.setText(transaction);
        }
    }

    public ItemHelper getItemHelperForPosition(int position){
        Log.d(TAG, "position = " + position);
        return mItemHelpers.get(mKeyList.get(position));
    }

    public boolean isBirthday(String month, int year){
        //TODO
        return false;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView, emailView, totalView;
        public ImageView birthdayImage;
        public ImageView rewardImage;
        public ImageButton settingsButton;
        public LinearLayout childView;
        public boolean childViewIsClear;
        public long parentId;

        public ViewHolder(View view) {
            super(view);

            childViewIsClear = true;

            nameView = (TextView)view.findViewById(R.id.text_name_test);
            emailView = (TextView)view.findViewById(R.id.text_email_test);
            totalView = (TextView)view.findViewById(R.id.text_total_test_num);

            childView = (LinearLayout)view.findViewById(R.id.child_view);

            settingsButton = (ImageButton)view.findViewById(R.id.button_settings_test);
            settingsButton.setColorFilter(Color.parseColor("#F44336"));

            birthdayImage = (ImageView)view.findViewById(R.id.image_birthday);
            birthdayImage.setVisibility(View.GONE);

            rewardImage = (ImageView)view.findViewById(R.id.image_reward);
            rewardImage.setVisibility(View.GONE);
        }
    }

    public void notifyItemHelpersChanged(int cursorHolderKey){
        super.notifyDataSetChanged();
    }


    private class MyAdapterObserver extends RecyclerView.AdapterDataObserver{
        //TODO
    }


    /**
     * Helper class to manipulate data from cursor(s) for compatibility with rvAdapter
     */
    private class CursorManager {
        private SparseArray<CursorHolder> mCursorHolders;
        int parentCursorIndex;
        int childCursorIndex;

        public CursorManager(){
            mCursorHolders = new SparseArray<>();
        }

        public CursorManager(Cursor cursor, Cursor childCursor) {
            boolean cursorPresent = cursor != null;
            boolean childCursorPresent = childCursor != null;

            mCursorHolders = new SparseArray<>();

            if (cursorPresent){
                parentCursorIndex = addCursor(cursor, PARENT_CURSOR_KEY, false);
            }
            if (childCursorPresent){
                childCursorIndex = addCursor(childCursor, CHILD_CURSOR_KEY, true);
            }

            Log.d(TAG, "cursor index for parent = " + parentCursorIndex + " child = " + childCursorIndex);
        }

        public boolean cursorInitialized(int cursorIndex){
            CursorHolder holder = mCursorHolders.get(cursorIndex);
            if (holder != null){
                if (holder.mCursor != null) return true;
            };
            return false;
        }

        public int addCursor(Cursor cursor, int key, boolean isChild){
            mCursorHolders.put(key, new CursorHolder(cursor, key, isChild));

            return key;
        }

        public int addCursor(Cursor cursor, boolean isChild){
            int key = mCursorHolders.size();
            while (mCursorHolders.get(key) != null){
                key++;
            }

            CursorHolder holder = new CursorHolder(cursor, key, isChild);
            mCursorHolders.put(key, holder);

            return key;
        }

        Cursor getCursor(int cursorIndex){
            return mCursorHolders.get(cursorIndex).getCursor();
        }

        void changeCursor(Cursor cursor, int cursorIndex){
            if (!cursorInitialized(cursorIndex)){
                boolean isChild = false;
                if (cursorIndex > 0) isChild = true;
                addCursor(cursor, cursorIndex, isChild);
                return;
            }
            mCursorHolders.get(cursorIndex).changeCursor(cursor);
        }

        int getItemCount(){
            return mItemHelpers.size();
        }

        void notifyDataSetChanged(int cursorHolderKey){
            notifyItemHelpersChanged(cursorHolderKey);
        }

        void notifyDataSetInvalidated(){
            //TODO
        }


        //helper class to manage cursors and data observers
        public class CursorHolder{
            private int mRowIDColumn;
            private boolean mIsChild;
            private boolean mDataValid;
            private Cursor mCursor;
            private MyDataSetObserver mObserver;
            private String mTag;
            private int mPositionKey;

            //cursor must be present
            public CursorHolder(Cursor cursor, int key,  boolean isChild){
                setIsChild(isChild);
                this.mCursor = cursor;
                this.mPositionKey = key;
                mObserver = new MyDataSetObserver();
                registerDataSetObserver(cursor, mObserver);

                mRowIDColumn =cursor.getColumnIndex("_id");

                sortCursorToMap();
            }

            public void registerDataSetObserver(Cursor cursor, MyDataSetObserver observer){
                observer.setHolderKey(mPositionKey);
                cursor.registerDataSetObserver(observer);
            }
            public Cursor getCursor(){
                return mCursor;
            }

            public void setIsChild(boolean isChild){
                this.mIsChild = isChild;
            }

            public boolean isChild(){
                return mIsChild;
            }

            public void setTag(String tag){
                this.mTag = tag;
            }

            public String getTag(){
                return mTag;
            }

            void changeCursor(Cursor cursor) {
                if (cursor == mCursor) return;
                deactivate();
                mCursor = cursor;

                if (cursor != null) {
                    registerDataSetObserver(cursor, mObserver);
                    mRowIDColumn = cursor.getColumnIndex("_id");
                    mDataValid = true;
                    sortCursorToMap();
                    // notify the observers about the new cursor
                    notifyDataSetChanged(mPositionKey);
                } else {
                    mRowIDColumn = -1;
                    mDataValid = false;
                    notifyDataSetChanged(mPositionKey);
                    // notify the observers about the lack of a data set
                    notifyDataSetInvalidated();
                }
            }

            public void deactivate(){
                if (mCursor == null){
                    return;
                }
                mCursor.unregisterDataSetObserver(mObserver);
                mObserver.setHolderKey(-1);
                mCursor.close();
            }

            int getCount() {
                if (mDataValid && mCursor != null) {
                    return mCursor.getCount();
                } else {
                    return 0;
                }
            }

            boolean isValid() {
                return mDataValid && mCursor != null;
            }

            ItemKey findKey(long key){
                Set<ItemKey> keys = mItemHelpers.keySet();
                Iterator<ItemKey> i = keys.iterator();
                while (i.hasNext()){
                    ItemKey itemKey = i.next();
                    if (itemKey.getLongKey() == key){
                        return itemKey;
                    }
                }
                return null;
            }

            int sortChildCursor(){
                ItemKey key;
                ItemHelper helper;
                int count = 0;

                while (mCursor.moveToNext()){
                    key = findKey(ItemKey.getKeyFromCursor(mCursor, true).getLongKey());

                    if (key == null) {
                        Log.d(TAG, "failed to add child record, parent not found");
                        Toast.makeText(mContext, "Add failed, unable to find parent record", Toast.LENGTH_SHORT).show();
                        return count;
                    }

                    helper = mItemHelpers.get(key);
                    helper.addChildItemFromCursor(mCursor);

                    ++count;
                }
                return count;
            }

            int sortParentCursor(){
                ItemKey key;
                ItemHelper helper;
                int count = 0;

                while (mCursor.moveToNext()){
                    long longKey = ItemKey.getKeyFromCursor(mCursor, false).getLongKey();
                    key = findKey(longKey);
                    if (key == null){
                        key = new ItemKey(longKey);
                        mKeyList.add(key);
                        helper = new ItemHelper(key);
                        mItemHelpers.put(key, helper);
                    }else {
                        helper = mItemHelpers.get(key);
                    }

                    helper.setParentItemFromCursor(mCursor);
                    ++count;
                }
                return count;
            }

            void sortCursorToMap(){
                if (!mCursor.isBeforeFirst()) mCursor.moveToPosition(-1);

                if (!isChild()){
                    sortParentCursor();
                    return;
                }

                sortChildCursor();
            }



            public class MyDataSetObserver extends DataSetObserver {
                int cursorHolderKey;

                public void setHolderKey(int cursorHolderKey){
                    this.cursorHolderKey = cursorHolderKey;
                }

                @Override
                public void onChanged() {
                    super.onChanged();
                    mDataValid = true;
                    notifyDataSetChanged(cursorHolderKey);
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    mDataValid = false;
                    notifyDataSetChanged(cursorHolderKey);
                    notifyDataSetInvalidated();
                }
            }
        }
    }
}
