import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Test per lo scheduling.
 * Created by cioni on 08/10/17.
 */
public class ScheduleTester {
    public static void main(String[] args) throws IOException{
        Runtime.getRuntime().exec(new String[]{"/bin/bash","-c","rm /home/cioni/git/MesosPlanner/loadsLog/*"});
        Runtime.getRuntime().exec(new String[]{"/bin/bash","-c","rm /home/cioni/git/MesosPlanner/logs/*"});
        Properties pfile = new Properties();
        InputStream inp = new FileInputStream("src/config.properties");
        pfile.load(inp);

        Logger lg=new Logger(pfile.getProperty("general_file"));

        DbInterface db = new DbInterface(pfile.getProperty("db"),pfile.getProperty("user"),pfile.getProperty("password"),pfile.getProperty("procs_table"));

        List<Job> jobs = TestingUtils.generateDummyJobs(700,20);

        List<Host> park;
        try{
        park = db.getRealMachines();
        }
        catch (SQLException sql){
            System.out.println("nopeMACHINE");
            park = new ArrayList<>();
            park.add(new Host(4,32,400,"I should'nt be here",0.9f));
        }

        park= park.subList(0,4);





        ScheduleManager scheduleManager = new ScheduleManager(park);

        // Passo allo scheduler una lista di Jobs
        scheduleManager.setNewSchedule(jobs);

        // Alloco i Jobs
        scheduleManager.allocateJobs();

//        for(String s:scheduleManager.machineInfo()){
//            System.out.println(s);
//        }
        for(Receiver m:park){
            lg.logNodeInfo(m,"");
            m.saveSchedule();
            m.saveLoads();
        }
//        String imgName="outVis3";
//        //System.out.println("/bin/bash -c \"python /home/cioni/PycharmProjects/vizuMes/vizu_mez.py " + imgName + "\"");
//        Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash","-c","python /home/cioni/PycharmProjects/vizuMes/vizu_mez.py "+imgName});// outVis3"});
//        Runtime.getRuntime().exec("/bin/sh -c \"python viz/prova.py "+imgName+" \"");//+imgName);
//        //Process d = Runtime.getRuntime().exec(new String[]{"/bin/bash","-c","gwenview /home/cioni/git/MesosPlanner/"+imgName+".png"});
//        try{
//            d.waitFor();
//        }catch (InterruptedException ie){}
//        //Runtime.getRuntime().exec("gwenview "+imgName+".png ");
//        Runtime.getRuntime().exec("notify-send finito ");

    }

    public static Task getOneFromDB(DbInterface db){
        String id = "3";
        Task t;
        try{
            t=db.getTaskHistoricData(id);
        }catch (SQLException sq){
            System.out.println("nopePROCS");
            t = new Task("somethingWentWrong",0,0,0,0);
        }
        return t;
    }




}
