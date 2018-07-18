import docker
import sys
import db_handler
import deamon
#import dock_server
#TODO: eseguire come thread?
#TODO: lanciare con le opzioni di CPU;MEM;DSK
#CPU NON FUNZIONA
#DSK NON FUNZIONA
#MEM FUNZIONA
#TODO get stats a modo
#deve ricevere mem,cpu,dsk,come?  -> da DB ; DB FATTO
#NOOOOOOOOO ricevere da deamon
#NOOOOOOOOO il deamon viene spostato qui



#DONE leggere dal file config associazioni MACCHINA:PORTA




class Docker_handler():

    def __init__(self,conf):
        self.config=conf
        self.code_f = {"DOCKERON":self.DOCKERON,"DOCKEROFF":self.kill,"VMON":self.VMON}
        self.conts = {}

        self.client= docker.from_env()
        self.stats=self.get_stats()

    def handle_this(self,req):
        print("========================")
        self.code_f[req["type"]](**req)
        print("========================")

    def DOCKERON(self,**kwargs):
        print("DOCKERON")
        for k in kwargs:
            print(k + " " + kwargs[k])
        self.start_container(image=kwargs['name'],command=kwargs['command'],mem=kwargs['MEM'],cpu=float(kwargs['CPU']),time=kwargs['startTime'])

    def VMON(self,**kwargs):
        print("VMON")


    def start_container(self, image,command,mem,cpu,time):
        inted_cpu = int(cpu*1024.0)
        cont = self.client.containers.run(image,command, mem_limit=str(mem)+"m",detach=True,cpu_shares=inted_cpu)#,storage_opt={"size":"100"})
        self.conts[cont.id]=image+command+str(time)
        return cont

    def get_container(self,id):
        return self.client.containers.get(id)

    def get_stats(self):
        print(self.conts)
        to_ret={}
        for i in self.client.containers.list():
            data=i.stats(decode=True,stream=False)

            to_ret[data['id']]=[data['cpu_stats'],data['memory_stats']]

        return to_ret

    def kill(self, **kwarg):
        print("DOCKEROFF")
        #self.client.containers.get(kwarg['name']+kwarg['command']+str(kwarg['startTime'])).kill()
        pass

    def read_machines_mockup(self):
        self.machines={}
        self.machines['localhost']=["localhost","8082"] #formato ADDRESS:PORT
        self.machines['second']=["192.168.1.12","8080"]

    def read_machines_from_config(self):
        f = open(self.config,'r').readlines()
        d={}
        for l in f:
            if(l.startswith("machines")):
                dt = l.split("=")[1].split(",")
                for m in dt:
                    k,v=m.split(":")
                    d[k]=v
                    print("------")
                    print(k)
                    print(d[k])
        self.machines=d

def main():
    handler = Docker_handler()
    grabber = db_handler.db_handler("")
    images=grabber.populate_from_dummy_db()


    commands=['sleep 30','echo $PATH']
    for c in images:
        out = handler.start_container(image=c,command="sleep 30",mem=4)
        print(out.id)
        print("-----------------")
    for k in handler.conts:
        cnt=handler.get_container(k)
        cnt.exec_run(commands[1])

def from_planner():
    PORT = int(sys.argv[1])
    handler=Docker_handler()

    server=dock_server.dock_server(("localhost",PORT),handler=handler)
    server.run()


def full_test():
    handler = Docker_handler()
    grabber= db_handler.db_handler("")
    images = grabber.populate_from_db()
    deam = deamon.deamon(images,handler)
    deam.start()

def mach_test():
    d = Docker_handler("mesos/MesosPlanner/config.properties")
    d.read_machines_from_config()

def _dummy_test():
    handler = Docker_handler()
    grabber= db_handler.db_handler("")
    images = grabber.test_populate()
    deam = deamon.deamon(images,handler)
    deam.dummy_start()

    from_planner()

