package com.wx.recyclerviewdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wx.recyclerviewdemo.R;

import java.util.List;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder>{

    private List<String> mLists;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{
        void itemClick(int position);
    }

    public BaseAdapter(List<String> mLists) {
        this.mLists = mLists;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        holder.textView.setText(mLists.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.itemClick(position);
                }
            }
        });
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public BaseViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.tv_view);
        }
    }

}
