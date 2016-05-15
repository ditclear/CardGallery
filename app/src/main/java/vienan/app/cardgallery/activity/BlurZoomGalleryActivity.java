package vienan.app.cardgallery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.fafaldo.blurzoomgallery.widget.BlurZoomCoordinatorLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.GalleryPagerAdapter;
import vienan.app.cardgallery.model.CardModel;

/**
 * Created by vienan on 2015/10/12.
 */
public class BlurZoomGalleryActivity extends AppCompatActivity {
    @Bind(R.id.tv_title_blurZoom)
    TextView tvTitle;
    @Bind(R.id.tv_content_blurZoom)
    TextView tvContent;
    @Bind(R.id.cv_blurzoom)
    CardView cardView_blur;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private BlurZoomCoordinatorLayout coordinatorLayout;
    private ViewPager gallery;
    private GalleryPagerAdapter adapter;

    public boolean isScrolling = false;
    public int currentPage = 0;
    private List<CardModel> lists;
    public boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blurzoom);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        currentPage=intent.getIntExtra("position",0);
        lists= (List<CardModel>) intent.getSerializableExtra("list");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        toolbar = (Toolbar) findViewById(R.id.gallery_coordinator_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.gallery_coordinator_appbarlayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        gallery = (ViewPager) findViewById(R.id.gallery_coordinator_gallery);
        coordinatorLayout = (BlurZoomCoordinatorLayout) findViewById(R.id.coordinator);
        adapter = new GalleryPagerAdapter(getSupportFragmentManager(),lists);

        gallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                YoYo.with(Techniques.FadeIn).playOn(cardView_blur);
                CardModel model= lists.get(position);
                collapsingToolbarLayout.setTitle(model.date.toUpperCase());
                tvTitle.setText(model.title);
                tvContent.setText(model.description);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    currentPage = gallery.getCurrentItem();

                    coordinatorLayout.prepareBlur(null);
                } else {
                    isScrolling = true;
                }
            }
        });

        gallery.setOffscreenPageLimit(10);
        gallery.setAdapter(adapter);
        gallery.setCurrentItem(currentPage);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle("Paris, France");
        coordinatorLayout.setOnStateChangedListener(new BlurZoomCoordinatorLayout.OnStateChangedListener() {
            @Override
            public void stateChanged(boolean state) {
                isExpanded = state;
            }
        });

        coordinatorLayout.setDuration(400);
        coordinatorLayout.setInterpolator(new AccelerateDecelerateInterpolator());

        appBarLayout.setExpanded(true, true);
    }

    public void blur(Bitmap bmp, BlurZoomCoordinatorLayout.OnBlurCompletedListener listener) {
        coordinatorLayout.prepareBlur(bmp, listener);
    }
   List<String> title_content=new ArrayList<String>();
   List<String> dates=new ArrayList<String>();
    public void onEventMainThread(CardModel model){
        title_content.add(model.title+"vienna"+model.description);
        dates.add(model.date);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Unregister
        EventBus.getDefault().unregister(this);

    }

}

