package com.github.ksoichiro.android.observablescrollview.samples;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;

public class ViewPagerTab2RecyclerViewFragment extends Fragment implements ObservableScrollViewCallbacks {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        Activity parentActivity = getActivity();
        final ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        recyclerView.setHasFixedSize(false);
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i <= 100; i++) {
            items.add("Item " + i);
        }
        View headerView = LayoutInflater.from(parentActivity).inflate(R.layout.padding, null);
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(parentActivity, items, headerView));

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
            final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
            ViewTreeObserver vto = recyclerView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    recyclerView.scrollVerticallyToPosition(initialPosition);
                }
            });
        }
        recyclerView.setScrollViewCallbacks(this);
        return view;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ((ViewPagerTab2Activity) getActivity()).onScrollChanged(scrollY, firstScroll, dragging);
    }

    @Override
    public void onDownMotionEvent() {
        ((ViewPagerTab2Activity) getActivity()).onDownMotionEvent();
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ((ViewPagerTab2Activity) getActivity()).onUpOrCancelMotionEvent(scrollState);
    }
}
