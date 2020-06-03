# 聚合和排序

## 对表进行聚合查询

### 聚合函数

用于汇总的函数称为聚合函数或聚集函数，所谓聚合，就是多行汇总成一行，即输入多行，输出一行。
5个常用的聚合函数
|函数名|功能|
|------|------|
|COUNT|计算表的行数|
|SUM|求和|
|AVG|求平均|
|MAX|最大值|
|MIN|最小值|

```sql
SELECT COUNT(*)  AS "行数" FROM Product;
 行数
------
    8
(1 行记录)

SELECT COUNT(*)  FROM Product;
 count
-------
     8
(1 行记录)
--可见可以给函数的输出值(返回值)重命名
```

COUNT(\*)中的\*为参数 *parameter*，参数可变，根据输入的参数名不同，统计的行数也不一定相同，因为COUNT在统计某一行时，NULL数据不纳入统计。

```sql
SELECT *  FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |NULL
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |           NULL | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |           NULL | 2009-11-11
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(8 行记录)
SELECT COUNT(*) AS "关系的总行数",COUNT(sale_price) AS "售价的行数",COUNT(regist_date) AS "进货日期的行数",COUNT(purchase_price) AS "进货价的行数" FROM Product;
 关系的总行数 | 售价的行数 | 进货日期的行数 | 进货价的行数
--------------+------------+----------------+--------------
            8 |          8 |              7 |            6
(1 行记录)
```

**法则 3-1** COUNT函数的结果根据参数不同而不同。COUNT(*)会得到包含NULL的数据行数，而COUNT(<列名>)会得到NULL以外的数据的行数。

此外，聚合函数的基本用法都一致。  
**法则 3-2** 需要注意的是除COUNT(*)外，其余情况下对NULL的处理都是排除在外，NULL不计数也不参与计算。

```sql
SELECT SUM(sale_price)AS "售价合计" FROM Product;
 售价合计
----------
    16780
(1 行记录)

SELECT AVG(sale_price)AS "平均售价" FROM Product;
       平均售价
-----------------------
 2097.5000000000000000

SELECT MAX(sale_price)AS "售价最大值",MIN(sale_price) AS "售价最小值" FROM Product;
 售价最大值 | 售价最小值
------------+------------
       6800 |        100
(1 行记录)
```

**法则 3-3** MAX\MIN函数几乎适用于所有数据类型，而SUM\AVG只能用于数值类型的列。  

### 使用聚合函数删除重复值(DISTINCT)

**法则 3-4** 想要计算值的种类时，可以在COUNT函数的参数中使用DISTINCT。

```sql
SELECT COUNT(DISTINCT Product_type) AS "产品种类数" FROM Product;
 产品种类数
------------
          3
(1 行记录)
```

**法则 3-5** 在聚合函数的参数中使用DISTINCT，可以删除重复数据。

```sql
 SELECT sale_price AS "售价" FROM Product;
 售价
------
  500
 4000
 3000
 6800
  500
  880
  100
 1000
(8 行记录)
--售价有两个500
SELECT SUM(DISTINCT sale_price) AS "删除重复数据的售价之和",SUM(sale_price) AS "原始售价之和" FROM Product;
 删除重复数据的售价之和 | 原始售价之和
------------------------+--------------
                  16280 |        16780
(1 行记录)
```

## 对表进行分组

先把表分成几组再汇总处理。需要用到GROUP BY子句

```sql
SELECT <列名1>,<列名2>,<列名3>...
FROM <表名>
GROUP BY <列名1>,<列名2>,<列名3>...;
```

按照商品种类来统计一下数据行

```sql
SELECT product_type,COUNT(*) FROM Product GROUP BY Product_type;
 product_type | count
--------------+-------
 衣服         |     2
 办公用品     |     2
 厨房用具     |     4
(3 行记录)
--依据种类对行数进行分组，所得结果解读为：衣服有两行；办公用品有2行；厨房用具有4行。
```

GROUP BY 子句中指定的列称为**聚合键**或者**分组列**，可用逗号隔开多个分组列，但最终结果以最多的分组为准：

```sql
SELECT product_type,COUNT(*) FROM Product GROUP BY Product_type,sale_price;
 product_type | count
--------------+-------
 衣服         |     1
 厨房用具     |     1
 衣服         |     1
 厨房用具     |     1
 厨房用具     |     1
 办公用品     |     1
 厨房用具     |     1
 办公用品     |     1
(8 行记录)
--可见，以种类和售价分组，最终结果以售价为为分组依据(猜测)
```

**法则 3-6** GROUP BY就像是切分表的一把刀。

子句书写顺序

* SELECT→FROM→WHERE→GROUP BY
  
**法则 3-7** SQL子句顺序不能改变，也不能互相替换。

### 聚合键中包含NULL的情况

以进货价为聚合键对表进行切分

```sql
SELECT purchase_price ,COUNT(*) FROM Product GROUP BY purchase_price;
 purchase_price | count
----------------+-------
                |     2
            320 |     1
            500 |     1
           2800 |     2
           5000 |     1
            790 |     1
(6 行记录)
```

**法则 3-8** 聚合键中包含NULL时，在结果中会以“不确定”行(空行)的形式表现出来。

### 使用WHERE时的GROUP BY的执行结果

```sql
SELECT <列名1>,<列名2>,<列名3>...
FROM <表名>
WHERE
GROUP BY <列名1>,<列名2>,<列名3>...;
```

添加WHERE子句后，会先应用WHERE过滤，再汇总：

```sql
SELECT product_name AS "商品名称",purchase_price AS "进货价" FROM Product WHERE product_type='衣服';
 商品名称 | 进货价
----------+--------
 运动T恤  |   2800
 T恤衫    |    500
(2 行记录)
--上面是先由WHERE筛选后的数据，下面是由GROUP BY切割后的数据
SELECT purchase_price ,COUNT(*) FROM Product WHERE product_type = '衣服'GROUP BY purchase_price;
 purchase_price | count
----------------+-------
            500 |     1
           2800 |     1
(2 行记录)
```

### 与聚合函数和GROUP BY有关的常见错误

1. 在SELECT子句写了多余的列  
   使用聚合函数时，在SELECT子句中只能存在以下三种元素

* 常数
* 聚合函数
* GROUP BY子句中指定的列名(即聚合键)

这里常出现的错误是**把聚合键之外的列名书写在SELECT子句中**。

```sql
SELECT product_name,Product_type,COUNT(*) FROM Product GROUP BY product_type;
错误:  字段 "product.product_name" 必须出现在 GROUP BY 子句中或者在聚合函数中使用
第1行SELECT product_name,Product_type,COUNT(*) FROM Product GROUP...
--product_name非聚合键，不应出现在SELECT子句中。
SELECT Product_type,COUNT(*) FROM Product GROUP BY product_type;
 product_type | count
--------------+-------
 衣服         |     2
 办公用品     |     2
 厨房用具     |     4
(3 行记录)
--如此才正确
```

**法则 3-9** 使用GROUP BY子句时，SELECT子句不能出现聚合键之外的列名。  
2. 在GROUP BY子句中写了列的别名  

```sql
SELECT Product_type AS PT ,COUNT(*) FROM Product GROUP BY PT;
    pt    | count
----------+-------
 衣服     |     2
 办公用品 |     2
 厨房用具 |     4
(3 行记录)
--在PostgreSQL和MySQL中不会出错，但在别的DBMS不能用
```

**法则 3-10** 在GROUP BY子句中不能使用SELECT子句中定义的别名。  
3. GROUP BY子句的结果能排序吗
GROUP BY的结果顺序是完全随机的，若要按照某种顺序进行排序的话，必须在SELECT语句中指定。具体下一章介绍。
**法则 3-11** GROUP BY子句结果的显示是无序的。  
4. 在WHERE子句中使用聚合函数  
**法则 3-12** 只有SELECT子句和HAVING子句(以及ORDER BY子句)中能够使用聚合函数。  

## 为聚合结果指定条件

### HAVING子句

在已经使用GROUP BY分好组的集合中，取出特定条件的一组或几组，需要使用HAVING子句。

```sql
SELECT <列名1>,<列名2>,...
FROM <表名>
GROUP BY <列名1>,<列名2>,...
HAVING <分组对应的条件>
```

使用HAVING子句时顺序为  

* SELECT→FROM→WHERE→GROUP BY→HAVING

**法则 3-13** HAVING子句要写在GROUP BY子句之后。

```sql
SELECT product_type,COUNT(*) FROM Product GROUP BY Product_type HAVING COUNT(*)=2;
 product_type | count
--------------+-------
 衣服         |     2
 办公用品     |     2
(2 行记录)
--不使用HAVING
SELECT product_type,COUNT(*) FROM Product GROUP BY Product_type;
 product_type | count
--------------+-------
 衣服         |     2
 办公用品     |     2
 厨房用具     |     4
(3 行记录)
```

* HAVING子句中能使用的3种要素如下：
  * 常数
  * 聚合函数
  * GROUP BY子句中指定的列名(即聚合键)

### 相对于HAVING更适合写在WHERE中的条件

有些条件既可以写在HAVING子句中又可以写在WHERE子句中，这些条件是**聚合键对应的条件**。

```sql
SELECT product_type,COUNT(*) FROM Product WHERE product_type = '衣服' GROUP BY Product_type;
 product_type | count
--------------+-------
 衣服         |     2
(1 行记录)
--此语句与下述语句等价
SELECT product_type,COUNT(*) FROM Product GROUP BY Product_type HAVING Product_type='衣服';
 product_type | count
--------------+-------
 衣服         |     2
(1 行记录)
```

条件的内容和返回值均一致，仅结果看，似乎随便怎么写都无所谓，但作者认为，聚合键所对应的条件应当写在WHERE子句中。  
理由有二。首先，根本原因是WHERE和HAVING子句的作用不同。HAVING是用来指定“组”的条件的。因此“行”对应的条件还是应该写在WHERE子句中。这样写出的SELECT子句更容易理解。  
WHERE子句=指定行所对应的条件  
HAVING子句=指定组所对应的条件  
其次，WHERE子句比HAVING子句执行速度更快。

## 对查询结果进行排序

### ORDER BY子句

对查询结果进行排序是常见的需求，在SQL中使用ORDER BY子句进行排序。ORDER BY子句的语法如下

```sql
SELECT <列名1>,<列名2>,<列名3>...
FROM <表名>
ORDER BY <排序基准列1>,<排序基准列2>,...;
```

如下实验

```sql
SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(8 行记录)


shop=# SELECT * FROM Product ORDER BY product_id;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
(8 行记录)
```

ORDER BY子句中书写的列名称为**排序键**。
**法则 3-15** ORDER　BY子句通常写在SELECT语句的末尾。

### 指定升序或降序

在排序键后指定关键字DESC，结果就为降序，指定关键字ASC指定结果为升序。  
**法则 3-16** 未指定ORDER BY子句中排列顺序时会默认使用升序进行排列。  

```sql
SELECT * FROM Product ORDER BY product_id ASC,sale_price DESC;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
(8 行记录)


shop=# SELECT * FROM Product ORDER BY product_type ASC,sale_price DESC;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(8 行记录)


shop=# SELECT * FROM Product ORDER BY product_type ASC;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(8 行记录)
```

可见同时指定两个列一为升一为降时，如果第一个指定的列无冲突，则不会应用第二个指定的顺序，仅有第一个存在相同项时，第二个才应用。也可以指定更多排序键。

### 对NULL的处理

由于NULL为"不确定"，所以无法对NULL进行比较，各大DBMS对含有NULL的列作为排序键时，NULL会在开头或结尾汇总显示。

```sql
SELECT * FROM Product ORDER BY regist_date DESC;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
(8 行记录)


shop=# SELECT * FROM Product ORDER BY regist_date ASC;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
(8 行记录)
--PostgreSQL中在开头和结尾汇总NULL似乎跟升降序有关
```

**法则 3-17** 排序键中包含有NULL时，会在开头或结尾进行汇总。

### 在排序键中使用别名

在GROUP BY自己中不能使用SELECT中定义的别名，但在ORDER BY中可以。  
因为在SQL内部，使用HAVING子句时的**执行**顺序为：  
FROM→WHERE→GROUP　BY→HAVING→SELECT→ORDER BY  
具体流程根据DBMS不同而不同，有一个印象即可。SELECT子句执行顺序在**GROUP BY之后，ORDER BY之前**。所以在GROUP BY自己中不能使用SELECT中定义的别名，但在ORDER BY中可以。  
**法则 3-18** 在ORDER BY子句中可以使用SELECT子句中定义的别名。
**法则 3-19** 在ORDER BY子句中可以使用SELECT子句中未使用的列和聚合函数。  

小想法：分组计算每组的平均值

```sql
SELECT Product_type,COUNT(*),AVG(sale_price) FROM Product GROUP BY product_type ORDER BY AVG(sale_price) DESC;
 product_type | count |          avg
--------------+-------+-----------------------
 厨房用具     |     4 | 2795.0000000000000000
 衣服         |     2 | 2500.0000000000000000
 办公用品     |     2 |  300.0000000000000000
(3 行记录)
```

### 其他注意事项

**法则 3-19** 在ORDER BY子句中可以使用SELECT子句中未使用的列和聚合函数。

```sql
SELECT Product_type,COUNT(*) FROM Product GROUP BY product_type ORDER BY AVG(sale_price) DESC;
 product_type | count
--------------+-------
 厨房用具     |     4
 衣服         |     2
 办公用品     |     2
(3 行记录)
--依据平均值的降序排序，显然此聚合函数并没有出现在SELECT中，但仍可用
```

**法则 3-20** 在ORDER子句中不要使用列编号。
列编号是SQL中对列从左到右的以此编号(1,2,3...)，因此有如下结果：  

```sql
SELECT product_id,product_name,sale_price,purchase_price FROM Product ORDER BY sale_price DESC,product_id;
 product_id | product_name | sale_price | purchase_price
------------+--------------+------------+----------------
 0005       | 高压锅       |       6800 |           5000
 0003       | 运动T恤      |       4000 |           2800
 0004       | 菜刀         |       3000 |           2800
 0001       | T恤衫        |       1000 |            500
 0007       | 擦菜板       |        880 |            790
 0002       | 打孔器       |        500 |            320
 0006       | 叉子         |        500 |
 0008       | 圆珠笔       |        100 |
(8 行记录)

--下面是使用数字的结果
shop=# SELECT product_id,product_name,sale_price,purchase_price FROM Product ORDER BY 3 DESC,1;
 product_id | product_name | sale_price | purchase_price
------------+--------------+------------+----------------
 0005       | 高压锅       |       6800 |           5000
 0003       | 运动T恤      |       4000 |           2800
 0004       | 菜刀         |       3000 |           2800
 0001       | T恤衫        |       1000 |            500
 0007       | 擦菜板       |        880 |            790
 0002       | 打孔器       |        500 |            320
 0006       | 叉子         |        500 |
 0008       | 圆珠笔       |        100 |
(8 行记录)
```

但由于代码阅读困难，且这个功能将来会被SQL标准取消，所以尽量不要使用此功能。