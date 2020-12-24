/*     */ package gnu.io;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ 
/*     */ class CommPortEnumerator
/*     */   implements Enumeration
/*     */ {
/*     */   private CommPortIdentifier index;
/*     */   private static final boolean debug = false;
/*     */ 
/*     */   public Object nextElement()
/*     */   {
/*  93 */     synchronized (CommPortIdentifier.Sync)
/*     */     {
/*  95 */       if (this.index != null) this.index = this.index.next; else
/*  96 */         this.index = CommPortIdentifier.CommPortIndex;
/*  97 */       return this.index;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasMoreElements()
/*     */   {
/* 111 */     synchronized (CommPortIdentifier.Sync)
/*     */     {
/* 113 */       if (this.index != null) return this.index.next != null;
/* 114 */       return CommPortIdentifier.CommPortIndex != null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortEnumerator
 * JD-Core Version:    0.6.2
 */