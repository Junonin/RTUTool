/*     */ package gnu.io;
/*     */ 
/*     */ import java.util.EventObject;
/*     */ 
/*     */ public class RawPortEvent extends EventObject
/*     */ {
/*     */   public static final int DATA_AVAILABLE = 1;
/*     */   public static final int OUTPUT_BUFFER_EMPTY = 2;
/*     */   public static final int CTS = 3;
/*     */   public static final int DSR = 4;
/*     */   public static final int RI = 5;
/*     */   public static final int CD = 6;
/*     */   public static final int OE = 7;
/*     */   public static final int PE = 8;
/*     */   public static final int FE = 9;
/*     */   public static final int BI = 10;
/*     */   private boolean OldValue;
/*     */   private boolean NewValue;
/*     */   private int eventType;
/*     */ 
/*     */   public RawPortEvent(RawPort paramRawPort, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  89 */     super(paramRawPort);
/*  90 */     this.OldValue = paramBoolean1;
/*  91 */     this.NewValue = paramBoolean2;
/*  92 */     this.eventType = paramInt;
/*     */   }
/*     */ 
/*     */   public int getEventType() {
/*  96 */     return this.eventType;
/*     */   }
/*     */ 
/*     */   public boolean getNewValue() {
/* 100 */     return this.NewValue;
/*     */   }
/*     */ 
/*     */   public boolean getOldValue() {
/* 104 */     return this.OldValue;
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RawPortEvent
 * JD-Core Version:    0.6.2
 */