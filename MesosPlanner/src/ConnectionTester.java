import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketAddress;
import java.net.URL;
import java.net.Socket;



//QUESTO VA MESSO NEL DAEMON


public class ConnectionTester {
    public static void main (String[] args) throws IOException {
        //Socket socket = new Socket("localhost", 8081);

        while (true) {

            Socket socket = new Socket("192.168.1.10", 8082);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("ok");

            System.out.println("Message sent");
            //stdIn.readLine();
        }
    }
}
