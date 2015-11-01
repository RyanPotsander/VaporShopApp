package com.ryanpotsander.thevaporshop;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.bearcub.recyclerviewlibrary.BaseRecyclerFragment;

/**
 * Created by Home on 8/22/2015.
 */
public class BaseFragment extends BaseRecyclerFragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    ActivityCallback mFragmentListener;

    public BaseFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentListener = (ActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActivityCallback");
        }
    }

    public void toggleViewVisibility(View view, CardView parent){
        if(view.isShown()){
            view.setVisibility(View.GONE);
            setAnimation(view, R.anim.slide_up);
            parent.setCardElevation(0);
        }else{
            view.setVisibility(View.VISIBLE);
            setAnimation(view, R.anim.slide_down);
            parent.setCardElevation(parent.getMaxCardElevation());
        }
    }

    public void setAnimation(View view, int animationResource) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), animationResource);
        if (animation != null) {
            animation.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }
    public boolean checkForButtonClick(View button, View parent, MotionEvent e){
        int eventX = (int)e.getX();
        int eventY = (int)e.getY();
        int buttonLeft = button.getLeft() + parent.getLeft();
        int buttonTop = button.getTop() + parent.getTop();
        Rect buttonArea = new Rect(buttonLeft, buttonTop , buttonLeft +
                button.getWidth(), buttonTop + button.getHeight());
        return buttonArea.contains(eventX, eventY);
    }

    public View findViewUnderClick(View itemView, View menuView, View child, MotionEvent e){

        int addTop = menuView.getTop() + itemView.getTop();
        int addLeft = menuView.getLeft() + itemView.getLeft();

        View clicked = null;

        int eventX = (int)e.getX();
        int eventY = (int)e.getY();

        int childX = child.getLeft() + addLeft;
        int childY = child.getTop() + addTop;
        Rect deleteArea = new Rect(childX, childY, childX + child.getWidth(), childY + child.getHeight());
        if(deleteArea.contains(eventX, eventY)){
            clicked = child;
        }

        return clicked;
    }
}
