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
package com.orientechnologies.orient.server.distributed;

import org.junit.Ignore;
import org.junit.Test;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;

/**
 * Distributed TX test against "plocal" protocol. <br>
 * This test is ignored because TX are not parallel on distributed yet (exclusive lock on dstorage.commit()).
 */
@Ignore
public class LocalConcurrentTxNoAutoRetryTest extends AbstractDistributedConcurrentTxTest {

  private static final int SERVERS = 3;

  @Test
  public void test() throws Exception {
    expectedConcurrentException = true;
    writerCount = 8;

    final int old = OGlobalConfiguration.DISTRIBUTED_CONCURRENT_TX_MAX_AUTORETRY.getValueAsInteger();
    OGlobalConfiguration.DISTRIBUTED_CONCURRENT_TX_MAX_AUTORETRY.setValue(1);
    try {

      init(SERVERS);
      prepare(false);
      execute();
    } finally {
      OGlobalConfiguration.DISTRIBUTED_CONCURRENT_TX_MAX_AUTORETRY.setValue(old);
    }
  }

  protected String getDatabaseURL(final ServerRun server) {
    return "plocal:" + server.getDatabasePath(getDatabaseName());
  }

  @Override
  public String getDatabaseName() {
    return "distributed-conc-txnoautoretry";
  }
}
