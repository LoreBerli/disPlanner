import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  Un Job è un task assegnato ad una macchina.
 */

/* DB SCHEMA
+-----------------------+--------------------+------+-----+---------+----------------+
| Field                 | Type               | Null | Key | Default | Extra          |
+-----------------------+--------------------+------+-----+---------+----------------+
| ID                    | int(10) unsigned   | NO   | PRI | NULL    | auto_increment |*
| JOB_NAME              | varchar(255)       | YES  | MUL | NULL    |                |*
| JOB_GROUP             | varchar(255)       | YES  |     | NULL    |                |
| DATE                  | datetime           | YES  | MUL | NULL    |                |*
| TRIGGER_NAME          | varchar(255)       | YES  |     | NULL    |                |
| TRIGGER_GROUP         | varchar(255)       | YES  |     | NULL    |                |
| PREV_FIRE_TIME        | datetime           | YES  |     | NULL    |                |
| NEXT_FIRE_TIME        | datetime           | YES  |     | NULL    |                |*
| REFIRE_COUNT          | bigint(7) unsigned | YES  |     | NULL    |                |
| RESULT                | longtext           | YES  |     | NULL    |                |
| SCHEDULER_INSTANCE_ID | varchar(255)       | YES  |     | NULL    |                |
| SCHEDULER_NAME        | varchar(255)       | YES  |     | NULL    |                |
| IP_ADDRESS            | varchar(255)       | YES  |     | NULL    |                |*
| STATUS                | varchar(255)       | YES  | MUL | NULL    |                |
| LOGGER                | varchar(255)       | YES  |     | NULL    |                |
| LEVEL                 | varchar(45)        | YES  |     | NULL    |                |
| MESSAGE               | longtext           | YES  |     | NULL    |                |
| FIRE_INSTANCE_ID      | varchar(255)       | YES  |     | NULL    |                |
| JOB_DATA              | blob               | YES  |     | NULL    |                |
| PROGRESS              | varchar(45)        | YES  |     | NULL    |                |*
+-----------------------+--------------------+------+-----+---------+----------------+

 */
public class Job implements Schedulable{
    /*
    Un Job è un task assegnato ad una macchina
    */

    //TODO: deadline come LocalDateTime , checkDeadline(LocalDateTime proposedStart) , cambiare tutti gli ''int start'' in ''LocalDateTime tStart''
    private Task tsk;
    private boolean reAllocable;
    private int priority;

    /**
     * tempo di inizio del {@link Job}
     */
    private LocalDateTime tStart;

    /**
     * tempo di fine del {@link Job}. Stimato come tStart+secondsDuration
     */
    private LocalDateTime tEnd;//stimato, = tStart + secondsDuration
    /**
     * tempo durata del {@link Job}
     */
    private int secondsDuration; //in secondi
    private LocalDateTime deadline;
    /**
     * {@link Machine} a cui il {@link Job} è stato assegnato
     */
    private Receiver assignedMachine;

    public Job(Task tsk,LocalDateTime start,int priority,boolean reAllocable,Receiver m){

        this.tsk=tsk;
        this.tStart=start;
        this.priority = priority;
        this.reAllocable=reAllocable;
        this.assignedMachine=m;
        this.secondsDuration =tsk.getExpectedDUR();
        if(!reAllocable){assert this.assignedMachine!=null;}
        //System.out.println("EXPECTED DURATION INITIALIZED TO "+this.secondsDuration);
    }

    public Job(Task tsk,int start,int end,Machine m){

        this.tsk=tsk;
        this.assignedMachine=m;
        this.secondsDuration =tsk.getExpectedDUR();
        //System.out.println("EXPECTED DURATION INITIALIZED TO "+this.secondsDuration);
    }

    public Job(Job j){
        this.tsk=j.tsk;
        this.assignedMachine=j.assignedMachine;
        this.secondsDuration =tsk.getExpectedDUR();
    }
    public Receiver getRecevier(){
        return this.assignedMachine;
    }

    public LocalDateTime getStartTime(){
        return tStart;
    }
    public LocalDateTime getEndTime(){
        return tStart.plusSeconds(this.secondsDuration);
    }

    @Override
    public boolean isSchedulable() {
        return reAllocable;
    }

    @Override
    public int getExpectedCPU() {
        return this.tsk.getExpectedCPU();
    }

    @Override
    public int getExpectedMEM() {
        return this.tsk.getExpectedMEM();
    }

    @Override
    public int getExpectedDUR() {
        return this.tsk.getExpectedDUR();
    }

    @Override
    public int getExpectedDSK() {
        return this.tsk.getExpectedDSK();
    }

    /**
     * Calcola quanto il {@link Job} sia "removable".
     * @return float
     */
//    public float removability(){//metrica di "importanza" per i JOB
//        if(!reAllocable){
//            return 0f;
//        }
//        else {
//            float relativeCPU = (float)this.tsk.getExpectedCPU()/(float)this.assignedMachine.getTotalCPU();
//            float relativeMEM = (float)this.tsk.getExpectedMEM()/(float)this.assignedMachine.getTotalMEM();
//            float relativeDSK = (float)this.tsk.getExpectedDSK()/(float)this.assignedMachine.getTotalDSK();
//            float resourcesSUM=relativeCPU+relativeMEM+relativeDSK;
//            float r=(float)(priority+1)*resourcesSUM*(float) secondsDuration;
//
//            return r;
//
//        }
//    }
    public int getSecondsDuration(){
        return secondsDuration;
    }
    public LocalDateTime getStart(){
        return this.tStart;
    }
    public void setAssignedMachine(Machine m){
        this.assignedMachine=m;
    }
    public Receiver getAssignedMachine(){
        return this.assignedMachine;
    }
    public Task getTask(){
        return this.tsk;
    }

    @Override
    public String getInfo() {
        return toString();
    }

    public void setStart(LocalDateTime tm){
        this.tStart=tm;
    }

    @Override
    public String toString() {
        return this.tsk.getDescriptor()+" , "+ this.tStart.format(DateTimeFormatter.ISO_LOCAL_TIME) + " , " +this.getSecondsDuration()+";\n";
    }

    @Override
    public void updateConsumption(){
        return;
    }

}
