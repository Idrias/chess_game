import processing.net.*;

Server serv;

void setup() {
  serv = new Server(this, 6878);

}


void draw() {
   // Get the next available client
  Client thisClient = serv.available();
  // If the client is not null, and says something, display what it said
  if (thisClient !=null) {
    String whatClientSaid = thisClient.readString();
    if (whatClientSaid != null) {
      //println(thisClient.ip() + "t" + whatClientSaid);
      
      int hash = 0;
      for(int i=0; i<whatClientSaid.length(); i++) {
        hash += whatClientSaid.charAt(i);
        
      }
      println(hash, whatClientSaid.charAt(0), whatClientSaid.charAt(whatClientSaid.length()-1));
      serv.write("$THX;"+hash+";€");
    }
  }
}