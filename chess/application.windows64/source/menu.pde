class Menu {
  PImage background = find_referencedImage("study room");
  Button play;
  Button options;
  
  Menu() {
    play = new Button( new PVector(width/2, height/2-20), new PVector(200, 50), color(#FCB80A), "PLAY", true);
    options = new Button( new PVector(width/2, height/2+55), new PVector(200, 50), color(#FCB80A), "OPTIONS", true);
  }
  
  void draw() {
    image(background, width/2, height/2);
    strokeWeight(1);
    
    if(play.mouseOver()) play.col = color(#FAFF0F); else play.col = color(#FCB80A);
    if(options.mouseOver()) options.col = color(#FAFF0F); else options.col = color(#FCB80A);
    

    play.draw();
    options.draw();
    
  }
  
  
  void checkclick() {
    if(play.mouseOver()) {browser = new Serverbrowser(); game.state = SERVERBROWSER;}
    if(options.mouseOver()) game.state = OPTIONS;
  }
}