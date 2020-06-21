import java.sql.*;

public class DBConnect5 {
	public static void main(String[] args) throws Exception {

        /* 1) PostgreSQL的连接信息 */
        Connection con; 
        Statement st;

        String url = "jdbc:postgresql://localhost:5432/shop";
        String user = "postgres";
        String password = "test"; 

        /* 2) 定义JDBC驱动 */
        Class.forName("org.postgresql.Driver"); 

        /* 3) 连接PostgreSQL */
        con = DriverManager.getConnection(url, user, password); 
        st = con.createStatement(); 

        /* 4) 执行SELECT语句 */
        int delcnt = st.executeUpdate("UPDATE Product SET product_name = 'Y恤衫',product_type = '衣服' WHERE product_id='0001';");

        /* 5) 在画面中显示结果 */
        System.out.print(delcnt+" 行已更新"); 

        /* 6) 切断与PostgreSQL的连接 */ 
        st.close();
        con.close(); 
   }
}
