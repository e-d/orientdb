/* Generated By:JJTree: Do not edit this line. ODeleteEdgeStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import java.util.List;
import java.util.Map;

public class ODeleteEdgeStatement extends OStatement {
  private static final Object unset           = new Object();

  protected OIdentifier       className;
  protected OIdentifier       targetClusterName;

  protected ORid              rid;
  protected List<ORid>        rids;

  protected ORid              leftRid;
  protected List<ORid>        leftRids;
  protected OSelectStatement  leftStatement;
  protected OInputParameter   leftParam;
  protected Object            leftParamValue  = unset;
  protected OIdentifier       leftIdentifier;

  protected ORid              rightRid;
  protected List<ORid>        rightRids;
  protected OSelectStatement  rightStatement;
  protected OInputParameter   rightParam;
  protected Object            rightParamValue = unset;
  protected OIdentifier       rightIdentifier;

  protected OWhereClause      whereClause;

  protected Integer           limit;

  public ODeleteEdgeStatement(int id) {
    super(id);
  }

  public ODeleteEdgeStatement(OrientSql p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("DELETE EDGE");

    if (className != null) {
      result.append(" ");
      result.append(className.toString());
      if (targetClusterName != null) {
        result.append(" CLUSTER ");
        result.append(targetClusterName.toString());
      }
    }

    if (rid != null) {
      result.append(" ");
      result.append(rid.toString());
    }
    if (rids != null) {
      result.append("[");
      boolean first = true;
      for (ORid rid : rids) {
        if (!first) {
          result.append(", ");
        }
        result.append(rid.toString());
        first = false;
      }
      result.append("]");
    }

    if (leftRid != null || leftRids != null || leftStatement != null || leftParam != null || leftIdentifier != null) {
      result.append(" FROM ");
      if (leftRid != null) {
        result.append(leftRid.toString());
      } else if (leftRids != null) {
        result.append("[");
        boolean first = true;
        for (ORid rid : leftRids) {
          if (!first) {
            result.append(", ");
          }
          result.append(rid.toString());
          first = false;
        }
        result.append("]");
      } else if (leftStatement != null) {
        result.append("(");
        result.append(leftStatement.toString());
        result.append(")");
      } else if (leftParam != null) {
        if (leftParamValue == unset) {
          result.append(leftParam.toString());
        } else if (leftParamValue == null) {
          result.append("NULL");
        } else if (leftParamValue instanceof String) {
          result.append("\"" + OExpression.encode("" + leftParamValue) + "\"");
        } else {
          result.append(leftParamValue.toString());
        }
      } else if (leftIdentifier != null) {
        result.append(leftIdentifier.toString());
      }

    }
    if (rightRid != null || rightRids != null || rightStatement != null || rightParam != null || rightIdentifier != null) {
      result.append(" TO ");
      if (rightRid != null) {
        result.append(rightRid.toString());
      } else if (rightRids != null) {
        result.append("[");
        boolean first = true;
        for (ORid rid : rightRids) {
          if (!first) {
            result.append(", ");
          }
          result.append(rid.toString());
          first = false;
        }
        result.append("]");
      } else if (rightStatement != null) {
        result.append("(");
        result.append(rightStatement.toString());
        result.append(")");
      } else if (rightParam != null) {
        if (rightParamValue == unset) {
          result.append(rightParam.toString());
        } else if (rightParamValue == null) {
          result.append("NULL");
        } else if (rightParamValue instanceof String) {
          result.append("\"" + OExpression.encode("" + rightParamValue) + "\"");
        } else {
          result.append(rightParamValue.toString());
        }
      } else if (rightIdentifier != null) {
        result.append(rightIdentifier.toString());
      }
    }

    if (whereClause != null) {
      result.append(" WHERE ");
      result.append(whereClause.toString());
    }

    if (limit != null) {
      result.append(" LIMIT ");
      result.append(limit);
    }

    return result.toString();
  }

  public void replaceParameters(Map<Object, Object> params) {
    if (leftStatement != null) {
      leftStatement.replaceParameters(params);
    }
    if (leftParam != null) {
      Object val = leftParam.bindFromInputParams(params);
      if (val != leftParam) {
        leftParamValue = val;
      }
    }

    if (rightStatement != null) {
      rightStatement.replaceParameters(params);
    }
    if (rightParam != null) {
      Object val = rightParam.bindFromInputParams(params);
      if (val != rightParam) {
        rightParamValue = val;
      }
    }

    if (whereClause != null) {
      whereClause.replaceParameters(params);
    }
  }

}
/* JavaCC - OriginalChecksum=8f4c5bafa99572d7d87a5d0a2c7d55a7 (do not edit this line) */
