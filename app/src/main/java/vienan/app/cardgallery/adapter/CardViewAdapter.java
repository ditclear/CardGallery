package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.util.List;

import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.R;

/**
 * Created by vienan on 2015/8/21.
 */
public class CardViewAdapter extends ArrayAdapter<CardModel> {
    private Context context;
    private int resourceId;
    public CardViewAdapter(Context context, int resource, List<CardModel> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resourceId=resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(resourceId,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.title= (TextView) convertView.findViewById(R.id.titleTextView);
            //viewHolder.description= (TextView) convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(viewHolder);
        }else {
            Log.e("viewHolder", convertView.getTag().toString());
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if (getItem(position)!=null){
            CardModel cardModel=getItem(position);
            Picasso.with(context).load(Uri.parse(cardModel.imgPath)).into(viewHolder.imageView);
            viewHolder.title.setText(cardModel.title);
            viewHolder.description.setText(cardModel.description);
            if(cardModel.description.equals("说点什么...")){
                Log.i("description","equal");
                JumpingBeans jumpingBeans1 =getJumpBeans2(viewHolder.description);
            }
            if(cardModel.title.equals("TITLE")){
                JumpingBeans jumpingBeans = getJumpBeans1(viewHolder.title);
            }
        }
        return convertView;
    }
    private JumpingBeans getJumpBeans2(TextView textView) {
        return  JumpingBeans.with(textView)
                .makeTextJump(0,textView.getText().toString().length())
                .setIsWave(false)
                .setLoopDuration(1000)  // ms
                .build();

    }
    private JumpingBeans getJumpBeans1(TextView textView) {
        return JumpingBeans.with(textView)
                .appendJumpingDots()
                .build();

    }

    private static class  ViewHolder{
        public ImageView imageView;
        public TextView title,description;
    }
}
