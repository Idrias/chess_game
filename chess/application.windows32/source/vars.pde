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
final int JOIN = 0;
final int CREATE = 1;
final int ALL = 42;
/* END OF INTEGERS */


chess sketchRef = this;

String IPPRESET = "192.168.178.21:6877";
// 192.168.178.21:6877
// 84.200.52.231:6877

int thisPlayerFaction = WHITE;
int preference = WHITE;

color CAN_GO_COLOR = #91FF81;
color IN_DANGER_COLOR = #FF4646;
color SELECTED_COLOR = #FBFF46;



/* OBJECTS */
Game game;
Menu menu;
Serverbrowser browser;
xml_parser parser;
InputHandler input;
Networker net;

ArrayList<ReferencedImage> images;
/* END OF OBJECTS */




void init_vars() {
  
  images = new ArrayList<ReferencedImage>();
  load_images();
  
  parser = new xml_parser();
  input = new InputHandler();
  net = new Networker("0", 0);
  game = new Game();
  menu = new Menu();
  browser = new Serverbrowser();
  
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