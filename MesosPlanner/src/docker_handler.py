#!/usr/bin/sudo python
import docker
import sys

class Docker_handler():


    def __init__(self):
        self.client= docker.from_env()

    def start_container(self,id):
        return self.client.containers.run(id)


def main():
    handler = Docker_handler()
    out = handler.start_container(sys.argv[1])
    print(out)

main()