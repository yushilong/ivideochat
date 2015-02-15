package com.shouyanwang;

public class h264encoder {
	
	enum intype{
		X264_TYPE_P,
		X264_TYPE_IDR,
		X264_TYPE_I,
		X264_TYPE_AUTO;
	}
	
	static{
		System.loadLibrary("X264Encoder");
	}
	
	public   native long  initEncoder (int encodeWidth,int encodeHeight);
	public   native int destory(long handle);
	public   native int encodeframe(long handle,int type,byte[] in,int inSize,byte[] out);
}
