void openXMLSelector() {
  String dp = dataPath("");
  dp = dp.substring(0, dp.length()-5);
  dp += "\\assets\\xml\\default_board.xml";
  File f = new File(dp);
  selectInput("Choose XML file you wish to upload to the server.", "selectdone", f);
}


void selectdone(File selection) {
  if (selection == null) return; // User aborted
  
  String path = selection.getAbsolutePath();
  browser.pathToXML = path;
  
  String filename = path.substring(path.lastIndexOf("\\")+1, path.length());
  browser.enterXMLname.content = ".../" + filename;
}