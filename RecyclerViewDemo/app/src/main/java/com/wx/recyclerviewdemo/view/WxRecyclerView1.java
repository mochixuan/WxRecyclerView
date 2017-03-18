package com.wx.recyclerviewdemo.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.wx.recyclerviewdemo.R;
import com.wx.wxrecyclerview.RefreshLayout;

/**
 * Created by wangxuan.
 */

public class WxRecyclerView1 extends RefreshLayout<RecyclerView> {

    @Override
    public int getHeaderLayoutId() {
        return 0;
    }

    @Override
    public int getFooterLayoutId() {
        return R.layout.item_footer;
    }

    public WxRecyclerView1(Context context) {
        this(context,null);
    }

    public WxRecyclerView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected RecyclerView createRecyclerView(Context context, AttributeSet attrs) {
        return new RecyclerView(context,attrs);
    }

}
