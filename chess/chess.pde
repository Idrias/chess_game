
void setup() {
  size(1000, 690); 
  frameRate(144);
  init_vars();
  rectMode(CORNERS);
  imageMode(CENTER);
  textAlign(CENTER, CENTER);
}


void draw() {
  input.check();
  net.comCheck();
  game.draw();
}