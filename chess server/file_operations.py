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


