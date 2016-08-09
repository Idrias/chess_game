from field import *


class Board:
    def __init__(self):
        self.xSize = None
        self.ySize = None
        self.whoseTurn = None

        self.fields = []
        #TODO PARSE SIZE OF FIELD

        for x in range(8):
            for y in range(8):
                self.fields.append(Field(x, y))

    def getFieldByCords(self, x, y):
        for field in self.fields:
            if field.posx == x and field.posy == y:
                return field
        return None