from game import *
from vars import *
import command_interpreter as interpreter
import file_operations as fileops
import networking as net
from time import sleep

frameCount = 0

def setup():
    fileops.mkdir("xml")
    net.setup_net()


def draw():
    for nc in net.ncs:
        for req in nc.messages:
            interpreter.interpret(req)
            nc.messages.remove(req)

    global frameCount

    if frameCount % 30 == 0:
        collectGarbage()

    frameCount +=1


def collectGarbage():
    for g in glist:
        if g.movesmade < 4 and g.playerWHITE is None and g.playerBLACK is None \
                and net.ti() > g.creationtime + MAXBEGINIDLE:
            glist.remove(g) #heavy TODO
            net.sendToAllAll("GAME REMOVED", [str(g.id)])

        elif net.ti()-g.lastmovetime > MAXIDLE:
            glist.remove(g)
            net.sendToAllAll("GAME REMOVED", [str(g.id)])

setup()

try:
    while True:
        draw()
        sleep(INTERFRAMETIME)
except (KeyboardInterrupt, SystemExit):
    print("Exiting now...")
    exit()