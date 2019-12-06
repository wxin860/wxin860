package Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatroomClientTest {
  ChatroomClient chatroomClient;
  final static String[] args = {"", "3000"};

  @Before
  public void setUp() throws Exception {
    chatroomClient= new ChatroomClient();
  }

  private static Socket serverSocket;
  private static final int PORT_NUMBER = 3000;

  @BeforeClass
  public static void init() throws IOException {

  }

  private final InputStream systemIn = System.in;
  private final PrintStream systemOut = System.out;

  private ByteArrayInputStream testIn;
  private ByteArrayOutputStream testOut;

  @Before
  public void setUpOutput() {
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
  }

  private void provideInput(String data) {
    testIn = new ByteArrayInputStream(data.getBytes());
    System.setIn(testIn);
  }

  private String getOutput() {
    return testOut.toString();
  }

  @After
  public void restoreSystemInputOutput() {
    System.setIn(systemIn);
    System.setOut(systemOut);
  }

  @Test
  public void mainTest1() {
    ChatroomClient.main(args);
    assertEquals("", getOutput());

  }

  @Test
  public void mainTestLessArgs() {
    ChatroomClient.main(new String[]{"2323"});
    assertEquals("Usage: need Server IP and port number\n", getOutput());
  }

  @Test
  public void mainTest2(){
    ChatroomClient.main(args);
    assertEquals(null, ChatroomClient.getUserName());

  }

//  @Test
//  public void mainTest3() throws IOException {
//    ChatroomClient.main(args);
//    String userName = ChatroomClient.getUserName();
//    String s = getOutput();
//  }

}
