package com.pangge.opencvjni;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding2.internal.Functions;
import com.jakewharton.rxbinding2.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.btn_gray_process) Button btnProc;

    @BindView(R.id.btn_cartoon_process) Button btnCartoon;

    @BindView(R.id.btn_canny_process) Button btnCanny;

    @BindView(R.id.btn_epf_process) Button btnEpf;

    @BindView(R.id.btn_exit) Button btnExit;

    @BindView(R.id.image_view) ImageView imageView;


    private CompositeDisposable compositeDisposable;

    private final static int GRAY_INDEX = 1;
    private final static int CANNY_INDEX = 2;
    private final static int EPF_INDEX = 3;
    private final static int CARTOON_INDEX = 4;
    private final static int EXIT_INDEX = 5;

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;



    private Bitmap bmp;
    private int w;
    private int h;
    private int[] pixels;


    public native int[] grayProc(int[] pixels, int w,int h);
    public native int[] cannyProc(int[] pixels, int w,int h);
    public native int[] epfProc(int[] pixels, int w,int h);
    public native int[] cartoonProc(int[] pixels, int w,int h);




    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();

        ButterKnife.bind(this);
       // btnTest = (Button)findViewById(R.id.btn_canny_process);

        //imageView.setDrawingCacheEnabled(true);

        //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tu1);
        Glide.with(this)
                .load(R.drawable.tu1)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bmp = ((BitmapDrawable)resource).getBitmap();
                        Log.i("null-----","??????!!!!!");

                        return false;
                    }
                })

                .into(imageView);

        //imageView.buildDrawingCache(true);










        Log.i("OK","ssuccessful");
        proImage();

        //imageView.setImageBitmap(bmp);
       /* btnProc.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnCanny.setOnClickListener(this);
        btnCartoon.setOnClickListener(this);*/



        // Example of a call to a native method
       // TextView tv = (TextView) findViewById(R.id.sample_text);
       // tv.setText(stringFromJNI());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void proImage(){
        Log.i("OK-process","ssuccessful");
        //bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        compositeDisposable.add(RxView.clicks(btnProc)
                .flatMap(o -> handlerImage(GRAY_INDEX))
                //.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
               // .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<int[]>() {
                    @Override
                    public void onComplete() {

                        Log.i("con", "ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i(e.toString(), "error");
                    }

                    @Override
                    public void onNext(int[] value) {
                        Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        resultImg.setPixels(value, 0, w, 0, 0, w, h);
                        imageView.setImageBitmap(resultImg);
                        //Glide.with(MainActivity.this).load(resultImg).into(imageView);

                    }
                }));
        compositeDisposable.add(RxView.clicks(btnExit)
                .flatMap(o -> handlerImage(EXIT_INDEX))
                //.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<int[]>() {
                    @Override
                    public void onComplete() {

                        Log.i("con", "ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i(e.toString(), "error");
                    }

                    @Override
                    public void onNext(int[] value) {
                        //getAbsolutePath(this,)

                        imageView.setImageBitmap(bmp);

                    }
                }));
        compositeDisposable.add(RxView.clicks(btnCanny)
                .flatMap(o -> handlerImage(CANNY_INDEX))
                //.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<int[]>() {
                    @Override
                    public void onComplete() {

                        Log.i("con", "ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i(e.toString(), "error");
                    }

                    @Override
                    public void onNext(int[] value) {
                        Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        resultImg.setPixels(value, 0, w, 0, 0, w, h);
                        imageView.setImageBitmap(resultImg);
                    }
                }));
        compositeDisposable.add(RxView.clicks(btnCartoon)
                .flatMap(o -> handlerImage(CARTOON_INDEX))
                //.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<int[]>() {
                    @Override
                    public void onComplete() {

                        Log.i("con", "ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i(e.toString(), "error");
                    }

                    @Override
                    public void onNext(int[] value) {
                        Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        resultImg.setPixels(value, 0, w, 0, 0, w, h);
                        imageView.setImageBitmap(resultImg);

                    }
                }));
        compositeDisposable.add(RxView.clicks(btnEpf)
                .flatMap(o -> handlerImage(EPF_INDEX))
                //.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<int[]>() {
                    @Override
                    public void onComplete() {

                        Log.i("con", "ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i(e.toString(), "error");
                    }

                    @Override
                    public void onNext(int[] value) {
                        Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        resultImg.setPixels(value, 0, w, 0, 0, w, h);
                        imageView.setImageBitmap(resultImg);

                    }
                }));
        compositeDisposable.add(RxView.clicks(imageView)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> addImage()));


    }
    private void addImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == ALBUM_REQUEST_CODE){
                try{
                    Uri uri = data.getData();
                    final String absolutePath = getAbsolutePath(this,uri);
                    Log.i("path = ",absolutePath);
                    //bmp = BitmapFactory.decodeFile(absolutePath);
                    //imageView.setImageBitmap(bmp);

                    Glide.with(this)
                            .load(absolutePath)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    bmp = ((BitmapDrawable)resource).getBitmap();
                                    return false;
                                }
                            })

                            .into(imageView);
                    //bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    //bmp = imageView.getDrawingCache();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String getAbsolutePath(final Context context, final Uri uri){
        if(null == uri) return null;
        final String schema = uri.getScheme();
        String data = null;
        if(schema == null){
            data = uri.getPath();
        }else if(ContentResolver.SCHEME_FILE.equals(schema)){
            data = uri.getPath();
        }else if(ContentResolver.SCHEME_CONTENT.equals(schema)){
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
            if(null != cursor){
                if(cursor.moveToFirst()){
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if(index > -1){
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }

        }

        return data;
    }

    /* private Observable<> addImage(){
            return Observable.create(new ObservableOnSubscribe<Object>() {
                Intent intent = new Intent(Intent.ACTION_PICK,null);

            }).subscribeOn(Schedulers.newThread());
        }*/
    private Observable<int[]> handlerImage(int i){
        return Observable.create(new ObservableOnSubscribe<int[]>() {

            @Override
            public void subscribe(ObservableEmitter<int[]> e) throws Exception {
                Log.i("OK--handler","ssuccessful");
                w = bmp.getWidth();
                h = bmp.getHeight();
                Log.i("handler","sucess---");
                pixels = new int[w*h];

                bmp.getPixels(pixels, 0, w,0,0,w,h);


                int[] resultInt;
                switch (i){
                    case GRAY_INDEX:
                        resultInt = grayProc(pixels, w, h);
                        e.onNext(resultInt);
                        break;
                    case EXIT_INDEX:
                       // resultInt = Proc(pixels, w, h);
                        int[] t = {1,2};
                        e.onNext(t);
                        break;
                    case CANNY_INDEX:
                        resultInt = cannyProc(pixels, w, h);
                        e.onNext(resultInt);
                        break;
                    case CARTOON_INDEX:
                        resultInt = cartoonProc(pixels, w, h);
                        e.onNext(resultInt);
                        break;
                    case EPF_INDEX:
                        resultInt = epfProc(pixels, w, h);
                        e.onNext(resultInt);
                        break;
                    default:
                        int[] t1 = {1,2};
                        e.onNext(t1);
                        break;
                }

            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
