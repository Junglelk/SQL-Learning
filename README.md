# 通过应用程序连接数据库

## 数据库世界和应用程序世界的连接

应用和数据库靠*驱动*连接，驱动是个很小的程序，是连接两个世界的桥梁。  
驱动的有两种:

* ODBC-*Open DataBase Connectivity*
* JDBC-*Java DataBase Connectivity*

本次使用JDBC进行学习。　　
JDBC版本为postgresql-42.2.14.jar  
Java版本为Java8

## Java基础

由于已经学过Java，仅记录本书内的一些注意事项

**法则 9-1** 执行Java程序前，必须对源代码进行编译。
**法则 9-2** Java源代码中保留字要区分大小写，这是它和数据库的不同点之一。
**法则 9-3** Java源码中不能出现全角字符/全角空格(注释除外)。

## 通过Java连接PostgreSQL

```java
import java.sql.*;

public class DBConnect1 {
     public static void main(String[] args) throws Exception {

          /* 1) PostgreSQL的连接信息 */
          Connection con;
          Statement st;
          ResultSet rs;

          String url = "jdbc:postgresql://localhost:5432/postgres";
          String user = "postgres";
          String password = "test";
          /*此处的默认密码必须是安装Postgres时未更新，如果当初更改了默认密码，则此处应该改成相对应的密码，否则会无法连接数据库*/

          /* 2) 定义JDBC驱动 */
          Class.forName("org.postgresql.Driver");

          /* 3) 连接PostgreSQL */
          con = DriverManager.getConnection(url, user, password);
          st = con.createStatement();

          /* 4) 执行SELECT语句 */
          rs = st.executeQuery("SELECT 1 AS col_1");

          /* 5) 显示结果画面 */
          rs.next();
          System.out.print(rs.getInt("col_1"));

          /* 6) 切断与PostgreSQL的连接 */
          rs.close();
          st.close();
          con.close();
     }
}
```

运行该Java程序,使用控制台编译、运行即可

```java
//如果是直接仅使用控制台输入
javac DBConnect1.java

java -cp C:\PostgresSQL\jdbc\*;. DBConnect1
/*"-cp"指的是classpath，用于指明类的路径。这个里面有两个类，一个是DBConnect1，  
一个是驱动类，可以将驱动类添加至环境变量，或者使用eclipse创建项目时，  
将驱动类引入项目*/
```

下面，实践对Product的操作

```java
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
          //此处密码应为自己设定的密码,安全起见，已被更换

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
//运行结果为

0001, T恤
0002, 打孔器
0003, 运动T恤
0004, 菜刀
0005, 高压锅
0006, 叉子
0007, 擦菜板
0008, 圆珠笔
```

**法则 9-4** 在Java等程序语言的世界中，每次只能访问一条数据。因此，在访问多条记录时，需要循环处理。  
**法则 9-5** 通过使用驱动，程序可以执行包括SELECT、DELETE、UPDATE和INSERT在内的所有SQL语句。  

### 习题

* 9.1

```java
import java.sql.*;

public class DBConnect4 {
     public static void main(String[] args) throws Exception {

        /* 1) PostgreSQL的连接信息 */
        Connection con;
        Statement st;

        String url = "jdbc:postgresql://localhost:5432/shop";
        String user = "postgres";
        String password = "159753";

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

```

* 9.2

```java
import java.sql.*;

public class DBConnect5 {
     public static void main(String[] args) throws Exception {

        /* 1) PostgreSQL的连接信息 */
        Connection con;
        Statement st;

        String url = "jdbc:postgresql://localhost:5432/shop";
        String user = "postgres";
        String password = "159753";

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


SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |       1000 |            500 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2009-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0001       | T恤衫        | Y恤衫        |       1000 |            500 | 2009-09-20
(8 行记录)
--这里是第一次搞错了，把类别改成Y恤衫了。后证明可以一次修改两个属性

SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |       1000 |            500 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2009-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0001       | Y恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(8 行记录)
--修改完成后的情况
```
