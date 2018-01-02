import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *Modella un nodo virtuale del cluster.
 *Possiede delle risorse allocabili (CPU;MEM;DSK), una lista di @see Job e una mappa (@see Job,@see LocalDateTime) come schedule
 */
public class Machine implements Receiver,Schedulable{

    private int totalCPU;
    private int totalMEM;
    private int totalDSK;
    private int usedCPU;
    private int usedMEM;
    private int usedDSK;
    private int internalTick;
    private float treshold;
    private List<Job> inExecution;
    private Map<Job,LocalDateTime> currentSchedule;
    public String ID;
    public int getTotalCPU(){
        return totalCPU;
    }

    public int getTotalDSK() {
        return totalDSK;
    }

    public int getTotalMEM() {
        return totalMEM;
    }

    public int getUsedCPU() {
        return usedCPU;
    }

    public int getUsedDSK() {
        return usedDSK;
    }

    public int getUsedMEM() {
        return usedMEM;
    }

    public void setUsedCPU(int usedCPU) {
        if(usedCPU<=totalCPU){
            this.usedCPU = usedCPU;
        }

    }

    public Map<Job ,LocalDateTime> getCurrentSchedule(){
        return this.currentSchedule;
    }

    public void setUsedDSK(int usedDSK) {
        if(usedDSK<=totalDSK) {
            this.usedDSK = usedDSK;
        }
    }

    public void setUsedMEM(int usedMEM) {
        if(usedMEM<=totalMEM){
        this.usedMEM = usedMEM;}
    }
    public Machine(int CPU,int MEM,int DSK,String name){
        this.ID=name;
        this.internalTick=0;
        this.totalCPU=CPU;
        this.totalMEM=MEM;
        this.totalDSK=DSK;
        this.treshold=0.9f;
        this.inExecution=new ArrayList<>();
        this.currentSchedule=new HashMap<>();
        System.out.println("CREATA_ "+this.getInfo());
    }
//    private boolean checkForAcceptance(Job j){
//        //Controlla se la richiesta di risorse è fisicamente possibile
//        if((j.getTask().getExpectedCPU()<=this.totalCPU-this.usedCPU)&(j.getTask().getExpectedMEM()<=this.totalMEM-this.usedMEM)&(j.getTask().getExpectedDSK()<=this.totalDSK-this.usedDSK)){
//            return true;}
//        return false;
//    }

//    public Map<Job,LocalDateTime> pickTheRemovables(Map<Job,LocalDateTime> proposedSchedule){
//        /*
//        Dovrebbe scegliere JOBS dalla proposedSchedule fino a che il loadFactor non scende sotto 1.0
//         */
//        Map<Job,Float> sortedJobs = sortJobsByRemovability(proposedSchedule);
//        Map<Job,LocalDateTime> toBeRemoved = new LinkedHashMap<>();
//        Map<Job,LocalDateTime> betterSchedule=new HashMap<>(proposedSchedule);
//        for(Map.Entry<Job,Float> jb:sortedJobs.entrySet()){
//            if(!checkSchedule(betterSchedule)){
//               toBeRemoved.put(jb.getKey(),proposedSchedule.get(jb.getKey()));
//               betterSchedule.remove(jb.getKey());
//            }
//        }
//        System.out.println(betterSchedule.size() + "   " + proposedSchedule.size());
//        return toBeRemoved;
//
//    }

    /**
     *Per ogni JOB controllo al tempo di start del Job il carico sulla macchina
     */
    public boolean checkSchedule(Map<Job,LocalDateTime> proposedSchedule){

        float[] loads=new float[proposedSchedule.values().size()];
        int i=0;
        for(Map.Entry<Job,LocalDateTime> j:proposedSchedule.entrySet()){
            loads[i]=checkLoadAtTime(j.getValue(),proposedSchedule);
            if(loads[i]>0.9f){
                float[] res = checkNormalizedResAtTime(j.getValue(),proposedSchedule);
                System.out.println("-----: "+res[0]+" "+res[1]+" "+res[2]);
            }
            //System.out.println("loads["+i+"] = "+loads[i]);
            i++;
        }
        for(int j=0;j<loads.length;j++){
            if(loads[j]>0.9f){
                System.out.println("fallito per il job:"+j+"-esimo:"+loads[j]);
                System.out.println(proposedSchedule);
                return false;
            }
        }
        return true;

    }

//    public Map<Job,Float> sortJobsByRemovability(Map<Job,LocalDateTime> proposedSchedule){
//        //TODO: ordinati per  r(j) chiamare checkSchedule fino a che non resistuisce true, rimuovendo i job di volta in volta
//        Map<Job,Float> toBeRemoved=new HashMap<>();
//        Map<Job,Float> sorted=new LinkedHashMap<>();
//
//        for(Map.Entry<Job,LocalDateTime> e:proposedSchedule.entrySet()){
//            toBeRemoved.put(e.getKey(),e.getKey().removability());
//            //sorted.put(e.getValue().removability(),e.getValue());
//
//        }
//
//        toBeRemoved.entrySet().stream()
//                        .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
//                        .forEach(k -> sorted.put(k.getKey(),k.getValue()));
//        //System.out.println(sorted);
//        return sorted;
//
//    }

    public String state(){
        //TODO forse un Override di ToString ????
        return this.ID+"  -:CPU:"+this.getUsedCPU()+" MEM:"+this.getUsedMEM()+" DSK:"+this.getTotalDSK();
    }

    public boolean queueJob(Schedulable sj){
        Job j = (Job)sj;
        Map<Job,LocalDateTime> proposedSchedule=new HashMap<Job,LocalDateTime>(this.currentSchedule);
        proposedSchedule.put(j,j.getStartTime());
        if(checkSchedule(proposedSchedule)){
            currentSchedule.put(j,j.getStartTime());
            //System.out.println("Ho allocato con successo " +j.getTask().getDescriptor()+" su "+this.ID);
            return true;
        }
        System.out.println("Ho fallito nell'allocare un job su "+this.ID);
        System.out.println("job"+j.getTask());
        return false;
    }

    /**
     * Ritorna una lista di {@link Job} che sarebbero in esecuzione al tempo t secondo la {@link Map<Job,LocalDateTime>}proposedSchedule
     * @param t
     * @param proposedSchedule
     * @return
     */
    private List<Job> jobsAtTimeT (LocalDateTime t,Map<Job,LocalDateTime> proposedSchedule){
        //ordino i job per startingTime
        Map<Job,LocalDateTime> ordered=proposedSchedule.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<Job> inExecutionAtTime=new ArrayList<>();

        Set s = ordered.entrySet();

        Iterator it = s.iterator();
        //System.out.println("Iterator.size= "+s.size());
        if(it.hasNext()){
            Map.Entry<Job,LocalDateTime> ss = (Map.Entry)it.next();
            LocalDateTime tmp = ss.getValue();

            while(it.hasNext() && tmp.isBefore(t)){//scorro i job con startTime precedente a t
                if(ss.getKey().getEndTime().isAfter(t)){//se terminano dopo t
                    inExecutionAtTime.add(ss.getKey());//li aggiungo alla lista dei job in esecuzione al tempo t
                }
                ss=(Map.Entry) it.next();
                tmp=ss.getValue();

            }
            //System.out.println("Ci sono "+inExecutionAtTime.size()+" jobs al tempo "+t.toString());
            //System.out.println(inExecutionAtTime.toString());
            //System.out.println("InExecutionAtTime.size= "+inExecutionAtTime.size());
            return inExecutionAtTime;}
        return new ArrayList<Job>();
    }
    /**
     * Ritorna un float tra 0.0 e 1.0 con il "carico" eventuale al tempo con la currentSChedule
     */
    public float checkLoadAtTime(LocalDateTime t,Map<Job,LocalDateTime> proposedSchedule){

        List<Job> inExe = jobsAtTimeT(t,proposedSchedule);

        float normalizedCPU=0f;
        float normalizedMEM=0f;
        float normalizedDSK=0f;
        for(Job j :inExe){
            normalizedCPU+=(float)j.getTask().getExpectedCPU()/(float)this.getTotalCPU();
            normalizedMEM+=(float)j.getTask().getExpectedMEM()/(float)this.getTotalMEM();
            normalizedDSK+=(float)j.getTask().getExpectedDSK()/(float)this.getTotalDSK();
            //System.out.println("normCPU_: "+normalizedCPU);
        }
        return (normalizedCPU+normalizedMEM+normalizedDSK)/3f;
    }

    public float checkLoadAtTime(LocalDateTime t){
        return checkLoadAtTime(t,this.currentSchedule);
    }

    /**
     *Ritorna la media del carico durante la durata della schedule corrente.
     */
    public float averageLoadDuringCurrentSchedule(){

        float[] loads= new float[currentSchedule.size()];
        int i=0;
        for(Map.Entry<Job,LocalDateTime> e : currentSchedule.entrySet()){
            loads[i]=checkLoadAtTime(e.getValue());
            i++;
        }
        float avg = 0f;
        for(int j =0;j< loads.length;j++){
            avg+=loads[j];
            //System.out.println(loads[j]);
        }
        //System.out.println("---");
        avg=avg/loads.length;
        return avg;
    }
    /**
     * Ritorna un array di float con l'utilizzo delle risorse al tempo t con la currentSchedule
     */
    public int[] checkResAtTime(LocalDateTime t,Map<Job,LocalDateTime> proposedSchedule){

        List<Job> inExe = jobsAtTimeT(t,proposedSchedule);
        int CPU=0;
        int MEM=0;
        int DSK=0;
        for(Job j :inExe){
            CPU+=j.getTask().getExpectedCPU();///(float)this.getTotalCPU();
            MEM+=j.getTask().getExpectedMEM();///(float)this.getTotalMEM();
            DSK+=j.getTask().getExpectedDSK();///(float)this.getTotalDSK();
            //System.out.println("normCPU_: "+normalizedCPU);
        }
        int[] res = new int[3];
        res[0]=CPU;
        res[1]=MEM;
        res[2]=DSK;
        return res;
    }

    /**
     *Ritorna un array di float tra 0.0 e 1.0 con il "carico" per ogni risorsa al tempo t con la currentSChedule
     */

    public float[] checkNormalizedResAtTime(LocalDateTime t,Map<? extends Schedulable,LocalDateTime> proposedSchedule){

        //Map<Job,LocalDateTime> proposedSchedule=this.currentSchedule;
        Map<Job,LocalDateTime> proposed=(Map<Job, LocalDateTime>)proposedSchedule;
        List<Job> inExe = jobsAtTimeT(t,proposed);
        float normalizedCPU=0f;
        float normalizedMEM=0f;
        float normalizedDSK=0f;
        for(Job j :inExe){
            normalizedCPU+=(float)j.getTask().getExpectedCPU()/(float)this.getTotalCPU();
            normalizedMEM+=(float)j.getTask().getExpectedMEM()/(float)this.getTotalMEM();
            normalizedDSK+=(float)j.getTask().getExpectedDSK()/(float)this.getTotalDSK();
            System.out.println("normCPU_: "+j.getTask().getExpectedCPU());
        }
        float[] res = new float[3];
        res[0]=normalizedCPU;
        res[1]=normalizedMEM;
        res[2]=normalizedDSK;
        return res;
    }

    public float[] checkNormalizedResAtTime(LocalDateTime t){
        return checkNormalizedResAtTime(t,this.currentSchedule);
    }

    public int[] checkResAtTime(LocalDateTime t){
        return checkResAtTime(t,this.currentSchedule);
    }

    public void saveSchedule() throws IOException{
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(this.ID+".txt"), "utf-8"));
        writer.write(this.currentSchedule.keySet().toString());
        try{
            writer.close();
        }
        catch (Exception ex){}

    }



    public void saveLoads() throws IOException{
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("loadsLog/"+this.ID+"Loads.txt"), "utf-8"));
        if(this.currentSchedule.values().size()<1){
            return;}
        LocalDateTime min = Collections.min(this.currentSchedule.values(), new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isBefore(t1)? 0:1;
            }
        });
        LocalDateTime max = Collections.max(this.currentSchedule.values(),new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isBefore(t1)? 0:1;
            }
        });
        System.out.println(min);
        System.out.println(max);
        System.out.println("------------");
        for(LocalDateTime start=min;start.isBefore(max);start=start.plusSeconds(20)){
            float[] res = this.checkNormalizedResAtTime(start);
            writer.write(" "+start.toString()+","+this.checkLoadAtTime(start)+","+this.jobsAtTimeT(start,this.currentSchedule).size()+","+Float.toString(res[0])+","+Float.toString(res[1])+","+Float.toString(res[2])+"\n");
        }
        try{
            writer.close();
        }
        catch (Exception ex){}

    }

    @Override
    public String toString(){
        return getInfo();
    }
    public String getInfo(){
        String info=this.ID;
        info+=" CPU:"+this.getTotalCPU();
        info+=" MEM:"+this.getTotalMEM();
        info+=" DSK:"+this.getTotalDSK();
        info+=" schedule_size:"+this.currentSchedule.size();
        info+=" average_load:"+this.averageLoadDuringCurrentSchedule();
        info+="\n";
        return info;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime min = Collections.min(this.currentSchedule.values(), new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isBefore(t1)? 0:1;
            }
        });
        return min;
    }

    @Override
    public void setStart(LocalDateTime t) {
        //TODO : SHIFTA LA SCHEDULE DI t

    }

    @Override
    public Task getTask() {
        return null;
    }

    @Override
    public LocalDateTime getEndTime(){
        LocalDateTime min = Collections.min(this.currentSchedule.values(), new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isBefore(t1)? 0:1;
            }
        });
        LocalDateTime max = Collections.max(this.currentSchedule.values(),new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isBefore(t1)? 0:1;
            }
        });
        // LocalDateTime non supporta il confronto fra due istanze.porcaputtana
        Duration offset = Duration.between(max,min);
        return min.plusSeconds(offset.getSeconds());
    }

    @Override
    public boolean isSchedulable() {
        return false;
    }

    @Override
    public void updateConsumption(){
        return;
    }

    @Override
    public int getExpectedCPU() {
        return 0;
    }

    @Override
    public int getExpectedMEM() {
        return 0;
    }

    @Override
    public int getExpectedDUR() {
        return 0;
    }

    @Override
    public int getExpectedDSK() {
        return 0;
    }
}
