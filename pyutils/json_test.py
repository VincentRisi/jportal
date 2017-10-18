import sys, json, inspect
from types import InstanceType

def tree_tester():
    sys.path.append('/main/jportal/build/anydbtest/sql/PythonTree')
    from ZedZed import database
    def fill_it(o, pad=0):
        d = o.__dict__
        for k in d:
            v = d[k]
            if isinstance(v, list):
                for n, i in enumerate(v):
                    if isinstance(i, InstanceType):
                        v[n] = fill_it(i, pad+2)
                    elif not isinstance(v[n], str):    
                        v[n] = repr(i)
            elif isinstance(v, InstanceType):
                d[k] = fill_it(v, pad+2)   
            elif not isinstance(v, str):    
                d[k] = repr(v)
        return d             
    dd = repr(fill_it(database)).replace("'", '"')
    print dd             

def json_tester():
    file_name = '/main/jportal/build/anydbtest/sql/json/zedzed.json'
    fp = open(file_name, 'r') 
    zedzed = json.load(fp)
    print zedzed['procs'][0]['name']

tree_tester()    