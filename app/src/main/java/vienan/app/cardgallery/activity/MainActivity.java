package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.konifar.fab_transformation.FabTransformation;
import com.melnykov.fab.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vienan.app.cardgallery.adapter.CardAdapter;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.SwipeCardAdapter;
import vienan.app.cardgallery.view.WheelView;
import vienan.app.cardgallery.colorpicker.ColorPickerDialog;
import vienan.app.cardgallery.colorpicker.ColorPickerSwatch;
import vienan.app.cardgallery.utils.FileUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private final static int TO_EDIT = 1;
    private static final String[] STYLES = new String[]{"STANDARD", "GROW", "CARDS", "CURL",
            "WAVE", "FLIP", "FLY", "REVERSE_FLY", "HELIX", "FAN", "TILT", "ZIPPER", "FADE", "TWIRL",
            "SLIDE_IN"};
    @Bind(R.id.toolbar_footer)
    Toolbar toolbarFooter;
    private int[] mColor;
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
    View guillotineMenu;
    SwipeCardAdapter adapter;
    //CardViewAdapter cardViewAdapter;
    CardAdapter cardViewAdapter;
    List<String> uris = new ArrayList<String>();
    GuillotineAnimation guillotineAnimation;
    @Bind(R.id.touch_interceptor_view)
    View mListTouchInterceptor;
    @Bind(R.id.details_layout)
    LinearLayout mDetailsLayout;
    @Bind(R.id.unfoldable_view)
    UnfoldableView mUnfoldableView;
    @Bind(R.id.swipe_card_button)
    ImageView swipeCardButton;
    @Bind(R.id.view_helper)
    View viewHelper;
    @Bind(R.id.header_view_helper)
    View header_helper;
    SharedPreferences preferences;
    private int selectedStyle;
    TextView title;
    TextView description;
    SystemBarTintManager tintManager;
    int mSelectedColor;
    ImageButton ib_capture,ib_album;
    String createDate;
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
        Calendar c=result.imageSqlInfo.createDate;
        Intent toEditIntent = new Intent(MainActivity.this, EditCardActivity.class);
        String createDate=c.get(Calendar.MONTH)+1+"月"
                +c.get(Calendar.DAY_OF_MONTH)+"日";
        toEditIntent.putExtra("createDate",createDate);
        toEditIntent.putExtra("imgPath", result.imageSqlInfo.path);
        toEditIntent.putExtra("theme", mSelectedColor);
        startActivityForResult(toEditIntent, TO_EDIT);
    }

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences=getSharedPreferences("config", MODE_PRIVATE);
        if (mColor == null) {
            mColor = getColorArray();
        }
        mSelectedColor=preferences.getInt("theme",mColor[0]);
        selectedStyle=preferences.getInt("style",JazzyHelper.GROW);
        Intent intent=getIntent();
        createDate=intent.getStringExtra("date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(mSelectedColor);
        ButterKnife.bind(this);
        //deleteAll();
        initView();
    }

    /**
     * 跳转到单张浏览模式
     */
    @OnClick(R.id.swipe_card_button)
    public void toSwipeActivity() {
        Intent toSwipeIntent = new Intent(MainActivity.this, SwipeAbleCardsActivity.class);
        toSwipeIntent.putExtra("lists", (Serializable) lists);
        toSwipeIntent.putExtra("theme", mSelectedColor);
        startActivity(toSwipeIntent);
    }


    @OnClick(R.id.fab)
    public void addImg() {
        if (guillotineMenu.getVisibility() == View.VISIBLE) {
            return;
        }else if (mUnfoldableView.isUnfolded()){
            mUnfoldableView.foldBack();
        }
        FabTransformation.with(fab)
                .transformTo(toolbarFooter);
        if (ib_capture==null||ib_album==null){
            ib_capture= (ImageButton) toolbarFooter.findViewById(R.id.capture_img);
            ib_album= (ImageButton) toolbarFooter.findViewById(R.id.add_from_album);
        }
        ib_capture.setOnClickListener(this);
        ib_album.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fab.getVisibility() == View.GONE) {
            fab.show();
        }
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
            toolbar.setBackgroundColor(mSelectedColor);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        initGuillotineMenuItem();
        root.addView(guillotineMenu);
        ImageView guillotine_hamburger = (ImageView) guillotineMenu.findViewById(R.id.guillotine_hamburger);
        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu,
                guillotine_hamburger, contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setRightToLeftLayout(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        viewHelper.setAlpha(1.0f);
                        viewHelper.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onGuillotineClosed() {

                        if (toolbarFooter.getVisibility()==View.VISIBLE) {
                            FabTransformation.with(fab).transformFrom(toolbarFooter);
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewHelper.setVisibility(View.GONE);
                            }
                        }, 0);
                        if (viewHelper.getVisibility() == View.VISIBLE) {
                            //初始化
                            float x = viewHelper.getX();
                            float y = viewHelper.getY();
                            //初始化
                            Animation alphaAnimation = new AlphaAnimation(0.0f, 0.0f);
                            alphaAnimation.setDuration(800);
                            viewHelper.startAnimation(alphaAnimation);
                        }


                    }
                })
                .build();
        guillotineAnimation.close();
        //加载主题样式
        loadThem(mSelectedColor);
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
        //adapter=new SwipeCardAdapter(this, uris);

        //cardViewAdapter = new CardViewAdapter(this, R.layout.card_basic_img, lists);
        cardViewAdapter = new CardAdapter(this, lists);
        //list.setAdapter(adapter);
        list.setAdapter(cardViewAdapter);
        list.setTransitionEffect(selectedStyle);
        mListTouchInterceptor.setClickable(false);
        mDetailsLayout.setVisibility(View.INVISIBLE);
        Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        mUnfoldableView.setFoldShading(new GlanceFoldShading(this, glance));

        mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                mDetailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(false);
                mDetailsLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 初始化GuillotineMenuItem
     */
    private void initGuillotineMenuItem() {
        LinearLayout profile_group = (LinearLayout) guillotineMenu.findViewById(R.id.profile_group);
        LinearLayout feed_group = (LinearLayout) guillotineMenu.findViewById(R.id.feed_group);
        LinearLayout style_group = (LinearLayout) guillotineMenu.findViewById(R.id.style_group);
        LinearLayout settings_group = (LinearLayout) guillotineMenu.findViewById(R.id.settings_group);
        profile_group.setOnClickListener(this);
        feed_group.setOnClickListener(this);
        style_group.setOnClickListener(this);
        settings_group.setOnClickListener(this);

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
    private Handler mHandler = new Handler();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (guillotineMenu.getVisibility() == View.VISIBLE) {
                viewHelp();
            } else if (mUnfoldableView != null && (mUnfoldableView.isUnfolded() || mUnfoldableView.isUnfolding())) {
                mUnfoldableView.foldBack();
            } else if (toolbarFooter.getVisibility() == View.VISIBLE) {
                FabTransformation.with(fab).transformFrom(toolbarFooter);
            }else {
                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt("style",selectedStyle);
                editor.putInt("theme",mSelectedColor);
                editor.commit();
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    private List<CardModel> getAll() {
        return new Select()
                .from(CardModel.class)
                .where("date=?",new Object[]{createDate})
                .execute();
    }

    private void deleteAll() {
        new Delete().from(CardModel.class).execute();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.profile_group:
                viewHelp();
                toast("profile_group");
                break;
            case R.id.feed_group:

                final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.initialize(R.string.color_picker_default_title, mColor, mSelectedColor, 4, 0);
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColor = color;
                        loadThem(color);
                        viewHelp();
                    }
                });
                colorPickerDialog.show(getSupportFragmentManager(), "THEME");
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
                        selectedStyle = selectedIndex - 2;
                    }
                });

                new AlertDialog.Builder(this)
                        .setTitle("STYLE")
                        .setView(outerView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.setTransitionEffect(selectedStyle);
                                viewHelp();
                            }
                        })
                        .show();
                break;
            case R.id.settings_group:
                viewHelp();
                toast("profile_group");
                break;
            case R.id.details_title:

            case R.id.details_text:

                break;
            case R.id.capture_img:
                CameraAndEditCutSimple cameraAndEditCutSimple = new CameraAndEditCutSimple();
                cameraAndEditCutSimple.mDelegate = mDelegate;
                cameraAndEditCutSimple.showSimple(MainActivity.this);
                break;
            case R.id.add_from_album:
                EditMultipleComponentSimple multipleComponentSimple = new EditMultipleComponentSimple();
                multipleComponentSimple.delegate = delegate;
                multipleComponentSimple.showSimple(MainActivity.this);
                break;
        }
    }

    private void loadThem(int color) {
        viewHelper.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        tintManager.setTintColor(color);
        fab.setColorNormal(color);
        toolbarFooter.setBackgroundColor(color);
        guillotineMenu.setBackgroundColor(color);
        guillotineMenu.findViewById(R.id.menu_toolbar).setBackgroundColor(color);
        header_helper.setBackgroundColor(color);
    }

    private void viewHelp() {
        guillotineAnimation.close();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHelper.setVisibility(View.GONE);
            }
        }, 300);

    }
    private int[] getColorArray() {
        int[] mColorChoices = null;
        String[] color_array = getResources().getStringArray(R.array.default_color_choice_values);
        if (color_array != null && color_array.length > 0) {
            mColorChoices = new int[color_array.length];

            for (int i = 0; i < color_array.length; ++i) {
                mColorChoices[i] = Color.parseColor(color_array[i]);
                Log.i("color", color_array[i]);
            }
        }
        return mColorChoices;
    }


    public void openDetails(View coverView, CardModel cardModel) {
        if (guillotineMenu.getVisibility() == View.VISIBLE) {
            return;
        }
        if (toolbarFooter.getVisibility()==View.VISIBLE){
            FabTransformation.with(fab).transformFrom(toolbarFooter);
            fab.hide();
        }
        ImageView image = Views.find(mDetailsLayout, R.id.details_image);
        title = Views.find(mDetailsLayout, R.id.details_title);
        description = Views.find(mDetailsLayout, R.id.details_text);
        title.setOnClickListener(this);
        description.setOnClickListener(this);
        Log.i("imgPath", cardModel.imgPath);
        Picasso.with(this).load(new File(cardModel.imgPath)).into(image);
        title.setText(cardModel.title);
        description.setText(cardModel.description);
        mUnfoldableView.unfold(coverView, mDetailsLayout);
    }


}
