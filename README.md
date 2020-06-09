# 数据更新

数据的更新处理一般是插入*INSERT*、删除*DELETE*、更新*UPDATE*。此外数据库种用来管理数据更新的概念**事务**。

## 数据的插入(INSERT语句的使用)

要向表中插入时数据，必须先有一个表

```sql
CREATE TABLE ProductIns(
product_id CHAR(4) NOT NULL,
product_name VARCHAR(100) NOT NULL,
product_type VARCHAR(32) NOT NULL,
sale_price INTEGER DEFAULT 0,
purchase_price INTEGER ,
regist_date DATE ,
PRIMARY KEY (product_id));
```

INSERT 语句的语法为

```sql
INSERT INTO <表名>(列1,列2,列3...) VALUES (值1,值2，值3...);
```

插入一行数据

|product_id|product_type|product_type|sale_price|purchase_price|regist_date|
|------|-------|-------|-------|-------|------|-------|
|0001|T恤衫|衣服|1000|500|2009-09-20|

```sql
INSERT INTO ProductIns(product_id,product_name,product_type,sale_price,purchase_price,regist_date) VALUES ('0001','T恤衫','衣服',100,500,'2009-09-20');
INSERT 0 1
--注：此时我粗心输错了一位数，售价1000我输成了100，正好留在后面数据删除或更新处修改
```

**法则 4-1** 原则上，一条插入语句会插入一行数据。

### 多行INSERT

一行行插入太过于麻烦，多种RDBMS都支持多行插入，称为**多行INSERT** *multi row INSERT*

```sql
--普通插入
INSERT INTO ProductIns VALUES('0002','打孔器','办公用品',500,320,'2009-09-11');
INSERT 0 1

INSERT INTO ProductIns VALUES('0003','运动T恤','衣服',4000,2800,NULL);
INSERT 0 1

INSERT INTO ProductIns VALUES('0004','菜刀','厨房用具',3000,2800,'2009-09-20');
INSERT 0 1
--多行插入
INSERT INTO ProductIns VALUES('0002','打孔器','办公用品',500,320,'2009-09-11'),
('0003','运动T恤','衣服',4000,2800,NULL),
('0004','菜刀','厨房用具',3000,2800,'2009-09-20');
INSERT 0 1
--也应用了列清单省略，即不需要把列全部写出来。
```

缺点是由于是多行插入，如果插入出错，不方便找出是哪一行出的问题。此外，Oracle不支持这种格式的多行插入，支持的是：

```sql
INSERT ALL INTO ProductIns VALUES ('0002','打孔器','办公用品',500,320,'2009-09-11') INTO ProductIns VALUES ('0003','运动T恤','衣服',4000,2800,NULL) INTO ProductIns VALUES ('0004','菜刀','厨房用具',3000,2800,'2009-09-20')
SELECT * FROM DUAL;--DUAL是Oracle的一个临时表
```

### 插入NULL

在未设置NOT NULL约束的列可插入NULL，若往有NOT NULL的列插入NULL则会报错，插入失败。

```sql
INSERT INTO ProductIns VALUES('0006','叉子','厨房用具',500,NULL,'2009-09-20');
INSERT 0 1
```

### 插入默认值

CREATE　TABLE语句中设置DEFAULT约束来设定默认值。语法同之前创建ProductIns表时的DEFAULT语句

```sql
DEFAULT<默认值>
DEFAULT 0--设置默认值为0
```

显示插入：就是在相应位置用DEFAULT替换

```sql
INSERT INTO ProductIns VALUES('0007','擦菜板','厨房用具',DEFAULT,790,'2009-04-28');
INSERT 0 1
product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0001       | T恤衫        | 衣服         |        100 |            500 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0005       | 高压锅       | 厨房用具     |       6800 |           5000 | 2009-01-15
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |          0 |            790 | 2009-04-28
(7 行记录)
```

隐式插入：在列清单和值清单中省略该项即可。

```sql
INSERT INTO ProductIns(product_id,product_name,product_type,/*sale_price,*/purchase_price,regist_date) VALUES ('0007','擦菜板','厨房用具',790,'2009-04-28');
```

### 从其他表中复制数据

新建一个空白表，然后从Product表往新建的空白表中复制数据。

```sql
CREATE TABLE ProductCopy(
product_id CHAR(4) NOT NULL,
product_name VARCHAR(100) NOT NULL,
product_type VARCHAR(32) NOT NULL,
sale_price INTEGER DEFAULT 0,
purchase_price INTEGER ,
regist_date DATE ,
PRIMARY KEY (product_id));
--新表的格式和旧表完全一致
INSERT INTO ProductCopy(
product_id,product_name,product_type,sale_price,purchase_price,regist_date
) SELECT--目标行
product_id,product_name,product_type,sale_price,purchase_price,regist_date
FROM Product;--源数据行
INSERT 0 8
```

INSERT语句中的SELECT语句可以使用GROUP BY或WHERE语句。也可以使用聚合函数等。对关系列表之间存取数据来说很方便。  

```sql
 CREATE TABLE ProductType(
product_type VARCHAR(32) NOT NULL,
sum_sale_price INTEGER ,
sum_purchase_price INTEGER ,
PRIMARY KEY (product_type));
CREATE TABLE
--新建一个表

INSERT INTO ProductType (product_type,sum_sale_price,sum_purchase_price)
SELECT product_type,SUM(sale_price),SUM(purchase_price)
FROM Product
GROUP BY Product_type;
INSERT 0 3
--复制+聚合函数插入数据
--就是对应位置对应数据即可
SELECT * FROM ProductType;
 product_type | sum_sale_price | sum_purchase_price
--------------+----------------+--------------------
 衣服         |           5000 |               3300
 办公用品     |            600 |                320
 厨房用具     |          11180 |               8590
(3 行记录)
--表的最终形态
```

实验使用WHERE语句

```sql
--注意: SELECT语句后的列名不需要加括号！！！！！！！否则就是下面那样
INSERT INTO ProductSome (product_id,product_name,sale_price)
SELECT (product_id,product_name,sale_price)
FROM Product
WHERE sale_price>=800;
错误:  INSERT 的指定字段数多于表达式
第1行INSERT INTO ProductSome (product_id,product_name,sale_price)
--会出错，而且这错误提示是真的...根本没啥参考价值
--正常应该是如下这样
INSERT INTO ProductSome (product_id,product_name,sale_price)
SELECT product_id,product_name,sale_price
FROM Product
WHERE sale_price>=800;
INSERT 0 5
--结果
SELECT * FROM ProductSome;
 product_id | product_name | sale_price
------------+--------------+------------
 0003       | 运动T恤      |       4000
 0004       | 菜刀         |       3000
 0005       | 高压锅       |       6800
 0007       | 擦菜板       |        880
 0001       | T恤衫        |       1000
(5 行记录)
--均是售价大于800的商品
```

**法则 4-3** INSERT语句的SELECT语句中，可以使用WHERE子句或者GROUP　BY子句等任何SQL语法(但ORDER BY语句不会产生任何效果)。

## 数据的删除(DELETE语句的使用)

删除大致有两种

1. DROP TABLE语句可以将表完全删除;
2. DELETE语句会留下表(容器)，而删除表中的全部数据。

### DELETE语句的基本语法

```sql
DELETE FROM <表名>

DELETE FROM Product;
--清空Product表
--注意,想要使用
DELETE * FROM Product;
--清空Product表是错误的
```

**法则 4-4** DELETE语句的删除对象不是表或者列，而是记录(行)。

### 搜索型DELETE(删除指定某一行)

指定删除条件后可删除指定的一行或多行，这种DELETE语句被称为搜索型DELETE。语法如下所示：

```sql
DELETE FROM <表名>
WHERE　<条件>;
```

下为演示：

```sql
DELETE FROM Product
WHERE sale_price >= 4000;
DELETE 2

DELETE FROM Product
WHERE product_id='0001';
DELETE 1

SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
(5 行记录)
```

**法则 4-5** 可以通过WHERE子句指定对象条件来删除部分数据。

## 数据的更新(UPDATE语句的使用)

用于修改部分数据错误之类的问题，语法是

```sql
UPDATE <表名>
SET<列名> = <表达式>
--显然这是并不特别精准的update，仅能将某一列做统一修改
```

将Product表内的regist_date设定为2009-10-10

```sql
SELECT * FROM product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-09-20
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-09-20
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2008-04-28
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-11-11
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-09-20
(6 行记录)


UPDATE Product SET regist_date = '2009-10-10';
UPDATE 6
SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-10-10
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-10-10
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-10-10
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2009-10-10
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-10-10
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-10-10
(6 行记录)
```

### 指定条件的UPDATE语句(搜索型UPDATE)

```sql
UPDATE <表名>
SET <列名> = <表达式>--此处的表达式可以为NULL(如果该列没有NOT NULL约束的话)
WHERE <条件>;
```

**法则 4-6** 使用UPDATE语句可以将值清空为NULL(仅限于未设置NOT NULL约束的列)。

假设有需求:"将厨房用具的销售单价变为原来的十倍，进价变为原来的一半，其余不变。"

```sql
SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-10-10
 0004       | 菜刀         | 厨房用具     |       3000 |           2800 | 2009-10-10
 0006       | 叉子         | 厨房用具     |        500 |                | 2009-10-10
 0007       | 擦菜板       | 厨房用具     |        880 |            790 | 2009-10-10
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-10-10
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-10-10
(6 行记录)
--原表

UPDATE Product SET sale_price = sale_price*10
WHERE product_type='厨房用具';
UPDATE 3
UPDATE Product SET purchase_price = purchase_price/2
WHERE product_type='厨房用具';
UPDATE 3
SELECT * FROM Product;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-10-10
 0008       | 圆珠笔       | 办公用品     |        100 |                | 2009-10-10
 0001       | T恤衫        | 衣服         |       1000 |            500 | 2009-10-10
 0004       | 菜刀         | 厨房用具     |      30000 |           1400 | 2009-10-10
 0006       | 叉子         | 厨房用具     |       5000 |                | 2009-10-10
 0007       | 擦菜板       | 厨房用具     |       8800 |            395 | 2009-10-10
(6 行记录)
--更改后
```

上面需要更新两列的内容，分开写过于麻烦且琐碎，这类统一修改的问题，显然可以同时修改

```sql
--多列更新
UPDATE Product
SET sale_price = sale_price*10,
purchase_price = purchase_price/2
WHERE product_type = '厨房用具';

--或者也可以这样
UPDATE Product
SET (sale_price, purchase_price)= (sale_price*10, purchase_price/2)
WHERE product_type = '厨房用具';
--第二种方式在某些DBMS内不适用，所以一般使用第一种
```

## 事务

**法则 4-7** 事务是需要在同一个处理单元中执行的一系列更新处理的集合。
就像之前的更新售价和进货价的例子，就可以整合为一个事务统一处理

### 创建事务

```sql
--语法

事务开始语句;
DML语句1;
DML语句2;
DML语句3;
...
事务结束语句(COMMIT 或 ROLLBACK);
```

标准SQL中并没有定义事务开始语句，由各个DBMS自己定义。

* SQL Server、PostgreSQL
  * BEGIN TRANSACTION
* MySQL
  * START TRANSACTION
* Oracle、DB2
  * 无

事务结束语句均一致为COMMIT 或 ROLLBACK

```sql
 BEGIN TRANSACTION;
BEGIN
UPDATE Product
SET sale_price=sale_price-1000
WHERE product_name='运动T恤';
UPDATE 0--将运动T恤的价格降低1000元
UPDATE Product
SET sale_price=sale_price+1000
WHERE product_name='T恤衫';
UPDATE 1--将T恤衫的价格提高1000元
COMMIT;
COMMIT
```

事务开始是隐性的，但事务结束则需要明确指令。

* COMMIT-提交处理
  COMMIT 是提交事务包含的全部更新处理的结束指令，相当于覆盖保存。一旦提交就无法恢复到事务开始之前的状态了。  
  COMMIT流程=直线进行  
  ①事务开始语句→②执行更新语句(DML)→③执行COMMIT  
  结束后的状态:②中的所有更新均被反映到数据库中
  **法则 4-8** 虽然可以不清楚事务开始的时间点，但是在事务结束时一定要仔细进行确认。  
* ROLLBACK-取消处理
  ROLLBACK 是取消事务包含的全部更新处理的结束指令，相当于文件处理中的取消保存。  
  ROLLBACK流程=掉头回到起点  
  ①事务开始语句→②执行更新语句(DML)→③执行ROLLBACK  
  结束后的状态:和①执行前相同

### 专栏:事务处理是何时开始的

几乎所有的数据库产品的事务都不需要事务开始指令。这是因为大多数情况下事务在数据库连接建立时就已经悄悄开始了，并不需要用户再明确发出指令。  
那么如何辨别各个事务呢?通常由两种形式:

1. 每条SQL语句就是一个事务(自动提交模式);
2. 直到用户执行COMMIT或者ROLLBACK为止算作一个事务。

一般的DBMS均可自由选择任一模式，默认使用自动提交的有SQL Sever、PostgreSQL和MySQL等。在默认使用模式2的Oracle中，事务都是直到用户自己提交或回滚指令才会结束。  
自动提交指令模式下如果显式写出BEGIN/START TRANSACTION...COMMIT/ROLLBACK时，中间的语句不会自动提交，除非遇到结尾的COMMIT。此外如果结尾是ROLLBACK，则中间即使有DELETE语句也可恢复，但**一般单独的DELETE执行后不可恢复**。

### ACID特性

* 原子性*Atomicity*
  * 事务结束时，其中包含的更新要么全部执行(COMMIT)要么完全不执行(ROLLBACK);
* 一致性*Consistency*
  * 事务中包含的处理要符合数据库提前设置的约束，如主键约束或NOT NULL。不合法的更新会被取消。一致性也称为完整性。
* 隔离性*Isolation*
  * 指保证不同事务之间不干扰的特性。
* 持久性*Durability*
  * 指的是在事务(不论是提交还是回滚)结束后，DBMS能够保证该时间点的数据状态会被保存的特性。


### 习题

4.3

```sql
INSERT INTO ProductMargin (product_id,product_name,sale_price,purchase_price,margin) SELECT product_id,product_name,sale_price,purchase_price,(sale_price-purchase_price) FROM Product WHERE product_id='0001' OR product_id='0002' OR product_id='0003';
--将编号为0001、0002、0003的记录复制到ProductMargin中
SELECT * FROM ProductMargin;
 product_id | product_name | sale_price | purchase_price | margin
------------+--------------+------------+----------------+--------
 0002       | 打孔器       |        500 |            320 |    180
 0001       | T恤衫        |       2000 |            500 |   1500
 0003       | 运动T恤      |       4000 |           2800 |   1200
(3 行记录)
--顺序有误因为原表Product和题目要求的并不相同，结果对就行。
--将运动T恤的价格下调为3000，依据此结果更新利润
--实验将价格修改和利润更新放在同一语句中
SET sale_price = 3000,margin = sale_price-purchase_price
WHERE product_name='运动T恤';
UPDATE 1
SELECT * FROM ProductMargin;
 product_id | product_name | sale_price | purchase_price | margin
------------+--------------+------------+----------------+--------
 0002       | 打孔器       |        500 |            320 |    180
 0001       | T恤衫        |       2000 |            500 |   1500
 0003       | 运动T恤      |       3000 |           2800 |   1200
(3 行记录)
--显然不行
-- 1.下调销售单价
UPDATE ProductMargin
   SET sale_price = 3000
 WHERE product_id = '0003';

-- 2.利润再计算
UPDATE ProductMargin
   SET margin = sale_price - purchase_price
 WHERE product_id = '0003';

-- 确认数据更新
SELECT * FROM ProductMargin;
--只能一步步来(目前为止，不知道后面会不会有类似"函数式"的方法)
```
