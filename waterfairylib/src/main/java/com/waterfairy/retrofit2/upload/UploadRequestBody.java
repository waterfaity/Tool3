package com.waterfairy.retrofit2.upload;

import com.waterfairy.retrofit2.base.BaseProgress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by water_fiay on 2017/6/26.
 * 995637517@qq.com
 */

class UploadRequestBody extends RequestBody {
    private BaseProgress baseProgress;
    private RequestBody sourceBody;
    private BufferedSink bufferedSink;

    public UploadRequestBody(RequestBody sourceBody, BaseProgress baseProgress) {
        this.baseProgress = baseProgress;
        this.sourceBody = sourceBody;

    }

    @Override
    public long contentLength() throws IOException {
        return sourceBody.contentLength();
    }

    /**
     * Returns the Content-Type header for this body.
     */
    @Override
    public MediaType contentType() {
        return sourceBody.contentType();
    }

    /**
     * Writes the content of this request to {@code out}.
     *
     * @param sink
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {


        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        sourceBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                    baseProgress.setTotalLen(contentLength);
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                if (bytesWritten < contentLength) {
                    baseProgress.onDownloading(false, contentLength, bytesWritten);
                } else {
                    baseProgress.onDownloading(true, contentLength, bytesWritten);
                }
            }
        };
    }
}
