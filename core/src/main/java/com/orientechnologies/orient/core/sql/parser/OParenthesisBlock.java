/* Generated By:JJTree: Do not edit this line. OParenthesisBlock.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.util.List;
import java.util.Map;

public class OParenthesisBlock extends OBooleanExpression {

  OBooleanExpression subElement;

  public OParenthesisBlock(int id) {
    super(id);
  }

  public OParenthesisBlock(OrientSql p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override
  public boolean evaluate(OIdentifiable currentRecord, OCommandContext ctx) {
    return subElement.evaluate(currentRecord, ctx);
  }


  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("(");
    subElement.toString(params, builder);
    builder.append(" )");
  }

  @Override
  public boolean supportsBasicCalculation() {
    return subElement.supportsBasicCalculation();
  }

  @Override
  protected int getNumberOfExternalCalculations() {
    return subElement.getNumberOfExternalCalculations();
  }

  @Override
  protected List<Object> getExternalCalculationConditions() {
    return subElement.getExternalCalculationConditions();
  }

  @Override public List<OAndBlock> flatten() {
    return subElement.flatten();
  }

  @Override public List<String> getMatchPatternInvolvedAliases() {
    return subElement.getMatchPatternInvolvedAliases();
  }
}
/* JavaCC - OriginalChecksum=9a16b6cf7d051382acb94c45067631a9 (do not edit this line) */
