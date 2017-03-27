package bbd.binu.parser;

import java.sql.PreparedStatement;
import org.apache.log4j.Logger;
import bbd.binu.BinuStructs.MultipleOnlyReturn;
import bbd.binu.BinuStructs.MultipleReturn;
import bbd.binu.BinuStructs.SingleOnlyReturn;
import bbd.binu.BinuStructs.SingleReturn;
import bbd.binu.Idl2Thread;
import bbd.jportal.util.Connector;

public class TJAction
{
  private Idl2Thread thread;
  static Logger log = Logger.getLogger("TJAction");
  private PreparedStatement prep;
  public TJAction(Idl2Thread thread)
  {
    this.thread = thread;
  }
  private TJProc compile(String queryCode)
  {
    Parser parser = new Parser(new Scanner(queryCode));
    parser.Parse();
    return parser.proc;
  }
  public void action(String queryCode, String buffer) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    byte[] rec = buffer.getBytes();
    prep = proc.setup(conn, rec);
    prep.executeUpdate();
    prep.close();
  }
  public void actionOnly(String queryCode) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    prep = proc.setup(conn);
    prep.executeUpdate();
    prep.close();
  }
  public void bulkAction(String queryCode, int noRecs, String buffer) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    byte[] recs = buffer.getBytes();
    prep = proc.setupBulk(conn, noRecs, recs);
    prep.executeBatch();
    prep.close();
  }
  public void multiple(MultipleReturn _tx, String queryCode, String buffer) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    byte[] rec = buffer.getBytes();
    prep = proc.setup(conn, rec);
    proc.runMulti();
    _tx.noRecs = proc.getNoRecs();
    _tx.outBufferSize = proc.getOutBufferSize();
    _tx.outBuffer = proc.getOutBuffer();
  }
  public void multipleOnly(MultipleOnlyReturn _tx, String queryCode) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    prep = proc.setup(conn);
    proc.runMulti();
    _tx.noRecs = proc.getNoRecs();
    _tx.outBufferSize = proc.getOutBufferSize();
    _tx.outBuffer = proc.getOutBuffer();
  }
  public void single(SingleReturn _tx, String queryCode, String buffer) throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    byte[] rec = buffer.getBytes();
    prep = proc.setup(conn, rec);
    proc.runSingle();
    _tx._rc = proc.getRc();
    _tx.outBufferSize = proc.getOutBufferSize();
    _tx.outBuffer = proc.getOutBuffer();
  }
  public void singleOnly(SingleOnlyReturn _tx, String queryCode)
      throws Exception
  {
    TJProc proc = compile(queryCode);
    Connector conn = thread.getConn(proc.database);
    prep = proc.setup(conn);
    proc.runSingle();
    _tx._rc = proc.getRc();
    _tx.outBufferSize = proc.getOutBufferSize();
    _tx.outBuffer = proc.getOutBuffer();
  }
}