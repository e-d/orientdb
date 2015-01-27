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

import com.orientechnologies.common.console.DefaultConsoleReader;

public class OServerMain {
	private static OServer instance;

	public static OServer create() throws Exception {
		instance = new OServer();
		return instance;
	}

  public static OServer create(boolean shutdownEngineOnExit) throws Exception {
    instance = new OServer(shutdownEngineOnExit);
    return instance;
  }

	public static OServer server() {
		return instance;
	}

	public static void main(final String[] args) throws Exception {
		instance = OServerMain.create();
		instance.startup().activate();

		preventServiceStop();
	}

	/**
	 * To make OrientDB compatible with Apache Commons Daemon (to be a Windows service), we have to keep the main 
	 * method from returning. Waiting for console input seems to do the trick.
	 * 
	 * @throws IOException
	 */
	private static void preventServiceStop() throws IOException {
		System.out.println("Preventing JVM from exiting by waiting for console input...");
		String exitInput = new DefaultConsoleReader().readLine();
		System.out.println("Console input read returned (" + exitInput + ").");
	}
	
	/**
	 * To make OrientDB compatible with Apache Commons Daemon (to be a Windows service), we have to keep the main 
	 * method from returning. This closes console input to make the main method return.
	 * 
	 * @throws IOException
	 */
	public static void enableServiceStop() {
		System.out.println("Re-enabling JVM exit by closing console input...");
		try {
			System.in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Console input closed.");
	}
}
