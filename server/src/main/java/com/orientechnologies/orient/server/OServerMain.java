/*
 *
 *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */
package com.orientechnologies.orient.server;

import java.io.IOException;
import java.nio.channels.Selector;

import com.orientechnologies.common.log.OLogManager;

public class OServerMain {
  private OServer server;
  private static OServerMain instance;
  private static Selector serviceStopPreventionSelector;
  
  static {
    instance = new OServerMain();
  }
  
  public static OServer create() throws Exception {
    instance.server = new OServer();
    return instance.server;
  }

  public static OServer create(boolean shutdownEngineOnExit) throws Exception {
    instance.server = new OServer(shutdownEngineOnExit);
    return instance.server;
  }

  public static OServer server() {
    return instance.server;
  }

  public static void main(final String[] args) {
    try {
      // To enable use of OrientDB by Apache Commons Daemon, we need to check arguments.
      if (args != null && args.length > 0 && args[0].equalsIgnoreCase("stop")) {
        shutdown();
      } else {
        // Assume "start" otherwise.
        OLogManager.instance().info(instance, "Creating server...");
        instance.server = OServerMain.create();
        instance.server.startup().activate();
        preventServiceStop();
      }
    } catch (Throwable t) {
      OLogManager.instance().error(instance, "Failed to start server.", t);
      t.printStackTrace();
    }
  }
  
  /**
   * Called by the "stop" function of Apache Commons Daemon.
   */
  private static void shutdown() {
    OLogManager.instance().info(instance, "Shutdown service request received...");
    instance.server.shutdown();
    enableServiceStop();
  }

  /**
   * To make OrientDB compatible with Apache Commons Daemon (to be a Windows service), we have to keep the main 
   * method from returning. Waiting for a selector action seems to do the trick.
   * 
   * @throws IOException
   */
  private static void preventServiceStop() {
    OLogManager.instance().info(instance, "Preventing JVM from exiting by waiting for selector action...");
    int exitInput = 0;
    try {
      serviceStopPreventionSelector = Selector.open();
      exitInput = serviceStopPreventionSelector.select(); // Blocks until wakeup is called.
    } catch (Exception e) {
      OLogManager.instance().error(instance, "Couldn't wait for selector wakeup.", e);
    }
    OLogManager.instance().info(instance, "Selector returned with code: " + exitInput + ".");
  }
  
  /**
   * To make OrientDB compatible with Apache Commons Daemon (to be a Windows service), we have to keep the main 
   * method from returning. This wakes up a selector to make the main method return.
   * 
   * @throws IOException
   */
  private static void enableServiceStop() {
    OLogManager.instance().info(instance, "Re-enabling JVM exit by waking up the selector...");
    serviceStopPreventionSelector.wakeup();
    OLogManager.instance().info(instance, "Selector has been awoken.");
  }
}
