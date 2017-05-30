# NDKImage
# canny
    Mat srcImage(h, w, CV_8UC4, (unsigned char *) srcBuf);
    Mat src = srcImage.clone();
    Mat grayImage;
    Mat blurImage;
 

    cvtColor(src, grayImage, COLOR_RGBA2GRAY);
    //medianBlur(grayImage, blurImage, 7);
    blur(grayImage,blurImage,Size(3,3));
    Canny(blurImage,blurImage,50,150,3);
    srcImage = Scalar::all(0);
    src.copyTo(srcImage,blurImage);

# cartoon
由于双边滤镜平滑平坦的区域，同时保持边缘清晰，因此非常适合将RGB图像转换为卡通图像。

    Mat srcImage(h, w, CV_8UC4, (unsigned char*)srcBuf);
    Mat src = srcImage;
    Mat colorImage;
    Mat grayImage;
    Mat blurImage;
    Mat edgeImage;
    Mat dstImage;
    
     //TODO 1 --- use bilateral filter reduce the color of image
    
    //channel 4->3 
    cvtColor(srcImage,colorImage,COLOR_RGBA2RGB);

    //Apply a bilateral filter to reduce the color palette of the image.
    //1 and 3
    bilateralFilter(colorImage,dstImage,9,9,7);

    //channel 3->4
    cvtColor(dstImage,dstImage,COLOR_RGB2RGBA);
    
     //TODO 2 --- covert color to gray

    //get gray scale
    cvtColor(src, grayImage,COLOR_RGB2GRAY);
    
     //TODO 3 --- 降低图像噪点
    
    //Apply a median blur to reduce image noise in the grayscale image.
    //1 ,3 ,4
    medianBlur(grayImage,blurImage,7);
    
     //TODO 4 --- 使用自适应阀值处理灰度图像，创造轮廓
   
  
    //Create an edge mask from the grayscale image using adaptive thresholding.
    //detect and enhance edges
    adaptiveThreshold(blurImage, edgeImage,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,9,2);
    
     //TODO 5 --- 将1 彩图与 4轮廓 叠加
    
    //convert back to color, bit-AND with color image   
    cvtColor(edgeImage,edgeImage,COLOR_GRAY2RGBA);
    bitwise_and(dstImage,edgeImage,srcImage);


# gray
    jboolean ptFalse = false;

    jint* srcBuf = env->GetIntArrayElements(buf, &ptFalse);
    if(srcBuf == NULL){
        return 0;
    }
    int size=w * h;
    Mat srcImage(h, w, CV_8UC4, (unsigned char*)srcBuf);
    Mat grayImage;
    cvtColor(srcImage, grayImage, COLOR_BGRA2GRAY);
    cvtColor(grayImage, srcImage, COLOR_GRAY2BGRA);
    
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result,0,size,srcBuf);
    env->ReleaseIntArrayElements(buf,srcBuf,0);
    return result;

# glide
在大多数情况下，我们建议您使用Glide库来获取，解码和显示应用程序中的位图。在处理与在Android上使用位图和其他图像相关的这些和其他任务时，Glide可以提取大部分复杂性。

https://developer.android.com/topic/performance/graphics/index.html

在Android应用中加载位图很棘手的几个原因：

* 位图很容易耗尽应用程序的内存预算。例如，Pixel手机上的相机拍摄的照片最高可达4048x3036像素（1200万像素）。如果使用的位图配置是ARGB_8888，Android 2.3（API等级9）和更高版本的默认值，将单张照片加载到内存中大约需要48MB的内存（4048 * 3036 * 4字节）。这样大的内存需求可以立即占用应用程序可用的所有内存。
* UI线程上的加载位图可能会降低应用程序的性能，导致响应速度慢甚至ANR消息。因此，在使用位图时适当地管理线程很重要。
* 如果您的应用程序正在将多个位图加载到内存中，则需要熟练地管理内存和磁盘缓存。否则，您应用程序的UI的响应性和流动性可能会受到影响。

Glide以异步方式加载图像，因此，在启动加载操作后，对位图的测试将返回null，因为图像尚未加载。
要知道您的图像何时确实加载，可以在Glide请求中设置一个监听器

    Glide.with(this)
                .load(R.drawable.tu1)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    // drawable is in resource variable
                        bmp = ((BitmapDrawable)resource).getBitmap();
                     

                        return false;
                    }
                })

                .into(imageView);

