import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cioni on 08/10/17.
 */
public class TestingUtils {
    public static List<Task> generateDummyTasks(int howMany){
        List<Task> tasks=new ArrayList<>();
        Random r = new Random();

        for (int t=0;t<howMany;t++){
            int randCpu=r.nextInt(3)+1;
            int randMem = r.nextInt(400);
            int randDsk= r.nextInt(30);
            int ranDuration = 100+r.nextInt(800);
            Task tmp = new Task("dummy"+ t,randCpu,randMem,randDsk,ranDuration);
            tasks.add(tmp);
        }
        return tasks;
    }

    public static List<Host> setUpPark(int mchins,boolean special,DbInterface db){
        List<Host> park = new ArrayList<>();

        for (int ms = 0; ms < mchins; ms++) {
            Host tmp = new Host(4, 3000, 20,"TestMach"+ms,0.9f,db);
            tmp.setUsedCPU(ms%4);
            tmp.setUsedMEM((1000*ms)%3);
            tmp.setUsedDSK(14);
            park.add(tmp);
        }

        if(special){
            Host otherMachine = new Host(4, 3000, 20,"special",0.9f,db);
            otherMachine.setUsedCPU(0);
            otherMachine.setUsedMEM(0);
            otherMachine.setUsedDSK(4);
            park.add(otherMachine);}
        return park;
    }

    public static List<Job> generateDummyJobs(int howMany,long secondsOffset){
        List<Task> tsks=generateDummyTasks(howMany);
        List<Job> toReturn=new ArrayList<>();
        Random rnd = new Random();
        long offset=rnd.nextInt((int)secondsOffset);
        for(Task t:tsks){
            offset=offset+secondsOffset;
            Job j = new Job(t, LocalDateTime.now().plusSeconds(offset),0,true,null);
            toReturn.add(j);
        }
        return toReturn;
    }

    public static void logOutSchedules(List<Machine> park) throws IOException{
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("bestSchedule"), "utf-8"))) {
            for(Machine m:park){

            }
    }

    }

}
