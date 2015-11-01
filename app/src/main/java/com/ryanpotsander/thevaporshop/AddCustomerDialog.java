package com.ryanpotsander.thevaporshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Home on 8/9/2015.
 */
public class AddCustomerDialog extends BaseDialogFragment {
    private static final String TAG = AddCustomerDialog.class.getSimpleName();
    private static final String SHARED_PREFERENCES_FILE_NAME = "shared_preferences";
    private static final String LAST_CUSTOMER_NUMBER_KEY = "last_customer_number";
    TextInputLayout textInputFirstName, textInputLastName, textInputEmail, textInputEmail2,
            textInputBirthMonth, textInputBirthDay;

    public AddCustomerDialog(){} // required empty constructor

    public static AddCustomerDialog newInstance(){
        return new AddCustomerDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        setHasOptionsMenu(true);

        textInputFirstName = (TextInputLayout)view.findViewById(R.id.text_input_layout_first_name);
        textInputLastName = (TextInputLayout)view.findViewById(R.id.text_input_layout_last_name);
        textInputEmail = (TextInputLayout)view.findViewById(R.id.text_input_layout_email);
        textInputEmail2 = (TextInputLayout)view.findViewById(R.id.text_input_layout_email_2);
        textInputBirthMonth = (TextInputLayout)view.findViewById(R.id.text_input_layout_birthmonth);
        textInputBirthDay = (TextInputLayout)view.findViewById(R.id.text_input_layout_birthday);

        Button ok = (Button)view.findViewById(R.id.button_ok_fragment); //todo error handling
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });

        Button cxl = (Button)view.findViewById(R.id.button_cancel_fragment);
        cxl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void setTextInputError(TextInputLayout view){
        String error = "Required";
        view.setError(error);
        view.setErrorEnabled(true);
    }

    public void writeToSharedPreferences(Context context, String key, int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int readFromSharedPreferences(Context context, String key, int defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defValue);
    }

    private String getTextInputValue(TextInputLayout parent) throws NullPointerException{
        EditText view = parent.getEditText();
        try{
            return view.getText().toString();
        }catch (NullPointerException e){
            return null;
        }
    }

    public void add(){
        ItemHelper helper = ItemHelper.getTempHelper();
        ItemHelper.ParentItemParams item = helper.getParentItemHolder() ;

        item.firstName = getTextInputValue(textInputFirstName);
        if(item.firstName == null || item.firstName.length() < 1){
            setTextInputError(textInputFirstName);
            return;
        }

        item.lastName = getTextInputValue(textInputLastName);
        if(item.lastName == null || item.lastName.length() < 1){
            setTextInputError(textInputLastName);
            return;
        }

        String emailStart = getTextInputValue(textInputEmail);
        String emailEnd = getTextInputValue(textInputEmail2);
        if (emailStart == null || emailStart.length() < 1 || emailEnd == null || emailEnd.length() < 1){
            item.email = "Not Provided";
        }else{
            item.email = emailStart + "@" + emailEnd;
        }

        String monthText = getTextInputValue(textInputBirthMonth);
        if (monthText == null || monthText.length() < 1){
            item.birthMonth = "n/a";
        }else {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, Integer.valueOf(monthText) -1);
            item.birthMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        }

        String dayText = getTextInputValue(textInputBirthDay);
        if (dayText == null || dayText.length() < 1){
            item.birthDay = 0;
        }else {
            item.birthDay = Integer.valueOf(dayText);
        }

        mDialogListener.onCursorTaskSelected(TaskParams.addTaskParams(item));

        Toast.makeText(getActivity(), "Touch card to add purchase", Toast.LENGTH_LONG).show();

        dismiss();
    }
}
