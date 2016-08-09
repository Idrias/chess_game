class xml_parser {
  XML xml;
  
  int[] parseMeta(String fileLocation) {
  xml = loadXML(fileLocation);
  XML[] XMLmeta = xml.getChildren("meta");
  
  XML sizeX = XMLmeta[0].getChildren("sizeX")[0];
  XML sizeY = XMLmeta[0].getChildren("sizeY")[0];
  XML whoseTurn = XMLmeta[0].getChildren("turn")[0];

  int i_sizeX = int(sizeX.getContent());
  int i_sizeY = int(sizeY.getContent());
  int i_turn = whoseTurn.getContent().equals("white") ? WHITE : BLACK;

  return new int[]{i_sizeX, i_sizeY, i_turn};
  }
  
  ArrayList<Figure> parseFigures(String fileLocation) {
    try {
      xml = loadXML(fileLocation);
    }
    catch(NullPointerException e) {
      println("WARNING: XML FILE NOT FOUND: " + fileLocation);
      return null;
    }

    XML[] XMLfigures = xml.getChildren("figures");
    XMLfigures = XMLfigures[0].getChildren("figure");
    
    ArrayList<Figure> figures = new ArrayList<Figure>();

    for (int i = 0; i < XMLfigures.length; i++) {
      XML f = XMLfigures[i];

      String cString = f.getString("color");
      int col = cString.equals("white") ? WHITE : cString.equals("black") ? BLACK : UNDEFINED;

      int figuretype;

      switch(f.getContent()) {
      case "Pawn": 
        figuretype = PAWN; 
        break;
      case "Tower": 
        figuretype = TOWER; 
        break;
      case "Horse": 
        figuretype = HORSE; 
        break;
      case "Bishop": 
        figuretype = BISHOP; 
        break;
      case "Queen": 
        figuretype = QUEEN; 
        break;
      case "King": 
        figuretype = KING; 
        break;
      default: 
        figuretype = UNDEFINED; 
        break;
      }
      
      // TODO change 8 
      int figureY = 8 - (f.getInt("yloc"));
      int figureX;
      //int figureX = unhex(f.getString("xloc"));
      switch(f.getString("xloc")) {
        case "a": figureX = 0; break;
        case "b": figureX = 1; break;
        case "c": figureX = 2; break;
        case "d": figureX = 3; break;
        case "e": figureX = 4; break;
        case "f": figureX = 5; break;
        case "g": figureX = 6; break;
        case "h": figureX = 7; break;
        
        case "i": figureX = 8; break;
        case "j": figureX = 9; break;
        case "k": figureX = 10; break;
        case "l": figureX = 11; break;
        case "m": figureX = 12; break;
        case "n": figureX = 13; break;
        case "o": figureX = 14; break;
        case "p": figureX = 15; break;
        
        case "q": figureX = 16; break;
        case "r": figureX = 17; break;
        case "s": figureX = 18; break;
        case "t": figureX = 19; break;
        case "u": figureX = 20; break;
        case "v": figureX = 21; break;
        case "w": figureX = 22; break;
        case "x": figureX = 23; break;
        case "y": figureX = 24; break;
        case "z": figureX = 25; break;
        default: continue;
      }

      Figure figure = new Figure( col, figuretype, new PVector(figureX, figureY));
      figures.add(figure);
    }

    return figures.size() > 0 ? figures : null;
  }
}