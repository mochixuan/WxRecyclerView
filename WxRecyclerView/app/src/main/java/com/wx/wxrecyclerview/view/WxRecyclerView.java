package com.wx.wxrecyclerview.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.wx.wxrecyclerview.R;

/**
 * Created by wangxuan
 */

public class WxRecyclerView extends RefreshLayout<RecyclerView>{

    @Override
    public int getHeaderLayoutId() {
        return R.layout.item_header;
    }

    @Override
    public int getFooterLayoutId() {
        return R.layout.item_footer;
    }

    public WxRecyclerView(Context context) {
        this(context,null);
    }

    public WxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected RecyclerView createRecyclerView(Context context, AttributeSet attrs) {
        return new RecyclerView(context,attrs);
    }

}
