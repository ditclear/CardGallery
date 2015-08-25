package vienan.app.cardgallery;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.melnykov.fab.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.yalantis.guillotine.animation.GuillotineAnimation;

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
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private final static int TO_EDIT = 1;
    private static final String[] STYLES = new String[]{"STANDARD", "GROW", "CARDS", "CURL",
            "WAVE", "FLIP", "FLY", "REVERSE_FLY", "HELIX", "FAN", "TILT", "ZIPPER", "FADE", "TWIRL",
            "SLIDE_IN"};
    @Bind(R.id.content_hamburger)
    ImageView contentHamburger;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.root)
    FrameLayout root;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    JazzyListView list;
    List<CardModel> lists = new ArrayList<CardModel>();
    AlertView alertView;
    View guillotineMenu;
    ListAdapter adapter;
    CardViewAdapter cardViewAdapter;
    List<String> uris = new ArrayList<String>();
    GuillotineAnimation guillotineAnimation;
    private int selectedStyle=JazzyHelper.GROW;
    // 组件委托
    TuSdkComponent.TuSdkComponentDelegate delegate = new TuSdkComponent.TuSdkComponentDelegate() {
        @Override
        public void onComponentFinished(TuSdkResult result, Error error,
                                        TuFragment lastFragment) {
            toEditActivity(result);
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
                    toEditActivity(result);
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


    private void toEditActivity(TuSdkResult result) {
        Log.i("image2", result.toString());
        Log.d("img2", result.imageSqlInfo.path);
        Intent toEditIntent = new Intent(MainActivity.this, EditCardActivity.class);
        toEditIntent.putExtra("imgPath", result.imageSqlInfo.path);
        startActivityForResult(toEditIntent, TO_EDIT);
    }

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initToolbar();
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

    /*private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }*/
    @OnClick(R.id.fab)
    public void addImg() {
        if (guillotineMenu.getVisibility() == View.VISIBLE) {
            return;
        }
        fab.hide(false);
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
                        default:
                            fab.show();

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
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        initGuillotineMenuItem();
        root.addView(guillotineMenu);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu,
                guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .build();
        guillotineAnimation.close();
        // 异步方式初始化滤镜管理器
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        TuProgressHub.setStatus(this, TuSdkContext.getString("lsq_initing"));
        TuSdk.checkFilterManager(mFilterManagerDelegate);
        list = (JazzyListView) findViewById(R.id.list);
        File file = new File(FileUtils.path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                lists = getAll();
            }
        }
        //adapter=new ListAdapter(this, uris);

        cardViewAdapter = new CardViewAdapter(this, R.layout.card_basic_img, lists);
        //list.setAdapter(adapter);
        list.setAdapter(cardViewAdapter);
        list.setTransitionEffect(selectedStyle);
        /*list.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });*/
        fab.attachToListView(list);
    }

    /**
     * 初始化GuillotineMenuItem
     */
    private void initGuillotineMenuItem() {
        LinearLayout profile_group= (LinearLayout) guillotineMenu.findViewById(R.id.profile_group);
        LinearLayout feed_group= (LinearLayout) guillotineMenu.findViewById(R.id.feed_group);
        LinearLayout style_group= (LinearLayout) guillotineMenu.findViewById(R.id.style_group);
        LinearLayout settings_group= (LinearLayout) guillotineMenu.findViewById(R.id.settings_group);
        profile_group.setOnClickListener(this);
        feed_group.setOnClickListener(this);
        style_group.setOnClickListener(this);
        settings_group.setOnClickListener(this);

    }

    private void hideViews() {
        Log.i("view", "hideView");
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

    }

    private void showViews() {
        Log.i("view", "showView");
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TO_EDIT:
                if (resultCode == RESULT_OK) {
                    CardModel model = (CardModel) data.getSerializableExtra("cardModel");
                    lists.add(model);
                    model.save();
                    toast("添加成功");
                    //adapter.notifyDataSetChanged();
                    cardViewAdapter.notifyDataSetChanged();
                    list.invalidate();
                    list.setSelection(lists.size());
                }
        }
    }

    /**
     * 得到返回结果
     */


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

    private List<CardModel> getAll() {
        return new Select()
                .from(CardModel.class)
                .execute();
    }

    private void deleteAll() {
        new Delete().from(CardModel.class).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_group:
                guillotineAnimation.close();
                toast("profile_group");
                break;
            case R.id.feed_group:
                guillotineAnimation.close();
                toast("profile_group");
                break;
            case R.id.style_group:
                View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
                WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(Arrays.asList(STYLES));
                wv.setSeletion(selectedStyle);
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        Log.i("index", "" + selectedIndex);
                        selectedStyle=selectedIndex-2;
                    }
                });

                new AlertDialog.Builder(this)
                        .setTitle("STYLE")
                        .setView(outerView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.setTransitionEffect(selectedStyle);
                                guillotineAnimation.close();
                            }
                        })
                        .show();



                break;
            case R.id.settings_group:
                guillotineAnimation.close();
                toast("profile_group");
                break;
        }
    }
}
