import xml.etree.ElementTree as ET
from xml.dom import minidom
from Figure import *
from vars import *
import file_operations as fileops

def parse_figures(path):
    figures = []

    tree = ET.parse(path)
    root = tree.getroot()

    for xmlFigure in root.iter("figure"):
        figure = Figure()
        figure.col = WHITE if xmlFigure.attrib["color"] == "white" else BLACK
        figure.hasMoved = True if xmlFigure.attrib["hasMoved"] == "yes" else False
        figure.posx = xmlFigure.attrib["xloc"]
        figure.posx = alphanum[figure.posx] - 1
        figure.posy = 8-int(xmlFigure.attrib["yloc"])
        figure.type = stringtofigure[xmlFigure.text]
        figures.append(figure)

    return figures


def parse_meta(path):
    meta = {}
    tree = ET.parse(path)
    root = tree.getroot()

    for xmlMeta in root.iter("meta"):
        for child in xmlMeta:
            meta[child.tag] = child.text
    return meta

def create_xml(path, data):
    f = open(path, 'w')

    for line in data:
        line = line.replace("\\t", "\t")
        f.write(line + "\n")
    f.close()


def createSave(path, g):
    board = ET.Element("board")

    # Meta
    meta = ET.SubElement(board, "meta")

    #TODO
    sizeX = ET.SubElement(meta, "sizeX")
    #sizeX.text = str(g.board.xSize)
    sizeX.text = str(8)

    sizeY = ET.SubElement(meta, "sizeY")
    #sizeY.text = str(g.board.ySize)
    sizeY.text = str(8)

    turn = ET.SubElement(meta, "turn")
    turn.text = "white" if g.board.whoseTurn == WHITE else "black"

    turnspassed = ET.SubElement(meta, "movesmade")
    turnspassed.text = str(g.movesmade)

    password = ET.SubElement(meta, "password")
    password.text = g.password
    # End of Meta

    figures = ET.SubElement(board, "figures")

    for field in g.board.fields:
        fig = field.figure
        if fig is not None:
            xloc = ""
            for alpha in alphanum:
                if alphanum[alpha] == fig.posx+1:
                    xloc = alpha

            figXML = ET.SubElement(figures, "figure", {"color": "white" if fig.col == WHITE else "black",
                                                       "xloc": str(xloc),
                                                       "yloc": str(8-(fig.posy)),
                                                       "hasMoved": "yes" if fig.hasMoved else "no"
                                                       })

            for string in stringtofigure:
                if stringtofigure[string] == fig.type:
                    figXML.text = string

    board = printpretty(board)
    fileops.writeFile(path, board)


def printpretty(e):
    old = ET.tostring(e, 'utf-8')
    new = minidom.parseString(old)
    new = new.toprettyxml(indent="  ")
    return new
