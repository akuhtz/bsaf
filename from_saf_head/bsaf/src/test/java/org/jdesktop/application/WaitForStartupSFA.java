/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

/**
 * Identical to WaitForStartupApplication.
 * <p/>
 * Support for launching a SingleFrameApplication from a non-EDT thread and
 * waiting until its startup method has finished running on the EDT.
 */
public class WaitForStartupSFA extends SingleFrameApplication {
    private static final Object lock = new Object(); // static: Application is a singleton
    private boolean started = false;

    /**
     * Unblock the launchAndWait() method.
     */
    protected void startup() {
        synchronized (lock) {
            started = true;
            lock.notifyAll();
        }
    }

    boolean isStarted() {
        return started;
    }

    /**
     * Launch the specified subclsas of WaitForStartupApplication and block
     * (wait) until it's startup() method has run.
     */
    public static void launchAndWait(Class<? extends WaitForStartupSFA> applicationClass) {
        Launcher.getInstance().launch(applicationClass, new String[]{});
        synchronized (lock) {
            while (true) {

                Application app = Application.getInstance(WaitForStartupSFA.class);
                if (app instanceof WaitForStartupSFA) {
                    if (((WaitForStartupSFA) app).isStarted()) {
                        break;
                    }
                }
				try {
                    lock.wait();
                }
                catch (InterruptedException e) {
                    System.err.println("launchAndWait interrupted!");
                    break;
                }
            }
        }
    }
}
