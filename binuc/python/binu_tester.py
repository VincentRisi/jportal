import sys
sys.path.append('c:/bbd/build/lib')
sys.path.append('c:/bbd/build/binu/python')

from binu import __init__ as Logon, Commit, Rollback

from binu_zedzed import ZedZed_Insert, ZedZed, ZedZed_LastOne, ZedZed_Stats, ZedZed_DeleteAll

Logon('smdev7:ssmd00:devsm:devpassword')

def insert():
  rc, rec = ZedZed_Insert(
    ay=1.0,
    bee='2.0',
    cee='3.0',
    toppers=4.0,
    dee=5L,
    epic=6,
    efyou=7,
    gee=8,
    dOB=None,
    aitch='nein',
    spatch='attention',
    snitch='elven baskets',
    tmStamp='19010101000000')
  print rc
  rec._display()
  Commit()
  rec.ay = 3.456
  rec.Update()
  Commit()
  rc, rec = rec.SelectOne()
  print rc
  rec._display()

def bulk_insert():
  recs = []
  for i in range(1000):
    rec = ZedZed(
    ay=1.0+i,
    bee='2.0',
    cee='3.0',
    toppers=4.0,
    dee=5L+i,
    epic=6+i,
    efyou=7+i,
    gee=8+i,
    dOB=None,
    aitch='nein - %d' % (i),
    spatch='attention - %d' % (i),
    snitch='elven baskets - %d' % (i),
    tmStamp='19010101000000')
    recs.append(rec)
  rec = ZedZed()
  rec.BulkInsert(recs)
  Commit()

def single_only():
  rec = ZedZed()
  rec._display()
  rc, rec = rec.LastOne()
  rec._display()

def multiple_only():
  rec = ZedZed()
  no_recs, recs = rec.MOD117()
  print no_recs
  recs[-1]._display()

def multiple():
  rec = ZedZed(xavier=147)
  no_recs, recs = rec.MODGiven()
  print no_recs
  if no_recs > 0:
    recs[-1]._display()

def stats():
  rc, rec = ZedZed_Stats()
  rec._display()

def main():
  ZedZed_DeleteAll()
  insert()
  bulk_insert()
  single_only()
  multiple_only()
  multiple()
  stats()

main()
