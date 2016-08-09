import xml.etree.ElementTree as ET
from figure import *
from vars import *


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


def create_xml(path, data):
    f = open(path, 'w')

    for line in data:
        line = line.replace("\\t", "\t")
        f.write(line + "\n")
    f.close()
