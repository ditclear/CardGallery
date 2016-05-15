package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.activity.MainActivity;
import vienan.app.cardgallery.activity.MyGalleryActivity;
import vienan.app.cardgallery.model.CardModel;

/**
 * Created by vienan on 2015/9/19.
 */
public class CardRecyclerAdapter extends RecyclerSwipeAdapter<CardRecyclerAdapter.MyViewHolder>{

    private Context context;
    private List<CardModel> datas;
    private LayoutInflater mInflater;

    public CardRecyclerAdapter(Context context,List<CardModel> datas) {
        this.context=context;
        this.datas=datas;
        this.mInflater=LayoutInflater.from(context);
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        CardModel item=datas.get(i);
        myViewHolder.image.setTag(item);
        if (item.imgPath!=null){
            Picasso.with(context).load(new File(item.imgPath)).noFade().into(myViewHolder.image);
        }
        myViewHolder.title.setText(item.title);
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.list_item_image) ImageView image;
        @Bind(R.id.list_item_title)TextView title;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick(R.id.list_item_image)
        public void openImage(View view) {
            CardModel model=(CardModel) view.getTag();
            if (view.getContext() instanceof MainActivity) {
                MainActivity activity = (MainActivity) view.getContext();
                activity.openDetails(view, model);
            }else if (view.getContext() instanceof MyGalleryActivity){
                MyGalleryActivity myGalleryActivity= (MyGalleryActivity) view.getContext();
                myGalleryActivity.toBlurZoomActivity(getAdapterPosition());
            }
        }
    }
}
