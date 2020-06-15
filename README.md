# 函数、谓词、CASE表达式

## 各种函数

* 算术函数:用于进行数值计算;
* 字符串函数:用于操作字符串;
* 日期函数:用于对日期操作;
* 转换函数:用于转换数据类型和值;
* 聚合函数:用于进行数据聚合.

创建一个SampleMath表

```sql
--DDL：创建表
CREATE TABLE SampleMath
(m  NUMERIC (10,3),
 n  INTEGER,
 p  INTEGER);

--DML：插入数据
BEGIN TRANSACTION;

INSERT INTO SampleMath(m, n, p) VALUES (500,  0,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (-180, 0,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (NULL, NULL, NULL);
INSERT INTO SampleMath(m, n, p) VALUES (NULL, 7,    3);
INSERT INTO SampleMath(m, n, p) VALUES (NULL, 5,    2);
INSERT INTO SampleMath(m, n, p) VALUES (NULL, 4,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (8,    NULL, 3);
INSERT INTO SampleMath(m, n, p) VALUES (2.27, 1,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (5.555,2,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (NULL, 1,    NULL);
INSERT INTO SampleMath(m, n, p) VALUES (8.76, NULL, NULL);

COMMIT;


--确认表中的内容
SELECT * FROM SampleMath;
    m     | n | p
----------+---+---
  500.000 | 0 |
 -180.000 | 0 |
          |   |
          | 7 | 3
          | 5 | 2
          | 4 |
    8.000 |   | 3
    2.270 | 1 |
    5.555 | 2 |
          | 1 |
    8.760 |   |
(11 行记录)
```

NUMERIc是大多数DBMS都支持的数据类型。且Post SQL中ROUND函数仅支持NUMERIC类型。

### 算术函数

|函数名|功能|语法|
|------|----|---|
|ABS|绝对值|ABS(m)|
|MOD|求余|MOD(被除数，除数)|
|ROUND|四舍五入|ROUND(对象数值，保留小数位数)|

```sql
SELECT m,ABS(m) FROM SampleMath;
    m     |   abs
----------+---------
  500.000 | 500.000
 -180.000 | 180.000
          |
          |
          |
          |
    8.000 |   8.000
    2.270 |   2.270
    5.555 |   5.555
          |
    8.760 |   8.760
(11 行记录)
SELECT m,n, MOD(m,n) FROM SampleMath;
错误:  除以零
SELECT m,p, MOD(m,p) FROM SampleMath;
    m     | p |  mod
----------+---+-------
  500.000 |   |
 -180.000 |   |
          |   |
          | 3 |
          | 2 |
          |   |
    8.000 | 3 | 2.000
    2.270 |   |
    5.555 |   |
          |   |
    8.760 |   |
(11 行记录)


SELECT n,p, MOD(n,p) FROM SampleMath;
 n | p | mod
---+---+-----
 0 |   |
 0 |   |
   |   |
 7 | 3 |   1
 5 | 2 |   1
 4 |   |
   | 3 |
 1 |   |
 2 |   |
 1 |   |
   |   |
(11 行记录)


SELECT n, MOD(n,3) FROM SampleMath;
 n | mod
---+-----
 0 |   0
 0 |   0
   |
 7 |   1
 5 |   2
 4 |   1
   |
 1 |   1
 2 |   2
 1 |   1
   |
(11 行记录)
SELECT m,n,ROUND(m,n) AS round_col
FROM SampleMath;
    m     | n | round_col
----------+---+-----------
  500.000 | 0 |       500
 -180.000 | 0 |      -180
          |   |
          | 7 |
          | 5 |
          | 4 |
    8.000 |   |
    2.270 | 1 |       2.3
    5.555 | 2 |      5.56
          | 1 |
    8.760 |   |
(11 行记录)
```

### 字符串函数

```sql
SELECT * FROM SampleStr;
   str1    | str2 |    str3
-----------+------+------------
 opx       | rt   |
 abc       | def  |
 aaa       |      |
           | xyz  |
 @!#$%     |      |
 ABC       |      |
 aBC       |      |
 abc太郎   | abc  | ABC
 abcdefabc | abc  | ABC
 micmic    | i    | I
 山田      | 太郎 | 是一个名字
(11 行记录)
--数据表，用于学习函数使用
```

|函数|作用|语法|
|:----:|---|----|
|\|\||拼接|字符串1\|\|字符串2|
|LENGTH|字符串长度|LENGTH(字符串)|
|LOWER|小写转换|LOWER(字符串)|
|REPLACE|字符串的替换|REPLACE(对象字符串,替换前的字符串,替换后的字符串)|
|UPPER|大写转换|UPPER(字符串)|
|SUBSTRING|字符串的截取|SUBSTRING(对象字符串 FROM 截取起始位置 FOR 截取的字符数)|

使用方法大多一致

```sql
SELECT 其他列,函数(参数) FROM 表名;
--注:对汉字的大小写无效
--其中有几个需要注意:
SELECT str1,str2,str3,REPLACE(str1,str2,str3) AS rep_str FROM SampleStr;
   str1    | str2 |    str3    |  rep_str
-----------+------+------------+-----------
 opx       | rt   |            |
 abc       | def  |            |
 aaa       |      |            |
           | xyz  |            |
 @!#$%     |      |            |
 ABC       |      |            |
 aBC       |      |            |
 abc太郎   | abc  | ABC        | ABC太郎
 abcdefabc | abc  | ABC        | ABCdefABC
 micmic    | i    | I          | mIcmIc
 山田      | 太郎 | 是一个名字 | 山田
(11 行记录)
/*实质上是将str1内与str2内相同的字符串用str3内的字符串替换*/
SUBSTRING()函数截取的起始位置从字符串最左侧开始计算
```

### 日期函数

|函数|作用|语法|
|----|-----|---|
|CURRENT_DATE|返回SQL执行的日期|CURRENT_DATE|
|CURRENT_TIME|返回SQL执行的时间|CURRENT_TIME|
|CURRENT_TIMESTAMP|返回当前日期和时间|CURRENT_TIMESTAMP|
|EXTRACT|返回日期数据的一部分|EXTRACT(日期元素 FROM 日期)|

```sql
SELECT CURRENT_DATE;
 current_date
--------------
 2020-06-13
(1 行记录)

SELECT CURRENT_TIME;
    current_time
--------------------
 09:18:00.992686+08
(1 行记录)

SELECT CURRENT_TIMESTAMP;
       current_timestamp
-------------------------------
 2020-06-13 09:18:33.953387+08
(1 行记录)

SELECT CURRENT_TIMESTAMP,
EXTRACT(YEAR FROM CURRENT_TIMESTAMP) AS year,
EXTRACT(MONTH FROM CURRENT_TIMESTAMP) AS month,
EXTRACT(DAY FROM CURRENT_TIMESTAMP) AS day,
EXTRACT(HOUR FROM CURRENT_TIMESTAMP) AS hour,
EXTRACT(MINUTE FROM CURRENT_TIMESTAMP) AS minute,
EXTRACT(SECOND FROM CURRENT_TIMESTAMP) AS second;
       current_timestamp       | year | month | day | hour | minute |  second
-------------------------------+------+-------+-----+------+--------+-----------
 2020-06-13 09:22:54.925529+08 | 2020 |     6 |  13 |    9 |     22 | 54.925529
(1 行记录)
```

### 转换函数

|函数|作用|语法|
|----|-----|---|
|CAST|类型转换|CAST(转换前的值 AS 想要转换的数据类型)|
|COALESCE|将NULL转换为其他值|COALESCE(数据1,数据2,数据3...)|

```sql
SELECT CAST('0001' AS INTEGER) AS "转换";
 转换
------
    1
(1 行记录)

SELECT CAST('2009-12-25' AS DATE) AS "日期";
    日期
------------
 2009-12-25
(1 行记录)
SELECT CAST('2009-12-45' AS DATE) AS "日期";
错误:  日期/时间值超出范围: "2009-12-45"
第1行SELECT CAST('2009-12-45' AS DATE) AS "日期";
--有点意思

SELECT str2,COALESCE(str2,'NULL') FROM SampleStr;
 str2 | coalesce
------+----------
 rt   | rt
 def  | def
      | NULL
 xyz  | xyz
      | NULL
      | NULL
      | NULL
 abc  | abc
 abc  | abc
 i    | i
 太郎 | 太郎
(11 行记录)
--COALESCE函数会返回可变参数中第一个非NULL的值
SELECT COALESCE(NULL,NULL,1) AS "转换函数";
 转换函数
----------
        1
(1 行记录)
```

## 谓词

**谓词**，返回值均为真值的函数。之前的=、<、>、<>等运算符的正式名称就是比较谓词。下面会介绍的谓词有：

* LIKE
* BETWEEN
* IS NULL、IS NOT NULL
* IN
* EXISTS

### LIKE

```sql
SELECT * FROM SampleLike;
 strcol
--------
 abcddd
 dddabc
 abdddc
 abcdd
 ddabc
 abddc
(6 行记录)
--实践表
```

进行字符串部分一致查询时需要使用该谓词。LIKE有三种使用方法，假设查询包含"ddd"字符依据查询方法的不同有三种不同结果：

* 前方一致:选出"dddabc"
  * 所谓前方一致,就是作为查询条件的字符串与查询对象字符串起始部分相同的记录的查询方法;
* 中间一致:选出"abcddd" "dddabc" "abdddc"
  * 所谓中间一致,就是查询对象字符串含有作为查询条件的字符串的记录的查询方法,不论查询条件是在哪个位置;
* 后方一致:选出"abcddd"
  * 所谓后方一致,就是作为查询条件的字符串与查询对象字符串末尾部分相同的记录的查询方法.

```sql
--不同查询方法之间的差别极小
--关键在于查询条件前后的"%"的位置.
SELECT * FROM SampleLike WHERE strcol LIKE 'ddd%';
 strcol
--------
 dddabc
(1 行记录)
SELECT * FROM SampleLike WHERE strcol LIKE '%ddd%';
 strcol
--------
 abcddd
 dddabc
 abdddc
(3 行记录)
SELECT * FROM SampleLike WHERE strcol LIKE '%ddd';
 strcol
--------
 abcddd
(1 行记录)
--显然百分号％是指"任意数量的任意字符"
--此外"_"表示"任意一个字符",所以也有
SELECT * FROM SampleLike WHERE strcol LIKE '___ddd';
 strcol
--------
 abcddd
(1 行记录)
SELECT * FROM SampleLike WHERE strcol LIKE '_ddd';
 strcol
--------
(0 行记录)
```

### BETWEEN

使用BETWEEN可以进行范围查询。它有三个参数。

```sql
SELECT product_name,sale_price FROM Product WHERE sale_price BETWEEN 100 AND 1000;--注意，此处的AND也是参数
 product_name | sale_price
--------------+------------
 T恤          |       1000
 打孔器       |        500
 叉子         |        500
 擦菜板       |        880
 圆珠笔       |        100
(5 行记录)
--结果中会包含临界值，即是闭区间
```

### IS NULL、IS NOT NULL

为了取出NULL数据或NOT NULL的数据。

```sql
 SELECT product_name,purchase_price FROM Product WHERE purchase_price IS NULL;
 product_name | purchase_price
--------------+----------------
 叉子         |
 圆珠笔       |
(2 行记录)
SELECT product_name,purchase_price FROM Product WHERE purchase_price IS NOT NULL;
 product_name | purchase_price
--------------+----------------
 T恤          |            500
 打孔器       |            320
 运动T恤      |           2800
 菜刀         |           2800
 高压锅       |           5000
 擦菜板       |            790
(6 行记录)
```

### IN、NOT IN

IN是一种或的关系，用于替换过于冗长的OR语句。

```sql
SELECT product_name,purchase_price
FROM Product
WHERE purchase_price IN (320,500,5000);
 product_name | purchase_price
--------------+----------------
 T恤          |            500
 打孔器       |            320
 高压锅       |           5000
(3 行记录)


shop=# SELECT product_name,purchase_price
FROM Product
WHERE purchase_price NOT IN (320,500,5000);
 product_name | purchase_price
--------------+----------------
 运动T恤      |           2800
 菜刀         |           2800
 擦菜板       |            790
(3 行记录)
```

#### 使用子查询作为IN谓词的参数

IN(NOT IN)谓词具有其他谓词不具有的特点，即可以使用子查询做参数，而子查询就是一张SQL内部生成的表，因此能够将视图作为IN的参数。  
创建一个新的ShopProduct表

```sql
SELECT * FROM ShopProduct;
 shop_id | shop_name | product_id | quantity
---------+-----------+------------+----------
 000A    | 东京      | 0001       |       30
 000A    | 东京      | 0002       |       50
 000A    | 东京      | 0003       |       15
 000B    | 名古屋    | 0002       |       30
 000B    | 名古屋    | 0003       |      120
 000B    | 名古屋    | 0004       |       20
 000B    | 名古屋    | 0006       |       10
 000B    | 名古屋    | 0007       |       40
 000C    | 大阪      | 0003       |       20
 000C    | 大阪      | 0004       |       50
 000C    | 大阪      | 0006       |       90
 000C    | 大阪      | 0007       |       70
 000D    | 福冈      | 0001       |      100
(13 行记录)
```

查询"大阪店在售商品的销售单价"，显然是两张表的联动查询

```sql
SELECT product_name,sale_price FROM Product
WHERE product_id IN (SELECT product_id FROM ShopProduct WHERE shop_id='000C');
 product_name | sale_price
--------------+------------
 运动T恤      |       4000
 菜刀         |       3000
 叉子         |        500
 擦菜板       |        880
(4 行记录)
--NOT IN用法一致，不再列出
```

### EXISTS

EXISTS较为复杂和困难，一言以蔽之，谓词的作用是"判断是否存在满足某种条件的记录"。EXISTS谓词的主语是"记录"。  

```sql
SELECT product_name,sale_price FROM Product AS P
WHERE EXISTS (SELECT * FROM ShopProduct AS SP 
WHERE SP.shop_id='000C' AND SP.product_id=P.product_id);
 product_name | sale_price
--------------+------------
 运动T恤      |       4000
 菜刀         |       3000
 叉子         |        500
 擦菜板       |        880
(4 行记录)
/*用一句话说就是：查询000C店内出售的商品价格。
实现算法是：此条件存在，即ShopProduct关系内，
shop_id字段与000C匹配，而product_id与Product内的product_id匹配。*/
SELECT product_name,sale_price FROM Product AS P
WHERE NOT EXISTS (SELECT * FROM ShopProduct AS SP 
WHERE SP.shop_id='000C' AND SP.product_id=P.product_id);
 product_name | sale_price
--------------+------------
 T恤          |       1000
 打孔器       |        500
 高压锅       |       6800
 圆珠笔       |        100
(4 行记录)
```

**法则 6-1** 通常指定关联子查询作为EXISTS的参数。
**法则 6-2** 作为EXISTS参数的子查询中经常会使用SELECT *。

## CASE表达式

CASE表达式是一种进行运算的功能，用于区分情况，这里的"区分情况"在编程中被称为*条件分支*。CASE表达式分为简单CASE表达式、搜索CASE表达式，后者包括前者所有功能。故本节仅介绍搜索CASE表达式。

```sql
--搜索CASE表达式语法
CASE  WHEN <求值表达式> THEN <表达式>
      WHEN <求值表达式> THEN <表达式>
      WHEN <求值表达式> THEN <表达式>
      ...
      ELSE <表达式>
END
```

其中，WHEN子句中的"<求值表达式>"就是类似"列=值"这样，返回值为真值的表达式。也可以看作是使用逻辑表达式或诸如LIKE、BETWEEN等谓词写出来的表达式。

### CASE使用方法

```sql
SELECT product_name,
CASE WHEN product_type = '衣服' THEN 'A:' || product_type
WHEN product_type = '办公用品' THEN 'B:' || product_type
WHEN product_type = '厨房用具' THEN 'C:' || product_type
ELSE NULL
END AS abc_product_type--整个CASE语句都仅为一列而已
FROM Product;
 product_name | abc_product_type
--------------+------------------
 T恤          | A:衣服
 打孔器       | B:办公用品
 运动T恤      | A:衣服
 菜刀         | C:厨房用具
 高压锅       | C:厨房用具
 叉子         | C:厨房用具
 擦菜板       | C:厨房用具
 圆珠笔       | B:办公用品
(8 行记录)
```

**法则 6-3** 虽然CASE表达式中的ELSE子句可以忽略，但还是请大家不要忽略。
**法则 6-4** CASE表达式中的END不能忽略。

CASE语句可以用于任何位置。

```sql
SELECT product_type,SUM(sale_price) FROM Product
GROUP BY product_type;
 product_type |  sum
--------------+-------
 衣服         |  5000
 办公用品     |   600
 厨房用具     | 11180
(3 行记录)
--虽可以是分类合计,但仍为一列,如何变为分别的三列?
SELECT SUM(CASE WHEN product_type='衣服' THEN sale_price ELSE 0 END)
AS "衣服售价合计",
SUM(CASE WHEN product_type='办公用品' THEN sale_price ELSE 0 END)
AS "办公用品售价合计",
SUM(CASE WHEN product_type='厨房用具' THEN sale_price ELSE 0 END)
AS "厨房用具售价合计"
FROM Product;
 衣服售价合计 | 办公用品售价合计 | 厨房用具售价合计
--------------+------------------+------------------
         5000 |              600 |            11180
(1 行记录)
```

## 习题

* 6.1

```sql
SELECT product_name,purchase_price
FROM Product
WHERE purchase_price NOT IN (500,2800,5000);
 product_name | purchase_price
--------------+----------------
 打孔器       |            320
 擦菜板       |            790
(2 行记录)
---------
SELECT product_name,purchase_price
FROM Product
WHERE purchase_price NOT IN (500,2800,5000,NULL);
 product_name | purchase_price
--------------+----------------
(0 行记录)
--可见NULL不在IN可用范围内，是不是意味着使用IN就不能直接处理带有NULL的数据?
SELECT product_name,purchase_price
FROM Product AS p1
WHERE NOT EXISTS (SELECT * FROM Product WHERE p1.purchase_price=500
OR p1.purchase_price=2800 OR p1.purchase_price=5000 OR p1.purchase_price=NULL);
 product_name | purchase_price
--------------+----------------
 打孔器       |            320
 叉子         |
 擦菜板       |            790
 圆珠笔       |
(4 行记录)
--可以用EXISTS来输出想要的结果
```

* 6.2

```sql
SELECT COUNT(CASE WHEN sale_price<=1000 THEN sale_price ELSE NULL) AS "低价商品" FROM Product;
错误:  语法错误 在 ")" 或附近的
--第1行...SE WHEN sale_price<=1000 THEN sale_price ELSE NULL) AS "低价...
--首次尝试错误，原因是忘了END结束语
SELECT COUNT (CASE WHEN sale_price<=1000 THEN sale_price ELSE NULL END)
AS "低价商品",
COUNT (CASE WHEN sale_price>1001 AND sale_price<=3000 THEN sale_price ELSE NULL END)
AS "中价商品",
COUNT (CASE WHEN sale_price>3000 THEN sale_price ELSE NULL END)
AS "高价商品"
FROM Product;
 低价商品 | 中价商品 | 高价商品
----------+----------+----------
        5 |        1 |        2
(1 行记录)
```
