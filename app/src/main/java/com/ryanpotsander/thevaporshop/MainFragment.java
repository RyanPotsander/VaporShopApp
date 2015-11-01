package com.ryanpotsander.thevaporshop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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

public class MainFragment extends BaseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    public RecyclerView mRecyclerView;
    public RecyclerViewCursorAdapter adapter;
    public FrameLayout expandedView = null;
    public CardView lastView = null;

    public MainFragment(){}

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        DbConnection.init(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_main_fragment);
        initRecyclerView();

        //mFragmentListener.onCursorTaskSelected(TaskParams.getCursorsTaskParams());
        mFragmentListener.setTestCursors();

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        initActionButtons(fab);



        return view;
    }

    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new Animator());
        adapter = new RecyclerViewCursorAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new ExtTouchListener(getActivity(), mRecyclerView, new ExtClickListener() {
            @Override
            public void onClick(View view, int position, MotionEvent e) {
                lastView = (CardView) view;
                RecyclerViewCursorAdapter.ViewHolder holder =  (RecyclerViewCursorAdapter.ViewHolder)
                        mRecyclerView.getChildViewHolder(view);
                long id = holder.parentId;

                mFragmentListener.showOptionsDialog(position, id);
            }

            @Override
            public void onLongClick(View view, int position) {
                //long click an expanded view
                if (lastView != null && lastView == view) {
                }
            }
        }));
    }

    private void initActionButtons(FloatingActionButton fab){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.showAddCustomerFragment();
            }
        });
    }

    public void deleteItem(int position){
        mFragmentListener.onCursorTaskSelected(TaskParams.deleteTaskParams(adapter.getItemId(position), position));
    }
 }

