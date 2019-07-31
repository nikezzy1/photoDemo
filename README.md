# photoDemo

一：基本效果

1.    拍照------>获取略缩图并加载到布局上

2.    相册选取(9张一次)------------->可以预览缩放-------->获取略缩图并加载到布局上
3.     点击略缩图预览大图，移动缩放，可以删除图片



引入依赖：

implementation'me.iwf.photopicker:PhotoPicker:0.1.8'

implementation'com.github.bumptech.glide:glide:3.6.1'

implementation'com.commit451:PhotoView:1.2.4'



allprojects {

repositories {

jcenter()

maven { url"https://jitpack.io" }

}

}



二：效果图（九宫格选择，在MainActivity里打开choosePhoto方法即可，效果图就先不放了）







三：核心代码



1）MainActivity

public class MainActivity extends AppCompatActivity {

private Stringfilename;

private Stringname;

Buttonbtn_photo;

private static final int REQUEST_CODE =100;

private NoScrollGridViewitemLayout;

private ArrayListphotos;

private Listphotossss;

private NinePicturesAdapterninePicturesAdapter;

protected ArrayListaddresses =new ArrayList<>();

@Override

    protected void onCreate(Bundle savedInstanceState) {

super.onCreate(savedInstanceState);

setContentView(R.layout.activity_main);

//创建监听器 绑定Button资源

          btn_photo = (Button) findViewById(R.id.btn_photo);

//设置Button监听

        btn_photo.setOnClickListener(new MyButtonListener());

filename = Environment.getExternalStorageDirectory().getAbsolutePath() +"/photos/";

//为不可滑动GridView设置适配器

        itemLayout = (NoScrollGridView) findViewById(R.id.recycler_view);

//九宫格图片适配器，一次最多选择9张图片

        ninePicturesAdapter =new NinePicturesAdapter(this,9,new NinePicturesAdapter.OnClickAddListener() {

@Override

            public void onClickAdd(int positin) {

//choosePhoto();

                  takephoto();

}

},new NinePicturesAdapter.OnItemClickAddListener() {

@Override

            public void onItemClick(int positin) {

Log.i(TAG,"------------onItemClick: "+positin);

//得到图片Url

                String[] array =new String[ninePicturesAdapter.getPhotoCount()];

// List转换成数组

                for (int i =0; i

array[i] =photossss.get(i);

}

Log.i(TAG,"----array:--- "+array.length);

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

        outState.putString("name",name);

}

//读取数据

    @Override

    protected void onRestoreInstanceState(Bundle savedInstanceState) {

super.onRestoreInstanceState(savedInstanceState);

name = savedInstanceState.getString("name");

}

//实现OnClickListener接口

    private class MyButtonListenerimplements View.OnClickListener {

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

File mFile =new File(filename);

if (!mFile.exists()) {

mFile.mkdirs();

}

File mPhotoFile =new File(filename,name);

//启动相机

            Intent captureIntent =new Intent("android.media.action.IMAGE_CAPTURE");

Uri fileUri = FileProvider.getUriForFile(MainActivity.this,"com.an.myphotodemo.fileprovider", mPhotoFile);

captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

startActivityForResult(captureIntent,0);

}else {

Toast.makeText(MainActivity.this,"系统无相机", Toast.LENGTH_SHORT).show();

}

}

//设置拍照后图片名称

    private String getPhotoFileName() {

Date date =new Date(System.currentTimeMillis());

SimpleDateFormat dateFormat =new SimpleDateFormat(

"'IMG'_yyyyMMdd_HH-mm-ss");

return dateFormat.format(date) +".jpg";

}

/**

* 每一张图片放大查看

    * @param position

    * @param urls

    */

    private void imageBrower(int position, String[] urls) {

Intent intent =new Intent(this, ImagePagerActivity.class);

// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取

        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);

intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);

startActivity(intent);

}

/**

* 开启图片选择器

*/

    private void choosePhoto() {

PhotoPickerIntent intent =new PhotoPickerIntent(MainActivity.this);

intent.setPhotoCount(9);

intent.setShowCamera(true);

startActivityForResult(intent,REQUEST_CODE);

//ImageLoaderUtils.display(context,imageView,path);

    }

private static final StringTAG ="MainActivity";

/**

* 接受返回的图片数据

    * @param requestCode

    * @param resultCode

    * @param data

    */

    @Override

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {

super.onActivityResult(requestCode, resultCode, data);

//获取到相机拍的照片

          if (requestCode==0) {

String filestr =filename +"/" +name;

//向flowlayout中添加图片

//调试打印照相图片路径

            System.out.println("=========================1=============================");

System.out.println(filestr);

if(ninePicturesAdapter!=null) {

//将String类型转换成ListArray

                  List addresses = Arrays.asList(filestr);

//九宫格里添加图片

                  ninePicturesAdapter.addAll(addresses);

photossss =ninePicturesAdapter.getData();

}

}

//获取到相册里的图片

        else  if (resultCode ==RESULT_OK && requestCode ==REQUEST_CODE) {

if (data !=null) {

photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);

for (int i =0; i

Log.i(TAG,"----------onActivityResult: "+photos.get(i));

}

if(ninePicturesAdapter!=null) {

Log.i(TAG,"----------photossss: ========");

//九宫格里添加图片

                        ninePicturesAdapter.addAll(photos);

//相册照片路径打印

                        Log.i(TAG,"==========================================================");

System.out.println(photos);

photossss =ninePicturesAdapter.getData();

Log.i(TAG,"----------photossss: ========"+photossss.size());

}

}

}

}

}



2)ImageLoaderUtils：图片加载工具类 使用glide框架封装，将图片加载到Gridview

public class ImageLoaderUtils {

public static void display(Context context, ImageView imageView, String url,int placeholder,int error) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url).placeholder(placeholder)

.error(error).crossFade().into(imageView);

}

public static void display(Context context, ImageView imageView, String url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url)

.diskCacheStrategy(DiskCacheStrategy.ALL)

.fitCenter()

.placeholder(R.drawable.ic_image_loading)

.error(R.drawable.ic_empty_picture)

.crossFade().into(imageView);

}

public static void display(Context context, ImageView imageView, File url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url)

.diskCacheStrategy(DiskCacheStrategy.ALL)

.fitCenter()

.placeholder(R.drawable.ic_image_loading)

.error(R.drawable.ic_empty_picture)

.crossFade().into(imageView);

}

public static void displaySmallPhoto(Context context, ImageView imageView, String url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url).asBitmap()

.diskCacheStrategy(DiskCacheStrategy.ALL)

.placeholder(R.drawable.ic_image_loading)

.error(R.drawable.ic_empty_picture)

.thumbnail(0.5f)

.into(imageView);

}

public static void displayBigPhoto(Context context, ImageView imageView, String url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url).asBitmap()

.format(DecodeFormat.PREFER_ARGB_8888)

.diskCacheStrategy(DiskCacheStrategy.ALL)

.placeholder(R.drawable.ic_image_loading)

.error(R.drawable.ic_empty_picture)

.into(imageView);

}

public static void display(Context context, ImageView imageView,int url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url)

.diskCacheStrategy(DiskCacheStrategy.ALL)

//.fitCenter()

                .placeholder(R.drawable.ic_image_loading)

.error(R.drawable.ic_empty_picture)

.crossFade().into(imageView);

}

public static void displayRound(Context context, ImageView imageView, String url) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(url)

.diskCacheStrategy(DiskCacheStrategy.ALL)

.error(R.drawable.toux2)

.fitCenter().transform(new GlideRoundTransformUtil(context)).into(imageView);

}

public static void displayRound(Context context, ImageView imageView,int resId) {

if (imageView ==null) {

throw new IllegalArgumentException("argument error");

}

Glide.with(context).load(resId)

.diskCacheStrategy(DiskCacheStrategy.ALL)

.error(R.drawable.toux2)

.fitCenter().transform(new GlideRoundTransformUtil(context)).into(imageView);

}

}



3)xml

1>image_detail_pager.xml

<?xml version="1.0" encoding="utf-8"?>

    android:layout_width="match_parent"

    android:layout_height="match_parent" >

        android:id="@+id/pager"

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        />

        android:id="@+id/btn_back1"

        android:layout_width="wrap_content"

        android:layout_height="wrap_content"

        android:text="返回" />





        android:id="@+id/indicator"

        android:layout_width="match_parent"

        android:layout_height="wrap_content"

        android:layout_gravity="bottom"

        android:gravity="center"

        android:textSize="18sp"

        android:textColor="@android:color/white"

        android:text="@string/viewpager_indicator"

        android:background="@android:color/transparent" />



2>image_detail_fragment.xml



    android:layout_width="match_parent"

    android:layout_height="match_parent"

    >

        android:id="@+id/image"

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:scaleType="fitCenter"

        />



说明一下，上面俩个xml文件分别对应viewpage 和 photoview，一个用来滑动页，一个具体的放缩



具体源码请移步我的GitHub：https://github.com/nikezzy1/photoDemo.git





