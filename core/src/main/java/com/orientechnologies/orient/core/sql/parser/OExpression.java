/* Generated By:JJTree: Do not edit this line. OExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;

import java.util.List;
import java.util.Map;

public class OExpression extends SimpleNode {

  protected Boolean singleQuotes;
  protected Boolean doubleQuotes;

  public OExpression(int id) {
    super(id);
  }

  public OExpression(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public Object execute(OIdentifiable iCurrentRecord, OCommandContext ctx) {
    if (value instanceof ORid) {
      ORid v = (ORid) value;
      return new ORecordId(v.cluster.getValue().intValue(), v.position.getValue().longValue());
    } else if (value instanceof OMathExpression) {
      return ((OMathExpression) value).execute(iCurrentRecord, ctx);
    } else if (value instanceof OJson) {
      return ((OJson) value).toMap(iCurrentRecord, ctx);
    } else if (value instanceof String) {
      return value;
    } else if (value instanceof Number) {
      return value;
    }

    return value;

  }

  public boolean isBaseIdentifier() {
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).isBaseIdentifier();
    }

    return false;
  }

  public boolean isEarlyCalculated() {
    if (value instanceof Number) {
      return true;
    }
    if (value instanceof String) {
      return true;
    }
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).isEarlyCalculated();
    }

    return false;
  }

  public OIdentifier getDefaultAlias() {

    if (value instanceof String) {
      OIdentifier identifier = new OIdentifier(-1);
      identifier.setValue((String) value);
      return identifier;
    }
    // TODO create an interface for this;

    // if (value instanceof ORid) {
    // return null;// TODO
    // } else if (value instanceof OMathExpression) {
    // return null;// TODO
    // } else if (value instanceof OJson) {
    // return null;// TODO
    // }

    if (value instanceof OBaseExpression && ((OBaseExpression) value).isBaseIdentifier()) {
      return ((OBaseExpression) value).identifier.suffix.identifier;
    }

    String result = ("" + value).replaceAll("\\.", "_").replaceAll(" ", "_").replaceAll("\n", "_").replaceAll("\b", "_")
        .replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll("\\(", "_").replaceAll("\\)", "_");
    OIdentifier identifier = new OIdentifier(-1);
    identifier.setValue(result);
    return identifier;
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    if (value == null) {
      builder.append("null");
    } else if (value instanceof SimpleNode) {
      ((SimpleNode) value).toString(params, builder);
    } else if (value instanceof String) {
      if (Boolean.TRUE.equals(singleQuotes)) {
        builder.append("'" + value + "'");
      } else {
        builder.append("\"" + value + "\"");
      }
    } else {
      builder.append("" + value);
    }
  }

  public static String encode(String s) {
    StringBuilder builder = new StringBuilder(s.length());
    for (char c : s.toCharArray()) {
      if (c == '\n') {
        builder.append("\\n");
        continue;
      }
      if (c == '\t') {
        builder.append("\\t");
        continue;
      }
      if (c == '\\' || c == '"') {
        builder.append("\\");
      }
      builder.append(c);
    }
    return builder.toString();
  }

  public boolean supportsBasicCalculation() {
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).supportsBasicCalculation();
    }
    return true;
  }

  public boolean isIndexedFunctionCal() {
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).isIndexedFunctionCall();
    }
    return false;
  }

  public static String encodeSingle(String s) {

    StringBuilder builder = new StringBuilder(s.length());
    for (char c : s.toCharArray()) {
      if (c == '\n') {
        builder.append("\\n");
        continue;
      }
      if (c == '\t') {
        builder.append("\\t");
        continue;
      }
      if (c == '\\' || c == '\'') {
        builder.append("\\");
      }
      builder.append(c);
    }
    return builder.toString();
  }

  public long estimateIndexedFunction(OFromClause target, OCommandContext context, OBinaryCompareOperator operator, Object right) {
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).estimateIndexedFunction(target, context, operator, right);
    }
    return -1;
  }

  public Iterable<OIdentifiable> executeIndexedFunction(OFromClause target, OCommandContext context,
      OBinaryCompareOperator operator, Object right) {
    if (value instanceof OMathExpression) {
      return ((OMathExpression) value).executeIndexedFunction(target, context, operator, right);
    }
    return null;
  }

  /**
   * if the condition involved the current pattern (MATCH statement, eg. $matched.something = foo),
   * returns the name of involved pattern aliases ("something" in this case)
   *
   * @return a list of pattern aliases involved in this condition. Null it does not involve the pattern
   */
  List<String> getMatchPatternInvolvedAliases() {
    if (value instanceof OMathExpression)
      return ((OMathExpression)value).getMatchPatternInvolvedAliases();
    return null;
  }
}
/* JavaCC - OriginalChecksum=9c860224b121acdc89522ae97010be01 (do not edit this line) */
