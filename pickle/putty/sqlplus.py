import glob

actions = ['ADD', 'CHANGE', 'DELETE']

def make_dict(name, action, fields, old, new):
  d = {}
  d['name'] = name
  d['action'] = action
  for i, field in enumerate(fields):
    oldvalue = old[i] if len(old) == len(fields) else ''
    newvalue = new[i] if len(new) == len(fields) else ''
    d[field+'.old'] = oldvalue
    d[field+'.new'] = newvalue
  return d

def make_class(name, action, fields, old, new):
  class Class(object): pass
  c = Class()
  c._name = name
  c._action = actions[action] if action < len(actions) else action
  for i, field in enumerate(fields):
    f = Class()
    exec("c.%s=f" % (field))
    f.old = old[i] if len(old) == len(fields) else ''
    f.new = new[i] if len(new) == len(fields) else ''
  return c

def make_xml(name, action, fields, old, new):
  xml = '<%s action="%s">' % (name, actions[action])
  for i, field in enumerate(fields):
    old_value = old[i] if len(old) == len(fields) else ''
    new_value = new[i] if len(new) == len(fields) else ''
    xml += '<%s old="%s" new="%s"/>' % (fields[i], old_value, new_value)
  xml += '</%s>' % (name)
  return xml

import email

def main():
  email.email()
  dd = make_dict('Staff', 0, ['name', 'descr', 'status'], [], ['1','2', '3'])
  print repr(dd)
  cc = make_class('Staff', 0, ['name', 'descr', 'status'], [], ['1','2', '3'])
  print cc._name, cc._action, 'name', cc.name.old, cc.name.new, 'descr', cc.descr.old, cc.descr.new, 'status', cc.status.old, cc.status.new
  xx = make_xml('Staff', 0, ['name', 'descr', 'status'], [], ['1','2', '3'])
  print xx

if __name__ == '__main__':
  main()
