import sys
import socket
import json
import dockerHandler
import db_handler
#
# CLASSE CHE RICEVE DAL DAEMON.java
# TODO: testare la connessione
#
# TODO: ricevere descrittori di Dockers e passarli al docker_handler
#

class dock_server():
    # dock_server si connette con il modulo java e riceve le chiamate dal deamon in questa forma:
    #
    #

    def __init__(self,address,handler):
        #address = tuple(address,port)
        self.handler=handler

        self.address=address
        self.sock = socket.socket()
        self.sock.bind(self.address)
        self.sock.listen()

    def run(self):
        print("SERVER PARTITO SU"+str(self.address))
        while True:
            conn,cli=self.sock.accept()

            try:
                ############################################################

                rec =conn.recv(1024)
                print("received")
                #self.parse_request(rec)
                self.relay_request(rec)
            finally:

                conn.close()

    def parse_request(self,raw_req):
        #OK TESTED

        obj = json.loads(raw_req)
        return obj

    def docker_request(self,dock_req):
        pass
    def vm_request(self,vm_req):
        pass

    def relay_request(self,req):
        #def start_container(self, image,command,mem,cpu):
        cl_req=self.parse_request(req)
        self.handler.handle_this(cl_req)
        # if(cl_req['type']=="DOCKERON" or cl_req['type']=="DOCKEROFF"):
        #     self.handler.start_container(cl_req[])

def main():
    online=sys.argv[1]
    config=sys.argv[2]
    print(sys.argv)
    hdn = dockerHandler.Docker_handler(config)
    if(online):
        dk_server = dock_server(address=('localhost',8082),handler=hdn)
        dk_server.run()
    else:
        db_handler.db_handler()

main()

