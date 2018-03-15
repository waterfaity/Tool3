package com.waterfairy.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.util.HashMap;


/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/9/27
 * @Description:
 */

public class VideoUtils {

    private static final String TAG = "videoUtils";

    public static File createVideoThumbnailFile(String savePath, String url, int width, int height) {
        Bitmap videoThumbnail = createVideoThumbnail(url, width, height);
        if (videoThumbnail == null) return null;
        ImageUtils.saveBitmap(savePath, videoThumbnail);
        return new File(savePath);
    }

    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    //获取视频封面
//    compile 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14'
//    compile 'com.github.wseemann:FFmpegMediaMetadataRetriever-armeabi:1.0.14'
//    public static Bitmap createVideoThumbnail2(String sourcePath, int width, int height) {
//        Log.i(TAG, "createVideoThumbnail2: a:"+System.currentTimeMillis());
//        Bitmap bitmap = null;
//        FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
//        try {
//            fmmr.setDataSource(sourcePath);
//            bitmap = fmmr.getFrameAtTime();
//
//            if (bitmap != null) {
//                Bitmap b2 = fmmr
//                        .getFrameAtTime(
//                                4000000,
//                                FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                Log.i(TAG, "createVideoThumbnail2: b:"+System.currentTimeMillis());
//                if (b2 != null) {
//                    bitmap = b2;
//                }
//                if (bitmap.getWidth() > width) {// 如果图片宽度规格超过640px,则进行压缩
//                    bitmap = ThumbnailUtils.extractThumbnail(bitmap,
//                            width, height,
//                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//                    Log.i(TAG, "createVideoThumbnail2: c:"+System.currentTimeMillis());                }
//            }
//        } catch (IllegalArgumentException ex) {
//            ex.printStackTrace();
//        } finally {
//            fmmr.release();
//        }
//        return bitmap;
//    }

}
