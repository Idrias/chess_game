class LightSwitch {
  PVector pos;
  PVector size;
  
  String textA;
  String textB;
  
  boolean state = false;
  
  color colA;
  color col2ndA;
  color colB;
  color col2ndB;
  
  color drawcol;
  
  
  LightSwitch(PVector p_pos, PVector p_size, color p_colA, color p_col2ndA, String p_textA, color p_colB, color p_col2ndB, String p_textB, boolean p_state) {
    
    // Same for both sides
    pos = p_pos.copy();
    size = p_size.copy();
    
    textA = p_textA;
    textB = p_textB;
    
    colA = p_colA;
    col2ndA = p_col2ndA;
    
    colB = p_colB;
    col2ndB = p_col2ndB;
    
    state = p_state;
    
    drawcol = state ? colA : colB;
  }
  
  
  void draw() {
    mouseOver();
    rectMode(CENTER);
    fill(drawcol);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(state ? colB : colA);
    text(state ? textA : textB, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  void press() {
    state = !state;
  }
  
  boolean mouseOver() {
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      
      drawcol = state ? col2ndA : col2ndB;
      return true;
    }
    
    drawcol = state? colA : colB;
    return false;
  }
}