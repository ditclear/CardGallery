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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.CardRecyclerAdapter;
import vienan.app.cardgallery.adapter.SwipeCardAdapter;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.utils.FileUtils;
import vienan.app.cardgallery.view.WheelView;


public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    private final static int TO_EDIT = 1;
    private static final String[] STYLES = new String[]{"垂直方向一列", "垂直方向两列", "水平方向三行"};
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    RecyclerView list;
    List<CardModel> lists = new ArrayList<CardModel>();
    SwipeCardAdapter adapter;
    CardRecyclerAdapter cardViewAdapter;
    @Bind(R.id.fab_main)
    FloatingActionButton fabMain;
    private JazzyRecyclerViewScrollListener jazzyRecyclerViewScrollListener;
    List<String> uris = new ArrayList<String>();
    @Bind(R.id.touch_interceptor_view)
    View mListTouchInterceptor;
    @Bind(R.id.details_layout)
    LinearLayout mDetailsLayout;
    @Bind(R.id.unfoldable_view)
    UnfoldableView mUnfoldableView;
    @Bind(R.id.swipe_card_button)
    ImageView swipeCardButton;
    @Bind(R.id.back_to_main)
    ImageView backToMain;
    private int selectedStyle;
    TextView title;
    TextView description;
    SystemBarTintManager tintManager;
    int mSelectedColor;
    int mSelectedIndex;
    int spanCount;
    int orientaion;
    String createDate;
    private StaggeredGridLayoutManager layoutManager;

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("model", "onCreate");
        Intent intent = getIntent();
        mSelectedColor = intent.getIntExtra("theme", R.color.pink);
        selectedStyle = intent.getIntExtra("style", JazzyHelper.GROW);
        mSelectedIndex = intent.getIntExtra("mSelectedIndex", 0);
        createDate = intent.getStringExtra("date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(mSelectedColor);
        ButterKnife.bind(this);
        initLayoutManager(mSelectedIndex);
        initView();
    }

    private void initLayoutManager(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
                spanCount = 1;
                orientaion = StaggeredGridLayoutManager.VERTICAL;
                break;
            case 1:
                spanCount = 2;
                orientaion = StaggeredGridLayoutManager.VERTICAL;
                break;
            case 2:
                spanCount = 3;
                orientaion = StaggeredGridLayoutManager.HORIZONTAL;
                break;
        }
    }

    /**
     * 跳转到单张浏览模式
     */
    @OnClick(R.id.swipe_card_button)
    public void toSwipeActivity() {
        Intent toSwipeIntent = new Intent(MainActivity.this, SwipeAbleCardsActivity.class);
        toSwipeIntent.putExtra("fromWhere", "Main");
        toSwipeIntent.putExtra("date", createDate);
        toSwipeIntent.putExtra("theme", mSelectedColor);
        startActivity(toSwipeIntent);
    }

    @OnClick(R.id.back_to_main)
    public void backToMain() {
        super.onBackPressed();
    }

    @OnClick(R.id.fab_main)
    public void onFabClicked() {
        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(Arrays.asList(STYLES));
        wv.setSeletion(mSelectedIndex);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.i("index", "" + selectedIndex);
                mSelectedIndex = selectedIndex - 2;
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("视图")
                .setView(outerView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initLayoutManager(mSelectedIndex);
                        layoutManager.setOrientation(orientaion);
                        layoutManager.setSpanCount(spanCount);
                        list.setLayoutManager(layoutManager);
                        YoYo.with(Techniques.Landing).playOn(list);
                        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                        editor.putInt("mSelectedIndex", mSelectedIndex);
                        editor.commit();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutManager.setSpanCount(spanCount);
        layoutManager.setOrientation(orientaion);
        list.setLayoutManager(layoutManager);
        if (lists.isEmpty()) {
            fabMain.setVisibility(View.GONE);
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()).text(R.string.no_more_cards_main)
                            .textColor(Color.WHITE)
                            .color(mSelectedColor)
                            .actionLabel("知道了")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    onBackPressed();
                                }
                            })
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .animation(true)
                    , this);
        }else {
            fabMain.setVisibility(View.VISIBLE);
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

        //加载主题样式
        loadThem(mSelectedColor);
        list = (RecyclerView) findViewById(R.id.list);
        File file = new File(FileUtils.path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                lists = getAll();
            }
        }
        cardViewAdapter = new CardRecyclerAdapter(this, lists);
        layoutManager = new StaggeredGridLayoutManager(spanCount, orientaion);
        list.setLayoutManager(layoutManager);
        list.setAdapter(cardViewAdapter);
        jazzyRecyclerViewScrollListener = new JazzyRecyclerViewScrollListener();
        list.addOnScrollListener(jazzyRecyclerViewScrollListener);
        jazzyRecyclerViewScrollListener.setTransitionEffect(selectedStyle);
        //进行fab和view的关联
        fabMain.attachToRecyclerView(list);
        YoYo.with(Techniques.ZoomIn).playOn(list);
        mListTouchInterceptor.setClickable(false);
        mDetailsLayout.setVisibility(View.INVISIBLE);
        Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        mUnfoldableView.setFoldShading(new GlanceFoldShading(this, glance));
        mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                fabMain.hide();
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
                fabMain.show();
            }
        });
    }

    /**
     * 点击返回键退出程序
     */
    private Handler mHandler = new Handler();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mUnfoldableView != null && (mUnfoldableView.isUnfolded() || mUnfoldableView.isUnfolding())) {
                mUnfoldableView.foldBack();
            } else {
                super.onBackPressed();
            }
        }
        return false;
    }

    private List<CardModel> getAll() {
        return new Select()
                .from(CardModel.class)
                .where("date=? and type=?", new Object[]{createDate, "cardNote"})
                .execute();
    }

    /**
     * 初始化主题
     *
     * @param color
     */
    private void loadThem(int color) {
        toolbar.setBackgroundColor(color);
        tintManager.setTintColor(color);
        fabMain.setColorNormal(color);
    }

    /**
     * 打开详细信息
     *
     * @param coverView
     * @param cardModel
     */
    public void openDetails(View coverView, final CardModel cardModel) {
        ImageView image = Views.find(mDetailsLayout, R.id.details_image);
        title = Views.find(mDetailsLayout, R.id.details_title);
        description = Views.find(mDetailsLayout, R.id.details_text);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.text_detail, null);
                TextView textView = (TextView) view.findViewById(R.id.descriptionTextView);
                textView.setText(cardModel.description);
                new MaterialDialog(MainActivity.this).setView(view).setCanceledOnTouchOutside(true).show();
            }
        });
        if (cardModel.imgPath != null) {
            if (image.getVisibility() != View.VISIBLE) {
                image.setVisibility(View.VISIBLE);
            }
            Picasso.with(this).load(new File(cardModel.imgPath)).into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        title.setText(cardModel.title);
        description.setText(cardModel.description);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1 = (TextView) v;
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.text_detail, null);
                TextView textView = (TextView) view.findViewById(R.id.descriptionTextView);
                textView.setText(textView1.getText());
                new MaterialDialog(MainActivity.this).setView(view).setCanceledOnTouchOutside(true).show();
            }
        });
        mUnfoldableView.unfold(coverView, mDetailsLayout);
    }


}
