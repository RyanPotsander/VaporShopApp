package com.bearcub.recyclerviewlibrary;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Home on 7/25/2015.
 */
public class BaseSupportRecyclerFragment extends Fragment {
    public OnTouchItemSelectedListener mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallBack = (OnTouchItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTouchItemSelectedListener");
        }
    }

    public class ExtTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ExtClickListener extClickListener;


        public ExtTouchListener(Context context, final RecyclerView recyclerView, final ExtClickListener extClickListener){
            this.extClickListener = extClickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View view = recyclerView.findChildViewUnder(e.getX(),e.getY());

                    if(view!=null && extClickListener!=null){
                        extClickListener.onLongClick(view, recyclerView.getChildAdapterPosition(view));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View view = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

            if(view!=null && extClickListener!=null && gestureDetector.onTouchEvent(motionEvent)){
                extClickListener.onClick(view, recyclerView.getChildAdapterPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
            //TODO ...
        }

    }

    public static interface ExtClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    // Container Activity must implement this interface
    public interface OnTouchItemSelectedListener {
        public void onTouchItemSelected(View view, int position);
    }
}
