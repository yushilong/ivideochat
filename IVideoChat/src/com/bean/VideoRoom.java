package com.bean;

import java.io.Serializable;

public class VideoRoom implements Serializable {
	public String code;
	public String name;
	public int streamQuota;
	public int videoDelay;
	public long createTime;
}
