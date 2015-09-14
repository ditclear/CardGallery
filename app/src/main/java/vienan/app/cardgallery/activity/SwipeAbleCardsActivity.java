package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.amulyakhare.textdrawable.TextDrawable;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.SwipeCardAdapter;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.view.CanaroTextView;

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
    @Bind(R.id.toolbar_title)
    CanaroTextView toolbarTitle;
    String fromWhere;

    @OnClick(R.id.back_to_main)
    public void backToMain() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_cards);
        Intent intent = getIntent();
        //lists = (List<CardModel>) intent.getSerializableExtra("lists");
        fromWhere = intent.getStringExtra("fromWhere");

        theme = intent.getIntExtra("theme", R.color.pink);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(theme);
        ButterKnife.bind(this);
        if (fromWhere.equals("TimeLine")) {
            toolbarTitle.setText("My Favorites");
            lists = getAllStar();
        } else {
            lists = getAllNote(intent.getStringExtra("date"));
        }
        initView(theme);
    }

    private List<CardModel> getAllNote(String date) {
        return new Select().from(CardModel.class)
                .where("date=? and type=?", new Object[]{date, "note"}).execute();
    }

    private List<CardModel> getAllStar() {
        return new Select().from(CardModel.class).where("star=?", 1).execute();
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
            Drawable cardImageDrawable = getImgDrawable(cardModel);
            com.andtinder.model.CardModel model = new com.andtinder.model.CardModel(
                    cardModel.title, cardModel.description, cardImageDrawable
            );
            model.setOnClickListener(this);
            model.setOnCardDimissedListener(this);
            adapter.add(model);
        }
        mCardContainer.setAdapter(adapter);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
    }

    private Drawable getImgDrawable(CardModel cardModel) {
        if (cardModel.type.equals("cardNote")) {
            return Drawable.createFromPath(cardModel.imgPath);
        } else {
            return TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4) /* thickness in px */
                    .endConfig()
                    .buildRoundRect("NOTE",theme, 20);
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
    private Handler mHandler = new Handler();
    private boolean hasChanged=false;
    /**
     * 浏览完毕返回
     */
    @Override
    public void onLike() {
        back();
        if (fromWhere.equals("TimeLine")) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showDialog();
                }
            });

        }

    }

    private void showDialog() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setCancelText("取消")
                .showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
        dialog.setTitleText("取消收藏").setContentText("从收藏夹中移除？");
        dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                CardModel model=lists.get(index-1);
                model.star = 0;
                Log.d("model",""+index+ model.toString()+ model.getId());
                new Update(CardModel.class).set("star=?", model.star).where("Id=?", model.getId()).execute();
                hasChanged=true;
                sweetAlertDialog.cancel();
            }
        }).show();
    }

    @Override
    public void onDislike() {
        back();
        adapter.getLists().set(index - 1, null);

    }

    private void back() {
        index++;
        if (index== adapter.getCount()) {
            adapter.getLists().set( adapter.getCount()-1,null);
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()).text(R.string.no_more_cards)
                            .textColor(Color.WHITE)
                            .color(theme)
                            .actionLabel("知道了")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    Intent back_to_timeLine=new Intent();
                                    back_to_timeLine.putExtra("hasChanged",hasChanged);
                                    setResult(RESULT_OK,back_to_timeLine);
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
