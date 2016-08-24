import XML

import file_operations as fileops
import networking as net
from game import *
from player import *


# Gittest

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
    game = Game()
    glist.append(game)

    game.password = m.args[0]
    net.sendToAllAll("GAME", [game.id, game.playerWHITE, game.playerBLACK])

    fileops.rmdir("./xml/" + str(game.id))
    fileops.mkdir("./xml/" + str(game.id))

    # Write to file
    xmlPath = "./xml/" + str(game.id) + "/"
    game.xmlPath = xmlPath
    xmlPath += str(game.latestXML) + ".xml"
    XML.create_xml(xmlPath, m.args[1:])

    figures = XML.parse_figures(xmlPath)
    for figure in figures:
        game.board.getFieldByCords(figure.posx, figure.posy).figure = figure

    net.getClientByAddr(m.sender).sendmessage("CODE IS", [game.id])


def join_game(m):
    g = findGameByID(int(m.args[0]))
    if g is None:
        return

    nc = net.getClientByAddr(m.sender)
    nc.linkedID = int(m.args[0])

    preference = m.args[1]
    name = m.args[2]
    if name == "[EMPTY]":
        name = "Little Hacker"

    if g.playerWHITE is None and g.playerBLACK is None:
        if int(preference) == BLACK:
            g.playerBLACK = Player(nc, name)
            nc.sendmessage("YOU ARE", [BLACK])
            print("BLACK connected to " + str(g.id))
        else:
            g.playerWHITE = Player(nc, name)
            nc.sendmessage("YOU ARE", [WHITE])
            print("WHITE connected to " + str(g.id))

    elif g.playerWHITE is None:
        g.playerWHITE = Player(nc, name)
        nc.sendmessage("YOU ARE", [WHITE])
        print("WHITE connected to " + str(g.id))
    elif g.playerBLACK is None:
        g.playerBLACK = Player(nc, name)
        nc.sendmessage("YOU ARE", [BLACK])
        print("BLACK connected to " + str(g.id))

    else:
        print("[SYSTEM] GAME", g.id, "IS FULL! WE HAVE TO KICK NEWEST CLIENT!")
        nc.comsock.close()
        return

    for field in g.board.fields:
        if field.figure is not None:
            nc.sendmessage("ADD FIGURE", [field.figure.col, field.figure.type, field.figure.posx, field.figure.posy, field.figure.hasMoved])
    nc.sendmessage("TURN", [g.whoseturn])

    if g.playerBLACK is not None:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, g.playerBLACK.name])
    else:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, "[EMPTY]"])
    if g.playerWHITE is not None:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", WHITE, g.playerWHITE.name])
    else:
        net.sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, "[EMPTY]"])
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

    g.whoseturn = WHITE if g.whoseturn == BLACK else BLACK
    net.sendToAll(g.id, "TURN", [g.whoseturn])


def list_games(m):
    c = net.getClientByAddr(m.sender)

    for g in glist:
        c.sendmessage("GAME", [g.id, g.playerWHITE, g.playerBLACK])
