import sys, json, inspect
from types import InstanceType

def tree_tester():
    sys.path.append('/main/jportal/build/anydbtest/sql/PythonTree')
    from ZedZed import database
    def print_it(obj, pad=0):
        print ' '*pad, obj.__dict__
        for attr in obj.__dict__:
            o = obj.__dict__[attr]
            if isinstance(o, list):
                for inst in o:
                    if isinstance(inst, InstanceType):
                        print_it(inst, pad+2)
                    #else:
                        #print ' '*pad, inst     
            elif isinstance(o, InstanceType):
                print_it(o, pad+2)   
            #else:
                #print ' '*pad, attr             
            
    print_it(database)                
    #for table in database.tables:
        #print table.__dict__

def json_tester():
    file_name = '/main/jportal/build/anydbtest/sql/json/zedzed.json'
    fp = open(file_name, 'r') 
    zedzed = json.load(fp)
    print zedzed['procs'][0]['name']

tree_tester()    