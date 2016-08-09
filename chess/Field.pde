class Field {
  int col;
  PVector pos;
  Figure figure;
  int drawX = 0, drawY = 0;
  int highlightCode = NOT_HIGHLIGHTED;
  
  
  Field() {
  
  }


  void draw( int startX, int startY ) {
    
    
    fill(col);
    
    if(highlightCode == CAN_GO) {
      fill(CAN_GO_COLOR);
      if(figure != null && figure.faction == HOSTILE) {
        fill(IN_DANGER_COLOR);
      }
    }
    
    else if(highlightCode == FRIENDLY_PICKED) fill(SELECTED_COLOR);

    
    drawX = int(startX + pos.x * game.board.DRAW_fieldSizeX);
    drawY = int(startY + pos.y * game.board.DRAW_fieldSizeY);
    
    rect(drawX, drawY, drawX+game.board.DRAW_fieldSizeX, drawY+game.board.DRAW_fieldSizeY);
  }
  
  void draw_figure() {
      if(figure != null) {
      figure.draw( int(drawX + game.board.DRAW_fieldSizeX/2), int(drawY +game.board.DRAW_fieldSizeY/2) );
    }
  } 
  
  boolean checkclick() {
    if(mouseX > drawX && mouseX < drawX + game.board.DRAW_fieldSizeX) {
      if(mouseY > drawY && mouseY < drawY + game.board.DRAW_fieldSizeY) {
        return true;
      }
    }
    return false;
  }
}