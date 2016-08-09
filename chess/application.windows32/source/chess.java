import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class chess extends PApplet {

Networker net;

public void setup() {
   
  frameRate(144);
  init_vars();
  
  rectMode(CORNERS);
  imageMode(CENTER);
  textAlign(CENTER, CENTER);
  
  //fegame.state = CONNECTED;
}

public void draw() {
  input.check();
  if(net != null) net.comCheck();
  background(0xff452017);
  game.draw();
}
class Board {

  int xSize = 8;
  int ySize = 8;
  int DRAW_spaceX = 570;
  int DRAW_spaceY = 570;
  int DRAW_beginX = 60;
  int DRAW_beginY = 60;
  int whoseTurn = 0;
  
  String nameWHITE = "";
  String nameBLACK = "";

  // other
  boolean isAFigurePicked = false;

  Field[][] fields;
  PImage background;

  int DRAW_endX, DRAW_endY;
  float DRAW_fieldSizeX;
  float DRAW_fieldSizeY;

  Board() {
    //int[] metaData = parser.parseMeta(xmlLocation);
    //xSize = metaData[0];
    //ySize = metaData[1];
    //whoseTurn = metaData[2];
    DRAW_endX = DRAW_beginX + DRAW_spaceX;
    DRAW_endY = DRAW_beginY + DRAW_spaceY;
    DRAW_fieldSizeX = (DRAW_endX - DRAW_beginX) / xSize;
    DRAW_fieldSizeY = (DRAW_endY - DRAW_beginY) / ySize;

    fields = new Field[xSize][ySize];
    int col = WHITE;

    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        fields[x][y] = new Field();
        fields[x][y].col = col;
        fields[x][y].pos = new PVector(x, y);
        col = col == WHITE ? BLACK : WHITE;
      }
      col = col == WHITE ? BLACK : WHITE;
    }

    background = find_referencedImage("wooden background");
    

  }


  public void draw() {
    strokeWeight(2);

    // Draw background
    image(background, width/2, height/2);

    // Draw board
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        fields[x][y].draw( DRAW_beginX, DRAW_beginY );
      }
    }

    // Draw side
    stroke(255);
    line(DRAW_beginX * 2 + DRAW_spaceX, 0, DRAW_beginX * 2 + DRAW_spaceX, height);
    stroke(0);
    
    // Draw names
    String writeString = "";
    
    textSize(25);
    fill(WHITE);
    if(whoseTurn == WHITE) writeString = "-> ";
    writeString += nameWHITE;
    text(writeString, 346, height-30);
    fill(BLACK);
    writeString = "";
    if(whoseTurn == BLACK) writeString = "-> ";
    writeString += nameBLACK;
    text(writeString, 346, 30);
    textSize(15);
    
    // Draw INFO
    fill(WHITE);
    text("Game ID: " + browser.enterID.content, 845, 16);
    textSize(12);
    
    // Draw figures
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        fields[x][y].draw_figure();
      }
    }
  }


  public void checkclick() {
    // DEV
      if(whoseTurn != thisPlayerFaction) return;
    // DEV
    
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        Field f = fields[x][y];
        if (f.checkclick()) {

          if (f.figure != null && f.figure.faction == FRIENDLY) {

            if (f.highlightCode == NOT_HIGHLIGHTED && !isAFigurePicked) {
              isAFigurePicked = true;
              f.highlightCode = FRIENDLY_PICKED;
              f.figure.picked = true;

              calculateMoves(x, y);
            } else if (f.highlightCode == FRIENDLY_PICKED) {
              isAFigurePicked = false;
              f.figure.picked = false;

              for (int yII = 0; yII < ySize; yII++) {
                for (int xII = 0; xII < xSize; xII++) {
                  fields[xII][yII].highlightCode = NOT_HIGHLIGHTED;
                }
              }
            }
          }
          
          if(f.highlightCode == CAN_GO) {
            // request movement
            Field originField = null;
            
               for (int yII = 0; yII < ySize; yII++) {
                for (int xII = 0; xII < xSize; xII++) {
                  if(fields[xII][yII].highlightCode == FRIENDLY_PICKED)
                    originField = fields[xII][yII];
                }
              }
              
            if(originField == null) return;
            net.addMessage("MOVEMENT REQUEST", new String[]{""+originField.pos.x, ""+originField.pos.y, ""+x, ""+y});
            originField.figure.picked = false;
            isAFigurePicked = false; // TODO change
              for (int yII = 0; yII < ySize; yII++) {
                for (int xII = 0; xII < xSize; xII++) {
                  fields[xII][yII].highlightCode = NOT_HIGHLIGHTED;
                }
              }
          }
          
        }
      }
    }
  }
  
  
  
  
  
  
    public boolean isFriendly(int xcord, int ycord) {
    Figure f = game.board.fields[xcord][ycord].figure;
    if (f != null && f.faction == FRIENDLY) return true;
    return false;
  }

  public boolean isHostile(int xcord, int ycord) {
    Figure f = game.board.fields[xcord][ycord].figure;
    if (f != null && f.faction == HOSTILE) return true;
    return false;
  }



public void calculateMoves(int xout, int yout) {
  Figure f = game.board.fields[xout][yout].figure;
  // TODO IMPLEMENT BAUERNTAUSCH
  // TODO BAUER DARF (NUR) \u00dcBER KREUZ SCHLAGEN
  // TODO IMPLEMENT ROCHADE
  // TODO IMPLEMENT EN PASSANT
  switch(f.type) {
  case PAWN: 
    // Bewegung
    if (f.col == WHITE && yout - 1 > 0 && !isFriendly(xout, yout-1) && !isHostile(xout, yout-1)) fields[xout][yout-1].highlightCode = CAN_GO;
    if (f.col == WHITE && yout - 2 > 0 && !isFriendly(xout, yout-2) && !isFriendly(xout, yout-1) && !isHostile(xout, yout-1) && !isHostile(xout, yout-2) && !f.hasMoved) fields[xout][yout-2].highlightCode = CAN_GO;
    if (f.col == BLACK && yout + 1 < ySize && !isFriendly(xout, yout+1) && !isHostile(xout, yout+1)) fields[xout][yout+1].highlightCode = CAN_GO;
    if (f.col == BLACK && yout + 2 < ySize && !isFriendly(xout, yout+2) && !isFriendly(xout, yout+1) && !isHostile(xout, yout+1) && !isHostile(xout, yout+2) &&!f.hasMoved) fields[xout][yout+2].highlightCode = CAN_GO;
    
    // Schlagen "\u00fcber Kreuz"
    if (f.col == WHITE && yout - 1 > -1 && xout - 1 > -1 && isHostile(xout-1, yout-1)) fields[xout-1][yout-1].highlightCode = CAN_GO;
    if (f.col == WHITE && yout - 1 > -1 && xout + 1 < xSize && isHostile(xout+1, yout-1)) fields[xout+1][yout-1].highlightCode = CAN_GO;
    
    if (f.col == BLACK && yout + 1 < ySize && xout - 1 > -1 && isHostile(xout-1, yout+1)) fields[xout-1][yout+1].highlightCode = CAN_GO;
    if (f.col == BLACK && yout + 1 < ySize && xout + 1 < xSize && isHostile(xout+1, yout+1)) fields[xout+1][yout+1].highlightCode = CAN_GO;
    break;

  case TOWER:
    for (int x = xout+1; x < xSize; x++) {
      if (!isFriendly(x, yout)) {
        fields[x][yout].highlightCode = CAN_GO;
      } else break;

      if (isHostile(x, yout)) break;
    }
    for (int x = xout-1; x >= 0; x--) {
      if (!isFriendly(x, yout)) {
        fields[x][yout].highlightCode = CAN_GO;
      } else break;
      if (isHostile(x, yout)) break;
    }
    for (int y = yout+1; y < ySize; y++) {
      if (!isFriendly(xout, y)) {
        fields[xout][y].highlightCode = CAN_GO;
      } else break;
      if (isHostile(xout, y)) break;
    }
    for (int y = yout-1; y >= 0; y--) {
      if (!isFriendly(xout, y)) {
        fields[xout][y].highlightCode = CAN_GO;
      } else break;
      if (isHostile(xout, y)) break;
    }
    break;

  case HORSE:
    if (xout+2 < xSize) {
      if (yout+1 < ySize && !isFriendly(xout+2, yout+1)) fields[xout+2][yout+1].highlightCode = CAN_GO;
      if (yout-1 > -1 && !isFriendly(xout+2, yout-1)) fields[xout+2][yout-1].highlightCode = CAN_GO;
    }

    if (xout-2 > -1) {
      if (yout+1 < ySize && !isFriendly(xout-2, yout+1)) fields[xout-2][yout+1].highlightCode = CAN_GO;
      if (yout-1 > -1 && !isFriendly(xout-2, yout-1)) fields[xout-2][yout-1].highlightCode = CAN_GO;
    }

    if (yout-2 > -1) {
      if (xout+1 < xSize && !isFriendly(xout+1, yout-2)) fields[xout+1][yout-2].highlightCode = CAN_GO;
      if (xout-1 > -1 && !isFriendly(xout-1, yout-2)) fields[xout-1][yout-2].highlightCode = CAN_GO;
    }

    if (yout+2 < ySize) {
      if (xout+1 < xSize && !isFriendly(xout+1, yout+2)) fields[xout+1][yout+2].highlightCode = CAN_GO;
      if (xout-1 > -1 && !isFriendly(xout-1, yout+2)) fields[xout-1][yout+2].highlightCode = CAN_GO;
    }
    break;


  case BISHOP:
     for (int s = 1; true; s++) {
      int x = xout + s;
      int y = yout + s;

      if (x == xSize || y == ySize) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
  
    for (int s = -1; true; s--) {
      int x = xout + s;
      int y = yout + s;

      if (x < 0 || y < 0) break;
      if (x == xSize || y == ySize) continue;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
  
    for (int s = 1; true; s++) {
      int x = xout + s;
      int y = yout - s;

      if (x == xSize || y == ySize) break;
      if(x < 0 || y < 0) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
 
    for (int s = -1; true; s--) {
      int x = xout + s;
      int y = yout - s;

      if (x < 0 || y < 0) break;
      if(x == xSize || y == ySize) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    } break;


  case KING:
    if(xout+1 < xSize && yout+1 < ySize) if (!isFriendly(xout+1, yout+1)) fields[xout+1][yout+1].highlightCode = CAN_GO;
    if(yout+1 < ySize) if (!isFriendly(xout+0, yout+1)) fields[xout+0][yout+1].highlightCode = CAN_GO;
    if(xout-1 > -1 && yout+1 < ySize) if (!isFriendly(xout-1, yout+1)) fields[xout-1][yout+1].highlightCode = CAN_GO;
    if(xout+1 < xSize) if (!isFriendly(xout+1, yout+0)) fields[xout+1][yout+0].highlightCode = CAN_GO;
    if(xout+1 < xSize && yout-1 > -1) if (!isFriendly(xout+1, yout-1)) fields[xout+1][yout-1].highlightCode = CAN_GO;
    if(xout-1 > -1 && yout-1 > -1) if (!isFriendly(xout-1, yout-1)) fields[xout-1][yout-1].highlightCode = CAN_GO;
    if(xout-1 > -1) if (!isFriendly(xout-1, yout+0)) fields[xout-1][yout+0].highlightCode = CAN_GO;
    if(yout-1 > -1) if (!isFriendly(xout+0, yout-1)) fields[xout+0][yout-1].highlightCode = CAN_GO;
    break;



  case QUEEN: 
    for (int s = 1; true; s++) {
      int x = xout + s;
      int y = yout + s;

      if (x == xSize || y == ySize) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
  
    for (int s = -1; true; s--) {
      int x = xout + s;
      int y = yout + s;

      if (x < 0 || y < 0) break;
      if (x == xSize || y == ySize) continue;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
  
    for (int s = 1; true; s++) {
      int x = xout + s;
      int y = yout - s;

      if (x == xSize || y == ySize) break;
      if(x < 0 || y < 0) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }
 
    for (int s = -1; true; s--) {
      int x = xout + s;
      int y = yout - s;

      if (x < 0 || y < 0) break;
      if(x == xSize || y == ySize) break;
      if(isFriendly(x, y)) break;
      if (isHostile(x, y)) {
        fields[x][y].highlightCode = CAN_GO; 
        break;
      }
      fields[x][y].highlightCode = CAN_GO;
    }

    for (int x = xout+1; x < xSize; x++) {
      if (!isFriendly(x, yout)) {
        fields[x][yout].highlightCode = CAN_GO;
      } else break;

      if (isHostile(x, yout)) break;
    }
    for (int x = xout-1; x >= 0; x--) {
      if (!isFriendly(x, yout)) {
        fields[x][yout].highlightCode = CAN_GO;
      } else break;
      if (isHostile(x, yout)) break;
    }
    for (int y = yout+1; y < ySize; y++) {
      if (!isFriendly(xout, y)) {
        fields[xout][y].highlightCode = CAN_GO;
      } else break;
      if (isHostile(xout, y)) break;
    }
    for (int y = yout-1; y >= 0; y--) {
      if (!isFriendly(xout, y)) {
        fields[xout][y].highlightCode = CAN_GO;
      } else break;
      if (isHostile(xout, y)) break;
    }
    break;
  }
}

  
  
}
 
  




// TODO IMPLEMENT HAS MOVED IN XML
class Field {
  int col;
  PVector pos;
  Figure figure;
  int drawX = 0, drawY = 0;
  int highlightCode = NOT_HIGHLIGHTED;
  
  
  Field() {
  
  }


  public void draw( int startX, int startY ) {
    
    
    fill(col);
    
    if(highlightCode == CAN_GO) {
      fill(CAN_GO_COLOR);
      if(figure != null && figure.faction == HOSTILE) {
        fill(IN_DANGER_COLOR);
      }
    }
    
    else if(highlightCode == FRIENDLY_PICKED) fill(SELECTED_COLOR);

    
    drawX = PApplet.parseInt(startX + pos.x * game.board.DRAW_fieldSizeX);
    drawY = PApplet.parseInt(startY + pos.y * game.board.DRAW_fieldSizeY);
    
    rect(drawX, drawY, drawX+game.board.DRAW_fieldSizeX, drawY+game.board.DRAW_fieldSizeY);
  }
  
  public void draw_figure() {
      if(figure != null) {
      figure.draw( PApplet.parseInt(drawX + game.board.DRAW_fieldSizeX/2), PApplet.parseInt(drawY +game.board.DRAW_fieldSizeY/2) );
    }
  } 
  
  public boolean checkclick() {
    if(mouseX > drawX && mouseX < drawX + game.board.DRAW_fieldSizeX) {
      if(mouseY > drawY && mouseY < drawY + game.board.DRAW_fieldSizeY) {
        return true;
      }
    }
    return false;
  }
}
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
  
  public void draw(int drawX, int drawY) { faction = col == thisPlayerFaction ? FRIENDLY : HOSTILE; // DEBUG
    if(!picked) image(image, drawX, drawY);
    else image(image, mouseX, mouseY+20);
  }
  
  
  
  public void draw() {}
}
class Game {
  Board board;
  int state;
  
  Game() {
    state = MENU;
    board = new Board();
  }


  public void draw() {
    if(state == MENU) menu.draw();
    if(state == CONNECTED) board.draw();
    if(state == SERVERBROWSER) browser.draw();
  }

}
class ReferencedImage {
  PImage image;
  String reference;

  ReferencedImage(String p_path, String p_reference) {
    image = loadImage(p_path);
    reference = p_reference;
  }
}


public PImage find_referencedImage(String reference) {
  for (ReferencedImage refim : images) {
    if (refim.reference.equals(reference)) return refim.image;
  }
  return null;
}


public String figTypeToString(int figType) {
  switch (figType) {
    case PAWN: return "PAWN";
    case TOWER: return "TOWER";
    case HORSE: return "HORSE";
    case BISHOP: return "BISHOP";
    case QUEEN: return "QUEEN";
    case KING: return "KING";
    default: return null;
  }
}
class Button {
  PVector pos;
  PVector size;
  String text;
  boolean state = false;
  int col;
  
  Button(PVector p_pos, PVector p_size, int p_col, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    state = p_state;
  }
  
  public void draw() {
    rectMode(CENTER);
    fill(col);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  public boolean mouseOver() {
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      return true;
    }
    else return false;
  }
}

class InputHandler {
  boolean registeredMouseClick = false;
  
  public void check() {
    if(mousePressed && !registeredMouseClick) {
      registeredMouseClick = true;
    
      if(mouseButton == RIGHT) {net.close(); game.state = MENU; return;}
      
      switch(game.state) {
        case MENU: menu.checkclick(); break;
        case CONNECTED: game.board.checkclick(); break;
        case SERVERBROWSER: browser.checkclick(); break;
      }
      
    }
    
    else if(!mousePressed) registeredMouseClick = false;
  }
  

}


  public void keyPressed() {
  if(game.state == SERVERBROWSER) {
                //if(keyCode == TAB) for(int i = 0; i<browser.textboxes.size(); i++) if(browser.textboxes.get(i).active) {browser.textboxes.get(i).active = false; browser.textboxes.get((i+1)%browser.textboxes.size()).active = true;}
                if(key==TAB || key==ENTER || key==RETURN || key==ESC || key==DELETE || key==SHIFT || key==ALT || key==CODED) return;
                
                Textbox target = null;
                for(Textbox tb : browser.textboxes) if(tb.active) {target = tb;}
                if(target==null) return;
                
                if (key==BACKSPACE) {
                  if (target.content.length() > 0)
                    target.content = target.content.substring(0, target.content.length()-1);
                } else if ((isNum(key) || target.isAlphaAllowed) && target.content.length()+1 <= target.maxchars) target.content += key;
              }
}
class Menu {
  PImage background = find_referencedImage("study room");
  Button play;
  Button options;
  
  Menu() {
    play = new Button( new PVector(width/2, height/2-20), new PVector(200, 50), color(0xffFCB80A), "PLAY", true);
    options = new Button( new PVector(width/2, height/2+55), new PVector(200, 50), color(0xffFCB80A), "OPTIONS", true);
  }
  
  public void draw() {
    image(background, width/2, height/2);
    strokeWeight(1);
    
    if(play.mouseOver()) play.col = color(0xffFAFF0F); else play.col = color(0xffFCB80A);
    if(options.mouseOver()) options.col = color(0xffFAFF0F); else options.col = color(0xffFCB80A);
    

    play.draw();
    options.draw();
    
  }
  
  
  public void checkclick() {
    if(play.mouseOver()) {browser = new Serverbrowser(); game.state = SERVERBROWSER;}
    if(options.mouseOver()) game.state = OPTIONS;
  }
}


class Networker {
  Client client;
  String serverIP;
  int serverPORT;
  ArrayList<outMessage> outMSGS;
  String appendix = "";
  int lastState = NOTACTIVE;

  Networker(String i_serverIP, int i_serverPORT) {
    serverIP = i_serverIP;
    serverPORT = i_serverPORT;
    client = new Client(sketchRef, serverIP, serverPORT);
    outMSGS = new ArrayList<outMessage>();
  }

  public void close() {
    if(client.active()) 
      client.stop();
  }
  
  
  public void comCheck() {
    if(client.active()) {lastState = ACTIVE;}
    if(!client.active() && lastState == ACTIVE) {lastState = NOTACTIVE; game.state = MENU;}
    if(!client.active()) return;
    
    inCheck();
    outCheck();
  }

  public void addMessage(String command, String[] args) {
    String message = "$" + command + ";";
    
    for(int i=0; i < args.length; i++) {
      message  += args[i] + ";";
    }
    message += "&";
    
    //println(message);
    outMSGS.add(new outMessage(message));
  }

  public void interpret(String message) {
    //println(message);
    String command = message.substring(0, message.indexOf(";"));
    int lastSemi = message.indexOf(";");
    int nextSemi = message.indexOf(";", lastSemi+1);
    ArrayList<String> arguments = new ArrayList<String>();
    
    while(nextSemi != -1) {
      arguments.add( message.substring(lastSemi+1, nextSemi) ); 
      lastSemi = nextSemi;
      nextSemi = message.indexOf(";", lastSemi+1);
    }
    
    //println("COMMAND: " + command);
    //for(String arg : arguments) println("ARG: " + arg);
    
    
    //////////////////////////////
    // COMMAND EXECUTION
    //////////////////////////////
    
    if(command.equals("THX")) {
      String hash = arguments.get(0);
      for(outMessage msg : outMSGS) {
        if(PApplet.parseInt(hash) == msg.specialHash) {
          msg.hasBeenReceived = true;
          print("HAPPY HASH", hash);}
        else
          print(hash, "is not the needed hash", msg.specialHash);
      }
    }
    
    
    if(command.equals("CODE IS")) {
      browser.takeID(PApplet.parseInt(arguments.get(0)));
   }
 
    if(command.equals("YOU ARE")) {
      // WE GOT ACCEPTED!
      if(PApplet.parseInt(arguments.get(0))==WHITE) thisPlayerFaction = WHITE;
      else if(PApplet.parseInt(arguments.get(0))==BLACK) thisPlayerFaction = BLACK;
      
      game.board = new Board();
      game.state = CONNECTED;
    }
    
    if(command.equals("ADD FIGURE")) {
      Figure f = new Figure(PApplet.parseInt(arguments.get(0)), PApplet.parseInt(arguments.get(1)), new PVector(PApplet.parseInt(arguments.get(2)), PApplet.parseInt(arguments.get(3))));
      
      println(arguments.get(4));
      if(arguments.get(4).equals("True")) f.hasMoved = true;
      game.board.fields[PApplet.parseInt(f.pos.x)][PApplet.parseInt(f.pos.y)].figure = f;
  }
  
    if(command.equals("REMOVE FIGURE")) {
      game.board.fields[PApplet.parseInt(arguments.get(0))][PApplet.parseInt(arguments.get(1))].figure = null;
    }
  
    if(command.equals("TURN")) {
      game.board.whoseTurn = PApplet.parseInt(arguments.get(0));
    }
  
    
    if(command.equals("UI UPDATE")) {
      if(arguments.get(0).equals("NAME")) {
        if(PApplet.parseInt(arguments.get(1)) == WHITE) {
          game.board.nameWHITE = arguments.get(2);
        }
        
        else if(PApplet.parseInt(arguments.get(1)) == BLACK) {
          game.board.nameBLACK = arguments.get(2);
        }
      }
    }
    
 }



  public void inCheck() {
    String incoming = client.readString();
    if(incoming == null) return;
    
    ArrayList<String> inMSGS = new ArrayList<String>();
    String formingMSG = appendix;

    for (int i=0; i<incoming.length(); i++) {
      //println("we got in:", formingMSG);
      if (incoming.charAt(i) == '$') {
        formingMSG = "";
      } else if (incoming.charAt(i) == '&') {
        inMSGS.add(formingMSG);
        
      } else {
        formingMSG += incoming.charAt(i);
      }
    }

    appendix = formingMSG;


    /////////////////////////////////
    for (String msg : inMSGS) {
      interpret(msg);
    }
  }


  public void outCheck() {
    int timeNow = millis();
    for (outMessage msg : outMSGS) {
      if (!msg.hasBeenReceived && timeNow - msg.lastTime > SENDAGAINTHERESHOLD && !msg.devoverride) {
        client.write(msg.message);
        msg.lastTime = timeNow;
        msg.devoverride = true;
      }
    }
  }


  public void createGame(String gameFile) {
    println("CREATING GAME WITH " + gameFile);
    String[] lines = loadStrings(gameFile);

    /*
      Syntax
     $[message]&
     [message] = command;argument1;argument2;argument3;...;argumentN
     */

    String writeString = "$CREATE GAME;";

    for (int i=0; i < lines.length; i++) 
      writeString += lines[i] + ";";

    writeString += "&";
    outMSGS.add( new outMessage(writeString) );
  }


  
  public void joinGame(String id, String name) {
    addMessage("JOIN GAME", new String[]{id, str(preference), name});
  }
  
  

  public boolean active() {
    if (client.active()) {
      println(client, "is connected to", serverIP+":"+serverPORT);
      return true;
    }

    println(client, "is NOT ANYMORE connected to", serverIP+":"+serverPORT);
    return false;
  }
}





class outMessage {
  String message;
  int lastTime;
  int specialHash = 0;
  boolean hasBeenReceived = false;
  boolean devoverride = false;

  outMessage(String i_message) {
    message = i_message;
    for (int i=0; i<message.length(); i++) {
      specialHash += PApplet.parseInt(message.charAt(i));
    }
    lastTime = millis();
  }
}
class Serverbrowser {

  PImage background;
  Textbox enterID;
  Textbox enterName;
  Textbox enterServerIP;
  Textbox enterCreateServerIP;
  Textbox enterXMLloc;
  Textbox enterXMLname;
  Button enterGame;
  Button createGame;
  ArrayList<Textbox> textboxes;
  ArrayList<Button> buttons;
  boolean hasIDBeenReceived = false;
  int receivedID = 9999;

  Serverbrowser() {
    background = find_referencedImage("server room");
    enterID = new Textbox(135, 232, 130, 40, "1234");
    enterID.isAlphaAllowed = false;
    enterName = new Textbox(135, 320, 130, 40, "Magnus");
    
    enterServerIP = new Textbox(135, 408, 200, 40, IPPRESET);
    enterServerIP.maxchars = 20;
   
    String dp = dataPath("");
    dp = dp.substring(0, dp.length()-5);
    enterXMLloc = new Textbox(550, 232, 400, 40, dp+"\\assets\\xml\\");
    enterXMLloc.maxchars = 250;
    enterXMLname = new Textbox(550, 320, 400, 40, "default_board.xml");
    enterXMLname.maxchars = 60;
    enterCreateServerIP = new Textbox(550, 408, 400, 40, IPPRESET);
    enterCreateServerIP.maxchars = 20;
    
    
    enterGame = new Button( new PVector(width/2-260, height/2+280), new PVector(335, 39), color(0xffDDFF1F), "Join Game", true );
    createGame = new Button( new PVector(width/2+250, height/2+280), new PVector(335, 39), color(0xffDDFF1F), "Create Game", true );
     
     
    textboxes = new ArrayList<Textbox>();
    textboxes.add(enterID);
    textboxes.add(enterName);
    textboxes.add(enterServerIP);
    textboxes.add(enterXMLloc);
    textboxes.add(enterXMLname); 
    textboxes.add(enterCreateServerIP);
    
    buttons = new ArrayList<Button>();
    buttons.add(enterGame);
    buttons.add(createGame);
  }
  
  public void takeID(int id) {
    receivedID = id;
    hasIDBeenReceived = true;
    enterID.content = str(id);
  }

  public void draw() {
    if(enterServerIP.active) enterCreateServerIP.content = enterServerIP.content;
    if(enterCreateServerIP.active) enterServerIP.content = enterCreateServerIP.content;
    
    image(background, width/2, height/2);

    strokeWeight(5);
    stroke(0xffDDFF1F);  // #03FFF0
    line(width/2, 0, width/2, height);
    line(0, 150, width, 150);

    fill(0xffDDFF1F);
    textSize(50);
    text("JOIN GAME", width/4, 100);
    text("CREATE GAME", 3*width/4, 100);
    
    textSize(20);
    text("GAME ID:", 70, 250);
    text("PLAYER:", 70, 338);
    text("SERVER IP:", 70, 426);
    text("XML PATH:", 602, 217);
    text("XML FILEMAME:", 625, 305);
    text("SERVER IP:", 602, 393);
    
    if(hasIDBeenReceived) {text("GAME HAS BEEN CREATED!", 750, 539); text("ID: " + receivedID, 750, 563);}
    textSize(12);
    
    
    for(Textbox tb : textboxes) tb.draw();
    for(Button b : buttons) {
      if(b.mouseOver()) b.col = color(0xffEDFF7C);
      else b.col = color(0xffDDFF1F);
      
      b.draw();
    
    }
    
    stroke(0); 
    strokeWeight(1);
  }

  public void checkclick() { 

    for(Textbox tb : textboxes) tb.active = tb.mouseOver()? true : false;
    
    if(createGame.mouseOver()) {
      int sepindex = enterServerIP.content.indexOf(":");
      String ip = enterServerIP.content.substring(0, sepindex);
      String port = enterServerIP.content.substring(sepindex+1, enterServerIP.content.length());
      
      println(ip, port);
      net = new Networker(ip, PApplet.parseInt(port)); println("HEALTHY"); net.createGame(enterXMLloc.content+enterXMLname.content); println("STILL HEALTHY");
    }
    
    if(enterGame.mouseOver()) {
      int sepindex = enterServerIP.content.indexOf(":");
      String ip = enterServerIP.content.substring(0, sepindex);
      String port = enterServerIP.content.substring(sepindex+1, enterServerIP.content.length());
      
      println(ip, port);
      
      net = new Networker(ip, PApplet.parseInt(port));
      println("HEALTHY");
      net.joinGame(enterID.content, enterName.content);
    }
  }
}








public boolean isnan(float num) {
  return num != num;
}

public boolean isNum(char what) {
  switch (what) {
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
    case '0': return true;
    default: return false;
  }
}
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

  public void draw() {
    if (active) fill(255);
    else fill(160);

    if(correctionstate == 2 && millis()-correctiontimer > 1000) correctionstate = 0;
    
    if(correctionstate == 1 && millis()-correctiontimer < 1000) fill(0xff7FFF6C);
    else if(correctionstate == 2) fill(0xffFF4649);
    else if(correctionstate == 3) fill(0xffFFF752);
    
    rectMode(CORNER);
    rect(xpos, ypos, radx, rady);
    rectMode(CENTER);
    
    fill(0);
    displaytext = content;
    //if (content.length()>25) displaytext = content.substring(0, 25);
    

    text(displaytext, xpos+radx/2, ypos+rady/2);

  }
  
  public void correct(int state) {
    correctionstate = state;
    correctiontimer = millis();
  }
  
  public boolean mouseOver() {
    if (mouseX > xpos && mouseX < xpos+radx) {
      if (mouseY > ypos && mouseY < ypos+rady) {
        return true;
      }
    }
    return false;
  }
}
/* INTEGERS */
final int BLACK = 180;
final int WHITE = 255;
final int PAWN = 2;
final int TOWER = 3;
final int BISHOP = 4;
final int HORSE = 5;
final int QUEEN = 6;
final int KING = 7;
final int UNDEFINED = 999;
final int CONNECTED = 1;
final int OPTIONS = 4;
final int WAITING = 0;
final int MENU = -1;
final int SERVERBROWSER = 3;
final int GAMECREATION = 4;
final int NOT_HIGHLIGHTED = 0;
final int FRIENDLY_PICKED = 1;
final int CAN_GO = 2;
final int FRIENDLY = 42; 
final int HOSTILE = 666;
final int SENDAGAINTHERESHOLD = 0;
final int ACTIVE = 1;
final int NOTACTIVE = 2;
/* END OF INTEGERS */

// DEV - HAS TO CHANGE!!!
int thisPlayerFaction = WHITE;
String xmlLocation = "/assets/xml/default_board.xml";
chess sketchRef = this;
int preference = WHITE;
String IPPRESET = "127.0.0.1:6877";

// Colors
int CAN_GO_COLOR = 0xff91FF81;
int IN_DANGER_COLOR = 0xffFF4646;
int SELECTED_COLOR = 0xffFBFF46;
// End of colors

/* OBJECTS */
Game game;
Menu menu;
Serverbrowser browser;
xml_parser parser;
InputHandler input;
ArrayList<ReferencedImage> images;
/* END OF OBJECTS */





public void init_vars() {
  parser = new xml_parser();
  images = new ArrayList<ReferencedImage>();
  load_images();
  
  input = new InputHandler();
  
  game = new Game();
  menu = new Menu();
  browser = new Serverbrowser();

  try {
    for (Figure figure : parser.parseFigures(xmlLocation)) {
      game.board.fields[PApplet.parseInt(figure.pos.x)][PApplet.parseInt(figure.pos.y)].figure = figure;
    }
  }
  catch (NullPointerException ThisWasNotATriumphGladOS) {
    println("Dis is no gud...");
  }
}



public void load_images() {
  // Backgrounds
    images.add(new ReferencedImage("/assets/background/wood-texture.jpg", "wooden background"));
    images.add(new ReferencedImage("/assets/background/studyroom.jpg", "study room"));
    images.add(new ReferencedImage("/assets/background/server.jpg", "server room"));
  
  // Figures
    // White
      images.add(new ReferencedImage("/assets/figures/white/pawn.png", "white pawn"));
      images.add(new ReferencedImage("/assets/figures/white/tower.png", "white tower"));
      images.add(new ReferencedImage("/assets/figures/white/horse.png", "white horse"));
      images.add(new ReferencedImage("/assets/figures/white/bishop.png", "white bishop"));
      images.add(new ReferencedImage("/assets/figures/white/queen.png", "white queen"));
      images.add(new ReferencedImage("/assets/figures/white/king.png", "white king"));
      
    // Black
      images.add(new ReferencedImage("/assets/figures/black/pawn.png", "black pawn"));
      images.add(new ReferencedImage("/assets/figures/black/tower.png", "black tower"));
      images.add(new ReferencedImage("/assets/figures/black/horse.png", "black horse"));
      images.add(new ReferencedImage("/assets/figures/black/bishop.png", "black bishop"));
      images.add(new ReferencedImage("/assets/figures/black/queen.png", "black queen"));
      images.add(new ReferencedImage("/assets/figures/black/king.png", "black king"));
      
   // Misc
      images.add(new ReferencedImage("/assets/figures/missing no.png", "missing no"));
}
class xml_parser {
  XML xml;
  
  public int[] parseMeta(String fileLocation) {
  xml = loadXML(fileLocation);
  XML[] XMLmeta = xml.getChildren("meta");
  
  XML sizeX = XMLmeta[0].getChildren("sizeX")[0];
  XML sizeY = XMLmeta[0].getChildren("sizeY")[0];
  XML whoseTurn = XMLmeta[0].getChildren("turn")[0];

  int i_sizeX = PApplet.parseInt(sizeX.getContent());
  int i_sizeY = PApplet.parseInt(sizeY.getContent());
  int i_turn = whoseTurn.getContent().equals("white") ? WHITE : BLACK;

  return new int[]{i_sizeX, i_sizeY, i_turn};
  }
  
  public ArrayList<Figure> parseFigures(String fileLocation) {
    try {
      xml = loadXML(fileLocation);
    }
    catch(NullPointerException e) {
      println("WARNING: XML FILE NOT FOUND: " + fileLocation);
      return null;
    }

    XML[] XMLfigures = xml.getChildren("figures");
    XMLfigures = XMLfigures[0].getChildren("figure");
    
    ArrayList<Figure> figures = new ArrayList<Figure>();

    for (int i = 0; i < XMLfigures.length; i++) {
      XML f = XMLfigures[i];

      String cString = f.getString("color");
      int col = cString.equals("white") ? WHITE : cString.equals("black") ? BLACK : UNDEFINED;

      int figuretype;

      switch(f.getContent()) {
      case "Pawn": 
        figuretype = PAWN; 
        break;
      case "Tower": 
        figuretype = TOWER; 
        break;
      case "Horse": 
        figuretype = HORSE; 
        break;
      case "Bishop": 
        figuretype = BISHOP; 
        break;
      case "Queen": 
        figuretype = QUEEN; 
        break;
      case "King": 
        figuretype = KING; 
        break;
      default: 
        figuretype = UNDEFINED; 
        break;
      }
      
      // TODO change 8 
      int figureY = 8 - (f.getInt("yloc"));
      int figureX;
      //int figureX = unhex(f.getString("xloc"));
      switch(f.getString("xloc")) {
        case "a": figureX = 0; break;
        case "b": figureX = 1; break;
        case "c": figureX = 2; break;
        case "d": figureX = 3; break;
        case "e": figureX = 4; break;
        case "f": figureX = 5; break;
        case "g": figureX = 6; break;
        case "h": figureX = 7; break;
        
        case "i": figureX = 8; break;
        case "j": figureX = 9; break;
        case "k": figureX = 10; break;
        case "l": figureX = 11; break;
        case "m": figureX = 12; break;
        case "n": figureX = 13; break;
        case "o": figureX = 14; break;
        case "p": figureX = 15; break;
        
        case "q": figureX = 16; break;
        case "r": figureX = 17; break;
        case "s": figureX = 18; break;
        case "t": figureX = 19; break;
        case "u": figureX = 20; break;
        case "v": figureX = 21; break;
        case "w": figureX = 22; break;
        case "x": figureX = 23; break;
        case "y": figureX = 24; break;
        case "z": figureX = 25; break;
        default: continue;
      }

      Figure figure = new Figure( col, figuretype, new PVector(figureX, figureY));
      figures.add(figure);
    }

    return figures.size() > 0 ? figures : null;
  }
}
  public void settings() {  size(1000, 690); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "chess" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
