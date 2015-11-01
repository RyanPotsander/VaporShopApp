package com.ryanpotsander.thevaporshop;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Home on 10/17/2015.
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder>{
    private static String TAG = RvAdapter.class.getSimpleName();

    private DataHelper mDataHelper;

    private ArrayList<ItemHelper> mItemHelpers;

    public RvAdapter(DataHelper dataHelper){
        mDataHelper = dataHelper;
        mItemHelpers = dataHelper.getDefaultList();
    }

    //viewType is position
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemHelper helper = mDataHelper.getItem(position);

        ItemHelper.ParentItemParams parentItemParams = helper.getParentItemParams();

        if(parentItemParams.floatTotal >= 200 && !holder.rewardImage.isShown()){
            holder.rewardImage.setVisibility(View.VISIBLE);
        }

        if (isBirthday(parentItemParams.birthMonth, parentItemParams.birthDay) && !holder.birthdayImage.isShown()){
            holder.birthdayImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mItemHelpers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private boolean isBirthday(String month, int year){
        Calendar c = Calendar.getInstance();
        return (month.equals(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)) && year == c.get(Calendar.YEAR));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameView, emailView, totalView;
        public ImageView birthdayImage;
        public ImageView rewardImage;
        public ImageButton settingsButton;
        public LinearLayout purchaseHistoryView;

        public ViewHolder(View view){
            super(view);

            nameView = (TextView)view.findViewById(R.id.text_name_test);
            emailView = (TextView)view.findViewById(R.id.text_email_test);
            totalView = (TextView)view.findViewById(R.id.text_total_test_num);

            purchaseHistoryView = (LinearLayout)view.findViewById(R.id.child_view);

            settingsButton = (ImageButton)view.findViewById(R.id.button_settings_test);
            settingsButton.setColorFilter(Color.parseColor("#F44336"));

            birthdayImage = (ImageView)view.findViewById(R.id.image_birthday);
            birthdayImage.setVisibility(View.GONE);

            rewardImage = (ImageView)view.findViewById(R.id.image_reward);
            rewardImage.setVisibility(View.GONE);
        }
    }
}
