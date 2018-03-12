import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *Gestisce le schedule di ogni Host.
 *Propone schedule ai Host, se la proposta fallisce raccoglie i removable-Schedulables e prova a ri-allocarli (con binPacker.FindOptimum())
 *Funziona come interfaccia esterna esponendo metodi per accogliere Schedulable.
 * Created by cioni on 08/10/17.
 */

//TODO: Dinamica degli Schedulable "non schedulable".
// Assegnarli PRIMA dello scheduling, rimuoverli dal pool di quelli schedulabili e iniziare lo scheduling degli altri.



public class ScheduleManager {
    /*

     */
    private List<? extends Schedulable> toBeAllocated;
    private List<Schedulable> allocated;
    private List<? extends Schedulable> failedToAllocate;
    private List<? extends Receiver> nodes;


    public ScheduleManager (List<? extends Receiver> toBeManaged){
        this.nodes=toBeManaged;
    }


    /**
     * Inserisce una nuova lista di elementi Schedulable e li ordina per startTime
     * @param schedule
     */
    public void setNewSchedule(List<? extends Schedulable> schedule) {
        this.toBeAllocated = schedule;
        this.failedToAllocate=new ArrayList<>(schedule);
        this.allocated=new ArrayList<>();
        sortJobs();
    }

    /**
     * Scorre la lista degli Schedulable controllando quali siano vincolati o già schedulati.
     */
    public void checkSchedulability(){

        List<Schedulable> clean= new ArrayList<>();
        for(Schedulable s : this.toBeAllocated){
            if (s.isSchedulable()){
                clean.add(s);
            }
            else {
                if(s.getRecevier()!=null){
                s.getRecevier().queueJob(s);}
                System.out.println("###SCHEDULE MANAGER "+s.getInfo()+" is not schedulable");
            }
        }
        this.toBeAllocated=clean;
    }
    /**
*Scorre la lista dei Job da essere allocati e tenta di assegnarli alle macchine.
     * Se il binPacker fallisce nel restituire un nodo ottimale , allora si cambia lo startTime del processo
    *Ritorna True se e solo se tutti i Job sono stati assegnati ad una macchina.
 */
    public boolean allocateJobs(){

        for (Schedulable j:toBeAllocated){
            j.updateConsumption();
            Receiver best = binPacker.findOptimum(j,nodes);
            while(best==null){
                //TODO : qui fa schifo!
                j.updateConsumption();
                j.setStart(j.getStartTime().plusSeconds(20));
                best = binPacker.findOptimum(j,nodes);
            }
            if(best!=null) {
                //j.setAssignedMachine(best);
                if (!best.queueJob(j)) {

                    System.out.println("###SCHEDULE MANAGER Questo non dovrebbe succedere. Il nodo scelto dal binPacker ha rifiutato il job");
                    //System.out.println("Scelto il nodo " + best.ID + " per il job" + j.toString());
                    System.out.println("###SCHEDULE MANAGER che all'istante " + j.getStartTime() + " ha un lf :" + best.checkLoadAtTime(j.getStartTime()));
                    float[] res = best.checkNormalizedResAtTime(j.getStartTime());
                    System.out.println("###SCHEDULE MANAGER con utilizzo risorse pari a: " + res[0] + " " + res[1] + " " + res[2]);
                    return false;

                }
                allocated.add(j);
                failedToAllocate.remove(j);

            }
            else{
                System.out.println("###SCHEDULEMANAGER Ho allocato " + allocated.size() + " jobs su "+toBeAllocated.size());
                System.out.println("###SCHEDULEMANAGER NON ho allocato " + failedToAllocate.size() + " jobs su "+toBeAllocated.size());
                return false;
            }
        }
        //System.out.println("SCHEDULABLES:");
        //System.out.println(toBeAllocated);
        return true;
    }

    /**
Aggiunge un nuovo Schedulable alla lista e lo schedula senza rimuovere i precedenti allocamenti.
 */
    public void addHotJob(Schedulable j){
        List<? extends Schedulable> planB=this.toBeAllocated;
        List<Schedulable> newOne= new ArrayList<>();
        newOne.add(j);
        this.toBeAllocated=newOne;
        ///////NOOOO
        // NON è SICURO
        // COSA succede se ri alloco in esecuzione
        
        if (!allocateJobs()){
            this.toBeAllocated=planB;
            allocateJobs();
            System.out.println("####SCHEDULE MANAGER HOT ADD FALLITO");
            System.out.println("####SCHEUDLE MANAGER Riprovo schedulando tutti i job (COLD ADD)");
        }

    }

    /**
     * Ordina gli Schedulable inseriti in toBeAllocated per startTime
     */
    public void sortJobs(){
        this.checkSchedulability();
        this.toBeAllocated.sort(new Comparator<Schedulable>() {
            @Override
            public int compare(Schedulable job, Schedulable t1) {

                if (job.getStartTime().isAfter(t1.getStartTime())){
                    return 1;
                }
                else {
                    return -1;
                }
            }
        });


    }

}
