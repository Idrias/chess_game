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
    enterID = new Textbox(135, 232, 130, 40, "");
    enterID.isAlphaAllowed = false;
    enterName = new Textbox(135, 320, 130, 40, "");
    
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
    
    
    enterGame = new Button( new PVector(width/2-260, height/2+280), new PVector(335, 39), color(#DDFF1F), "Join Game", true );
    createGame = new Button( new PVector(width/2+250, height/2+280), new PVector(335, 39), color(#DDFF1F), "Create Game", true );
     
     
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
  
  void takeID(int id) {
    receivedID = id;
    hasIDBeenReceived = true;
    enterID.content = str(id);
  }

  void draw() {
    if(enterServerIP.active) enterCreateServerIP.content = enterServerIP.content;
    if(enterCreateServerIP.active) enterServerIP.content = enterCreateServerIP.content;
    
    image(background, width/2, height/2);

    strokeWeight(5);
    stroke(#DDFF1F);  // #03FFF0
    line(width/2, 0, width/2, height);
    line(0, 150, width, 150);

    fill(#DDFF1F);
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
      if(b.mouseOver()) b.col = color(#EDFF7C);
      else b.col = color(#DDFF1F);
      
      b.draw();
    
    }
    
    stroke(0); 
    strokeWeight(1);
  }

  void checkclick() { 

    for(Textbox tb : textboxes) tb.active = tb.mouseOver()? true : false;
    
    if(createGame.mouseOver()) {
      int sepindex = enterServerIP.content.indexOf(":");
      String ip = enterServerIP.content.substring(0, sepindex);
      String port = enterServerIP.content.substring(sepindex+1, enterServerIP.content.length());
      
      println(ip, port);
      net = new Networker(ip, int(port)); println("HEALTHY"); net.createGame(enterXMLloc.content+enterXMLname.content); println("STILL HEALTHY");
    }
    
    if(enterGame.mouseOver()) {
      if(enterName.content.equals("")) enterName.correct(2);
      if(enterID.content.equals("")) enterID.correct(2);
      if(enterName.content.equals("") || enterID.content.equals(" ")) return; 
      println("HI");
      int sepindex = enterServerIP.content.indexOf(":");
      String ip = enterServerIP.content.substring(0, sepindex);
      String port = enterServerIP.content.substring(sepindex+1, enterServerIP.content.length());
      
      println(ip, port);
      
      net = new Networker(ip, int(port));
      println("HEALTHY");
      net.joinGame(enterID.content, enterName.content);
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
    case '0': return true;
    default: return false;
  }
}