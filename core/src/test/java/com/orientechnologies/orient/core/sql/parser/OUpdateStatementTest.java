package com.orientechnologies.orient.core.sql.parser;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.testng.Assert.fail;

@Test
public class OUpdateStatementTest {

  protected SimpleNode checkRightSyntax(String query) {
    SimpleNode result = checkSyntax(query, true);
    return checkSyntax(result.toString(), true);
  }

  protected SimpleNode checkWrongSyntax(String query) {
    return checkSyntax(query, false);
  }

  protected SimpleNode checkSyntax(String query, boolean isCorrect) {
    OrientSql osql = getParserFor(query);
    try {
      SimpleNode result = osql.parse();
      if (!isCorrect) {
        System.out.println(query);
        if (result != null) {
          System.out.println("->");
          System.out.println(result.toString());
          System.out.println("............");
        }
        fail();
      }


      return result;
    } catch (Exception e) {
      if (isCorrect) {
        System.out.println(query);
        e.printStackTrace();
        fail();
      }
    }
    return null;
  }

  public void testSimpleInsert() {
    checkRightSyntax("update  Foo set a = b");
    checkRightSyntax("update  Foo set a = 'b'");
    checkRightSyntax("update  Foo set a = 1");
    checkRightSyntax("update  Foo set a = 1+1");
    checkRightSyntax("update  Foo set a = a.b.toLowerCase()");

    checkRightSyntax("update  Foo set a = b, b=c");
    checkRightSyntax("update  Foo set a = 'b', b=1");
    checkRightSyntax("update  Foo set a = 1, c=k");
    checkRightSyntax("update  Foo set a = 1+1, c=foo, d='bar'");
    checkRightSyntax("update  Foo set a = a.b.toLowerCase(), b=out('pippo')[0]");
    printTree("update  Foo set a = a.b.toLowerCase(), b=out('pippo')[0]");
  }

  public void testCollections() {
    checkRightSyntax("update Foo add a = b");
    checkWrongSyntax("update Foo add 'a' = b");
    checkRightSyntax("update Foo add a = 'a'");
    checkWrongSyntax("update Foo put a = b");
    checkRightSyntax("update Foo put a = b, c");
    checkRightSyntax("update Foo put a = 'b', 1.34");
    checkRightSyntax("update Foo put a = 'b', 'c'");
  }

  public void testJson() {
    checkRightSyntax("update Foo merge {'a':'b', 'c':{'d':'e'}} where name = 'foo'");
    checkRightSyntax("update Foo content {'a':'b', 'c':{'d':'e', 'f': ['a', 'b', 4]}} where name = 'foo'");
  }

  public void testIncrementOld() {
    checkRightSyntax("update  Foo increment a = 2");
  }

  public void testIncrement() {
    checkRightSyntax("update  Foo set a += 2");
    printTree("update  Foo set a += 2");
  }

  public void testDecrement() {
    checkRightSyntax("update  Foo set a -= 2");
  }

  public void testQuotedJson() {
    checkRightSyntax("UPDATE V SET key = \"test\", value = {\"f12\":\"test\\\\\"} UPSERT WHERE key = \"test\"");
  }

  public void testTargetQuery() {
    //issue #4415
    checkRightSyntax("update (select from (traverse References from ( select from Node WHERE Email = 'julia@local'  ) ) WHERE @class = 'Node' and $depth <= 1 and Active = true ) set Points = 0 RETURN BEFORE $current.Points");
  }

  public void testTargetMultipleRids() {
    checkRightSyntax("update [#9:0, #9:1] set foo = 'bar'");
  }

  public void testDottedTarget() {
    //issue #5397
    checkRightSyntax("update $publishedVersionEdge.row set isPublished = false");
  }

  @Test
  public void testLockRecord() {
    checkRightSyntax("update foo set bar = 1 lock record");
    checkRightSyntax("update foo set bar = 1 lock none");
    checkRightSyntax("update foo set bar = 1 lock shared");
    checkRightSyntax("update foo set bar = 1 lock default");
    checkRightSyntax("update foo set bar = 1 LOCK RECORD");
    checkRightSyntax("update foo set bar = 1 LOCK NONE");
    checkRightSyntax("update foo set bar = 1 LOCK SHARED");
    checkRightSyntax("update foo set bar = 1 LOCK DEFAULT");

    checkWrongSyntax("update foo set bar = 1 LOCK Foo");
  }

  @Test
  public void testReturnCount() {
    checkRightSyntax("update foo set bar = 1 RETURN COUNT");
    checkRightSyntax("update foo set bar = 1 return count");
  }

  @Test
  public void testRemove() {
    checkRightSyntax("update foo remove a");
    checkRightSyntax("update foo remove a = 12");
    checkRightSyntax("update foo remove a.b.c = a.b.c[0]");

  }

  @Test
  public void testLet() {
    checkRightSyntax("update foo set a = $a let $a = 2");
    checkRightSyntax("update foo set a = $a let $a = 2 where foo = 12");
  }

  private void printTree(String s) {
    OrientSql osql = getParserFor(s);
    try {
      SimpleNode result = osql.parse();


    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  protected OrientSql getParserFor(String string) {
    InputStream is = new ByteArrayInputStream(string.getBytes());
    OrientSql osql = new OrientSql(is);
    return osql;
  }
}
