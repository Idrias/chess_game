import os
import shutil
from game import *
import XML

def rmdir(path):
    try:
        shutil.rmtree(path)
    except FileNotFoundError:
        pass
        #print("[SYSTEM / CRITICAL]: Could not find folder to rmdir in: " + path)


def mkdir(path):
    try:
        os.mkdir(path)
    except FileExistsError:
        print("[SYSTEM]: /xml Folder already existed!")
    except FileNotFoundError:
        print("[SYSTEM / CRITICAL]: Could not find folder to mkdir in: " + path)


def readFile(path):
    try:
        f = open(path, 'r')
        s = f.readlines()
        f.close()
    except FileNotFoundError:
        return []
    return s


def appendFile(path, data):
    try:
        f = open(path, "a")
        f.writelines(data)
        f.write("\n")
        f.close()
    except FileNotFoundError:
        return


def writeFile(path, data):
    f = open(path, 'w')
    f.writelines(data)
    f.write("\n")
    f.close()


def removeLine(path, line):
    f = open(path, "r")
    l = f.readlines()
    f.close()

    nl = []
    for li in l:
        if li != line:
            nl.append(li)

    f = open(path, "w")
    f.writelines(nl)
    f.close()


def replaceLine(path, line, replace):
    print("WE WANT TO REPLACE", line, replace)
    line+="\n"
    replace+="\n"

    f = open(path, "r")
    l = f.readlines()
    f.close()

    nl = []
    for li in l:
        if li != line:
            nl.append(li)
        else:
            nl.append(replace)

    f = open(path, "w")
    f.writelines(nl)
    f.close()


def loadGames():
    try:
        f = open("./xml/packlist.chess", "r")
    except FileNotFoundError:
        return

    saves = f.readlines()

    for save in saves:
        save = save.rstrip()
        gameID = save[:save.find(":")]
        lastNum = save[save.find(":")+1:]

        g = Game()
        g.id = int(gameID)
        xmlPath = "./xml/"+gameID+"/"+lastNum+".xml"

        try:
            m = XML.parse_meta(xmlPath)
            g.password = m["password"]

            g.whoseturn = WHITE if m["turn"] == "white" else BLACK
            g.board.xSize = int(m["sizeX"])
            g.board.ySize = int(m["sizeY"])
            g.movesmade = int(m["movesmade"])
            g.board.setup_fields()
        except KeyError:
            continue

        figures = XML.parse_figures(xmlPath)
        for figure in figures:
            print(figure.posx, figure.posy)
            print(g.board.xSize, g.board.ySize)
            g.board.getFieldByCords(figure.posx, figure.posy).figure = figure

        if g.password is None:
            g.password = ""

        if g.movesmade > 0:
            glist.append(g)