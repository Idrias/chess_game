from random import randint
from vars import *
from Board import *

glist = []


class Game:
    def __init__(self):
        self.genID()
        self.latestXML = 0
        self.board = Board()
        self.playerWHITE = None
        self.playerBLACK = None
        self.whoseturn = WHITE #TODO
        self.xmlPath = ""

    def genID(self):
        flag = False
        while True:
            self.id = randint(MINID, MAXID)
            for game in glist:
                if self.id == game.id:
                    flag = True
                    # Todo implement alle games voll
            if flag:
                flag = False
            else:
                break

        print("Opened Game:", self.id)


def findGameByID(id):
    for g in glist:
        if g.id == id:
            return g
    return None
