package com.cloud.classroom.audiorecord.lame;

public interface SimpleLameCallBack {
	public void onProgress(int progress);

	public void onError(String message);
}
