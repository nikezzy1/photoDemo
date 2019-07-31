package com.an.myphotodemo.image;


/*
 ** 查看大图移动缩放
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.an.myphotodemo.ImageLoaderUtils;
import com.an.myphotodemo.R;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import uk.co.senab.photoview.PhotoView;

//从Bundle中提取图片Url，Glide加载到PhotoView

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;




	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);
		return f;
	}

	private static final String TAG = "ImageDetailFragment";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

		Log.i(TAG, "onCreate: "+mImageUrl);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);


		Glide.with(getContext()).load(mImageUrl).into(mImageView);

		//progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}




	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ImageLoaderUtils.display(getActivity(),mImageView,mImageUrl);

	}
}
