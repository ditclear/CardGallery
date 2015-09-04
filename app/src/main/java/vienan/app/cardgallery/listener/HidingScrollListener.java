package vienan.app.cardgallery.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Created by vienan on 2015/8/23.
 */
public abstract class HidingScrollListener implements OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;
    private int dy;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int lvPosition=0;
        switch (scrollState){
            case SCROLL_STATE_TOUCH_SCROLL:
                lvPosition=view.getLastVisiblePosition();
                break;
            case SCROLL_STATE_IDLE:
                int position=view.getLastVisiblePosition();
                dy=position-lvPosition;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            if(!mControlsVisible) {
                onShow();
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                onHide();
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                onShow();
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        }
        if((mControlsVisible && dy>0) || (!mControlsVisible && dy<0)) {
            mScrolledDistance += dy;
        }

    }

    public abstract void onHide();
    public abstract void onShow();
}
