package com.ryanpotsander.thevaporshop;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Home on 9/5/2015.
 */
public abstract class RecyclerViewCursorTreeAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor mCursor;
    private boolean mDataValid;
    private int mCurrentRowId;
    private DataSetObserver mDataSetObserver;
    private boolean mAutoRequery;
    private Context mContext;
    private Handler mHandler;

    SparseArray<MyCursorHelper> mChildrenCursorHelpers;
    MyCursorHelper mGroupCursorHelper;
    //CursorFilter mCursorFilter;
    //FilterQueryProvider mFilterQueryProvider;

    public RecyclerViewCursorTreeAdapter(Context context, Cursor cursor){
        init(context, cursor, true);
    }

    public RecyclerViewCursorTreeAdapter(Context context, Cursor cursor, boolean autoRequery){
        init(context, cursor, autoRequery);
    }

    //RecyclerViewAdapter methods

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("Cursor is invalid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Unable to move primary cursor to position " + position);
        }
        onBindViewHolder(holder, getCursor());
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    //CursorAdapter related methods

    private void init(Context context, Cursor cursor, boolean autoRequery){
        mContext = context;
        mHandler = new Handler();
        mAutoRequery = autoRequery;

        mGroupCursorHelper = new MyCursorHelper(cursor);
        mChildrenCursorHelpers = new SparseArray<MyCursorHelper>();
    }

    synchronized MyCursorHelper getChildrenCursorHelper(int groupPosition, boolean requestCursor) {
        MyCursorHelper cursorHelper = mChildrenCursorHelpers.get(groupPosition);

        if (cursorHelper == null) {
            if (mGroupCursorHelper.moveTo(groupPosition) == null) return null;

            final Cursor cursor = getChildrenCursor(mGroupCursorHelper.getCursor());
            cursorHelper = new MyCursorHelper(cursor);
            mChildrenCursorHelpers.put(groupPosition, cursorHelper);
        }

        return cursorHelper;
    }

    /**
     * Gets the Cursor for the children at the given group. Subclasses must
     * implement this method to return the children data for a particular group.
     * <p>
     * If you want to asynchronously query a provider to prevent blocking the
     * UI, it is possible to return null and at a later time call
     * {@link #setChildrenCursor(int, Cursor)}.
     * <p>
     * It is your responsibility to manage this Cursor through the Activity
     * lifecycle. It is a good idea to use {Activity#managedQuery} which
     * will handle this for you. In some situations, the adapter will deactivate
     * the Cursor on its own, but this will not always be the case, so please
     * ensure the Cursor is properly managed.
     *
     * @param groupCursor The cursor pointing to the group whose children cursor
     *            should be returned
     * @return The cursor for the children of a particular group, or null.
     */
    abstract protected Cursor getChildrenCursor(Cursor groupCursor);

    /**
     * Sets the group Cursor.
     *
     * @param cursor The Cursor to set for the group. If there is an existing cursor
     * it will be closed.
     */
    public void setGroupCursor(Cursor cursor) {
        mGroupCursorHelper.changeCursor(cursor, false);
    }

    /**
     * Sets the children Cursor for a particular group. If there is an existing cursor
     * it will be closed.
     * <p>
     * This is useful when asynchronously querying to prevent blocking the UI.
     *
     * @param groupPosition The group whose children are being set via this Cursor.
     * @param childrenCursor The Cursor that contains the children of the group.
     */
    public void setChildrenCursor(int groupPosition, Cursor childrenCursor) {

        /*
         * Don't request a cursor from the subclass, instead we will be setting
         * the cursor ourselves.
         */
        MyCursorHelper childrenCursorHelper = getChildrenCursorHelper(groupPosition, false);
        /*
         * Don't release any cursor since we know exactly what data is changing
         * (this cursor, which is still valid).
         */
        childrenCursorHelper.changeCursor(childrenCursor, false);
    }

    public Cursor getChild(int groupPosition, int childPosition) {
        // Return this group's children Cursor pointing to the particular child
        return getChildrenCursorHelper(groupPosition, true).moveTo(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return getChildrenCursorHelper(groupPosition, true).getId(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        MyCursorHelper helper = getChildrenCursorHelper(groupPosition, true);
        return (mGroupCursorHelper.isValid() && helper != null) ? helper.getCount() : 0;
    }

    public Cursor getGroup(int groupPosition) {
        // Return the group Cursor pointing to the given group
        return mGroupCursorHelper.moveTo(groupPosition);
    }

    public int getGroupCount() {
        return mGroupCursorHelper.getCount();
    }

    public long getGroupId(int groupPosition) {
        return mGroupCursorHelper.getId(groupPosition);
    }

    //TODO
    /**
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        Cursor cursor = mGroupCursorHelper.moveTo(groupPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        View v;
        if (convertView == null) {
            v = newGroupView(mContext, cursor, isExpanded, parent);
        } else {
            v = convertView;
        }
        bindGroupView(v, mContext, cursor, isExpanded);
        return v;
    }
     */
    /**
     * Makes a new group view to hold the group data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor The group cursor from which to get the data. The cursor is
     *            already moved to the correct position.
     * @param isExpanded Whether the group is expanded.
     * @param parent The parent to which the new view is attached to
     * @return The newly created view.
     */

    /**
    protected abstract View newGroupView(Context context, Cursor cursor, boolean isExpanded,
                                         ViewGroup parent);
     */

    /**
     * Bind an existing view to the group data pointed to by cursor.
     *
     * @param view Existing view, returned earlier by newGroupView.
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is
     *            already moved to the correct position.
     * @param isExpanded Whether the group is expanded.
     */

    /**
    protected abstract void bindGroupView(View view, Context context, Cursor cursor,
                                          boolean isExpanded);


    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        MyCursorHelper cursorHelper = getChildrenCursorHelper(groupPosition, true);

        Cursor cursor = cursorHelper.moveTo(childPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        View v;
        if (convertView == null) {
            v = newChildView(mContext, cursor, isLastChild, parent);
        } else {
            v = convertView;
        }
        bindChildView(v, mContext, cursor, isLastChild);
        return v;
    }
     */
    /**
     * Makes a new child view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is
     *            already moved to the correct position.
     * @param isLastChild Whether the child is the last child within its group.
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    /**
    protected abstract View newChildView(Context context, Cursor cursor, boolean isLastChild,
                                         ViewGroup parent);
     */
    /**
     * Bind an existing view to the child data pointed to by cursor
     *
     * @param view Existing view, returned earlier by newChildView
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is
     *            already moved to the correct position.
     * @param isLastChild Whether the child is the last child within its group.
     */
    /**
    protected abstract void bindChildView(View view, Context context, Cursor cursor,
                                          boolean isLastChild);
     */

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private synchronized void releaseCursorHelpers() {
        for (int pos = mChildrenCursorHelpers.size() - 1; pos >= 0; pos--) {
            mChildrenCursorHelpers.valueAt(pos).deactivate();
        }

        mChildrenCursorHelpers.clear();
    }

    /**
     * Notifies a data set change, but with the option of not releasing any
     * cached cursors.
     *
     * @param releaseCursors Whether to release and deactivate any cached
     *            cursors.
     */
    /**
    public void notifyDataSetChanged(boolean releaseCursors) {

        if (releaseCursors) {
            releaseCursorHelpers();
        }

        super.notifyDataSetChanged();
    }
    */

    /**
    @Override
    public void notifyDataSetInvalidated() {
        releaseCursorHelpers();
        super.notifyDataSetInvalidated();
    } */

    public void onGroupCollapsed(int groupPosition) {
        deactivateChildrenCursorHelper(groupPosition);
    }

    /**
     * Deactivates the Cursor and removes the helper from cache.
     *
     * @param groupPosition The group whose children Cursor and helper should be
     *            deactivated.
     */
    synchronized void deactivateChildrenCursorHelper(int groupPosition) {
        MyCursorHelper cursorHelper = getChildrenCursorHelper(groupPosition, true);
        mChildrenCursorHelpers.remove(groupPosition);
        cursorHelper.deactivate();
    }

    public String convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    //filter related methods...TODO
    /**
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }
        return mGroupCursorHelper.getCursor();
    }

    public Filter getFilter() {
        if (mCursorFilter == null) {
            mCursorFilter = new Filter(this);
        }
        return mCursorFilter;
    }

    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    } **/

    public void changeCursor(Cursor cursor) {
        mGroupCursorHelper.changeCursor(cursor, true);
    }

    public Cursor getCursor() {
        return mGroupCursorHelper.getCursor();
    }

    class MyCursorHelper {
        private Cursor mCursor;
        private boolean mDataValid;
        private int mRowIDColumn;
        private MyContentObserver mContentObserver;
        private MyDataSetObserver mDataSetObserver;

        public MyCursorHelper(Cursor cursor) {
            final boolean cursorPresent = cursor != null;
            mCursor = cursor;
            mDataValid = cursorPresent;
            mRowIDColumn = cursorPresent ? cursor.getColumnIndex("_id") : -1;
            mContentObserver = new MyContentObserver();
            mDataSetObserver = new MyDataSetObserver();
            if (cursorPresent) {
                cursor.registerContentObserver(mContentObserver);
                cursor.registerDataSetObserver(mDataSetObserver);
            }
        }

        Cursor getCursor() {
            return mCursor;
        }

        int getCount() {
            if (mDataValid && mCursor != null) {
                return mCursor.getCount();
            } else {
                return 0;
            }
        }

        long getId(int position) {
            if (mDataValid && mCursor != null) {
                if (mCursor.moveToPosition(position)) {
                    return mCursor.getLong(mRowIDColumn);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        Cursor moveTo(int position) {
            if (mDataValid && (mCursor != null) && mCursor.moveToPosition(position)) {
                return mCursor;
            } else {
                return null;
            }
        }

        void changeCursor(Cursor cursor, boolean releaseCursors) {
            if (cursor == mCursor) return;
            deactivate();
            mCursor = cursor;
            if (cursor != null) {
                cursor.registerContentObserver(mContentObserver);
                cursor.registerDataSetObserver(mDataSetObserver);
                mRowIDColumn = cursor.getColumnIndex("_id");
                mDataValid = true;
                // notify the observers about the new cursor
                notifyDataSetChanged();
                releaseCursorHelpers();
            } else {
                mRowIDColumn = -1;
                mDataValid = false;
                notifyDataSetChanged();
                // notify the observers about the lack of a data set
                //notifyDataSetInvalidated();
            }
        }

        void deactivate() {
            if (mCursor == null) {
                return;
            }

            mCursor.unregisterContentObserver(mContentObserver);
            mCursor.unregisterDataSetObserver(mDataSetObserver);
            mCursor.close();
            mCursor = null;
        }

        boolean isValid() {
            return mDataValid && mCursor != null;
        }
    }

    private class MyContentObserver extends ContentObserver {
        public MyContentObserver() {
            super(mHandler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            if (mAutoRequery && mCursor != null && !mCursor.isClosed()) {
                if (false) Log.v("Cursor", "Auto requerying " + mCursor +
                        " due to update");
                mDataValid = mCursor.requery();
            }
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //TODO
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
