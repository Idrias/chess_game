class Button {
  PVector pos;
  PVector size;
  String text;
  boolean state = false;
  color col;
  color col2nd;
  color drawcol;
  int forMode = -1;
  
  Button(PVector p_pos, PVector p_size, color p_col, color p_col2nd, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    drawcol = col;
    col2nd = p_col2nd;
    state = p_state;
  }
  
  void draw() {
    mouseOver();
    rectMode(CENTER);
    fill(drawcol);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  boolean mouseOver() {
    if(!state) {drawcol = 100; return false;}
    
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      drawcol = col2nd;
      return true;
    }
    drawcol = col;
    return false;
  }
}