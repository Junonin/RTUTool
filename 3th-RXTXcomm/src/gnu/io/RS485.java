/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.TooManyListenersException;
/*     */ 
/*     */ final class RS485 extends RS485Port
/*     */ {
/*     */   private int fd;
/*  97 */   static boolean dsrFlag = false;
/*     */ 
/* 100 */   private final RS485OutputStream out = new RS485OutputStream();
/*     */ 
/* 105 */   private final RS485InputStream in = new RS485InputStream();
/*     */ 
/* 127 */   private int speed = 9600;
/*     */ 
/* 131 */   private int dataBits = 8;
/*     */ 
/* 135 */   private int stopBits = 1;
/*     */ 
/* 139 */   private int parity = 0;
/*     */ 
/* 144 */   private int flowmode = 0;
/*     */ 
/* 174 */   private int timeout = 0;
/*     */ 
/* 200 */   private int threshold = 0;
/*     */ 
/* 230 */   private int InputBuffer = 0;
/* 231 */   private int OutputBuffer = 0;
/*     */   private RS485PortEventListener SPEventListener;
/*     */   private MonitorThread monThread;
/* 284 */   private int dataAvailable = 0;
/*     */ 
/*     */   private static native void Initialize();
/*     */ 
/*     */   public RS485(String paramString)
/*     */     throws PortInUseException
/*     */   {
/*  88 */     this.fd = open(paramString);
/*     */   }
/*     */ 
/*     */   private native int open(String paramString)
/*     */     throws PortInUseException;
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 101 */     return this.out;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 106 */     return this.in;
/*     */   }
/*     */ 
/*     */   public void setRS485PortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 115 */     nativeSetRS485PortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/* 116 */     this.speed = paramInt1;
/* 117 */     this.dataBits = paramInt2;
/* 118 */     this.stopBits = paramInt3;
/* 119 */     this.parity = paramInt4;
/*     */   }
/*     */ 
/*     */   private native void nativeSetRS485PortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public int getBaudRate()
/*     */   {
/* 128 */     return this.speed;
/*     */   }
/*     */ 
/*     */   public int getDataBits() {
/* 132 */     return this.dataBits;
/*     */   }
/*     */ 
/*     */   public int getStopBits() {
/* 136 */     return this.stopBits;
/*     */   }
/*     */ 
/*     */   public int getParity() {
/* 140 */     return this.parity;
/*     */   }
/*     */ 
/*     */   public void setFlowControlMode(int paramInt)
/*     */   {
/*     */     try {
/* 146 */       setflowcontrol(paramInt);
/*     */     } catch (IOException localIOException) {
/* 148 */       localIOException.printStackTrace();
/* 149 */       return;
/*     */     }
/* 151 */     this.flowmode = paramInt;
/*     */   }
/* 153 */   public int getFlowControlMode() { return this.flowmode; }
/*     */ 
/*     */ 
/*     */   native void setflowcontrol(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public void enableReceiveFraming(int paramInt)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 166 */     throw new UnsupportedCommOperationException("Not supported");
/*     */   }
/*     */   public void disableReceiveFraming() {  } 
/* 169 */   public boolean isReceiveFramingEnabled() { return false; } 
/* 170 */   public int getReceiveFramingByte() { return 0; }
/*     */ 
/*     */   public native int NativegetReceiveTimeout();
/*     */ 
/*     */   public native boolean NativeisReceiveTimeoutEnabled();
/*     */ 
/*     */   public native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/*     */ 
/*     */   public void disableReceiveTimeout()
/*     */   {
/* 180 */     enableReceiveTimeout(0);
/*     */   }
/*     */   public void enableReceiveTimeout(int paramInt) {
/* 183 */     if (paramInt >= 0) {
/* 184 */       this.timeout = paramInt;
/* 185 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else {
/* 188 */       System.out.println("Invalid timeout");
/*     */     }
/*     */   }
/*     */ 
/* 192 */   public boolean isReceiveTimeoutEnabled() { return NativeisReceiveTimeoutEnabled(); }
/*     */ 
/*     */   public int getReceiveTimeout() {
/* 195 */     return NativegetReceiveTimeout();
/*     */   }
/*     */ 
/*     */   public void enableReceiveThreshold(int paramInt)
/*     */   {
/* 203 */     if (paramInt >= 0)
/*     */     {
/* 205 */       this.threshold = paramInt;
/* 206 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else
/*     */     {
/* 210 */       System.out.println("Invalid Threshold");
/*     */     }
/*     */   }
/*     */ 
/* 214 */   public void disableReceiveThreshold() { enableReceiveThreshold(0); }
/*     */ 
/*     */   public int getReceiveThreshold() {
/* 217 */     return this.threshold;
/*     */   }
/*     */   public boolean isReceiveThresholdEnabled() {
/* 220 */     return this.threshold > 0;
/*     */   }
/*     */ 
/*     */   public void setInputBufferSize(int paramInt)
/*     */   {
/* 234 */     this.InputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getInputBufferSize() {
/* 238 */     return this.InputBuffer;
/*     */   }
/*     */ 
/*     */   public void setOutputBufferSize(int paramInt) {
/* 242 */     this.OutputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getOutputBufferSize() {
/* 246 */     return this.OutputBuffer;
/*     */   }
/*     */ 
/*     */   public native boolean isDTR();
/*     */ 
/*     */   public native void setDTR(boolean paramBoolean);
/*     */ 
/*     */   public native void setRTS(boolean paramBoolean);
/*     */ 
/*     */   private native void setDSR(boolean paramBoolean);
/*     */ 
/*     */   public native boolean isCTS();
/*     */ 
/*     */   public native boolean isDSR();
/*     */ 
/*     */   public native boolean isCD();
/*     */ 
/*     */   public native boolean isRI();
/*     */ 
/*     */   public native boolean isRTS();
/*     */ 
/*     */   public native void sendBreak(int paramInt);
/*     */ 
/*     */   private native void writeByte(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   private native void drain() throws IOException;
/*     */ 
/*     */   private native int nativeavailable() throws IOException;
/*     */ 
/*     */   private native int readByte() throws IOException;
/*     */ 
/*     */   private native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   native void eventLoop();
/*     */ 
/*     */   public void sendEvent(int paramInt, boolean paramBoolean)
/*     */   {
/* 286 */     switch (paramInt) {
/*     */     case 1:
/* 288 */       this.dataAvailable = 1;
/* 289 */       if (!this.monThread.Data) return;
/*     */       break;
/*     */     case 2:
/* 292 */       if (!this.monThread.Output)
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 3:
/* 312 */       if (!this.monThread.CTS) return;
/*     */       break;
/*     */     case 4:
/* 315 */       if (!this.monThread.DSR) return;
/*     */       break;
/*     */     case 5:
/* 318 */       if (!this.monThread.RI) return;
/*     */       break;
/*     */     case 6:
/* 321 */       if (!this.monThread.CD) return;
/*     */       break;
/*     */     case 7:
/* 324 */       if (!this.monThread.OE) return;
/*     */       break;
/*     */     case 8:
/* 327 */       if (!this.monThread.PE) return;
/*     */       break;
/*     */     case 9:
/* 330 */       if (!this.monThread.FE) return;
/*     */       break;
/*     */     case 10:
/* 333 */       if (!this.monThread.BI) return;
/*     */       break;
/*     */     default:
/* 336 */       System.err.println("unknown event:" + paramInt);
/* 337 */       return;
/*     */     }
/* 339 */     RS485PortEvent localRS485PortEvent = new RS485PortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 340 */     if (this.SPEventListener != null) this.SPEventListener.RS485Event(localRS485PortEvent);
/*     */   }
/*     */ 
/*     */   public void addEventListener(RS485PortEventListener paramRS485PortEventListener)
/*     */     throws TooManyListenersException
/*     */   {
/* 347 */     if (this.SPEventListener != null) throw new TooManyListenersException();
/* 348 */     this.SPEventListener = paramRS485PortEventListener;
/* 349 */     this.monThread = new MonitorThread();
/* 350 */     this.monThread.start();
/*     */   }
/*     */ 
/*     */   public void removeEventListener() {
/* 354 */     this.SPEventListener = null;
/* 355 */     if (this.monThread != null) {
/* 356 */       this.monThread.interrupt();
/* 357 */       this.monThread = null;
/*     */     }
/*     */   }
/*     */ 
/* 361 */   public void notifyOnDataAvailable(boolean paramBoolean) { this.monThread.Data = paramBoolean; } 
/*     */   public void notifyOnOutputEmpty(boolean paramBoolean) {
/* 363 */     this.monThread.Output = paramBoolean;
/*     */   }
/* 365 */   public void notifyOnCTS(boolean paramBoolean) { this.monThread.CTS = paramBoolean; } 
/* 366 */   public void notifyOnDSR(boolean paramBoolean) { this.monThread.DSR = paramBoolean; } 
/* 367 */   public void notifyOnRingIndicator(boolean paramBoolean) { this.monThread.RI = paramBoolean; } 
/* 368 */   public void notifyOnCarrierDetect(boolean paramBoolean) { this.monThread.CD = paramBoolean; } 
/* 369 */   public void notifyOnOverrunError(boolean paramBoolean) { this.monThread.OE = paramBoolean; } 
/* 370 */   public void notifyOnParityError(boolean paramBoolean) { this.monThread.PE = paramBoolean; } 
/* 371 */   public void notifyOnFramingError(boolean paramBoolean) { this.monThread.FE = paramBoolean; } 
/* 372 */   public void notifyOnBreakInterrupt(boolean paramBoolean) { this.monThread.BI = paramBoolean; }
/*     */ 
/*     */   private native void nativeClose();
/*     */ 
/*     */   public void close()
/*     */   {
/* 378 */     setDTR(false);
/* 379 */     setDSR(false);
/* 380 */     nativeClose();
/* 381 */     super.close();
/* 382 */     this.fd = 0;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 388 */     if (this.fd > 0) close();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     System.loadLibrary("rxtxRS485");
/*  75 */     Initialize();
/*     */   }
/*     */ 
/*     */   class MonitorThread extends Thread
/*     */   {
/* 458 */     private boolean CTS = false;
/* 459 */     private boolean DSR = false;
/* 460 */     private boolean RI = false;
/* 461 */     private boolean CD = false;
/* 462 */     private boolean OE = false;
/* 463 */     private boolean PE = false;
/* 464 */     private boolean FE = false;
/* 465 */     private boolean BI = false;
/* 466 */     private boolean Data = false;
/* 467 */     private boolean Output = false;
/*     */ 
/*     */     MonitorThread() {  } 
/* 470 */     public void run() { RS485.this.eventLoop(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   class RS485InputStream extends InputStream
/*     */   {
/*     */     RS485InputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 411 */       RS485.this.dataAvailable = 0;
/* 412 */       return RS485.this.readByte();
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 416 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 420 */       RS485.this.dataAvailable = 0;
/* 421 */       int i = 0; int j = 0;
/* 422 */       int[] arrayOfInt = { paramArrayOfByte.length, RS485.this.InputBuffer, paramInt2 };
/*     */ 
/* 434 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) i++;
/* 435 */       j = arrayOfInt[i];
/* 436 */       while (i < arrayOfInt.length)
/*     */       {
/* 438 */         if (arrayOfInt[i] > 0)
/*     */         {
/* 440 */           j = Math.min(j, arrayOfInt[i]);
/*     */         }
/* 442 */         i++;
/*     */       }
/* 444 */       j = Math.min(j, RS485.this.threshold);
/* 445 */       if (j == 0) j = 1;
/* 446 */       int k = available();
/* 447 */       int m = RS485.this.readArray(paramArrayOfByte, paramInt1, j);
/* 448 */       return m;
/*     */     }
/*     */     public int available() throws IOException {
/* 451 */       return RS485.this.nativeavailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   class RS485OutputStream extends OutputStream
/*     */   {
/*     */     RS485OutputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void write(int paramInt)
/*     */       throws IOException
/*     */     {
/* 395 */       RS485.this.writeByte(paramInt);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte) throws IOException {
/* 398 */       RS485.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 401 */       RS485.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */     public void flush() throws IOException {
/* 404 */       RS485.this.drain();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RS485
 * JD-Core Version:    0.6.2
 */