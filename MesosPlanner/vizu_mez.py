from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont
import csv
import datetime
import os
import time
import sys

def open_csv(f_path):
    data=[]
    with open(f_path,'r') as fil:
        print(f_path)
        read = csv.reader(fil,delimiter=",")
        for r in read:

            dt = datetime.datetime.strptime(r[0][1:-4],"%Y-%m-%dT%H:%M:%S")
            load = float(r[1])
            card = int(r[2])
            cpu=float(r[3])
            ram=float(r[4])
            dsk=float(r[5])
            data.append([dt,load,card,cpu,ram,dsk])
    return data

def drawLoad(loads,name,min_time=None):
    if(len(loads)<1):
        return
    im = Image.new("RGB",(1200,400),color=(255,255,255))
    drw=ImageDraw.ImageDraw(im)
    #min_time=loads[0][0]


    for r in loads:

        diff = r[0]-min_time

        drw.line((diff.seconds/100,400,diff.seconds/100,400-int(r[1]*400)),(255,0,0),width=2)
    im.save(name+".png")

def drawLoads(names,loads,name,min_time=None):
    if(len(loads)<1):
        return
    fnt = ImageFont.truetype('Pillow/Tests/fonts/FreeMono.ttf', 80)
    scale=2
    top=550
    im = Image.new("RGB",(5200,top*len(loads)),color=(200,200,200))
    drw=ImageDraw.ImageDraw(im)
    print(len(loads))
    #min_time=loads[0][0]

    for i,l in enumerate(loads):
        if(i%2==0):
            drw.rectangle((0,(i+1)*top,top*len(loads),(i+2)*top),fill=(220,220,220))
        drw.text((0,(i)*top),names[i],font=fnt,fill=(0,0,255))
        for r in l:
            if(names[i]=="phy2Loads.txt"):
                print(r)
            diff = r[0]-min_time
            

            drw.line(
                    (30+diff.seconds / scale, (i + 1) * top, 30 +diff.seconds / scale, ((i + 1) * top) - int(4 + r[1] * 400)),
                    (int(255*r[3]),int(255*r[4]),int(255*r[5])), width=3)

                #drw.line((diff.seconds/scale,(i+1)*top,diff.seconds/scale,((i+1)*top)-int(4+r[1]*400)),(10*r[2],2*r[2],2*r[2]),width=3)
    for k in range(0,3200,100):
        drw.line((k, 0, k, top*len(loads)), fill=(0, 128+128*(k%600), 2))

    print("saving to :"+name)
    im.save(name+".png")
    im.close()

def draw_loads_lines(loads,name,min_time=None):
    if(len(loads)<1):
        return
    scale = 12
    top=550
    im = Image.new("RGB",(6200,top*len(loads)),color=(210,210,210))
    drw=ImageDraw.ImageDraw(im)
    #min_time=loads[0][0]
    offset=-600
    print(len(loads))
    for k in loads:
        print(len(k))

    for i,l in enumerate(loads):
        
        last=(i+1)*top
        lastX=(l[0][0]-min_time).seconds/scale

        for r in l:
            #print(r)
            diff =offset+ r[0]-min_time

            drw.line((lastX,last,diff.seconds/scale,((i+1)*top)-int(r[1]*400)),(r[2]*80,40*r[2],30*r[2]),width=6)
            last=((i+1)*top)-int(r[1]*400)
            lastX=diff.seconds/scale
    print("saving to :"+name)
    im.save(name+".png")
    im.close()

def main():
    name="out2"
    if sys.argv[1] is not None:
        name=sys.argv[1]
    dir = "/home/cioni/mesos/MesosPlanner/loadsLog/"
    p=[i for i in os.listdir(dir)]
    #p = ["192.168.0.141Loads.txt","192.168.0.142Loads.txt","192.168.0.144Loads.txt","192.168.0.147Loads.txt","192.168.0.148Loads.txt","192.168.0.149Loads.txt"]
    loads=[]
    names=[]
    for n in p:
        loads.append(open_csv(dir+n))

    min_time = min([l[0] for l in loads if len(l)>0])[0]
    print(min_time)
    for l in loads:
        if (len(l)==0):
            l.append([min_time,0.0,0,0.0,0.0,0.0])

    #drawLoads(loads,str(datetime.datetime.now())+"OUT",min_time)
    drawLoads(p,loads, "/home/cioni/mesos/vizuMes/out/"+name, min_time)
    #draw_loads_lines(loads, "linee", min_time)
    return

main()
