import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Offre metodi per lo scheduling ottimale.
 *
 * Created by cioni on 22/09/17.
 */
public class binPacker {
    /**
     * Dati una lista di {@link Machine} ed un {@link Job} seleziona la macchina ottimale per l'esecuzione del {@link Job}
     * @param toBeScheduled
     * @param park
     * @return {@link Machine}
     */
    static public  Receiver findOptimum(Schedulable toBeScheduled,List<? extends Receiver> park){
        //vedi metriche di Fenzo, fa la stessa cosa
        Map<Receiver,Float[]> fits = new HashMap<>();



        for (Receiver m:park) {
            //per ogni macchina recupero lo scheduling corrente
            Map<Schedulable,LocalDateTime> mSchedule =new HashMap<>(m.getCurrentSchedule());
            mSchedule.put(toBeScheduled,toBeScheduled.getStartTime());
            //creo un nuovo scheduling aggiungendo a quello vecchio il nuovo job da schedulare
            float[] res = m.checkNormalizedResAtTime(toBeScheduled.getStartTime(),mSchedule);
            //Float fit = (float)((res[0]+res[1]+res[2])/res.length);
            //TODO: cambiare sta cosa
            Float[] jesusReally=new Float[3];
            jesusReally[0]=new Float(res[0]);
            jesusReally[1]=new Float(res[1]);
            jesusReally[2]=new Float(res[2]);

            //controllo la fitness del nodo con il nuovo job
            fits.put(m,jesusReally);
        }

        float[] bestValues=new float[]{Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE};
        Receiver[] bestMachines=new Receiver[3];
        for(Map.Entry<Receiver,Float[]> e:fits.entrySet()){
            Float[] rez = e.getValue();
            for(int k=0;k<3;k++) {
                if (bestMachines[k] == null && rez[k] < 0.9f) {
                    bestMachines[k] = e.getKey();
                }
                if (rez[k] >bestValues[k] && rez[k] < 0.9f) {
                    bestMachines[k] = e.getKey();
                    bestValues[k] = rez[k];
                }
            }

        }
        int index=0;
        float best=Float.MAX_VALUE;
        Receiver bst= null;
        for(int f=0;f<3;f++){
            if(bestValues[f]<best && bestValues[f]<1.0){
                System.out.println("====> "+bestValues[f]);
                best=bestValues[f];
                index=f;
                bst=bestMachines[index];
            }
        }
        

        /*TODO: findMIN index su rez
                scegliere il Receiver che realizza il minimo
                cosa fare nel caso di parità
        */



        if(bst==null){
            System.out.println("No suitable node found - Changing JOB:"+ toBeScheduled.toString() +" StartTime");
            System.out.println(fits.toString());
            return null;
        }
//        try{
//            Logger lg = new Logger("");
//            lg.binPackerInfo(toBeScheduled,fits,bst);
//        }
//        catch (IOException io){
//            System.out.println("ops");
//        }

        return bst;

    }

    static public Machine findPerResourcesOptimum(Job toBeScheduled,List<Machine> park){
        //vedi metriche di Fenzo, fa la stessa cosa
        Map<Machine,Float> fits = new HashMap<>();

        for (Machine m:park) {
            Map<Job,LocalDateTime> mSchedule =new HashMap<>(m.getCurrentSchedule());
            mSchedule.put(toBeScheduled,toBeScheduled.getStartTime());
            float[] res = m.checkNormalizedResAtTime(toBeScheduled.getStartTime(),mSchedule);
            Float fit = (float)((res[0]+res[1]+res[2])/res.length);
            fits.put(m,fit);
        }

        float bestValue=Float.MIN_VALUE;
        Map.Entry<Machine,Float> bst=null;
        for(Map.Entry<Machine,Float> e:fits.entrySet()){
            if(bst==null && e.getValue()<0.9f){
                bst=e;
            }
            if(e.getValue()>bestValue && e.getValue()<0.9f){
                bst=e;
                bestValue=e.getValue();
            }

        }
        if(bst==null){
            System.out.println("No suitable node found - Changing JOB:"+ toBeScheduled.toString() +" StartTime");
            System.out.println(fits.toString());
            return null;
        }
        return bst.getKey();

    }



}
