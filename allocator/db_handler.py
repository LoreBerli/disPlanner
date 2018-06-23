import os
import MySQLdb
import deamon
import time
import datetime
import json


# test : QRTZ_DOCKER
# schedules : examples
class db_handler():
    def __init__(self,path,host,user,password,db):
        self.path=path
        self.dockers={}
        self.procs={}
        self.machines=[]
        self.db=db
        self.table="new_sched"
        self.db =MySQLdb.connect(host,user,password,db)
        self.cur = self.db.cursor()

    def populate_from_dummy_db(self):
        self.cur.execute("SELECT * FROM example;")
        result = self.cur.fetchall()
        return result

    def populate_from_db(self):
        self.cur.execute("select ID_DOCKER,CPU_PERCENT,MEMORY_USAGE,DATE  from QRTZ_DOCKER LIMIT 10;")
        res=self.cur.fetchall()
        return res

    def test_populate(self):
        #self.cur.execute("select ID_DOCKER,CPU_PERCENT,MEMORY_USAGE,DATE  from QRTZ_DOCKER LIMIT 10;")
        self.cur.execute("select *  from example order by startTime")

        res=self.cur.fetchall()

        res= list(res)
        for i,r in enumerate(res):
            ru=list(r)
            ru[3]=datetime.datetime.now() + datetime.timedelta(seconds=10+10*i)
            print("----------------------"+str(ru[3]))
            res[i]=ru
        return res

    def show_everything(self):
        self.cur.execute("SELECT * FROM new_sched LIMIT 10;")
        return self.cur.fetchall()

    def mock_populate(self):
        for i in os.listdir(self.path):
            print(i)
            tmp_fil = open(self.path+"/"+i).read().replace("\n","").replace("[","").replace("[","").split(",")
            self.dockers[i]=tmp_fil
        print(len(self.dockers))
        return self.dockers

    def retrieve_db(self):
        self.cur.execute("SHOW COLUMNS FROM "+self.table)
        res_schema = self.cur.fetchall()
        schema=[]
        for s in res_schema:
            schema.append(s[0])

        self.cur.execute("SELECT * FROM "+self.table+" ORDER BY startTime;")
        res = self.cur.fetchall()
        for r in res:
            tmp = dict(zip(schema,r))
            print(tmp)


def test_populate_drom_db():
    db = db_handler("")
    res = db.populate_from_db()
    assert (len(res)==10)
    for i,r in enumerate(res):
        print(str(i)+" : "+str(r))

def test_boh():
    db = db_handler("","localhost","root","password","new_sched")
    res = db.retrieve_db()



#test_populate_drom_db()
test_boh()
