import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RealTester {

    public static void main(String[] args) throws IOException,SQLException {
        //Physicals
        Properties properties = new Properties();
        InputStream inp = new FileInputStream("config.properties");
        properties.load(inp);

        /// Logger
        Logger lg=new Logger(properties.getProperty("general_file"));
        List<Host> machines=new ArrayList<>();
        System.out.println(properties.getProperty("sched_table"));
        DbInterface dbInterface= new DbInterface(properties.getProperty("db"),properties.getProperty("user"),properties.getProperty("password"),properties.getProperty("procs_table"),properties.getProperty("sched_table"),properties.getProperty("host_table"));
        dbInterface.cleanDB();
        Host phy0 = new Host(8,8000,400,"phy",0.9f,dbInterface);

        machines.add(phy0);


        //VMs////
        List<Host> vmPark=dbInterface.getMachines();
        ////////

        List<Job> jobs = dbInterface.getDockers();
        //Jobs////////
//        List<Job> jobs = new ArrayList<>();
//        Task t = new Task("sleep 30",2,200,20,120);
//        Task t2 = new Task("ps",2,200,20,120);
//        Job j = new Job("alpine:latest",t, LocalDateTime.now().plusSeconds(30),1,true,null);
//        Job j2 = new Job("alpine:latest",t2,LocalDateTime.now().plusSeconds(20),1,true,null);
//        jobs.add(j);
//        jobs.add(j2);
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
            //deam.vMScan(m,m.getCurrentSchedule());

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
