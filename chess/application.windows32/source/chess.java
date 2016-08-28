import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Arrays; 
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



public void setup() {
   
  frameRate(144);
  init_vars();
  rectMode(CORNERS);
  imageMode(CENTER);
  textAlign(CENTER, CENTER);
}


public void draw() {
  input.check();
  net.comCheck();
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
    if(whoseTurn == WHITE) {writeString = "-> ";
    writeString += nameWHITE + " <-";}
    else writeString = nameWHITE;
    text(writeString, 346, height-30);
    fill(BLACK);
    writeString = "";
    if(whoseTurn == BLACK) {writeString = "-> ";
    writeString += nameBLACK + " <-";}
    else writeString = nameBLACK;
    text(writeString, 346, 30);
    textSize(15);
    
    // Draw INFO
    fill(WHITE);
    text("Game ID: " + "42", 845, 16);
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
    
    fill(0);
    text(PApplet.parseInt(pos.x) +" "+ PApplet.parseInt(pos.y), drawX+game.board.DRAW_fieldSizeX / 2, drawY+game.board.DRAW_fieldSizeY-10);
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
    image(image, drawX, drawY);
  }
  
 
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



  public void draw() {
    fill(0xffDDFF1F);
    text(description, pos.x, pos.y);
    
    noFill();
    stroke(255, 0, 0);
    if(selected) rect(130, pos.y-11, width-130, pos.y+15);
    
  }


  public void get_physical(int i_posx, int i_posy) {
    pos = new PVector(i_posx, i_posy);
  }


  public boolean checkclick() {
    if (mouseX > 112 && mouseX < width-112) {
      if (mouseY > pos.y-11 && mouseY < pos.y + 15) 
        return true;
    }
    return false;
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
  int col2nd;
  int drawcol;
  int forMode = -1;
  
  Button(PVector p_pos, PVector p_size, int p_col, int p_col2nd, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    drawcol = col;
    col2nd = p_col2nd;
    state = p_state;
  }
  
  public void draw() {
    mouseOver();
    rectMode(CENTER);
    fill(drawcol);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  public boolean mouseOver() {
    if(!state) {drawcol = 100; return false;}
    
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      drawcol = col2nd;
      return true;
    }
    drawcol = col;
    return false;
  }
}

public void openXMLSelector() {
  String dp = dataPath("");
  dp = dp.substring(0, dp.length()-5);
  dp += "\\assets\\xml\\default_board.xml";
  File f = new File(dp);
  selectInput("Choose XML file you wish to upload to the server.", "selectdone", f);
}


public void selectdone(File selection) {
  if (selection == null) return; // User aborted
  
  String path = selection.getAbsolutePath();
  browser.pathToXML = path;
  
  String filename = path.substring(path.lastIndexOf("\\")+1, path.length());
  browser.enterXMLname.content = ".../" + filename;
}
class InputHandler {
  boolean registeredMouseClick = false;

  public void check() {
    if (mousePressed && !registeredMouseClick) {
      registeredMouseClick = true;


      switch(game.state) {
      case MENU: 
        menu.checkclick(); 
        break;
      case CONNECTED: 
        game.board.checkclick(); 
        break;
      case SERVERBROWSER: 
        browser.checkclick(); 
        break;
      }
    } else if (!mousePressed) registeredMouseClick = false;
  }
}



public void mouseWheel(MouseEvent e) {
  if(game.state == SERVERBROWSER) browser.startOfScope += e.getCount();

}



public void keyPressed() {
  if (key == ESC)
  {
    if (net!=null) net.restart();
    net.addMessage("LIST GAMES", new String[]{});
    game.state = SERVERBROWSER;
    key='0'; 
    return;
  }

  if (game.state == SERVERBROWSER) {
    //if(keyCode == TAB) for(int i = 0; i<browser.textboxes.size(); i++) if(browser.textboxes.get(i).active) {browser.textboxes.get(i).active = false; browser.textboxes.get((i+1)%browser.textboxes.size()).active = true;}

    if (keyCode == ENTER) {
      establishGamebrowser();
    }

    if (key==TAB || key==ENTER || key==RETURN || key==ESC || key==DELETE || key==SHIFT || key==ALT || key==CODED) return;

    Textbox target = null;
    for (Textbox tb : browser.textboxes) if (tb.active) {
      target = tb;
    }
    if (target==null) return;

    if (key==BACKSPACE) {
      if (target.content.length() > 0)
        target.content = target.content.substring(0, target.content.length()-1);
    } else if ((isNum(key) || target.isAlphaAllowed) && target.content.length()+1 <= target.maxchars) target.content += key;
  }
}




public void establishGamebrowser() {
  for (Textbox tb : browser.textboxes) {
    if (tb.active && tb == browser.enterServerIP) {

      int sepindex = tb.content.indexOf(":");
      String ip = tb.content.substring(0, sepindex);
      String port = tb.content.substring(sepindex+1, tb.content.length());

      if (ip!=null && port!=null && !ip.equals("") && !port.equals("")) {
        browser.glinks = new ArrayList<GameLink>();
        
        net = new Networker(ip, PApplet.parseInt(port));
        net.start();
        net.addMessage("LIST GAMES", new String[]{});
        
        
      }
      tb.active = false;
    }
  }
}
class LightSwitch {
  PVector pos;
  PVector size;
  
  String textA;
  String textB;
  
  boolean state = false;
  
  int colA;
  int col2ndA;
  int colB;
  int col2ndB;
  
  int drawcol;
  
  
  LightSwitch(PVector p_pos, PVector p_size, int p_colA, int p_col2ndA, String p_textA, int p_colB, int p_col2ndB, String p_textB, boolean p_state) {
    
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
  
  
  public void draw() {
    mouseOver();
    rectMode(CENTER);
    fill(drawcol);
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(state ? colB : colA);
    text(state ? textA : textB, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  public void press() {
    state = !state;
  }
  
  public boolean mouseOver() {
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      
      drawcol = state ? col2ndA : col2ndB;
      return true;
    }
    
    drawcol = state? colA : colB;
    return false;
  }
}
class Menu {
  PImage background = find_referencedImage("study room");
  Button play;
  Button options;
  
  Menu() {
    play = new Button( new PVector(width/2, height/2-20), new PVector(200, 50), color(0xffFCB80A), color(0xffFAFF0F), "PLAY", true);
    options = new Button( new PVector(width/2, height/2+55), new PVector(200, 50), color(0xffFCB80A), color(0xffFAFF0F), "OPTIONS", true);
  }
  
  public void draw() {
    image(background, width/2, height/2);
    strokeWeight(1);
    
    play.draw();
    options.draw();
    
  }
  
  
  public void checkclick() {
    if(play.mouseOver()) {browser = new Serverbrowser(); game.state = SERVERBROWSER;}
    if(options.mouseOver()) game.state = OPTIONS;
  }
}
class ModeSelector {
  
  PVector pos;
  PVector size;
  String text;
  boolean state = false;
  boolean changedFlag = false;
  int col;
  int col2nd;
  int drawcol;
  ModeSelector partner;
  
  ModeSelector(PVector p_pos, PVector p_size, int p_col, int p_col2nd, String p_text, boolean p_state) {
    pos = p_pos.copy();
    size = p_size.copy();
    text = p_text;
    col = p_col;
    drawcol = col;
    col2nd = p_col2nd;
    state = p_state;
  }
  
  
  public void set_partner(ModeSelector p_partner) {
    partner = p_partner;
  }
  
  public void draw() { 
    boolean oldState = state;
    mouseOver();
    
    if(oldState != state) {
      // State has changed!
       changedFlag = true;
    }
    
    else changedFlag = false;
    
    rectMode(CENTER);
    fill(drawcol);
    
    if(!state) fill(0xffC9C9C9); 
    
    rect(pos.x, pos.y, size.x, size.y, 10);
    fill(0);
    text(text, pos.x, pos.y);
    rectMode(CORNERS);
  }
  
 
  
  public boolean mouseOver() {    
    if(mouseX >= pos.x - size.x/2 && mouseX <= pos.x + size.x/2 && mouseY >= pos.y - size.y/2 && mouseY <= pos.y + size.y/2) {
      drawcol = col2nd;
      partner.state = false;
      state = true;
      return true;
    }
    drawcol = col;
    return false;
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
    outMSGS = new ArrayList<outMessage>();
  }

  public void start() {
    client = new Client(sketchRef, serverIP, serverPORT);
  }
    


  public void close() {
    if (client !=  null && client.active()) 
      client.stop();
  }
  
  
  public void restart() {
    if(client == null) return;
    client.stop();
    client = new Client(sketchRef, serverIP, serverPORT);
  }


  public void comCheck() {
    if(client == null) return;
    
    if (client.active()) {
      lastState = ACTIVE;
    }
    
    if (!client.active() && lastState == ACTIVE) {
      lastState = NOTACTIVE; 
      game.state = SERVERBROWSER; 
    }
    
    if (!client.active()) return;

    inCheck();
    outCheck();
  }

  public void addMessage(String command, String[] args) {
    String message = "$" + command + ";";

    for (int i=0; i < args.length; i++) {
      message  += args[i] + ";";
    }
    message += "&";


    outMSGS.add(new outMessage(message));
  }

  public void interpret(String message) {
    //println(message);
    String command = message.substring(0, message.indexOf(";"));
    int lastSemi = message.indexOf(";");
    int nextSemi = message.indexOf(";", lastSemi+1);
    ArrayList<String> arguments = new ArrayList<String>();

    while (nextSemi != -1) {
      arguments.add( message.substring(lastSemi+1, nextSemi) ); 
      lastSemi = nextSemi;
      nextSemi = message.indexOf(";", lastSemi+1);
    }

    //println("COMMAND: " + command);
    //for(String arg : arguments) println("ARG: " + arg);


    //////////////////////////////
    // COMMAND EXECUTION
    //////////////////////////////

    if (command.equals("THX")) {
      String hash = arguments.get(0);
      for (outMessage msg : outMSGS) {
        if (PApplet.parseInt(hash) == msg.specialHash) {
          msg.hasBeenReceived = true;
          print("HAPPY HASH", hash);
        } else
          print(hash, "is not the needed hash", msg.specialHash);
      }
    }



    if (command.equals("YOU ARE")) {
      // WE GOT ACCEPTED!
      if (PApplet.parseInt(arguments.get(0))==WHITE) thisPlayerFaction = WHITE;
      else if (PApplet.parseInt(arguments.get(0))==BLACK) thisPlayerFaction = BLACK;

      game.board = new Board();
      game.state = CONNECTED;
    }

    if (command.equals("ADD FIGURE")) {
      Figure f = new Figure(PApplet.parseInt(arguments.get(0)), PApplet.parseInt(arguments.get(1)), new PVector(PApplet.parseInt(arguments.get(2)), PApplet.parseInt(arguments.get(3))));

      if (arguments.get(4).equals("True")) f.hasMoved = true;
      game.board.fields[PApplet.parseInt(f.pos.x)][PApplet.parseInt(f.pos.y)].figure = f;
    }

    if (command.equals("REMOVE FIGURE")) {
      game.board.fields[PApplet.parseInt(arguments.get(0))][PApplet.parseInt(arguments.get(1))].figure = null;
    }

    if (command.equals("TURN")) {
      game.board.whoseTurn = PApplet.parseInt(arguments.get(0));
    }
    
    if (command.equals("CODE IS")) {
      browser.lastID = PApplet.parseInt(arguments.get(0));
      browser.nextCreationPossibility = millis() + PApplet.parseInt(arguments.get(1))*1000;
    }
    
    if (command.equals("GAME")) {
      int linkID = PApplet.parseInt(arguments.get(0));
      
      for(int i = 0; i < browser.glinks.size(); i++) {
        GameLink q = browser.glinks.get(i);
        if(q.id == linkID) browser.glinks.remove(i);
      }
      
      String linkString = "Game No. " + arguments.get(0) + ", ";
             linkString += "WHITE: " + arguments.get(1) + ", ";
             linkString += "BLACK: " + arguments.get(2);
      
      browser.glinks.add( new GameLink(linkString, linkID)  );
      browser.sortGameLinks();
    }


    if (command.equals("UI UPDATE")) {
      if (arguments.get(0).equals("NAME")) {
        if (PApplet.parseInt(arguments.get(1)) == WHITE) {
          game.board.nameWHITE = arguments.get(2);
        } else if (PApplet.parseInt(arguments.get(1)) == BLACK) {
          game.board.nameBLACK = arguments.get(2);
        }
      }
    }
    
    
    if(command.equals("CREATION ERROR")) {
      println(arguments.get(0), arguments.get(1));
    }
    
    println(command);
    if(command.equals("JOIN REJECTED")) {
      if(arguments.get(0).equals("WRONG PASSWORD")) {browser.enterPassword.correct(2);}
    }
    
    if(command.equals("GAME REMOVED")) {
      for(int i=0; i<browser.glinks.size(); i++) {
        if(browser.glinks.get(i).id == PApplet.parseInt(arguments.get(0))) {
          browser.glinks.remove(i);
          i=0;
        }
      }
    }
  }



  public void inCheck() {
    String incoming = client.readString();
    if (incoming == null) return;

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


  public void createGame(String gameFile, String pwd) {
    if(!active()) return;
    
    println("CREATING GAME WITH " + gameFile);
    String[] lines = loadStrings(gameFile);

    /*
      Syntax
     $[message]&
     [message] = command;argument1;argument2;argument3;...;argumentN
     */

    String writeString = "$CREATE GAME;"+pwd+";";

    for (int i=0; i < lines.length; i++) 
      writeString += lines[i] + ";";

    writeString += "&";
    outMSGS.add( new outMessage(writeString) );
  }



  public void joinGame(String id, String name, String password) {
    if(!active()) return;
    if(name.equals("")) {browser.enterName.correct(2); return;}
    addMessage("JOIN GAME", new String[]{id, str(preference), name, password});
  }



  public boolean active() {
    if(client == null) return false;
    
    if (client.active()) {
      return true;
    }
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
  Textbox enterName;
  Textbox enterServerIP;
  Textbox enterXMLname;
  Textbox enterPassword;
  Textbox enterPasswordCreate;

  Button joinGame;
  Button createGame;
  Button selectFile;
  Button scrollUp;
  Button scrollDown;

  LightSwitch prefSwitch;

  ModeSelector selJoin;
  ModeSelector selCreate;
  ModeSelector selSortID;
  ModeSelector selSortName;

  ArrayList<Textbox> textboxes;
  ArrayList<Button> buttons;
  ArrayList<GameLink> glinks;
  ArrayList<ModeSelector> mselectors;

  String pathToXML;
  String defaultFile = "default_board.xml";

  int mode = UNDEFINED;  
  int lastID = -1;
  int nextCreationPossibility = 0;

  int startOfScope = 0;

  Serverbrowser() {
    // Find background
    background = find_referencedImage("server room");
    init_ui();


    glinks = new ArrayList<GameLink>();
  }



  public void init_ui() {
    // Find default chess file
    String dp = dataPath("");
    dp = dp.substring(0, dp.length()-5) + "\\assets\\xml"; 

    pathToXML = dp+"\\"+defaultFile;



    //** BEGIN OF TEXTBOXES
    textboxes = new ArrayList<Textbox>();

    // Server IP Box
    enterServerIP = new Textbox(696, 36, 192, 25, IPPRESET);
    enterServerIP.maxchars = 20;
    enterServerIP.forMode = ALL;
    textboxes.add(enterServerIP);

    // Player name Box
    enterName = new Textbox(226, 450, 255, 33, "");
    enterName.maxchars = 20;
    enterName.forMode = JOIN;
    textboxes.add(enterName);

    // XML File Box
    enterXMLname = new Textbox(226, 450, 255, 33, ".../"+defaultFile);
    enterXMLname.maxchars = 60;
    enterXMLname.forMode = CREATE;
    textboxes.add(enterXMLname);

    // Password for client connect
    enterPassword = new Textbox(226, 559, 255, 33, "");
    enterPassword.maxchars = 20;
    enterPassword.isContentSecret = true;
    enterPassword.forMode = JOIN;
    textboxes.add(enterPassword);

    // Password for server creation
    enterPasswordCreate = new Textbox(226, 559, 255, 33, "");
    enterPasswordCreate.maxchars = 20;
    enterPasswordCreate.isContentSecret = true;
    enterPasswordCreate.forMode = CREATE;
    textboxes.add(enterPasswordCreate);
    //** END OF TEXTBOXES


    //** BEGIN OF BUTTONS
    buttons = new ArrayList<Button>();

    // Join Button
    joinGame = new Button( new PVector(width/2+191, height/2+230), new PVector(257, 40), color(0xffDDFF1F), color(0xffEDFF7C), "Join Game", true);
    joinGame.forMode = JOIN;
    buttons.add(joinGame);

    // Create Button
    createGame = new Button( new PVector(width/2+191, height/2+230), new PVector(257, 40), color(0xffDDFF1F), color(0xffEDFF7C), "Create Game", true);
    createGame.forMode = CREATE;
    buttons.add(createGame);

    // File select Button
    selectFile = new Button( new PVector(423, 504), new PVector(120, 25), color(0xffDDFF1F), color(0xffEDFF7C), "Select File", true);
    selectFile.forMode = CREATE;
    buttons.add(selectFile);

    // Scroll Buttons
    scrollUp = new Button( new PVector(899, 127), new PVector(20, 128), color(0xffDDFF1F), color(0xffEDFF7C), "\u028c", true);
    scrollDown = new Button( new PVector(899, 256), new PVector(20, 128), color(0xffDDFF1F), color(0xffEDFF7C), "v", true);
    scrollUp.forMode = ALL;
    scrollDown.forMode = ALL;
    buttons.add(scrollUp);
    buttons.add(scrollDown);
    //** END OF BUTTONS

    prefSwitch = new LightSwitch(new PVector(width/2+255, height/2+121), new PVector(width/2-348, height/2+-310), 255, 200, "White", 0, 55, "Black", true);

    //** MODE SELECTORS
    mselectors = new ArrayList<ModeSelector>();

    selJoin = new ModeSelector(new PVector(435, 360), new PVector(130, 35), color(0xffDDFF1F), color(0xffDDFF1F), "Join Game", true);
    selCreate = new ModeSelector( new PVector(565, 360), new PVector(130, 35), color(0xffDDFF1F), color(0xffDDFF1F), "Create Game", false);
    selJoin.set_partner(selCreate);
    selCreate.set_partner(selJoin);
    mselectors.add(selJoin);
    mselectors.add(selCreate);

    selSortID = new ModeSelector(new PVector(95, 100), new PVector(35, 30), color(0xffDDFF1F), color(0xffDDFF1F), "ID", true);
    selSortName = new ModeSelector( new PVector(95, 131), new PVector(35, 30), color(0xffDDFF1F), color(0xffDDFF1F), "ABC", false);
    selSortID.set_partner(selSortName);
    selSortName.set_partner(selSortID);
    mselectors.add(selSortID);
    mselectors.add(selSortName);
    //** END OF MODE SELECTORS
  }



  public void draw() {
    enterName.content = str(millis());
    if (selJoin.state && !selCreate.state) mode = JOIN;
    else if (selCreate.state && !selJoin.state) mode = CREATE;
    else mode = UNDEFINED;

    // Background Image
    image(background, width/2, height/2);

    // Game List
    strokeWeight(2);

    fill(0xffDDFF1F);

    if (startOfScope < 0) startOfScope = 0;
    else if (startOfScope > glinks.size()) startOfScope = glinks.size();

    textSize(50);
    if (startOfScope > 0) text("...", width/2, 60);
    if (startOfScope + 8 < glinks.size()) text("...", width/2, (75+25*(8+1)-15));
    textSize(18);


    for (int i = startOfScope; i < glinks.size() && i < startOfScope+8; i++) {
      glinks.get(i).get_physical(width/2, 75+25*(i-startOfScope+1)+2);
      glinks.get(i).draw();
    }

    if (net.active()) {
      stroke(0xff0EE830); 
      fill(0xff0EE830);
    } else {
      stroke(0xffFA5103); 
      fill(0xffFA5103);
      if(glinks.size() != 0) glinks = new ArrayList<GameLink>();
      lastID = -1;
    }




    //fill(#0EE830);

    textSize(20);
    text("Active Games", 180, 47);

    if (net.active()) text("(Connected)", 318, 47);
    else text("(Disconnected)", 331, 47);


    noFill();
    rect(112, 62, width-112, 319);



    //rect(113, 63, width-113, 318);

    fill(0xffDDFF1F);
    text("Server IP:", 641, 47);

    // Mode JOIN
    if (mode == JOIN) {
      text("Player:", 167, 462);
      text("Preference:", 602, 462);
      text("Password:", 167, 572);
      textSize(12);
      prefSwitch.draw();
    }

    // Mode CREATE
    if (mode == CREATE) {
      text("XML file:", 167, 462);
      text("Password:", 167, 572);

      if (lastID != -1) {
        textSize(16);
        text("Game created on server " + net.serverIP + "!", 695, 460);
        textSize(16);
        text("(ID: " + lastID+")", 690, 481);
        textSize(12);
      }
    }
    
    float timeLeft = PApplet.parseFloat(nextCreationPossibility) - PApplet.parseFloat(millis());
    timeLeft /= 100;
    timeLeft = PApplet.parseFloat(round(timeLeft))/10;
    
    if(nextCreationPossibility > millis()) {createGame.text = ""+timeLeft; createGame.state = false;}
    else {createGame.text = "Create Game"; createGame.state = true;}
    
    textSize(12);

    for (Textbox tb : textboxes) if (tb.forMode == mode || tb.forMode == ALL) tb.draw();
    for (Button b : buttons) if (b.forMode == mode || b.forMode == ALL) b.draw();
    for (ModeSelector m : mselectors) m.draw();

    stroke(0); 
    strokeWeight(1);


    if (selSortID.changedFlag || selSortName.changedFlag) sortGameLinks();
  }


  public void sortGameLinks() {

    ArrayList<GameLink> sortedLinks = new ArrayList<GameLink>();

    if (selSortID.state) {
      // Sort by ID
      while (glinks.size() > 0) {
        int smallestNum = -1;
        GameLink smallestLink = null;
        for (int i=0; i<glinks.size(); i++) {
          if (glinks.get(i).id < smallestNum || smallestNum == -1) {
            smallestLink = glinks.get(i);
            smallestNum = smallestLink.id;
            i = 0;
          }
        }
        sortedLinks.add(smallestLink);
        for (int i=0; i<glinks.size(); i++) if (glinks.get(i) == smallestLink) {
          glinks.remove(i);
        }
      }
    }
    
    
    else {
      // Sort by name
      String[] strings = new String[ glinks.size() ];
      for(int i=0; i<glinks.size(); i++) strings[i] = glinks.get(i).pWhite + glinks.get(i).pBlack;
      Arrays.sort(strings);
      
      for(int i=0; i<strings.length; i++) {
        for(GameLink g : glinks) if((g.pWhite+g.pBlack).equals(strings[i])) {sortedLinks.add(g);}
      }
    }
    
    
    glinks = sortedLinks;
  }


  public void checkclick() { 

    boolean enterServerIP_state = enterServerIP.active;

    for (Textbox tb : textboxes) tb.active = tb.mouseOver() && tb.forMode == mode || tb.forMode == ALL && tb.mouseOver() ? true : false;

    if (enterServerIP_state == true && enterServerIP.active == false) {
      enterServerIP.active = true; 
      establishGamebrowser();
    }


    for (GameLink g : glinks) {
      if (g.checkclick()) {
        g.selected = !g.selected;
        for (int i=0; i<glinks.size(); i++) if (glinks.get(i) != g) glinks.get(i).selected = false;
      }
    }



    if (createGame.mouseOver() && mode == CREATE) {
      net.createGame(pathToXML, enterPasswordCreate.content);
    }

    if (joinGame.mouseOver() && mode == JOIN) {
      GameLink g = null;
      for (GameLink q : glinks) if (q.selected) g = q; 
      if (g == null) return;

      net.joinGame( str(g.id), enterName.content, enterPassword.content );

      // ID NAME PASSWORT
    }

    if (selectFile.mouseOver() && mode == CREATE) {
      openXMLSelector();
    }

    if (scrollDown.mouseOver()) {
      startOfScope += 1;
    }

    if (scrollUp.mouseOver()) {
      startOfScope -= 1;
    }

    if (prefSwitch.mouseOver()) {
      prefSwitch.press();
      preference = prefSwitch.state ? WHITE : BLACK;
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
  case '0': 
    return true;
  default: 
    return false;
  }
}
class Textbox {
  String content = "";
  float xpos, ypos, radx, rady;
  boolean active = false;
  boolean isAlphaAllowed = true;
  boolean isContentSecret = false;
  int maxchars = 12;
  int correctionstate = 0;
  int correctiontimer = 0;
  int forMode = -1;
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
    
    if(isContentSecret) {
      int len = displaytext.length();
      displaytext = "";
      while(len > 0) {
        displaytext += "*";
        len--;
      } 
    }
    
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
final int JOIN = 0;
final int CREATE = 1;
final int ALL = 42;
/* END OF INTEGERS */


chess sketchRef = this;

String IPPRESET = "192.168.178.21:6877";
// 192.168.178.21:6877
// 84.200.52.231:6877

int thisPlayerFaction = WHITE;
int preference = WHITE;

int CAN_GO_COLOR = 0xff91FF81;
int IN_DANGER_COLOR = 0xffFF4646;
int SELECTED_COLOR = 0xffFBFF46;



/* OBJECTS */
Game game;
Menu menu;
Serverbrowser browser;
xml_parser parser;
InputHandler input;
Networker net;

ArrayList<ReferencedImage> images;
/* END OF OBJECTS */




public void init_vars() {
  
  images = new ArrayList<ReferencedImage>();
  load_images();
  
  parser = new xml_parser();
  input = new InputHandler();
  net = new Networker("0", 0);
  game = new Game();
  menu = new Menu();
  browser = new Serverbrowser();
  
}



public void load_images() {
  // Backgrounds
    images.add(new ReferencedImage("/assets/background/wood-texture.jpg", "wooden background"));
    images.add(new ReferencedImage("/assets/background/studyroom.jpg", "study room"));
    images.add(new ReferencedImage("/assets/background/server.jpg", "server room"));
    images.add(new ReferencedImage("/assets/background/bnw.png", "checkmate"));
  
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
