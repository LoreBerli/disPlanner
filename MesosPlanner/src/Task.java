/**
 * Task modella il concetto di processo, ha delle risorse richieste ed una durata.
 * I {@link Task} sono diversi dai {@link Job} in quanto un Job è formato da un {@link Task} e da tempi di esecuzione e da {@link Machine} su cui eseguire.
 */
public class Task {
    /*
    Task modella il concetto di processo, ha delle risorse richieste ed una durata
     */

    //TODO : deve avere una deadline (?)

    private String descriptor;
    private int expectedCPU;
    private int expectedMEM;
    private int expectedDSK;
    private int expectedDUR;
    public Task(String descriptor,int expectedCPU,int expectedMEM,int expectedDSK,int expectedDUR){
        this.descriptor=descriptor;
        this.expectedCPU=expectedCPU;
        this.expectedMEM=expectedMEM;
        this.expectedDSK=expectedDSK;
        this.expectedDUR=expectedDUR;
    }


    public int getExpectedCPU(){
        return this.expectedCPU;
    }
    public int getExpectedMEM(){
        return this.expectedMEM;
    }
    public String getDescriptor(){
        return this.descriptor;
    }
    public int getExpectedDUR(){
        return this.expectedDUR;
    }
    public int getExpectedDSK() {
        return expectedDSK;
    }
    @Override
    public String toString(){
        return "task:"+this.descriptor+" CPU: "+this.expectedCPU+" MEM: "+this.expectedMEM+" DSK: "+this.expectedDSK+" DUR: "+this.expectedDUR;
    }
}
