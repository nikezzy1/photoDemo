package com.an.myphotodemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.an.myphotodemo.image.ImagePagerActivity;
import com.an.myphotodemo.image.PhotoUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import me.iwf.photopicker.widget.SquareItemLayout;



public class MainActivity extends AppCompatActivity {

    private String filename;
    private String name;
    Button btn_photo;
    private static final int REQUEST_CODE = 100;
    private NoScrollGridView itemLayout;
    private ArrayList<String> photos;
    private List<String> photossss;
    private NinePicturesAdapter ninePicturesAdapter;
    protected ArrayList<String> addresses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //创建监听器 绑定Button资源
          btn_photo = (Button) findViewById(R.id.btn_photo);
        //设置Button监听
         btn_photo.setOnClickListener(new MyButtonListener());

        filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/photos/";
        //为不可滑动GridView设置适配器
        itemLayout = (NoScrollGridView) findViewById(R.id.recycler_view);




        //九宫格图片适配器，一次最多选择9张图片
        ninePicturesAdapter = new NinePicturesAdapter(this, 9, new NinePicturesAdapter.OnClickAddListener() {
            @Override
            public void onClickAdd(int positin) {
                 //choosePhoto();
                  takephoto();
            }
        }, new NinePicturesAdapter.OnItemClickAddListener() {
            @Override
            public void onItemClick(int positin) {

                Log.i(TAG, "------------onItemClick: "+positin);

                //得到图片Url
                String[] array = new String[ninePicturesAdapter.getPhotoCount()];
                // List转换成数组
                for (int i = 0; i < photossss.size()-1; i++) {
                    array[i] = photossss.get(i);
                }

                Log.i(TAG, "----array:--- "+array.length);
                //Toast.makeText(MainActivity.this,"查看大图",Toast.LENGTH_SHORT).show();
                //查看大图
                imageBrower(positin,array);
            }
        });
        //不可滑动GridView设置为九宫格格式
        itemLayout.setAdapter(ninePicturesAdapter);

    }






    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在这保存 name 的数据
        outState.putString("name", name);
    }

    //读取数据
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name = savedInstanceState.getString("name");
    }


     //实现OnClickListener接口
    private class MyButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this,"打开了相机",Toast.LENGTH_SHORT).show();
            //拍照
            takephoto();
        }
    }

    //拍照
    private void takephoto() {
        //判断是否有相机
        boolean b = PhotoUtils.hasCamera(MainActivity.this);
        if (b) {
            name = getPhotoFileName();

            File mFile = new File(filename);
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            File mPhotoFile = new File(filename, name);

            //启动相机
            Intent captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            Uri fileUri = FileProvider.getUriForFile(MainActivity.this, "com.an.myphotodemo.fileprovider", mPhotoFile);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

            startActivityForResult(captureIntent, 0);
        } else {
            Toast.makeText(MainActivity.this, "系统无相机", Toast.LENGTH_SHORT).show();
        }
    }

    //设置拍照后图片名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HH-mm-ss");
        return dateFormat.format(date) + ".jpg";
    }






    /**
     * 每一张图片放大查看
     * @param position
     * @param urls
     */
    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }



    /**
     * 开启图片选择器
     */
    private void choosePhoto() {
        PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
        intent.setPhotoCount(9);
        intent.setShowCamera(true);
        startActivityForResult(intent, REQUEST_CODE);

        //ImageLoaderUtils.display(context,imageView,path);
    }

    private static final String TAG = "MainActivity";




    /**
     * 接受返回的图片数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //获取到相机拍的照片
           if (requestCode==0) {

            String filestr = filename + "/" + name;

            //向flowlayout中添加图片
            //调试打印照相图片路径
            System.out.println("=========================1=============================");
            System.out.println(filestr);
               if(ninePicturesAdapter!=null) {
                   //将String类型转换成ListArray<String>
                   List addresses = Arrays.asList(filestr);



                   //九宫格里添加图片
                   ninePicturesAdapter.addAll(addresses);
                   photossss = ninePicturesAdapter.getData();


               }
        }

        //获取到相册里的图片
         else  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);

                for (int i = 0; i < photos.size(); i++) {
                    Log.i(TAG, "----------onActivityResult: "+ photos.get(i));
                }
                    if(ninePicturesAdapter!=null) {
                        Log.i(TAG, "----------photossss: ========");
                        //九宫格里添加图片
                        ninePicturesAdapter.addAll(photos);
                        //相册照片路径打印
                        Log.i(TAG, "==========================================================");
                        System.out.println(photos);
                        photossss = ninePicturesAdapter.getData();
                        Log.i(TAG, "----------photossss: ========"+photossss.size());

                    }
            }
        }

    }
}
