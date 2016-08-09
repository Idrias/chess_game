class Figure {
  int col;
  int type;
  int faction;
  boolean picked = false;
  boolean hasMoved = false; // TODO!
  PVector pos;
  PImage image;
  
  Figure(int i_col, int i_type, PVector i_pos) {
    col = i_col;
    type = i_type;
    pos = new PVector(i_pos.x, i_pos.y);
    
    String searchref = col == BLACK ? "black " : "white ";
    searchref += figTypeToString(type).toLowerCase();
    image = find_referencedImage(searchref);
    
    
    //if(image == null) image = find_referencedImage("missing no");
  }
  
  void draw(int drawX, int drawY) { faction = col == thisPlayerFaction ? FRIENDLY : HOSTILE; // DEBUG
    if(!picked) image(image, drawX, drawY);
    else image(image, mouseX, mouseY+20);
  }
  
  
  
  void draw() {}
}