import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HostTester {
    //TODO: 3 nodi fisici , 8 VMs ,1000 processi
    public static void main(String[] args) throws IOException {
        //Physicals
        Logger lg=new Logger("\\logs");
        List<Host> machines=new ArrayList<>();
        Host phy0 = new Host(8,8000,400,"phy0");
        Host phy1 = new Host(8,8192,100,"phy1");
        Host phy2 = new Host(12,12000,400,"phy2");
        machines.add(phy0);
        machines.add(phy1);
        machines.add(phy2);
        //VMs
        List<Host> vmPark=new ArrayList<>();
        for(int i=0;i<8;i++){
            Host tmp;
            if(i%2==0){
            tmp=new Host(4,2048,100,"VM"+Integer.toString(i)+"high");}
            else{
                tmp=new Host(2,1024,50,"VM"+Integer.toString(i)+"low");
            }
            vmPark.add(tmp);
        }
        //Jobs
        List<Job> jobs = TestingUtils.generateDummyJobs(500,20);
        //////////////////////////////////////////
        ScheduleManager vmManager = new ScheduleManager(vmPark);

        // Passo allo scheduler una lista di Jobs
        vmManager.setNewSchedule(jobs);

        // Alloco i Jobs
        vmManager.allocateJobs();

        for(Receiver m:vmPark){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
        }
        //////////////////////////////////////////
        ScheduleManager machinesManager = new ScheduleManager(machines);

        machinesManager.setNewSchedule(vmPark);

        machinesManager.allocateJobs();
        for(Receiver m:machines){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
        }


    }
}
