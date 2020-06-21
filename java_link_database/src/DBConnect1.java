import java.sql.*;

public class DBConnect1 {
    public static void main(String[] args) throws Exception {
        
        Connection con;
        Statement st;
        ResultSet rs;

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "test";

        /* 2) 定义JDBC驱动 */
        Class.forName("org.postgresql.Driver");
        //Class.forName("java.sql.Driver");

        /* 3) 连接PostgreSQL */
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();

        /* 4) 执行SELECT语句 */
        rs = st.executeQuery("SELECT 1 AS col_1");

        /* 5) 在画面中显示结果 */
        rs.next();
        System.out.print(rs.getInt("col_1"));

          
        rs.close();
        st.close();
        con.close();

    }
}