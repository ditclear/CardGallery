package vienan.app.cardgallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.gpuimage.extend.FilterManager;
import org.lasque.tusdk.core.gpuimage.extend.FilterManager.FilterManagerDelegate;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.base.TuSdkComponent;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity{

    @Bind(R.id.bt_addImg)
    Button addImg;
    @Bind(R.id.bt_capImg)
    Button capImg;
    @Bind(R.id.iv_img)
    ImageView img;
    // 组件委托
    TuSdkComponent.TuSdkComponentDelegate delegate = new TuSdkComponent.TuSdkComponentDelegate()
    {
        @Override
        public void onComponentFinished(TuSdkResult result, Error error,
                                        TuFragment lastFragment)
        {
            img.setImageBitmap(result.image);
            TLog.d("onEditMultipleComponentReaded: %s | %s", result, error);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.pink);
        ButterKnife.bind(this);
        initView();
    }
    @OnClick(R.id.bt_addImg)
    public void addImg(){
        EditMultipleComponentSimple multipleComponentSimple=new EditMultipleComponentSimple();
        multipleComponentSimple.delegate=this.delegate;
        multipleComponentSimple.showSimple(this);
    }
    @OnClick(R.id.bt_capImg)
    public void capImg(){
        new CameraAndEditCutSimple().showSimple(this);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private void initView() {
        // 异步方式初始化滤镜管理器
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        TuProgressHub.setStatus(this, TuSdkContext.getString("lsq_initing"));
        TuSdk.checkFilterManager(mFilterManagerDelegate);
    }

    private FilterManagerDelegate mFilterManagerDelegate = new FilterManagerDelegate() {
        @Override
        public void onFilterManagerInited(FilterManager manager) {
            TuProgressHub.showSuccess(MainActivity.this,
                    TuSdkContext.getString("lsq_inited"));
        }
    };
    /**
     * 点击返回键退出程序
     */
    private static Boolean isExit = false;
    private Handler mHandler = new Handler();
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }
}
