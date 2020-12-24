/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class RXTXCommDriver
/*     */   implements CommDriver
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   private static final boolean devel = false;
/*  79 */   private static final boolean noVersionOutput = "true".equals(System.getProperty("gnu.io.rxtx.NoVersionOutput"));
/*     */   private String deviceDirectory;
/*     */   private String osName;
/*     */ 
/*     */   private native boolean registerKnownPorts(int paramInt);
/*     */ 
/*     */   private native boolean isPortPrefixValid(String paramString);
/*     */ 
/*     */   private native boolean testRead(String paramString, int paramInt);
/*     */ 
/*     */   private native String getDeviceDirectory();
/*     */ 
/*     */   public static native String nativeGetVersion();
/*     */ 
/*     */   private final String[] getValidPortPrefixes(String[] paramArrayOfString)
/*     */   {
/* 150 */     String[] arrayOfString1 = new String[256];
/*     */ 
/* 153 */     if (paramArrayOfString == null);
/* 158 */     int i = 0;
/* 159 */     for (int j = 0; j < paramArrayOfString.length; j++) {
/* 160 */       if (isPortPrefixValid(paramArrayOfString[j])) {
/* 161 */         arrayOfString1[(i++)] = paramArrayOfString[j];
/*     */       }
/*     */     }
/*     */ 
/* 165 */     String[] arrayOfString2 = new String[i];
/* 166 */     System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
/* 167 */     if (arrayOfString1[0] == null);
/* 191 */     return arrayOfString2; } 
/*     */   private void checkSolaris(String paramString, int paramInt) { // Byte code:
/*     */     //   0: iconst_1
/*     */     //   1: newarray char
/*     */     //   3: dup
/*     */     //   4: iconst_0
/*     */     //   5: bipush 91
/*     */     //   7: castore
/*     */     //   8: astore_3
/*     */     //   9: aload_3
/*     */     //   10: iconst_0
/*     */     //   11: bipush 97
/*     */     //   13: castore
/*     */     //   14: aload_3
/*     */     //   15: iconst_0
/*     */     //   16: caload
/*     */     //   17: bipush 123
/*     */     //   19: if_icmpge +51 -> 70
/*     */     //   22: aload_0
/*     */     //   23: aload_1
/*     */     //   24: new 2	java/lang/String
/*     */     //   27: dup
/*     */     //   28: aload_3
/*     */     //   29: invokespecial 5	java/lang/String:<init>	([C)V
/*     */     //   32: invokevirtual 6	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   35: iload_2
/*     */     //   36: invokespecial 7	gnu/io/RXTXCommDriver:testRead	(Ljava/lang/String;I)Z
/*     */     //   39: ifeq +20 -> 59
/*     */     //   42: aload_1
/*     */     //   43: new 2	java/lang/String
/*     */     //   46: dup
/*     */     //   47: aload_3
/*     */     //   48: invokespecial 5	java/lang/String:<init>	([C)V
/*     */     //   51: invokevirtual 6	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   54: iload_2
/*     */     //   55: aload_0
/*     */     //   56: invokestatic 8	gnu/io/CommPortIdentifier:addPortName	(Ljava/lang/String;ILgnu/io/CommDriver;)V
/*     */     //   59: aload_3
/*     */     //   60: iconst_0
/*     */     //   61: dup2
/*     */     //   62: caload
/*     */     //   63: iconst_1
/*     */     //   64: iadd
/*     */     //   65: i2c
/*     */     //   66: castore
/*     */     //   67: goto -53 -> 14
/*     */     //   70: aload_3
/*     */     //   71: iconst_0
/*     */     //   72: bipush 48
/*     */     //   74: castore
/*     */     //   75: aload_3
/*     */     //   76: iconst_0
/*     */     //   77: caload
/*     */     //   78: bipush 57
/*     */     //   80: if_icmpgt +51 -> 131
/*     */     //   83: aload_0
/*     */     //   84: aload_1
/*     */     //   85: new 2	java/lang/String
/*     */     //   88: dup
/*     */     //   89: aload_3
/*     */     //   90: invokespecial 5	java/lang/String:<init>	([C)V
/*     */     //   93: invokevirtual 6	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   96: iload_2
/*     */     //   97: invokespecial 7	gnu/io/RXTXCommDriver:testRead	(Ljava/lang/String;I)Z
/*     */     //   100: ifeq +20 -> 120
/*     */     //   103: aload_1
/*     */     //   104: new 2	java/lang/String
/*     */     //   107: dup
/*     */     //   108: aload_3
/*     */     //   109: invokespecial 5	java/lang/String:<init>	([C)V
/*     */     //   112: invokevirtual 6	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   115: iload_2
/*     */     //   116: aload_0
/*     */     //   117: invokestatic 8	gnu/io/CommPortIdentifier:addPortName	(Ljava/lang/String;ILgnu/io/CommDriver;)V
/*     */     //   120: aload_3
/*     */     //   121: iconst_0
/*     */     //   122: dup2
/*     */     //   123: caload
/*     */     //   124: iconst_1
/*     */     //   125: iadd
/*     */     //   126: i2c
/*     */     //   127: castore
/*     */     //   128: goto -53 -> 75
/*     */     //   131: return }
}
/* 227 */   private void registerValidPorts(String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt) { int i = 0;
/* 228 */     int j = 0;
/*     */ 
/* 255 */     if ((paramArrayOfString1 != null) && (paramArrayOfString2 != null))
/*     */     {
/* 257 */       for (i = 0; i < paramArrayOfString1.length; i++)
/* 258 */         for (j = 0; j < paramArrayOfString2.length; j++)
/*     */         {
/* 274 */           String str1 = paramArrayOfString2[j];
/* 275 */           int k = str1.length();
/* 276 */           String str2 = paramArrayOfString1[i];
/* 277 */           if (str2.length() >= k) {
/* 278 */             String str3 = str2.substring(k).toUpperCase();
/*     */ 
/* 280 */             String str4 = str2.substring(k).toLowerCase();
/*     */ 
/* 282 */             if ((str2.regionMatches(0, str1, 0, k)) && (str3.equals(str4)))
/*     */             {
/*     */               String str5;
/* 288 */               if (this.osName.toLowerCase().indexOf("windows") == -1)
/*     */               {
/* 290 */                 str5 = this.deviceDirectory + str2;
/*     */               }
/*     */               else
/*     */               {
/* 294 */                 str5 = str2;
/*     */               }
/*     */ 
/* 303 */               if ((this.osName.equals("Solaris")) || (this.osName.equals("SunOS")))
/*     */               {
/* 305 */                 checkSolaris(str5, paramInt);
/* 306 */               } else if (testRead(str5, paramInt))
/*     */               {
/* 308 */                 CommPortIdentifier.addPortName(str5, paramInt, this);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   @Override
public void initialize()
/*     */   {
/* 352 */     this.osName = System.getProperty("os.name");
/* 353 */     this.deviceDirectory = getDeviceDirectory();
/*     */ 
/* 359 */     for (int i = 1; i <= 2; i++)
/* 360 */       if ((!registerSpecifiedPorts(i)) && 
/* 361 */         (!registerKnownPorts(i)))
/* 362 */         registerScannedPorts(i);
/*     */   }
/*     */ 
/*     */   private void addSpecifiedPorts(String paramString, int paramInt)
/*     */   {
/* 370 */     String str1 = System.getProperty("path.separator", ":");
/* 371 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, str1);
/*     */ 
/* 375 */     while (localStringTokenizer.hasMoreElements())
/*     */     {
/* 377 */       String str2 = localStringTokenizer.nextToken();
/*     */ 
/* 379 */       if (testRead(str2, paramInt))
/* 380 */         CommPortIdentifier.addPortName(str2, paramInt, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean registerSpecifiedPorts(int paramInt)
/*     */   {
/* 404 */     String str1 = null;
/* 405 */     Properties localProperties1 = System.getProperties();
/*     */     Properties localProperties2;
/*     */     Iterator localIterator;
/*     */     try
/*     */     {
/* 410 */       String str2 = System.getProperty("java.ext.dirs") + System.getProperty("file.separator");
/* 411 */       FileInputStream localFileInputStream = new FileInputStream(str2 + "gnu.io.rxtx.properties");
/* 412 */       localProperties2 = new Properties();
/* 413 */       localProperties2.load(localFileInputStream);
/* 414 */       System.setProperties(localProperties2);
/* 415 */       for (localIterator = localProperties2.keySet().iterator(); localIterator.hasNext(); ) {
/* 416 */         String str3 = (String)localIterator.next();
/* 417 */         System.setProperty(str3, localProperties2.getProperty(str3));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 431 */     switch (paramInt) {
/*     */     case 1:
/* 433 */       if ((str1 = System.getProperty("gnu.io.rxtx.SerialPorts")) == null)
/* 434 */         str1 = System.getProperty("gnu.io.SerialPorts"); break;
/*     */     case 2:
/* 438 */       if ((str1 = System.getProperty("gnu.io.rxtx.ParallelPorts")) == null)
/* 439 */         str1 = System.getProperty("gnu.io.ParallelPorts"); break;
/*     */     }
/*     */ 
/* 446 */     System.setProperties(localProperties1);
/* 447 */     if (str1 != null) {
/* 448 */       addSpecifiedPorts(str1, paramInt);
/* 449 */       return true;
/*     */     }
/* 451 */     return false;
/*     */   }
/*     */ 
/*     */   private void registerScannedPorts(int paramInt)
/*     */   {
/*     */     String[] localObject1;
/*     */     String[] arrayOfString1;
/* 466 */     if (this.osName.equals("Windows CE"))
/*     */     {
/* 468 */       localObject1 = new String[] { "COM1:", "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:" };
/*     */ 
/* 471 */       
/*     */     }
/*     */     else
/*     */     {
/*     */       //int i;
/* 473 */       if (this.osName.toLowerCase().indexOf("windows") != -1)
/*     */       {
/* 475 */         localObject1 = new String[259];
/* 476 */         for (int i = 1; i <= 256; i++)
/*     */         {
/* 478 */           localObject1[i - 1] = ("COM" + i);
/*     */         }
/* 480 */         for (int i = 1; i <= 3; i++)
/*     */         {
/* 482 */           localObject1[i + 255] = ("LPT" + i);
/*     */         }
/* 484 */         //localObject1 = localObject2;
/*     */       }
/* 486 */       else if ((this.osName.equals("Solaris")) || (this.osName.equals("SunOS")))
/*     */       {
/* 523 */         localObject1 = new String[2];
/* 524 */          int i = 0;
/* 525 */         File localFile = null;
/*     */ 
/* 527 */         localFile = new File("/dev/term");
/* 528 */         if (localFile.list().length > 0) {
/* 529 */            localObject1[i++] = "term/";
/*     */         }
/*     */ 
/* 535 */         String[] arrayOfString2 = new String[i];
/* 536 */         for (i=0; i >= 0; i--)
/* 537 */           arrayOfString2[i] = localObject1[i];
/* 538 */         localObject1 = arrayOfString2;
/*     */       }
/*     */       else
/*     */       {
/* 542 */         File localObject2 = new File(this.deviceDirectory);
/* 543 */         arrayOfString1 = ((File)localObject2).list();
/* 544 */         localObject1 = arrayOfString1;
/*     */       }
/*     */     }
/* 546 */     if (localObject1 == null)
/*     */     {
/* 550 */       return;
/*     */     }
/*     */ 
/* 553 */     Object localObject2 = new String[0];
/* 554 */     switch (paramInt)
/*     */     {
/*     */     case 1:
/* 574 */       if (this.osName.equals("Linux"))
/*     */       {
/* 576 */         arrayOfString1 = new String[] { "ttyS", "ttySA", "ttyUSB", "rfcomm", "ttyircomm" };
/*     */ 
/* 583 */         localObject2 = arrayOfString1;
/*     */       }
/* 585 */       else if (this.osName.equals("Linux-all-ports"))
/*     */       {
/* 590 */         arrayOfString1 = new String[] { "comx", "holter", "modem", "rfcomm", "ttyircomm", "ttycosa0c", "ttycosa1c", "ttyACM", "ttyC", "ttyCH", "ttyD", "ttyE", "ttyF", "ttyH", "ttyI", "ttyL", "ttyM", "ttyMX", "ttyP", "ttyR", "ttyS", "ttySI", "ttySR", "ttyT", "ttyUSB", "ttyV", "ttyW", "ttyX" };
/*     */ 
/* 622 */         localObject2 = arrayOfString1;
/*     */       }
/* 624 */       else if (this.osName.toLowerCase().indexOf("qnx") != -1)
/*     */       {
/* 626 */         arrayOfString1 = new String[] { "ser" };
/*     */ 
/* 629 */         localObject2 = arrayOfString1;
/*     */       }
/* 631 */       else if (this.osName.equals("Irix"))
/*     */       {
/* 633 */         arrayOfString1 = new String[] { "ttyc", "ttyd", "ttyf", "ttym", "ttyq", "tty4d", "tty4f", "midi", "us" };
/*     */ 
/* 644 */         localObject2 = arrayOfString1;
/*     */       }
/* 646 */       else if (this.osName.equals("FreeBSD"))
/*     */       {
/* 648 */         arrayOfString1 = new String[] { "ttyd", "cuaa", "ttyA", "cuaA", "ttyD", "cuaD", "ttyE", "cuaE", "ttyF", "cuaF", "ttyR", "cuaR", "stl" };
/*     */ 
/* 663 */         localObject2 = arrayOfString1;
/*     */       }
/* 665 */       else if (this.osName.equals("NetBSD"))
/*     */       {
/* 667 */         arrayOfString1 = new String[] { "tty0" };
/*     */ 
/* 670 */         localObject2 = arrayOfString1;
/*     */       }
/* 672 */       else if ((this.osName.equals("Solaris")) || (this.osName.equals("SunOS")))
/*     */       {
/* 675 */         arrayOfString1 = new String[] { "term/", "cua/" };
/*     */ 
/* 679 */         localObject2 = arrayOfString1;
/*     */       }
/* 681 */       else if (this.osName.equals("HP-UX"))
/*     */       {
/* 683 */         arrayOfString1 = new String[] { "tty0p", "tty1p" };
/*     */ 
/* 687 */         localObject2 = arrayOfString1;
/*     */       }
/* 690 */       else if ((this.osName.equals("UnixWare")) || (this.osName.equals("OpenUNIX")))
/*     */       {
/* 693 */         arrayOfString1 = new String[] { "tty00s", "tty01s", "tty02s", "tty03s" };
/*     */ 
/* 699 */         localObject2 = arrayOfString1;
/*     */       }
/* 702 */       else if (this.osName.equals("OpenServer"))
/*     */       {
/* 704 */         arrayOfString1 = new String[] { "tty1A", "tty2A", "tty3A", "tty4A", "tty5A", "tty6A", "tty7A", "tty8A", "tty9A", "tty10A", "tty11A", "tty12A", "tty13A", "tty14A", "tty15A", "tty16A", "ttyu1A", "ttyu2A", "ttyu3A", "ttyu4A", "ttyu5A", "ttyu6A", "ttyu7A", "ttyu8A", "ttyu9A", "ttyu10A", "ttyu11A", "ttyu12A", "ttyu13A", "ttyu14A", "ttyu15A", "ttyu16A" };
/*     */ 
/* 738 */         localObject2 = arrayOfString1;
/*     */       }
/* 740 */       else if ((this.osName.equals("Compaq's Digital UNIX")) || (this.osName.equals("OSF1")))
/*     */       {
/* 742 */         arrayOfString1 = new String[] { "tty0" };
/*     */ 
/* 745 */         localObject2 = arrayOfString1;
/*     */       }
/* 748 */       else if (this.osName.equals("BeOS"))
/*     */       {
/* 750 */         arrayOfString1 = new String[] { "serial" };
/*     */ 
/* 753 */         localObject2 = arrayOfString1;
/*     */       }
/* 755 */       else if (this.osName.equals("Mac OS X"))
/*     */       {
/* 757 */         arrayOfString1 = new String[] { "cu.KeyUSA28X191.", "tty.KeyUSA28X191.", "cu.KeyUSA28X181.", "tty.KeyUSA28X181.", "cu.KeyUSA19181.", "tty.KeyUSA19181." };
/*     */ 
/* 771 */         localObject2 = arrayOfString1;
/*     */       }
/* 773 */       else if (this.osName.toLowerCase().indexOf("windows") != -1)
/*     */       {
/* 775 */         arrayOfString1 = new String[] { "COM" };
/*     */ 
/* 779 */         localObject2 = arrayOfString1;
/* 780 */       }break;
/*     */     case 2:
/* 796 */       if (this.osName.equals("Linux"))
/*     */       {
/* 806 */         arrayOfString1 = new String[] { "lp" };
/*     */ 
/* 809 */         localObject2 = arrayOfString1;
/*     */       }
/* 811 */       else if (this.osName.equals("FreeBSD"))
/*     */       {
/* 813 */         arrayOfString1 = new String[] { "lpt" };
/*     */ 
/* 816 */         localObject2 = arrayOfString1;
/*     */       }
/* 818 */       else if (this.osName.toLowerCase().indexOf("windows") != -1)
/*     */       {
/* 820 */         arrayOfString1 = new String[] { "LPT" };
/*     */ 
/* 823 */         localObject2 = arrayOfString1;
/*     */       }
/*     */       else
/*     */       {
/* 827 */         arrayOfString1 = new String[0];
/* 828 */         localObject2 = arrayOfString1;
/*     */       }
/* 830 */       break;
/*     */     }
/*     */ 
/* 835 */     registerValidPorts(localObject1, (String[])localObject2, paramInt);
/*     */   }
/*     */ 
/*     */   @Override
public CommPort getCommPort(String paramString, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 856 */       switch (paramInt) {
/*     */       case 1:
/* 858 */         if (this.osName.toLowerCase().indexOf("windows") == -1)
/*     */         {
/* 861 */           return new RXTXPort(paramString);
/*     */         }
/*     */ 
/* 865 */         return new RXTXPort(this.deviceDirectory + paramString);
/*     */       case 2:
/* 868 */         return new LPRPort(paramString);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (PortInUseException localPortInUseException)
/*     */     {
/*     */     }
/*     */ 
/* 878 */     return null;
/*     */   }
/*     */ 
/*     */   public void Report(String paramString)
/*     */   {
/* 884 */     System.out.println(paramString);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  84 */     System.loadLibrary("rxtxSerial");
/*     */ 
/*  96 */     String str1 = RXTXVersion.getVersion();
/*     */     String str2;
/*     */     try
/*     */     {
/*  99 */       str2 = RXTXVersion.nativeGetVersion();
/*     */     }
/*     */     catch (Error localError)
/*     */     {
/* 103 */       str2 = nativeGetVersion();
/*     */     }
/*     */ 
/* 116 */     if (!str1.equals(str2))
/*     */     {
/* 118 */       System.out.println("WARNING:  RXTX Version mismatch\n\tJar version = " + str1 + "\n\tnative lib Version = " + str2);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RXTXCommDriver
 * JD-Core Version:    0.6.2
 */