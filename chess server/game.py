from random import randint
from vars import *
from Board import *

import networking as net

glist = []


class Game:
    def __init__(self):
        self.genID()
        self.latestXML = 0
        self.board = Board()
        self.playerWHITE = None
        self.playerBLACK = None
        self.board.whoseTurn = WHITE #TODO
        self.xmlPath = ""
        self.password = ""
        self.creationtime = net.ti()
        self.movesmade = 0
        self.lastmovetime = net.ti()


    def genID(self):
        flag = False
        while True:
            self.id = randint(MINID, MAXID)
            for game in glist:
                if self.id == game.id:
                    flag = True
                    # Todo implement alle games voll / implement Remis bei wiederholung
            if flag:
                flag = False
            else:
                break



    def sendlistupdate(self):
        whiteName = "None" if self.playerWHITE is None else self.playerWHITE.name
        blackName = "None" if self.playerBLACK is None else self.playerBLACK.name
        net.sendToAllAll("GAME", [self.id, whiteName, blackName])


def findGameByID(id):
    for g in glist:
        if g.id == id:
            return g
    return None
