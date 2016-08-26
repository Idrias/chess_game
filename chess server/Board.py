from Field import *
from vars import *
from PossibleMove import *

class Board:
    def __init__(self):
        self.xSize = None
        self.ySize = None
        self.whoseTurn = None

        self.fields = []

    def setup_fields(self):
        for x in range(self.xSize):
            for y in range(self.ySize):
                self.fields.append(Field(x, y))

    def getFieldByCords(self, x, y):
        for field in self.fields:
            if field.posx == x and field.posy == y:
                return field
        return None

# Future
"""
    def findValidMoves(self):

        FRIENDLY = self.whoseTurn
        HOSTILE = WHITE if FRIENDLY == BLACK else BLACK

        def v(x, y):
            f = self.getFieldByCords(x, y)
            if f is not None:
                if f.figure is None or f.figure.col == HOSTILE:
                    return True
            return False

        def h(x, y):
            f = self.getFieldByCords(x, y)
            if f is not None:
                if f.figure is not None and f.figure.col == HOSTILE:
                    return True
            return False

        def gf(x, y):
            return self.getFieldByCords(x, y).figure

        vmoves = []
        possibleFigures = []


        for field in self.fields:
            if field.figure is not None:
                if field.figure.col == FRIENDLY:
                    possibleFigures.append(field.figure)


        for f in possibleFigures:
            px = f.posx
            py = f.posy

            if f.type == PAWN:
                if FRIENDLY == WHITE:
                    if v(px, py-1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py-1, f, gf(px, py-1)))
                    if v(px, py-2) and not f.hasMoved:
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py-2, f, gf(px, py-2)))
                    if h(px-1, py-1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px-1, py-1, f, gf(px-1, py-1)))
                    if h(px-1, py-1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px-1, py-1, f, gf(px-1, py-1)))


"""






