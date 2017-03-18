package com.wx.wxrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import static com.wx.wxrecyclerview.RefreshLayout.RefreshState.PULL_DOWN;
import static com.wx.wxrecyclerview.RefreshLayout.RefreshState.PULL_TO_REFRESH;
import static com.wx.wxrecyclerview.RefreshLayout.RefreshState.PULL_UP;
import static com.wx.wxrecyclerview.RefreshLayout.RefreshState.REFRESHING;
import static com.wx.wxrecyclerview.RefreshLayout.RefreshState.RELEASE_TO_REFRESH;

/**
 * Created by wangxuan.
 */

public abstract class RefreshLayout<T extends RecyclerView> extends LinearLayout{

    protected T mRecyclerView;

    private LayoutInflater mInflater;

    private View mHeaderView,mFooterView;

    private int mDownMotionY;

    private Handler mHandler;

    private Context mContext;

    private AttributeSet mAttrs;

    private int mHeaderViewHeight,mFooterViewHeight;

    private RefreshState mGlobalState,mHeaderState,mFooterState;

    private OnHeaderRefreshListener mHeaderRefreshListener;

    private OnFooterRefreshListener mFooterRefreshListener;

    public abstract int getHeaderLayoutId();

    public abstract int getFooterLayoutId();

    public RefreshLayout(Context context) {
        this(context,null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        mHandler = new Handler();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mAttrs = attrs;
        addHeadOrFootView(true,mContext,mAttrs);                                    //add header
        mRecyclerView = createRecyclerView(context,attrs);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);    //remove luminous effect
        addView(mRecyclerView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addHeadOrFootView(false,mContext,mAttrs);
    }

    private void addHeadOrFootView(boolean isHeader,Context context, AttributeSet attrs) {
        try {
            if (isHeader) {
                mHeaderView = null;
                LayoutParams params = null;
                if (getHeaderLayoutId() > 0) {
                    mHeaderView = mInflater.inflate(getHeaderLayoutId(),this,false);
                    measureView(mHeaderView);
                    mHeaderViewHeight = mHeaderView.getMeasuredHeight();
                    params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mHeaderViewHeight);
                    params.topMargin = -mHeaderViewHeight;
                } else {
                    mHeaderView = new View(mContext,mAttrs);
                    measureView(mHeaderView);
                    mHeaderViewHeight = 0;
                    params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mHeaderViewHeight);
                    params.topMargin = -mHeaderViewHeight;
                }
                params.topMargin = -mHeaderViewHeight;
                addView(mHeaderView,params);
            }
            if (!isHeader && getFooterLayoutId()>0) {
                mFooterView = mInflater.inflate(getFooterLayoutId(),this,false);
                measureView(mFooterView);
                mFooterViewHeight = mFooterView.getMeasuredHeight();
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
                addView(mFooterView, params);
            }
        } catch (Exception e) {
            if (isHeader) {
                mHeaderView = null;
            } else {
                mFooterView = null;
            }
        }
    }

    private void measureView(View view){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params==null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            //Is not specified size, this situation is not much, usually the parent control is AdapterView
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:           //load cannot be clicked
                if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);        //Not just return true false,See source
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int y = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mDownMotionY;
                if (isWantScroll(deltaY)) {
                    return true;    //Do you need to pass the event down
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mDownMotionY;
                if (mGlobalState==PULL_DOWN && mHeaderViewHeight != 0) {
                    headerPrepareToRefresh(deltaY);
                } else if (mGlobalState==PULL_UP && mFooterViewHeight != 0) {
                    footerPrepareToRefresh(deltaY);
                }
                mDownMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderViewTopMargin ();
                if (mGlobalState==PULL_DOWN && mHeaderViewHeight != 0) {
                    if (topMargin >= 0) {       //refreshing
                        refreshing(true);
                    } else {                    //restore
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                } else if (mGlobalState == PULL_UP) {
                    if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
                        refreshing(false);
                    } else {
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void headerPrepareToRefresh(int deltaY) {
        int topMargin = dealtScrollMargin(deltaY);
        if (topMargin>=0 && mHeaderState != RELEASE_TO_REFRESH) {
            if (mHeaderRefreshListener != null && mHeaderViewHeight != 0) {
                mHeaderRefreshListener.onRealseRefresh();
            }
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (topMargin<0 && topMargin>-mHeaderViewHeight && mHeaderState != PULL_TO_REFRESH) {
            if (mHeaderRefreshListener != null && mHeaderViewHeight != 0) {
                mHeaderRefreshListener.onPullupRefresh();
            }
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    private void footerPrepareToRefresh(int deltaY) {
        int topMargin = dealtScrollMargin(deltaY);
        if (Math.abs(topMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
            if (mFooterRefreshListener != null && mFooterView != null) {
                mFooterRefreshListener.onRealseRefresh();
            }
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(topMargin) < (mHeaderViewHeight + mFooterViewHeight) && mFooterState != PULL_TO_REFRESH) {
            if (mFooterRefreshListener != null && mFooterView != null) {
                mFooterRefreshListener.onPullupRefresh();
            }
            mFooterState = PULL_TO_REFRESH;
        }
    }

    private int dealtScrollMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float topMargin = params.topMargin+deltaY*0.3f;
        if (deltaY >= 0 && mGlobalState == PULL_UP && Math.abs(params.topMargin) <= mHeaderViewHeight) {
            return params.topMargin;
        }
        if (deltaY <= 0 && mGlobalState == PULL_DOWN && -params.topMargin >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) topMargin;
        mHeaderView.setLayoutParams(params);
        return params.topMargin;
    }

    private boolean isWantScroll(int deltaY) {

        if (mHeaderState== REFRESHING || mFooterState == REFRESHING) {
            return false;
        }

        if (deltaY>=-20 && deltaY<=20) {
            return false;
        }

        if (mRecyclerView != null) {
            if (deltaY>0 && mHeaderViewHeight != 0) {
                View view = mRecyclerView.getChildAt(0);
                if (view == null)
                    return false;
                if (isScrollTop() && view.getTop()==0) {    //begin pull down
                    mGlobalState = PULL_DOWN;
                    return true;
                }
                int top = view.getTop();
                int padding = mRecyclerView.getPaddingTop();
                if (isScrollTop() && Math.abs(top-padding)<=8) {
                    mGlobalState = PULL_DOWN;
                    return true;
                }
            } else if (deltaY<=0 && mFooterViewHeight != 0){
                View view = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1);
                if (view == null)
                    return false;
                if (view.getBottom() <= getHeight() && isScrollBottom()) {
                    mGlobalState = PULL_UP;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isScrollTop() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isScrollBottom() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (linearLayoutManager.findLastVisibleItemPosition() == (mRecyclerView.getAdapter().getItemCount() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    private void setHeaderTopMargin(int topMargin) {

        ValueAnimator animator = ValueAnimator.ofInt(getHeaderViewTopMargin (),topMargin);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
                params.topMargin = (int) animation.getAnimatedValue();
                mHeaderView.setLayoutParams(params);
            }
        });
        animator.start();

    }

    private int getHeaderViewTopMargin () {
        return  ((LayoutParams) mHeaderView.getLayoutParams()).topMargin;
    }

    private void refreshing(boolean isHeader) {
        if (isHeader) {
            mHeaderState = REFRESHING;
            setHeaderTopMargin(0);
            if (mHeaderRefreshListener != null && mHeaderViewHeight != 0) {
                mHeaderRefreshListener.onHeaderRefresh();
            }
        } else {
            mFooterState = REFRESHING;
            int top = - (mHeaderViewHeight+mFooterViewHeight);
            setHeaderTopMargin(top);
            if (mFooterRefreshListener != null && mFooterView != null) {
                mFooterRefreshListener.onFooterRefresh();
            }
        }
    }

    protected abstract T createRecyclerView(Context context, AttributeSet attrs);

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void onHeaderRefreshComplete() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
        if (mHeaderViewHeight != 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setHeaderTopMargin(-mHeaderViewHeight);
                }
            },400);
        }
        mHeaderState = PULL_DOWN;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void onFooterRefreshComplete() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setHeaderTopMargin(-mHeaderViewHeight);
            }
        },400);
        mFooterState = PULL_UP;
    }

    public enum RefreshState{
        PULL_UP,PULL_DOWN,PULL_TO_REFRESH,RELEASE_TO_REFRESH,REFRESHING
    }

    public void setFooterRefreshListener(OnFooterRefreshListener mFooterRefreshListener) {
        this.mFooterRefreshListener = mFooterRefreshListener;
        mFooterRefreshListener.initView(mFooterView);
    }

    public void setHeaderRefreshListener(OnHeaderRefreshListener mHeaderRefreshListener) {
        this.mHeaderRefreshListener = mHeaderRefreshListener;
        mHeaderRefreshListener.initView(mHeaderView);
    }

    public interface OnHeaderRefreshListener{
        void initView(View view);
        void onHeaderRefresh();
        void onRealseRefresh();
        void onPullupRefresh();
    }

    public interface OnFooterRefreshListener{
        void initView(View view);
        void onFooterRefresh();
        void onRealseRefresh();
        void onPullupRefresh();
    }

}
