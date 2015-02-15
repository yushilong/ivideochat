package com.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.MainApplication;
import com.StringUtil;

/**
 * Created by yushilong on 2015/1/23.
 */
public class CacheManager {
	private static CacheManager _instance;
	private String cacheKey;
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间
	private static Object mLock = new Object();
	public static final String CACKE_PATH = "/arpid/cache";
	private Context context = MainApplication.getInstance();

	public static CacheManager getInstance() {
		synchronized (mLock) {
			if (_instance == null)
				_instance = new CacheManager();
			return _instance;
		}
	}

	/**
	 * 判断缓存是否存在
	 *
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = context.getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 *
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = context.getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 保存对象
	 *
	 * @param object
	 * @param file
	 * @throws java.io.IOException
	 */
	public boolean saveObject(Object object, String file) {
		file = StringUtil.md5(file);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 *
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 */
	public Object readObject(String file) {
		file = StringUtil.md5(file);
		if (!isExistDataCache(file) || isCacheDataFailure(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = context.getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}
}
