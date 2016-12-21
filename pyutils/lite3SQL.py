import sqlite3
import optparse

def get_args():
  parser = optparse.OptionParser()
  parser.add_option("-d", "--db", dest="db_file", default="a.db_file", help='lite3 db file')
  parser.add_option("-v", "--verbose", dest="verbose", default=False, help="verbose", action="store_true")
  parser.description = '''\
  usage: python lite3SQL.py [-v] [-d dbFile] [sqlFile ...]
         dbFile   - the path of the dbFile for processing the sqlFiles against 
         sqlFiles - extra arguments for SCRIPTS again just the name of the scriptnode
    '''
  return parser.parse_args()

def execute(conn, cursor, buff):
  buff = buff.replace(';', '').strip()
  if buff.lower() == 'commit':
    conn.commit()
  else:
    print '>> %s' % buff
    cursor.execute(buff)
    if cursor.rowcount == -1:
      for row in cursor:
        print row
    else:
      print 'rows affected %d' % (cursor.rowcount)
    

def main():
  options, args = get_args()
  conn = sqlite3.connect(options.db_file)
  cursor = conn.cursor()
  for fileName in args:
    print 'Running script %s' % (fileName)
    sql = open(fileName, 'rt')
    buff = ''
    for line in sql:
      if len(line.strip()) == 0:
        continue
      buff += line
      if ';' in buff:
        execute(conn, cursor, buff)
        buff = ''
    if len(buff) > 0:
      execute(conn, cursor, buff)
    sql.close()
  cursor.close()
  conn.close()

if __name__ == '__main__':
  exit(main())
  
