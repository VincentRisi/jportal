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

class Table(object):
  def __init__(self, name, fullpath):
    self.name = name
    self.fullpath = fullpath
    self.links = []
    self.froms = []

class Tables(object):
  def __init__(self, parser):
    (self.options, self.args) = parser.parse_args()
    self.tables = []
    self.builds = []
    for si_file in glob.glob(self.options.wildCard):
      _, filename = os.path.split(si_file)
      if filename.upper() in ['SIMON.SI', 'DOMAIN.SI']:
        continue
      in_file = open(si_file, 'rt')
      for line in in_file:
        words = line.lower().split()
        if len(words) < 2:
          continue
        if words[0] == 'output':
          output = line.split()[1]
          continue
        if words[0] == 'table':
          name = words[1]
          table = Table(name, filename)
          table.output = output
          self.tables.append(table)
          continue
        if words[0] == 'link':
          link_name = words[1]
          if link_name != name and not link_name in table.links:
            table.links.append(link_name)
      in_file.close()
  def move(self):
    for table in self.tables:
      if len(table.links) == 0:
        self.builds.append(table)
        self.tables.remove(table)
        for left in self.tables:
          if table.name in left.links:
            left.links.remove(table.name)
    return len(self.tables)
  def not_done(self):
    for table in self.tables:
      print '%s ** not done' % (table.name), table.links
  def build_list(self):
    buildorder = open('%s/buildorder.lst' % (self.options.buildPath), 'wt')
    for table in self.builds:
      filename = table.fullpath[:-3]
      buildorder.write("%s\n" % (filename))
    buildorder.close()
    droporder = open('%s/droporder.lst' % (self.options.buildPath), 'wt')
    no = len(self.builds)
    while no > 0:
      no -= 1
      table = self.builds[no]
      filename = table.fullpath[:-3]
      droporder.write("%s\n" % (filename))
    droporder.close()

def main():
  tables = Tables(parser)
  count = 20
  left = 0
  while count > 0:
    count -= 1
    left = tables.move()
    if left == 0:
      break
  if left != 0:
    tables.not_done()
  tables.build_list()

if __name__ == '__main__':
  main()
