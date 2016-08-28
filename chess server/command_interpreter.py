import XML

import file_operations as fileops
import networking as net
from game import *
from player import *
from vars import *


def interpret(m):

    if m.command == "MOVEMENT REQUEST":
        movement(m)

    elif m.command == "CREATE GAME":
        create_game(m)

    elif m.command == "JOIN GAME":
        join_game(m)

    elif m.command == "LIST GAMES":
        list_games(m)


def create_game(m):

    c = net.getClientByAddr(m.sender)

    if len(glist) > MAXID - MINID:
        c.sendmessage("CREATION ERROR", ["FULL", str(len(glist))])
        return

    if net.ti() < c.lastgamecreation+CREATIONCOOLDOWN:
        c.sendmessage("CREATION ERROR", ["COOLDOWN", str(round(c.lastgamecreation+CREATIONCOOLDOWN-net.ti(), 1))])
        return

    game = Game()
    glist.append(game)
    c.lastgamecreation = net.ti()

    game.password = m.args[0]

    fileops.appendFile("./xml/packlist.chess", [str(game.id)+":0"])
    fileops.rmdir("./xml/" + str(game.id))
    fileops.mkdir("./xml/" + str(game.id))

    # Write to file
    xmlPath = "./xml/" + str(game.id) + "/"
    game.xmlPath = xmlPath
    xmlPath += str(game.latestXML) + ".xml"
    XML.create_xml(xmlPath, m.args[1:])

    m = XML.parse_meta(xmlPath)
    game.board.whoseTurn = WHITE if m["turn"] == "white" else BLACK
    game.board.xSize = int(m["sizeX"])
    game.board.ySize = int(m["sizeY"])
    game.board.setup_fields()

    figures = XML.parse_figures(xmlPath)
    for figure in figures:
        game.board.getFieldByCords(figure.posx, figure.posy).figure = figure


    c.sendmessage("CODE IS", [game.id, CREATIONCOOLDOWN])
    game.sendlistupdate()
    print("Opened Game:", game.id)


def join_game(m): #id pref name pw

    g = findGameByID(int(m.args[0]))
    if g is None:
        return

    nc = net.getClientByAddr(m.sender)
    nc.linkedID = int(m.args[0])
    preference = m.args[1]
    name = m.args[2]
    givenPW = m.args[3]

    if givenPW != g.password:
        #Wrong password!
        print(str(m.sender) + " entered wrong password for " + str(g.id) + "!")
        nc.sendmessage("JOIN REJECTED", ["WRONG PASSWORD"])
        return

    if name == "[EMPTY]":
        name = "Little Hacker"

    if g.playerWHITE is None and g.playerBLACK is None:
        if int(preference) == BLACK:
            g.playerBLACK = Player(nc, name)
            nc.sendmessage("YOU ARE", [BLACK])
            print("BLACK ("+str(m.sender)+") connected to " + str(g.id))
        else:
            g.playerWHITE = Player(nc, name)
            nc.sendmessage("YOU ARE", [WHITE])
            print("WHITE ("+str(m.sender)+") connected to " + str(g.id))

    elif g.playerWHITE is None:
        g.playerWHITE = Player(nc, name)
        nc.sendmessage("YOU ARE", [WHITE])
        print("WHITE ("+str(m.sender)+") connected to " + str(g.id))
    elif g.playerBLACK is None:
        g.playerBLACK = Player(nc, name)
        nc.sendmessage("YOU ARE", [BLACK])
        print("BLACK ("+str(m.sender)+") connected to " + str(g.id))

    else:
        print("[SYSTEM] GAME", g.id, "IS FULL! WE HAVE TO KICK NEWEST CLIENT!")
        nc.sendmessage("JOIN REJECTED", ["GAME FULL"])
        return

    for field in g.board.fields:
        if field.figure is not None:
            nc.sendmessage("ADD FIGURE", [field.figure.col, field.figure.type, field.figure.posx, field.figure.posy, field.figure.hasMoved])
    nc.sendmessage("TURN", [g.board.whoseTurn])

    if g.playerBLACK is not None:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, g.playerBLACK.name])
    else:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, "[EMPTY]"])
    if g.playerWHITE is not None:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", WHITE, g.playerWHITE.name])
    else:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", WHITE, "[EMPTY]"])

    g.sendlistupdate()
    # TODO SUPPORT SPECTATORS


def movement(m):
    c = net.getClientByAddr(m.sender)
    g = findGameByID(c.linkedID)

    fromPosX = int(float(m.args[0]))
    fromPosY = int(float(m.args[1]))
    toPosX = int(float(m.args[2]))
    toPosY = int(float(m.args[3]))

    fig = g.board.getFieldByCords(fromPosX, fromPosY).figure
    fieldFrom = g.board.getFieldByCords(fromPosX, fromPosY)
    fieldTo = g.board.getFieldByCords(toPosX, toPosY)
    fig.hasMoved = True

    net.sendToAll(g.id, "REMOVE FIGURE", [fromPosX, fromPosY])
    net.sendToAll(g.id, "ADD FIGURE", [fig.col, fig.type, toPosX, toPosY, fig.hasMoved])

    fig.posx = toPosX
    fig.posy = toPosY

    fieldFrom.figure = None
    fieldTo.figure = fig

    g.board.whoseTurn = WHITE if g.board.whoseTurn == BLACK else BLACK
    g.movesmade += 1
    g.lastmovetime = net.ti()
    net.sendToAll(g.id, "TURN", [g.board.whoseTurn])
    # todo check check
    XML.createSave("./xml/"+str(g.id)+"/"+str(g.movesmade)+".xml", g)
    fileops.replaceLine("./xml/packlist.chess", str(g.id)+":"+str(g.movesmade-1), str(g.id)+":"+str(g.movesmade))

    turns = g.board.findValidMoves(g.board.whoseTurn, True)

    for turn in turns:
        turn.p()
    print()



def list_games(m):
    c = net.getClientByAddr(m.sender)

    for g in glist:
        whiteName = "None" if g.playerWHITE is None else g.playerWHITE.name
        blackName = "None" if g.playerBLACK is None else g.playerBLACK.name
        c.sendmessage("GAME", [g.id, whiteName, blackName])
