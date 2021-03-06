import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * Interfaccia per risorse ***
 * I Receiver ricevono una lista di Schedulables
 */
public interface Receiver {
    Map< ? extends Schedulable,LocalDateTime> getCurrentSchedule();

    boolean queueJob(Schedulable s);
    float getTreshold();
    String getID();
    String getInfo();
    void saveSchedule() throws IOException;
    void saveLoads()throws IOException;
    void saveScheduleToDB() throws IOException,SQLException;
    float[] checkNormalizedResAtTime(LocalDateTime t,Map< ? extends Schedulable, LocalDateTime> proposedSchedule);
    float[] checkNormalizedResAtTime(LocalDateTime t);
    float checkLoadAtTime(LocalDateTime t);
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();


}
