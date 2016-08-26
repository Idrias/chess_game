class InputHandler {
  boolean registeredMouseClick = false;

  void check() {
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



void mouseWheel(MouseEvent e) {
  if(game.state == SERVERBROWSER) browser.startOfScope += e.getCount();

}



void keyPressed() {
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




void establishGamebrowser() {
  for (Textbox tb : browser.textboxes) {
    if (tb.active && tb == browser.enterServerIP) {

      int sepindex = tb.content.indexOf(":");
      String ip = tb.content.substring(0, sepindex);
      String port = tb.content.substring(sepindex+1, tb.content.length());

      if (ip!=null && port!=null && !ip.equals("") && !port.equals("")) {
        browser.glinks = new ArrayList<GameLink>();
        
        net = new Networker(ip, int(port));
        net.start();
        net.addMessage("LIST GAMES", new String[]{});
        
        
      }
      tb.active = false;
    }
  }
}