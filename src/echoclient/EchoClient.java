package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Observable implements Runnable
{
    boolean listening;
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private Scanner input;
  private PrintWriter output;
  String msg;
public EchoClient(){
    listening = true;
}
  
  
  public void connect(String address, int port) throws UnknownHostException, IOException
  {
    this.port = port;
    serverAddress = InetAddress.getByName(address);
    socket = new Socket(serverAddress, port);
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
  }
  
  public void send(String msg)
  {
    output.println(msg);
    this.msg = msg;
    hasChanged();
    notifyObservers();
           
  }
  
  public void stop() throws IOException{
    output.println(ProtocolStrings.STOP);
  }
  
  public String receive(){
      return this.msg;
  }
  public void run()
  {
      while (listening){
    msg = input.nextLine();
    setChanged();
    notifyObservers(msg);
              
    if(msg.equals(ProtocolStrings.STOP)){
      try {
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
      }
  }
  public void addObs(Observer o){
      addObserver(o);
  }
  
  public static void main(String[] args)
  {   
    int port = 9090;
    String ip = "localhost";
    if(args.length == 2){
      port = Integer.parseInt(args[0]);
      ip = args[1];
    }
    try {
      EchoClient tester = new EchoClient();      
      tester.connect(ip, port);
      System.out.println("Sending 'Hello world'");
      tester.send("Hello World");
      System.out.println("Waiting for a reply");
      System.out.println("Received: " + tester.receive()); //Important Blocking call         
      tester.stop();      
      //System.in.read();      
    } catch (UnknownHostException ex) {
      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
