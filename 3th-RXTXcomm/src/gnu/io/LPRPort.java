/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.TooManyListenersException;
/*     */ 
/*     */ final class LPRPort extends ParallelPort
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   private int fd;
/* 112 */   private final ParallelOutputStream out = new ParallelOutputStream();
/*     */ 
/* 116 */   private final ParallelInputStream in = new ParallelInputStream();
/*     */ 
/* 121 */   private int lprmode = 0;
/*     */ 
/* 174 */   private int timeout = 0;
/*     */ 
/* 185 */   private int threshold = 1;
/*     */   private ParallelPortEventListener PPEventListener;
/*     */   private MonitorThread monThread;
/*     */ 
/*     */   private static native void Initialize();
/*     */ 
/*     */   public LPRPort(String paramString)
/*     */     throws PortInUseException
/*     */   {
/*  98 */     this.fd = open(paramString);
/*  99 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */   private synchronized native int open(String paramString)
/*     */     throws PortInUseException;
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 113 */     return this.out;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() {
/* 117 */     return this.in;
/*     */   }
/*     */ 
/*     */   public int getMode()
/*     */   {
/* 122 */     return this.lprmode;
/*     */   }
/*     */   public int setMode(int paramInt) throws UnsupportedCommOperationException {
/*     */     try {
/* 126 */       setLPRMode(paramInt);
/*     */     } catch (UnsupportedCommOperationException localUnsupportedCommOperationException) {
/* 128 */       localUnsupportedCommOperationException.printStackTrace();
/* 129 */       return -1;
/*     */     }
/* 131 */     this.lprmode = paramInt;
/* 132 */     return 0;
/*     */   }
/*     */ 
/*     */   public void restart() {
/* 136 */     System.out.println("restart() is not implemented");
/*     */   }
/*     */ 
/*     */   public void suspend() {
/* 140 */     System.out.println("suspend() is not implemented"); } 
/*     */   public native boolean setLPRMode(int paramInt) throws UnsupportedCommOperationException;
/*     */ 
/*     */   public native boolean isPaperOut();
/*     */ 
/*     */   public native boolean isPrinterBusy();
/*     */ 
/*     */   public native boolean isPrinterError();
/*     */ 
/*     */   public native boolean isPrinterSelected();
/*     */ 
/*     */   public native boolean isPrinterTimedOut();
/*     */ 
/*     */   private native void nativeClose();
/*     */ 
/* 155 */   public synchronized void close() { if (this.fd < 0) return;
/* 156 */     nativeClose();
/* 157 */     super.close();
/* 158 */     removeEventListener();
/*     */ 
/* 160 */     this.fd = 0;
/* 161 */     Runtime.getRuntime().gc();
/*     */   }
/*     */ 
/*     */   public void enableReceiveFraming(int paramInt)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 167 */     throw new UnsupportedCommOperationException("Not supported");
/*     */   }
/*     */   public void disableReceiveFraming() {  } 
/* 170 */   public boolean isReceiveFramingEnabled() { return false; } 
/* 171 */   public int getReceiveFramingByte() { return 0; }
/*     */ 
/*     */ 
/*     */   public void enableReceiveTimeout(int paramInt)
/*     */   {
/* 177 */     if (paramInt > 0) this.timeout = paramInt; else
/* 178 */       this.timeout = 0; 
/*     */   }
/* 180 */   public void disableReceiveTimeout() { this.timeout = 0; } 
/* 181 */   public boolean isReceiveTimeoutEnabled() { return this.timeout > 0; } 
/* 182 */   public int getReceiveTimeout() { return this.timeout; }
/*     */ 
/*     */ 
/*     */   public void enableReceiveThreshold(int paramInt)
/*     */   {
/* 188 */     if (paramInt > 1) this.threshold = paramInt; else
/* 189 */       this.threshold = 1; 
/*     */   }
/* 191 */   public void disableReceiveThreshold() { this.threshold = 1; } 
/* 192 */   public int getReceiveThreshold() { return this.threshold; } 
/* 193 */   public boolean isReceiveThresholdEnabled() { return this.threshold > 1; }
/*     */ 
/*     */ 
/*     */   public native void setInputBufferSize(int paramInt);
/*     */ 
/*     */   public native int getInputBufferSize();
/*     */ 
/*     */   public native void setOutputBufferSize(int paramInt);
/*     */ 
/*     */   public native int getOutputBufferSize();
/*     */ 
/*     */   public native int getOutputBufferFree();
/*     */ 
/*     */   protected native void writeByte(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   protected native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   protected native void drain()
/*     */     throws IOException;
/*     */ 
/*     */   protected native int nativeavailable()
/*     */     throws IOException;
/*     */ 
/*     */   protected native int readByte()
/*     */     throws IOException;
/*     */ 
/*     */   protected native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   native void eventLoop();
/*     */ 
/*     */   public boolean checkMonitorThread()
/*     */   {
/* 229 */     if (this.monThread != null)
/* 230 */       return this.monThread.isInterrupted();
/* 231 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean sendEvent(int paramInt, boolean paramBoolean)
/*     */   {
/* 238 */     if ((this.fd == 0) || (this.PPEventListener == null) || (this.monThread == null))
/*     */     {
/* 240 */       return true;
/*     */     }
/*     */ 
/* 243 */     switch (paramInt)
/*     */     {
/*     */     case 2:
/* 246 */       if (!this.monThread.monBuffer)
/* 247 */         return false; break;
/*     */     case 1:
/* 249 */       if (!this.monThread.monError)
/* 250 */         return false; break;
/*     */     default:
/* 252 */       System.err.println("unknown event:" + paramInt);
/* 253 */       return false;
/*     */     }
/* 255 */     ParallelPortEvent localParallelPortEvent = new ParallelPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/*     */ 
/* 257 */     if (this.PPEventListener != null)
/* 258 */       this.PPEventListener.parallelEvent(localParallelPortEvent);
/* 259 */     if ((this.fd == 0) || (this.PPEventListener == null) || (this.monThread == null))
/*     */     {
/* 261 */       return true;
/*     */     }
/*     */     try
/*     */     {
/* 265 */       Thread.sleep(50L); } catch (Exception localException) {
/* 266 */     }return false;
/*     */   }
/*     */ 
/*     */   public synchronized void addEventListener(ParallelPortEventListener paramParallelPortEventListener)
/*     */     throws TooManyListenersException
/*     */   {
/* 275 */     if (this.PPEventListener != null)
/* 276 */       throw new TooManyListenersException();
/* 277 */     this.PPEventListener = paramParallelPortEventListener;
/* 278 */     this.monThread = new MonitorThread();
/* 279 */     this.monThread.start();
/*     */   }
/*     */ 
/*     */   public synchronized void removeEventListener()
/*     */   {
/* 285 */     this.PPEventListener = null;
/* 286 */     if (this.monThread != null)
/*     */     {
/* 288 */       this.monThread.interrupt();
/* 289 */       this.monThread = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void notifyOnError(boolean paramBoolean)
/*     */   {
/* 298 */     System.out.println("notifyOnError is not implemented yet");
/* 299 */     this.monThread.monError = paramBoolean;
/*     */   }
/*     */ 
/*     */   public synchronized void notifyOnBuffer(boolean paramBoolean) {
/* 303 */     System.out.println("notifyOnBuffer is not implemented yet");
/* 304 */     this.monThread.monBuffer = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 311 */     if (this.fd > 0) close();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     System.loadLibrary("rxtxParallel");
/*  75 */     Initialize();
/*     */   }
/*     */ 
/*     */   class MonitorThread extends Thread
/*     */   {
/* 367 */     private boolean monError = false;
/* 368 */     private boolean monBuffer = false;
/*     */ 
/*     */     MonitorThread() {
/*     */     }
/* 372 */     public void run() { LPRPort.this.eventLoop();
/* 373 */       yield();
/*     */     }
/*     */   }
/*     */ 
/*     */   class ParallelInputStream extends InputStream
/*     */   {
/*     */     ParallelInputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 345 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 346 */       return LPRPort.this.readByte();
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 350 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 351 */       return LPRPort.this.readArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 356 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 357 */       return LPRPort.this.readArray(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public int available() throws IOException {
/* 361 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 362 */       return LPRPort.this.nativeavailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   class ParallelOutputStream extends OutputStream
/*     */   {
/*     */     ParallelOutputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public synchronized void write(int paramInt)
/*     */       throws IOException
/*     */     {
/* 319 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 320 */       LPRPort.this.writeByte(paramInt);
/*     */     }
/*     */ 
/*     */     public synchronized void write(byte[] paramArrayOfByte) throws IOException {
/* 324 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 325 */       LPRPort.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 330 */       if (LPRPort.this.fd == 0) throw new IOException();
/* 331 */       LPRPort.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public synchronized void flush() throws IOException {
/* 335 */       if (LPRPort.this.fd == 0) throw new IOException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.LPRPort
 * JD-Core Version:    0.6.2
 */