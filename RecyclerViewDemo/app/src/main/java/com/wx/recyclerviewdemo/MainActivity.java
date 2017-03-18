package com.wx.recyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.wx.recyclerviewdemo.adapter.BaseAdapter;
import com.wx.recyclerviewdemo.view.WxRecyclerView;
import com.wx.wxrecyclerview.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mLists;
    private WxRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mLists = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mLists.add("data : " + i);
        }

        mRecyclerView = (WxRecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final BaseAdapter mAdapter = new BaseAdapter(mLists);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                Toast.makeText(MainActivity.this,""+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,FooterRefreshActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView.setHeaderRefreshListener(new RefreshLayout.OnHeaderRefreshListener() {

            TextView tvHeader;
            AVLoadingIndicatorView avHeader;

            @Override
            public void initView(View view) {
                tvHeader = (TextView) view.findViewById(R.id.tv_header);
                avHeader = (AVLoadingIndicatorView) view.findViewById(R.id.av_header);
            }

            @Override
            public void onHeaderRefresh() {
                tvHeader.setText("开始刷新");
                tvHeader.setVisibility(View.GONE);
                avHeader.show();
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
                        avHeader.hide();
                        tvHeader.setVisibility(View.VISIBLE);
                        tvHeader.setText("刷新成功");
                        mRecyclerView.onHeaderRefreshComplete();
                    }
                },3000);
            }

            @Override
            public void onRealseRefresh() {
                avHeader.hide();
                tvHeader.setText("松开刷新");
            }

            @Override
            public void onPullupRefresh() {
                avHeader.hide();
                tvHeader.setText("上拉刷新");
            }
        });

        mRecyclerView.setFooterRefreshListener(new RefreshLayout.OnFooterRefreshListener() {

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
