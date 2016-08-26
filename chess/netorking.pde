import processing.net.*;

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

  void start() {
    client = new Client(sketchRef, serverIP, serverPORT);
  }
    


  void close() {
    if (client !=  null && client.active()) 
      client.stop();
  }
  
  
  void restart() {
    client.stop();
    client = new Client(sketchRef, serverIP, serverPORT);
  }


  void comCheck() {
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

  void addMessage(String command, String[] args) {
    String message = "$" + command + ";";

    for (int i=0; i < args.length; i++) {
      message  += args[i] + ";";
    }
    message += "&";

    //println(message);
    outMSGS.add(new outMessage(message));
  }

  void interpret(String message) {
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
        if (int(hash) == msg.specialHash) {
          msg.hasBeenReceived = true;
          print("HAPPY HASH", hash);
        } else
          print(hash, "is not the needed hash", msg.specialHash);
      }
    }



    if (command.equals("YOU ARE")) {
      // WE GOT ACCEPTED!
      if (int(arguments.get(0))==WHITE) thisPlayerFaction = WHITE;
      else if (int(arguments.get(0))==BLACK) thisPlayerFaction = BLACK;

      game.board = new Board();
      game.state = CONNECTED;
    }

    if (command.equals("ADD FIGURE")) {
      Figure f = new Figure(int(arguments.get(0)), int(arguments.get(1)), new PVector(int(arguments.get(2)), int(arguments.get(3))));

      if (arguments.get(4).equals("True")) f.hasMoved = true;
      game.board.fields[int(f.pos.x)][int(f.pos.y)].figure = f;
    }

    if (command.equals("REMOVE FIGURE")) {
      game.board.fields[int(arguments.get(0))][int(arguments.get(1))].figure = null;
    }

    if (command.equals("TURN")) {
      game.board.whoseTurn = int(arguments.get(0));
    }
    
    if (command.equals("CODE IS")) {
      browser.lastID = int(arguments.get(0));
      browser.nextCreationPossibility = millis() + int(arguments.get(1))*1000;
    }
    
    if (command.equals("GAME")) {
      int linkID = int(arguments.get(0));
      
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
        if (int(arguments.get(1)) == WHITE) {
          game.board.nameWHITE = arguments.get(2);
        } else if (int(arguments.get(1)) == BLACK) {
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
        if(browser.glinks.get(i).id == int(arguments.get(0))) {
          browser.glinks.remove(i);
          i=0;
        }
      }
    }
  }



  void inCheck() {
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


  void outCheck() {
    int timeNow = millis();
    for (outMessage msg : outMSGS) {
      if (!msg.hasBeenReceived && timeNow - msg.lastTime > SENDAGAINTHERESHOLD && !msg.devoverride) {
        client.write(msg.message);
        msg.lastTime = timeNow;
        msg.devoverride = true;
      }
    }
  }


  void createGame(String gameFile, String pwd) {
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



  void joinGame(String id, String name, String password) {
    if(!active()) return;
    if(name.equals("")) {browser.enterName.correct(2); return;}
    addMessage("JOIN GAME", new String[]{id, str(preference), name, password});
  }



  boolean active() {
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
      specialHash += int(message.charAt(i));
    }
    lastTime = millis();
  }
}