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
package com.orientechnologies.orient.stresstest;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.OConstants;
import com.orientechnologies.orient.stresstest.workload.OWorkload;
import com.orientechnologies.orient.stresstest.workload.OWorkloadFactory;

/**
 * The main class of the OStressTester. It is instantiated from the OStressTesterCommandLineParser and takes care of launching the
 * needed threads (OOperationsExecutor) for executing the operations of the test.
 *
 * @author Andrea Iacono
 */
public class OStressTester {
  /**
   * The access mode to the database
   */
  public enum OMode {
    PLOCAL, MEMORY, REMOTE, DISTRIBUTED
  }

  private int                           threadsNumber;
  private ODatabaseIdentifier           databaseIdentifier;
  private int                           opsInTx;
  private String                        outputResultFile;
  private OConsoleProgressWriter        consoleProgressWriter;

  private static final OWorkloadFactory workloadFactory = new OWorkloadFactory();
  private OWorkload                     workload;

  public OStressTester(final OWorkload workload, ODatabaseIdentifier databaseIdentifier, int threadsNumber, int opsInTx,
      String outputResultFile) throws Exception {
    this.workload = workload;
    this.threadsNumber = threadsNumber;
    this.databaseIdentifier = databaseIdentifier;
    this.opsInTx = opsInTx;
    this.outputResultFile = outputResultFile;
    consoleProgressWriter = new OConsoleProgressWriter(this.workload);
  }

  public static void main(String[] args) {
    System.out.println(String.format("OrientDB Stress Tool v.%s - %s", OConstants.getVersion(), OConstants.COPYRIGHT));

    int returnValue = 1;
    try {
      final OStressTester stressTester = OStressTesterCommandLineParser.getStressTester(args);
      returnValue = stressTester.execute();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
    System.exit(returnValue);
  }

  @SuppressWarnings("unchecked")
  private int execute() throws Exception {

    int returnCode = 0;

    // we don't want logs from DB
    OLogManager.instance().setConsoleLevel("SEVERE");

    // creates the temporary DB where to execute the test
    ODatabaseUtils.createDatabase(databaseIdentifier);
    consoleProgressWriter.printMessage(String.format("Created database [%s].", databaseIdentifier.getUrl()));

    try {
      new Thread(consoleProgressWriter).start();

      consoleProgressWriter
          .printMessage(String.format("Starting workload %s - concurrencyLevel=%d...", workload.getName(), threadsNumber));

      final long startTime = System.currentTimeMillis();

      workload.execute(threadsNumber, databaseIdentifier);

      final long endTime = System.currentTimeMillis();

      consoleProgressWriter.sendShutdown();

      System.out.println(String.format("\nTotal execution time: %.3f secs", ((float) (endTime - startTime) / 1000f)));

      System.out.println(workload.getFinalResult());

      // if specified, writes output (in JSON format) to file
      if (outputResultFile != null) {
        // OIOUtils.writeFile(new File(outputResultFile), OJsonResultsFormatter.format(stressTestResults));
      }
    } catch (Exception ex) {
      System.err.println("\nAn error has occurred while running the stress test: " + ex.getMessage());
      returnCode = 1;
    } finally {
      // we don't need to drop the in-memory DB
      if (databaseIdentifier.getMode() != OMode.MEMORY) {
        ODatabaseUtils.dropDatabase(databaseIdentifier);
        consoleProgressWriter.printMessage(String.format("\nDropped database [%s].", databaseIdentifier.getUrl()));
      }
    }

    return returnCode;
  }

  public int getThreadsNumber() {
    return threadsNumber;
  }

  public OMode getMode() {
    return databaseIdentifier.getMode();
  }

  public ODatabaseIdentifier getDatabaseIdentifier() {
    return databaseIdentifier;
  }

  public String getPassword() {
    return databaseIdentifier.getPassword();
  }

  public int getTransactionsNumber() {
    return opsInTx;
  }

  public static OWorkloadFactory getWorkloadFactory() {
    return workloadFactory;
  }
}