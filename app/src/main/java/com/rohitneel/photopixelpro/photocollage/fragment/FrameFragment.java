package com.rohitneel.photopixelpro.photocollage.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitneel.photopixelpro.R;
import com.rohitneel.photopixelpro.photocollage.adapters.FrameColorAdapter;
import com.rohitneel.photopixelpro.photocollage.adapters.FrameGradientAdapter;
import com.rohitneel.photopixelpro.photocollage.utils.DegreeSeekBar;
import com.rohitneel.photopixelpro.photocollage.utils.FilterUtils;
import com.rohitneel.photopixelpro.photocollage.utils.SystemUtil;

public class FrameFragment extends DialogFragment implements FrameGradientAdapter.FrameListener,
        FrameColorAdapter.FrameListener {

    private static final String TAG = "FrameFragment";
    private RelativeLayout relative_layout_loading;
    private Bitmap bitmap;
    private Bitmap blurBitmap;
    private DegreeSeekBar adjustSeekBar;
    public RatioSaveListener ratioSaveListener;
    public ImageView image_view_ratio;
    private RelativeLayout relativeLayoutColor;
    private RelativeLayout relativeLayoutGradient;
    private TextView textViewColor;
    private TextView textViewGradient;
    private View viewColor;
    private View viewGradient;
    private ConstraintLayout constraint_layout_ratio;
    public RecyclerView recycler_view_gradient;
    public RecyclerView recycler_view_color;
    public FrameLayout frame_layout_wrapper;

    public interface RatioSaveListener {
        void ratioSavedBitmap(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static FrameFragment show(@NonNull AppCompatActivity appCompatActivity, RatioSaveListener ratioSaveListener, Bitmap mBitmap, Bitmap iBitmap) {
        FrameFragment ratioFragment = new FrameFragment();
        ratioFragment.setBitmap(mBitmap);
        ratioFragment.setBlurBitmap(iBitmap);
        ratioFragment.setRatioSaveListener(ratioSaveListener);
        ratioFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return ratioFragment;
    }

    public void setBlurBitmap(Bitmap bitmap2) {
        this.blurBitmap = bitmap2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void setRatioSaveListener(RatioSaveListener iRatioSaveListener) {
        this.ratioSaveListener = iRatioSaveListener;
    }

    @SuppressLint("WrongConstant")
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_frame, viewGroup, false);
        this.relative_layout_loading = inflate.findViewById(R.id.relative_layout_loading);
        this.relative_layout_loading.setVisibility(View.GONE);
        this.relativeLayoutColor = inflate.findViewById(R.id.relativeLayoutColor);
        this.relativeLayoutGradient = inflate.findViewById(R.id.relativeLayoutGradient);
        this.viewColor = inflate.findViewById(R.id.ViewColor);
        this.viewGradient = inflate.findViewById(R.id.ViewGradient);
        this.textViewColor  = inflate.findViewById(R.id.textViewColor);
        this.textViewGradient  = inflate.findViewById(R.id.textViewGradient);
        this.recycler_view_gradient = inflate.findViewById(R.id.recyclerViewGradient);
        this.recycler_view_gradient.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_gradient.setAdapter(new FrameGradientAdapter(getContext(), this));
        this.recycler_view_gradient.setVisibility(View.GONE);
        this.recycler_view_color = inflate.findViewById(R.id.recyclerViewColor);
        this.recycler_view_color.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_color.setAdapter(new FrameColorAdapter(getContext(), this));
        this.recycler_view_color.setVisibility(View.VISIBLE);

        inflate.findViewById(R.id.relativeLayoutColor).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                recycler_view_color.setVisibility(View.VISIBLE);
                recycler_view_gradient.setVisibility(View.GONE);
                textViewColor.setTextColor(getResources().getColor(R.color.white));
                textViewGradient.setTextColor(getResources().getColor(R.color.grayText));
                viewColor.setVisibility(View.VISIBLE);
                viewGradient.setVisibility(View.INVISIBLE);
            }
        });

        inflate.findViewById(R.id.relativeLayoutGradient).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               recycler_view_color.setVisibility(View.GONE);
                textViewColor.setTextColor(getResources().getColor(R.color.grayText));
                textViewGradient.setTextColor(getResources().getColor(R.color.white));
               recycler_view_gradient.setVisibility(View.VISIBLE);
               viewColor.setVisibility(View.INVISIBLE);
               viewGradient.setVisibility(View.VISIBLE);
            }
        });

        this.adjustSeekBar = inflate.findViewById(R.id.seekbarOverlay);
        this.adjustSeekBar.setCenterTextColor(getResources().getColor(R.color.mainColor));
        this.adjustSeekBar.setTextColor(getResources().getColor(R.color.white));
        this.adjustSeekBar.setPointColor(getResources().getColor(R.color.white));
        this.adjustSeekBar.setDegreeRange(0, 50);
        this.adjustSeekBar.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            public void onScrollEnd() {
            }

            public void onScrollStart() {
            }

            public void onScroll(int i) {
                int dpToPx = SystemUtil.dpToPx(FrameFragment.this.getContext(), i);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) FrameFragment.this.image_view_ratio.getLayoutParams();
                layoutParams.setMargins(dpToPx, dpToPx, dpToPx, dpToPx);
                FrameFragment.this.image_view_ratio.setLayoutParams(layoutParams);
            }
        });

        this.image_view_ratio = inflate.findViewById(R.id.imageViewFrame);
        this.image_view_ratio.setImageBitmap(this.bitmap);
        this.image_view_ratio.setAdjustViewBounds(true);
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.constraint_layout_ratio = inflate.findViewById(R.id.constraintLayoutFrame);
        this.frame_layout_wrapper = inflate.findViewById(R.id.frameLayoutWrapper);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_ratio);
        ConstraintSet constraintSet2 = constraintSet;
        constraintSet2.connect(this.frame_layout_wrapper.getId(), 3, this.constraint_layout_ratio.getId(), 3, 0);
        constraintSet2.connect(this.frame_layout_wrapper.getId(), 1, this.constraint_layout_ratio.getId(), 1, 0);
        constraintSet2.connect(this.frame_layout_wrapper.getId(), 4, this.constraint_layout_ratio.getId(), 4, 0);
        constraintSet2.connect(this.frame_layout_wrapper.getId(), 2, this.constraint_layout_ratio.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_ratio);
        inflate.findViewById(R.id.imageViewCloseFrame).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FrameFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imageViewSaveFrame).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new SaveRatioView().execute(getBitmapFromView(FrameFragment.this.frame_layout_wrapper));
            }
        });
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    public void onStop() {
        super.onStop();
    }

    class SaveRatioView extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        SaveRatioView() {
        }

        public void onPreExecute() {
            FrameFragment.this.mLoading(true);
        }

        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap cloneBitmap = FilterUtils.cloneBitmap(bitmapArr[0]);
            bitmapArr[0].recycle();
            bitmapArr[0] = null;
            return cloneBitmap;
        }

        public void onPostExecute(Bitmap bitmap) {
            FrameFragment.this.mLoading(false);
            FrameFragment.this.ratioSaveListener.ratioSavedBitmap(bitmap);
            FrameFragment.this.dismiss();
        }
    }

    @Override
    public void onGradientSelected(FrameColorAdapter.SquareView squareView) {
        if (squareView.isColor) {
            this.frame_layout_wrapper.setBackgroundColor(squareView.drawableId);
        }  else {
            this.frame_layout_wrapper.setBackgroundResource(squareView.drawableId);
        }
        this.frame_layout_wrapper.invalidate();
    }

    @Override
    public void onBackgroundSelected(FrameGradientAdapter.SquareView squareView) {
        if (squareView.isColor) {
            this.frame_layout_wrapper.setBackgroundColor(squareView.drawableId);
        }  else {
            this.frame_layout_wrapper.setBackgroundResource(squareView.drawableId);
        }
        this.frame_layout_wrapper.invalidate();
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.blurBitmap != null) {
            this.blurBitmap.recycle();
            this.blurBitmap = null;
        }
        this.bitmap = null;
    }

    public void onColorChanged(String str) {
        this.image_view_ratio.setBackgroundColor(Color.parseColor(str));
        if (!str.equals("#00000000")) {
            int dpToPx = SystemUtil.dpToPx(getContext(), 35);
            this.image_view_ratio.setPadding(dpToPx, dpToPx, dpToPx, dpToPx);
            return;
        }
        this.image_view_ratio.setPadding(0, 0, 0, 0);
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void mLoading(boolean z) {
        if (z) {
            getActivity().getWindow().setFlags(16, 16);
            this.relative_layout_loading.setVisibility(View.VISIBLE);
            return;
        }
        getActivity().getWindow().clearFlags(16);
        this.relative_layout_loading.setVisibility(View.GONE);
    }
}
