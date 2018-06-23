import time
import datetime
import math
class deamon:

    def __init__(self,dockers,handler,vms=None,phy=None):
        self.dockers=sorted(dockers,key=lambda x:x[3])


        self.launcher = handler

        self.vms=vms
        self.phy=phy
        self.get_time_range()
        self.max_time=datetime.datetime.now()+datetime.timedelta(minutes=15)
        #self.start()


    def start(self):
        for d in self.dockers:
            while((d[3].second-datetime.datetime.now().second)>0):
                time.sleep(1)
            print("LAUNCHING: "+d[1])
            self.launcher.start_container(d[1],'',cpu=d[1],mem=d[2])
            time.sleep(10)
            print("-------> KILL ME <------------")

    def dummy_start(self):
        for d in self.dockers:
            print("-/|/|/|/|-")
            while((d[3].second-datetime.datetime.now().second)>0):
                time.sleep(1)
                print("STILL :"+str(d[3].second-datetime.datetime.now().second))

            print(d[3])
            print("LAUNCHING")
            self.launcher.start_container('alpine:latest','sleep 60',mem=d[2],cpu=d[1])
            print("-------> KILL ME <------------")


    def get_time_range(self):
        print("------------------------")
        m = min ([i[3] for i in self.dockers])
        M = max([i[3] for i in self.dockers])
        self.max_time=M
        self.min_time=m


