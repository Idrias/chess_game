Networker net;

void setup() {
  size(1000, 690); 
  frameRate(144);
  init_vars();
  
  rectMode(CORNERS);
  imageMode(CENTER);
  textAlign(CENTER, CENTER);
  
  //fegame.state = CONNECTED;
}

void draw() {
  input.check();
  if(net != null) net.comCheck();
  background(#452017);
  game.draw();
}