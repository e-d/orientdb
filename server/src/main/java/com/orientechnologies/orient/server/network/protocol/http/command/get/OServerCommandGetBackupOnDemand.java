package com.orientechnologies.orient.server.network.protocol.http.command.get;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.common.parser.OSystemVariableResolver;
import com.orientechnologies.common.parser.OVariableParser;
import com.orientechnologies.common.parser.OVariableParserListener;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.metadata.security.OSecurityNull;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

/**
 * Similar to OAtutomaticBackup plugin, except this one is triggered on-demand through HTTP GET request.
 * 
 * @author Ed St. Louis
 */
public class OServerCommandGetBackupOnDemand extends OServerCommandAbstract {

  private static final String[] NAMES = { "GET|backupondemand" };
  private static boolean isRunning = false;
  private OHttpResponse response;

  public enum VARIABLES {
    DBNAME, DATE
  }

  public enum MODE {
    FULL_BACKUP, INCREMENTAL_BACKUP, EXPORT
  }

  @Override
  public String[] getNames() {
    return NAMES;
  }

  public OServerCommandGetBackupOnDemand() {
  }

  @Override
  public boolean execute(final OHttpRequest iRequest, final OHttpResponse iResponse) throws Exception {

    if (isRunning) {
      iResponse.send(OHttpUtils.STATUS_INTERNALERROR_CODE, "Error Already Running", OHttpUtils.CONTENT_TEXT_PLAIN, "Error Already Running", null);
    } else {
      isRunning = true;
      try {
        iResponse.writeStatus(OHttpUtils.STATUS_OK_CODE, OHttpUtils.STATUS_OK_DESCRIPTION);
        iResponse.writeHeaders(OHttpUtils.CONTENT_TEXT_PLAIN, true);
        response = iResponse;
        runBackupOnDemand(iRequest);
      } finally {
        isRunning = false;
        response.getOutputStream().close();
      }
    }

    return false; // Is not a chained command.
  }

  private void runBackupOnDemand(final OHttpRequest iRequest) {
    String dir = iRequest.getParameter("targetDirectory");
    String targetDirectory = dir == null ? "backup/" : dir;

    String file = iRequest.getParameter("targetFileName");
    String targetFilename = file == null ? "${DBNAME}-${DATE:yyyyMMddHHmmss}.zip" : file;

    String compression = iRequest.getParameter("compressionLevel");
    int compressionLevel = compression == null ? 9 : Integer.parseInt(compression);

    String buffsize = iRequest.getParameter("bufferSize");
    int bufferSize = buffsize == null ? 1048576 : Integer.parseInt(buffsize);

    String mode = iRequest.getParameter("mode");
    MODE backupMode = mode == null ? MODE.FULL_BACKUP : MODE.valueOf(mode);
    
    String exportOptions = iRequest.getParameter("exportOptions");

    status("Scanning databases to backup...");

    int ok = 0, errors = 0;

    final Map<String, String> databases = server.getAvailableStorageNames();
    for (final Entry<String, String> database : databases.entrySet()) {
      final String dbURL = database.getValue();

      ODatabaseDocumentInternal db = null;
      try {

        db = new ODatabaseDocumentTx(dbURL);
        db.setProperty(ODatabase.OPTIONS.SECURITY.toString(), OSecurityNull.class);
        db.open("admin", "aaa");

        final long begin = System.currentTimeMillis();

        switch (backupMode) {
        case FULL_BACKUP:
          fullBackupDatabase(dbURL, targetDirectory + getFileName(targetFilename, database), db, compressionLevel, bufferSize);
          status("Full Backup of database '" + dbURL + "' completed in " + (System.currentTimeMillis() - begin) + "ms");
          break;

        case INCREMENTAL_BACKUP:
          incrementalBackupDatabase(dbURL, targetDirectory, db);
          status("Incremental Backup of database '" + dbURL + "' completed in " + (System.currentTimeMillis() - begin) + "ms");
          break;

        case EXPORT:
          exportDatabase(dbURL, targetDirectory + getFileName(targetFilename, database), db, exportOptions);
          status("Export of database '" + dbURL + "' completed in " + (System.currentTimeMillis() - begin) + "ms");
          break;
        }

        ok++;

      } catch (Exception e) {

        OLogManager.instance().error(this, "Error on backup of database '" + dbURL + "' to directory: " + targetDirectory, e);
        status("Error on backup of database '" + dbURL + "' to directory: " + targetDirectory + " Error: " + e);
        errors++;

      } finally {
        if (db != null)
          db.close();
      }
    }
    status(String.format("Backup On-Demand finished: %d ok, %d errors", ok, errors));
  }

  private void status(String message) {
    OLogManager.instance().info(this, message);
    try {
      response.writeLine(message);
      response.flush();
    } catch (IOException e) {
      OLogManager.instance().error(this, message, e);
    }
  }

  private String getFileName(String targetFilename, final Entry<String, String> dbName) {
    return (String) OVariableParser.resolveVariables(targetFilename, OSystemVariableResolver.VAR_BEGIN,
        OSystemVariableResolver.VAR_END, new OVariableParserListener() {
          @Override
          public String resolve(final String iVariable) {
            if (iVariable.equalsIgnoreCase(VARIABLES.DBNAME.toString()))
              return dbName.getKey();
            else if (iVariable.startsWith(VARIABLES.DATE.toString())) {
              return new SimpleDateFormat(iVariable.substring(VARIABLES.DATE.toString().length() + 1)).format(new Date());
            }

            // NOT FOUND
            throw new IllegalArgumentException("Variable '" + iVariable + "' was not found");
          }
        });
  }

  protected void incrementalBackupDatabase(final String dbURL, String iPath, final ODatabaseDocumentInternal db) throws IOException {
    // APPEND DB NAME TO THE DIRECTORY NAME
    if (!iPath.endsWith("/"))
      iPath += "/";
    iPath += db.getName();

    status(String.format("Backup-On-Demand: executing incremental backup of database '%s' to %s", dbURL, iPath));

    db.incrementalBackup(iPath);
  }

  protected void fullBackupDatabase(final String dbURL, final String iPath, final ODatabaseDocumentInternal db, int compressionLevel, int bufferSize) throws IOException {
    status(String.format("Backup-On-Demand: executing full backup of database '%s' to %s", dbURL, iPath));

    db.backup(new FileOutputStream(iPath), null, null, new OCommandOutputListener() {
      @Override
      public void onMessage(String iText) {
        status(iText);
      }
    }, compressionLevel, bufferSize);
  }

  protected void exportDatabase(final String dbURL, final String iPath, final ODatabaseDocumentInternal db, String exportOptions) throws IOException {

    status(String.format("Backup-On-Demand: executing export of database '%s' to %s", dbURL, iPath));

    final ODatabaseExport exp = new ODatabaseExport(db, iPath, new OCommandOutputListener() {
      @Override
      public void onMessage(String iText) {
        status(iText);
      }
    });

    if (exportOptions != null && !exportOptions.trim().isEmpty())
      exp.setOptions(exportOptions.trim());

    exp.exportDatabase().close();
  }
}
