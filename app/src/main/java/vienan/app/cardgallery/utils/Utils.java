package vienan.app.cardgallery.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.Toast;

import vienan.app.cardgallery.R;

/**
 * Created by vienan on 2015/10/11.
 */
public class Utils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
    public static void toast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
