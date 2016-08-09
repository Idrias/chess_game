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


  void draw() {
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


  void checkclick() {
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
  
  
  
  
  
  
    boolean isFriendly(int xcord, int ycord) {
    Figure f = game.board.fields[xcord][ycord].figure;
    if (f != null && f.faction == FRIENDLY) return true;
    return false;
  }

  boolean isHostile(int xcord, int ycord) {
    Figure f = game.board.fields[xcord][ycord].figure;
    if (f != null && f.faction == HOSTILE) return true;
    return false;
  }



void calculateMoves(int xout, int yout) {
  Figure f = game.board.fields[xout][yout].figure;
  // TODO IMPLEMENT BAUERNTAUSCH
  // TODO BAUER DARF (NUR) ÜBER KREUZ SCHLAGEN
  // TODO IMPLEMENT ROCHADE
  // TODO IMPLEMENT EN PASSANT
  switch(f.type) {
  case PAWN: 
    // Bewegung
    if (f.col == WHITE && yout - 1 > 0 && !isFriendly(xout, yout-1) && !isHostile(xout, yout-1)) fields[xout][yout-1].highlightCode = CAN_GO;
    if (f.col == WHITE && yout - 2 > 0 && !isFriendly(xout, yout-2) && !isFriendly(xout, yout-1) && !isHostile(xout, yout-1) && !isHostile(xout, yout-2) && !f.hasMoved) fields[xout][yout-2].highlightCode = CAN_GO;
    if (f.col == BLACK && yout + 1 < ySize && !isFriendly(xout, yout+1) && !isHostile(xout, yout+1)) fields[xout][yout+1].highlightCode = CAN_GO;
    if (f.col == BLACK && yout + 2 < ySize && !isFriendly(xout, yout+2) && !isFriendly(xout, yout+1) && !isHostile(xout, yout+1) && !isHostile(xout, yout+2) &&!f.hasMoved) fields[xout][yout+2].highlightCode = CAN_GO;
    
    // Schlagen "über Kreuz"
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