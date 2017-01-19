import sqlite3
import optparse

def get_args():
  parser = optparse.OptionParser()
  parser.add_option("-d", "--db", dest="db_file", default="a.db_file", help='lite3 db file')
  parser.add_option("-f", "--files", dest="files", default="", help='lite3 file list')
  parser.add_option("-v", "--verbose", dest="verbose", default=False, help="verbose", action="store_true")
  parser.description = '''\
  usage: python lite3SQL.py [-v] [-d dbFile] [-f files] [sqlFile ...]
         dbFile   - the path of the dbFile for processing the sqlFiles against 
         sqlFile  - extra arguments for SCRIPTS again just the name of the scriptnode
    '''
  return parser.parse_args()

def execute(conn, cursor, buff):
  global result
  buff = buff.replace(';', '').strip()
  if buff.lower() == 'commit':
    conn.commit()
  else:
    print '>> %s' % buff
    try:
      cursor.execute(buff)
      if cursor.rowcount == -1:
        for row in cursor:
          print row
      else:
        print 'rows affected %d' % (cursor.rowcount)
    except Exception as ex:
      result = 1
      print type(ex)       
      print ex

result = 0

def main():
  global result
  options, args = get_args()
  if len(options.files) > 0:
    files = open(options.files, 'rt')
    for line in files:
      if len(line.strip()) == 0:
        continue
      args.append(line.strip())
    print args  
  files.close()
  try:
    conn = sqlite3.connect(options.db_file)
  except Exception as ex:
    print type(ex)       
    print ex
    result = 1
    return result
  cursor = conn.cursor()
  for fileName in args:
    print 'Running script %s' % (fileName)
    sql = open(fileName, 'rt')
    buff = ''
    for line in sql:
      if len(line.strip()) == 0:
        continue
      if line.upper().find('START') == 0:
        continue
      buff += line
      if line.strip()[-1] == ';':
        execute(conn, cursor, buff)
        buff = ''
    if len(buff) > 0:
      execute(conn, cursor, buff)
    conn.commit()
    sql.close()
  cursor.close()
  conn.close()

if __name__ == '__main__':
  try:
    main()
  except Exception as ex:
    print type(ex)       
    print ex
    result = 1
  exit(result)
  
