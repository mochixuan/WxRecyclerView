package com.wx.recyclerviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.wx.recyclerviewdemo.adapter.BaseAdapter;
import com.wx.recyclerviewdemo.view.WxRecyclerView1;

import java.util.ArrayList;
import java.util.List;

public class FooterRefreshActivity extends AppCompatActivity {

    private List<String> mLists;
    private WxRecyclerView1 mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer_refresh);
        init();
    }

    private void init() {
        mLists = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mLists.add("second data : " + i);
        }

        mRecyclerView = (WxRecyclerView1) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final BaseAdapter mAdapter = new BaseAdapter(mLists);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLists.add(0,"my name is Header7");
                        mLists.add(0,"my name is Header6");
                        mLists.add(0,"my name is Header5");
                        mLists.add(0,"my name is Header4");
                        mLists.add(0,"my name is Header3");
                        mLists.add(0,"my name is Header2");
                        mLists.add(0,"my name is Header1");
                        mAdapter.notifyItemRangeInserted(0,7);
                        mRecyclerView.onHeaderRefreshComplete();
                        mSwipeRefresh.setRefreshing(false);
                    }
                },3000);
            }
        });

        mRecyclerView.setFooterRefreshListener(new WxRecyclerView1.OnFooterRefreshListener() {

            TextView tvFooter;
            AVLoadingIndicatorView avFooter;

            @Override
            public void initView(View view) {
                tvFooter = (TextView) view.findViewById(R.id.tv_footer);
                avFooter = (AVLoadingIndicatorView) view.findViewById(R.id.av_footer);
            }

            @Override
            public void onFooterRefresh() {
                avFooter.show();
                tvFooter.setVisibility(View.GONE);
                tvFooter.setText("开始刷新");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLists.add("my name is Footer1");
                        mLists.add("my name is Footer2");
                        mLists.add("my name is Footer3");
                        mLists.add("my name is Footer4");
                        mLists.add("my name is Footer5");
                        mLists.add("my name is Footer6");
                        mLists.add("my name is Footer7");
                        mAdapter.notifyItemRangeInserted(mLists.size()-7,7);
                        avFooter.hide();
                        tvFooter.setVisibility(View.VISIBLE);
                        tvFooter.setText("刷新成功");
                        mRecyclerView.onFooterRefreshComplete();
                    }
                },3000);
            }

            @Override
            public void onRealseRefresh() {
                avFooter.hide();
                tvFooter.setText("松开刷新");
            }

            @Override
            public void onPullupRefresh() {
                avFooter.hide();
                tvFooter.setText("上拉刷新");
            }
        });
    }


}
