class Game {
  Board board;
  int state;
  
  Game() {
    state = MENU;
    board = new Board();
  }


  void draw() {
    if(state == MENU) menu.draw();
    if(state == CONNECTED) board.draw();
    if(state == SERVERBROWSER) browser.draw();
  }

}