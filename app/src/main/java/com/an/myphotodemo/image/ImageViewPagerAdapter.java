package com.an.myphotodemo.image;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;


//ViewPager的适配器，用来生成Fragment供ViewPager显示

public class ImageViewPagerAdapter extends FragmentStatePagerAdapter {

    public String[] fileList;

    public ImageViewPagerAdapter(FragmentManager fm, String[] fileList) {
        super(fm);
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList == null ? 0 : fileList.length;
    }

    @Override
    public Fragment getItem(int position) {
        String url = fileList[position];
        return ImageDetailFragment.newInstance(url);
    }

}

