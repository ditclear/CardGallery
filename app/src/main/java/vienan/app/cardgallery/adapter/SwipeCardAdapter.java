package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import vienan.app.cardgallery.R;

/**
 * Created by vienan on 2015/8/19.
 */
public class SwipeCardAdapter extends CardStackAdapter{
    List<vienan.app.cardgallery.model.CardModel> lists;
    Context context;


    public List<vienan.app.cardgallery.model.CardModel> getLists() {
        return lists;
    }

    public SwipeCardAdapter(Context context,List<vienan.app.cardgallery.model.CardModel> lists) {
        super(context);
        this.context=context;
        this.lists=lists;
    }
    @Override
    protected View getCardView(int i, final CardModel cardModel, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.card_big_img,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.title= (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.description= (TextView) convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(viewHolder);
        }else {
            Log.e("viewHolder", convertView.getTag().toString());
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Log.d("cardModel","cardModel"+i);
        if (cardModel!=null){
           /* ImageUtils.imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(lists.get(i)
            .imgPath),viewHolder.imageView,ImageUtils.options);*/
            viewHolder.imageView.setImageDrawable(cardModel.getCardImageDrawable());
            viewHolder.title.setText(cardModel.getTitle());
            viewHolder.description.setText(cardModel.getDescription());
            viewHolder.description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view=LayoutInflater.from(context).inflate(R.layout.text_detail,null);
                    TextView textView= (TextView) view.findViewById(R.id.descriptionTextView);
                    textView.setText(cardModel.getDescription());
                    new MaterialDialog(context).setView(view).setCanceledOnTouchOutside(true).show();
                }
            });
        }
        return convertView;
    }
    private class  ViewHolder{
        public ImageView imageView;
        public TextView title,description;
    }
}