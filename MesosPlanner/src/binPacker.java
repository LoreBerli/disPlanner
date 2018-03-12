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
     * Dati una lista di {@link Receiver} ed un {@link Schedulable} seleziona l'Host ottimale per l'esecuzione dello {@link Schedulable}
     * @param toBeScheduled
     * @param park
     * @return {@link Receiver}
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
            //TODO: cambiare sta cosa
            Float[] jesusReally=new Float[3];
            jesusReally[0]=res[0];
            jesusReally[1]=res[1];
            jesusReally[2]=res[2];
            //System.out.println( "BINPACKIN' "+m.getInfo()+" "+res[0]+" "+res[1]+" "+res[2]);
            //controllo la fitness del nodo con il nuovo job
            fits.put(m,jesusReally);
        }

        float[] bestValues=new float[]{-1f,-1f,-1f};

        Receiver[] bestMachines=new Receiver[3];

        for(Map.Entry<Receiver,Float[]> e:fits.entrySet()){
            Float[] rez = e.getValue();
            for(int k=0;k<3;k++) {
//                if (bestMachines[k] == null && rez[k] < 0.9f) {
//                    bestMachines[k] = e.getKey();
//                    bestValues[k] = rez[k];
//                }
                if (rez[k] >bestValues[k] && rez[k] < e.getKey().getTreshold()) {
                    bestMachines[k] = e.getKey();
                    bestValues[k] = rez[k];
                }
            }

        }
        float best=Float.MAX_VALUE;
        Receiver bst= null;
        for(int f=0;f<3;f++){
            //TODO: qui c'è il baho.(c'era?)
            if(bestMachines[f]!=null) {
                if (bestValues[f] < best && fits.get(bestMachines[f])[0] < 0.9f && fits.get(bestMachines[f])[1] < 0.9f && fits.get(bestMachines[f])[2] < 0.9f) {
                    //System.out.println("####BINPACKER ====> " + bestValues[f] + "(" + bestValues[0] + "," + bestValues[1] + "," + bestValues[2] + ")"+ bestMachines[f].getInfo());
                    best = bestValues[f];
                    bst = bestMachines[f];
                }
            }
            else{
                //System.out.println("Beep"+f);
            }
        }
        

        /*TODO: findMIN index su rez
                scegliere il Receiver che realizza il minimo
                cosa fare nel caso di parità
        */



        if(bst==null){
            System.out.println("###BINPACKER No suitable Receiver found - Changing Schedulable:"+ toBeScheduled.toString() +" StartTime");
            //System.out.println(fits.toString());
            return null;
        }
// try{
//            Logger lg = new Logger("");
//            lg.binPackerInfo(toBeScheduled,fits,bst);
//        }
//        catch (IOException io){
//            System.out.println("ops");
//        }

        return bst;

    }




}
