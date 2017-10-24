package com.waterfairy.retrofit2.base;

import com.waterfairy.retrofit2.download.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

/**
 * Created by shui on 2017/5/6.
 */

public class FileWriter {

    /**
     * 文件存储
     *
     * @param onDownloadListener
     * @param responseBody
     * @param info
     */
    public void writeFile(OnProgressListener onDownloadListener, ResponseBody responseBody, DownloadInfo info) {
        File file = new File(info.getSavePath());
        long totalLen = 0;
        long currentLen = info.getCurrentLen();
        if (info.getTotalLen() == 0) {
            totalLen = responseBody.contentLength();
            info.setTotalLen(totalLen);
        } else {
            totalLen = info.getTotalLen();
            info.setLastLen(currentLen);
        }
        if (canSave(file)) {
            FileChannel channelOut = null;
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                channelOut = randomAccessFile.getChannel();
                MappedByteBuffer mappedByteBuffer =
                        channelOut.map(FileChannel.MapMode.READ_WRITE, currentLen, totalLen - currentLen);
                byte[] buffer = new byte[1024 * 8];
                int len;
                int record = 0;
                try {
                    while ((len = responseBody.byteStream().read(buffer)) != -1) {
                        mappedByteBuffer.put(buffer, 0, len);
                        record += len;
                    }
                    responseBody.byteStream().close();
                } catch (IOException | NullPointerException e) {
                    onDownloadListener.onError(BaseManager.PAUSE);
                }
                channelOut.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
                onDownloadListener.onError(BaseManager.ERROR_FILE_SAVE);
            }

        } else {
            onDownloadListener.onError(BaseManager.ERROR_FILE_CREATE);
        }
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    private boolean canSave(File file) {
        boolean canSave = false;
        if (file.exists()) {
            canSave = true;
        } else {
            if (!file.getParentFile().exists()) {
                canSave = file.getParentFile().mkdirs();
            } else {
                canSave = true;
            }
            if (canSave) {
                try {
                    canSave = file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    canSave = false;
                }
            }
        }
        return canSave;
    }


}
