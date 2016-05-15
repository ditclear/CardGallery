package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.andtinder.R.color;
import com.andtinder.model.CardModel;
import com.andtinder.view.BaseCardStackAdapter;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MyCardStackAdapter extends BaseCardStackAdapter {
    private final Context mContext;
    private final Object mLock = new Object();
    private ArrayList<CardModel> mData;

    public MyCardStackAdapter(Context context) {
        this.mContext = context;
        this.mData = new ArrayList();
    }

    public MyCardStackAdapter(Context context, Collection<? extends CardModel> items) {
        this.mContext = context;
        this.mData = new ArrayList(items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout wrapper = (FrameLayout)convertView;
        FrameLayout innerWrapper;
        View cardView;
        if(wrapper == null) {
            wrapper = new FrameLayout(this.mContext);
            if(this.shouldFillCardBackground()) {
                innerWrapper = new FrameLayout(this.mContext);
                innerWrapper.setBackgroundColor(this.mContext.getResources().getColor(color.card_bg));
                wrapper.addView(innerWrapper);
            } else {
                innerWrapper = wrapper;
            }

            cardView = this.getCardView(position, this.getCardModel(position), (View)null, parent);
            innerWrapper.addView(cardView);
        } else {
            if(this.shouldFillCardBackground()) {
                innerWrapper = (FrameLayout)wrapper.getChildAt(0);
            } else {
                innerWrapper = wrapper;
            }

            cardView = innerWrapper.getChildAt(0);
            View convertedCardView = this.getCardView(position, this.getCardModel(position), cardView, parent);
            if(convertedCardView != cardView) {
                wrapper.removeView(cardView);
                wrapper.addView(convertedCardView);
            }
        }

        return wrapper;
    }

    protected abstract View getCardView(int var1, CardModel var2, View var3, ViewGroup var4);

    public boolean shouldFillCardBackground() {
        return true;
    }

    public void add(CardModel item) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mData.add(item);
        }

        this.notifyDataSetChanged();
    }

    public CardModel pop() {
        Object var2 = this.mLock;
        CardModel model;
        synchronized(this.mLock) {
            model = (CardModel)this.mData.remove(this.mData.size() - 1);
        }

        this.notifyDataSetChanged();
        return model;
    }

    public Object getItem(int position) {
        return this.getCardModel(position);
    }

    public CardModel getCardModel(int position) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            return (CardModel)this.mData.get(this.mData.size() - 1 - position);
        }
    }

    public int getCount() {
        return this.mData.size();
    }

    public long getItemId(int position) {
        return (long)this.getItem(position).hashCode();
    }

    public Context getContext() {
        return this.mContext;
    }
}
