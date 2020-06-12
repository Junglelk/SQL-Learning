# 复杂查询

* 视图
* 子查询
* 关联子查询

## 视图

从SQL的角度看，视图和表是相同的，两者的区别在于表中保存的是实际的数据，而视图中保存的是SELECT语句，视图本身不保存数据。使用视图可完成跨多表查询数据等复杂操作。

### 视图和表

视图的优点：

1. 由于视图无需保存数据，所以可以节省存储设备的容量；
**法则 5-1** 表中存储的是实际数据，而视图中存储的是从表中取出数据所使用的SELECT语句。
2. 可以将频繁使用的SELECT语句保存成视图，这样就不用每次都重新书写了。
**法则 5-2** 应该解经常使用的SELECT语句做成视图。
(隐隐有种函数的味道)

### 创建视图

使用CREATE VIEW语句，语法如下

```sql
CREATE VIEW 视图名称(<视图列名1>,<视图列名2>,...)
AS
<SELECT 语句>
```

创建及使用VIEW

```sql
CREATE VIEW ProductSum (product_type,cnt_product)
AS
SELECT product_type,COUNT(*)
FROM Product
GROUP BY product_type;
CREATE VIEW
--创建一个VIEW，
SELECT product_type,cnt_product
FROM ProductSum;
--使用这个VIEW
 product_type | cnt_product
--------------+-------------
 衣服         |           2
 办公用品     |           2
 厨房用具     |           4
(3 行记录)

SELECT * FROM ProductSum;
 product_type | cnt_product
--------------+-------------
 衣服         |           2
 办公用品     |           2
 厨房用具     |           4
(3 行记录)
--使用VIEW
```

使用视图查询一般有两个步骤:

1. 首先执行定义视图的SELECT语句;
2. 根据所得到的结果,再执行在FROM子句中使用视图的SELECT语句.

此外,视图和表除了存储数据方面没有任何区别,所以也可以创建视图的视图,即**多重视图**,但多重视图会降低SQL性能.
**法则 5-3** 应该避免在视图的基础上创建视图..

视图的限制:

* 关于ORDER BY语句  
**法则 5-4** 定义视图时不要使用ORDER BY子句。
* 对视图进行更新
  标准SQL有规定:如果定义视图的SELECT语句能够满足某些条件，那么这个视图就可以更新
  1. SELECT子句中未使用DISTINCT
  2. FROM子句中只有一张表
  3. 未使用GROUP BY子句
  4. 未使用HAVING子句  
**法则 5-5** 视图和表要同时进行更新，因此通过汇总得到的视图无法进行更新。

```sql
CREATE VIEW ProductJim (product_id,product_name,product_type,sale_price,purchase_price,regist_date)
AS
SELECT * FROM Product WHERE
product_type='办公用品';
CREATE VIEW
--创建一个可更新视图
INSERT INTO ProductJim VALUES ('0009','印章','办公用品',95,10,'2009-11-30');
INSERT 0 1
--插入数据
SELECT * FROM Product;--可见源表与视图已同步更新
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0001       | T恤          | 衣服         |       1000 |            500 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0009       | 印章         | 办公用品     |         95 |             10 | 2009-11-30
(9 行记录)
SELECT * FROM ProductJim;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0009       | 印章         | 办公用品     |         95 |             10 | 2009-11-30
(3 行记录)
```

### 删除视图

使用DROP VIEW语句删除视图

```sql
DROP VIEW 视图名称(<视图列名1>,<视图列名2>...);
```

```sql
DROP VIEW ProductSum;
DROP VIEW
```

此外，如果删除以基础视图为基础创建出来的多重视图，由于存在关联的视图会出现错误:

```sql
ERROR:由于存在关联视图，因此无法删除视图ProductSum
DETALL: 视图ProductSumJim与视图ProductSum相关联
HINT: 删除关联对象请使用DROP...CASCADE
--这时应使用CASCADE选项来删除关联视图
DROP VIEW ProductSum CASCADE;
```

### 子查询

子查询就是一次性的视图。用于条件比较复杂，用GROUP BY或WHERE无法解决的情况。

```sql
CREATE VIEW ProductSum (product_type,cnt_product)
AS
SELECT product_type,COUNT(*)
FROM Product
GROUP BY product_type;
CREATE VIEW

SELECT * FROM ProductSum;
 product_type | cnt_product
--------------+-------------
 衣服         |           2
 办公用品     |           2
 厨房用具     |           4
(3 行记录)
--使用视图来进行查询

SELECT product_type,cnt_product
FROM (SELECT product_type,COUNT(*) AS cnt_product
FROM Product
GROUP BY product_type) AS ProductSum;

 product_type | cnt_product
--------------+-------------
 衣服         |           2
 办公用品     |           2
 厨房用具     |           4
(3 行记录)
--子查询
--注:在Oracle的FROM子句中，不能使用AS，
--因此，在Oracle中要将"AS ProductSum"变为")ProductSum"
```

子查询结束后，子查询名ProductSum就被删除，不会保存在硬盘中。
**法则 5-6** 子查询作为内层查询会首先执行。
子查询可以嵌套

```sql
SELECT product_type,cnt_product FROM
(SELECT * FROM
(SELECT product_type,COUNT(*) AS cnt_product
FROM Product GROUP BY product_type) AS ProductSum1
WHERE cnt_product=4) AS ProductSum2;

 product_type | cnt_product
--------------+-------------
 厨房用具     |           4
(1 行记录)
--嵌套子查询，从最里层的子查询开始，是更外一层的数据源
```

### 标量子查询

标量就是单一的意思。  
**法则 5-7** 标量子查询就是返回单一值的子查询。  
由于标量子查询返回的是单一值，所以标量子查询语句本身就是一个值，既然是一个值，那就可以参与运算。  
试想这样一个条件查询"查询出销售单价高于平均销售单价的商品"。

```sql
SELECT * FROM Product WHERE sale_price>AVG(sale_price);
错误:  聚合函数不允许出现在WHERE中
第1行SELECT * FROM Product WHERE sale_price>AVG(sale_price);
--显然会出错
SELECT * FROM Product WHERE sale_price>(SELECT AVG(sale_price) FROM Product);
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
(3 行记录)
--这样就可以了
```

*标量子查询*本质上就是一个单一值,可以用于任意需要单一值的位置,包括但不限于SELECT子句、GROUP BY子句、HAVING子句还是ORDER BY子句。**标量子查询绝对不能返回多行结果**。

## 关联子查询

先来一个例子，如果按种类的平均值选取超过某个种类的平均值的商品如何选择? 即
|商品名称|销售单价|
|----|----|
|菜刀|3000|
|高压锅|6800|
|叉子|500|
|擦菜板|880|
表格内商品均为厨房用具,平均价格为(3000+6800+500+880)/4=2795元,则大于平均价格的商品为菜刀和高压锅，这两个产品就是我们的选取对象。如果想直接查询出每个种类的商品中高于平均值的商品，似乎可以这么干:

```sql
SELECT product_type,product_name,sale_price FROM Product
WHERE sale_price>(SELECT AVG(sale_price) FROM Product GROUP BY product_type);
/*查询商品类别,商品名称,商品价格,条件是售价大于平均值,
但这里有一个显然的错误,sale_price是一个单个值,它不能与一个分组完成的表进行比较,
因为后者会返回三行结果*/
```

这时就要使用**关联子查询**

```sql
SELECT product_type,product_name,sale_price
FROM Product AS P1
WHERE sale_price > (SELECT AVG(sale_price)
                      FROM Product AS P2
                    WHERE P1.product_type = P2.product_type
--限定比较的对象是两个查询结果的product_type
                      GROUP BY product_type);
product_type | product_name | sale_price
--------------+--------------+------------
 办公用品     | 打孔器       |        500
 衣服         | 运动T恤      |       4000
 厨房用具     | 菜刀         |       3000
 厨房用具     | 高压锅       |       6800
(4 行记录)
--有种for循环内接for循环的感觉
```

**法则 5-8** 在细分的组内进行比较时，需要使用关联子查询。  
关联子查询的结合条件语句必须写在子查询中。

### 习题

* 5.1

```sql
CREATE VIEW ViewPractice5_1(product_name,sale_price,regist_date)
AS
SELECT product_name,sale_price,regist_date
FROM Product WHERE sale_price>=1000 AND regist_date='2009-09-20';
CREATE VIEW
--创建视图
SELECT * FROM ViewPractice5_1;
 product_name | sale_price | regist_date
--------------+------------+-------------
 T恤          |       1000 | 2009-09-20
 菜刀         |       3000 | 2009-09-20
(2 行记录)
```

* 5.2

```sql
INSERT INTO ViewPractice5_1 VALUES ('刀子',300,'2009-11-02');
错误:  在字段 "product_id" 中空值违反了非空约束
描述:  失败, 行包含(null, 刀子, null, 300, null, 2009-11-02).
--虽然ViewPractice5_1内只有三行，但实际上还是遵守原约束
```

* 5.3

```sql
--第一次尝试
SELECT product_id,product_name,product_type,sale_price,AVG(sale_price) FROM Product;
错误:  字段 "product.product_id" 必须出现在 GROUP BY 子句中或者在聚合函数中使用
第1行SELECT product_id,product_name,product_type,sale_price,AVG(s...
/*错误原因可能是返回类型不同，AVG仅返回一个数，而SELECT是返回一个列*/
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product) AS sale_price_all FROM Product;
 product_id | product_name | product_type | sale_price |    sale_price_all
------------+--------------+--------------+------------+-----------------------
 0001       | T恤          | 衣服         |       1000 | 2097.5000000000000000
 0002       | 打孔器       | 办公用品     |        500 | 2097.5000000000000000
 0003       | 运动T恤      | 衣服         |       4000 | 2097.5000000000000000
 0004       | 菜刀         | 厨房用具     |       3000 | 2097.5000000000000000
 0005       | 高压锅       | 厨房用具     |       6800 | 2097.5000000000000000
 0006       | 叉子         | 厨房用具     |        500 | 2097.5000000000000000
 0007       | 擦菜板       | 厨房用具     |        880 | 2097.5000000000000000
 0008       | 圆珠笔       | 办公用品     |        100 | 2097.5000000000000000
(8 行记录)
```

* 5.4

```sql
--前期试验,错了很多次
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product)AS sale_price_all FROM Product AS P1 WHERE product_type = (SELECT AVG(sale_price) FROM Product AS p2 WHERE p1.product_type=p2.product_type);
错误:  操作符不存在: character varying = numeric
第1行...e_price_all FROM Product AS P1 WHERE product_type = (SELECT ...
                                                          ^
提示:  没有匹配指定名称和参数类型的操作符. 您也许需要增加明确的类型转换.
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product) AS avg_sale_price FROM Product AS P1 WHERE product_type = (SELECT AVG(sale_price) FROM Product AS p2 WHERE p1.product_type = p2.product_type);
错误:  操作符不存在: character varying = numeric
第1行..._sale_price FROM Product AS P1 WHERE product_type = (SELECT ...
                                                          ^
提示:  没有匹配指定名称和参数类型的操作符. 您也许需要增加明确的类型转换.
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product) AS avg_sale_price FROM Product AS P1 WHERE product_type = (SELECT product_type FROM Product AS p2 WHERE p1.product_type = p2.product_type);
错误:  作为一个表达式使用的子查询返回了多列
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product) AS avg_sale_price FROM Product AS P1 WHERE product_type = (SELECT AVG(sale_price) FROM Product AS p2 WHERE p1.product_type = p2.product_type);
错误:  操作符不存在: character varying = numeric
第1行..._sale_price FROM Product AS P1 WHERE product_type = (SELECT ...
                                                          ^
提示:  没有匹配指定名称和参数类型的操作符. 您也许需要增加明确的类型转换.
SELECT product_id,product_name,product_type,sale_price,(SELECT AVG(sale_price) FROM Product) AS avg_sale_price FROM Product AS P1 GROUP BY product_type;
错误:  字段 "p1.product_id" 必须出现在 GROUP BY 子句中或者在聚合函数中使用
第1行SELECT product_id,product_name,product_type,sale_price,(SELE...
/*以上为实验
显然实验都失败了*/
SELECT product_id,product_name,product_type,sale_price,
(SELECT AVG(sale_price) FROM Product AS P2
WHERE P1.product_type=P2.product_type) AS avg_sale_price
FROM Product AS P1;

 product_id | product_name | product_type | sale_price |    avg_sale_price
------------+--------------+--------------+------------+-----------------------
 0001       | T恤          | 衣服         |       1000 | 2500.0000000000000000
 0002       | 打孔器       | 办公用品     |        500 |  300.0000000000000000
 0003       | 运动T恤      | 衣服         |       4000 | 2500.0000000000000000
 0004       | 菜刀         | 厨房用具     |       3000 | 2795.0000000000000000
 0005       | 高压锅       | 厨房用具     |       6800 | 2795.0000000000000000
 0006       | 叉子         | 厨房用具     |        500 | 2795.0000000000000000
 0007       | 擦菜板       | 厨房用具     |        880 | 2795.0000000000000000
 0008       | 圆珠笔       | 办公用品     |        100 |  300.0000000000000000
(8 行记录)
--P1与P2的位置并没有严格的前后关系?
--下为官方答案
-- 创建视图的语句
CREATE VIEW AvgPriceByType AS
SELECT product_id,
       product_name,
       product_type,
       sale_price,
       (SELECT AVG(sale_price)
          FROM Product P2
         WHERE P1.product_type = P2.product_type
         GROUP BY P1.product_type) AS avg_sale_price
 FROM Product P1;

-- 确认视图内容
SELECT * FROM AvgPriceByType;
--结果一致,不再列出
```
