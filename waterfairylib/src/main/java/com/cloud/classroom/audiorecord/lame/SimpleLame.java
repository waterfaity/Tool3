/* 
 * Copyright (c) 2011-2012 Yuichi Hirano
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloud.classroom.audiorecord.lame;

/**
 * LAME interface class
 */
public class SimpleLame {
	static {
		System.loadLibrary("mp3lame");
	}

	/**
	 * Initialize LAME.
	 * 
	 * @param inSamplerate
	 *            input sample rate in Hz.
	 * @param outChannel
	 *            number of channels in input stream.
	 * @param outSamplerate
	 *            output sample rate in Hz.
	 * @param outBitrate
	 *            brate compression ratio in KHz.
	 */
	public static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate) {
		init(16000, 1, 16000, 128, 2);
	}

	/**
	 * Initialize LAME.
	 * 
	 * @param inSamplerate
	 *            input sample rate in Hz.
	 * @param outChannel
	 *            number of channels in input stream.
	 * @param outSamplerate
	 *            output sample rate in Hz.
	 * @param outBitrate
	 *            brate compression ratio in KHz.
	 * @param quality
	 *            quality=0..9. 0=best (very slow). 9=worst.<br />
	 *            recommended:<br />
	 *            2 near-best quality, not too slow<br />
	 *            5 good quality, fast<br />
	 *            7 ok quality, really fast
	 */
	public native static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate, int quality);

	/**
	 * Encode buffer to mp3.
	 * 
	 * @param buffer_l
	 *            PCM data for left channel.
	 * @param buffer_r
	 *            PCM data for right channel.
	 * @param sambles
	 *            number of samples per channel.
	 * @param mp3buf
	 *            result encoded MP3 stream. You must specified
	 *            "7200 + (1.25 * buffer_l.length)" length array.
	 * @return number of bytes output in mp3buf. Can be 0.<br />
	 *         -1: mp3buf was too small<br />
	 *         -2: malloc() problem<br />
	 *         -3: lame_init_params() not called<br />
	 *         -4: psycho acoustic problems
	 */
	public native static int encode(short[] buffer_l, short[] buffer_r,
			int samples, byte[] mp3buf);

	/**
	 * Flush LAME buffer.
	 * 
	 * @param mp3buf
	 *            result encoded MP3 stream. You must specified at least 7200
	 *            bytes.
	 * @return number of bytes output to mp3buf. Can be 0.
	 */
	public native static int flush(byte[] mp3buf);

	/**
	 * Close LAME.
	 */
	public native static void close();

	/** --------------------------------------------------------- */
	public static void initEncoderNdk(int numChannels, int sampleRate,
			int bitRate, int mode, int quality) {
		initEncoder(numChannels, sampleRate, bitRate, mode, quality);
	}

	public static void destroyEncoderNdk() {
		destroyEncoder();
	}

	public static int encodeFileNdk(String sourcePath, String targetPath) {
		return encodeFile(sourcePath, targetPath);
	}

	private native static void initEncoder(int numChannels, int sampleRate,
			int bitRate, int mode, int quality);

	private native static void destroyEncoder();

	private native static int encodeFile(String sourcePath, String targetPath);

	/**
	 * wav文件转mp3
	 * 
	 * @param wav
	 *            wav音频文件路径
	 * @param mp3
	 *            mp3文件路径
	 */
	public static native int convertmp3(String wav, String mp3,
			SimpleLameCallBack callback);

	/**
	 * wav文件合并
	 * 
	 * @param sourcefile1
	 *            源文件1路径
	 * @param sourcefile2
	 *            源文件2路径
	 * @param targetfile
	 *            目标文件路径
	 * @param callback
	 * @return
	 */
	public native static int wavMerge(String sourcefile1, String sourcefile2,
			String targetfile, SimpleLameCallBack callback);

}
