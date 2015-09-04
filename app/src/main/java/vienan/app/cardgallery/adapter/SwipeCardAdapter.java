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
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

import vienan.app.cardgallery.R;
import vienan.app.cardgallery.activity.SwipeAbleCardsActivity;
import vienan.app.cardgallery.utils.ImageUtils;

/**
 * Created by vienan on 2015/8/19.
 */
public class SwipeCardAdapter extends CardStackAdapter{
    List<vienan.app.cardgallery.model.CardModel> lists;
    Context context;
    public SwipeCardAdapter(Context context,List<vienan.app.cardgallery.model.CardModel> lists) {
        super(context);
        this.context=context;
        this.lists=lists;
    }
    @Override
    protected View getCardView(int i, CardModel cardModel, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(this.getContext()).inflate(R.layout.card_big_img,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.title= (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.description= (TextView) convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(viewHolder);
        }else {
            Log.e("viewHolder", convertView.getTag().toString());
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if (cardModel!=null){
            ImageUtils.imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(lists.get(i)
            .imgPath),viewHolder.imageView,ImageUtils.options);
            viewHolder.title.setText(cardModel.getTitle());
            viewHolder.description.setText(cardModel.getDescription());
        }
        if (super.getItem(i)==null) {
            SwipeAbleCardsActivity activity= (SwipeAbleCardsActivity) this.getContext();
            activity.onBackPressed();
        }
        Log.i("i",""+i);
        return convertView;
    }
    class  ViewHolder{
        public ImageView imageView;
        public TextView title,description;
    }
}