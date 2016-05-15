package vienan.app.cardgallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import vienan.app.cardgallery.activity.ImageFragment;
import vienan.app.cardgallery.model.CardModel;

/**
 * Created by fafik on 2015-08-23.
 */
public class GalleryPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<ImageFragment> fragments = new ArrayList<>();
    private List<CardModel> lists;
    public GalleryPagerAdapter(FragmentManager fm,List<CardModel> lists) {
        super(fm);
        this.lists=lists;
        for (int i = 0; i <lists.size() ; i++) {
            fragments.add(ImageFragment.newInstance(lists.get(i), i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position>=0){
            return fragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return lists.size();
    }
}
