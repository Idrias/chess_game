class Textbox {
  String content = "";
  float xpos, ypos, radx, rady;
  boolean active = false;
  boolean isAlphaAllowed = true;
  int maxchars = 12;
  int correctionstate = 0;
  int correctiontimer = 0;
  String displaytext = "";
  
  Textbox(float p_xpos, float p_ypos, float p_radx, float p_rady, String p_content) {
    xpos = p_xpos;
    ypos = p_ypos;
    radx = p_radx;
    rady = p_rady;
    content = p_content;
  }

  void draw() {
    if (active) fill(255);
    else fill(160);

    if(correctionstate == 2 && millis()-correctiontimer > 1000) correctionstate = 0;
    
    if(correctionstate == 1 && millis()-correctiontimer < 1000) fill(#7FFF6C);
    else if(correctionstate == 2) fill(#FF4649);
    else if(correctionstate == 3) fill(#FFF752);
    
    rectMode(CORNER);
    rect(xpos, ypos, radx, rady);
    rectMode(CENTER);
    
    fill(0);
    displaytext = content;
    //if (content.length()>25) displaytext = content.substring(0, 25);
    

    text(displaytext, xpos+radx/2, ypos+rady/2);

  }
  
  void correct(int state) {
    correctionstate = state;
    correctiontimer = millis();
  }
  
  boolean mouseOver() {
    if (mouseX > xpos && mouseX < xpos+radx) {
      if (mouseY > ypos && mouseY < ypos+rady) {
        return true;
      }
    }
    return false;
  }
}