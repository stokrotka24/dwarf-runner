import java.util.Arrays;

import server.MenuServer;
import server.ServerData;

public class Hello {

  public static void main(String[] args) {
    if (Arrays.stream(args).anyMatch("-debug"::equals)) {
      ServerData.getInstance(2139).setIsDebug(true);;
    } else {
      ServerData.getInstance(2137);
    }
    MenuServer menuServer = new MenuServer();
    menuServer.go();
  }
}
