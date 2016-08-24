class GameLink {

  String description;
  int id;
  PVector pos;
  boolean selected = false;

  GameLink(String i_description, int i_id) {
    description = i_description;
    id = i_id;
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