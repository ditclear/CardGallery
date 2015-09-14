package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.CardAdapter;
import vienan.app.cardgallery.adapter.SwipeCardAdapter;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.utils.FileUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private final static int TO_EDIT = 1;


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    JazzyListView list;
    List<CardModel> lists = new ArrayList<CardModel>();
    SwipeCardAdapter adapter;
    //CardViewAdapter cardViewAdapter;
    CardAdapter cardViewAdapter;
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
    String createDate;

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        mSelectedColor = intent.getIntExtra("theme", R.color.pink);
        selectedStyle = intent.getIntExtra("style", JazzyHelper.GROW);
        createDate = intent.getStringExtra("date");
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
        toSwipeIntent.putExtra("fromWhere","Main");
        toSwipeIntent.putExtra("date",createDate);
        toSwipeIntent.putExtra("theme", mSelectedColor);
        startActivity(toSwipeIntent);
    }
    @OnClick(R.id.back_to_main)
    public void backToMain() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lists.isEmpty()){
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
                    , this);
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
        /*// 异步方式初始化滤镜管理器
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        TuProgressHub.setStatus(this, TuSdkContext.getString("lsq_initing"));
        TuSdk.checkFilterManager(mFilterManagerDelegate);*/
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


    /*private FilterManagerDelegate mFilterManagerDelegate = new FilterManagerDelegate() {
        @Override
        public void onFilterManagerInited(FilterManager manager) {
            TuProgressHub.showSuccess(MainActivity.this,
                    TuSdkContext.getString("lsq_inited"));
        }
    };*/
    /**
     * 点击返回键退出程序
     */
    private Handler mHandler = new Handler();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mUnfoldableView != null && (mUnfoldableView.isUnfolded() || mUnfoldableView.isUnfolding())) {
                mUnfoldableView.foldBack();
            }else {
                super.onBackPressed();
            }
        }
        return false;
    }

    private List<CardModel> getAll() {
        return new Select()
                .from(CardModel.class)
                .where("date=? and type=?", new Object[]{createDate,"cardNote"})
                .execute();
    }

    private void deleteAll() {
        new Delete().from(CardModel.class).execute();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

        }
    }

    private void loadThem(int color) {
        toolbar.setBackgroundColor(color);
        tintManager.setTintColor(color);
    }


    public void openDetails(View coverView, final CardModel cardModel) {
        ImageView image = Views.find(mDetailsLayout, R.id.details_image);
        title = Views.find(mDetailsLayout, R.id.details_title);
        description = Views.find(mDetailsLayout, R.id.details_text);
        title.setOnClickListener(this);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.text_detail,null);
                TextView textView= (TextView) view.findViewById(R.id.descriptionTextView);
                textView.setText(cardModel.description);
                new MaterialDialog(MainActivity.this).setView(view).setCanceledOnTouchOutside(true).show();
            }
        });
        if (cardModel.imgPath!=null) {
            if (image.getVisibility()!=View.VISIBLE){
                image.setVisibility(View.VISIBLE);
            }
            Picasso.with(this).load(new File(cardModel.imgPath)).into(image);
        }else {
            image.setVisibility(View.GONE);
        }
        title.setText(cardModel.title);
        description.setText(cardModel.description);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1= (TextView) v;
                View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.text_detail,null);
                TextView textView= (TextView) view.findViewById(R.id.descriptionTextView);
                textView.setText(textView1.getText());
                new MaterialDialog(MainActivity.this).setView(view).setCanceledOnTouchOutside(true).show();
            }
        });
        mUnfoldableView.unfold(coverView, mDetailsLayout);
    }


}
