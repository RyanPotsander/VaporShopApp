package com.ryanpotsander.thevaporshop;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.bearcub.recyclerviewlibrary.BaseRecyclerFragment;

import java.util.Calendar;
import java.util.Locale;

/**
 * The MIT License (MIT)

 Copyright (c) [2015] [Ryan Potsander]

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */


public class MainActivity extends BaseActivity implements BaseRecyclerFragment.OnTouchItemSelectedListener, ActivityCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String POPULATE_PURCHASE_HISTORY_TASK_TAG = "populate purchase history";
    public static final String GET_CURSOR_TASK_TAG = "get cursor";
    public static final String DELETE_TASK_TAG = "delete";
    public static final String ADD_TASK_TAG = "add";
    public static final String UPDATE_TOTAL_TASK_TAG = "updateTotal";
    public static final String HISTORY_CURSOR_TASK = "historyCursor";
    public Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    MainFragment mainFragment;
    android.widget.SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initToolbar();

        initNavigationDrawer();

        if(savedInstanceState==null) {
            showMainFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);

        String[] from = {Contract.MainTable.COLUMN_LAST_NAME};
        int[] to = {R.id.textView2};
        SimpleCursorAdapter suggestionAdapter = new SimpleCursorAdapter(this, R.layout.suggestion, null, from, to);

        mSearchView = (android.widget.SearchView) search.getActionView();
        mSearchView.setSuggestionsAdapter(suggestionAdapter);
        mSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), "searched " + query, Toast.LENGTH_SHORT).show();

                //String[] selectionArgs = new String[]{"%" + query + "%"};
                //String selection = Contract.MainTable.COLUMN_LAST_NAME + " LIKE ?";
                //TODO refactor to async
                String likeLast = Contract.MainTable.COLUMN_LAST_NAME + " LIKE " + "'%" + query + "%'";
                String likeFirst = Contract.MainTable.COLUMN_FIRST_NAME + " LIKE " + "'%" + query + "%'";
                String likeEmail = Contract.MainTable.COLUMN_EMAIL + " LIKE " + "'%" + query + "%'";
                String selection = "SELECT * FROM " + Contract.MainTable.TABLE_NAME + " WHERE " + likeLast
                        + " OR " + likeFirst + " OR " + likeEmail;
                Cursor mainCursor = DbConnection.getCursor(selection);
                Cursor historyCursor = null;


                if (mainCursor.getCount() > 0){
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

                    //mainFragment.adapter.changeCursor(RecyclerViewCursorAdapter.MAIN_CURSOR_HELPER_POSITION, mainCursor);
                    //mainFragment.adapter.changeCursor(RecyclerViewCursorAdapter.PRIMARY_CHILD_CURSOR_HELPER_POSITION, historyCursor);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            item.collapseActionView();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTouchItemSelected(View view, int position) {
    }

    /**
    @Override
    public void onTouchNavigationItemSelected(View view, int position) {
        Toast.makeText(this, "clicked " + position, Toast.LENGTH_SHORT).show();
    } */

    public void initToolbar(){
        mToolbar = setUpToolbar(R.id.my_toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        mToolbar.setTitle("Customer Database");
    }

    public void initNavigationDrawer(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setUpDrawer(mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_closed);
        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        if (!mainFragment.isResumed()) {
                            showMainFragment();
                        }
                        Snackbar.make(navigationView, "home", Snackbar.LENGTH_SHORT)
                                .show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.navigation_all:
                        mDrawerLayout.closeDrawers();
                        Snackbar.make(navigationView, "all", Snackbar.LENGTH_SHORT)
                                .show();
                        return true;
                    case R.id.navigation_search:
                        Snackbar.make(navigationView, "search", Snackbar.LENGTH_SHORT)
                                .show();
                        mDrawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });
    }

    public void showMainFragment(){
        if (mainFragment == null){
            mainFragment = MainFragment.newInstance();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content_view, mainFragment).commit();
    }

    public void showHistoryDialog(long id, int position){
        String tag = "historyDialog";
        DialogFragment fragment = HistoryDialogFragment.newInstance(id, position);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog);
        fragment.show(getFragmentManager(), tag);
    }

    public void showAddCustomerFragment(){
        String tag = "addDialog";
        DialogFragment fragment = AddCustomerDialog.newInstance();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog);
        fragment.show(getFragmentManager(), tag);
    }

    @Override
    public void showOptionsDialog(int position, long id) {
        String tag = "optionsDialog";
        OptionsDialog fragment = OptionsDialog.newInstance(position, id);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog);
        fragment.show(getFragmentManager(), tag);
    }

    @Override
    public void setTestCursors() {
        GetDefaultCursor task = new GetDefaultCursor();
        task.execute("");
    }

    @Override
    public void onCursorTaskSelected(TaskParams params) {
        switch (params.tag){
            case GET_CURSOR_TASK_TAG:
                break;
            case DELETE_TASK_TAG:
                DeleteTask deleteTask = new DeleteTask();
                deleteTask.execute(params.id);
                break;
            case ADD_TASK_TAG:
                AddCustomerTask addTask = new AddCustomerTask();
                addTask.execute(params.parentItemParams);
                break;
        }
    }

    @Override
    public void onInsertPurchase(ItemHelper.ChildItemParams params) {
        InsertPurchase task = new InsertPurchase();
        task.execute(params);
    }

    @Override
    public void onOptionItemSelected(int viewId, int position, long id) {
        switch (viewId){
            case R.id.btn_add_options:
                showHistoryDialog(id, position);
                break;
            case R.id.btn_edit_options:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_close_option:
                mainFragment.toggleViewVisibility(mainFragment.expandedView, mainFragment.lastView);
                break;
            case R.id.btn_delete_options:
                mainFragment.deleteItem(position);
                mainFragment.adapter.notifyItemRemoved(position);
                break;
            default:
                break;
        }
    }

    public class GetDefaultCursor extends AsyncTask<String, Void, Cursor[]>{

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
            //TODO
            mainFragment.adapter.changeCursor(result[0], 0);
            mainFragment.adapter.changeCursor(result[1], 1);
        }
    }

    public class GetSearchedCursor extends AsyncTask<String, Void, Cursor> {
        @Override
        protected Cursor doInBackground(String... params) {
            return DbConnection.getSearchedCursor(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (result != null && result.getCount() > 0) {
                mainFragment.adapter.changeCursor(result, 0);
            }
        }
    }

    public class InsertPurchase extends AsyncTask<ItemHelper.ChildItemParams, Void, Cursor>{

        @Override
        protected Cursor doInBackground(ItemHelper.ChildItemParams... params) {
            return DbConnection.insertPurchase(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            mainFragment.adapter.changeCursor(result, 1);
        }
    }

    //TODO update
    public class DeleteTask extends AsyncTask<Long, Void, Cursor[]> {
        @Override
        protected Cursor[] doInBackground(Long... params) {
            return DbConnection.deleteRecord(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor[] result) {
            mainFragment.adapter.changeCursor(result[0], 0);
            mainFragment.adapter.changeCursor(result[1], 1);
        }
    }

    public class AddCustomerTask extends AsyncTask<ItemHelper.ParentItemParams, Void, Cursor> {

        @Override
        protected Cursor doInBackground(ItemHelper.ParentItemParams... params) {
            return DbConnection.insertCustomer(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if (result == null){
                Log.d(TAG, "result is null");
            }
            mainFragment.adapter.changeCursor(result, 0);
        }
    }
}
