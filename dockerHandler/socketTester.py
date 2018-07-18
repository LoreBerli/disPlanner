import socket
import sys

ADDR = ('localhost',10001)

class server:
    def __init__(self):
        sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        sock.bind(ADDR)
        print("server listening on "+str(ADDR))
        sock.listen()
        while True:
            print(".")
            conn,client = sock.accept()
            try:
                print("connected")
            finally:
                conn.close()

class client:
    def __init__(self):
        sk = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        sk.connect(ADDR)
