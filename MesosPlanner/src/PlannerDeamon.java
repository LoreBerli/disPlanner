import org.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
DONE: usare le API dell'Allocator
TODO: usare un database per leggere la schedule
DONE: vm AND docker apis
DONE: RIFARE messageSchedule -> Map <String[],LocalDateTime>
 */

    public enum NODETYPE{
        VM,DOCKER,HOST
    }


    private Map<String[],LocalDateTime> messageSchedule;
    private boolean running;
    private Map<Receiver,Map<? extends Schedulable,LocalDateTime>> nodes;
    private NODETYPE receiverType;
    Properties apiFile;
    Properties allocatorFile;

    /**
     * Demone che chiama le API dell'Allocator ai t specificati dallo ScheduleManager
     */
    public PlannerDeamon(){
        messageSchedule=new HashMap<>();
        //this.receiverType=nd;
        nodes=new HashMap<>();
        running=true;
        apiFile=new Properties();
        allocatorFile=new Properties();
        try{
        InputStream apinp = new FileInputStream("apis.properties");
        InputStream alinp = new FileInputStream("allocator.properties");
        apiFile.load(apinp);
        allocatorFile.load(alinp);
        }
        catch (IOException io){
            System.out.println("####DEAMON PLANNER File .properties non trovati.");
        }
    }



    public void run(){
        //DONE: TESTARE il check sul tempo.
        sortSchedule();
        Iterator<Map.Entry<String[],LocalDateTime>> it=messageSchedule.entrySet().iterator();

        //DONE:sort sulle date. I messaggi ora  sono ordinati per tempo di invio.
        Map.Entry<String[],LocalDateTime> prossimo = it.next();
        //System.out.println("Il primo parte a "+prossimo.getValue()+" "+prossimo.getKey());
        while(running && it.hasNext()){
            if(LocalDateTime.now().isAfter(prossimo.getValue())){
                System.out.println("###DEAMON PLANNER - SIGNAL "+prossimo.getValue()+" -"+prossimo.getKey()[0]+"- "+prossimo.getKey()[1]);
                String[] data = {"prova"};
                sendSignal(prossimo.getKey()[0].toString(),data);//nope
                prossimo=it.next();
            }
        }
    }

    /**
     * Generates VMON and VMOFF signals according to schedule start and end times.
     *
     */
    public void vMScan(Receiver rec,Map<? extends Schedulable,LocalDateTime> sched){
        nodes.put(rec,sched);
        if(sched.size()>1){
            messageSchedule.put(new String[]{"VMON",rec.getInfo()} ,rec.getStartTime().minusSeconds(10));
            messageSchedule.put(new String[]{"VMOFF",rec.getInfo()} ,rec.getEndTime().plusSeconds(10));}

    }

    /**
     * Generates DOCKERON and DOCKEROFF signals according to schedule start and end times
     * @param rec
     * @param sched
     */
    public void dockerScan(Receiver rec,Map<? extends Schedulable,LocalDateTime> sched){
        nodes.put(rec,sched);
        if(sched.size()>1){
            //messageSchedule.put(rec.getStartTime(),"DOCKERSTART");
            messageSchedule.put(new String[]{"DOCKERON",rec.getInfo()} ,rec.getStartTime());
            messageSchedule.put(new String[]{"DOCKEROFF",rec.getInfo()} ,rec.getEndTime());}
    }

    private void sortSchedule(){

       messageSchedule = messageSchedule.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for(Map.Entry<String[],LocalDateTime> e: messageSchedule.entrySet()){
            //System.out.println("###DEAMON PLANNER "+e.getKey()[0]+" "+e.getKey()[1]+" "+e.getValue());
        }

    }

    /**
     * Riceve una stringa con il codice del messaggio : VMON VMOFF VMSHUTDOWN DOCKERON DOCKEROFF
     * e una con i dati
     */

    private void sendSignal(String type,String[] data){
        // docker richiede il json con la descrizione. Lo prendo dal db?
        //cos'è keys???????
        //
        //
        //questo si è rotto con l'aggiornamento FIXME

        String general = "http://"+allocatorFile.getProperty("hostname")+":"+allocatorFile.getProperty("port")+apiFile.getProperty("pre");
        System.out.println(type);
        String commandSpecific = apiFile.getProperty(type);

        System.out.println(commandSpecific);

        JSONObject j= new JSONObject(commandSpecific);
        int params=j.length();
        assert (params==data.length-1);
        for(int i=0;i<data.length;i++){
            j.put(j.keys().next(),data[i]);
        }
        try{
            all("id");
        allocatorCall(general+j.toString());}
        catch (IOException io){}
        System.out.println(j.toString());
    }


    private int allocatorCall(String url)throws IOException{
        System.out.println(url);
        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "CIAONE");
        return con.getResponseCode();



    }

    private int all(String id)throws IOException{//TODO gestire sta cosa

        String[] cmd = {
                "/bin/bash",
                "python3 /home/cioni/PycharmProjects/dockerHandler.py hello-world"
        };
        System.out.println(Runtime.getRuntime().exec(cmd));
        return 0;
    }



}
