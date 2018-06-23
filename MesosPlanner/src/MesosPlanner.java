import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



public class MesosPlanner {

    public static void newSchedule(Boolean test) throws IOException,SQLException {
        ///////CARICO il file di properties
        Properties properties = new Properties();
        InputStream inp = new FileInputStream("config.properties");
        properties.load(inp);

        /// Logger
        Logger lg=new Logger(properties.getProperty("general_file"));


        /// Host = VM
        List<Host> park=new ArrayList<>();

        /// Job = Docker
        List<Job> jobs=new ArrayList<>();
        if(!test) {
            /// Interfaccia Database
            DbInterface db = new DbInterface(properties.getProperty("db"),properties.getProperty("user"),properties.getProperty("password"),properties.getProperty("procs_table"),properties.getProperty("sched_table"));
            try {
                park = db.getRealMachines();
                //COME SI TROVANO I JOBS??
                // per ora :
                jobs = TestingUtils.generateDummyJobs(300, 40);


                //jobs = db.getMachines();
            } catch (SQLException sql) {
            }

        }
//        else{
//            park=TestingUtils.setUpPark(5,false,);
//            jobs= TestingUtils.generateDummyJobs(200,50);
//
//        }
        /// Schedule Manager
        ScheduleManager scheduleManager = new ScheduleManager(park);

        // Passo allo scheduler una lista di Jobs
        scheduleManager.setNewSchedule(jobs);

        // Alloco i Jobs
        scheduleManager.allocateJobs();

        PlannerDeamon deam = new PlannerDeamon();

        for(Host h:park){
            h.saveLoads();
            h.saveSchedule();
            deam.dockerScan(h,h.getCurrentSchedule());
        }

        deam.run();


    }

    public static void main(String[] args)throws IOException,SQLException{
        if(args.length>0){
            if(args[0]=="-t")
            System.out.println(args[0]);
            newSchedule(true);
        }else{
        newSchedule(false);}
    }


}
