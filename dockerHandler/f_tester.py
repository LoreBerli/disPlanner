def read_machines_from_config(config):
    f = open(config, 'r').readlines()
    d = {}
    for l in f:
        if (l.startswith("machines")):
            dt = l.split("=")[1].split(",")
            for m in dt:
                k, v = m.split(":")
                d[k] = v
                print("------")
                print(k)
                print(d[k])
    return d

d=read_machines_from_config("/home/cioni/mesos/MesosPlanner/config.properties")
