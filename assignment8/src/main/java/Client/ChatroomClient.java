package Client;

import Protocol.Message;
import Protocol.Requests.BroadcastMessage;
import Protocol.Requests.ConnectMessage;
import Protocol.Requests.DirectMessage;
import Protocol.Requests.DisconnectMessage;
import Protocol.Requests.QueryUsersMessage;
import Protocol.Requests.SendInsultMessage;
import Protocol.Responses.ConnectResponse;
import assignment7.Generator;
import assignment7.Grammar;
import assignment7.JSONParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Client class, entrance of client side
 */
public class ChatroomClient extends Socket {
  private Socket socket;
  private static ObjectOutputStream toServer = null;
  private static ObjectInputStream fromServer = null;
  private static String userName;

  /**
   * start client
   * @param args array of String as server address and port number
   */
  public static void main(String[] args) {
    args = new String[]{"34.82.196.213", "17000"};
    if(args.length != 2){
      System.out.println("Usage: need Server IP and port number");
      return;
    }
    try {
      InputStreamReader fileInputStream = new InputStreamReader(System.in);
      BufferedReader bufferedReader = new BufferedReader(fileInputStream);
      Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
      toServer = new ObjectOutputStream(socket.getOutputStream());
      fromServer = new ObjectInputStream(socket.getInputStream());
      System.out.println("please enter your name");
      ListenToServerThread listenToServer = new ListenToServerThread(fromServer);

      while(true){
        if (bufferedReader.ready()) {
          String fromUser = bufferedReader.readLine();
          if (userName == null) {
            userName = fromUser;
            Message message = sendMessage(fromUser);
            toServer.writeObject(message);
            toServer.flush();
            Message resp = (Message) fromServer.readObject();
            if(!resp.isFailedMessage()){
              ConnectResponse response = (ConnectResponse) resp;
              String res = new String (response.getMessage());
              System.out.println(res);
              listenToServer.start();
            } else {
              break;
            }
          } else if (fromUser.equals("?")) {
            System.out.println("logoff: disconnect from server");
            System.out.println("who: query all users from server");
            System.out.println("@user: send direct message to user");
            System.out.println("@all: send broadcast message to all users");
            System.out.println("!user: send insult to all users");
            System.out.println("you can start typing");
          } else {
            Message message = sendMessage(fromUser);
            toServer.writeObject(message);
            toServer.flush();
          }
        }
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * helper method to generate Message object according to client's input
   * @param fromUser client's command
   * @return Message to send to server
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private static Message sendMessage(String fromUser) throws IOException, ClassNotFoundException {
     if (fromUser.startsWith("!")) {
      String recipientName = fromUser.replace("!", "");
      Path path = Paths.get("grammars/insult_grammar.json");
      Grammar grammar = JSONParser.parse(path);
      Generator generator = new Generator(grammar);
      String insult = generator.generate();
      SendInsultMessage sendInsultMessage = new SendInsultMessage(userName.length(),
          userName.getBytes(), recipientName.length(), recipientName.getBytes(), insult.length(), insult.getBytes());
      System.out.println("send insult");
      return sendInsultMessage;
    }else if (fromUser.equals("logoff")) {
       DisconnectMessage disconnectMessage = new DisconnectMessage(userName.length(),
           userName.getBytes());
       System.out.println(userName + " is going to log off");
       return disconnectMessage;
     }else if(fromUser.equals(userName)){
      ConnectMessage connectMessage = new ConnectMessage(userName.length(), userName.getBytes());
      System.out.println("trying to connect with server");
      return connectMessage;
    } else if (fromUser.equals("who")) {
       QueryUsersMessage queryUsersMessage = new QueryUsersMessage(userName.length(),
          userName.getBytes());
      System.out.println(userName + " queried users in system");
      return queryUsersMessage;
    } else if (fromUser.startsWith("@all")) {
      BroadcastMessage broadcastMessage = new BroadcastMessage(userName.length(),
         userName.getBytes(), fromUser.length(), fromUser.getBytes());
      System.out.println(userName + " broadcast in system");
      return broadcastMessage;
    } else {
      String[] splits = fromUser.split("\\s");
      String recipientUser = splits[0].replace("@", "");
      int len = splits[0].length();
      String m = userName + ":" + fromUser.substring(len).trim();
      DirectMessage directMessage = new DirectMessage(userName.length(),
          userName.getBytes(),
          recipientUser.length(), recipientUser.getBytes(), m.length(),
          m.getBytes());
      System.out.println(userName + " sent direct message");
      return directMessage;
    }
  }

  public static String getUserName() {
    return userName;
  }

}
