import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Receiver {
    Map< ? extends Schedulable,LocalDateTime> getCurrentSchedule();

    boolean queueJob(Schedulable s);
    String getInfo();
    void saveSchedule() throws IOException;
    void saveLoads()throws IOException;
    float[] checkNormalizedResAtTime(LocalDateTime t,Map< ? extends Schedulable, LocalDateTime> proposedSchedule);
    float[] checkNormalizedResAtTime(LocalDateTime t);
    float checkLoadAtTime(LocalDateTime t);
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();


}
