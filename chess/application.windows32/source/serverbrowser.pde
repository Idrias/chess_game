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



  void init_ui() {
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
    joinGame = new Button( new PVector(width/2+191, height/2+230), new PVector(257, 40), color(#DDFF1F), color(#EDFF7C), "Join Game", true);
    joinGame.forMode = JOIN;
    buttons.add(joinGame);

    // Create Button
    createGame = new Button( new PVector(width/2+191, height/2+230), new PVector(257, 40), color(#DDFF1F), color(#EDFF7C), "Create Game", true);
    createGame.forMode = CREATE;
    buttons.add(createGame);

    // File select Button
    selectFile = new Button( new PVector(423, 504), new PVector(120, 25), color(#DDFF1F), color(#EDFF7C), "Select File", true);
    selectFile.forMode = CREATE;
    buttons.add(selectFile);

    // Scroll Buttons
    scrollUp = new Button( new PVector(899, 127), new PVector(20, 128), color(#DDFF1F), color(#EDFF7C), "ʌ", true);
    scrollDown = new Button( new PVector(899, 256), new PVector(20, 128), color(#DDFF1F), color(#EDFF7C), "v", true);
    scrollUp.forMode = ALL;
    scrollDown.forMode = ALL;
    buttons.add(scrollUp);
    buttons.add(scrollDown);
    //** END OF BUTTONS

    prefSwitch = new LightSwitch(new PVector(width/2+255, height/2+121), new PVector(width/2-348, height/2+-310), 255, 200, "White", 0, 55, "Black", true);

    //** MODE SELECTORS
    mselectors = new ArrayList<ModeSelector>();

    selJoin = new ModeSelector(new PVector(435, 360), new PVector(130, 35), color(#DDFF1F), color(#DDFF1F), "Join Game", true);
    selCreate = new ModeSelector( new PVector(565, 360), new PVector(130, 35), color(#DDFF1F), color(#DDFF1F), "Create Game", false);
    selJoin.set_partner(selCreate);
    selCreate.set_partner(selJoin);
    mselectors.add(selJoin);
    mselectors.add(selCreate);

    selSortID = new ModeSelector(new PVector(95, 100), new PVector(35, 30), color(#DDFF1F), color(#DDFF1F), "ID", true);
    selSortName = new ModeSelector( new PVector(95, 131), new PVector(35, 30), color(#DDFF1F), color(#DDFF1F), "ABC", false);
    selSortID.set_partner(selSortName);
    selSortName.set_partner(selSortID);
    mselectors.add(selSortID);
    mselectors.add(selSortName);
    //** END OF MODE SELECTORS
  }



  void draw() {
    enterName.content = str(millis());
    if (selJoin.state && !selCreate.state) mode = JOIN;
    else if (selCreate.state && !selJoin.state) mode = CREATE;
    else mode = UNDEFINED;

    // Background Image
    image(background, width/2, height/2);

    // Game List
    strokeWeight(2);

    fill(#DDFF1F);

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
      stroke(#0EE830); 
      fill(#0EE830);
    } else {
      stroke(#FA5103); 
      fill(#FA5103);
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

    fill(#DDFF1F);
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
    
    float timeLeft = float(nextCreationPossibility) - float(millis());
    timeLeft /= 100;
    timeLeft = float(round(timeLeft))/10;
    
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


  void sortGameLinks() {

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


  void checkclick() { 

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





boolean isnan(float num) {
  return num != num;
}

boolean isNum(char what) {
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