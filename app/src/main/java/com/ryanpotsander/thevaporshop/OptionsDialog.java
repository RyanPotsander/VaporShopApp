package com.ryanpotsander.thevaporshop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Home on 8/28/2015.
 */
public class OptionsDialog extends BaseDialogFragment implements View.OnClickListener{
    private static final String TAG = OptionsDialog.class.getSimpleName();
    private static final String ADD_TAG = "add";
    private static final String CLOSE_TAG = "close";
    private static final String DELETE_TAG = "delete";
    private static final String EDIT_TAG = "edit";
    private static final String POSITION_KEY = "position";
    private static final String ID_KEY= "item_id";
    private Button add, close, delete, edit;

    public OptionsDialog(){}

    public static OptionsDialog newInstance(int position, long id){
        OptionsDialog fragment = new OptionsDialog();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        args.putLong(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_options, container, false);

        add = (Button)view.findViewById(R.id.btn_add_options);
        add.setOnClickListener(this);
        close = (Button)view.findViewById(R.id.btn_close_option);
        close.setOnClickListener(this);
        delete = (Button)view.findViewById(R.id.btn_delete_options);
        delete.setOnClickListener(this);
        edit = (Button)view.findViewById(R.id.btn_edit_options);
        edit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        mDialogListener.onOptionItemSelected(v.getId(), getArguments().getInt(POSITION_KEY), getArguments().getLong(ID_KEY));
        dismiss();
    }
}
