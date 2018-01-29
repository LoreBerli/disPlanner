import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HostTester {



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
        List<Job> jobs = TestingUtils.generateDummyJobs(300,60);
        ////////////////////////////////////////////
        Task t = new Task("unmovable",2,200,20,120);
        Job j = new Job(t, LocalDateTime.now().plusSeconds(300),1,false,vmPark.get(1));
        System.out.println("UNMOVABLE SU:"+j.getAssignedMachine().getInfo());
        jobs.add(j);

        Host vm0 = vmPark.get(1);

        vm0.setSchedulability(false);
        vm0.setReceiver(phy1);
        System.out.println("set to not move;:"+vm0.ID+ " from "+phy1.ID);
        ////////////////////////////////////////////
        //Manager vm
        ScheduleManager vmManager = new ScheduleManager(vmPark);

        // Passo allo scheduler una lista di Jobs
        vmManager.setNewSchedule(jobs);

        // Alloco i Jobs
        vmManager.allocateJobs();



        //////////////////////////////////////////
        //Manager nodi fisici
        ScheduleManager machinesManager = new ScheduleManager(machines);

        machinesManager.setNewSchedule(vmPark);

        machinesManager.allocateJobs();

        ////////////////////////////////////////

        PlannerDeamon deam = new PlannerDeamon();

        //////////////////////////
        for(Receiver m:machines){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
            deam.vMScan(m,m.getCurrentSchedule());

        }

        //deam.run();
        for(Receiver m:vmPark){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
            deam.dockerScan(m,m.getCurrentSchedule());
        }
        deam.run();





    }
}


