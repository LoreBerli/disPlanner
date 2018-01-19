import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerDeamon extends Thread {
/*
Problema: ScheduleManager alloca degli Schedulable su dei Receiver, non crea una vera schedule dei processi.
TODO: ricevere la schedule da ScheduleManager
TODO: generare eventi ai t nella schedule
TODO: usare un database per leggere la schedule
 */
    private Map<LocalDateTime,String> messageSchedule;
    private boolean running;
    private Map<Receiver,Map<? extends Schedulable,LocalDateTime>> nodes;
    public PlannerDeamon(){
        messageSchedule=new HashMap<>();
        nodes=new HashMap<>();
        running=true;


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
        //TODO TESTARE il check sul tempo.
        sortSchedule();
        Iterator<Map.Entry<LocalDateTime,String>> it=messageSchedule.entrySet().iterator();

        //TODO:sort sulle date. I messaggi non sono ordinati per tempo di invio.
        Map.Entry<LocalDateTime,String> prossimo = it.next();
        System.out.println("Il primo parte a "+prossimo.getValue()+" "+prossimo.getKey());
        while(running && it.hasNext()){
            //System.out.println("=== "+LocalDateTime.now()+"  "+prossimo.getKey()+" "+prossimo.getKey().isBefore(LocalDateTime.now()));
//            if(prossimo.getKey().isAfter(LocalDateTime.now())){
            if(LocalDateTime.now().isAfter(prossimo.getKey())){

                System.out.println("======== "+prossimo.getValue()+"  "+prossimo.getKey());
                System.out.println("Il prKASFASFLKANS "+prossimo.getValue()+" "+prossimo.getKey());
                prossimo=it.next();
                System.out.println("Il prssimo a "+prossimo.getValue()+" "+prossimo.getKey());
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


//    private LocalDateTime findEarliestStartTime(Map<Schedulable,LocalDateTime> sched){
//        return
//    }
//
//    private LocalDateTime findLatestEndTime(Map<Schedulable,LocalDateTime> sched){
//
//    }
}
