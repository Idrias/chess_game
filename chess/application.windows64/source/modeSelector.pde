class ModeSelector {
  
  PVector pos;
  PVector size;
  String text;
  boolean state = false;
  color col;
  color col2nd;
  color drawcol;
  ModeSelector partner;
  
  ModeSelector(PVector p_pos, PVector p_size, color p_col, color p_col2nd, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    drawcol = col;
    col2nd = p_col2nd;
    state = p_state;
  }
  
  
  void set_partner(ModeSelector p_partner) {
    partner = p_partner;
  }
  
  void draw() {
    mouseOver();
    rectMode(CENTER);
    fill(drawcol);
    
    if(!state) fill(#C9C9C9); 
    
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  boolean mouseOver() {
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      drawcol = col2nd;
      partner.state = false;
      state = true;
      return true;
    }
    drawcol = col;
    return false;
  }
  
}