class Menu {
  PImage background = find_referencedImage("study room");
  Button play;
  Button options;
  
  Menu() {
    play = new Button( new PVector(width/2, height/2-20), new PVector(200, 50), color(#FCB80A), color(#FAFF0F), "PLAY", true);
    options = new Button( new PVector(width/2, height/2+55), new PVector(200, 50), color(#FCB80A), color(#FAFF0F), "OPTIONS", true);
  }
  
  void draw() {
    image(background, width/2, height/2);
    strokeWeight(1);
    
    play.draw();
    options.draw();
    
  }
  
  
  void checkclick() {
    if(play.mouseOver()) {browser = new Serverbrowser(); game.state = SERVERBROWSER;}
    if(options.mouseOver()) game.state = OPTIONS;
  }
}