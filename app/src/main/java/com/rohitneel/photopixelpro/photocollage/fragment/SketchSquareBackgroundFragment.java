package com.rohitneel.photopixelpro.photocollage.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitneel.photopixelpro.R;
import com.rohitneel.photopixelpro.photocollage.adapters.SplashSquareAdapter;
import com.rohitneel.photopixelpro.photocollage.assets.StickerFileAsset;
import com.rohitneel.photopixelpro.photocollage.photo.PhotoSplashSquareView;
import com.rohitneel.photopixelpro.photocollage.photo.PhotoSplashSticker;

public class SketchSquareBackgroundFragment extends DialogFragment implements SplashSquareAdapter.SplashChangeListener {
    private static final String TAG = "SketchSquareBackgroundFragment";
    private ImageView image_view_background;
    public Bitmap bitmap;
    private Bitmap SketchBitmap;
    private FrameLayout frame_layout;
    public boolean isSplashView;
    public RecyclerView recycler_view_splash;
    public TextView image_view_shape;
    public SketchBackgroundListener sketchBackgroundListener;
    private PhotoSplashSticker photoSplashSticker;
    public PhotoSplashSquareView photoSplashView;
    private ViewGroup viewGroup;

    public interface SketchBackgroundListener {
        void onSaveSketchBackground(Bitmap bitmap);
    }

    public void setPhotoSplashView(boolean z) {
        this.isSplashView = z;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static SketchSquareBackgroundFragment show(@NonNull AppCompatActivity appCompatActivity, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, SketchBackgroundListener sketchBackgroundListener, boolean z) {
        SketchSquareBackgroundFragment splashDialog = new SketchSquareBackgroundFragment();
        splashDialog.setBitmap(bitmap2);
        splashDialog.setSketchBitmap(bitmap4);
        splashDialog.setSketchBackgroundListener(sketchBackgroundListener);
        splashDialog.setPhotoSplashView(z);
        splashDialog.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return splashDialog;
    }

    public void setSketchBitmap(Bitmap bitmap2) {
        this.SketchBitmap = bitmap2;
    }

    public void setSketchBackgroundListener(SketchBackgroundListener sketchBackgroundListener) {
        this.sketchBackgroundListener = sketchBackgroundListener;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @SuppressLint("WrongConstant")
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup2, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_square, viewGroup2, false);
        this.viewGroup = viewGroup2;
        this.image_view_background = inflate.findViewById(R.id.imageViewBackground);
        this.photoSplashView = inflate.findViewById(R.id.splashView);
        this.frame_layout = inflate.findViewById(R.id.frame_layout);
        this.image_view_background.setImageBitmap(this.bitmap);
        this.image_view_shape = inflate.findViewById(R.id.textViewTitle);
        if (this.isSplashView) {
            this.photoSplashView.setImageBitmap(this.SketchBitmap);
            this.image_view_shape.setText("SKETCH BG");
        }
        this.recycler_view_splash = inflate.findViewById(R.id.recyclerViewSplashSquare);
        this.recycler_view_splash.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_splash.setHasFixedSize(true);
        this.recycler_view_splash.setAdapter(new SplashSquareAdapter(getContext(), this, this.isSplashView));
        if (this.isSplashView) {
           this.photoSplashSticker = new PhotoSplashSticker(StickerFileAsset.loadBitmapFromAssets(getContext(), "frame/image_mask_1.webp"), StickerFileAsset.loadBitmapFromAssets(getContext(), "frame/image_frame_1.webp"));
            this.photoSplashView.addSticker(this.photoSplashSticker);
        }

        this.photoSplashView.refreshDrawableState();
        this.photoSplashView.setLayerType(2, null);
        this.image_view_shape.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SketchSquareBackgroundFragment.this.photoSplashView.setcSplashMode(0);
                SketchSquareBackgroundFragment.this.recycler_view_splash.setVisibility(View.VISIBLE);
                SketchSquareBackgroundFragment.this.photoSplashView.refreshDrawableState();
                SketchSquareBackgroundFragment.this.photoSplashView.invalidate();
            }
        });
        inflate.findViewById(R.id.imageViewSaveSplash).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SketchSquareBackgroundFragment.this.sketchBackgroundListener.onSaveSketchBackground(SketchSquareBackgroundFragment.this.photoSplashView.getBitmap(SketchSquareBackgroundFragment.this.bitmap));
                SketchSquareBackgroundFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imageViewCloseSplash).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SketchSquareBackgroundFragment.this.dismiss();
            }
        });
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.photoSplashView.getSticker().release();
        if (this.SketchBitmap != null) {
            this.SketchBitmap.recycle();
        }
        this.SketchBitmap = null;
        this.bitmap = null;
    }

    public void onSelected(PhotoSplashSticker splashSticker2) {
        this.photoSplashView.addSticker(splashSticker2);
    }

}
