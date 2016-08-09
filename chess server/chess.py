from command_interpreter import *
from vars import *
from game import *

import command_interpreter as interpreter
import networking as net

from time import sleep


def setup():
    fileops.mkdir("xml")
    net.setup_net()


def draw():
    for nc in net.ncs:
        for req in nc.messages:
            interpreter.interpret(req)
            nc.messages.remove(req)


setup()
while True:
    draw()
    sleep(INTERFRAMETIME)
