/*     */
package gnu.io;

/*     */
/*     */ import java.io.FileDescriptor;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */
/*     */ public class CommPortIdentifier
/*     */ {
	/*     */ public static final int PORT_SERIAL = 1;
	/*     */ public static final int PORT_PARALLEL = 2;
	/*     */ public static final int PORT_I2C = 3;
	/*     */ public static final int PORT_RS485 = 4;
	/*     */ public static final int PORT_RAW = 5;
	/*     */ private String PortName;
	/*  79 */ private boolean Available = true;
	/*     */ private String Owner;
	/*     */ private CommPort commport;
	/*     */ private CommDriver RXTXDriver;
	/*     */ static CommPortIdentifier CommPortIndex;
	/*     */ CommPortIdentifier next;
	/*     */ private int PortType;
	/*     */ private static final boolean debug = false;
	/* 104 */ static Object Sync = new Object();
	/*     */ Vector ownershipListener;
	/*     */ private boolean HideOwnerEvents;

	/*     */
	/*     */ CommPortIdentifier(String paramString, CommPort paramCommPort, int paramInt, CommDriver paramCommDriver)
	/*     */ {
		/* 127 */ this.PortName = paramString;
		/* 128 */ this.commport = paramCommPort;
		/* 129 */ this.PortType = paramInt;
		/* 130 */ this.next = null;
		/* 131 */ this.RXTXDriver = paramCommDriver;
		/*     */ }

	/*     */
	/*     */ public static void addPortName(String paramString, int paramInt, CommDriver paramCommDriver)
	/*     */ {
		/* 148 */ AddIdentifierToList(new CommPortIdentifier(paramString, null, paramInt, paramCommDriver));
		/*     */ }

	/*     */
	/*     */ private static void AddIdentifierToList(CommPortIdentifier paramCommPortIdentifier)
	/*     */ {
		/* 161 */ synchronized (Sync)
		/*     */ {
			/* 163 */ if (CommPortIndex == null)
			/*     */ {
				/* 165 */ CommPortIndex = paramCommPortIdentifier;
				/*     */ }
			/*     */ else
			/*     */ {
				/* 170 */ CommPortIdentifier localCommPortIdentifier = CommPortIndex;
				/* 171 */ while (localCommPortIdentifier.next != null)
				/*     */ {
					/* 173 */ localCommPortIdentifier = localCommPortIdentifier.next;
					/*     */ }
				/*     */
				/* 176 */ localCommPortIdentifier.next = paramCommPortIdentifier;
				/*     */ }
			/*     */ }
		/*     */ }

	/*     */
	/*     */ public void addPortOwnershipListener(CommPortOwnershipListener paramCommPortOwnershipListener)
	/*     */ {
		/* 194 */ if (this.ownershipListener == null)
		/*     */ {
			/* 196 */ this.ownershipListener = new Vector();
			/*     */ }
		/*     */
		/* 201 */ if (!this.ownershipListener.contains(paramCommPortOwnershipListener))
		/*     */ {
			/* 203 */ this.ownershipListener.addElement(paramCommPortOwnershipListener);
			/*     */ }
		/*     */ }

	/*     */
	/*     */ public String getCurrentOwner()
	/*     */ {
		/* 217 */ return this.Owner;
		/*     */ }

	/*     */
	/*     */ public String getName()
	/*     */ {
		/* 230 */ return this.PortName;
		/*     */ }

	/*     */
	/*     */ public static CommPortIdentifier getPortIdentifier(String paramString)/*     */ throws NoSuchPortException
	/*     */ {
		/*     */ CommPortIdentifier localCommPortIdentifier;
		/* 245 */ synchronized (Sync)
		/*     */ {
			/* 247 */ localCommPortIdentifier = CommPortIndex;
			/* 248 */ while ((localCommPortIdentifier != null) && (!localCommPortIdentifier.PortName.equals(paramString)))
			{
				/* 249 */ localCommPortIdentifier = localCommPortIdentifier.next;
				/*     */ }
			/* 251 */ if (localCommPortIdentifier == null)
			/*     */ {
				/* 257 */ getPortIdentifiers();
				/* 258 */ localCommPortIdentifier = CommPortIndex;
				/* 259 */ while ((localCommPortIdentifier != null) && (!localCommPortIdentifier.PortName.equals(paramString)))
				{
					/* 260 */ localCommPortIdentifier = localCommPortIdentifier.next;
					/*     */ }
				/*     */ }
			/*     */ }
		/* 264 */ if (localCommPortIdentifier != null)
			return localCommPortIdentifier;
		/*     */
		/* 269 */ throw new NoSuchPortException();
		/*     */ }

	/*     */
	/*     */ public static CommPortIdentifier getPortIdentifier(CommPort paramCommPort)/*     */ throws NoSuchPortException
	/*     */ {
		/*     */ CommPortIdentifier localCommPortIdentifier;
		/* 285 */ synchronized (Sync)
		/*     */ {
			/* 287 */ localCommPortIdentifier = CommPortIndex;
			/* 288 */ while ((localCommPortIdentifier != null) && (localCommPortIdentifier.commport != paramCommPort))
				/* 289 */ localCommPortIdentifier = localCommPortIdentifier.next;
			/*     */ }
		/* 291 */ if (localCommPortIdentifier != null)
		{
			/* 292 */ return localCommPortIdentifier;
			/*     */ }
		/*     */
		/* 296 */ throw new NoSuchPortException();
		/*     */ }

	/*     */
	/*     */ public static Enumeration getPortIdentifiers()
	/*     */ {
		/* 311 */ synchronized (Sync)
		/*     */ {
			/* 313 */ HashMap localHashMap = new HashMap();
			/* 314 */ CommPortIdentifier localCommPortIdentifier1 = CommPortIndex;
			/* 315 */ while (localCommPortIdentifier1 != null)
			{
				/* 316 */ localHashMap.put(localCommPortIdentifier1.PortName, localCommPortIdentifier1);
				/* 317 */ localCommPortIdentifier1 = localCommPortIdentifier1.next;
				/*     */ }
			/* 319 */ CommPortIndex = null;
			/*     */ try
			/*     */ {
				/* 326 */ CommDriver localCommDriver = (CommDriver) Class.forName("gnu.io.RXTXCommDriver").newInstance();
				/* 327 */ localCommDriver.initialize();
				/*     */
				/* 331 */ CommPortIdentifier localCommPortIdentifier2 = CommPortIndex;
				/* 332 */ Object localObject1 = null;
				/* 333 */ while (localCommPortIdentifier2 != null)
				{
					/* 334 */ CommPortIdentifier localCommPortIdentifier3 = (CommPortIdentifier) localHashMap.get(localCommPortIdentifier2.PortName);
					/* 335 */ if ((localCommPortIdentifier3 != null) && (localCommPortIdentifier3.PortType == localCommPortIdentifier2.PortType))
					/*     */ {
						/* 337 */ localCommPortIdentifier3.RXTXDriver = localCommPortIdentifier2.RXTXDriver;
						/* 338 */ localCommPortIdentifier3.next = localCommPortIdentifier2.next;
						/* 339 */ if (localObject1 == null)
							/* 340 */ CommPortIndex = localCommPortIdentifier3;
						/*     */ else
						{
							/* 342 */ ((CommPortIdentifier) localObject1).next = localCommPortIdentifier3;
							/*     */ }
						/* 344 */ localObject1 = localCommPortIdentifier3;
						/*     */ } else
					{
						/* 346 */ localObject1 = localCommPortIdentifier2;
						/*     */ }
					/* 348 */ localCommPortIdentifier2 = localCommPortIdentifier2.next;
					/*     */ }
				/*     */ }
			/*     */ catch (Throwable localThrowable)
			/*     */ {
				/* 353 */ System.err.println(localThrowable + " thrown while loading " + "gnu.io.RXTXCommDriver");
				/* 354 */ System.err.flush();
				/*     */ }
			/*     */ }
		/* 357 */ return new CommPortEnumerator();
		/*     */ }

	/*     */
	/*     */ public int getPortType()
	/*     */ {
		/* 370 */ return this.PortType;
		/*     */ }

	/*     */
	/*     */ public synchronized boolean isCurrentlyOwned()
	/*     */ {
		/* 383 */ return !this.Available;
		/*     */ }

	/*     */
	/*     */ public synchronized CommPort open(FileDescriptor paramFileDescriptor)/*     */ throws UnsupportedCommOperationException
	/*     */ {
		/* 396 */ throw new UnsupportedCommOperationException();
		/*     */ }

	/*     */
	/*     */ private native String native_psmisc_report_owner(String paramString);

	/*     */
	/*     */ public CommPort open(String paramString, int timeOut)/*     */ throws PortInUseException
	/*     */ {
		/*     */ boolean bool;
		/* 416 */ synchronized (this)
		{
			/* 417 */ bool = this.Available;
			/* 418 */ if (bool)
			/*     */ {
				/* 420 */ this.Available = false;
				/* 421 */ this.Owner = paramString;
				/*     */ }
			/*     */ }
		/* 424 */ if (!bool)
		/*     */ {
			/* 426 */ long l1 = System.currentTimeMillis() + timeOut;
			/*     */
			/* 428 */ fireOwnershipEvent(3);
			/*     */
			/* 430 */ synchronized (this)
			/*     */ {
				/*     */ while (true)
				/*     */ {
					/*     */ long l2;
					/* 431 */ if ((!this.Available) && ((l2 = System.currentTimeMillis()) < l1))
					{
						/*     */ try
						/*     */ {
							/* 434 */ wait(l1 - l2);
							/*     */ }
						/*     */ catch (InterruptedException localInterruptedException)
						/*     */ {
							/* 438 */ Thread.currentThread().interrupt();
							/*     */ }
						/*     */ }
					/*     */ }
				/*				bool = this.Available;
								if (bool)
								 {
									this.Available = false;
									this.Owner = paramString;
									 }
				*/				/*     */ }
			/*     */ }
		/* 450 */ if (!bool)
		/*     */ {
			/* 452 */ throw new PortInUseException(getCurrentOwner());
			/*     */ }
		/*     */ try
		/*     */ {
			/* 456 */ if (this.commport == null)
			/*     */ {
				/* 458 */ this.commport = this.RXTXDriver.getCommPort(this.PortName, this.PortType);
				/*     */ }
			/* 460 */ if (this.commport != null)
			/*     */ {
				/* 462 */ fireOwnershipEvent(1);
				/* 463 */ return this.commport;
				/*     */ }
			/*     */
			/* 467 */ throw new PortInUseException(native_psmisc_report_owner(this.PortName));
			/*     */ }
		/*     */ finally
		/*     */ {
			/* 471 */ if (this.commport == null)
			/*     */ {
				/* 473 */ synchronized (this)
				{
					/* 474 */ this.Available = true;
					/* 475 */ this.Owner = null;
					/*     */ }
				/*     */ }
			/*     */ }
		/*     */ }

	/*     */
	/*     */ public void removePortOwnershipListener(CommPortOwnershipListener paramCommPortOwnershipListener)
	/*     */ {
		/* 492 */ if (this.ownershipListener != null)
			/* 493 */ this.ownershipListener.removeElement(paramCommPortOwnershipListener);
		/*     */ }

	/*     */
	/*     */ void internalClosePort()
	/*     */ {
		/* 506 */ synchronized (this)
		/*     */ {
			/* 508 */ this.Owner = null;
			/* 509 */ this.Available = true;
			/* 510 */ this.commport = null;
			/*     */
			/* 512 */ notifyAll();
			/*     */ }
		/* 514 */ fireOwnershipEvent(2);
		/*     */ }

	/*     */
	/*     */ void fireOwnershipEvent(int paramInt)
	/*     */ {
		/* 527 */ if (this.ownershipListener != null)
		/*     */ {
			/* 530 */ Enumeration localEnumeration = this.ownershipListener.elements();
			/*     */ CommPortOwnershipListener localCommPortOwnershipListener;
			/* 532 */ for (; localEnumeration.hasMoreElements(); /* 532 */ localCommPortOwnershipListener.ownershipChange(paramInt))
				/* 533 */ localCommPortOwnershipListener = (CommPortOwnershipListener) localEnumeration.nextElement();
			/*     */ }
		/*     */ }

	/*     */
	/*     */ static
	/*     */ {
		/*     */ try
		/*     */ {
			/* 107 */ CommDriver localCommDriver = (CommDriver) Class.forName("gnu.io.RXTXCommDriver").newInstance();
			/* 108 */ localCommDriver.initialize();
			/*     */ }
		/*     */ catch (Throwable localThrowable)
		/*     */ {
			/* 112 */ System.err.println(localThrowable + " thrown while loading " + "gnu.io.RXTXCommDriver");
			/*     */ }
		/*     */
		/* 117 */ String str = System.getProperty("os.name");
		/* 118 */ if (str.toLowerCase().indexOf("linux") == -1)
			;
		/* 123 */ System.loadLibrary("rxtxSerial");
		/*     */ }
	/*     */ }
/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortIdentifier
 * JD-Core Version:    0.6.2
 */