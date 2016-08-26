import os
import shutil


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
