#include "com_jamarfal_instagramndk_MainActivity.h"
#include <android/log.h>
#include <android/bitmap.h>
#include <stdio.h>
#include <stdlib.h>


#define LOG_TAG "libimgprocesadondk"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t alpha;
} rgba;


JNIEXPORT void JNICALL Java_com_jamarfal_instagramndk_MainActivity_addFrameToImage
        (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapMarco) {


    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infoMarco;
    void *pixelsMarco;
    int ret;
    int y;
    int x;
    int borderSize = 10;
    LOGI("convertiSepia");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    if ((ret = AndroidBitmap_getInfo(env, bitmapMarco, &infoMarco)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride,
         infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d",
         infoMarco.width, infoMarco.height, infoMarco.stride,
         infoMarco.format, infoMarco.flags);
    if (infoMarco.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor))
        < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }


    if ((ret = AndroidBitmap_lockPixels(env, bitmapMarco, &pixelsMarco)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    }

    for (y = 0; y < infocolor.height; y++) {
        rgba *colorLine = (rgba *) pixelscolor;
        rgba *marcoLine = (rgba *) pixelsMarco;
        for (x = 0; x < infocolor.width; x++) {
            if (y < borderSize || y >= infocolor.height - borderSize || x < borderSize ||
                x >= infocolor.width - borderSize) {
                marcoLine[x].red = (uint8_t) 0;
                marcoLine[x].green = (uint8_t) 0;
                marcoLine[x].blue = (uint8_t) 0;
            } else {
                marcoLine[x].red = colorLine[x].red;
                marcoLine[x].green = colorLine[x].green;
                marcoLine[x].blue = colorLine[x].blue;
            }
            marcoLine[x].alpha = colorLine[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsMarco = (char *) pixelsMarco + infoMarco.stride;
    }


    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapMarco);
}


JNIEXPORT void JNICALL Java_com_jamarfal_instagramndk_MainActivity_convertImageToSepia
        (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapSepia) {

    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infoSepia;
    void *pixelsSepia;
    int ret;
    int y;
    int x;
    LOGI("convertiSepia");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    if ((ret = AndroidBitmap_getInfo(env, bitmapSepia, &infoSepia)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride,
         infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d",
         infoSepia.width, infoSepia.height, infoSepia.stride,
         infoSepia.format, infoSepia.flags);
    if (infoSepia.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor))
        < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }


    if ((ret = AndroidBitmap_lockPixels(env, bitmapSepia, &pixelsSepia)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    }

    for (y = 0; y < infocolor.height; y++) {
        rgba *colorLine = (rgba *) pixelscolor;
        rgba *sepiaLine = (rgba *) pixelsSepia;
        for (x = 0; x < infocolor.width; x++) {
            double outputRed = (colorLine[x].red * .393) + (colorLine[x].green * .769) +
                               (colorLine[x].blue * .189);
            double outputGreen = (colorLine[x].red * .349) + (colorLine[x].green * .686) +
                                 (colorLine[x].blue * .168);
            double outputBlue = (colorLine[x].red * .272) + (colorLine[x].green * .534) +
                                (colorLine[x].blue * .131);
            if (outputRed > 255) outputRed = 255;
            if (outputGreen > 255) outputGreen = 255;
            if (outputBlue > 255) outputBlue = 255;

            sepiaLine[x].red = (uint8_t) outputRed;
            sepiaLine[x].green = (uint8_t) outputGreen;
            sepiaLine[x].blue = (uint8_t) outputBlue;
            sepiaLine[x].alpha = colorLine[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsSepia = (char *) pixelsSepia + infoSepia.stride;
    }

    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapSepia);

}

JNIEXPORT void JNICALL Java_com_jamarfal_instagramndk_MainActivity_convertImageToGrey
        (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapgray) {
    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infogris;
    void *pixelsgris;
    int ret;
    int y;
    int x;
    LOGI("convertirGrises");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    if ((ret = AndroidBitmap_getInfo(env, bitmapgray, &infogris)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d", infocolor.width,
         infocolor.height, infocolor.stride,
         infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    LOGI("imagen color :: ancho %d;alto %d;avance %d;formato %d;flags %d",
         infogris.width, infogris.height, infogris.stride,
         infogris.format, infogris.flags);
    if (infogris.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap no es formato RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor))
        < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }


    if ((ret = AndroidBitmap_lockPixels(env, bitmapgray, &pixelsgris)) < 0) {
        LOGE("AndroidBitmap_lockPixels() fallo ! error=%d", ret);
    }
    // modificacion pixeles en el algoritmo de escala grises
    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        rgba *grisline = (rgba *) pixelsgris;
        for (x = 0; x < infocolor.width; x++) {
            float output = (line[x].red + line[x].green + line[x].blue) / 3;
            if (output > 255) output = 255;
            grisline[x].red = grisline[x].green = grisline[x].blue =
                    (uint8_t) output;
            grisline[x].alpha = line[x].alpha;
        }
        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsgris = (char *) pixelsgris + infogris.stride;
    }

    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapgray);
}

JNIEXPORT void JNICALL Java_com_jamarfal_instagramndk_MainActivity_convertImageToGreyScale
        (JNIEnv *env, jobject obj, jobject bitmapcolor, jobject bitmapgray) {
    AndroidBitmapInfo infocolor;
    void *pixelscolor;
    AndroidBitmapInfo infogray;
    void *pixelsgray;
    int ret;
    int y;
    int x;


    LOGI("convertToGray");
    if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    if ((ret = AndroidBitmap_getInfo(env, bitmapgray, &infogray)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    LOGI("color image :: width is %d; height is %d; stride is %d; format is %d;flags is %d",
         infocolor.width, infocolor.height, infocolor.stride, infocolor.format, infocolor.flags);
    if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }


    LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d;flags is %d",
         infogray.width, infogray.height, infogray.stride, infogray.format, infogray.flags);
    if (infogray.format != ANDROID_BITMAP_FORMAT_A_8) {
        LOGE("Bitmap format is not A_8 !");
        return;
    }


    if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapgray, &pixelsgray)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    // modify pixels with image processing algorithm

    for (y = 0; y < infocolor.height; y++) {
        rgba *line = (rgba *) pixelscolor;
        uint8_t *grayline = (uint8_t *) pixelsgray;
        for (x = 0; x < infocolor.width; x++) {
            grayline[x] = 0.3 * line[x].red + 0.59 * line[x].green + 0.11 * line[x].blue;
        }

        pixelscolor = (char *) pixelscolor + infocolor.stride;
        pixelsgray = (char *) pixelsgray + infogray.stride;
    }

    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bitmapcolor);
    AndroidBitmap_unlockPixels(env, bitmapgray);
}

JNIEXPORT void JNICALL Java_com_jamarfal_instagramndk_MainActivity_convertImageToSobel
        (JNIEnv *env, jobject obj, jobject bitmapgray, jobject bitmapedges) {

    AndroidBitmapInfo infogray;
    void *pixelsgray;
    AndroidBitmapInfo infoedges;
    void *pixelsedge;
    int ret;
    int y;
    int x;
    int sumX, sumY, sum;
    int i, j;
    int Gx[3][3];
    int Gy[3][3];
    uint8_t *graydata;
    uint8_t *edgedata;

    LOGI("findEdges running");

    Gx[0][0] = -1;
    Gx[0][1] = 0;
    Gx[0][2] = 1;
    Gx[1][0] = -2;
    Gx[1][1] = 0;
    Gx[1][2] = 2;
    Gx[2][0] = -1;
    Gx[2][1] = 0;
    Gx[2][2] = 1;


    Gy[0][0] = 1;
    Gy[0][1] = 2;
    Gy[0][2] = 1;
    Gy[1][0] = 0;
    Gy[1][1] = 0;
    Gy[1][2] = 0;
    Gy[2][0] = -1;
    Gy[2][1] = -2;
    Gy[2][2] = -1;

    if ((ret = AndroidBitmap_getInfo(env, bitmapgray, &infogray)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }


    if ((ret = AndroidBitmap_getInfo(env, bitmapedges, &infoedges)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d;flags is%d",
         infogray.width, infogray.height, infogray.stride, infogray.format, infogray.flags);
    if (infogray.format != ANDROID_BITMAP_FORMAT_A_8) {
        LOGE("Bitmap format is not A_8 !");
        return;
    }

    LOGI("color image :: width is %d; height is %d; stride is %d; format is %d;flags is%d",
         infoedges.width, infoedges.height, infoedges.stride, infoedges.format, infoedges.flags);
    if (infoedges.format != ANDROID_BITMAP_FORMAT_A_8) {
        LOGE("Bitmap format is not A_8 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapgray, &pixelsgray)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmapedges, &pixelsedge)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }


    // modify pixels with image processing algorithm


    LOGI("time to modify pixels....");

    graydata = (uint8_t *) pixelsgray;
    edgedata = (uint8_t *) pixelsedge;

    for (y = 0; y <= infogray.height - 1; y++) {
        for (x = 0; x < infogray.width - 1; x++) {
            sumX = 0;
            sumY = 0;
            // check boundaries
            if (y == 0 || y == infogray.height - 1) {
                sum = 0;
            } else if (x == 0 || x == infogray.width - 1) {
                sum = 0;
            } else {
                // calc X gradient
                for (i = -1; i <= 1; i++) {
                    for (j = -1; j <= 1; j++) {
                        sumX += (int) ((*(graydata + x + i + (y + j)
                                                             * infogray.stride)) *
                                       Gx[i + 1][j + 1]);
                    }
                }

                // calc Y gradient
                for (i = -1; i <= 1; i++) {
                    for (j = -1; j <= 1; j++) {
                        sumY += (int) ((*(graydata + x + i + (y + j)
                                                             * infogray.stride)) *
                                       Gy[i + 1][j + 1]);
                    }
                }

                sum = abs(sumX) + abs(sumY);

            }

            if (sum > 255) sum = 255;
            if (sum < 0) sum = 0;

            *(edgedata + x + y * infogray.width) = 255 - (uint8_t) sum;


        }
    }

    AndroidBitmap_unlockPixels(env, bitmapgray);
    AndroidBitmap_unlockPixels(env, bitmapedges);
}
