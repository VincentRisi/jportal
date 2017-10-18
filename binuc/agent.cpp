#include "agent.h"
#include "addlist.h"
#include "Parser.h"

#include "zedzed.h"
#include "zedzedbin.h"

bool compile(TJProc &proc, const char* code)
{
  Scanner scanner((const unsigned char *)code, strlen(code));
  Parser parser(&scanner);
  parser.proc = &proc;
  parser.Parse();
  return true;
}

TJConnector conn;

bool readone(const char* code, void *rec)
{
  TJProc proc;
  compile(proc, code);
  bool result = proc.readone(&conn, rec);
  return result;
}

void action(const char* code, void *rec)
{
  TJProc proc;
  compile(proc, code);
  proc.action(&conn, rec);
}

void bulkaction(const char* code, int no, void *recs)
{
  TJProc proc;
  compile(proc, code);
  proc.bulkaction(&conn, no, recs);
}

int multiple(const char* code, void *&recs)
{
  TJProc proc;
  compile(proc, code);
  return proc.multiple(&conn, recs);
}

int multiple(const char* code, void *rec, void *&recs)
{
  TJProc proc;
  compile(proc, code);
  return proc.multiple(&conn, rec, recs);
}

void display(ZedZed &rec, const char* title)
{
  printf (
    "%s\n"
    "Xavier:%d "         
    "Ay:%f "             
    "Bee:%s "            
    "Cee:%s "            
    "Toppers:%f "        
    "Dee:%lld "          
    "Epic:%d "           
    "Efyou:%d "          
    "Gee:%d "            
    "DOB:%s%s "            
    "Aitch:%10.10s "     
    "Spatch:%10.10s "    
    "Snitch:%10.10s "    
    "tmStamp:%s\n"
    , title       
    , rec.xavier
    , rec.ay
    , rec.bee
    , rec.cee
    , rec.toppers
    , rec.dee
    , rec.epic
    , rec.efyou
    , rec.gee
    , rec.dOB, rec.dOBIsNull == true ? "Y" : "N" 
    , rec.aitch
    , rec.spatch
    , rec.snitch
    , rec.tmStamp
    );
}

#define COPY(a, b) strncpy(a,b,sizeof(a)-1),a[sizeof(a)-1]=0

int test1()
{
  try
  {
    ZedZed rec;
    rec.ay = 12345.12;
    COPY(rec.bee, "12345.12");
    COPY(rec.cee, "12345.12");
    rec.toppers = 12345.12;
    rec.dee = 123456789;
    rec.epic= 123456789;
    rec.efyou = 12345;
    rec.gee = 123;
    COPY(rec.dOB, "20110101");
    rec.dOBIsNull = JP_NOT_NULL;
    COPY(rec.aitch, "AITCH");
    COPY(rec.spatch, "SPATCH");
    COPY(rec.snitch, "SNITCH");
    readone(ZEDZED_INSERT_BIN, &rec);
    conn.Commit();
    display(rec, "test1");
    return 0;
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int test2()
{
  try
  {
    ZedZedSimple simple;
    COPY(simple.sqlcode, "select * from ssmd00.zedzed where xavier = 12345678");
    action(ZEDZED_SIMPLE_BIN, &simple);
    return 0;
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    return 1;
  }
}

int test3()
{
  try
  {
    ZedZedUpdateField rec;
    rec.xavier = 12;
    COPY(rec.field, "epic");
    COPY(rec.value, "54321");
    action(ZEDZED_UPDATEFIELD_BIN, &rec);
    conn.Commit();
    return 0;
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int test4()
{
  struct X
  {
    int no;
    ZedZed *recs;
    X(int no)
    {
      this->no = no;
      recs = new ZedZed [no];
    }
    ~X()
    {
      delete [] recs;
    }
  };
  X x(1000);
  try
  {
    for (int i=0; i<x.no; i++)
    {
      ZedZed &rec = x.recs[i];
      rec.ay = 12345.12;
      COPY(rec.bee, "12345.12");
      COPY(rec.cee, "12345.12");
      rec.toppers = 12345.12;
      rec.dee = 123456789;
      rec.epic= 123456789;
      rec.efyou = 12345;
      rec.gee = i;
      COPY(rec.dOB, "20110101");
      rec.dOBIsNull = JP_NOT_NULL;
      COPY(rec.aitch, "AITCH");
      COPY(rec.spatch, "SPATCH");
      COPY(rec.snitch, "SNITCH");
      COPY(rec.tmStamp, "20140101120000");
    }
    bulkaction(ZEDZED_BULKINSERT_BIN, x.no, x.recs);
    conn.Commit();
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int test5()
{
  try
  {
    ZedZed rec;
    rec.xavier = 1;
    if (readone(ZEDZED_SELECTONEUPD_BIN, &rec) == true)
    {
      display(rec, "test5");
      rec.epic= 987654321;
      action(ZEDZED_UPDATE_BIN, &rec);
    }
    conn.Commit();
    return 0;
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int test6()
{
  try
  {
    ZedZed *recs=0;
    int noOf = multiple(ZEDZED_MOD117_BIN, (void*&)recs);
    for (int i=noOf-1; i>=0 && i>noOf-6; i--)
      display(recs[i], "test6");
    free(recs);
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int test7()
{
  try
  {
    ZedZed rec, *recs=0;
    rec.xavier = 1312;
    int noOf = multiple(ZEDZED_MODGIVEN_BIN, &rec, (void*&)recs);
    for (int i=noOf-1; i>=0 && i>noOf-6; i--)
      display(recs[i], "test7");
    free(recs);
  }
  catch (xCept x)
  {
    printf("%s\n", x.ErrorStr());
    conn.Rollback();
    return 1;
  }
}

int main(int argc, char *argv[])
{
  conn.Logon("devsm","devpassword","smdev7","ssmd00");
  for (int i=0; i<5; i++)
    test1();
  test2();
  test3();
  test4();
  test5();
  test6();
  test7();
  return 0;
}
