#!/usr/bin/python3


import socket
import threading
import game
from vars import *
from time import time as ti

s = socket.socket()
ncs = []


class Message:
    def __init__(self, command, args, sender):
        self.command = command
        self.args = args
        self.sender = sender


class NetClient:
    def __init__(self, c, addr):
        self.comsock = c
        self.addr = addr
        self.backlog = ""
        self.messages = []
        self.lastResponse = ti()
        self.linkedID = 0

    def sendmessage(self, command, args):
        s = "$" + str(command) + ";"
        for arg in args:
            arg = str(arg)
            s += arg + ";"
        s += "&"
        s = s.encode()

        try:
            self.comsock.send(s)
        except OSError:
            print("Client is not here anymore...")


def gethash(msg):
    value = 36
    msg = msg[:-4]

    for ch in msg:
        value += ord(ch)
    return value


def parse_messages(c):
    soup = c.backlog
    while True:
        beginreq = soup.find("$")
        endreq = soup.find("&")
        if beginreq == -1 or endreq == -1:
            return

        req = soup[beginreq + 1: endreq]

        """
        sending = "$THX;"+str(gethash(req))+";&"
        print(sending)
        c.comsock.send(sending.encode())
        """

        command = req[0: req.find(";")]
        lastsemi = req.find(";")
        nextsemi = req.find(";", lastsemi + 1)
        arguments = []

        while nextsemi != -1:
            arguments.append(req[lastsemi + 1:nextsemi])
            lastsemi = nextsemi
            nextsemi = req.find(";", lastsemi + 1)

        c.messages.append(Message(command, arguments, c.addr))
        soup = soup[endreq + 4:]
        c.backlog = soup


def setup_net():
    host = socket.gethostname()
    port = 6877
    s.bind((host, port))
    s.listen(5)
    welcome_thread = threading.Thread(target=welcome)
    welcome_thread.start()


def welcome():
    while True:
        c, addr = s.accept()
        ncs.append(NetClient(c, addr))
        rcv_thread = threading.Thread(target=rcv)
        rcv_thread.start()


def rcv():
    c = ncs[-1]

    while True:
        try:
            mail, fromaddr = c.comsock.recvfrom(1024)
        except ConnectionResetError:
            print("Client", c.addr, "disconnected!")
            dcFromGame(c)
            ncs.remove(c)
            return
        except ConnectionAbortedError:
            print("Client", c.addr, "disconnected!")
            dcFromGame(c)
            ncs.remove(c)
            return
        except OSError:
            print("[SYSTEM] OS ERROR")
            try:
                dcFromGame(c)
                ncs.remove(c)
            except:
                print("Already done.")

        if mail == b'':
            dcFromGame(c)
            c.comsock.close()
            ncs.remove(c)
            print("Oh no...")
            return

        else:
            c.backlog += str(mail)[2:-1]
            c.lastResponse = ti()
            parse_messages(c)


def getClientByAddr(addr):
    for nc in ncs:
        if nc.addr == addr:
            return nc
    return None


def dcFromGame(c):
    g = game.findGameByID(c.linkedID)

    if g is not None:
        if g.playerWHITE is not None and g.playerWHITE.client == c:
            g.playerWHITE = None
            sendToAll(g.id, "UI UPDATE", ["NAME", WHITE, "[EMPTY]"])
            print("WHITE disconnected from " + str(g.id))
        elif g.playerWHITE is not None and g.playerBLACK.client == c:
            g.playerBLACK = None
            sendToAll(g.id, "UI UPDATE", ["NAME", BLACK, "[EMPTY]"])
            print("BLACK disconnected from " + str(g.id))


def sendToAll(gameID, command, args):
    for nc in ncs:
        if nc.linkedID == gameID:
            nc.sendmessage(command, args)