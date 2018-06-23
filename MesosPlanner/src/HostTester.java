import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HostTester {



    public static void main(String[] args) throws IOException,SQLException{
        //Physicals
        Logger lg=new Logger("\\logs");
        List<Host> machines=new ArrayList<>();
        DbInterface dbInterface= new DbInterface("new_sched","root","911nirvana911","null","new_sched");
        dbInterface.cleanDB();
        Host phy0 = new Host(8,8000,400,"phy0",0.9f,dbInterface);
        Host phy1 = new Host(8,8192,100,"phy1",0.9f,dbInterface);
        Host phy2 = new Host(12,12000,400,"phy2",0.9f,dbInterface);
        machines.add(phy0);
        machines.add(phy1);
        machines.add(phy2);

        //VMs////
        List<Host> vmPark=new ArrayList<>();
        for(int i=0;i<8;i++){
            Host tmp;
            if(i%2==0){
            tmp=new Host(4,2048,100,"VM"+Integer.toString(i)+"high",0.9f,dbInterface);}
            else{
                tmp=new Host(2,1024,50,"VM"+Integer.toString(i)+"low",0.9f,dbInterface);
            }
            vmPark.add(tmp);
        }
        ////////


        //Jobs////////
        List<Job> jobs = TestingUtils.generateDummyJobs(60,60);
        ////////////////////////////////////////////

//        /////////CONSTRAINT TEST
//        Task t = new Task("unmovable",2,200,20,120);
//        Job j = new Job(t, LocalDateTime.now().plusSeconds(300),1,false,vmPark.get(1));
//        System.out.println("##### CONSTRAINT TEST: UNMOVABLE JOB ALLOCATO SU:"+j.getAssignedMachine().getInfo());
//        jobs.add(j);
//
//        Host vm0 = vmPark.get(1);
//
//        vm0.setSchedulability(false);
//        vm0.setReceiver(phy1);
//        System.out.println("##### CONSTRAINT TEST:"+vm0.ID+" set not to move from "+phy1.ID);

        ////////////////////////////////////////////

        //Manager vm//
        ScheduleManager vmManager = new ScheduleManager(vmPark);

        // Passo allo scheduler una lista di Jobs//
        vmManager.setNewSchedule(jobs);

        // Alloco i Jobs
        vmManager.allocateJobs();


        ////////////////////HOT ADD///////////////

        //////////////TEST HOT ADD
//        Task ta = new Task("spezial",1,200,3,60);
//
//        Job kl = new Job(ta,LocalDateTime.now().plusSeconds(3600),0,true,null);
//
//        System.out.println("####### HOT ADD");
//        System.out.println("####### HOT ADD: HOT JOB:"+kl.getInfo());
//        vmManager.addHotJob(kl);

        //////////////////////////////////////////

        //Manager nodi fisici

        ScheduleManager machinesManager = new ScheduleManager(machines);

        machinesManager.setNewSchedule(vmPark);

        machinesManager.allocateJobs();

        //////////////////////////////////////////

        PlannerDeamon deam = new PlannerDeamon();

        //////////////////////////////////////////
        for(Receiver m:machines){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
            //m.saveScheduleToDB();
            deam.vMScan(m,m.getCurrentSchedule());

        }

        //deam.run();
        for(Receiver m:vmPark){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();

            m.saveScheduleToDB();
            deam.dockerScan(m,m.getCurrentSchedule());
        }
        deam.run();





    }
}


