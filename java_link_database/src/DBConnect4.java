import java.sql.*;

public class DBConnect4 {
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
        String[] sqlStrings = {"INSERT INTO Product VALUES('0001','T恤衫','衣服',1000,500,'2009-09-20');",
        		"INSERT INTO Product VALUES('0002','打孔器','办公用品',1000,500,'2009-09-11');",
        		"INSERT INTO Product VALUES('0003','运动T恤','衣服',4000,2800,NULL);",
        		"INSERT INTO Product VALUES('0004','菜刀','厨房用具',3000,2800,'2009-09-20');",
        		"INSERT INTO Product VALUES('0005','高压锅','厨房用具',6800,5000,'2009-01-15');",
        		"INSERT INTO Product VALUES('0006','叉子','厨房用具',500,NULL,'2009-09-20');",
        		"INSERT INTO Product VALUES('0007','擦菜板','厨房用具',880,790,'2009-04-28');",
        		"INSERT INTO Product VALUES('0008','圆珠笔','办公用品',100,NULL,'2009-11-11');"};
        int delcnt=0;
        
        for (int j = 0; j < sqlStrings.length; j++) {
        	
        	delcnt += st.executeUpdate(sqlStrings[j]);
			
		}
        

        /* 5) 在画面中显示结果 */
        System.out.print(delcnt+" 行添加"); 

        /* 6) 切断与PostgreSQL的连接 */ 
        st.close();
        con.close(); 
   }
}
