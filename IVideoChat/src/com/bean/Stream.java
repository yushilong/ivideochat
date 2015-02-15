package com.bean;

import java.io.Serializable;

public class Stream implements Serializable {
	public int cdnType;
	public long linkTime;
	public long linkExpired;
	public String streamName;
	public String link;
	public long streamLease;
}
