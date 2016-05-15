package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.activeandroid.query.Select;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.CardRecyclerAdapter;
import vienan.app.cardgallery.listener.HidingScrollListener;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.utils.Utils;

/**
 * Created by vienan on 2015/10/11.
 */
public class MyGalleryActivity extends ActionBarActivity {
    @Bind(R.id.back_to_main)
    ImageView backToMain;
    private LinearLayout mToolbarContainer;
    private Toolbar mToolbar;
    private int mToolbarHeight;
    SystemBarTintManager tintManager;
    int mSelectedColor;
    private List<CardModel> lists;

    @OnClick(R.id.back_to_main)
    public void backToMain() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        mSelectedColor = getIntent().getIntExtra("theme", R.color.pink);
        mToolbarContainer = (LinearLayout) findViewById(R.id.toolbarContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(mSelectedColor);
        initToolbar();
        initRecyclerView();
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

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.allCard));
        mToolbar.setBackgroundColor(mSelectedColor);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        mToolbarHeight = Utils.getToolbarHeight(this);
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        lists = getAllCardNote();
        CardRecyclerAdapter recyclerAdapter = new CardRecyclerAdapter(this, lists);
        recyclerView.setAdapter(recyclerAdapter);
        HidingScrollListener listener = new HidingScrollListener(this) {
            @Override
            public void onMoved(int distance) {
                mToolbarContainer.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                mToolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void onHide() {
                mToolbarContainer.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        };
        recyclerView.addOnScrollListener(listener);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private List<CardModel> getAllCardNote() {
        return new Select()
                .from(CardModel.class)
                .where("type=?", "cardNote")
                .execute();
    }

    public void toBlurZoomActivity(int position) {
        Intent toBlurZoomIntent = new Intent(MyGalleryActivity.this, BlurZoomGalleryActivity.class);
        toBlurZoomIntent.putExtra("position", position);
        toBlurZoomIntent.putExtra("list", (Serializable) lists);
        startActivity(toBlurZoomIntent);
    }
}
