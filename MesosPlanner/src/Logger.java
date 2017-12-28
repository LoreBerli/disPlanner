import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Logger.
 * Created by cioni on 23/11/17.
 */
public class Logger {
    private String generalFile;
    private String nodeFile;
    private String procFile;
    private Writer writer;

    public Logger(String general) throws FileNotFoundException,UnsupportedEncodingException{
        generalFile=general;
        //writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logs/outLog"), "utf-8"));

    }
    public void logNodeInfo(Receiver node,String extra) throws IOException{
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logs/outLog"), "utf-8"));
        String toWrite="";
        toWrite+=node.getInfo();
        writer.write(toWrite);
        writer.close();
    }

    public void binPackerInfo(Schedulable j,Map<Receiver,Float> fits,Receiver chosenOne) throws IOException{
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logs/BinPackerLog",true),"utf-8"));
        String toWrite="\n ----------------";
        toWrite+=j.toString();
        toWrite+="\n";
        toWrite+=fits;
        toWrite+="\n BEST: "+chosenOne.toString();
        float[] el = chosenOne.checkNormalizedResAtTime(j.getStartTime());
        toWrite+="BESTVALUES: "+el[0]+" "+el[1]+" "+el[2];
        writer.write(toWrite);
        writer.close();
    }
}
