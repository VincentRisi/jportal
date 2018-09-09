import fileinput

class Job:
  def __init__(self):
    self.keywords = {}
    self.pairs = {}
  def build(self):
    pair = [self.title, self.filename]
    pkey = self.key+':'+pair[0]+','+pair[1]
    if self.pairs.has_key(pkey):
      print '<!--', pkey, 'duplicate -->'
      self.pairs[pkey] = self.pairs[pkey] + 1
    else:
      self.pairs[pkey] = 1
      if self.keywords.has_key(self.key):
        self.keywords[self.key].append(pair)
      else:
        self.keywords[self.key] = [pair]
  def strip(self, n, line):
    new = line[n:]
    m = new.find('"')
    self.key = (new[0:m])
    self.build()
    self.process(new[m])
  def process(self, line):
    n = line.find('<a name="')
    if n != -1:
      self.strip(n+9, line)
  def run(self):
    for line in fileinput.input():
      self.filename = fileinput.filename()
      st = line.find('<title>')
      if st != -1:
        et = line.find('</title>')
        self.title = line[st+7:et]
      else:
        self.process(line)
    keys = self.keywords.keys()
    keys.sort()
    for key in keys:
      print '<li><object type="text/sitemap">'
      print '  <param name="Name" value="%s">' % (key)
      for pair in self.keywords[key]:
        print '  <param name="Name" value="%s">' % (pair[0])
        print '  <param name="Local" value="%s#%s">' % (pair[1], key)
      print '</object>'

job = Job()
job.run()


