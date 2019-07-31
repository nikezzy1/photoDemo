package com.an.myphotodemo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.util.List;

/**
 * des:9宫图适配器
 */
public class NinePicturesAdapter extends BaseAblistViewAdapter<String> {

    private boolean showAdd = true;
    private int picturnNum = 0;
    private boolean isDelete = false;//当前是否显示删除按钮
    private OnClickAddListener onClickAddListener;
    private OnItemClickAddListener onItemClickListener;
    private boolean isAdd=true;//当前是否显示添加按钮


    private static final String TAG = "NinePicturesAdapter";

    public NinePicturesAdapter(Context context, int picturnNum, OnClickAddListener onClickAddListener,OnItemClickAddListener onItemClickListener) {
        super(context);
        this.picturnNum = picturnNum;
        this.onClickAddListener = onClickAddListener;
        this.onItemClickListener=onItemClickListener;
        showAdd();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_photo, parent, false);
        }
        final ImageView imageView = ViewHolderUtil.get(convertView, R.id.img_photo);
        ImageView imgDelete = ViewHolderUtil.get(convertView, R.id.img_delete);
        final String url = getData().get(position);

        //显示图片
        if (TextUtils.isEmpty(url) && showAdd) {
            ImageLoaderUtils.display(mContext, imageView, R.drawable.addphoto);
            imgDelete.setVisibility(View.GONE);
        } else {
            imgDelete.setVisibility(View.VISIBLE);
            ImageLoaderUtils.display(mContext, imageView, url);
        }

        autoHideShowAdd();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //再次选择图片
                if (TextUtils.isEmpty(url)) {
                    if (onClickAddListener != null) {
                        onClickAddListener.onClickAdd(position);
                    }
                } else {
                    //放大查看图片

                    onItemClickListener.onItemClick(position);
                    Log.i(TAG, "onClick: "+position);

                    //BigImagePagerActivity.startImagePagerActivity((Activity) mContext, getData(), position);
                }
            }
        });
        //删除按钮
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(position);
                if (!isDelete && getCount() < 1) {
                    add("");
                    isDelete = true;
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public void setData(List<String> d) {
        boolean hasAdd=false;
        for (int i = 0; i < d.size(); i++) {
            if(TextUtils.isEmpty(d.get(i))){
                hasAdd=true;
                break;
            }
        }
        super.setData(d);
        if(!hasAdd){
            showAdd();
        }
    }

    @Override
    public void addAll(List<String> d) {
        if(isAdd){
            HideAdd();
        }
        super.addAll(d);
        showAdd();
    }

    /**
     * 移除add按钮
     */
    public void autoHideShowAdd(){
        int lastPosition=getData().size()-1;
            if(lastPosition==picturnNum&&getData().get(lastPosition)!=null&& TextUtils.isEmpty(getData().get(lastPosition))){
                getData().remove(lastPosition);
                isAdd=false;
                notifyDataSetChanged();
            }else if(!isAdd){
                showAdd();
            }
    }
    /**
     * 移除add按钮
     */
    public void HideAdd(){
        int lastPosition=getData().size()-1;
        if(getData().get(lastPosition)!=null&& TextUtils.isEmpty(getData().get(lastPosition))){
            getData().remove(lastPosition);
            isAdd=false;
            notifyDataSetChanged();
        }
    }
    /**
     * 显示add按钮
     */
    public void showAdd(){
        if(getData().size()<picturnNum){



            addAt(getData().size(),"");
            isAdd=true;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取图片张数
     * @return
     */
   public int getPhotoCount(){
       return isAdd==true?getCount()-1:getCount();
   }


    /**
     * 加号接口
     */
    public interface OnClickAddListener {
        void onClickAdd(int positin);
    }

    public interface OnItemClickAddListener {
        void onItemClick(int positin);
    }

}