/**
 * Created by cioni on 02/10/17.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe che offre i metodi per recuperare i descrittori di VM,Docker dal DB
 *
 */
public class DbInterface {

    public Connection conn;
    private String db;
    private String user;
    private String pw;
    private String machs;
    private String docks;

    private String scheduleTable;


    public DbInterface(String db,String user,String pw,String procs,String scheduleTable,String machs){
        try
        {
            this.db=db;
            this.user=user;
            this.scheduleTable=scheduleTable;
            this.pw=pw;
            this.docks=procs;
            this.machs=machs;
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db,user,pw);

            //Database Name - testDB, Username - "root", Password - ""
            System.out.println("Connected...");

        }
        catch(Exception e)
        {
            System.out.println("FUUUUUUUUUUUCK");
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

    public List<Host> getMachines()throws java.sql.SQLException{

        Statement stmt = conn.createStatement() ;
        String query = "SELECT * FROM " + this.machs ;
        ResultSet rs = stmt.executeQuery(query) ;

        List<Host> machs=new ArrayList<>();
        while (rs.next()) {

            int CPU = Integer.parseInt(rs.getString("CPU"));

            int MEM = Integer.parseInt(rs.getString("MEM").substring(0,3));
            int DSK = Integer.parseInt(rs.getString("DSK").substring(0,2));
            float tresh = Float.parseFloat(rs.getString("tresh").substring(0,3));
            String adress = rs.getString("address");
            String name = rs.getString("name");
            Host tmp = new Host(CPU,MEM,DSK,name+":"+adress,tresh,this);
            machs.add(tmp);
        }
        return machs;
    }


    //TODO: TEST ME TEST ME TEST ME TEST ME

    public List<Job> getDockers()throws java.sql.SQLException{
        //////this shlould get the job done
        Statement stmt = conn.createStatement() ;
        String query = "SELECT * FROM JOBS;" ;
        ResultSet rs = stmt.executeQuery(query) ;

        List<Job> jobs=new ArrayList<>();
        while (rs.next()) {
            String command=rs.getString("command");
            String image= rs.getString("image");
            LocalDateTime start = rs.getTimestamp("startTime").toLocalDateTime();
            int CPU = Integer.parseInt(rs.getString("CPU"));

            int MEM = Integer.parseInt(rs.getString("MEM"));
            int DSK = Integer.parseInt(rs.getString("DSK"));
            int dur = Integer.parseInt(rs.getString("dur"));
            Task tt = new Task(command,CPU,MEM,DSK,dur);
            Job jb = new Job(image,tt,start,1,true,null);
            jobs.add(jb);
        }
        return jobs;
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

    public List<Host> getRealMachines()throws java.sql.SQLException{

        Statement stmt = conn.createStatement() ;
        String query = "SELECT VM_NAME,AVG(NUM_CPU),AVG(TOTAL_MEMORY),AVG(FREE_DISK) FROM mach_test GROUP BY VM_NAME;" ;
        ResultSet rs = stmt.executeQuery(query) ;
        System.out.println("------------------------- "+rs.getFetchSize());
        List<Host> machines=new ArrayList<>();
        while (rs.next()) {
            int CPU = (int)Float.parseFloat(rs.getString("AVG(NUM_CPU)"));
            int MEM = (int)Float.parseFloat(rs.getString("AVG(TOTAL_MEMORY)"));
            int DSK = (int)(Float.parseFloat(rs.getString("AVG(FREE_DISK)")));
            String name = rs.getString("VM_NAME");
            Host tmp = new Host(CPU,MEM,DSK,name,0.9f,this);
            machines.add(tmp);
        }
        return machines;
    }
    public void cleanDB()throws SQLException{
        //TRUNCATE TABLE
        Statement stmt = conn.createStatement();
        stmt.execute("TRUNCATE TABLE "+this.scheduleTable+";");

    }

    //
    public boolean writeScheduleToDb(String name, String command, LocalDateTime startTime,LocalDateTime endTime,int CPU,int MEM,int DSK,int duration,String host) throws java.sql.SQLException{
        // id | name | command | startTime | endTime |CPU|MEM|DSK host
        //datetime 'YYYY-MM-DD HH:MM:SS'
        //
        //  CREATE TABLE new_sched (id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
        //                          name VARCHAR(30),command VARCHAR(60),startTime DATETIME,
        //                          endTime DATETIME,cpu INT,mem INT,dsk INT,duration INT,host VARCHAR(30));
        //  INSERT INTO new_sched VALUE(0,
        //                         "docker1",
        //                         "ls",
        //                         '2018-05-04T16:36:45',
        //                         '2018-05-04T16:36:45',
        //                         4,256,2,60,"prova");

        Statement stmt = conn.createStatement();
        String query="INSERT INTO "+this.scheduleTable+" VALUES(0,";
        //INSERT INTO example VALUES(0,"docker1","ls",'2018-05-04 16:36:45',120,"vm0");
        java.sql.Timestamp sqlTimeStart=java.sql.Timestamp.valueOf(startTime);
        java.sql.Timestamp sqlTimeEnd=java.sql.Timestamp.valueOf(endTime);

        query=query+"  \""+name+"\",\""+command+"\",\""+sqlTimeStart+"\",\""+sqlTimeEnd+"\","+Integer.toString(CPU)+","+Integer.toString(MEM)+","+Integer.toString(DSK)+","+Integer.toString(duration)+",\""+host+"\");";
        System.out.println(query);
        return stmt.execute(query);

    }

}
