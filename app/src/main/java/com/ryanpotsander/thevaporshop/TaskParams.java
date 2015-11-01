package com.ryanpotsander.thevaporshop;

/**
 * Created by Home on 8/29/2015.
 */
public class TaskParams {
    String tag;
    ItemHelper.ParentItemParams parentItemParams;
    Long id;
    int position;

    public TaskParams(){}

    public static TaskParams getCursorsTaskParams(){
        TaskParams params = new TaskParams();
        params.tag = MainActivity.GET_CURSOR_TASK_TAG;
        return params;
    }

    public static TaskParams purchaseHistoryTaskParams(ItemHelper.ParentItemParams item){
        TaskParams params = new TaskParams();

        params.tag = MainActivity.POPULATE_PURCHASE_HISTORY_TASK_TAG;
        params.parentItemParams = item;

        return params;
    }

    public static TaskParams deleteTaskParams(long id, int position){
        TaskParams params = new TaskParams();
        params.id = id;
        params.tag = MainActivity.DELETE_TASK_TAG;
        params.position = position;
        return params;
    }

    public static TaskParams addTaskParams(ItemHelper.ParentItemParams parentItemParams){
        TaskParams params = new TaskParams();
        params.tag = MainActivity.ADD_TASK_TAG;
        params.parentItemParams = parentItemParams;
        return params;
    }

    public static TaskParams updateTotalParams(ItemHelper.ParentItemParams parentItemParams){
        TaskParams params = new TaskParams();
        params.tag = MainActivity.UPDATE_TOTAL_TASK_TAG;
        params.parentItemParams = parentItemParams;
        return params;
    }

    public static TaskParams historyCursorParams(){
        TaskParams params = new TaskParams();
        params.tag = MainActivity.HISTORY_CURSOR_TASK;
        return params;
    }
}
