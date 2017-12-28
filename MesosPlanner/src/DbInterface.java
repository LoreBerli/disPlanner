/**
 * Created by cioni on 02/10/17.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbInterface {

    public Connection conn;
    private String db;
    private String user;
    private String pw;
    private String docks;
    private String scheduleTable;


    public DbInterface(String db,String user,String pw,String procs){
        try
        {
            this.db=db;
            this.user=user;
            this.pw=pw;
            this.docks=procs;
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db,user,pw);

            //Database Name - testDB, Username - "root", Password - ""
            System.out.println("Connected...");
            this.queryTest();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void queryTest() throws java.sql.SQLException{
        Statement stmt = conn.createStatement() ;
        String query = "SHOW TABLES;" ;
        ResultSet rs = stmt.executeQuery(query) ;
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

    }

    public List<Machine> getMachines()throws java.sql.SQLException{

        Statement stmt = conn.createStatement() ;
        String query = "SELECT HOSTNAME,AVG(CPU_CORES),AVG(TOTAL_CPU_CAPACITY),AVG(MEMORY_SIZE) FROM QRTZ_VMWARE_HOST GROUP BY HOSTNAME ASC;" ;
        ResultSet rs = stmt.executeQuery(query) ;
        List<Machine> machines=new ArrayList<>();
        while (rs.next()) {
            int CPU = Integer.parseInt(rs.getString("AVG(CPU_CORES)"));

            int MEM = Integer.parseInt(rs.getString("AVG(MEMORY_SIZE)").substring(0,3));
            int DSK = 20000;
            String name = rs.getString("HOSTNAME");
            Machine tmp = new Machine(CPU,MEM,DSK,name);
            machines.add(tmp);
        }
        return machines;
    }

    public  Task getTaskHistoricData(String ID) throws java.sql.SQLException{
        System.out.println(ID);
        Statement stmt = conn.createStatement() ;
        //sanitize ID
        String query = "SELECT VM_NAME,AVG(CPU_USAGE),AVG(USED_MEMORY),AVG(USED_DISK) FROM" +this.docks + "GROUP BY VM_NAME HAVING VM_NAME = DISCES-Mesos-Marathon-Debian-9-86  ;";
        ResultSet rs = stmt.executeQuery(query) ;
        System.out.println(rs.next());
        //List<Machine> machines=new ArrayList<>();

        int CPU = (int)Float.parseFloat(rs.getString("AVG(CPU)"));
        int MEM = (int)Float.parseFloat(rs.getString("AVG(MEM)"));
        int DSK = (int)Float.parseFloat(rs.getString("AVG(DSK)"));
        //int DUR = (int)Float.parseFloat(rs.getString("AVG(DUR)"));
        Task t = new Task(ID,CPU,MEM,DSK,200);

        return t;
    }

    public List<Machine> getRealMachines()throws java.sql.SQLException{

        Statement stmt = conn.createStatement() ;
        String query = "SELECT VM_NAME,AVG(NUM_CPU),AVG(TOTAL_MEMORY),AVG(FREE_DISK) FROM mach_test GROUP BY VM_NAME;" ;
        ResultSet rs = stmt.executeQuery(query) ;
        System.out.println("-------------------------__"+rs.getFetchSize());
        List<Machine> machines=new ArrayList<>();
        while (rs.next()) {
            int CPU = (int)Float.parseFloat(rs.getString("AVG(NUM_CPU)"));
            int MEM = (int)Float.parseFloat(rs.getString("AVG(TOTAL_MEMORY)"));
            int DSK = (int)(Float.parseFloat(rs.getString("AVG(FREE_DISK)")));
            String name = rs.getString("VM_NAME");
            Machine tmp = new Machine(CPU,MEM,DSK,name);
            machines.add(tmp);
        }
        return machines;
    }

    public boolean writeScheduleToDb() throws java.sql.SQLException{
        Statement stmt = conn.createStatement();
        String query="INSERT INTO "+this.scheduleTable+" VALUES(";
        return true;
    }

}
