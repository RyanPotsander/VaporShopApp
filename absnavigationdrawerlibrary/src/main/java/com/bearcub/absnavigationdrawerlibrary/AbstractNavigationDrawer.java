package com.bearcub.absnavigationdrawerlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bearcub.recyclerviewlibrary.BaseRecyclerFragment;

import java.util.Collections;
import java.util.List;

/**
 * Created by Home on 6/12/2015.
 */
public abstract class AbstractNavigationDrawer extends BaseRecyclerFragment {
    public static final String SHARED_PREFERENCES_FILE_NAME = "shared_preferences";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private static final String TAG = "nav_drawer_fragment";

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private NavigationDrawerAdapter adapter;
    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private View containerView;
    OnTouchNavigationItemSelectedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLearnedDrawer = Boolean.valueOf(readFromSharedPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if(savedInstanceState != null){
            fromSavedInstanceState = true;
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getMainLayoutId(), container, false);

        setUpRecyclerView(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnTouchNavigationItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTouchNavigationItemSelectedListener");
        }
    }

    public void setUpRecyclerView(View view){
        recyclerView = (RecyclerView)view.findViewById(getRecyclerViewId());
        adapter = new NavigationDrawerAdapter(getActivity(), getItemData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(createLayoutManager());
        recyclerView.addOnItemTouchListener(new NavigationDrawerTouchListener(getActivity(), recyclerView, new NavigationClickListener() {
            @Override
            public void onClick(View view, int position) {
                mCallback.onTouchNavigationItemSelected(view, position); //todo need view arg?
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "you touched something longer at position " + position, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void initDrawer(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar, int openId, int closedId){
        containerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, openId, closedId){
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);

                if(!userLearnedDrawer){
                    userLearnedDrawer = true;
                    writeToSharedPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, Boolean.toString(userLearnedDrawer));
                }
                getActivity().invalidateOptionsMenu();
            }
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);

                getActivity().invalidateOptionsMenu();
            }

        };

        if(!userLearnedDrawer && !fromSavedInstanceState){
            drawerLayout.openDrawer(containerView);
        }
        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }

    public void writeToSharedPreferences(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readFromSharedPreferences(Context context, String key, String defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    class NavigationDrawerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private NavigationClickListener navigationClickListener;


        public NavigationDrawerTouchListener(Context context, final RecyclerView recyclerView, final NavigationClickListener navigationClickListener){
            this.navigationClickListener = navigationClickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View view = recyclerView.findChildViewUnder(e.getX(),e.getY());

                    if(view!=null && navigationClickListener!=null){
                        navigationClickListener.onLongClick(view, recyclerView.getChildPosition(view));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View view = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

            if(view!=null && navigationClickListener!=null && gestureDetector.onTouchEvent(motionEvent)){
                navigationClickListener.onClick(view, recyclerView.getChildPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            //TODO ...
        }
    }

    public static interface NavigationClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    // Container Activity must implement this interface
    public interface OnTouchNavigationItemSelectedListener { //todo this needed or can i use HomeFragmentClickListener above?
        public void onTouchNavigationItemSelected(View view, int position);
    }

    public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

        private static final String TAG = "navigation_adapter";
        private final LayoutInflater inflater;
        List<NavigationDrawerItem> list = Collections.emptyList();
        Context context;

        public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> list){
            inflater= LayoutInflater.from(context);
            this.context = context;
            this.list = list;
        }

        public void deleteItem(int position){
            list.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = inflater.inflate(getNavigationItemViewId(), viewGroup, false);

            MyViewHolder holder = new MyViewHolder(view);

            Log.d(TAG, "onCreateViewHolder called");
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            Log.d(TAG, "onBindVH called");

            NavigationDrawerItem currentItem = list.get(position);
            viewHolder.label.setText(currentItem.label);
            viewHolder.image.setImageResource(currentItem.imageId);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView label;
            ImageView image;

            public MyViewHolder(View itemView) {
                super(itemView);
                label = (TextView)itemView.findViewById(getTextViewId());
                image = (ImageView)itemView.findViewById(getImgViewId());
            }
        }
    }

    public abstract int getNavigationItemViewId();
    public abstract int getRecyclerViewId();
    public abstract int getMainLayoutId();
    public abstract RecyclerView.LayoutManager createLayoutManager();
    public abstract int getTextViewId();
    public abstract int getImgViewId();
    public abstract List<NavigationDrawerItem> getItemData();
}
