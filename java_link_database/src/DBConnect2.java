import java.sql.*;

public class DBConnect2{
     public static void main(String[] args) throws Exception {

          /* 1) PostgreSQL的连接信息 */
          Connection con; 
          Statement st;
          ResultSet rs;

          String url = "jdbc:postgresql://localhost:5432/shop";
          String user = "postgres";
          String password = "test"; 

          /* 2) 定义JDBC驱动 */
          Class.forName("org.postgresql.Driver"); 

          /* 3) 连接PostgreSQL */
          con = DriverManager.getConnection(url, user, password); 
          st = con.createStatement(); 

          /* 4) 执行SELECT语句 */
          rs = st.executeQuery("SELECT product_id, product_name FROM Product");

          /* 5) 在画面中显示结果 */
          while(rs.next()) { 
               System.out.print(rs.getString("product_id") + ", ");
               System.out.println(rs.getString("product_name"));
          } 

          /* 6) 切断与PostgreSQL的连接 */
          rs.close(); 
          st.close();
          con.close(); 
     }
}