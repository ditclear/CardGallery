package vienan.app.cardgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by vienan on 2015/8/19.
 */
public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<String> uris;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    public ListAdapter(Context context,List<String> uris){
        this.context=context;
        this.uris=uris;
        options=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_image_48pt_3x)
                .showImageOnFail(R.mipmap.ic_broken_image_black_36dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader=ImageLoader.getInstance();
    }
    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public String getItem(int position) {
        return uris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.card_basic_img,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.title= (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.description= (TextView) convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(viewHolder);
        }else {
            Log.e("viewHolder", convertView.getTag().toString());
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if (getItem(position)!=null){
            imageLoader.displayImage(getItem(position),viewHolder.imageView,options);
            viewHolder.title.setText("TITLE");
            viewHolder.description.setText("说点什么...");
        }
        return convertView;
    }
    private static class  ViewHolder{
        public ImageView imageView;
        public TextView title,description;
    }
}
