#!/usr/local/bin/python
import xml.sax
import sys
import os
import os.path
import stat
import glob

from optparse import OptionParser

parser = OptionParser()
parser.add_option("-r", "--root",  dest="root", default="$BASEDIR/projects/example/", help="help for root")
parser.add_option("-l", "--local", dest="local", default=r"c:\sars")
parser.add_option("-L", "--log",   dest="logFile", default="idl2Make.log")
parser.add_option("-j", "--jar",   dest="jarFile", default=r"c:\sars\code\bin\generators\crackle.jar")
parser.add_option("-p", "--pre",   dest="preproc", default="", help="example .x.y. with //$if .x. .. //$endif")

(options, args) = parser.parse_args()

rootPath = options.root
localPath = options.local
preproc = options.preproc
if len(preproc) > 0: preproc = "-p%s" % (preproc)

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

jarFile = fixname(options.jarFile)

if len(args) < 1:
   print "usage :-\> idl2Make.py [-j jarfile -p preproc -r rootPath -l localPath -L logFile] sourcefile"
   parser.print_help()
   exit()

logFile = options.logFile
if len(logFile) > 0:
  logFile = "-l %s" % (fixname(options.logFile))
  
sourceFile = fixname(args[0])

class Project(object): pass
class Switch(object): pass
class Source(object): pass
class Target(object): pass

def lastmod(name):
    if os.path.exists(name) == True:
        return os.stat(name)[stat.ST_MTIME]
    else:
        return 0

def crackle(name, switches):
    def listout(lines):
        for line in lines:
            print line[:-1]
    command = r'java -jar %s %s %s %s %s' %(jarFile, preproc, logFile, name, switches)
    print command
    os.system(command)

def makeSwitches(project):
    for source in project.sources:
        head, name = os.path.split(source.name)
        source.switches = ''
        d = ''
        for switch in project.switches:
            if switch.name.find(name) == 0:
                rest = switch.name[len(name)+1:]
                n = rest[:-9]
                b = rest[-9:]
                if b == 'Directory':
                    d = fixname(switch.value)
                elif b == 'Generator' and switch.value == "True":
                    if d == '':
                        source.switches += '%s ' % (n)
                    else:
                        source.switches += '-o %s %s ' % (d, n)
                        d = ''

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
            source.name = fixname(attrs.getValue('Name'))
            source.lastmod = lastmod(source.name)
            source.noTargets = attrs.getValue('Targets')
            source.exists = attrs.getValue('Exists')
            source.targets = []
            source.switches = ''
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
jarmod = lastmod(jarFile)
for source in project.sources:
    print source.name
    if len(source.switches) == 0:
        continue
    if int(source.noTargets) == 0:
        crackle(source.name, source.switches)
        continue
    tn = 0
    for target in source.targets:
        if tn < 5:
           tn += 1
           print ":=", target.name
        if source.lastmod > target.lastmod or jarmod > target.lastmod:
            crackle(source.name, source.switches)
            break

