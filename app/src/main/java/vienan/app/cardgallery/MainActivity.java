package vienan.app.cardgallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.melnykov.fab.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.gpuimage.extend.FilterManager;
import org.lasque.tusdk.core.gpuimage.extend.FilterManager.FilterManagerDelegate;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.base.TuSdkComponent;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutFragment;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {
    @Bind(R.id.fab)
    FloatingActionButton fab;
    JazzyListView list;
    AlertView alertView;
    ListAdapter adapter;
    List<String> uris=new ArrayList<String>();
    // 组件委托
    TuSdkComponent.TuSdkComponentDelegate delegate = new TuSdkComponent.TuSdkComponentDelegate() {
        @Override
        public void onComponentFinished(TuSdkResult result, Error error,
                                        TuFragment lastFragment) {
            loadImage(result);
            TLog.d("onEditMultipleComponentReaded: %s | %s", result, error);
        }
    };
    TuEditTurnAndCutFragment.TuEditTurnAndCutFragmentDelegate mDelegate =
            new TuEditTurnAndCutFragment.TuEditTurnAndCutFragmentDelegate() {
                /**
                 * 图片编辑完成
                 *
                 * @param fragment 旋转和裁剪视图控制器
                 * @param result   旋转和裁剪视图控制器处理结果
                 */
                @Override
                public void onTuEditTurnAndCutFragmentEdited(
                        TuEditTurnAndCutFragment fragment, TuSdkResult result) {
                    if (!fragment.isShowResultPreview()) {
                        fragment.hubDismissRightNow();
                        fragment.dismissActivityWithAnim();
                    }
                    loadImage(result);
                    TLog.d("onTuEditTurnAndCutFragmentEdited: %s", result);
                }

                /**
                 * 图片编辑完成 (异步方法)
                 *
                 * @param fragment 旋转和裁剪视图控制器
                 * @param result   旋转和裁剪视图控制器处理结果
                 * @return 是否截断默认处理逻辑 (默认: false, 设置为True时使用自定义处理逻辑)
                 */
                @Override
                public boolean onTuEditTurnAndCutFragmentEditedAsync(
                        TuEditTurnAndCutFragment fragment, TuSdkResult result) {

                    return false;
                }

                @Override
                public void onComponentError(TuFragment fragment, TuSdkResult result,
                                             Error error) {
                    TLog.d("onComponentError: fragment - %s, result - %s, error - %s",
                            fragment, result, error);
                }
            };

    private void loadImage(TuSdkResult result) {
        Log.i("image2", result.toString());
        Log.d("img2", result.imageSqlInfo.path);
        uris.add(result.imageSqlInfo.path);

        adapter.notifyDataSetChanged();
        list.invalidate();
        list.setSelection(uris.size());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.color.pink)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_main);
        setContentView(helper.createView(this));
        helper.initActionBar(this);*/
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.pink);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.fab)
    public void addImg() {
        if (alertView == null) {
            alertView = new AlertView("选择图片", null, "取消", null,
                    new String[]{"拍照", "从相册中选择"},
                    this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                public void onItemClick(Object o, int position) {
                    switch (position) {
                        case 0:
                            CameraAndEditCutSimple cameraAndEditCutSimple = new CameraAndEditCutSimple();
                            cameraAndEditCutSimple.mDelegate = mDelegate;
                            cameraAndEditCutSimple.showSimple(MainActivity.this);
                            break;
                        case 1:
                            EditMultipleComponentSimple multipleComponentSimple = new EditMultipleComponentSimple();
                            multipleComponentSimple.delegate = delegate;
                            multipleComponentSimple.showSimple(MainActivity.this);
                            break;
                    }
                }
            });
        }
        alertView.show();
    }

    @Override
    protected void onResume() {

        super.onResume();
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
        list= (JazzyListView) findViewById(R.id.list);
        File file=new File(FileUtils.path);
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if (files!=null) {
                for (File f : files) {
                    Log.i("fUri", f.toURI().toString());
                    uris.add(Uri.fromFile(f).toString());
                }
            }
        }
        adapter=new ListAdapter(this, uris);
        list.setAdapter(adapter);
        list.setTransitionEffect(JazzyHelper.WAVE);
        list.setOnScrollListener(null);
        fab.attachToListView(list);
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
