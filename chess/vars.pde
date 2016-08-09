/* INTEGERS */
final int BLACK = 180;
final int WHITE = 255;
final int PAWN = 2;
final int TOWER = 3;
final int BISHOP = 4;
final int HORSE = 5;
final int QUEEN = 6;
final int KING = 7;
final int UNDEFINED = 999;
final int CONNECTED = 1;
final int OPTIONS = 4;
final int WAITING = 0;
final int MENU = -1;
final int SERVERBROWSER = 3;
final int GAMECREATION = 4;
final int NOT_HIGHLIGHTED = 0;
final int FRIENDLY_PICKED = 1;
final int CAN_GO = 2;
final int FRIENDLY = 42; 
final int HOSTILE = 666;
final int SENDAGAINTHERESHOLD = 0;
final int ACTIVE = 1;
final int NOTACTIVE = 2;
/* END OF INTEGERS */

// DEV - HAS TO CHANGE!!!
int thisPlayerFaction = WHITE;
String xmlLocation = "/assets/xml/default_board.xml";
chess sketchRef = this;
int preference = WHITE;
String IPPRESET = "127.0.0.1:6877";

// Colors
color CAN_GO_COLOR = #91FF81;
color IN_DANGER_COLOR = #FF4646;
color SELECTED_COLOR = #FBFF46;
// End of colors

/* OBJECTS */
Game game;
Menu menu;
Serverbrowser browser;
xml_parser parser;
InputHandler input;
ArrayList<ReferencedImage> images;
/* END OF OBJECTS */





void init_vars() {
  parser = new xml_parser();
  images = new ArrayList<ReferencedImage>();
  load_images();
  
  input = new InputHandler();
  
  game = new Game();
  menu = new Menu();
  browser = new Serverbrowser();

  try {
    for (Figure figure : parser.parseFigures(xmlLocation)) {
      game.board.fields[int(figure.pos.x)][int(figure.pos.y)].figure = figure;
    }
  }
  catch (NullPointerException ThisWasNotATriumphGladOS) {
    println("Dis is no gud...");
  }
}



void load_images() {
  // Backgrounds
    images.add(new ReferencedImage("/assets/background/wood-texture.jpg", "wooden background"));
    images.add(new ReferencedImage("/assets/background/studyroom.jpg", "study room"));
    images.add(new ReferencedImage("/assets/background/server.jpg", "server room"));
  
  // Figures
    // White
      images.add(new ReferencedImage("/assets/figures/white/pawn.png", "white pawn"));
      images.add(new ReferencedImage("/assets/figures/white/tower.png", "white tower"));
      images.add(new ReferencedImage("/assets/figures/white/horse.png", "white horse"));
      images.add(new ReferencedImage("/assets/figures/white/bishop.png", "white bishop"));
      images.add(new ReferencedImage("/assets/figures/white/queen.png", "white queen"));
      images.add(new ReferencedImage("/assets/figures/white/king.png", "white king"));
      
    // Black
      images.add(new ReferencedImage("/assets/figures/black/pawn.png", "black pawn"));
      images.add(new ReferencedImage("/assets/figures/black/tower.png", "black tower"));
      images.add(new ReferencedImage("/assets/figures/black/horse.png", "black horse"));
      images.add(new ReferencedImage("/assets/figures/black/bishop.png", "black bishop"));
      images.add(new ReferencedImage("/assets/figures/black/queen.png", "black queen"));
      images.add(new ReferencedImage("/assets/figures/black/king.png", "black king"));
      
   // Misc
      images.add(new ReferencedImage("/assets/figures/missing no.png", "missing no"));
}