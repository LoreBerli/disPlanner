import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cioni on 28/09/17.
 */
public class Scheduling {
    private List<Job> proposedSchedule;
    private List<Job> betterSchedule;
    private int maxTicks;

    public Scheduling(List<Job> scheduleProposal){
        this.proposedSchedule=scheduleProposal;
        this.maxTicks=100;
    }


    public Scheduling(List<Job> scheduleProposal,int maxTicks){
        this.proposedSchedule=scheduleProposal;
        this.maxTicks=maxTicks;
    }

//    public boolean checkForOverload(){//bigmess
//        /*
//
//        */
//
//        int CPU=0;
//        int MEM=0;
//        int DSK=0;
//
//        Map<Integer,Job> orderedByStartTime=new HashMap<>();
//
//        for(Job j:proposedSchedule){
//            orderedByStartTime.put(j.getStart(),j);
//        }
//        Map<Integer,Job> result=orderedByStartTime.entrySet().stream().
//                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
//        //Map<Integer,Job> sorted = result.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
//
//        System.out.println(result);
//        List<Job> alreadyStarted = new ArrayList<>();
//        List<Job> alreadyExecuted = new ArrayList<>();
//        for(Map.Entry<Integer,Job> ex: result.entrySet()){
//            CPU+=ex.getValue().getTask().getExpectedCPU();
//            alreadyStarted.add(ex.getValue());
//            for(Job aJ:alreadyStarted){
//                if(!alreadyExecuted.contains(aJ)){
//                if(aJ.getStart()+aJ.getSecondsDuration()<ex.getValue().getStart()){
//                    CPU-=aJ.getTask().getExpectedCPU();
//                    alreadyExecuted.add(aJ);
//                }}
//
//            }
//            System.out.println("CPU : "+CPU);
//            //System.out.println(alreadyStarted);
//        }
//
//        return true;
//    }

    public void logOut() throws IOException{
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("bestSchedule"), "utf-8"))) {
            for(Job j : proposedSchedule){
            writer.write(j.getTask().getDescriptor() + ","+j.getAssignedMachine()+","+j.getStart()+","+j.getSecondsDuration()+"\n");}
        }
    }

}
