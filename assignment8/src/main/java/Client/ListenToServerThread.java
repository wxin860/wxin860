package Client;


import Protocol.Message;
import Protocol.Requests.BroadcastMessage;
import Protocol.Requests.DirectMessage;
import Protocol.Requests.SendInsultMessage;
import Protocol.Responses.ConnectResponse;
import Protocol.Responses.DisconnectResponse;
import Protocol.Responses.FailedMessage;
import Protocol.Responses.QueryResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javafx.util.Pair;

/**
 * Listen to server thread
 */
public class ListenToServerThread extends Thread {
  private ObjectInputStream fromServer;
  private boolean nonstop = true;

  /**
   * initiate a thread to listen to server
   * @param fromServer
   */
  public ListenToServerThread(ObjectInputStream fromServer) {
    this.fromServer = fromServer;
  }

  /**
   * print out the message received from server
   */
  @Override
  public void run() {
    while(nonstop){
        Message response = null;
        try {
          response = (Message) fromServer.readObject();;
          String res = getMessage(response);
          System.out.println(res);
          if(response.isDisconnectResponse()){
            nonstop = false;
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
    }
  }

  /**
   * helper class to get message from server and print out message
   * @param message Message received from server
   * @return
   */
  private static String getMessage(Message message){
    if(message.isDisconnectResponse()){
      DisconnectResponse disconnect = (DisconnectResponse) message;
      String res = new String(disconnect.getMessage());
      return res;
    }else if(message.isQueryResponse()){
      QueryResponse query = (QueryResponse) message;
      List<Pair<Integer, byte[]>> users = query.getUsers();
      StringBuilder sb = new StringBuilder();
      for(Pair<Integer, byte[]> user : users){
        sb.append(new String(user.getValue())).append(", ");
      }
      return sb.toString();
    }else if(message.isFailedMessage()){
      FailedMessage fail = (FailedMessage) message;
      String res = new String(fail.getDescription());
      return res;
    }else if(message.isConnectResponse()){
      ConnectResponse connect = (ConnectResponse) message;
      String res = new String(connect.getMessage());
      return res;
    }else if(message.isBroadcastMessage()){
      BroadcastMessage broadCast = (BroadcastMessage) message;
      String res = new String(broadCast.getMessage());
      return res;
    } else if(message.isDirectMessage()){
      DirectMessage direct = (DirectMessage) message;
      String res = new String(direct.getMessage());
      return res;
    } else if(message.isSendInsultMessage()){
      SendInsultMessage insult = (SendInsultMessage) message;
      String res = new String(insult.getMsg());
      return res;
    }else{
      return "no response yet";
    }
  }
}
