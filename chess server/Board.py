from Field import *
from vars import *
from PossibleMove import *
import copy


class Board:
    def __init__(self):
        self.xSize = None
        self.ySize = None
        self.whoseTurn = 1337
        self.isWhiteCheck = False
        self.isBlackCheck = False  # TODO check on load

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

    def isCheck(self, who):
        if who == WHITE:
            if self.isWhiteCheck:
                return True
            return False
        elif who == BLACK:
            if self.isBlackCheck:
                return True
            return False
        return False

    def checkCheck(self):
        for field in self.fields:
            f = field.figure
            if f is None:
                continue
            c = f.col
            notC = WHITE if f.col == BLACK else BLACK

            if f.type == KING:
                print("Imaginary King:", f.posx, f.posy)
                if self.isFieldInDanger(f.posx, f.posy, notC):
                    print("is in danger at this pos!")
                    if c == WHITE:
                        self.isWhiteCheck = True
                    elif c == BLACK:
                        self.isBlackCheck = True
                else:
                    if c == WHITE:
                        self.isWhiteCheck = False
                    elif c == BLACK:
                        self.isBlackCheck = False

    def findValidMoves(self, color, topLevel):

        FRIENDLY = color
        HOSTILE = WHITE if FRIENDLY == BLACK else BLACK

        def v(x, y):
            f = self.getFieldByCords(x, y)
            if f is not None:
                if f.figure is None or f.figure.col == HOSTILE:
                    return True
            return False

        def e(x, y):
            f = self.getFieldByCords(x, y)
            if f is not None:
                if f.figure is None:
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
                    if field.figure.type == KING:
                        print("There is a king that could move (SURPRISE MOTHERFUCKER)")
        # start finding moves
        for f in possibleFigures:
            px = f.posx
            py = f.posy

            if f.type == PAWN or f.type == QUEEN:

                if FRIENDLY == WHITE:

                    if e(px, py - 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py - 1, f, gf(px, py - 1)))
                        if e(px, py - 2) and not f.hasMoved:
                            vmoves.append(PossibleMove(FRIENDLY, px, py, px, py - 2, f, gf(px, py - 2)))
                    if h(px - 1, py - 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py - 1, f, gf(px - 1, py - 1)))
                    if h(px + 1, py - 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py - 1, f, gf(px + 1, py - 1)))

                if FRIENDLY == BLACK:
                    if e(px, py + 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py + 1, f, gf(px, py + 1)))
                        if e(px, py + 2) and not f.hasMoved:
                            vmoves.append(PossibleMove(FRIENDLY, px, py, px, py + 2, f, gf(px, py + 2)))
                    if h(px - 1, py + 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py + 1, f, gf(px - 1, py + 1)))
                    if h(px + 1, py + 1):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py + 1, f, gf(px + 1, py + 1)))

            if f.type == TOWER or f.type == QUEEN:
                i = 1
                while True:
                    if v(px + i, py):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py, f, gf(px + i, py)))
                    else:
                        break
                    if not e(px + i, py):
                        break
                    i += 1

                i = -1
                while True:
                    if v(px + i, py):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py, f, gf(px + i, py)))
                    else:
                        break
                    if not e(px + i, py):
                        break
                    i -= 1

                i = 1
                while True:
                    if v(px, py + i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py + i, f, gf(px, py + i)))
                    else:
                        break
                    if not e(px, py + i):
                        break
                    i += 1

                i = -1
                while True:
                    if v(px, py + i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px, py + i, f, gf(px, py + i)))
                    else:
                        break
                    if not e(px, py + i):
                        break
                    i -= 1

            if f.type == HORSE:
                if v(px + 2, py + 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 2, py + 1, f, gf(px + 2, py + 1)))
                if v(px + 2, py - 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 2, py - 1, f, gf(px + 2, py - 1)))
                if v(px - 2, py + 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 2, py + 1, f, gf(px - 2, py + 1)))
                if v(px - 2, py - 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 2, py - 1, f, gf(px - 2, py - 1)))
                if v(px + 1, py - 2):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py - 2, f, gf(px + 1, py - 2)))
                if v(px - 1, py - 2):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py - 2, f, gf(px - 1, py - 2)))
                if v(px - 1, py + 2):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py + 2, f, gf(px - 1, py + 2)))
                if v(px + 1, py + 2):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py + 2, f, gf(px + 1, py + 2)))

            if f.type == BISHOP:
                i = 1
                while True:
                    if v(px + i, py + i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py + i, f, gf(px + i, py + i)))
                    if not e(px + i, py + i):
                        break
                    i += 1

                i = -1

                while True:
                    if v(px + i, py + i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py + i, f, gf(px + i, py + i)))
                    if not e(px + i, py + i):
                        break
                    i -= 1

                i = 1

                while True:
                    if v(px + i, py - i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py - i, f, gf(px + i, py - i)))
                    if not e(px + i, py - i):
                        break
                    i += 1

                i = -1

                while True:
                    if v(px + i, py - i):
                        vmoves.append(PossibleMove(FRIENDLY, px, py, px + i, py - i, f, gf(px + i, py - i)))
                    if not e(px + i, py - i):
                        break
                    i -= 1

            if f.type == KING:
                if v(px, py + 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px, py + 1, f, gf(px, py + 1)))
                if v(px, py - 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px, py - 1, f, gf(px, py - 1)))
                if v(px + 1, py + 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py + 1, f, gf(px + 1, py + 1)))
                if v(px + 1, py):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py, f, gf(px + 1, py)))
                if v(px + 1, py - 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px + 1, py - 1, f, gf(px + 1, py - 1)))
                if v(px - 1, py + 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py + 1, f, gf(px - 1, py + 1)))
                if v(px - 1, py):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py, f, gf(px - 1, py)))
                if v(px - 1, py - 1):
                    vmoves.append(PossibleMove(FRIENDLY, px, py, px - 1, py - 1, f, gf(px - 1, py - 1)))


                """
                for t in possibleFigures:
                    if t.type == TOWER:
                        if not f.hasMoved and not t.hasMoved and topLevel:
                            if not self.isCheck(FRIENDLY):
                                if t.posx == px - 4:
                                    if not self.isFieldInDanger(px - 1, py, HOSTILE) and e(px - 1, py):
                                        if not self.isFieldInDanger(px - 2, py, HOSTILE) and e(px - 2, py):
                                            if e(px - 3, py):
                                                vmoves.append(PossibleMove(FRIENDLY, px, py, px - 2, py, f, None))
                                                print(px - 2, py)
                                                vmoves[-1].isRochade = True
                                                vmoves[-1].towerComponentFrom = px - 4
                                                vmoves[-1].towerComponentTo = px - 1
                                elif t.posx == px + 3:
                                    if not self.isFieldInDanger(px + 1, py, HOSTILE) and e(px + 1, py):
                                        if not self.isFieldInDanger(px + 2, py, HOSTILE) and e(px + 2, py):
                                            vmoves.append(PossibleMove(FRIENDLY, px, py, px + 2, py, f, None))
                                            print(px + 2, py)
                                            vmoves[-1].isRochade = True
                                            vmoves[-1].towerComponentFrom = px + 3
                                            vmoves[-1].towerComponentTo = px + 1

                """
        # end finding moves


        # look if the moves would cause checkmate
        returnmoves = []


        if topLevel:
            print("-------")
            for vm in vmoves:
                imaginary = copy.deepcopy(self)

                imaginary.getFieldByCords(vm.fromFieldX, vm.fromFieldY).figure = None
                imaginary.getFieldByCords(vm.toFieldX, vm.toFieldY).figure = vm.friendlyFigure
                vm.friendlyFigure.posx = vm.toFieldX
                vm.friendlyFigure.posy = vm.toFieldY


                if vm.isRochade:
                    imaginary.getFieldByCords(vm.towerComponentFrom, vm.toFieldY).figure.posx = vm.towerComponentTo
                    imaginary.getFieldByCords(vm.towerComponentTo, vm.toFieldY).figure = imaginary.getFieldByCords(
                        vm.towerComponentFrom, vm.toFieldY).figure
                    imaginary.getFieldByCords(vm.towerComponentFrom, vm.toFieldY).figure = None


                imaginary.checkCheck()

                if imaginary.isCheck(FRIENDLY):
                    continue
                else:
                    returnmoves.append(vm)

                print("-------")

        else:
            returnmoves = vmoves
        return returnmoves




    def isFieldInDanger(self, x, y, hostileCol):
        print("LOOKING FOR DANGER BY", hostileCol)
        moves = self.findValidMoves(hostileCol, False)
        for move in moves:
            print("enemy would move", move.toFieldX, move.toFieldY)
            if move.toFieldX == x and move.toFieldY == y:
                return True
        return False
