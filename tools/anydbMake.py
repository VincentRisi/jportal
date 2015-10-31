#!/usr/local/bin/python
import xml.sax
import sys
import os.path
import tempfile
import stat
import glob
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-B", "--buildPath",  dest="buildPath",  default=r"c:\sars\build")
parser.add_option("-S", "--sourcePath", dest="sourcePath", default=r"c:\sars\code")
parser.add_option("-b", "--build",      dest="build",      default=False, action="store_true", help='build all anyway')
parser.add_option("-c", "--crackle",    dest="crackle",    default=r'c:\sars\tools\bbd\idl2\crackle.jar')
parser.add_option("-j", "--jportal",    dest="jportal",    default=r'c:\sars\tools\bbd\anydb\jportal.jar')
parser.add_option("-g", "--generate",   dest="generate",   default=False, action="store_true", help="generate CMake build only")
parser.add_option("-l", "--local",      dest="local",      default=r"c:\sars")
parser.add_option("-r", "--root",       dest="root",       default="")
parser.add_option("-s", "--skip",       dest="skip",       default="", help=':CSIdl2Code:... colon switch list')
parser.add_option("-v", "--verbose",    dest="verbose",    default=False, action="store_true", help="verbose")

(options, args) = parser.parse_args()

rootPath = options.root
localPath = options.local
buildPath = options.buildPath
sourcePath = options.sourcePath

def front(a,b):
  n = min(len(a), len(b))
  if n == 0: return ''
  for i in range(n):
    if a[i] != b[i]: 
      return a[:i]
  return a

if sys.platform == 'win32':
  dirsep = '\\'
else:
  dirsep = '/'
  if len(rootPath) == 0 and len(sourcePath)> 2 and sourcePath[1] != ':' and '/' in sourcePath:
    rootPath = front(sourcePath, buildPath)  

def fixname(name):
  result = name
  if sys.platform != 'win32':
    if result[:len(localPath)].lower() == localPath.lower():
      result = rootPath + result[len(localPath):]
      result = result.replace('\\','/')
      head, tail = os.path.split(result)
      result = head.lower() + '/' + tail
      if os.path.exists(result.lower()) == True:
        result = result.lower()
  return result

def makedirs(path):
  if os.path.exists(path) == False:
    os.makedirs(path)
  return path  

def log(*line):
  if (options.verbose == True):
    print '%s ' * len(line) % line

jportalJar = fixname(options.jportal);log('jportal jar:', jportalJar) 
crackleJar = fixname(options.crackle);log('crackle jar:', crackleJar)

if len(args) < 1:
  print "usage :-\> anydbMake.py [options] sourcefile"
  parser.print_help()
  exit()

sourceFile = fixname(args[0])
print 'Project file:', sourceFile

class Project(object): pass
class Switch(object): pass
class Source(object): pass
class Target(object): pass
class CMake(object): pass

def lastmod(name):
  if os.path.exists(name) == True:
    return os.stat(name)[stat.ST_MTIME]
  elif os.path.exists(name.lower()) == True:
    return os.stat(name)[stat.ST_MTIME]
  else:
    return 0

def jportal(name, switches, iiFiles):
  fd, fname = tempfile.mkstemp('.~tmp')
  os.close(fd)
  command = r'java -jar %s -l %s %s %s' %(jportalJar, fname, name, switches)
  print command
  os.system(command)
  for line in open(fname):
    line = line[:-1]
    print line 
    if line[:6] == 'Code: ':
      _, tail = os.path.split(line[6:])
      _, ext = os.path.splitext(tail)
      if ext == '.ii':
        iiFile = Source()
        iiFile.name = fixname(line[6:])
        iiFile.lastmod = lastmod(iiFile.name)
        iiFiles.append(iiFile)
  os.remove(fname)
  return iiFiles

def crackle(name, switches):
  fd, fname = tempfile.mkstemp('.~tmp')
  os.close(fd)
  command = r'java -jar %s -l %s %s %s' %(crackleJar, fname, name, switches)
  print command
  os.system(command)
  for line in open(fname):
    line = line[:-1] 
    print line
  os.remove(fname)

switches = {}
def makeSwitches(project):
  dir = ''
  skips = ':%s:' % options.skip 
  switches['crackle'] = ''
  switches['jportal'] = ''
  for switch in project.switches:
    if switch.name in ['idlModule', 'idlTarget', 'idlBusLogicPath', 
                       'cmakeBuild', 'cmakeInclude', 'cmakeSource']:
      switches[switch.name] = switch.value
      continue
    if skips.find(':%s:' % switch.name[:-9]) >= 0:
      continue 
    if switch.name[0:6] == 'PopUbi':
      switchName = "crackle"
    else:  
      switchName = "jportal"
    n = switch.name[:-9]
    b = switch.name[-9:]
    if b == 'Directory':
      dir = fixname(switch.value)
      makedirs(dir)
    elif b == 'Generator' and switch.value == "True":
      if dir == '':
        switches[switchName] += '%s ' % (n)
      else:
        switches[switchName] += '-o %s %s ' % (dir, n)
      dir = ''

def clean(project):
  global dirsep
  dirLists = {}
  dirExts = {}
  for source in project.sources:
    for target in source.targets:
      head, tail = os.path.split(target.name)
      _, ext = os.path.splitext(tail)
      if dirLists.has_key(head) == False:
        dirLists[head] = []
        dirExts[head] = []
      if not tail in dirLists[head]:
        dirLists[head].append(tail)
      if not ext in dirExts[head]:
        dirExts[head].append(ext)
  for head in dirLists.keys():
    for ext in dirExts[head]:
      for fileName in glob.glob('%s%s*%s' % (fixname(head), dirsep, ext)):
        _, ft = os.path.split(fileName)
        if not ft in dirLists[head]:
          print 'removing %s' % (fileName)
          os.remove(fileName)

def make_ib_files(pathlist):
  ibFiles = []
  parts = pathlist.split(';')
  for part in parts:
    if len(part) > 3 and part[-3:] in ['.ib', '.ic']:
      ibFiles.append(part)
      continue
    files = glob.glob('%s%s*.ib' % (fixname(part), dirsep))
    for file in files:
      ibFiles.append(file)  
  return ibFiles
            
def generate(project):
  cmake = CMake() 
  cmake.module = switches['idlModule']
  cmake.target = switches['idlTarget']
  cmake.idlBusLogicPath = switches['idlBusLogicPath'] 
  cmake.include = switches['cmakeInclude']
  cmake.source = switches['cmakeSource'] 
  cmake.build = switches['cmakeBuild']
  
  def fileparts(filename):
    head, tail = os.path.split(filename)
    name, ext = os.path.splitext(tail)
    return head, name, ext
  
  def cmake_fix(filename):
    if filename.find(cmake.source) == 0:
      result = '${CMAKE_SOURCE_DIR}%s' % (filename[len(cmake.source)-1:]).replace('\\','/')
    elif filename.find(cmake.build) == 0:
      result = '${CMAKE_BINARY_DIR}%s' % (filename[len(cmake.source)-1:]).replace('\\','/')
    else:
      result = filename.replace('\\','/')  
    return result
  
  def add_typed_targets(sql_targets, ii_files, filename):
    head, _, ext = fileparts(filename)
    parts = head.split('\\')
    key = parts[-1].upper()
    fixed_name = cmake_fix(filename)
    if not key in sql_targets: sql_targets[key] = []
    sql_targets[key].append(fixed_name)
    if ext == '.ii':
      ii_files.append(fixed_name)
      
  cmake.name = fileparts(project.name)[1].upper()
  lines = []
  sql_targets = {}
  idl_targets = {}
  ii_files = []
  lines.append('SET(%s_PROJECT_FILE' % (cmake.name))
  lines.append('  %s' % (cmake_fix(project.name)))
  lines.append('  )')
  lines.append('')
  lines.append('SET(%s_IM_FILE' % (cmake.name))
  lines.append('  %s' % (cmake_fix(cmake.module)))
  lines.append('  )')
  lines.append('')
  lines.append('SET(%s_SI_FILES' % (cmake.name))
  for source in project.sources:
    lines.append('  %s' % (cmake_fix(source.name)))
    for target in source.targets:
      add_typed_targets(sql_targets, ii_files, target.name)
  lines.append('  )')
  lines.append('')
  keys = sql_targets.keys()
  keys.sort()
  for key in keys: 
    lines.append('SET(%s_SQL_%s_OUTPUT' % (cmake.name, key))
    for filename in sql_targets[key]:
      lines.append('  %s' % (cmake_fix(filename)))
    lines.append('  )')
    lines.append('')
  lines.append('SET(%s_II_FILES' % (cmake.name))
  for file in ii_files:
    lines.append('  %s' % (file))
  lines.append('  )')
  lines.append('')
  ib_files = make_ib_files(cmake.idlBusLogicPath)
  lines.append('SET(%s_IB_FILES' % (cmake.name))
  for file in ib_files:
    lines.append('  %s' % (file))
  lines.append('  )')
  lines.append('')
  source = project.idl
  for target in source.targets:
    add_typed_targets(idl_targets, ii_files, target.name)
  keys = idl_targets.keys()
  keys.sort()
  for key in keys: 
    lines.append('SET(%s_IDL_%s_OUTPUT' % (cmake.name, key))
    for filename in idl_targets[key]:
      lines.append('  %s' % (cmake_fix(filename)))
    lines.append('  )')
    lines.append('')
  for line in lines: print line

class Handler(xml.sax.ContentHandler):
  def startElement(self, name, attrs):
    if name == 'Project':
      project = Project()
      project.name = fixname(attrs.getValue('Name'))
      project.noSwitches = attrs.getValue('Switches')
      project.noSources = attrs.getValue('Sources')
      project.switches = []
      project.sources = []
      self.project = project
    elif name == 'Switch':
      switch = Switch()
      switch.name = attrs.getValue('Name')
      switch.value = attrs.getValue('Value')
      self.project.switches.append(switch)
    elif name == 'Source':
      source = Source()
      source.targets = []
      source.noTargets = attrs.getValue('Targets')
      source.exists = attrs.getValue('Exists')
      source.name = fixname(attrs.getValue('Name'))
      if source.name == 'Idl':
        self.project.idl = source
      else:  
        source.lastmod = lastmod(source.name)
        self.project.sources.append(source)
      self.source = source
    elif name == 'Target':
      target = Target()
      target.name = fixname(attrs.getValue('Name'))
      target.lastmod = lastmod(target.name)
      self.source.targets.append(target)

a = xml.sax.make_parser()
handler = Handler()
a.setContentHandler(handler)
a.parse(sourceFile)
project = handler.project
makeSwitches(project)
if options.generate == True:
  generate(project)
else:
  clean(project)
  projmod = lastmod(sourceFile)
  jportalJarMod = lastmod(jportalJar)
  crackleJarMod = lastmod(crackleJar)
  sourceList = []
  reasons = {}
  iiFiles = []
  ibFiles = [] 
  for source in project.sources:
    if source.noTargets == 0:
      reasons[source.name] = 'no targets'
      log('%s %s' % (source.name, reasons[source.name]))
      sourceList.append(source.name)
      continue
    compile=False  
    for target in source.targets:
      if source.lastmod > target.lastmod:
        reasons[source.name] = 'source newer than target %s' % (target.name)
        compile=True
        break
      if jportalJarMod > target.lastmod:
        reasons[source.name] = 'jar newer than target %s' % (target.name)
        compile=True
        break
    if compile == True:
      log('%s %s' % (source.name, reasons[source.name]))
      sourceList.append(source.name)
      for target in source.targets:
        if os.path.exists(target.name) == True:
          print 'removing %s' % (target.name)
          os.remove(target.name)
    else:
      log(source.name, 'up to date')
      for target in  source.targets:
        head, tail = os.path.split(target.name)
        root, ext = os.path.splitext(tail)
        if ext == '.ii':
          iiFile = Source()
          iiFile.name = fixname(target.name)
          iiFile.lastmod = lastmod(iiFile.name)
          iiFiles.append(iiFile)
      
  if len(sourceList) > 0:
    fd, fname = tempfile.mkstemp('.~tmp')
    for source in sourceList:
      if len(reasons[source]) > 0:
        print '%s --- %s' % (source, reasons[source])
      os.write(fd, '%s\n' % (source))
    os.close(fd)
    iiFiles = jportal('-f %s' % (fname), switches['jportal'], iiFiles)
    os.remove(fname)
  
  if 'idlModule' in switches and 'idlTarget' in switches:
    compile = False
    idlTarget = Source()
    idlTarget.name = fixname(switches['idlTarget'])
    idlTarget.lastmod = lastmod(idlTarget.name)
    idlModule = Source()
    idlModule.name = fixname(switches['idlModule'])
    idlModule.lastmod = lastmod(idlModule.name)
    if idlModule.lastmod > idlTarget.lastmod:
      compile = True
    if crackleJarMod > idlTarget.lastmod:
      compile = True
    if 'idlBusLogicPath' in switches:
      ibFileNames = make_ib_files(switches['idlBusLogicPath'])
      for name in ibFileNames:
        ibFile = Source()
        ibFile.name = fixname(name)
        ibFile.lastmod = lastmod(ibFile.name)
        ibFiles.append(ibFile)
        if ibFile.lastmod > idlTarget.lastmod:
          compile = True
    for iiFile in iiFiles:
      if iiFile.lastmod > idlTarget.lastmod:
        compile = True
        
  if compile == True:
    outfile = open(idlTarget.name, 'wt')
    ifile = open(idlModule.name, 'rt')
    outfile.write(ifile.read())
    ifile.close() 
    for iiFile in iiFiles:
      outfile.write('// *** %s\n' % (iiFile.name))
      ifile = open(iiFile.name, 'rt')
      outfile.write(ifile.read())
      ifile.close()
    for ibFile in ibFiles:
      outfile.write('// *** %s\n' % (ibFile.name))
      ifile = open(ibFile.name, 'rt')
      outfile.write(ifile.read())
      ifile.close()
    outfile.close()
    crackle(idlTarget.name, switches['crackle'])
