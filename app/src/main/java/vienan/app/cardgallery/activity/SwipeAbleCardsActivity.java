package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.andtinder.view.CardContainer;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.SwipeCardAdapter;

/**
 * Created by vienan on 2015/8/26.
 */
public class SwipeAbleCardsActivity extends Activity implements
        com.andtinder.model.CardModel.OnCardDimissedListener, com.andtinder.model.CardModel.OnClickListener {

    @Bind(R.id.layout_view)
    CardContainer mCardContainer;
    @Bind(R.id.back_to_main)
    ImageView backToMain;
    List<CardModel> lists;
    SwipeCardAdapter adapter;
    int index = 0;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    int theme;
    @OnClick(R.id.back_to_main)
    public void backToMain() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_cards);
        Intent intent = getIntent();
        lists = (List<CardModel>) intent.getSerializableExtra("lists");
        theme = intent.getIntExtra("theme", R.color.pink);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(theme);
        ButterKnife.bind(this);
        initView(theme);
    }


    private void initView(int theme) {
        if (theme != R.color.pink) {
            if (toolbar != null) {
                toolbar.setBackgroundColor(theme);
            }
        }
        //SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);
        adapter = new SwipeCardAdapter(this, lists);
        for (int i = lists.size() - 1; i >= 0; i--) {
            CardModel cardModel = lists.get(i);
            Drawable cardImageDrawable = Drawable.createFromPath(cardModel.imgPath);
            com.andtinder.model.CardModel model = new com.andtinder.model.CardModel(
                    cardModel.title, cardModel.description, cardImageDrawable
            );
            model.setOnClickListener(this);
            model.setOnCardDimissedListener(this);
            adapter.add(model);
        }
        mCardContainer.setAdapter(adapter);
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

    /**
     * 浏览完毕返回
     */
    @Override
    public void onLike() {
        back();
    }

    @Override
    public void onDislike() {
        back();
    }

    private void back() {
        index++;
        if (index == adapter.getCount()) {
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()).text(R.string.no_more_cards)
                            .textColor(Color.WHITE)
                            .color(theme)
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

    @Override
    public void OnClickListener() {

    }

    private void toast(String msg) {
        Toast.makeText(SwipeAbleCardsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
