package Server;

import Protocol.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class UserInputRequestThread implements Runnable {

  private BlockingQueue<Message> userInput;
  private ObjectInputStream in;
  private boolean stopFlag;

  public UserInputRequestThread(BlockingQueue<Message> userInput, ObjectInputStream in) {
    this.userInput = userInput;
    this.in = in;
    this.stopFlag = false;
  }

  @Override
  public void run() {
    while (!stopFlag) {
      try {
        Object temp = in.readObject();
        Message m = (Message) temp;
        userInput.add(m);
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
