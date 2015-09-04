package vienan.app.cardgallery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import vienan.app.cardgallery.MyApplication;

/**
 * Created by vienan on 2015/8/24.
 */
public class CanaroTextView extends TextView {
    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(MyApplication.canaroExtraBold);
    }

}
