# SQL高级处理

本章学习SQL高级聚合处理。主要内容有窗口函数、GROUPING运算符。

## 窗口函数

什么是窗口函数?窗口函数又称为OLAP函数。OLAP是*OnLine Analytical Processing*的简称，意思是对数据库数据进行实时分析处理。  
窗口函数语法如下

```sql
<窗口函数> OVER ([PARTITION BY <列清单>]
                      ORDER BY <排序用列清单>)
```

能作为窗口函数的函数有

* 能作为窗口函数的聚合函数(SUM、AVG、COUNT、MAX、MIN);
* RANK、DENSE_RANK、ROW_NUMBER等专用窗口函数。

### 语法基本使用方法-RANK函数

RANK是用来记录排序的函数。

```sql
SELECT product_name,product_type,sale_price,
RANK() OVER (PARTITION BY product_type
                 ORDER BY sale_price) AS Ranking
FROM Product;
 product_name | product_type | sale_price | ranking
--------------+--------------+------------+---------
 圆珠笔       | 办公用品     |        100 |       1
 打孔器       | 办公用品     |        500 |       2
 叉子         | 厨房用具     |        500 |       1
 擦菜板       | 厨房用具     |        880 |       2
 菜刀         | 厨房用具     |       3000 |       3
 高压锅       | 厨房用具     |       6800 |       4
 T恤          | 衣服         |       1000 |       1
 运动T恤      | 衣服         |       4000 |       2
(8 行记录)
```

先由PARTITION BY将Product按照类别进行分组，再将各组按照价格进行排序，即PARTITION BY进行横向分组，ORDER BY进行纵向排序。  
**法则 8-1** 窗口函数兼具分组和排序两个功能。  
**法则 8-2** 通过PARTITION BY分组后的记录集合称为"窗口"。  
无需指定PARTITION BY,不适用分组子句窗口函数依然可以使用。

```sql
SELECT product_name,product_type,sale_price,
RANK() OVER (ORDER BY sale_price) AS Ranking
FROM Product;
product_name | product_type | sale_price | ranking
--------------+--------------+------------+---------
 圆珠笔       | 办公用品     |        100 |       1
 叉子         | 厨房用具     |        500 |       2
 打孔器       | 办公用品     |        500 |       2
 擦菜板       | 厨房用具     |        880 |       4
 T恤          | 衣服         |       1000 |       5
 菜刀         | 厨房用具     |       3000 |       6
 运动T恤      | 衣服         |       4000 |       7
 高压锅       | 厨房用具     |       6800 |       8
(8 行记录)
--结果会比较乱
```

### 专用窗口函数的种类

* RANK函数
  * 计算排序时，如果存在相同位次的记录，则会跳过之后的位次。
    * 例如有三条记录排在第一位时：1位、1位、1位、4位...
* DENSE_RANK函数
  * 计算排序时，如果存在相同位次的记录，也不会跳过之后的位次。
    * 例如有三条记录排在第一位时：1位、1位、1位、2位...
* ROW_NUMBER函数
  * 赋予唯一的连续座次
    * 例如有三条记录排在第一位时：1位、2位、3位、4位...

```sql
SELECT product_name,product_type,sale_price,
RANK() OVER (ORDER BY sale_price) AS Ranking,
DENSE_RANK() OVER(ORDER BY sale_price) AS Dense_rank,
ROW_NUMBER() OVER(ORDER BY sale_price) AS Row_number
FROM Product;
product_name | product_type | sale_price | ranking | dense_rank | row_number
--------------+--------------+------------+---------+------------+------------
 圆珠笔       | 办公用品     |        100 |       1 |          1 |          1
 叉子         | 厨房用具     |        500 |       2 |          2 |          2
 打孔器       | 办公用品     |        500 |       2 |          2 |          3
 擦菜板       | 厨房用具     |        880 |       4 |          3 |          4
 T恤          | 衣服         |       1000 |       5 |          4 |          5
 菜刀         | 厨房用具     |       3000 |       6 |          5 |          6
 运动T恤      | 衣服         |       4000 |       7 |          6 |          7
 高压锅       | 厨房用具     |       6800 |       8 |          7 |          8
(8 行记录)
```

**法则 8-3** 由于专用窗口函数没有参数，因此通常括号中都是空的。  

### 窗口函数的适用范围

窗口函数运行时，实际上是对WHERE子句或GROUP BY子句处理后的"结果"进行操作。因此，窗口函数不能用于WHERE、GROUP BY等子句中。也即是说，在SELECT语句之外"使用窗口函数是无意义的"。  
**法则 8-4** 原则上窗口函数只能在SELECT子句中使用。  

### 作为窗口函数的聚合函数

所有聚合函数都能用作窗口函数。以下为部分演示

```sql
SELECT product_id,product_name,sale_price,
SUM(sale_price) OVER (ORDER BY product_id) AS current_sum
FROM Product;
 product_id | product_name | sale_price | current_sum
------------+--------------+------------+-------------
 0001       | T恤          |       1000 |        1000--1000
 0002       | 打孔器       |        500 |        1500--1000+500
 0003       | 运动T恤      |       4000 |        5500--1000+500+4000
 0004       | 菜刀         |       3000 |        8500--...
 0005       | 高压锅       |       6800 |       15300
 0006       | 叉子         |        500 |       15800
 0007       | 擦菜板       |        880 |       16680
 0008       | 圆珠笔       |        100 |       16780--1000+500+...+100
(8 行记录)

SELECT product_id,product_name,sale_price,
COUNT(sale_price) OVER (ORDER BY product_id) AS current_count
FROM Product;
 product_id | product_name | sale_price | current_count
------------+--------------+------------+---------------
 0001       | T恤          |       1000 |             1
 0002       | 打孔器       |        500 |             2
 0003       | 运动T恤      |       4000 |             3
 0004       | 菜刀         |       3000 |             4
 0005       | 高压锅       |       6800 |             5
 0006       | 叉子         |        500 |             6
 0007       | 擦菜板       |        880 |             7
 0008       | 圆珠笔       |        100 |             8
(8 行记录)

SELECT product_id,product_name,sale_price,
MAX(sale_price) OVER (ORDER BY product_id) AS current_MAX
FROM Product;
 product_id | product_name | sale_price | current_max
------------+--------------+------------+-------------
 0001       | T恤          |       1000 |        1000
 0002       | 打孔器       |        500 |        1000
 0003       | 运动T恤      |       4000 |        4000
 0004       | 菜刀         |       3000 |        4000
 0005       | 高压锅       |       6800 |        6800
 0006       | 叉子         |        500 |        6800
 0007       | 擦菜板       |        880 |        6800
 0008       | 圆珠笔       |        100 |        6800
(8 行记录)
--聚合函数用法一致
```

### 计算*移动平均*

窗口函数就是将表以窗口为单位切割，并对切割出的窗口进行排序的函数。之前的演示中都是将整个表作为一个窗口，接下来引入"框架"，用以指定更加详细的汇总范围。

* 截止到之前~行
  * 关键字是PRECEDING-之前

```sql
SELECT product_id,product_name,sale_price,AVG(sale_price)
OVER (ORDER BY product_id ROWS 2 PRECEDING) AS mov_avg
FROM Product;
 product_id | product_name | sale_price |        mov_avg
------------+--------------+------------+-----------------------
 0001       | T恤          |       1000 | 1000.0000000000000000--(1000)/1
 0002       | 打孔器       |        500 |  750.0000000000000000---(500+1000)/2
 0003       | 运动T恤      |       4000 | 1833.3333333333333333--(4000+500+1000)/3
 0004       | 菜刀         |       3000 | 2500.0000000000000000--(3000+4000+500)/3
 0005       | 高压锅       |       6800 | 4600.0000000000000000--(6800+3000+4000)/3
 0006       | 叉子         |        500 | 3433.3333333333333333--(500+6800+3000)/3
 0007       | 擦菜板       |        880 | 2726.6666666666666667--(880+500+6800)/3
 0008       | 圆珠笔       |        100 |  493.3333333333333333--(100+880+500)/3
(8 行记录)
--每一行记录都是以其为基准行，再加上其PRECEDING两行计算得到
```

* 截止到之后~行
  * 关键字FOLLOWING-之后

```sql
SELECT product_id,product_name,sale_price,AVG(sale_price)
OVER (ORDER BY product_id ROWS 2 FOLLOWING) AS mov_avg
FROM Product;
错误:  从后面记录启动的窗口框架(frame)不能以当前记录结束
第1行...ce,AVG(sale_price) OVER (ORDER BY product_id ROWS 2 FOLLOWING...
--不知道为什么这里会出错，百度找不到(也许Google可以但我英语很差，而且也没梯子)
SELECT product_id,product_name,sale_price,AVG(sale_price)
OVER (ORDER BY product_id ROWS BETWEEN 0 PRECEDING AND 2 FOLLOWING) AS Avg_mov FROM Product;
 product_id | product_name | sale_price |        avg_mov
------------+--------------+------------+-----------------------
 0001       | T恤          |       1000 | 1833.3333333333333333
 0002       | 打孔器       |        500 | 2500.0000000000000000
 0003       | 运动T恤      |       4000 | 4600.0000000000000000
 0004       | 菜刀         |       3000 | 3433.3333333333333333
 0005       | 高压锅       |       6800 | 2726.6666666666666667
 0006       | 叉子         |        500 |  493.3333333333333333
 0007       | 擦菜板       |        880 |  490.0000000000000000
 0008       | 圆珠笔       |        100 |  100.0000000000000000
(8 行记录)
```

* 在~之前和之后
  * BETWEEN 行数 PRECEDING AND 行数 FOLLOWING

```sql
SELECT product_id,product_name,sale_price,AVG(sale_price)
OVER (ORDER BY product_id ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS Avg_mov FROM Product;
 product_id | product_name | sale_price |        avg_mov
------------+--------------+------------+-----------------------
 0001       | T恤          |       1000 |  750.0000000000000000
 0002       | 打孔器       |        500 | 1833.3333333333333333
 0003       | 运动T恤      |       4000 | 2500.0000000000000000
 0004       | 菜刀         |       3000 | 4600.0000000000000000
 0005       | 高压锅       |       6800 | 3433.3333333333333333
 0006       | 叉子         |        500 | 2726.6666666666666667
 0007       | 擦菜板       |        880 |  493.3333333333333333
 0008       | 圆珠笔       |        100 |  490.0000000000000000
(8 行记录)
```

### 两个ORDER BY

窗口函数内的ORDER BY仅仅是用来指定运算顺序的，与结束输出顺序无关，即有可能输出不按product_id排序的数据。只需在最后加一个ORDER BY语句即可。

```sql
SELECT product_id,product_name,sale_price,AVG(sale_price)
OVER (ORDER BY product_id ROWS BETWEEN 0 PRECEDING AND 2 FOLLOWING) AS Avg_mov,
SUM(sale_price) OVER (ORDER BY sale_price ROWS 2 PRECEDING) AS Mov_Sum
FROM Product
ORDER BY product_id DESC;
 product_id | product_name | sale_price |        avg_mov        | mov_sum
------------+--------------+------------+-----------------------+---------
 0008       | 圆珠笔       |        100 |  100.0000000000000000 |     100
 0007       | 擦菜板       |        880 |  490.0000000000000000 |    1880
 0006       | 叉子         |        500 |  493.3333333333333333 |     600
 0005       | 高压锅       |       6800 | 2726.6666666666666667 |   13800
 0004       | 菜刀         |       3000 | 3433.3333333333333333 |    4880
 0003       | 运动T恤      |       4000 | 4600.0000000000000000 |    8000
 0002       | 打孔器       |        500 | 2500.0000000000000000 |    1100
 0001       | T恤          |       1000 | 1833.3333333333333333 |    2380
(8 行记录)
```

**法则 8-5** 将聚合函数作为窗口函数使用时，会以当前记录为基准来决定汇总对象的记录。

## GROUPING运算符

如何得到这样一个表

|合计|16780|
|----|----|
|厨房用具|11180|
|衣服|5000|
|办公用品|600|

单纯的GROUP BY并不可行

```sql
SELECT product_type,SUM(sale_price)
FROM Product
GROUP BY product_type;
product_type |  sum
--------------+-------
 衣服         |  5000
 办公用品     |   600
 厨房用具     | 11180
(3 行记录)
--没有合计行
SELECT '合计' AS product_type,SUM(sale_price)
FROM Product
UNION
SELECT product_type,SUM(sale_price)
FROM Product GROUP BY Product_type;
 product_type |  sum
--------------+-------
 衣服         |  5000
 厨房用具     | 11180
 办公用品     |   600
 合计         | 16780
(4 行记录)

SELECT product_type,SUM(sale_price) AS "价格总计"
FROM Product GROUP BY Product_type
UNION
SELECT '合计' AS product_type,SUM(sale_price)
FROM Product;
 product_type | 价格总计
--------------+----------
 衣服         |     5000
 厨房用具     |    11180
 办公用品     |      600
 合计         |    16780
(4 行记录)
--Tips：重命名行要双引号引起汉字，而单独一行的名字是用单引号
```

用两个SELECT语句很繁琐，且系统开销大。

### ROLLUP-同时得出合计和小计

GROUPING运算符有三种：

* ROLLUP-卷起
* CUBE-立方体
* GROUPING SETS-取出

ROLLUP，简单说就是"一次计算出不同聚合键的结果"。

```sql
SELECT COALESCE(product_type,'合计') AS "商品类别",SUM(sale_price) AS "价格总计" FROM Product GROUP BY ROLLUP(Product_type);
 商品类别 | 价格总计
----------+----------
 合计     |    16780
 衣服     |     5000
 办公用品 |      600
 厨房用具 |    11180
(4 行记录)
```

本例中ROLLUP就是一次计算出以下两个组合的汇总结果

* GROUP BY()
* GROUP BY(product_type)

GROUP BY()表示没有聚合键，也就相当于没有GROUP BY子句,相当于

```sql
SELECT NULL,SUM(sale_price) AS "价格总计"
FROM Product GROUP BY ();
 ?column? | 价格总计
----------+----------
          |    16780
(1 行记录)
```

该行合计记录被称为**超级分组记录**(Super~~!!!)，其列的键值默认为NULL(上图说明了问题，至少某种程度上)。  
**法则 8-6** 超级分组记录默认使用NULL作为聚合键。

"一次计算出不同聚合键的结果"，下面是多个聚合键的情况。

```sql
--未使用ROLLUP
SELECT product_type,regist_date,SUM(sale_price)
FROM Product
GROUP BY product_type,regist_date
ORDER BY product_type;
 product_type | regist_date | sum
--------------+-------------+------
 办公用品     | 2009-11-11  |  100
 办公用品     | 2009-09-11  |  500
 厨房用具     | 2009-01-15  | 6800
 厨房用具     | 2009-09-20  | 3500
 厨房用具     | 2008-04-28  |  880
 衣服         | 2009-09-20  | 1000
 衣服         |             | 4000
(7 行记录)
--按照类别多的优先分组，显然"日期"是更多种类的那个
SELECT product_type,regist_date,SUM(sale_price)
FROM Product
GROUP BY ROLLUP(product_type,regist_date)
ORDER BY product_type;
product_type | regist_date |  sum
--------------+-------------+-------
 办公用品     | 2009-09-11  |   500
 办公用品     | 2009-11-11  |   100
 办公用品     |             |   600--小计(办公用品)
 厨房用具     | 2008-04-28  |   880
 厨房用具     | 2009-01-15  |  6800
 厨房用具     | 2009-09-20  |  3500
 厨房用具     |             | 11180--小计(厨房用具)
 衣服         | 2009-09-20  |  1000
 衣服         |             |  4000
 衣服         |             |  5000--小计(衣服)
              |             | 16780--合计
(11 行记录)
```

这个是对以下三种模式聚合的UNION

* GROUP BY()
* GROUP BY(product_type)
* GROUP BY(product_type,regist_date)

**法则 8-7** ROLLUP可以同时得出合计和小计，是非常方便的工具。

### GROUPING函数-处理产生的NULL

上文中

```sql
 衣服         | 2009-09-20  |  1000
 衣服         |             |  4000
 衣服         |             |  5000--小计(衣服)
```

第三行是超级分组记录生成的，其日期栏为NULL，而第二行的衣服其日期行本就为NULL，如何区分产生的NULL与源数据的混淆?  
SQL标准语法提供了GROUPING函数，GROUPING函数在超级分组记录产生NULL时返回1，其它情况返回0；

```sql
SELECT GROUPING(product_type) AS product_type,GROUPING(regist_date) AS regist_date,SUM(sale_price)
FROM Product
GROUP BY ROLLUP(product_type,regist_date)
ORDER BY product_type;
SELECT GROUPING(product_type) AS product_type,GROUPING(regist_date) AS regist_date,SUM(sale_price)
FROM Product
GROUP BY ROLLUP(product_type,regist_date)
ORDER BY product_type;

 product_type | regist_date |  sum
--------------+-------------+-------
            0 |           1 | 11180
            0 |           0 |  4000
            0 |           0 |   880
            0 |           0 |  1000
            0 |           0 |   100
            0 |           0 |  6800
            0 |           0 |   500
            0 |           0 |  3500
            0 |           1 |  5000
            0 |           1 |   600
            1 |           1 | 16780
(11 行记录)

SELECT CASE WHEN GROUPING(product_type)=1
                THEN '商品种类  合计'
                ELSE product_type END AS "商品种类",
       CASE WHEN GROUPING(regist_date)=1
                THEN '登记日期  合计'
                ELSE CAST(regist_date AS VARCHAR(16)) END AS "登记日期",
       SUM(sale_price) AS "价格合计"
FROM Product
GROUP BY ROLLUP(product_type,regist_date);
    商品种类    |    登记日期    | 价格合计
----------------+----------------+----------
 商品种类  合计 | 登记日期  合计 |    16780
 衣服           |                |     4000
 厨房用具       | 2008-04-28     |      880
 衣服           | 2009-09-20     |     1000
 办公用品       | 2009-11-11     |      100
 厨房用具       | 2009-01-15     |     6800
 办公用品       | 2009-09-11     |      500
 厨房用具       | 2009-09-20     |     3500
 衣服           | 登记日期  合计 |     5000
 办公用品       | 登记日期  合计 |      600
 厨房用具       | 登记日期  合计 |    11180
(11 行记录)
```

**法则 8-8** 使用GROUPING函数能够简单地分辨出原始数据中的NULL和超级分组记录中的NULL。

### CUBE-用数据来搭积木

CUBE与GROUPING有一致的用法。

```sql
SELECT CASE WHEN GROUPING(product_type)=1
                THEN '商品种类  合计'
                ELSE product_type END AS "商品种类",
       CASE WHEN GROUPING(regist_date)=1
                THEN '登记日期  合计'
                ELSE CAST(regist_date AS VARCHAR(16)) END AS "登记日期",
       SUM(sale_price) AS "价格合计"
FROM Product
GROUP BY CUBE(product_type,regist_date);
    商品种类    |    登记日期    | 价格合计
----------------+----------------+----------
 商品种类  合计 | 登记日期  合计 |    16780
 衣服           |                |     4000
 厨房用具       | 2008-04-28     |      880
 衣服           | 2009-09-20     |     1000
 办公用品       | 2009-11-11     |      100
 厨房用具       | 2009-01-15     |     6800
 办公用品       | 2009-09-11     |      500
 厨房用具       | 2009-09-20     |     3500
 衣服           | 登记日期  合计 |     5000
 办公用品       | 登记日期  合计 |      600
 厨房用具       | 登记日期  合计 |    11180
 商品种类  合计 |                |     4000--→追加，相比于GROUPING
 商品种类  合计 | 2009-11-11     |      100--→追加，相比于GROUPING
 商品种类  合计 | 2009-09-20     |     4500--→追加，相比于GROUPING
 商品种类  合计 | 2009-09-11     |      500--→追加，相比于GROUPING
 商品种类  合计 | 2009-01-15     |     6800--→追加，相比于GROUPING
 商品种类  合计 | 2008-04-28     |      880--→追加，相比于GROUPING
(17 行记录)
```

显然，这是将regist_date汇总的结果，这次汇总的数据如下

* GROUP BY()
* GROUP BY(product_type)
* GROUP BY(regist_date)
* GROUP BY(product_type,regist_date)

所谓CUBE就是将GROUP BY子句中聚合键的"所有组合"的汇总结果集中到一个结果中，即上述的一张表。有n个组合键，那组合的个数就是$2^n$。  
**法则 8-9** 可以把CUBE理解为将使用聚合键进行切割的模块堆积成一个立方体。　　

### GROUPING SETS-取得期望的积木

GROUPING SETS运算符可以从ROLLUP或CUBE结果中取出部分记录。

```sql
SELECT CASE WHEN GROUPING(product_type)=1
                THEN '商品种类  合计'
                ELSE product_type END AS "商品种类",
       CASE WHEN GROUPING(regist_date)=1
                THEN '登记日期  合计'
                ELSE CAST(regist_date AS VARCHAR(16)) END AS "登记日期",
       SUM(sale_price) AS "价格合计"
FROM Product
GROUP BY GROUPING SETS(product_type,regist_date);
    商品种类    |    登记日期    | 价格合计
----------------+----------------+----------
 衣服           | 登记日期  合计 |     5000
 办公用品       | 登记日期  合计 |      600
 厨房用具       | 登记日期  合计 |    11180
 商品种类  合计 |                |     4000
 商品种类  合计 | 2009-11-11     |      100
 商品种类  合计 | 2009-09-20     |     4500
 商品种类  合计 | 2009-09-11     |      500
 商品种类  合计 | 2009-01-15     |     6800
 商品种类  合计 | 2008-04-28     |      880
(9 行记录)
```

这是希望从CUBE中选取出将"商品种类"和"登记日期"各自作为聚合键的结果，或者不想得到"合计记录和使用2个聚合键的记录"时，可以使用GROUPING SETS。

### 习题

* 8.1

```sql
SELECT product_id,product_name,sale_price,MAX(sale_price) OVER(ORDER BY product_id) AS current_max_price
FROM Product;
 product_id | product_name | sale_price | current_max_price
------------+--------------+------------+-------------------
 0001       | T恤          |       1000 |              1000
 0002       | 打孔器       |        500 |              1000
 0003       | 运动T恤      |       4000 |              4000
 0004       | 菜刀         |       3000 |              4000
 0005       | 高压锅       |       6800 |              6800
 0006       | 叉子         |        500 |              6800
 0007       | 擦菜板       |        880 |              6800
 0008       | 圆珠笔       |        100 |              6800
(8 行记录)
```

* 8.2

```sql
SELECT regist_date,product_name,sale_price,
SUM(sale_price) OVER(ORDER BY COALESCE(regist_date,CAST('0001-01-01' AS DATE))) AS current_sum_price
FROM Product;
 regist_date | product_name | sale_price | current_sum_price
-------------+--------------+------------+-------------------
             | 运动T恤      |       4000 |              4000
 2008-04-28  | 擦菜板       |        880 |              4880
 2009-01-15  | 高压锅       |       6800 |             11680
 2009-09-11  | 打孔器       |        500 |             12180
 2009-09-20  | T恤          |       1000 |             16680
 2009-09-20  | 菜刀         |       3000 |             16680
 2009-09-20  | 叉子         |        500 |             16680
 2009-11-11  | 圆珠笔       |        100 |             16780
(8 行记录)
--ORDER BY子句规定了执行顺序，看样子还有排序的功能?这可能是数据库原理的内容，记住，以后有机会再回溯...
```
