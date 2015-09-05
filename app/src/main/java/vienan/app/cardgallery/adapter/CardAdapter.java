package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.adapters.ItemsAdapter;
import com.alexvasilkov.android.commons.utils.Views;
import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import vienan.app.cardgallery.R;
import vienan.app.cardgallery.activity.MainActivity;
import vienan.app.cardgallery.model.CardModel;

/**
 * Created by vienan on 2015/8/25.
 */
public class CardAdapter extends ItemsAdapter<CardModel> implements View.OnClickListener {

    public CardAdapter(Context context,List<CardModel> objects) {
        super(context);
        setItemsList(objects);
    }
    @Override
    protected View createView(CardModel item, int pos, ViewGroup parent, LayoutInflater inflater) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.image = Views.find(view, R.id.list_item_image);
        vh.image.setOnClickListener(this);
        vh.title = Views.find(view, R.id.list_item_title);
        view.setTag(vh);

        return view;
    }
    TextDrawable drawable;
    @Override
    protected void bindView(CardModel item, int pos, View convertView) {
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.image.setTag(item);
        if (item.imgPath!=null){
            Picasso.with(convertView.getContext()).load(new File(item.imgPath)).noFade().into(vh.image);
        }else {
            if (drawable==null) {
                drawable = TextDrawable.builder()
                        .buildRect("NOTE", Color.BLUE);
            }
                vh.image.setImageDrawable(drawable);
        }
        vh.title.setText(item.title);
    }

    @Override
    public void onClick(View view) {
        if (view.getContext() instanceof MainActivity) {
            MainActivity activity = (MainActivity) view.getContext();
            activity.openDetails(view, (CardModel) view.getTag());
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
    }

}
