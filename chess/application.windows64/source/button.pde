class Button {
  PVector pos;
  PVector size;
  String text;
  boolean state = false;
  color col;
  
  Button(PVector p_pos, PVector p_size, color p_col, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    state = p_state;
  }
  
  void draw() {
    rectMode(CENTER);
    fill(col);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  boolean mouseOver() {
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      return true;
    }
    else return false;
  }
}