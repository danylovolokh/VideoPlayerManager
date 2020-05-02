package com.volokh.danylo.videolist.visibility_demo.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.visibility_demo.adapter.VisibilityUtilsAdapter;
import com.volokh.danylo.videolist.visibility_demo.adapter.items.VisibilityItem;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by danylo.volokh on 08.01.2016.
 */
public class VisibilityUtilsFragment extends Fragment implements VisibilityItem.ItemCallback {

    public interface VisibilityUtilsCallback{
        void setTitle(String title);
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VisibilityUtilsCallback mVisibilityUtilsCallback;

    private List<VisibilityItem> mList = new ArrayList<>(Arrays.asList(
            new VisibilityItem("1", this),
            new VisibilityItem("2", this),
            new VisibilityItem("3", this),
            new VisibilityItem("4", this),
            new VisibilityItem("5", this),
            new VisibilityItem("6", this),
            new VisibilityItem("7", this),
            new VisibilityItem("8", this),
            new VisibilityItem("9", this),
            new VisibilityItem("10", this)));

    private final ListItemsVisibilityCalculator mListItemVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private ItemsPositionGetter mItemsPositionGetter;

    private int mScrollState;
    private Toast mToast;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVisibilityUtilsCallback = (VisibilityUtilsCallback)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mVisibilityUtilsCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.visibility_utils_activity, container, false);

        mRecyclerView = (RecyclerView)root.findViewById(R.id.visibility_demo_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        VisibilityUtilsAdapter adapter = new VisibilityUtilsAdapter(mList);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mListItemVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });

        adapter.notifyDataSetChanged();


        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void makeToast(String text) {
        if(mToast != null){
            mToast.cancel();
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @Override
    public void onActiveViewChangedActive(View newActiveView, int newActiveViewPosition) {
        mVisibilityUtilsCallback.setTitle("Active view at position " + newActiveViewPosition);
    }
}
