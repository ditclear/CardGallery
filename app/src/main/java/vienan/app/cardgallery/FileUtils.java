package vienan.app.cardgallery;

import android.os.Environment;

import java.io.File;

/**
 * Created by vienan on 2015/8/18.
 */
public class FileUtils {
    public final static String path= Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator+"DCIM/Camera";
}
