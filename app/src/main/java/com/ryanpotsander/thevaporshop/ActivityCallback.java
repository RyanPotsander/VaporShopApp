package com.ryanpotsander.thevaporshop;

/**
 * Created by Home on 9/3/2015.
 */
public interface ActivityCallback {
    void onOptionItemSelected(int viewId, int position, long id);
    void onInsertPurchase(ItemHelper.ChildItemParams params);
    void showMainFragment();
    void onCursorTaskSelected(TaskParams params);
    void showHistoryDialog(long id, int position);
    void showAddCustomerFragment();
    void showOptionsDialog(int position, long id);
    void setTestCursors();
}
