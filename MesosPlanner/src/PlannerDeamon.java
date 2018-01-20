import org.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;
import java.net.URL;
import java.util.stream.Collectors;
public class PlannerDeamon extends Thread {
/*
Problema: ScheduleManager alloca degli Schedulable su dei Receiver, non crea una vera schedule dei processi.
DONE: ricevere la schedule da ScheduleManager
DONE: generare eventi ai t nella schedule
TODO: usare le API dell'Allocator
TODO: usare un database per leggere la schedule
 */
    private Map<LocalDateTime,String> messageSchedule;
    private boolean running;
    private Map<Receiver,Map<? extends Schedulable,LocalDateTime>> nodes;

    Properties apiFile;
    Properties allocatorFile;


    public PlannerDeamon(){
        messageSchedule=new HashMap<>();
        nodes=new HashMap<>();
        running=true;
        apiFile=new Properties();
        allocatorFile=new Properties();
        try{
        InputStream apinp = new FileInputStream("src/apis.properties");
        InputStream alinp = new FileInputStream("src/allocator.properties");
        apiFile.load(apinp);
        allocatorFile.load(alinp);
        }
        catch (IOException io){
            System.out.println("opssss");
        }




    }

    public void addRecevier(Receiver rec,Map<? extends Schedulable,LocalDateTime> sched){
        nodes.put(rec,sched);
        if(sched.size()>1){
        messageSchedule.put(rec.getStartTime(),"POWER ON");
        messageSchedule.put(rec.getEndTime(),"POWER OFF");}

    }

    public void printMessages(){
        for(Map.Entry<LocalDateTime,String> em: messageSchedule.entrySet()){
            System.out.println(em.getKey()+"    "+em.getValue());
        }
    }

    public void run(){
        //DONE: TESTARE il check sul tempo.
        sortSchedule();
        Iterator<Map.Entry<LocalDateTime,String>> it=messageSchedule.entrySet().iterator();

        //DONE:sort sulle date. I messaggi non sono ordinati per tempo di invio.
        Map.Entry<LocalDateTime,String> prossimo = it.next();
        //System.out.println("Il primo parte a "+prossimo.getValue()+" "+prossimo.getKey());
        while(running && it.hasNext()){
            //System.out.println("=== "+LocalDateTime.now()+"  "+prossimo.getKey()+" "+prossimo.getKey().isBefore(LocalDateTime.now()));
//            if(prossimo.getKey().isAfter(LocalDateTime.now())){
            if(LocalDateTime.now().isAfter(prossimo.getKey())){

                System.out.println("======== "+prossimo.getValue()+"  "+prossimo.getKey());
                String[] data = {"prova"};
                sendSignal("VMON",data);
                //System.out.println("Il prKASFASFLKANS "+prossimo.getValue()+" "+prossimo.getKey());
                prossimo=it.next();
                //System.out.println("Il prssimo a "+prossimo.getValue()+" "+prossimo.getKey());
            }


        }
    }

    private void sortSchedule(){

       messageSchedule = messageSchedule.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for(Map.Entry<LocalDateTime,String> e: messageSchedule.entrySet()){
            System.out.println(e.getKey()+" "+e.getValue());
        }

    }

    private void sendSignal(String type,String[] data){
        String general = "http://"+allocatorFile.getProperty("hostname")+":"+allocatorFile.getProperty("port")+allocatorFile.getProperty("pre");
        String commandSpecific = apiFile.getProperty(type);
        System.out.println(commandSpecific);

        JSONObject j= new JSONObject(commandSpecific);
        int params=j.length();
        assert (params==data.length-1);
        for(int i=0;i<data.length;i++){
            j.put("vmName",data[i]);
        }

        System.out.println(j.toString());


    }


    private void allocatorCall(String url)throws IOException{

        URL u = new URL(url);
        InputStream is = u.openStream();




    }
//    private LocalDateTime findEarliestStartTime(Map<Schedulable,LocalDateTime> sched){
//        return
//    }
//
//    private LocalDateTime findLatestEndTime(Map<Schedulable,LocalDateTime> sched){
//
//    }
}
