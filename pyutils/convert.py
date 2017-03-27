import glob, os.path, os
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-b", "--buildPath",  dest="buildPath",  default="/main/jportal/picktester/sql")
parser.add_option("-s", "--sourcePath", dest="sourcePath", default="/main/jportal/picktester")
parser.add_option("-p", "--parmCheck",  dest="parmCheck",  default=False, action="store_true")
(options, args) = parser.parse_args()

top = '''\
DATABASE putty FLAGS 'user=USId(16)' 'when=TmStamp'
PACKAGE  bbd
OUTPUT   %s
SERVER   npu
SCHEMA   bbd
'''

for in_filename in sorted(glob.glob('%s/si/*.si' % (options.sourcePath))):
  print in_filename
  infile = open(in_filename, 'r')
  lines = infile.readlines()
  infile.close()
  path, basename = os.path.split(in_filename)
  out_filename = '%s/si/%s' % (options.buildPath, basename)
  if os.path.exists(out_filename):
    continue
  state = START = 1;TABLE = 2;SQLCODE = 3
  parm = False
  lookup = None
  nodomain = None
  descr = None
  show = None
  ofile = None
  if options.parmCheck == False:
    parm = True
    ofile = open(out_filename, 'w')
  has_option = False
  for line in lines:
    line = line.replace('#', '//').replace('\r','')
    fields = line.strip().split()
    if len(fields) == 0:
      if state != START:
        ofile.write(line)
      continue
    kw = fields[0].upper()
    if parm == False and kw == '$PARMS':
      parm = True
      ofile = open(out_filename, 'w')
      continue
    if kw == 'TABLE':
      if parm == False:
        break
      state = TABLE
      ofile.write(top % (fields[1]))
      ofile.write('\n%s' % line)
      if has_option == True:
        ofile.write('    OPTIONS')
        if lookup != None:
          ofile.write(' "lookup=%s"' % (lookup.replace('"',"'")))
        if nodomain != None:
          ofile.write(' "nodomain"')
        if descr != None:
          ofile.write(' "descr=%s"' % (descr.replace('"',"'")))
        if show != None:
          ofile.write(' "show=%s"' % (show.replace('"',"'")))
        ofile.write('\n')  
      continue
    if kw in ('SQL', 'SQLCODE', 'SQLDATA'):
      state = SQLCODE
      ofile.write(line.lower().replace('sql ', 'sql'))  
      continue
    if kw == 'DATA':
      state = SQLCODE
      ofile.write(line.lower().replace('data', 'sqldata'))  
      continue
    if kw in ('END', 'ENDCODE', 'ENDDATA'):
      state = TABLE
      ofile.write(line.lower().replace('end ', 'end'))  
      continue
    if state == START:
      if kw == '$LOOKUP':
        lookup = line[7:].strip()
        has_option = True
        continue
      if kw == '$NODOMAIN':
        nodomain = line[10:].strip()
        has_option = True
        continue        
      if kw == '$DESCR':
        descr = line[6:].strip()
        has_option = True
        continue                
      if kw == '$SHOW':
        show = line[5:].strip()
        has_option = True
        continue     
    if state == SQLCODE:
      ofile.write(line)
      continue
    if state == TABLE:
      p = line.upper().find('TINYINT')  
      if p > 0:
        line = '%sbyte%s' % (line[:p], line[p+7:])
      p = line.upper().find('SMALLINT')  
      if p > 0:
        line = '%sshort%s' % (line[:p], line[p+8:])
      p = line.upper().find('$PYTHON')  
      if p > 0:
        line = '%s%s' % (line[:p], line[p+7:])
      p = line.upper().find('(NOIDL)')  
      if p > 0:
        line = '%s%s' % (line[:p], line[p+7:])
      p = line.upper().find('(MULTIPLE)')  
      if p > 0:
        line = '%s%s' % (line[:p], line[p+10:])
      p = line.upper().find('DELETE CASCADE')  
      if p >= 0:
        ofile.write('// %s' % (line))
        continue
      p = line.upper().find('TABLESPACE')  
      if p >= 0:
        ofile.write('// %s' % (line))
        continue
      ofile.write(line)  
  if parm == True:    
    ofile.close()
  #break
