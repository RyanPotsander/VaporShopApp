package com.ryanpotsander.thevaporshop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Home on 8/14/2015.
 */
public class HistoryDialogFragment extends BaseDialogFragment implements View.OnClickListener{
    private static final String TAG = HistoryDialogFragment.class.getSimpleName();
    private static final String ID_KEY = "itemId";
    private static final String POSITION_TAG = "position";
    Button saveBtn;
    TextInputLayout textInputAmount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_purchase, container, false);

        textInputAmount = (TextInputLayout)view.findViewById(R.id.text_input_dollars);

        saveBtn = (Button)view.findViewById(R.id.button_save_purchase);
        saveBtn.setOnClickListener(this);

        return view;
    }

    public static HistoryDialogFragment newInstance(long id, int position){
        HistoryDialogFragment fragment = new HistoryDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_TAG, position);
        args.putLong(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    private void setTextInputError(TextInputLayout view){
        String error = "Required";
        view.setError(error);
        view.setErrorEnabled(true);
    }

    private String getValueIfExists(EditText view, TextInputLayout parent) throws NullPointerException{ //write base
        try{
            return view.getText().toString();
        }catch (NullPointerException e){
            parent.setErrorEnabled(true);
            return null;
        }
    }

    private String getTextInputValue(TextInputLayout parent) throws NullPointerException{
        EditText view = parent.getEditText();
        try{
            return view.getText().toString();
        }catch (NullPointerException e){
            return null;
        }
    }

    private void addPurchase(){
        String textInputValue = getTextInputValue(textInputAmount);
        if(textInputValue == null){
            setTextInputError(textInputAmount);
            return;
        }

        float floatValue = Float.parseFloat(textInputValue);

        Calendar calendar = Calendar.getInstance();

        ItemKey key = new ItemKey(getArguments().getLong(ID_KEY));
        ItemHelper helper = new ItemHelper(key);

        ItemHelper.ChildItemParams params = helper.getChildItemHolder();

        params.foreignKey = key.getLongKey();
        params.amount = floatValue;
        params.day = calendar.get(Calendar.DAY_OF_MONTH);
        params.month = calendar.get(Calendar.MONTH);

        mDialogListener.onInsertPurchase(params);

        dismiss();
    }



    @Override
    public void onClick(View v) {
        addPurchase();
    }

}
