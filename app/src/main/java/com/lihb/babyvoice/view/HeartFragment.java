package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.HeartAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.RemovedRecyclerView;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.utils.CommonToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhb on 2017/2/8.
 */

public class HeartFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private RemovedRecyclerView mRecyclerView;
    private HeartAdapter mHeartAdapter;
    private List<BabyVoice> mData = new ArrayList<>();
    private boolean hasMoreData = false;
    private View emptyView;
    private ImageView mImgGoToRecord;

    private VoiceRecordFragment mVoiceRecordFragment;

    public static HeartFragment create() {
        return new HeartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            showBottomTab();
        }
    }

    private void showBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.VISIBLE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.VISIBLE);
    }

    private void initView() {
        emptyView = getView().findViewById(R.id.empty_root_view);

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.heart_refreshlayout);
        mRecyclerView = (RemovedRecyclerView) getView().findViewById(R.id.heart_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setEmptyView(emptyView);

        mHeartAdapter = new HeartAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mHeartAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                getData(true);
            }
        });
        mRefreshLayout.registerLoadMoreListenerForChildView(mRecyclerView, new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if (hasMoreData) {
                    getData(false);
                } else {
                    CommonToast.showShortToast("加载完毕");
                }
                mRefreshLayout.setLoading(false);
            }
        });
        mRecyclerView.setOnItemClickListener(new RemovedRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BabyVoice voice = mData.get(position);
                CommonToast.showShortToast(voice.name + " " + voice.date + " " + voice.duration);
            }

            @Override
            public void onDeleteClick(int position) {
                mHeartAdapter.removeItem(position);
            }
        });

        mImgGoToRecord = (ImageView) getView().findViewById(R.id.img_goto_record);
        mImgGoToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoVoiceRecordFragment();
            }
        });
        getData(true);
    }

    private void gotoVoiceRecordFragment() {
        if (null == mVoiceRecordFragment) {
            mVoiceRecordFragment = VoiceRecordFragment.create();
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
        transaction.add(R.id.main_layout, mVoiceRecordFragment, "VoiceRecordFragment")
                .show(mVoiceRecordFragment)
                .addToBackStack(null)
                .commit();

    }

    private void getData(final boolean refresh) {
        if (refresh) {
            mData.clear();
        }
        for (int i = 0; i < 10; i++) {
            BabyVoice babyVoice = new BabyVoice("录音" + mData.size(), "2017/01/03", "00:02:50");
            mData.add(babyVoice);
        }
        mHeartAdapter.notifyDataSetChanged();
        hasMoreData = mData.size() < 50;
        onLoadedLessons(refresh);
    }

    private void onLoadedLessons(final boolean refresh) {
        if (refresh) {
            mRefreshLayout.setRefreshing(false);
        } else {
            mRefreshLayout.setLoading(false);
        }
    }


//    public static void main(String[] args) {
//        for (int i = 2; i < 10000; i++) {
//            if (((i - 1) % 2 == 0) && (i % 3 == 0) && ((i - 1) % 4 == 0) && ((i + 1) % 5 == 0) &&
//                    ((i - 3) % 6 == 0) && ((i) % 7 == 0) && ((i - 1) % 8 == 0) && ((i) % 9 == 0)) {
//                System.out.println(i);
//            }
//        }
//    }

}
