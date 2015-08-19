package vienan.app.cardgallery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by vienan on 2015/8/19.
 */
public class ListAdapter extends BaseAdapter {
    Context context;
    List<String> uris;
    public ListAdapter(Context context,List<String> uris){
        this.context=context;
        this.uris=uris;
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
            convertView= LayoutInflater.from(context).inflate(R.layout.list_item,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.item_img);
            convertView.setTag(viewHolder);
        }else {
            Log.e("viewHolder", convertView.getTag().toString());
            viewHolder= (ViewHolder) convertView.getTag();

        }
        if (getItem(position)!=null){
            viewHolder.imageView.setImageURI(Uri.parse(getItem(position)));
        }
        return convertView;
    }
    private static class  ViewHolder{
        public ImageView imageView;
    }
}
