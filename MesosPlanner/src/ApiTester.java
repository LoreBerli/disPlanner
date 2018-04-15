
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiTester {
    public static void main(String[] args) throws IOException{
        Properties apiFile=new Properties();
        Properties allocatorFile=new Properties();
        try{
            InputStream apinp = new FileInputStream("apis.properties");
            InputStream alinp = new FileInputStream("allocator.properties");
            apiFile.load(apinp);
            allocatorFile.load(alinp);
        }
        catch (IOException io){
            System.out.println("####DEAMON PLANNER File .properties non trovati.");
        }
        /////////////////////////

        PlannerDeamon deam= new PlannerDeamon();
        String[] apis = {"DOCKERON","DOCKEROFF","VMON","VMOFF","VMSHUTDOWN"};
        String cm=apiFile.getProperty(apis[0]);
        System.out.println(cm);
        JSONObject j= new JSONObject(cm);
        System.out.println(j.keySet());

    }
}
