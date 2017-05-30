#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
extern "C"{
using namespace std;
using namespace cv;

JNIEXPORT jintArray JNICALL
Java_com_pangge_opencvjni_MainActivity_grayProc(
        JNIEnv *env,
        jobject obj, jintArray buf, jint w, jint h) {
    // std::string hello = "Hello from C++";
    // return env->NewStringUTF(hello.c_str());
    jboolean ptFalse = false;

    jint* srcBuf = env->GetIntArrayElements(buf, &ptFalse);
    if(srcBuf == NULL){
        return 0;
    }
    int size=w * h;



    Mat srcImage(h, w, CV_8UC4, (unsigned char*)srcBuf);

    //cv::Mat srcImage(h, w, CV_8UC4, (unsigned char*)srcBuf);
    Mat grayImage;
    //cvtColor(srcImage, grayImage, COLOR_BGRA2GRAY);
    cvtColor(srcImage, grayImage, COLOR_BGRA2GRAY);
    cvtColor(grayImage, srcImage, COLOR_GRAY2BGRA);

    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result,0,size,srcBuf);
    env->ReleaseIntArrayElements(buf,srcBuf,0);
    return result;





}
JNIEXPORT jintArray JNICALL
Java_com_pangge_opencvjni_MainActivity_cannyProc(
        JNIEnv *env,
        jobject obj, jintArray buf, jint w, jint h) {
    jboolean ptFalse = false;

    jint *srcBuf = env->GetIntArrayElements(buf, &ptFalse);
    if (srcBuf == NULL) {
        return 0;
    }
    int size = w * h;


    Mat srcImage(h, w, CV_8UC4, (unsigned char *) srcBuf);
    Mat src = srcImage.clone();
    Mat grayImage;
    Mat blurImage;
    Mat dst;

    cvtColor(src, grayImage, COLOR_RGBA2GRAY);
    //medianBlur(grayImage, blurImage, 7);
    blur(grayImage,blurImage,Size(3,3));
    Canny(blurImage,blurImage,50,150,3);
    srcImage = Scalar::all(0);
    src.copyTo(srcImage,blurImage);

    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result,0,size,srcBuf);
    env->ReleaseIntArrayElements(buf,srcBuf,0);
    return result;



}

JNIEXPORT jintArray JNICALL
Java_com_pangge_opencvjni_MainActivity_epfProc(
        JNIEnv *env,
        jobject obj, jintArray buf, jint w, jint h) {
    jboolean ptFalse = false;

    jint *srcBuf = env->GetIntArrayElements(buf, &ptFalse);
    if (srcBuf == NULL) {
        return 0;
    }
    int size = w * h;


    Mat srcImage(h, w, CV_8UC4, (unsigned char *) srcBuf);
    Mat src = srcImage.clone();
    Mat grayImage;
    Mat blurImage;
    Mat dst;

    cvtColor(src, grayImage, COLOR_RGBA2GRAY);

    Canny(grayImage,srcImage,10,100);


    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result,0,size,srcBuf);
    env->ReleaseIntArrayElements(buf,srcBuf,0);
    return result;



}

JNIEXPORT jintArray JNICALL
Java_com_pangge_opencvjni_MainActivity_cartoonProc(
        JNIEnv *env,
        jobject obj, jintArray buf, jint w, jint h) {
    jboolean ptFalse = false;

    jint* srcBuf = env->GetIntArrayElements(buf, &ptFalse);
    if(srcBuf == NULL){
        return 0;
    }
    int size=w * h;



    Mat srcImage(h, w, CV_8UC4, (unsigned char*)srcBuf);
    Mat src = srcImage;
    Mat colorImage;
    Mat grayImage;
    Mat blurImage;
    Mat edgeImage;
    Mat dstImage;
    Mat testImage;
    cvtColor(srcImage,colorImage,COLOR_RGBA2RGB);

    /*for (int i = 0; i < 2; ++i) {
        pyrDown(colorImage,colorImage);

    }


    for (int i = 0; i < 7; ++i) {
        bilateralFilter(colorImage,testImage,9,9,7);
        bilateralFilter(testImage,colorImage,9,9,7);


    }*/

    bilateralFilter(colorImage,dstImage,9,9,7);
   /* for (int i = 0; i < 2; ++i) {
        pyrUp(colorImage,colorImage);
       // pyrUp(dstImage,dstImage);

    }*/
   // cvtColor(dstImage,srcImage,COLOR_BGR2BGRA,4);
    //Apply a bilateral filter to reduce the color palette of the image.
    //1 and 3


    cvtColor(dstImage,dstImage,COLOR_RGB2RGBA);


    cvtColor(src, grayImage,COLOR_RGB2GRAY);



    //Apply a median blur to reduce image noise in the grayscale image.
    //1 ,3 ,4
    medianBlur(grayImage,blurImage,7);
    //Create an edge mask from the grayscale image using adaptive thresholding.

    //detect and enhance edges
    adaptiveThreshold(blurImage, edgeImage,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,9,2);
    //convert back to color, bit-AND with color image
    cvtColor(edgeImage,edgeImage,COLOR_GRAY2RGBA);
    //cvtColor(edgeImage,srcImage,COLOR_RGBA2RGB);


    //jintArray result = env->NewIntArray(size);*/
    bitwise_and(dstImage,edgeImage,srcImage);



    //在Java中申请一块内存
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result,0,size,srcBuf);
    env->ReleaseIntArrayElements(buf,srcBuf,0);
    return result;






}




}

