/*      */ package gnu.io;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.util.TooManyListenersException;
/*      */ 
/*      */ public final class RXTXPort extends SerialPort
/*      */ {
/*      */   protected static final boolean debug = false;
/*      */   protected static final boolean debug_read = false;
/*      */   protected static final boolean debug_read_results = false;
/*      */   protected static final boolean debug_write = false;
/*      */   protected static final boolean debug_events = false;
/*      */   protected static final boolean debug_verbose = false;
/*      */   private static Zystem z;
/*   99 */   boolean MonitorThreadAlive = false;
/*      */ 
/*  142 */   int IOLocked = 0;
/*  143 */   Object IOLockedMutex = new Object();
/*      */ 
/*  146 */   private int fd = 0;
/*      */ 
/*  154 */   long eis = 0L;
/*      */ 
/*  156 */   int pid = 0;
/*      */ 
/*  159 */   static boolean dsrFlag = false;
/*      */ 
/*  162 */   private final SerialOutputStream out = new SerialOutputStream();
/*      */ 
/*  175 */   private final SerialInputStream in = new SerialInputStream();
/*      */ 
/*  233 */   private int speed = 9600;
/*      */ 
/*  246 */   private int dataBits = 8;
/*      */ 
/*  258 */   private int stopBits = 1;
/*      */ 
/*  270 */   private int parity = 0;
/*      */ 
/*  283 */   private int flowmode = 0;
/*      */   private int timeout;
/*  438 */   private int threshold = 0;
/*      */ 
/*  497 */   private int inputBufferSize = 0;
/*  498 */   private int outputBufferSize = 0;
/*      */   private SerialPortEventListener SPEventListener;
/*      */   private MonitorThread monThread;
/*  625 */   boolean monThreadisInterrupted = true;
/*      */ 
/*  794 */   boolean MonitorThreadLock = true;
/*      */ 
/* 1049 */   boolean closeLock = false;
/*      */ 
/*      */   private static native void Initialize();
/*      */ 
/*      */   public RXTXPort(String paramString)
/*      */     throws PortInUseException
/*      */   {
/*  123 */     this.fd = open(paramString);
/*  124 */     this.name = paramString;
/*      */ 
/*  126 */     this.MonitorThreadLock = true;
/*  127 */     this.monThread = new MonitorThread();
/*  128 */     this.monThread.start();
/*  129 */     waitForTheNativeCodeSilly();
/*  130 */     this.MonitorThreadAlive = true;
/*      */ 
/*  132 */     this.timeout = -1;
/*      */   }
/*      */ 
/*      */   private synchronized native int open(String paramString)
/*      */     throws PortInUseException;
/*      */ 
/*      */   @Override
public OutputStream getOutputStream()
/*      */   {
/*  171 */     return this.out;
/*      */   }
/*      */ 
/*      */   @Override
public InputStream getInputStream()
/*      */   {
/*  185 */     return this.in;
/*      */   }
/*      */ 
/*      */   private native int nativeGetParity(int paramInt);
/*      */ 
/*      */   private native int nativeGetFlowControlMode(int paramInt);
/*      */ 
/*      */   @Override
public synchronized void setSerialPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/*  210 */     if (nativeSetSerialPortParams(paramInt1, paramInt2, paramInt3, paramInt4)) {
/*  211 */       throw new UnsupportedCommOperationException("Invalid Parameter");
/*      */     }
/*  213 */     this.speed = paramInt1;
/*  214 */     if (paramInt3 == 3) this.dataBits = 5; else
/*  215 */       this.dataBits = paramInt2;
/*  216 */     this.stopBits = paramInt3;
/*  217 */     this.parity = paramInt4;
/*  218 */     z.reportln("RXTXPort:setSerialPortParams(" + paramInt1 + " " + paramInt2 + " " + paramInt3 + " " + paramInt4 + ") returning");
/*      */   }
/*      */ 
/*      */   private native boolean nativeSetSerialPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   @Override
public int getBaudRate()
/*      */   {
/*  242 */     return this.speed;
/*      */   }
/*      */ 
/*      */   @Override
public int getDataBits()
/*      */   {
/*  254 */     return this.dataBits;
/*      */   }
/*      */ 
/*      */   @Override
public int getStopBits()
/*      */   {
/*  266 */     return this.stopBits;
/*      */   }
/*      */ 
/*      */   @Override
public int getParity()
/*      */   {
/*  278 */     return this.parity;
/*      */   }
/*      */ 
/*      */   @Override
public void setFlowControlMode(int paramInt)
/*      */   {
/*  292 */     if (this.monThreadisInterrupted)
/*      */     {
/*  296 */       return;
/*      */     }
/*      */     try {
/*  299 */       setflowcontrol(paramInt);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  303 */       localIOException.printStackTrace();
/*  304 */       return;
/*      */     }
/*  306 */     this.flowmode = paramInt;
/*      */   }
/*      */ 
/*      */   @Override
public int getFlowControlMode()
/*      */   {
/*  317 */     return this.flowmode;
/*      */   }
/*      */ 
/*      */   native void setflowcontrol(int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   @Override
public void enableReceiveFraming(int paramInt)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/*  336 */     throw new UnsupportedCommOperationException("Not supported");
/*      */   }
/*      */ 
/*      */   @Override
public void disableReceiveFraming()
/*      */   {
/*      */   }
/*      */ 
/*      */   @Override
public boolean isReceiveFramingEnabled()
/*      */   {
/*  352 */     return false;
/*      */   }
/*      */ 
/*      */   @Override
public int getReceiveFramingByte()
/*      */   {
/*  361 */     return 0;
/*      */   }
/*      */ 
/*      */   public native int NativegetReceiveTimeout();
/*      */ 
/*      */   private native boolean NativeisReceiveTimeoutEnabled();
/*      */ 
/*      */   private native void NativeEnableReceiveTimeoutThreshold(int timeout, int threshold, int InputBuffer);
/*      */ 
/*      */   @Override
public void disableReceiveTimeout()
/*      */   {
/*  389 */     this.timeout = -1;
/*  390 */     NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.inputBufferSize);
/*      */   }
/*      */ 
/*      */   @Override
public void enableReceiveTimeout(int timeout)
/*      */   {
/*  401 */     if (timeout >= 0)
/*      */     {
/*  403 */       this.timeout = timeout;
/*  404 */       NativeEnableReceiveTimeoutThreshold(timeout, this.threshold, this.inputBufferSize);
/*      */     }
/*      */     else
/*      */     {
/*  409 */       throw new IllegalArgumentException("Unexpected negative timeout value");
/*      */     }
/*      */   }
/*      */ 
/*      */   @Override
public boolean isReceiveTimeoutEnabled()
/*      */   {
/*  424 */     return NativeisReceiveTimeoutEnabled();
/*      */   }
/*      */ 
/*      */   @Override
public int getReceiveTimeout()
/*      */   {
/*  433 */     return NativegetReceiveTimeout();
/*      */   }
/*      */ 
/*      */   @Override
public void enableReceiveThreshold(int threshold)
/*      */   {
/*  447 */     if (threshold >= 0)
/*      */     {
/*  449 */       this.threshold = threshold;
/*  450 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.inputBufferSize);
/*      */     }
/*      */     else
/*      */     {
/*  455 */       throw new IllegalArgumentException("Unexpected negative threshold value");
/*      */     }
/*      */   }
/*      */ 
/*      */   @Override
public void disableReceiveThreshold()
/*      */   {
/*  469 */     enableReceiveThreshold(0);
/*      */   }
/*      */ 
/*      */   @Override
public int getReceiveThreshold()
/*      */   {
/*  478 */     return this.threshold;
/*      */   }
/*      */ 
/*      */   @Override
public boolean isReceiveThresholdEnabled()
/*      */   {
/*  487 */     return this.threshold > 0;
/*      */   }
/*      */ 
/*      */   @Override
public void setInputBufferSize(int InputBufferSize)
/*      */   {
/*  507 */     if (InputBufferSize < 0) {
/*  508 */       throw new IllegalArgumentException("Unexpected negative buffer size value");
/*      */     }
/*      */ 
/*  512 */     this.inputBufferSize = InputBufferSize;
/*      */   }
/*      */ 
/*      */   @Override
public int getInputBufferSize()
/*      */   {
/*  523 */     return this.inputBufferSize;
/*      */   }
/*      */ 
/*      */   @Override
public void setOutputBufferSize(int outputBufferSize)
/*      */   {
/*  533 */     if (outputBufferSize < 0) {
/*  534 */       throw new IllegalArgumentException("Unexpected negative buffer size value");
/*      */     }
/*      */ 
/*  538 */     this.outputBufferSize = outputBufferSize;
/*      */   }
/*      */ 
/*      */   @Override
public int getOutputBufferSize()
/*      */   {
/*  551 */     return this.outputBufferSize;
/*      */   }
/*      */ 
/*      */   @Override
public native boolean isDTR();
/*      */ 
/*      */   @Override
public native void setDTR(boolean paramBoolean);
/*      */ 
/*      */   @Override
public native void setRTS(boolean paramBoolean);
/*      */ 
/*      */   private native void setDSR(boolean paramBoolean);
/*      */ 
/*      */   @Override
public native boolean isCTS();
/*      */ 
/*      */   @Override
public native boolean isDSR();
/*      */ 
/*      */   @Override
public native boolean isCD();
/*      */ 
/*      */   @Override
public native boolean isRI();
/*      */ 
/*      */   @Override
public native boolean isRTS();
/*      */ 
/*      */   @Override
public native void sendBreak(int paramInt);
/*      */ 
/*      */   protected native void writeByte(int paramInt, boolean paramBoolean)
/*      */     throws IOException;
/*      */ 
/*      */   protected native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */     throws IOException;
/*      */ 
/*      */   protected native boolean nativeDrain(boolean paramBoolean)
/*      */     throws IOException;
/*      */ 
/*      */   protected native int nativeavailable()
/*      */     throws IOException;
/*      */ 
/*      */   protected native int readByte()
/*      */     throws IOException;
/*      */ 
/*      */   protected native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */ 
/*      */   protected native int readTerminatedArray(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
/*      */     throws IOException;
/*      */ 
/*      */   native void eventLoop();
/*      */ 
/*      */   private native void interruptEventLoop();
/*      */ 
/*      */   public boolean checkMonitorThread()
/*      */   {
/*  631 */     if (this.monThread != null)
/*      */     {
/*  637 */       return this.monThreadisInterrupted;
/*      */     }
/*      */ 
/*  641 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean sendEvent(int paramInt, boolean paramBoolean)
/*      */   {
/*  655 */     if ((this.fd == 0) || (this.SPEventListener == null) || (this.monThread == null))
/*      */     {
/*  657 */       return true;
/*      */     }
/*      */ 
/*  660 */     switch (paramInt)
/*      */     {
/*      */     case 1:
/*  666 */       break;
/*      */     case 2:
/*  672 */       break;
/*      */     case 3:
/*  677 */       break;
/*      */     case 4:
/*  682 */       break;
/*      */     case 5:
/*  687 */       break;
/*      */     case 6:
/*  692 */       break;
/*      */     case 7:
/*  697 */       break;
/*      */     case 8:
/*  702 */       break;
/*      */     case 9:
/*  707 */       break;
/*      */     case 10:
/*  712 */       break;
/*      */     }
/*      */ 
/*  722 */     switch (paramInt)
/*      */     {
/*      */     case 1:
/*  725 */       if (!this.monThread.Data)
/*  726 */         return false; break;
/*      */     case 2:
/*  728 */       if (!this.monThread.Output)
/*  729 */         return false; break;
/*      */     case 3:
/*  731 */       if (!this.monThread.CTS)
/*  732 */         return false; break;
/*      */     case 4:
/*  734 */       if (!this.monThread.DSR)
/*  735 */         return false; break;
/*      */     case 5:
/*  737 */       if (!this.monThread.RI)
/*  738 */         return false; break;
/*      */     case 6:
/*  740 */       if (!this.monThread.CD)
/*  741 */         return false; break;
/*      */     case 7:
/*  743 */       if (!this.monThread.OE)
/*  744 */         return false; break;
/*      */     case 8:
/*  746 */       if (!this.monThread.PE)
/*  747 */         return false; break;
/*      */     case 9:
/*  749 */       if (!this.monThread.FE)
/*  750 */         return false; break;
/*      */     case 10:
/*  752 */       if (!this.monThread.BI)
/*  753 */         return false; break;
/*      */     default:
/*  755 */       System.err.println("unknown event: " + paramInt);
/*  756 */       return false;
/*      */     }
/*      */ 
/*  760 */     SerialPortEvent localSerialPortEvent = new SerialPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/*      */ 
/*  764 */     if (this.monThreadisInterrupted)
/*      */     {
/*  768 */       return true;
/*      */     }
/*  770 */     if (this.SPEventListener != null)
/*      */     {
/*  772 */       this.SPEventListener.serialEvent(localSerialPortEvent);
/*      */     }
/*      */ 
/*  778 */     if ((this.fd == 0) || (this.SPEventListener == null) || (this.monThread == null))
/*      */     {
/*  780 */       return true;
/*      */     }
/*      */ 
/*  784 */     return false;
/*      */   }
/*      */ 
/*      */   @Override
public void addEventListener(SerialPortEventListener paramSerialPortEventListener)
/*      */     throws TooManyListenersException
/*      */   {
/*  805 */     if (this.SPEventListener != null)
/*      */     {
/*  807 */       throw new TooManyListenersException();
/*      */     }
/*  809 */     this.SPEventListener = paramSerialPortEventListener;
/*  810 */     if (!this.MonitorThreadAlive)
/*      */     {
/*  812 */       this.MonitorThreadLock = true;
/*  813 */       this.monThread = new MonitorThread();
/*  814 */       this.monThread.start();
/*  815 */       waitForTheNativeCodeSilly();
/*  816 */       this.MonitorThreadAlive = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   @Override
public void removeEventListener()
/*      */   {
/*  828 */     waitForTheNativeCodeSilly();
/*      */ 
/*  830 */     if (this.monThreadisInterrupted == true)
/*      */     {
/*  832 */       z.reportln("\tRXTXPort:removeEventListener() already interrupted");
/*  833 */       this.monThread = null;
/*  834 */       this.SPEventListener = null;
/*  835 */       return;
/*      */     }
/*  837 */     if ((this.monThread != null) && (this.monThread.isAlive()))
/*      */     {
/*  841 */       this.monThreadisInterrupted = true;
/*      */ 
/*  849 */       interruptEventLoop();
/*      */       try
/*      */       {
/*  856 */         this.monThread.join(3000L);
/*      */       }
/*      */       catch (InterruptedException localInterruptedException)
/*      */       {
/*  860 */         Thread.currentThread().interrupt();
/*  861 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  871 */     this.monThread = null;
/*  872 */     this.SPEventListener = null;
/*  873 */     this.MonitorThreadLock = false;
/*  874 */     this.MonitorThreadAlive = false;
/*  875 */     this.monThreadisInterrupted = true;
/*  876 */     z.reportln("RXTXPort:removeEventListener() returning");
/*      */   }
/*      */ 
/*      */   protected void waitForTheNativeCodeSilly()
/*      */   {
/*  889 */     while (this.MonitorThreadLock)
/*      */       try
/*      */       {
/*  892 */         Thread.sleep(5L);
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   private native void nativeSetEventFlag(int paramInt1, int paramInt2, boolean paramBoolean);
/*      */ 
/*      */   @Override
public void notifyOnDataAvailable(boolean paramBoolean)
/*      */   {
/*  907 */     waitForTheNativeCodeSilly();
/*      */ 
/*  909 */     this.MonitorThreadLock = true;
/*  910 */     nativeSetEventFlag(this.fd, 1, paramBoolean);
/*      */ 
/*  912 */     this.monThread.Data = paramBoolean;
/*  913 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnOutputEmpty(boolean paramBoolean)
/*      */   {
/*  924 */     waitForTheNativeCodeSilly();
/*  925 */     this.MonitorThreadLock = true;
/*  926 */     nativeSetEventFlag(this.fd, 2, paramBoolean);
/*      */ 
/*  928 */     this.monThread.Output = paramBoolean;
/*  929 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnCTS(boolean paramBoolean)
/*      */   {
/*  940 */     waitForTheNativeCodeSilly();
/*  941 */     this.MonitorThreadLock = true;
/*  942 */     nativeSetEventFlag(this.fd, 3, paramBoolean);
/*  943 */     this.monThread.CTS = paramBoolean;
/*  944 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnDSR(boolean paramBoolean)
/*      */   {
/*  954 */     waitForTheNativeCodeSilly();
/*  955 */     this.MonitorThreadLock = true;
/*  956 */     nativeSetEventFlag(this.fd, 4, paramBoolean);
/*  957 */     this.monThread.DSR = paramBoolean;
/*  958 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnRingIndicator(boolean paramBoolean)
/*      */   {
/*  968 */     waitForTheNativeCodeSilly();
/*  969 */     this.MonitorThreadLock = true;
/*  970 */     nativeSetEventFlag(this.fd, 5, paramBoolean);
/*  971 */     this.monThread.RI = paramBoolean;
/*  972 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnCarrierDetect(boolean paramBoolean)
/*      */   {
/*  982 */     waitForTheNativeCodeSilly();
/*  983 */     this.MonitorThreadLock = true;
/*  984 */     nativeSetEventFlag(this.fd, 6, paramBoolean);
/*  985 */     this.monThread.CD = paramBoolean;
/*  986 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnOverrunError(boolean paramBoolean)
/*      */   {
/*  996 */     waitForTheNativeCodeSilly();
/*  997 */     this.MonitorThreadLock = true;
/*  998 */     nativeSetEventFlag(this.fd, 7, paramBoolean);
/*  999 */     this.monThread.OE = paramBoolean;
/* 1000 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnParityError(boolean paramBoolean)
/*      */   {
/* 1010 */     waitForTheNativeCodeSilly();
/* 1011 */     this.MonitorThreadLock = true;
/* 1012 */     nativeSetEventFlag(this.fd, 8, paramBoolean);
/* 1013 */     this.monThread.PE = paramBoolean;
/* 1014 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnFramingError(boolean paramBoolean)
/*      */   {
/* 1024 */     waitForTheNativeCodeSilly();
/* 1025 */     this.MonitorThreadLock = true;
/* 1026 */     nativeSetEventFlag(this.fd, 9, paramBoolean);
/* 1027 */     this.monThread.FE = paramBoolean;
/* 1028 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   @Override
public void notifyOnBreakInterrupt(boolean paramBoolean)
/*      */   {
/* 1038 */     waitForTheNativeCodeSilly();
/* 1039 */     this.MonitorThreadLock = true;
/* 1040 */     nativeSetEventFlag(this.fd, 10, paramBoolean);
/* 1041 */     this.monThread.BI = paramBoolean;
/* 1042 */     this.MonitorThreadLock = false;
/*      */   }
/*      */ 
/*      */   private native void nativeClose(String paramString);
/*      */ 
/*      */   @Override
public void close()
/*      */   {
/* 1052 */     synchronized (this)
/*      */     {
/* 1056 */       while (this.IOLocked > 0)
/*      */       {
/*      */         try
/*      */         {
/* 1061 */           wait(500L);
/*      */         }
/*      */         catch (InterruptedException localInterruptedException)
/*      */         {
/* 1065 */           Thread.currentThread().interrupt();
/* 1066 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1072 */       if (this.closeLock) return;
/* 1073 */       this.closeLock = true;
/*      */     }
/*      */ 
/* 1076 */     if (this.fd <= 0)
/*      */     {
/* 1078 */       z.reportln("RXTXPort:close detected bad File Descriptor");
/* 1079 */       return;
/*      */     }
/* 1081 */     setDTR(false);
/* 1082 */     setDSR(false);
/*      */ 
/* 1085 */     if (!this.monThreadisInterrupted)
/*      */     {
/* 1087 */       removeEventListener();
/*      */     }
/*      */ 
/* 1091 */     nativeClose(this.name);
/*      */ 
/* 1094 */     super.close();
/* 1095 */     this.fd = 0;
/* 1096 */     this.closeLock = false;
/*      */   }
/*      */ 
/*      */   @Override
protected void finalize()
/*      */   {
/* 1107 */     if (this.fd > 0)
/*      */     {
/* 1111 */       close();
/*      */     }
/* 1113 */     z.finalize();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setRcvFifoTrigger(int paramInt)
/*      */   {
/*      */   }
/*      */ 
/*      */   private static native void nativeStaticSetSerialPortParams(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticSetDSR(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticSetDTR(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticSetRTS(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsDSR(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsDTR(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsRTS(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsCTS(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsCD(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native boolean nativeStaticIsRI(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native int nativeStaticGetBaudRate(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native int nativeStaticGetDataBits(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native int nativeStaticGetParity(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private static native int nativeStaticGetStopBits(String paramString)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native byte nativeGetParityErrorChar()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetParityErrorChar(byte paramByte)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native byte nativeGetEndOfInputChar()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetEndOfInputChar(byte paramByte)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetUartType(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   native String nativeGetUartType()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetBaudBase(int paramInt)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native int nativeGetBaudBase()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetDivisor(int paramInt)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native int nativeGetDivisor()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetLowLatency()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeGetLowLatency()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeSetCallOutHangup(boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeGetCallOutHangup()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   private native boolean nativeClearCommInput()
/*      */     throws UnsupportedCommOperationException;
/*      */ 
/*      */   public static int staticGetBaudRate(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1745 */     return nativeStaticGetBaudRate(paramString);
/*      */   }
/*      */ 
/*      */   public static int staticGetDataBits(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1763 */     return nativeStaticGetDataBits(paramString);
/*      */   }
/*      */ 
/*      */   public static int staticGetParity(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1782 */     return nativeStaticGetParity(paramString);
/*      */   }
/*      */ 
/*      */   public static int staticGetStopBits(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1801 */     return nativeStaticGetStopBits(paramString);
/*      */   }
/*      */ 
/*      */   public static void staticSetSerialPortParams(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1829 */     nativeStaticSetSerialPortParams(paramString, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public static boolean staticSetDSR(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1852 */     return nativeStaticSetDSR(paramString, paramBoolean);
/*      */   }
/*      */ 
/*      */   public static boolean staticSetDTR(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1875 */     return nativeStaticSetDTR(paramString, paramBoolean);
/*      */   }
/*      */ 
/*      */   public static boolean staticSetRTS(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1898 */     return nativeStaticSetRTS(paramString, paramBoolean);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsRTS(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1919 */     return nativeStaticIsRTS(paramString);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsCD(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1939 */     return nativeStaticIsCD(paramString);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsCTS(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1959 */     return nativeStaticIsCTS(paramString);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsDSR(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1979 */     return nativeStaticIsDSR(paramString);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsDTR(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 1999 */     return nativeStaticIsDTR(paramString);
/*      */   }
/*      */ 
/*      */   public static boolean staticIsRI(String paramString)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2019 */     return nativeStaticIsRI(paramString);
/*      */   }
/*      */ 
/*      */   @Override
public byte getParityErrorChar()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2039 */     byte b = nativeGetParityErrorChar();
/*      */ 
/* 2043 */     return b;
/*      */   }
/*      */ 
/*      */   @Override
public boolean setParityErrorChar(byte paramByte)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2062 */     return nativeSetParityErrorChar(paramByte);
/*      */   }
/*      */ 
/*      */   @Override
public byte getEndOfInputChar()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2081 */     byte b = nativeGetEndOfInputChar();
/*      */ 
/* 2085 */     return b;
/*      */   }
/*      */ 
/*      */   @Override
public boolean setEndOfInputChar(byte paramByte)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2102 */     return nativeSetEndOfInputChar(paramByte);
/*      */   }
/*      */ 
/*      */   @Override
public boolean setUARTType(String paramString, boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2121 */     return nativeSetUartType(paramString, paramBoolean);
/*      */   }
/*      */ 
/*      */   @Override
public String getUARTType()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2134 */     return nativeGetUartType();
/*      */   }
/*      */ 
/*      */   @Override
public boolean setBaudBase(int paramInt)
/*      */     throws UnsupportedCommOperationException, IOException
/*      */   {
/* 2152 */     return nativeSetBaudBase(paramInt);
/*      */   }
/*      */ 
/*      */   @Override
public int getBaudBase()
/*      */     throws UnsupportedCommOperationException, IOException
/*      */   {
/* 2166 */     return nativeGetBaudBase();
/*      */   }
/*      */ 
/*      */   @Override
public boolean setDivisor(int paramInt)
/*      */     throws UnsupportedCommOperationException, IOException
/*      */   {
/* 2181 */     return nativeSetDivisor(paramInt);
/*      */   }
/*      */ 
/*      */   @Override
public int getDivisor()
/*      */     throws UnsupportedCommOperationException, IOException
/*      */   {
/* 2195 */     return nativeGetDivisor();
/*      */   }
/*      */ 
/*      */   @Override
public boolean setLowLatency()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2208 */     return nativeSetLowLatency();
/*      */   }
/*      */ 
/*      */   @Override
public boolean getLowLatency()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2221 */     return nativeGetLowLatency();
/*      */   }
/*      */ 
/*      */   @Override
public boolean setCallOutHangup(boolean paramBoolean)
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2235 */     return nativeSetCallOutHangup(paramBoolean);
/*      */   }
/*      */ 
/*      */   @Override
public boolean getCallOutHangup()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2249 */     return nativeGetCallOutHangup();
/*      */   }
/*      */ 
/*      */   public boolean clearCommInput()
/*      */     throws UnsupportedCommOperationException
/*      */   {
/* 2263 */     return nativeClearCommInput();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   88 */       z = new Zystem();
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*   93 */     System.loadLibrary("rxtxSerial");
/*   94 */     Initialize();
/*      */   }
/*      */ 
/*      */   class MonitorThread extends Thread
/*      */   {
/* 1617 */     private volatile boolean CTS = false;
/* 1618 */     private volatile boolean DSR = false;
/* 1619 */     private volatile boolean RI = false;
/* 1620 */     private volatile boolean CD = false;
/* 1621 */     private volatile boolean OE = false;
/* 1622 */     private volatile boolean PE = false;
/* 1623 */     private volatile boolean FE = false;
/* 1624 */     private volatile boolean BI = false;
/* 1625 */     private volatile boolean Data = false;
/* 1626 */     private volatile boolean Output = false;
/*      */ 
/*      */     MonitorThread()
/*      */     {
/*      */     }
/*      */ 
/*      */     @Override
public void run()
/*      */     {
/* 1640 */       RXTXPort.this.monThreadisInterrupted = false;
/* 1641 */       RXTXPort.this.eventLoop();
/*      */     }
/*      */ 
/*      */     @Override
protected void finalize()
/*      */       throws Throwable
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   class SerialInputStream extends InputStream
/*      */   {
/*      */     SerialInputStream()
/*      */     {
/*      */     }
/*      */ 
/*      */     @Override
public synchronized int read()
/*      */       throws IOException
/*      */     {
/* 1284 */       if (RXTXPort.this.fd == 0) throw new IOException();
/* 1285 */       if (RXTXPort.this.monThreadisInterrupted)
/*      */       {
/* 1287 */         RXTXPort.z.reportln("+++++++++ read() monThreadisInterrupted");
/*      */       }
/* 1289 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1290 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1295 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*      */ 
/* 1298 */         int localObject1 = RXTXPort.this.readByte();
/*      */ 
/* 1302 */         return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/* 1306 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1307 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public synchronized int read(byte[] paramArrayOfByte)
/*      */       throws IOException
/*      */     {
/* 1329 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1331 */         return 0;
/*      */       }
/* 1333 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1334 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1338 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1339 */         int  localObject1 = read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */ 
/* 1342 */         return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/* 1346 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1347 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */       throws IOException
/*      */     {
/* 1380 */       if (RXTXPort.this.fd == 0)
/*      */       {
/* 1384 */         RXTXPort.z.reportln("+++++++ IOException()\n");
/* 1385 */         throw new IOException();
/*      */       }
/*      */ 
/* 1388 */       if (paramArrayOfByte == null)
/*      */       {
/* 1390 */         RXTXPort.z.reportln("+++++++ NullPointerException()\n");
/*      */ 
/* 1393 */         throw new NullPointerException();
/*      */       }
/*      */ 
/* 1396 */       if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length))
/*      */       {
/* 1398 */         RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
/*      */ 
/* 1401 */         throw new IndexOutOfBoundsException();
/*      */       }
/*      */ 
/* 1407 */       if (paramInt2 == 0)
/*      */       {
/* 1411 */         return 0;
/*      */       }
/*      */ 
/* 1416 */       int i = paramInt2;
/*      */ 
/* 1418 */       if (RXTXPort.this.threshold == 0)
/*      */       {
/* 1427 */         int j = RXTXPort.this.nativeavailable();
/* 1428 */         if (j == 0)
/* 1429 */           i = 1;
/*      */         else {
/* 1431 */           i = Math.min(i, j);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1440 */         i = Math.min(i, RXTXPort.this.threshold);
/*      */       }
/* 1442 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1446 */         return 0;
/*      */       }
/* 1448 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1449 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1453 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1454 */         int localObject1 = RXTXPort.this.readArray(paramArrayOfByte, paramInt1, i);
/*      */ 
/* 1457 */         return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/* 1461 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1462 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized int read(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
/*      */       throws IOException
/*      */     {
/* 1493 */       if (RXTXPort.this.fd == 0)
/*      */       {
/* 1497 */         RXTXPort.z.reportln("+++++++ IOException()\n");
/* 1498 */         throw new IOException();
/*      */       }
/*      */ 
/* 1501 */       if (paramArrayOfByte1 == null)
/*      */       {
/* 1503 */         RXTXPort.z.reportln("+++++++ NullPointerException()\n");
/*      */ 
/* 1506 */         throw new NullPointerException();
/*      */       }
/*      */ 
/* 1509 */       if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte1.length))
/*      */       {
/* 1511 */         RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
/*      */ 
/* 1514 */         throw new IndexOutOfBoundsException();
/*      */       }
/*      */ 
/* 1520 */       if (paramInt2 == 0)
/*      */       {
/* 1524 */         return 0;
/*      */       }
/*      */ 
/* 1529 */       int i = paramInt2;
/*      */ 
/* 1531 */       if (RXTXPort.this.threshold == 0)
/*      */       {
/* 1540 */         int j = RXTXPort.this.nativeavailable();
/* 1541 */         if (j == 0)
/* 1542 */           i = 1;
/*      */         else {
/* 1544 */           i = Math.min(i, j);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1553 */         i = Math.min(i, RXTXPort.this.threshold);
/*      */       }
/* 1555 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1559 */         return 0;
/*      */       }
/* 1561 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1562 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1566 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1567 */         int localObject1 = RXTXPort.this.readTerminatedArray(paramArrayOfByte1, paramInt1, i, paramArrayOfByte2);
/*      */ 
/* 1570 */         return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/* 1574 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1575 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public synchronized int available()
/*      */       throws IOException
/*      */     {
/* 1585 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1587 */         return 0;
/*      */       }
/*      */ 
/* 1591 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1592 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1596 */         int localObject1 = RXTXPort.this.nativeavailable();
/*      */ 
/* 1600 */         return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/* 1604 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1605 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class SerialOutputStream extends OutputStream
/*      */   {
/*      */     SerialOutputStream()
/*      */     {
/*      */     }
/*      */ 
/*      */     @Override
public void write(int paramInt)
/*      */       throws IOException
/*      */     {
/* 1127 */       if (RXTXPort.this.speed == 0) return;
/* 1128 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1130 */         return;
/*      */       }
/* 1132 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1133 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try {
/* 1136 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1137 */         if (RXTXPort.this.fd == 0)
/*      */         {
/* 1139 */           throw new IOException();
/*      */         }
/* 1141 */         RXTXPort.this.writeByte(paramInt, RXTXPort.this.monThreadisInterrupted);
/*      */       }
/*      */       finally
/*      */       {
/* 1145 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1146 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public void write(byte[] paramArrayOfByte)
/*      */       throws IOException
/*      */     {
/* 1160 */       if (RXTXPort.this.speed == 0) return;
/* 1161 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1163 */         return;
/*      */       }
/* 1165 */       if (RXTXPort.this.fd == 0) throw new IOException();
/* 1166 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1167 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try {
/* 1170 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1171 */         RXTXPort.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length, RXTXPort.this.monThreadisInterrupted);
/*      */       }
/*      */       finally
/*      */       {
/* 1175 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1176 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public void write(byte[] bytes, int offset, int len)
/*      */       throws IOException
/*      */     {
/* 1190 */       if (RXTXPort.this.speed == 0) return;
/* 1191 */       if (offset + len > bytes.length)
/*      */       {
/* 1193 */         throw new IndexOutOfBoundsException("Invalid offset/length passed to read");
/*      */       }
/*      */ 
/* 1198 */       byte[] arrayOfByte = new byte[len];
/* 1199 */       System.arraycopy(bytes, offset, arrayOfByte, 0, len);
/*      */ 
/* 1204 */       if (RXTXPort.this.fd == 0) throw new IOException();
/* 1205 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1207 */         return;
/*      */       }
/* 1209 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1210 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1214 */         RXTXPort.this.waitForTheNativeCodeSilly();
/* 1215 */         RXTXPort.this.writeArray(arrayOfByte, 0, len, RXTXPort.this.monThreadisInterrupted);
/*      */       }
/*      */       finally
/*      */       {
/* 1219 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1220 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Override
public void flush()
/*      */       throws IOException
/*      */     {
/* 1230 */       if (RXTXPort.this.speed == 0) return;
/* 1231 */       if (RXTXPort.this.fd == 0) throw new IOException();
/* 1232 */       if (RXTXPort.this.monThreadisInterrupted == true)
/*      */       {
/* 1236 */         return;
/*      */       }
/* 1238 */       synchronized (RXTXPort.this.IOLockedMutex) {
/* 1239 */         RXTXPort.this.IOLocked += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1243 */         RXTXPort.this.waitForTheNativeCodeSilly();
/*      */ 
/* 1248 */         if (RXTXPort.this.nativeDrain(RXTXPort.this.monThreadisInterrupted)) {
/* 1249 */           RXTXPort.this.sendEvent(2, true);
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1255 */         synchronized (RXTXPort.this.IOLockedMutex) {
/* 1256 */           RXTXPort.this.IOLocked -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RXTXPort
 * JD-Core Version:    0.6.2
 */