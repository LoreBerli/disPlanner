from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont
import csv
import datetime
import os
import time
import sys


# parsa il singolo file csv
def open_csv(f_path):
    data = []
    with open(f_path, 'r') as fil:
        print(f_path)
        read = csv.reader(fil, delimiter=",")
        for r in read:
            dt = datetime.datetime.strptime(r[0][1:-4], "%Y-%m-%dT%H:%M:%S")
            load = float(r[1])
            card = int(r[2])
            cpu = float(r[3])
            ram = float(r[4])
            dsk = float(r[5])
            data.append([dt, load, card, cpu, ram, dsk])
    return data


def openSchedule(path):
    data = []
    with open(path, 'r') as fil:
        clean = fil.read().replace("[", "").replace("]", "").replace(", ", ",")
        clean = clean.split(",")
        for c in clean:
            c = c.split(" ")
            data.append(c[0])
    return data


def findDependencies(mechSc):
    depe = []
    deps={}

    print(len(mechSc))
    for machine in mechSc:
        for other in mechSc:
            if machine[0] in other[1]:
                depe.append([machine[0], other[0]])
    for d in depe:
        if deps.get(d[1]):
            deps[d[1]].append(d[0])
        else:
            deps[d[1]]=[d[0]]
    return deps


# ???
# def drawLoad(loads, name, min_time=None):
#     # non usato
#
#     if (len(loads) < 1):
#         return
#     im = Image.new("RGB", (1200, 400), color=(255, 255, 255))
#     drw = ImageDraw.ImageDraw(im)
#     # min_time=loads[0][0]
#
#     for r in loads:
#         diff = r[0] - min_time
#
#         drw.line((diff.seconds / 100, 400, diff.seconds / 100, 400 - int(r[1] * 400)), (255, 0, 0), width=2)
#     im.save(name + ".png")

# metodo usato al momento
def drawLoads(names, loads, name, min_time=None, max_time=None, scal=5):
    if (len(loads) < 1):
        return
    fnt = ImageFont.truetype('Pillow/Tests/fonts/FreeMono.ttf', 80)
    scale = scal
    top = 550
    wdt = int((max_time - min_time).total_seconds() / 2)
    im = Image.new("RGB", (wdt, top * len(loads)), color=(200, 200, 200))
    drw = ImageDraw.ImageDraw(im)

    for i, l in enumerate(loads):
        print(l[0])
        if (i % 2 == 0):
            drw.rectangle((0, (i + 1) * top, top * len(loads), (i + 2) * top), fill=(220, 220, 220))
        drw.text((0, (i) * top), names[i], font=fnt, fill=(0, 0, 255))
        for r in l:
            diff = r[0] - min_time

            drw.line(
                (30 + diff.seconds / scale, (i + 1) * top, 30 + diff.seconds / scale,
                 ((i + 1) * top) - int(4 + r[1] * 400)),
                (int(255 * r[3]), int(255 * r[4]), int(255 * r[5])), width=3)

            # drw.line((diff.seconds/scale,(i+1)*top,diff.seconds/scale,((i+1)*top)-int(4+r[1]*400)),(10*r[2],2*r[2],2*r[2]),width=3)
    for k in range(0, wdt, 100):
        drw.line((k, 0, k, top * len(loads)), fill=(0, 128 + 128 * (k % 600), 2))

    print("saving to :" + name)
    im.save(name + ".png")
    im.close()


def main():
    dataDir = "/home/cioni/mesos/MesosPlanner/loadsLog/"  # cartella da cui recupero i .csv
    machDir = "/home/cioni/mesos/MesosPlanner/scheduleLogs/"

    name = "prova"
    if len(sys.argv) < 2:
        print("usage: python vizu.py [name] [scale]")
    else:
        name = sys.argv[1]
        scale = int(sys.argv[2])

    machs = [o for o in os.listdir(machDir)]
    scheds = [openSchedule(machDir + m) for m in machs]

    machSched = list(zip(machs, scheds))

    depe=findDependencies(machSched)
    ordRec=[]

    for i in list(depe.keys()):
        ordRec.append(i)
        print("----" + str(i))
        for j in depe.get(i):
            ordRec.append(j)

    print(ordRec)
    receviers = [i for i in os.listdir(dataDir)]

    loads = []

    for rec in ordRec:
        loads.append(open_csv(dataDir + rec+"Loads"))

    # questi servono per decidere come scalare il grafico
    min_time = min([l[0] for l in loads if len(l) > 0])[0]  # minimo t assoluto
    max_time = max([l[0] for l in loads if len(l) > 0])[0]  # massimo t assoluto

    print("il t minimo è " + str(min_time))
    print("il t massimo è " + str(max_time))

    for l in loads:
        if (len(l) == 0):
            l.append([min_time, 0.0, 0, 0.0, 0.0, 0.0])

    drawLoads(ordRec, loads, "/home/cioni/PycharmProjects/visualizer/out/" + name, min_time, max_time, scale)
    return


main()
