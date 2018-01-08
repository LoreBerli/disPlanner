import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *Modella un nodo virtuale del cluster.
 *Possiede delle risorse allocabili (CPU;MEM;DSK), una lista di @see Job e una mappa (@see Job,@see LocalDateTime) come schedule
 */
public class Host implements Receiver,Schedulable{


    /*

    TODO: IMPLEMENTARE getTask();

     */

    private int totalCPU;
    private int totalMEM;
    private int totalDSK;
    private int usedCPU;
    private int usedMEM;
    private int usedDSK;

    private int expectedCPU;
    private int expectedRAM;
    private int expextedDSK;

    private int internalTick;
    private float treshold;
    private List<Schedulable> inExecution;
    private Map<Schedulable,LocalDateTime> currentSchedule;
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

    public Map<Schedulable ,LocalDateTime> getCurrentSchedule(){
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
    public Host(int CPU, int MEM, int DSK, String name){
        this.ID=name;
        this.internalTick=0;
        this.totalCPU=CPU;
        this.totalMEM=MEM;
        this.totalDSK=DSK;
        this.treshold=0.9f;
        this.inExecution=new ArrayList<Schedulable>();
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
    public boolean checkSchedule(Map<Schedulable,LocalDateTime> proposedSchedule){

        float[][] loads=new float[proposedSchedule.values().size()][3];
        int i=0;
        for(Map.Entry<Schedulable,LocalDateTime> j:proposedSchedule.entrySet()){
            loads[i]=checkNormalizedResAtTime(j.getValue(),proposedSchedule);
            for(int k=0;k<3;k++) {
                if (loads[i][k] > this.treshold) {
                    float[] res = loads[i];
                    System.out.println("-----: " + res[0] + " " + res[1] + " " + res[2]);
                }
            }
            //System.out.println("loads["+i+"] = "+loads[i]);
            i++;
        }
        for(int j=0;j<loads.length;j++){
            for(int k =0;k<3;k++) {
                if (loads[j][k]> this.treshold) {
                    System.out.println("fallito per il job:" + j + "-esimo:"+" => "+loads[j][k]);
                    System.out.println(proposedSchedule);
                    return false;
                }
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


    /**
     * Tenta di allocare uno Schedulable, controlla l'utilizzo delle risorse al tStart dello schedulable
     * se il controllo fallisce ritorna false.
     * @param j
     * @return boolean
     */
    public boolean queueJob(Schedulable j){
        Map<Schedulable,LocalDateTime> proposedSchedule=new HashMap<Schedulable,LocalDateTime>(this.currentSchedule);//copio la schedule corrente
        proposedSchedule.put(j,j.getStartTime()); //ci aggiungo il job
        if(checkSchedule(proposedSchedule)){ //testo se la schedule nuova proposta funziona
            this.currentSchedule.put(j,j.getStartTime());
            System.out.println("Ho allocato con successo " +j.getInfo()+" su "+this.ID);
            updateAverageResDuringCurrentSchedule();
            return true;
        }
        System.out.println("Ho fallito nell'allocare un job su "+this.ID);
        return false;
    }

    /**
     * Ritorna una lista di {@link Job} che sarebbero in esecuzione al tempo t secondo la {@link Map<Job,LocalDateTime>}proposedSchedule
     * @param t
     * @param proposedSchedule
     * @return
     */
    private List<Schedulable> jobsAtTimeT (LocalDateTime t, Map<Schedulable,LocalDateTime> proposedSchedule){
        //ordino i job per startingTime
        if(proposedSchedule.size()>0) {
            Map<Schedulable, LocalDateTime> ordered = proposedSchedule.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            List<Schedulable> inExecutionAtTime = new ArrayList<>();

            Set s = ordered.entrySet();

            Iterator it = s.iterator();
            while(it.hasNext()) {
                Map.Entry<Schedulable, LocalDateTime> ss = (Map.Entry) it.next();
                LocalDateTime tmp = ss.getValue();

                if (tmp.isBefore(t)) {//scorro i job con startTime precedente a t
                    if (ss.getKey().getEndTime().isAfter(t)) {//se terminano dopo t
                        inExecutionAtTime.add(ss.getKey());//li aggiungo alla lista dei job in esecuzione al tempo t
                    }
                }
            }
                return inExecutionAtTime;
        }
        return new ArrayList<>();
    }
    /**
     * Ritorna un float tra 0.0 e 1.0 con il "carico" eventuale al tempo con la currentSchedule
     */
    public float checkLoadAtTime(LocalDateTime t,Map<Schedulable,LocalDateTime> proposedSchedule){

        List<Schedulable> inExe = jobsAtTimeT(t,proposedSchedule);

        float normalizedCPU=0f;
        float normalizedMEM=0f;
        float normalizedDSK=0f;
        for(Schedulable j :inExe){

            normalizedCPU+=(float)j.getExpectedCPU()/(float)this.getTotalCPU();
            normalizedMEM+=(float)j.getExpectedMEM()/(float)this.getTotalMEM();
            normalizedDSK+=(float)j.getExpectedDSK()/(float)this.getTotalDSK();
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
        for(Map.Entry<Schedulable,LocalDateTime> e : currentSchedule.entrySet()){
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

    public float[] updateAverageResDuringCurrentSchedule(){

        float[][] loads= new float[currentSchedule.size()][3];
        int i=0;
        for(Map.Entry<Schedulable,LocalDateTime> e : currentSchedule.entrySet()){
            loads[i]=checkNormalizedResAtTime(e.getValue());
            i++;
        }
        float[] avg = {0,0,0};

        for(int j =0;j< currentSchedule.size();j++){
            avg[0]+=loads[j][0];
            avg[1]+=loads[j][1];
            avg[2]+=loads[j][2];
            //System.out.println(loads[j]);
        }
        //System.out.println("---");
        ///workaronud
        //System.out.println("ofFsake prima: "+avg[0]+"-"+avg[1]+"-"+avg[2]);
        avg[0]=avg[0]/(float)loads.length;
        this.expectedCPU=(int)Math.ceil(this.totalCPU*avg[0]);
        avg[1]=avg[1]/(float)loads.length;
        this.expectedRAM=(int)Math.ceil(this.totalMEM*avg[1]);
        avg[2]=avg[2]/(float)loads.length;
        this.expextedDSK=(int)Math.ceil(this.totalDSK*avg[2]);
        //System.out.println("ofFsake: "+avg[0]+"-"+avg[1]+"-"+avg[2]);
        //System.out.println("ofFsake: "+this.expectedCPU+"-"+this.expectedRAM+"-"+this.expextedDSK);
        return avg;
    }

    /**
     * Ritorna un array di float con l'utilizzo delle risorse al tempo t con la currentSchedule
     */
    public int[] checkResAtTime(LocalDateTime t,Map<Schedulable,LocalDateTime> proposedSchedule){

        List<Schedulable> inExe = jobsAtTimeT(t,proposedSchedule);
        int CPU=0;
        int MEM=0;
        int DSK=0;
        for(Schedulable j :inExe){
            CPU+=j.getExpectedCPU();///(float)this.getTotalCPU();
            MEM+=j.getExpectedMEM();///(float)this.getTotalMEM();
            DSK+=j.getExpectedDSK();///(float)this.getTotalDSK();
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
        Map<Schedulable,LocalDateTime> proposed=(Map<Schedulable, LocalDateTime>)proposedSchedule;
        List<Schedulable> inExe = jobsAtTimeT(t,proposed);
        float normalizedCPU=0f;
        float normalizedMEM=0f;
        float normalizedDSK=0f;
        for(Schedulable j :inExe){
            normalizedCPU+=(float)j.getExpectedCPU()/(float)this.getTotalCPU();
            normalizedMEM+=(float)j.getExpectedMEM()/(float)this.getTotalMEM();
            normalizedDSK+=(float)j.getExpectedDSK()/(float)this.getTotalDSK();
            //System.out.println("normCPU_: "+j.getTask().getExpectedCPU());
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
                new FileOutputStream("scheduleLogs/"+this.ID+".txt"), "utf-8"));
        writer.write(this.currentSchedule.keySet().toString());
        try{
            writer.close();
        }
        catch (Exception ex){}

    }



    public void saveLoads() throws IOException{
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("loadsLog/"+this.ID+"Loads.txt"), "utf-8"));
        if(this.currentSchedule.values().size()==0){
            return;}
        LocalDateTime min = Collections.min(this.currentSchedule.values(), new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isAfter(t1)? 1:-1;
            }
        });
        LocalDateTime max = Collections.max(this.currentSchedule.values(),new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {
                return localDateTime.isAfter(t1)? 1:-1;
            }
        });
        System.out.println(min);
        System.out.println(max);
        System.out.println(this.getEndTime());
        this.updateAverageResDuringCurrentSchedule();
        System.out.println("--------"+this.ID+" "+this.currentSchedule.size());
        //writer.write("--------"+this.ID+" "+this.currentSchedule.size()+"\n");
        for(LocalDateTime start=min.minusSeconds(20);start.isBefore(this.getEndTime());start=start.plusSeconds(20)){
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
        this.updateAverageResDuringCurrentSchedule();
        String info=this.ID;
        info+=" CPU:"+this.getTotalCPU();
        info+=" MEM:"+this.getTotalMEM();
        info+=" DSK:"+this.getTotalDSK();
        info+=" schedule_size:"+this.currentSchedule.size();
        info+=" average_load:"+this.averageLoadDuringCurrentSchedule();
        if(this.currentSchedule.size()>0) {
            info += " startTime:" + this.getStartTime().toString();
            info += " endTime:" + this.getEndTime().toString();
        }
        //info+="\n";
        return info;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime min = Collections.min(this.currentSchedule.values(), new Comparator<LocalDateTime>() {

            @Override
            public int compare(LocalDateTime localDateTime, LocalDateTime t1) {

                return localDateTime.isAfter(t1)? 1:-1;
            }
        });
        return min;
    }

    @Override
    public LocalDateTime getEndTime() {

        Map.Entry<Schedulable,LocalDateTime> max = Collections.max(this.currentSchedule.entrySet(),new Comparator <Map.Entry<Schedulable,LocalDateTime>>() {
            @Override
            public int compare(Map.Entry<Schedulable,LocalDateTime> m0,Map.Entry<Schedulable,LocalDateTime> m1) {

                return m0.getValue().plusSeconds(m0.getKey().getExpectedDUR()).isAfter(m1.getValue().plusSeconds(m1.getKey().getExpectedDUR()))? 1:-1;
            }
        });
        return max.getValue().plusSeconds(max.getKey().getExpectedDUR());
    }

    @Override
    public boolean isSchedulable() {
        return currentSchedule.size()>0;
    }

    @Override
    public int getExpectedCPU() {
        return expectedCPU;
    }

    @Override
    public int getExpectedMEM() {
        return this.expectedRAM;
    }

    /**
     * Ritorna la durata, in secondi,del processo
      * @return
     */
    @Override
    public int getExpectedDUR() {
        LocalDateTime start=getStartTime();
        LocalDateTime end=getEndTime();
        Duration offset = Duration.between(start ,end);
        return (int)offset.getSeconds();

    }

    @Override
    public int getExpectedDSK() {
        return this.expextedDSK;
    }

    /**
     * Permette di settare l'accensione dell'host. Aggiorna automaticamente il tStart di tutti i processi shiftandoli
     * @param t
     */
    @Override
    public void setStart(LocalDateTime t) {
        Map<Schedulable,LocalDateTime> different = new HashMap<>(this.currentSchedule);
        long offset=Duration.between(t,this.getStartTime()).getSeconds();
        //this.(this.getStartTime().plusSeconds(offset));
        for(Map.Entry<Schedulable,LocalDateTime> entry:this.currentSchedule.entrySet()){
            different.put(entry.getKey(),entry.getValue().plusSeconds(offset));
        }
        this.currentSchedule=different;



    }

    @Override
    public Task getTask() {
        return null;
    }

    @Override
    public void updateConsumption(){
        this.updateAverageResDuringCurrentSchedule();
    }
}
