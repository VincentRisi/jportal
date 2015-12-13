import glob, os.path
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-b", "--buildPath",  dest="buildPath",  default="/main/jportal/build/picktester")
parser.add_option("-s", "--sourcePath", dest="sourcePath", default="/main/jportal/picktester")
parser.add_option("-w", "--wildCard",   dest="wildCard",   default="/main/jportal/picktester/sql/si/*.si")
parser.add_option("-H", "--hostName",   dest="hostName",   default="localhost")
parser.add_option("-u", "--userName",   dest="userName",   default="vince")
parser.add_option("-l", "--logName",    dest="logName",    default="/main/jportal/build/picktester/logfile.txt")
parser.add_option("-d", "--dbName",     dest="dbName",     default="postgres")
(options, args) = parser.parse_args()

tableList  = {}
tableDone  = []
linksTo    = {}
linksFrom  = {}
tableLink  = {}
dropList   = []
subsetList = []
outputName = ''
tableName  = ''
linkName   = ''
skip       = False

def process(line, in_filename):
    global tableName, outputName, linkName, skip
    if skip == True:
        return
    words = line.lower().split()
    if len(words) < 2:
        return
    if words[0] == 'output':
        outputName = line.split()[1]
        return
    if words[0] == 'table':
        tableName = words[1]
        tableList[tableName] = [in_filename, outputName]
        print in_filename, outputName
    elif words[0] == 'link':
        linkName = words[1]
        if tableName != linkName:
            if linksTo.has_key(tableName) == False:
                linksTo[tableName] = []
            if linksTo[tableName].__contains__(linkName) == False:
                linksTo[tableName].append(linkName)
            if linksFrom.has_key(linkName) == False:
                linksFrom[linkName] = []
            if linksFrom[linkName].__contains__(tableName) == False:
                linksFrom[linkName].append(tableName)
            tableLink[tableName] = in_filename

opts = {}
opts['host']=options.hostName
opts['db']=options.dbName
opts['user']=options.userName
#opts['log']=options.logName
postgres = 'psql -h %(host)s -d %(db)s -U %(user)s -a -f %(sql)s -o %(log)s >> %(log)s\n'

def printOut(tableName):
    if tableDone.__contains__(tableName):
        return
    tableDone.append(tableName)
    if tableList.has_key(tableName) == True:
        listName = tableList[tableName][0][:-3].upper()
        if len(subsetList) == 0 or listName in subsetList:
            buildorder.write("%s\n" % listName)
            dropList.append(listName)
            opts['sql']='%s/sql/pgDDL/%s.sql' % (options.buildPath, tableList[tableName][1])
            opts['log']='%s/%s.log' % (options.buildPath, tableName)
            builder.write(postgres % opts)
    else:
        for fromName in linksFrom[tableName]:
            print "### error ### --- table %s does not exist for %s" % (tableName, fromName)

def descend(tableName, no):
    global linksTo, tableDone
    if linksTo.has_key(tableName) == False:
        printOut(tableName)
        return
    for linkName in linksTo[tableName]:
        if tableDone.__contains__(linkName) == False:
            if linkName != tableName and no < 10:
                descend(linkName, no+1)

if os.path.exists('subset.txt') == True:
    subsetFile = open('subset.txt', 'rt')
    for line in subsetFile:
        subsetList.append(line.strip())
    subsetFile.close()

buildorder = open('%s/buildorder.lst' % (options.buildPath), 'wt')
builder    = open('%s/builder' % (options.buildPath), 'wt')
droporder  = open('%s/droporder.lst'  % (options.buildPath), 'wt')

allsi = []
for si_file in glob.glob(options.wildCard):
    allsi.append(si_file)
for in_filename in allsi:
    head, tail = os.path.split(in_filename)
    skip = False
    infile = open(in_filename, 'rt')
    for line in infile:
        process(line, tail)
    infile.close()

for tableName in tableLink.keys():
    descend(tableName, 0)

for tableName in tableList.keys():
    printOut(tableName)

buildorder.close()
for name in subsetList:
    if name not in dropList:
        print name, 'not found'
for name in reversed(dropList):
    droporder.write('%s\n' % (name));
droporder.close()

