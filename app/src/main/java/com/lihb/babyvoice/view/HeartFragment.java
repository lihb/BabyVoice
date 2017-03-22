package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.adapter.HeartAdapter;
import com.lihb.babyvoice.command.PickedCategoryCommand;
import com.lihb.babyvoice.customview.PickRecordDialog;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.RemovedRecyclerView;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.model.HttpResList;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
    private VoicePlayFragment mVoicePlayFragment;

    private static final int COUNT = 10;
    private PickRecordDialog mPickCategoryDialog;
    private int mRecordType = PickedCategoryCommand.TYPE_HEART;

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
            getData(true);
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

        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

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
                    return;
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
                gotoVoicePlayFragment(voice);
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
//                gotoVoiceRecordFragment();
                showPickCategoryDialog();
            }
        });
        ((TitleBar) getView().findViewById(R.id.title_bar)).setLeftText("分享测试");
        ((TitleBar) getView().findViewById(R.id.title_bar)).setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        getData(true);
    }

    private void showPickCategoryDialog() {
        if (mPickCategoryDialog == null) {
            mPickCategoryDialog = new PickRecordDialog(getContext(),R.style.loading_dialog);
            mPickCategoryDialog.setContentView(R.layout.pick_category);
            Window window = mPickCategoryDialog.getWindow();
            window.setGravity(Gravity.CENTER);  // dialog显示的位置
            window.setWindowAnimations(R.style.pickCategoryDialogStyle);  //弹出动画
            mPickCategoryDialog.setOnPickRecordDialogListener(new PickRecordDialog.OnPickRecordDialogListener() {
                @Override
                public void onClick(int type) {
                    mRecordType = type;
                    gotoVoiceRecordFragment();
                }
            });
        }
        mPickCategoryDialog.show();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(getActivity());
    }

    private void gotoVoiceRecordFragment() {
        if (null == mVoiceRecordFragment) {
            mVoiceRecordFragment = VoiceRecordFragment.create();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("recordType", mRecordType);
        mVoiceRecordFragment.setArguments(bundle);
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

    private void gotoVoicePlayFragment(BabyVoice babyVoice) {
        if (null == mVoicePlayFragment) {
            mVoicePlayFragment = VoicePlayFragment.create();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("babyVoice", babyVoice);
        mVoicePlayFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
        transaction.add(R.id.main_layout, mVoicePlayFragment, "VoicePlayFragment")
                .show(mVoicePlayFragment)
                .addToBackStack(null)
                .commit();

    }

    private void getData(final boolean refresh) {
        int start = 0;
        if (refresh) {
            start = 0;
        } else {
            start = mData.size();
        }
        ServiceGenerator.createService(ApiManager.class)
                .getBabyVoiceRecord(start, COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse<HttpResList<BabyVoice>>>() {
                    @Override
                    public void call(HttpResponse<HttpResList<BabyVoice>> httpResListHttpResponse) {
                        if (httpResListHttpResponse.code == 200) {
                            HttpResList<BabyVoice> httpResList = httpResListHttpResponse.data;
                            if (refresh) {
                                mData.clear();
                            }
                            hasMoreData = mData.size() < httpResList.total;
                            List<BabyVoice> list = httpResList.dataList;

                            mData.addAll(list);
                            mHeartAdapter.notifyDataSetChanged();
                            onLoadedData(refresh);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("获取数据失败");
                        Logger.e(throwable.toString());
                        onLoadedData(refresh);
                    }
                });

    }

    private void onLoadedData(final boolean refresh) {
        if (refresh) {
            mRefreshLayout.setRefreshing(false);
        } else {
            mRefreshLayout.setLoading(false);
        }
    }

}
