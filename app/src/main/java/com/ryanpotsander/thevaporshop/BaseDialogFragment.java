package com.ryanpotsander.thevaporshop;

import android.app.Activity;
import android.app.DialogFragment;

/**
 * Created by Home on 9/2/2015.
 */
public class BaseDialogFragment extends DialogFragment {
    public ActivityCallback mDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mDialogListener = (ActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActivityCallback");
        }
    }
}
