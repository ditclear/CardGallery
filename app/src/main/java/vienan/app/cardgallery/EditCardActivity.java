package vienan.app.cardgallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vienan on 2015/8/21.
 */
public class EditCardActivity extends Activity {


    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.titleTextView)
    TextView titleTextView;
    @Bind(R.id.descriptionTextView)
    TextView descriptionTextView;
    CardModel model;
    @Bind(R.id.content_save)
    ImageView contentSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_big_img);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("imgPath");
        imageView.setImageURI(Uri.parse(imgPath));
        model = new CardModel();
        model.imgPath = imgPath;
        titleTextView.setText(R.string.title);
        descriptionTextView.setText(R.string.description);
    }

    @OnClick(R.id.titleTextView)
    public void onTitleClicked() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("标题")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            input = "TITLE";
                        }
                        titleTextView.setText(input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }

    @OnClick(R.id.descriptionTextView)
    public void description() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("描述")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            input = "说点什么...";
                        }
                        descriptionTextView.setText(input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    @OnClick(R.id.content_save)
    public void saveCard(){
        model.title = titleTextView.getText().toString();
        model.description = descriptionTextView.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("cardModel", model);
        setResult(RESULT_OK, intent);
        finish();
    }
}
