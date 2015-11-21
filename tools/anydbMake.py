#!/usr/local/bin/python
import xml.sax
import sys
import os.path
import tempfile
import stat
import glob
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-B", "--buildPath",  dest="buildPath",  default="/vlab/build")
parser.add_option("-S", "--sourcePath", dest="sourcePath", default="/vlab/source")
parser.add_option("-b", "--build",      dest="build",      default=False, action="store_true", help='build all anyway')
parser.add_option("-c", "--crackle",    dest="crackle",    default='crackle.jar')
parser.add_option("-j", "--jportal",    dest="jportal",    default='jportal.jar')
parser.add_option("-l", "--local",      dest="local",      default="/vlab")
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

def lastmod(name,try_lower=True):
  if os.path.exists(name) == True:
    return os.stat(name)[stat.ST_MTIME]
  elif try_lower == True and os.path.exists(name.lower()) == True:
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
    if switch.name[0:3] == 'Pop':
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
args = {}

def remove_comment(line):
  p = line.find('#')
  if p >= 0:
    line = line[:p]
  return line

def expand(line):
  result = ''
  while True:
    s = line.find('${')
    if s < 0:
      result += line
      break
    result += line[:s]
    line = line[s+2:]
    e = line.find('}')
    if e < 0:
      result += line
      break
    arg = line[:e]
    line = line[e+1:]
    if args.has_key(arg):
      result += args[arg]
  return result

def parse_anydb(sourceFile):
  ifile = open(sourceFile, 'r')
  lines = ifile.readlines()
  ifile.close()
  project = None
  state = 0;JPORTAL=1;CRACKLE=2;SOURCE=3;IDL=4
  switches['crackle'] = ''
  switches['jportal'] = ''
  for line in lines:
    line=expand(remove_comment(line.strip()))
    if len(line) == 0:
      continue
    fields=line.split('=')
    if len(fields) == 2:
      args[fields[0]] = fields[1]
      continue
    fields=line.split()
    if fields[0] == 'project' and len(fields) > 1:
      project = Project()
      project.name = fixname(fields[1])
      project.switches = []
      project.sources = []
      project.masks = {}
      project.masks['jportal'] = {}
      project.masks['crackle'] = {}
      project.idlname = None
      project.idls = {} 
      continue
    if project == None:
      print 'expecting project name'
      return None
    if fields[0] == 'jportal':
      state = JPORTAL
      continue
    if fields[0] == 'crackle':
      state = CRACKLE
      continue
    if fields[0] == 'source':
      state = SOURCE
      continue
    if fields[0] == 'idl':
      state = IDL
      continue
    if state == JPORTAL or state == CRACKLE:
      if state == JPORTAL: 
        sname = 'jportal'  
      else:
        sname = 'crackle'
      if len(fields) > 1:
        dir = fixname(fields[1])
        makedirs(dir)
        switches[sname] += '-o %s %s ' % (dir, fields[0])
      else:
        dir = ''  
        switches[sname] += '%s ' % (fields[0])
      if len(fields) > 2:
        if not project.masks.has_key(dir):
          project.masks[sname][dir] = []
        for mask in fields[2:]:
          project.masks[sname][dir].append(mask)
      continue
    if state == SOURCE:
      source = Source()
      source.targets = []
      source.name = fixname(fields[0])
      source.noTargets = 0
      source.lastmod = lastmod(source.name)
      project.sources.append(source)
      continue
    if state == IDL:
      source = Source()
      idl_list = ('idlfile', 'imfile', 'ibfile', 'icfile')
      idl_type = fields[0]
      if (not idl_type in idl_list):
        print '%s - not a valid - not in %s' % (idl_type, repr(idl_list))
        continue
      source.name = fixname(fields[1])
      if idl_type == 'idlfile':
        switches['idlTarget'] = source.name
        continue
      if idl_type == 'imfile':
        switches['idlModule'] = source.name
        continue
      if not project.idls.has_key(idl_type):
        project.idls[idl_type] = []
      project.idls[idl_type].append(source)
  return project

def add_target(source, file):
  target = Target()
  target.name = fixname(file)
  target.lastmod = lastmod(target.name, False)
  source.targets.append(target)
  source.noTargets = len(source.targets)

def get_targets(source, name, mask, project):
  masks = project.masks['jportal'][mask]
  for wildcard in masks:
    check_it = False
    if wildcard.find('%l') >= 0:
      wildcard = wildcard.replace('%l', name.lower())
    elif wildcard.find('%u') >= 0:
      wildcard = wildcard.replace('%u', name.upper())
    elif wildcard.find('%a') >= 0:
      wildcard = wildcard.replace('%a', name)
    elif wildcard.find('%i') >= 0:
      check_it = True
      empty = wildcard.replace('%i', name.upper())
      wildcard = wildcard.replace('%i', '?'*len(name))
    files = glob.glob('%s/%s' % (mask, wildcard))
    if len(files) == 0:
      if check_it == True:
        file = '%s/%s' % (mask, empty)
      else:
        file = '%s/%s' % (mask, wildcard)
      add_target(source, file)
      continue
    for file in files:
      if check_it == True:
        _, base = os.path.split(file)
        fname, _ = os.path.splitext(base)
        if fname.upper() != name.upper():
          continue
      add_target(source, file)

def derive_targets(project):
  mask_keys = sorted(project.masks['jportal'])
  for source in project.sources:
    _, base = os.path.split(source.name)
    name, _ = os.path.splitext(base)
    for mask in mask_keys:
      get_targets(source, name, mask, project)

_, ext = os.path.splitext(sourceFile)
if ext == '.prj':
  a = xml.sax.make_parser()
  handler = Handler()
  a.setContentHandler(handler)
  a.parse(sourceFile)
  project = handler.project
  makeSwitches(project)
  clean(project)
else:
  project = parse_anydb(sourceFile)
  derive_targets(project)

projmod = lastmod(sourceFile)
jportalJarMod = lastmod(jportalJar)
crackleJarMod = lastmod(crackleJar)
sourceList = []
reasons = {}
project.idls['iifile'] = iiFiles = []
if not project.idls.has_key('ibfile'):
  project.idls['ibfile'] = []
ibFiles = project.idls['ibfile']
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
