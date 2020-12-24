/*     */ package gnu.io;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ class Configure extends Frame
/*     */ {
/*     */   Checkbox[] cb;
/*     */   Panel p1;
/*     */   static final int PORT_SERIAL = 1;
/*     */   static final int PORT_PARALLEL = 2;
/*  70 */   int PortType = 1;
/*     */ 
/* 217 */   String EnumMessage = "gnu.io.rxtx.properties has not been detected.\n\nThere is no consistant means of detecting ports on this operating System.  It is necessary to indicate which ports are valid on this system before proper port enumeration can happen.  Please check the ports that are valid on this system and select Save";
/*     */ 
/*     */   private void saveSpecifiedPorts()
/*     */   {
/*  75 */     String str2 = System.getProperty("java.home");
/*  76 */     String str3 = System.getProperty("path.separator", ":");
/*  77 */     String str4 = System.getProperty("file.separator", "/");
/*  78 */     String str5 = System.getProperty("line.separator");
/*     */     String str1;
/*  81 */     if (this.PortType == 1) {
/*  82 */       str1 = str2 + str4 + "lib" + str4 + "gnu.io.rxtx.SerialPorts";
/*     */     }
/*  85 */     else if (this.PortType == 2) {
/*  86 */       str1 = str2 + "gnu.io.rxtx.ParallelPorts";
/*     */     }
/*     */     else
/*     */     {
/*  90 */       System.out.println("Bad Port Type!");
/*  91 */       return;
/*     */     }
/*  93 */     System.out.println(str1);
/*     */     try
/*     */     {
/*  96 */       FileOutputStream localFileOutputStream = new FileOutputStream(str1);
/*     */ 
/*  98 */       for (int i = 0; i < 128; i++)
/*     */       {
/* 100 */         if (this.cb[i].getState())
/*     */         {
/* 102 */           String str6 = this.cb[i].getLabel() + str3;
/*     */ 
/* 104 */           localFileOutputStream.write(str6.getBytes());
/*     */         }
/*     */       }
/* 107 */       localFileOutputStream.write(str5.getBytes());
/* 108 */       localFileOutputStream.close();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 112 */       System.out.println("IOException!");
/*     */     }
/*     */   }
/*     */ 
/*     */   void addCheckBoxes(String paramString)
/*     */   {
/* 118 */     for (int i = 0; i < 128; i++)
/* 119 */       if (this.cb[i] != null)
/* 120 */         this.p1.remove(this.cb[i]);
/* 121 */     for (int i = 1; i < 129; i++)
/*     */     {
/* 123 */       this.cb[(i - 1)] = new Checkbox(paramString + i);
/* 124 */       this.p1.add("NORTH", this.cb[(i - 1)]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Configure()
/*     */   {
/* 130 */     int i = 640;
/* 131 */     int j = 480;
/* 132 */     this.cb = new Checkbox[''];
/* 133 */     Frame localFrame = new Frame("Configure gnu.io.rxtx.properties");
/*     */ 
/* 135 */     String str1 = System.getProperty("file.separator", "/");
/*     */     String str2;
/* 137 */     if (str1.compareTo("/") != 0)
/* 138 */       str2 = "COM";
/*     */     else {
/* 140 */       str2 = "/dev/";
/*     */     }
/* 142 */     localFrame.setBounds(100, 50, i, j);
/* 143 */     localFrame.setLayout(new BorderLayout());
/* 144 */     this.p1 = new Panel();
/* 145 */     this.p1.setLayout(new GridLayout(16, 4));
/* 146 */     ActionListener local1 = new ActionListener()
/*     */     {
/*     */       @Override
public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 149 */         String str = paramAnonymousActionEvent.getActionCommand();
/* 150 */         if (str.equals("Save"))
/* 151 */           Configure.this.saveSpecifiedPorts();
/*     */       }
/*     */     };
/* 156 */     addCheckBoxes(str2);
/* 157 */     TextArea localTextArea = new TextArea(this.EnumMessage, 5, 50, 3);
/*     */ 
/* 159 */     localTextArea.setSize(50, i);
/* 160 */     localTextArea.setEditable(false);
/*     */ 
/* 162 */     Panel localPanel = new Panel();
/* 163 */     localPanel.add(new Label("Port Name:"));
/* 164 */     TextField localTextField = new TextField(str2, 8);
/* 165 */     localTextField.addActionListener(new ActionListener() {
/*     */       private final Frame frame = null;
/*     */ 
/* 168 */       @Override
public void actionPerformed(ActionEvent paramAnonymousActionEvent) { Configure.this.addCheckBoxes(paramAnonymousActionEvent.getActionCommand());
/* 169 */         this.frame.setVisible(true);
/*     */       }
/*     */     });
/* 172 */     localPanel.add(localTextField);
/* 173 */     Checkbox localCheckbox = new Checkbox("Keep Ports");
/* 174 */     localPanel.add(localCheckbox);
/* 175 */     Button[] arrayOfButton = new Button[6];
/* 176 */     int k = 0; for (int m = 4; m < 129; k++)
/*     */     {
/* 178 */       arrayOfButton[k] = new Button("1-" + m);
/* 179 */       arrayOfButton[k].addActionListener(new ActionListener() {
/*     */         private final Frame frame = null;
/*     */ 
/* 182 */         @Override
public void actionPerformed(ActionEvent paramAnonymousActionEvent) { int i = Integer.parseInt(paramAnonymousActionEvent.getActionCommand().substring(2));
/*     */ 
/* 184 */           for (int j = 0; j < i; j++)
/*     */           {
/* 186 */             Configure.this.cb[j].setState(!Configure.this.cb[j].getState());
/*     */ 
/* 188 */             this.frame.setVisible(true);
/*     */           }
/*     */         }
/*     */       });
/* 192 */       localPanel.add(arrayOfButton[k]);
/*     */ 
/* 176 */       m *= 2;
/*     */     }
/*     */ 
/* 194 */     Button localButton1 = new Button("More");
/* 195 */     Button localButton2 = new Button("Save");
/* 196 */     localButton1.addActionListener(local1);
/* 197 */     localButton2.addActionListener(local1);
/* 198 */     localPanel.add(localButton1);
/* 199 */     localPanel.add(localButton2);
/* 200 */     localFrame.add("South", localPanel);
/* 201 */     localFrame.add("Center", this.p1);
/* 202 */     localFrame.add("North", localTextArea);
/* 203 */     localFrame.addWindowListener(new WindowAdapter()
/*     */     {
/*     */       @Override
public void windowClosing(WindowEvent paramAnonymousWindowEvent)
/*     */       {
/* 207 */         System.exit(0);
/*     */       }
/*     */     });
/* 211 */     localFrame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public static void main(String[] paramArrayOfString) {
/* 215 */     new Configure();
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.Configure
 * JD-Core Version:    0.6.2
 */