class Board {
  
  boolean isAFigurePicked = false;
  boolean isCheckMate = false;
  
  boolean changePawn = false;
  PVector changePawnPos;
  
  int xSize = 8;
  int ySize = 8;
  int DRAW_spaceX = 570;
  int DRAW_spaceY = 570;
  int DRAW_beginX = 60;
  int DRAW_beginY = 60;
  int DRAW_endX, DRAW_endY;
  int whoseTurn = 0;
  int gID = 42;

  
  float DRAW_fieldSizeX;
  float DRAW_fieldSizeY;
  
  String nameWHITE = "";
  String nameBLACK = "";
  String winner = "";

  
  Field[][] fields;
  ArrayList<PossibleMove> pmoves;
  ArrayList<Button> pawnButtons;
  
  PImage background;

  
  Board() {
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
    pmoves = new ArrayList<PossibleMove>();
    
    pawnButtons = new ArrayList<Button>();
    pawnButtons.add( new Button(new PVector(730, 292), new PVector(70, 35), color(#DDFF1F), color(#EDFF7C), "Queen", true) );
    pawnButtons.add( new Button(new PVector(807, 292), new PVector(70, 35), color(#DDFF1F), color(#EDFF7C), "Tower", true) );
    pawnButtons.add( new Button(new PVector(884, 292), new PVector(70, 35), color(#DDFF1F), color(#EDFF7C), "Bishop", true) );
    pawnButtons.add( new Button(new PVector(960, 292), new PVector(70, 35), color(#DDFF1F), color(#EDFF7C), "Horse", true) );

}


  void draw() {
    
    
    strokeWeight(2);
 
    
    // Draw background
    
    image(background, width/2, height/2);
    //if (whoseTurn == thisPlayerFaction) {if(whoseTurn==WHITE) background(255); else background(0);}

    fill(255);
    textSize(15);
    text("Exchange Pawn for:", 845, 258);
    
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
    text("Game ID: " + str(gID), 845, 16);
    textSize(12);
    
    // Draw Buttons
    if (changePawn) {
      for(Button b : pawnButtons) b.draw();
    }
    
    // Draw figures
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        fields[x][y].draw_figure();
      }
    }
    
    
    // Draw GUI
    textSize(18);
    text("WHITE:", 743, 93);
    text("BLACK", 946, 93);
    text("01:10", 738, 137);
    text("01:10", 947, 137);
    text("01:10", 737, 198);
    text("01:10", 949, 197);
    text("TURNTIMER", 842, 137);
    text("MATCHTIMER", 844, 198);
    textSize(12);
    ///
    
    if (isCheckMate) {
      textSize(70);
      fill(255, 0, 0);
      text("CHECKMATE", 345, height/2-44);
      textSize(20);
      text(winner + "  wins.", 340, height/2+12);
      textSize(12);
    
    }
  }

  void setAllFields(int state) {
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        fields[x][y].highlightCode = state;
        }
     }
  }
  
  
  void checkclick() {
    
    if( changePawn ) {
       setAllFields(NOT_HIGHLIGHTED);
       game.board.fields[int(changePawnPos.x)][int(changePawnPos.y)].highlightCode = FRIENDLY_PICKED;
    }
    
    // DEV
      if(whoseTurn != thisPlayerFaction || changePawn) return;
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
            setAllFields(NOT_HIGHLIGHTED);
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
  
  
  
  void calculateMoves(int x, int y) {
    for(PossibleMove p : pmoves) {
      if(p.fromX == x && p.fromY == y)
        fields[p.toX][p.toY].highlightCode = CAN_GO;
    } 
    
  }
  
  
  
}
 
 