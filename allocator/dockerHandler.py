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




class Docker_handler():

    def __init__(self):
        self.code_f = {"DOCKERON":self.DOCKERON,"DOCKEROFF":self.kill,"VMON":self.VMON}
        self.conts = {}
        self.client= docker.from_env()
        self.stats=self.get_stats()

    def handle_this(self,req):
        print("========================")
        self.code_f[req["type"]](**req)

    def DOCKERON(self,**kwargs):
        print("DOCKERON")
        for k in kwargs:
            print(k + " " + kwargs[k])
            self.start_container(image="alpine",command="ls",mem=kwargs['MEM'],cpu=float(kwargs['CPU']))

    def VMON(self,**kwargs):
        print("VMON")


    def start_container(self, image,command,mem,cpu):
        inted_cpu = int(cpu*1024.0)
        cont = self.client.containers.run(image,command, mem_limit=str(mem)+"m",detach=True,cpu_shares=inted_cpu)#,storage_opt={"size":"100"})
        self.conts[cont.id]=command
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

    def kill(self, id):
        pass




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

def _dummy_test():
    handler = Docker_handler()
    grabber= db_handler.db_handler("")
    images = grabber.test_populate()
    deam = deamon.deamon(images,handler)
    deam.dummy_start()

    from_planner()