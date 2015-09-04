package vienan.app.cardgallery.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.model.CardModel;

/**
 * Created by vienan on 2015/8/21.
 */
public class EditCardActivity extends Activity {


    TextInputLayout titleTextView;
    CardModel model;
    @Bind(R.id.content_save)
    ImageView contentSave;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    TextView descriptionTextView;
    EditText et_title;
    @Bind(R.id.cardView)
    CardView cardView;
    private String imgPath,date;
    private Handler mHandler=new Handler();
    int theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcard);
        titleTextView = (TextInputLayout) findViewById(R.id.titleTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        et_title = titleTextView.getEditText();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        date=intent.getStringExtra("createDate");
        imgPath = intent.getStringExtra("imgPath");
        theme = intent.getIntExtra("theme", R.color.pink);
        model = new CardModel();
        model.imgPath = imgPath;
        titleTextView.setHint(getResources().getString(R.string.title));
        descriptionTextView.setHint(getResources().getString(R.string.description));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        if (theme == R.color.pink) {
            tintManager.setStatusBarTintResource(R.color.pink);
        } else {
            tintManager.setTintColor(theme);
        }
        if (theme != R.color.pink) {
            if (toolbar != null) {
                toolbar.setBackgroundColor(theme);
            }
        }
    }

    @OnClick(R.id.titleTextView)
    public void onTitleClicked() {
        /*final TuSdkEditText et = new TuSdkEditText(this);
        new AlertDialog.Builder(this).setTitle("标题")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            et.setShakeAnimation();
                            input = "TITLE";
                        }
                        titleTextView.setText(input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();*/

    }

    @OnClick(R.id.descriptionTextView)
    public void description() {
        final TextInputLayout textInputLayout = (TextInputLayout) getLayoutInflater().inflate(R.layout.edit_dialog, null);
        textInputLayout.setHint("说点什么..");
        final EditText editText = textInputLayout.getEditText();
        String input = descriptionTextView.getText().toString();
        if (!input.equals("")) {
            editText.setText(input);
        }
        final MaterialDialog dialog = new MaterialDialog(this).setContentView(textInputLayout);
        dialog.setPositiveButton("写好了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YoYo.with(Techniques.RollOut)
                        .duration(1000)
                        .playOn(v.getRootView());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        descriptionTextView.setText(editText.getText().toString());
                        dialog.dismiss();
                    }
                },1000);
            }
        }).show();
        YoYo.with(Techniques.RollIn).playOn(textInputLayout.getRootView());
    }

    @OnClick(R.id.content_save)
    public void saveCard() {
        String title = et_title.getText().toString();
        String description = descriptionTextView.getText().toString();
        if (title.equals("") || description.equals("")) {
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .playOn(cardView);
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()).text(R.string.editCard_tip)
                            .textColor(Color.WHITE)
                            .color(theme)
                            .actionLabel("知道了")
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
            ,this);
            return;
        }
        model.date=date;
        if (imgPath!=null){
            model.type="cardNote";
        }else {
            model.type="note";
        }

        model.title = title;
        model.description = description;
        Log.i("mode",model.toString());
        Intent intent = new Intent();
        intent.putExtra("cardModel", model);
        setResult(RESULT_OK, intent);
        finish();
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
}
