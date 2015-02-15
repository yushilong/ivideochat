package net.john.util;
import com.smaxe.logger.ILogger;
import com.smaxe.uv.ProtocolLayerInfo;
import com.smaxe.uv.Responder;
import com.smaxe.uv.UrlInfo;
import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.a.d;
import com.smaxe.uv.client.a.h;
import com.smaxe.uv.client.a.i;
import com.smaxe.uv.client.a.k;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public final class UltraNetConnection extends i
  implements INetConnection
{
  private final h a;
  private d b = null;
  private a c = null;
  private ExecutorService d = null;
  private ScheduledExecutorService e = null;
  private boolean f = false;
  private boolean g = false;
  private static final int h = 19;
  private static final int i = 9;
  private static final int[] j = { 5, 2, 7, 1, 0, 3, 6, 4 };
  private static byte[] k = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };

  public static void setSwfFileSizeAndHash(Map<String, Object> paramMap, File paramFile)
    throws Exception
  {
    a(paramMap, paramFile);
  }

  public UltraNetConnection()
  {
    this(null);
  }

  public UltraNetConnection(Map<String, Object> paramMap)
  {
    this(paramMap, null, null);
  }

  public UltraNetConnection(Map<String, Object> paramMap, ExecutorService paramExecutorService, ScheduledExecutorService paramScheduledExecutorService)
  {
    super(paramMap);
    this.d = (paramExecutorService == null ? Executors.newCachedThreadPool() : paramExecutorService);
    this.e = (paramScheduledExecutorService == null ? Executors.newSingleThreadScheduledExecutor() : paramScheduledExecutorService);
    this.f = (paramExecutorService == null);
    this.g = (paramScheduledExecutorService == null);
    this.a = new k(this.e);
  }

  public void addHeader(String paramString, boolean paramBoolean, Object paramObject)
  {
    this.b.a(paramString, paramBoolean, paramObject);
  }

  public void call(String paramString, Responder paramResponder, Object[] paramArrayOfObject)
  {
    if (!connected())
      return;
    this.b.a(paramString, paramResponder, paramArrayOfObject);
  }

  public void close()
  {
    b(com.smaxe.uv.a.e.b("NetConnection.Connect.Closed", "Connection is closed."));
  }

  public void connect(String paramString, Object... paramArrayOfObject)
  {
//    b(k);
    UrlInfo localUrlInfo = UrlInfo.parseUrl(paramString);
    com.smaxe.uv.client.a.e locale = new com.smaxe.uv.client.a.e();
    locale.a(this.d);
    locale.a((ILogger)configuration().get("logger"));
    this.b = locale;
    this.c = new a();
    try {
    	Method bitchMethod = com.smaxe.uv.client.a.h.class.getMethod("a", String.class, String.class, int.class, Map.class);
        Object btichResult = bitchMethod.invoke(this.a, localUrlInfo.protocol, localUrlInfo.host, localUrlInfo.port, configuration());
        Method[] aryMethod = com.smaxe.uv.client.a.d.class.getMethods();
        for(Method method : aryMethod) {
        	if(method.getName().equals("a") && method.getParameterTypes().length == 6) {
        		method.invoke(this.b, this, btichResult, paramString, localUrlInfo.getApp(), this.c, paramArrayOfObject);
        		break;
        	}
        }
    } catch(Exception ex) {
    	ex.printStackTrace();
    }
    
    super.connect(paramString, paramArrayOfObject);
  }

  public boolean connected()
  {
    if (this.b == null)
      return false;
    return this.b.a() == 3;
  }

  public String connectedProxyType()
  {
    return connected() ? this.b.b() : null;
  }

  public boolean usingTLS()
  {
    return connected() ? this.b.c() : false;
  }

  public ProtocolLayerInfo getInfo()
  {
    return this.b.d();
  }

  public int getUploadBufferSize()
  {
    return this.b.e();
  }

  public void setMaxUploadBandwidth(int paramInt)
  {
    if (paramInt < 0)
      throw new IllegalArgumentException("Parameter 'bandwidth' is negative: " + paramInt);
    this.b.a(paramInt);
  }

  public void onBWDone()
  {
	  
  }

  public void onBWDone(Object[] paramArrayOfObject)
  {
	  
  }

  private void b(Map<String, Object> paramMap)
  {
    if (this.b == null)
      return;
    this.b.a(paramMap);
    if ((this.f) && (this.d != null))
      this.d.shutdown();
    if ((this.g) && (this.e != null))
      this.e.shutdown();
    this.d = null;
    this.e = null;
  }

  static void a(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length != 25))
      return;
    k = paramArrayOfByte;
  }

  d a()
  {
    return this.b;
  }

  private static void b(byte abyte0[])
  throws IllegalArgumentException
{
  int l = 0;
  for(int i1 = 1; i1 < abyte0.length - 1; i1++)
      l += abyte0[i1] & 0xff;

  l &= 0xff;
  int j1 = abyte0[1] & 0xf;
  if((abyte0[0] & 0xff) != (byte)(l >> 0 & 0xf) || (abyte0[abyte0.length - 1] & 0xff) != (byte)(l >> 4 & 0xf) || abyte0[1] + abyte0[abyte0.length - 2] != 15)
      a(16);
  boolean aflag[] = new boolean[21];
  byte abyte1[] = new byte[8];
  int k1 = 1;
  int l1 = j1;
  for(int i2 = 0; i2 < abyte1.length; i2++)
  {
      for(; aflag[l1 % aflag.length]; l1++);
      aflag[l1 % aflag.length] = true;
      abyte1[i2] = abyte0[2 + l1 % aflag.length];
      k1 += 2;
      l1 += k1;
  }

  if((abyte1[1] & 0xf) != 3)
      a(32);
  boolean flag = (abyte1[3] & 0xf) >= 8;
  int j2 = (flag ? abyte1[3] - 8 : abyte1[3]) & 0xf;
  if(j2 < 1)
      a(1);
  if(flag)
  {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, 2000 + (abyte1[4] & 0xf));
      calendar.set(Calendar.MONTH, (abyte1[5] & 0xf) - 1);
      calendar.set(Calendar.DAY_OF_MONTH, ((abyte1[6] & 0xf) << 4) + (abyte1[7] & 0xf));
      if(System.currentTimeMillis() - calendar.getTimeInMillis() > 0L)
          a(18);
  }
}

  private static void a(int paramInt)
  {
    switch (paramInt & 0xF)
    {
    case 0:
      throw new IllegalArgumentException(a(new long[] { 8460391658548064800L, 8315163859177334048L, 8319872964449869929L, 7205878151055483136L }));
    case 1:
      throw new IllegalArgumentException(a(new long[] { 8460391658548064800L, 8315163859177334048L, 8319309735340351598L, 7811060823377406308L, 7162256601089340786L, 8532478991051810162L, 120946281218048L }));
    case 2:
      throw new IllegalArgumentException(a(new long[] { 8462924959242482208L, 2314957309810076517L, 2335505025909089656L, 2378011653932580864L }));
    }
  }

  private static String a(long[] paramArrayOfLong)
  {
    byte[] arrayOfByte = new byte[paramArrayOfLong.length * 8];
    int m = 0;
    for (int n = 0; n < paramArrayOfLong.length; n++)
      for (int i1 = 0; i1 < 8; i1++)
      {
        byte i2 = (byte)(int)(paramArrayOfLong[n] >> j[i1] * 8 & 0xFF);
        if (i2 == 0)
          break;
        arrayOfByte[(n * 8 + i1)] = i2;
        m++;
      }
    return new String(arrayOfByte, 0, m);
  }

  static void a(UltraNetConnection netconnection, String s, Exception exception)
  {
      netconnection.a(s, exception);
  }

  static void a(UltraNetConnection netconnection, String s)
  {
      netconnection.a(s);
  }

  static void a(UltraNetConnection netconnection, Map map)
  {
      netconnection.b(map);
  }

  static void b(UltraNetConnection netconnection, Map map)
  {
      netconnection.a(map);
  }

  static void c(UltraNetConnection netconnection, Map map)
  {
      netconnection.a(map);
  }
  
  private class a extends d.a
  {
    public a()
    {
    }

    public void a(String paramString, Exception paramException)
    {
      UltraNetConnection.a(UltraNetConnection.this, paramString, paramException);
    }

    public void a(String paramString)
    {
      UltraNetConnection.a(UltraNetConnection.this, paramString);
    }

    public void a(Map<String, Object> paramMap)
    {
      String str = (String)paramMap.get("code");
      if ((!"NetConnection.Connect.Success".equals(str)) && (!"NetConnection.Connect.Bandwidth".equals(str)) && (!"NetConnection.Call.Failed".equals(str)))
        UltraNetConnection.a(UltraNetConnection.this, paramMap);
      UltraNetConnection.b(UltraNetConnection.this, paramMap);
    }

    public void a(long paramLong1, long paramLong2)
    {
      if (!((Boolean)UltraNetConnection.this.configuration().get("enableAcknowledgementEventNotification")).booleanValue())
        return;
      Map localMap = com.smaxe.uv.a.e.b("NetConnection.Connect.Bandwidth", "'Acknowledgement' event notification.");
      localMap.put("acknowledgement", Long.valueOf(paramLong1));
      localMap.put("info", new ProtocolLayerInfo(UltraNetConnection.this.getInfo()));
      localMap.put("uploadBufferSize", Long.valueOf(paramLong2));
      UltraNetConnection.c(UltraNetConnection.this, localMap);
    }
  }
}