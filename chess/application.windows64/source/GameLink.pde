class GameLink {

  String description;
  String pWhite;
  String pBlack;
  int id;
  PVector pos;
  boolean selected = false;

  GameLink(String i_description, int i_id) {
    description = i_description;
    id = i_id;
    
    int wPosBegin = description.indexOf(":")+2;
    int wPosEnd = description.indexOf(",", wPosBegin);
    int bPosBegin = description.indexOf(":", wPosBegin)+2;
    int bPosEnd = description.indexOf(",", bPosBegin);
    if(bPosEnd == -1) bPosEnd = description.length();    
    pWhite = description.substring(wPosBegin, wPosEnd) + "_" + id;
    pBlack = description.substring(bPosBegin, bPosEnd) + "_" + id;
    
    if(pWhite.indexOf("None") != -1) pWhite = "zzz_" + pWhite;
    if(pBlack.indexOf("None") != -1) pBlack = "zzz_" + pBlack;
    
    pos = new PVector(-1, -1);
  }



  void draw() {
    fill(#DDFF1F);
    text(description, pos.x, pos.y);
    
    noFill();
    stroke(255, 0, 0);
    if(selected) rect(130, pos.y-11, width-130, pos.y+15);
    
  }


  void get_physical(int i_posx, int i_posy) {
    pos = new PVector(i_posx, i_posy);
  }


  boolean checkclick() {
    if (mouseX > 112 && mouseX < width-112) {
      if (mouseY > pos.y-11 && mouseY < pos.y + 15) 
        return true;
    }
    return false;
  }
}