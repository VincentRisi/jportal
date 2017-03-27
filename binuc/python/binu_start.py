import sys, optparse 

data_dir_default   = '/main/bbd/data/binu'
pythonpath_default = '/main/bbd/bin/pyextensions'
delim = '/'

class Class: pass

parser = optparse.OptionParser()
parser.add_option('-c', '--connect',    dest='connect',    default='smdev7:ssmd00:devsm:devpassword', help='connect string')
parser.add_option('-d', '--datadir',    dest='data_dir',   default=data_dir_default,                  help='directory for data file')
parser.add_option('-i', '--procid',     dest='proc_id',    default='1',                               help='process id')
parser.add_option('-l', '--logfile',    dest='logfile',    default='logfile.log',                     help='logfile')
parser.add_option('-p', '--pythonpath', dest='pythonpath', default=pythonpath_default,                help='pythonpaths')
parser.add_option('-s', '--script',     dest='script',     default='binu_query',                      help='directory with pyd files')
(opts, args) = parser.parse_args()

inp = Class()
inp.connect  = opts.connect.strip()
inp.data_dir = opts.data_dir.strip()
inp.proc_id  = opts.proc_id.strip()
inp.delim    = delim
inp.logfile  = opts.logfile.strip()
inp.script   = opts.script.strip()
inp.pythonpaths = opts.pythonpath.strip().split(';')
print inp.connect, inp.data_dir, inp.proc_id, inp.delim, inp.logfile, inp.script, inp.pythonpaths

for path in inp.pythonpaths:
    sys.path.append(path)

from_import = 'from %s import main' % (inp.script)
print from_import
exec(from_import)

exit(main(inp, args))
