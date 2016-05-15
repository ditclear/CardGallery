package vienan.app.cardgallery.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.fafaldo.blurzoomgallery.widget.BlurZoomCoordinatorLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import de.greenrobot.event.EventBus;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.model.CardModel;


public class ImageFragment extends Fragment {
    private static final String PHOTO = "photo";
    private static final String PAGE = "page";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String MODEL = "cardModel";

    private String photo = "";
    private String title = "";
    private String content = "";
    private int page;
    private CardModel cardModel;
    private ImageView imageView;

    private boolean isLoading = false;

    public static ImageFragment newInstance(CardModel model, int page) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO, model.imgPath);
        args.putInt(PAGE,page);
        args.putString(TITLE, model.title);
        args.putString(CONTENT, model.description);
        args.putSerializable(MODEL,model);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photo = getArguments().getString(PHOTO);
            page = getArguments().getInt(PAGE);
            title = getArguments().getString(TITLE);
            content = getArguments().getString(CONTENT);
            cardModel= (CardModel) getArguments().getSerializable(MODEL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageView = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
        EventBus.getDefault().post(cardModel);
        imageView.setTag(cardModel);
        ImageLoader.getInstance().loadImage(
                ImageDownloader.Scheme.FILE.wrap(photo),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        isLoading = true;
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_loading_bg));
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        isLoading = false;
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                        isLoading = false;
                        if (getActivity() != null) {
                            if (((BlurZoomGalleryActivity) getActivity()).isScrolling == true) {

                                imageView.setImageBitmap(loadedImage);
                                Log.d("ToolbarZoomGalleryEvent", page + ": we're scrolling, just display");
                            } else {
                                if (((BlurZoomGalleryActivity) getActivity()).currentPage == page) {
                                    if (((BlurZoomGalleryActivity) getActivity()).isExpanded) {
                                        ((BlurZoomGalleryActivity) getActivity()).blur(loadedImage, new BlurZoomCoordinatorLayout.OnBlurCompletedListener() {
                                            @Override
                                            public void blurCompleted() {
                                                imageView.setImageBitmap(loadedImage);
                                            }
                                        });

                                        Log.d("ToolbarZoomGalleryEvent", page + ": we're not scrolling, loaded current photo, is expanded, blur and display");
                                    } else {
                                        ((BlurZoomGalleryActivity) getActivity()).blur(loadedImage, new BlurZoomCoordinatorLayout.OnBlurCompletedListener() {
                                            @Override
                                            public void blurCompleted() {
                                                imageView.setImageBitmap(loadedImage);
                                            }
                                        });

                                        Log.d("ToolbarZoomGalleryEvent", page + ": we're not scrolling, loaded current photo, is collapsed, blur and display");
                                    }
                                } else {
                                    imageView.setImageBitmap(loadedImage);

                                    Log.d("ToolbarZoomGalleryEvent", page + ": we're not scrolling, loaded other photo (current " + ((BlurZoomGalleryActivity) getActivity()).currentPage + ") so just display");
                                }
                            }
                        }

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        isLoading = false;
                    }
                }
        );

        return imageView;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
