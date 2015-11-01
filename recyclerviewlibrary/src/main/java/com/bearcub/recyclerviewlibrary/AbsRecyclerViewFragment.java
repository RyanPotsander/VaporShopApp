package com.bearcub.recyclerviewlibrary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Home on 6/11/2015.
 */
public abstract class AbsRecyclerViewFragment extends BaseRecyclerFragment {

    public class ExtRecyclerViewAdapter extends RecyclerView.Adapter<ExtRecyclerViewAdapter.MyViewHolder> {
        private final LayoutInflater inflater;
        public List<AbsItem> list = Collections.emptyList();
        Context context;

        public ExtRecyclerViewAdapter(Context context, List<AbsItem> list){
            inflater= LayoutInflater.from(context);
            this.list = list;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = inflater.inflate(getViewHolderLayoutId(), viewGroup, false);

            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            implementOnBindVH(viewHolder, position);
        }

        @Override
        public int getItemCount() {
            return implementGetItemCount();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView label;

            public MyViewHolder(View itemView) {
                super(itemView);
                label = (TextView)itemView.findViewById(getTextViewId());
                //itemView.setTag();
            }
        }
    }

    public class AbsItem{
        public String label;
    }

    public abstract List<AbsItem> getRecyclerViewData();
    public abstract int getViewHolderLayoutId();
    public abstract int getTextViewId();
    public abstract void implementOnBindVH(ExtRecyclerViewAdapter.MyViewHolder viewHolder, int position);
    public abstract int implementGetItemCount();
}
