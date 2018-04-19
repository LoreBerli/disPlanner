import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionTester {
    public static void main (String[] args) throws IOException {
        String url = "http://127.0.0.1:8080";
        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "CIAONE");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
    }

}
