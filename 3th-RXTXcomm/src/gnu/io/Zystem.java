/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class Zystem
/*     */ {
/*     */   public static final int SILENT_MODE = 0;
/*     */   public static final int FILE_MODE = 1;
/*     */   public static final int NET_MODE = 2;
/*     */   public static final int MEX_MODE = 3;
/*     */   public static final int PRINT_MODE = 4;
/*     */   public static final int J2EE_MSG_MODE = 5;
/*     */   public static final int J2SE_LOG_MODE = 6;
/*  85 */   static int mode = 0;
/*     */   private static String target;
/*     */ 
/*     */   public Zystem(int paramInt)
/*     */     throws UnSupportedLoggerException
/*     */   {
/*  92 */     mode = paramInt;
/*  93 */     startLogger("asdf");
/*     */   }
/*     */ 
/*     */   public Zystem()
/*     */     throws UnSupportedLoggerException
/*     */   {
/* 109 */     String str = System.getProperty("gnu.io.log.mode");
/* 110 */     if (str != null)
/*     */     {
/* 112 */       if ("SILENT_MODE".equals(str))
/*     */       {
/* 114 */         mode = 0;
/*     */       }
/* 116 */       else if ("FILE_MODE".equals(str))
/*     */       {
/* 118 */         mode = 1;
/*     */       }
/* 120 */       else if ("NET_MODE".equals(str))
/*     */       {
/* 122 */         mode = 2;
/*     */       }
/* 124 */       else if ("MEX_MODE".equals(str))
/*     */       {
/* 126 */         mode = 3;
/*     */       }
/* 128 */       else if ("PRINT_MODE".equals(str))
/*     */       {
/* 130 */         mode = 4;
/*     */       }
/* 132 */       else if ("J2EE_MSG_MODE".equals(str))
/*     */       {
/* 134 */         mode = 5;
/*     */       }
/* 136 */       else if ("J2SE_LOG_MODE".equals(str))
/*     */       {
/* 138 */         mode = 6;
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 144 */           mode = Integer.parseInt(str);
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException)
/*     */         {
/* 148 */           mode = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 154 */       mode = 0;
/*     */     }
/* 156 */     startLogger("asdf");
/*     */   }
/*     */ 
/*     */   public void startLogger()
/*     */     throws UnSupportedLoggerException
/*     */   {
/* 162 */     if ((mode == 0) || (mode == 4))
/*     */     {
/* 165 */       return;
/*     */     }
/* 167 */     throw new UnSupportedLoggerException("Target Not Allowed");
/*     */   }
/*     */ 
/*     */   public void startLogger(String paramString)
/*     */     throws UnSupportedLoggerException
/*     */   {
/* 174 */     target = paramString;
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 198 */     mode = 0;
/* 199 */     target = null;
/*     */   }
/*     */ 
/*     */   public void filewrite(String paramString)
/*     */   {
/*     */     try {
/* 205 */       RandomAccessFile localRandomAccessFile = new RandomAccessFile(target, "rw");
/*     */ 
/* 207 */       localRandomAccessFile.seek(localRandomAccessFile.length());
/* 208 */       localRandomAccessFile.writeBytes(paramString);
/* 209 */       localRandomAccessFile.close();
/*     */     } catch (Exception localException) {
/* 211 */       System.out.println("Debug output file write failed");
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean report(String paramString)
/*     */   {
/* 217 */     if (mode != 2)
/*     */     {
/* 221 */       if (mode == 4)
/*     */       {
/* 223 */         System.out.println(paramString);
/* 224 */         return true;
/*     */       }
/* 226 */       if (mode != 3)
/*     */       {
/* 230 */         if (mode == 0)
/*     */         {
/* 232 */           return true;
/*     */         }
/* 234 */         if (mode == 1)
/*     */         {
/* 236 */           filewrite(paramString);
/*     */         } else {
/* 238 */           if (mode == 5)
/*     */           {
/* 240 */             return false;
/*     */           }
/* 242 */           if (mode == 6)
/*     */           {
/* 244 */             Logger.getLogger("gnu.io").fine(paramString);
/* 245 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean reportln()
/*     */   {
/* 253 */     if (mode != 2)
/*     */     {
/* 258 */       if (mode == 4)
/*     */       {
/* 260 */         System.out.println();
/* 261 */         return true;
/*     */       }
/* 263 */       if (mode != 3)
/*     */       {
/* 268 */         if (mode == 0)
/*     */         {
/* 270 */           return true;
/*     */         }
/* 272 */         if (mode == 1)
/*     */         {
/* 274 */           filewrite("\n");
/*     */         }
/* 276 */         else if (mode == 5)
/*     */         {
/* 278 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 280 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean reportln(String paramString)
/*     */   {
/* 286 */     if (mode != 2)
/*     */     {
/* 291 */       if (mode == 4)
/*     */       {
/* 293 */         System.out.println(paramString);
/* 294 */         return true;
/*     */       }
/* 296 */       if (mode != 3)
/*     */       {
/* 301 */         if (mode == 0)
/*     */         {
/* 303 */           return true;
/*     */         }
/* 305 */         if (mode == 1)
/*     */         {
/* 307 */           filewrite(paramString + "\n");
/*     */         } else {
/* 309 */           if (mode == 5)
/*     */           {
/* 311 */             return false;
/*     */           }
/* 313 */           if (mode == 6)
/*     */           {
/* 315 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 317 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.Zystem
 * JD-Core Version:    0.6.2
 */