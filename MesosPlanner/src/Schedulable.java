import java.time.LocalDateTime;

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

    void setStart(LocalDateTime t);
    Task getTask();
    String getInfo();
}
