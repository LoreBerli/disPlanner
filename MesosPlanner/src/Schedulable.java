import java.time.LocalDateTime;

/**
 * Interfaccia per qualsiasi cosa sia da schedulare (sia un Docker o una VM)
 * Gli Schedulable saranno associati ad un Recevier
 */
public interface Schedulable {
    Receiver getRecevier();

    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    boolean isSchedulable();
    public int getExpectedCPU();
    public int getExpectedMEM();
    //public int[] averageResDuringCurrentSchedule();
    public int getExpectedDUR();
    public int getExpectedDSK();
    void updateConsumption();
    void setSchedulability(boolean schedulable);
    void setStart(LocalDateTime t);
    Task getTask();
    String getInfo();
}
