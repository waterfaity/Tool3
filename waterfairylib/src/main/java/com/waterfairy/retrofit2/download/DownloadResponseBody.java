package com.waterfairy.retrofit2.download;

import com.waterfairy.retrofit2.base.OnBaseProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by shui on 2017/4/26.
 */

public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private OnBaseProgressListener downloadingListener;
    private BufferedSource bufferedSource;
    private long totalLen;

    public DownloadResponseBody(ResponseBody responseBody, OnBaseProgressListener downloadingListener) {
        this.responseBody = responseBody;
        this.downloadingListener = downloadingListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        if (totalLen == 0)
            totalLen = responseBody.contentLength();
        return totalLen;
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) bufferedSource = Okio.buffer(source(responseBody.source()));
        return bufferedSource;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long readTotal = 0l;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                readTotal += bytesRead != -1 ? bytesRead : 0;
                if (null != downloadingListener)
                    downloadingListener.onDownloading(bytesRead == -1,contentLength(), readTotal);
                return bytesRead;
            }
        };
    }
}
