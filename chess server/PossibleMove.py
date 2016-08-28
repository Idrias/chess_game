from vars import *

class PossibleMove:
    def __init__(self, p_actorColor, p_fromFieldX, p_fromFieldY, p_toFieldX, p_toFieldY, p_friendlyFigure, p_slainFigure):
        self.actorColor = p_actorColor
        self.fromFieldX = p_fromFieldX
        self.fromFieldY = p_fromFieldY
        self.toFieldX = p_toFieldX
        self.toFieldY = p_toFieldY
        self.friendlyFigure = p_friendlyFigure
        self.slainFigure = p_slainFigure

        self.isRochade = False
        self.towerComponentFrom = None
        self.towerComponentTo = None

    def p(self):
        if self.actorColor == WHITE:
            print("WHITE " + str(self.fromFieldX), str(self.fromFieldY), " - ", str(self.toFieldX), str(self.toFieldY))
        else:
            print("BLACK " + str(self.fromFieldX), str(self.fromFieldY), " - ", str(self.toFieldX), str(self.toFieldY))