package vienan.app.cardgallery.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import vienan.app.cardgallery.R;

/**
 * Created by vienan on 2015/8/26.
 */
public class ImageUtils {
    public static DisplayImageOptions options=new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_image_48pt_3x)
            .showImageOnFail(R.mipmap.ic_broken_image_black_36dp)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();
    public static ImageLoader imageLoader=ImageLoader.getInstance();
}
