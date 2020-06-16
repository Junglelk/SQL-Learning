# 集合运算

本章学习使用2张以上的表的SQL语句。通过以行方向(竖)为单位的集合运算符和以列方向(横)为单位的联接，就可以将分散在多张表中的数据组合成为期望的结果。

## 表的加减法

新建表Product2

```sql
SELECT * FROM Product2;
 product_id | product_name | product_type | sale_price | purchase_price | regist_date
------------+--------------+--------------+------------+----------------+-------------
 0001       | T恤          | 衣服         |       1000 |            500 | 2009-09-20
 0002       | 打孔器       | 办公用品     |        500 |            320 | 2009-09-11
 0003       | 运动T恤      | 衣服         |       4000 |           2800 |
 0009       | 手套         | 衣服         |        800 |            500 |
 0010       | 水壶         | 厨房用具     |       2000 |           1700 | 2009-09-20
(5 行记录)
```

### 表的加法-UNION

简单说就是并集。

```sql
SELECT product_id,product_name FROM Product
UNION
SELECT product_id,product_name FROM Product2;
 product_id | product_name
------------+--------------
 0006       | 叉子
 0001       | T恤
 0002       | 打孔器
 0007       | 擦菜板
 0003       | 运动T恤
 0004       | 菜刀
 0005       | 高压锅
 0008       | 圆珠笔
 0010       | 水壶
 0009       | 手套
(10 行记录)
--似乎结果不按顺序来
--如果要求保留重复元素也是可以的
SELECT product_id,product_name FROM Product
UNION ALL
SELECT product_id,product_name FROM Product2 ORDER BY product_id;
 product_id | product_name
------------+--------------
 0001       | T恤
 0001       | T恤
 0002       | 打孔器
 0002       | 打孔器
 0003       | 运动T恤
 0003       | 运动T恤
 0004       | 菜刀
 0005       | 高压锅
 0006       | 叉子
 0007       | 擦菜板
 0008       | 圆珠笔
 0009       | 手套
 0010       | 水壶
(13 行记录)
--使用UNION ALL
```

**法则 7-1** 集合运算符会去掉重复的记录。
**法则 7-2** 在集合运算符中使用ALL选项，可以保留重复行。
(有重复就不叫集合了)

### 选取公共部分-INTERSECT

简单说就是交集。

```sql
SELECT product_id,product_name FROM Product
INTERSECT
SELECT product_id,product_name FROM Product2 ORDER BY product_id;
 product_id | product_name
------------+--------------
 0001       | T恤
 0002       | 打孔器
 0003       | 运动T恤
(3 行记录)
--语法一致，没什么好讲的
```

希望保留重复行时有*INTERSECT ALL*选项。

### 记录的减法-EXCEPT

就是差集，语法一致，但是要注意的是差集运算对关系的前后有要求，不一样的顺序结果不同。

```sql
SELECT product_id,product_name FROM Product
EXCEPT
SELECT product_id,product_name FROM Product2 ORDER BY product_id;
 product_id | product_name
------------+--------------
 0004       | 菜刀
 0005       | 高压锅
 0006       | 叉子
 0007       | 擦菜板
 0008       | 圆珠笔
(5 行记录)

SELECT product_id,product_name FROM Product2
EXCEPT
SELECT product_id,product_name FROM Product ORDER BY product_id;
 product_id | product_name
------------+--------------
 0009       | 手套
 0010       | 水壶
(2 行记录)
```

## 联结(以列为单位对表进行联结)

集合运算是针对行方向的，即运算本身会对行数进行增减。而联结是针对列的，即是从不同表中选取列。联结根据用途有很多种，本次介绍两种

* 内联结
* 外联结

### 内联结-INNER JOIN

使用之前创建的ShopProduct表进行学习。

```sql
--ShopProduct表
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
SELECT * FROM Product;
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
(8 行记录)
```

所谓联结就是"以A中的列为桥梁，将B中满足同样条件的列汇集到一结果中"。

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP INNER JOIN Product AS P
ON SP.product_id=P.product_id;
 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | 打孔器       |        500
 000A    | 东京      | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0002       | 打孔器       |        500
 000B    | 名古屋    | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0004       | 菜刀         |       3000
 000B    | 名古屋    | 0006       | 叉子         |        500
 000B    | 名古屋    | 0007       | 擦菜板       |        880
 000C    | 大阪      | 0003       | 运动T恤      |       4000
 000C    | 大阪      | 0004       | 菜刀         |       3000
 000C    | 大阪      | 0006       | 叉子         |        500
 000C    | 大阪      | 0007       | 擦菜板       |        880
 000D    | 福冈      | 0001       | T恤          |       1000
(13 行记录)
```

内联结要点

* FROM子句
  * **FROM ShopProduct AS SP INNER JOIN Product AS P**使用INNER JOIN就可以将表联结在一起。SP和P为表的别名，别名非必须，但会更方便，因此尽量多使用别名。  
**法则 7-3** 进行联结时需要在FROM子句中使用多张表。
* ON子句
  * **ON SP.product_id=P.product_id**，可以在ON后面指定联结两个表所使用的列，称为联结键。它能起到WHERE的作用，需要指定多个键时，同样可以使用AND、OR。  
**法则 7-4** 进行内联结时必须使用ON子句，并且要写在FROM和WHERE之间。
* SELECT子句
  * **SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price**，用于指定列的来源。  
**法则 7-5** 使用联结时SELECT子句中的列需要按照"<表的别名>.<列名>"的格式书写。

内联结可以使用WHERE子句

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP INNER JOIN Product AS P
ON SP.product_id=P.product_id
WHERE shop_id='000A';
 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | 打孔器       |        500
 000A    | 东京      | 0003       | 运动T恤      |       4000
(3 行记录)
```

联结运算得到的表为临时表，出现后即消失，如果要保存，可以创建视图

```sql
CREATE VIEW ShopProductView("商店ID","商店名称","商品ID","商品名称","售价")
AS
(SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP INNER JOIN Product AS P
ON SP.product_id=P.Product_id);
CREATE VIEW

SELECT * FROM ShopProductView;
 商店ID | 商店名称 | 商品ID | 商品名称 | 售价
--------+----------+--------+----------+------
 000A   | 东京     | 0001   | T恤      | 1000
 000A   | 东京     | 0002   | 打孔器   |  500
 000A   | 东京     | 0003   | 运动T恤  | 4000
 000B   | 名古屋   | 0002   | 打孔器   |  500
 000B   | 名古屋   | 0003   | 运动T恤  | 4000
 000B   | 名古屋   | 0004   | 菜刀     | 3000
 000B   | 名古屋   | 0006   | 叉子     |  500
 000B   | 名古屋   | 0007   | 擦菜板   |  880
 000C   | 大阪     | 0003   | 运动T恤  | 4000
 000C   | 大阪     | 0004   | 菜刀     | 3000
 000C   | 大阪     | 0006   | 叉子     |  500
 000C   | 大阪     | 0007   | 擦菜板   |  880
 000D   | 福冈     | 0001   | T恤      | 1000
(13 行记录)
--一开始忘了视图必须在创建时命名
```

### 外联结-OUT JOIN

外联结也是通过ON子句将两个表联结起来。ON语句对主键约束指定条件。

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP RIGHT OUTER JOIN Product AS P
ON SP.product_id=P.Product_id;

 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | 打孔器       |        500
 000A    | 东京      | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0002       | 打孔器       |        500
 000B    | 名古屋    | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0004       | 菜刀         |       3000
 000B    | 名古屋    | 0006       | 叉子         |        500
 000B    | 名古屋    | 0007       | 擦菜板       |        880
 000C    | 大阪      | 0003       | 运动T恤      |       4000
 000C    | 大阪      | 0004       | 菜刀         |       3000
 000C    | 大阪      | 0006       | 叉子         |        500
 000C    | 大阪      | 0007       | 擦菜板       |        880
 000D    | 福冈      | 0001       | T恤          |       1000
         |           |            | 圆珠笔       |        100
         |           |            | 高压锅       |       6800
(15 行记录)
--可见，最后两行在ShopProduct表内没有，即没有哪个商店出售圆珠笔和高压锅。
--外联结就是"结果中包含原表中不存在的信息"。
```

外联结主表是哪张?指定主表的关键字为*RIGHT*和*LEFT*。顾名思义，使用RIGHT时，FROM右边的是主表，使用LEFT时，FROM左边的是主表。
**法则 7-6** 外联结中使用LEFT、RIGHT指定主表。使用二者得到的数据完全相同。

### 联结多个表

创建一个InventoryProduct表。

```sql
--SQL Server, PostgreSQL
--DDL：创建表
CREATE TABLE InventoryProduct
( inventory_id       CHAR(4)      NOT NULL,
  product_id          CHAR(4)      NOT NULL,
  inventory_quantity  INTEGER      NOT NULL,
  PRIMARY KEY (inventory_id, product_id));

--DML：插入数据
BEGIN TRANSACTION;

INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0001', 0);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0002', 120);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0003', 200);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0004', 3);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0005', 0);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0006', 99);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0007', 999);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P001', '0008', 200);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0001', 10);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0002', 25);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0003', 34);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0004', 19);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0005', 99);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0006', 0);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0007', 0);
INSERT INTO InventoryProduct (inventory_id, product_id, inventory_quantity) VALUES ('P002', '0008', 18);

COMMIT;

SELECT * FROM InventoryProduct;
 inventory_id | product_id | inventory_quantity
--------------+------------+--------------------
 P001         | 0001       |                  0
 P001         | 0002       |                120
 P001         | 0003       |                200
 P001         | 0004       |                  3
 P001         | 0005       |                  0
 P001         | 0006       |                 99
 P001         | 0007       |                999
 P001         | 0008       |                200
 P002         | 0001       |                 10
 P002         | 0002       |                 25
 P002         | 0003       |                 34
 P002         | 0004       |                 19
 P002         | 0005       |                 99
 P002         | 0006       |                  0
 P002         | 0007       |                  0
 P002         | 0008       |                 18
(16 行记录)
```

接下来需求是：列出P001仓库内的商品信息(在售商店信息、售价等)；

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price,IP.inventory_quantity
FROM ShopProduct AS SP INNER JOIN Product AS P
ON SP.product_id=P.product_id
INNER JOIN InventoryProduct AS IP
ON SP.product_id=IP.product_id
WHERE IP.inventory_id='P001'
ORDER BY P.product_id;

shop_id | shop_name | product_id | product_name | sale_price | inventory_quantity
---------+-----------+------------+--------------+------------+--------------------
 000A    | 东京      | 0001       | T恤          |       1000 |                  0
 000D    | 福冈      | 0001       | T恤          |       1000 |                  0
 000B    | 名古屋    | 0002       | 打孔器       |        500 |                120
 000A    | 东京      | 0002       | 打孔器       |        500 |                120
 000A    | 东京      | 0003       | 运动T恤      |       4000 |                200
 000B    | 名古屋    | 0003       | 运动T恤      |       4000 |                200
 000C    | 大阪      | 0003       | 运动T恤      |       4000 |                200
 000B    | 名古屋    | 0004       | 菜刀         |       3000 |                  3
 000C    | 大阪      | 0004       | 菜刀         |       3000 |                  3
 000C    | 大阪      | 0006       | 叉子         |        500 |                 99
 000B    | 名古屋    | 0006       | 叉子         |        500 |                 99
 000B    | 名古屋    | 0007       | 擦菜板       |        880 |                999
 000C    | 大阪      | 0007       | 擦菜板       |        880 |                999
(13 行记录)
--也可以创建成视图
CREATE VIEW ShopProductInventory
("商店ID","商店名称","产品ID","产品名称","售价","库存")
AS
(SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price,IP.inventory_quantity
FROM ShopProduct AS SP INNER JOIN Product AS P
ON SP.product_id=P.product_id
INNER JOIN InventoryProduct AS IP
ON SP.product_id=IP.product_id
WHERE IP.inventory_id='P001'
ORDER BY P.product_id);
CREATE VIEW
--结果与上一致，不再列出
--也可以使用外联结
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price,IP.inventory_quantity
FROM ShopProduct AS SP LEFT OUTER JOIN Product AS P
ON SP.product_id=P.product_id
LEFT OUTER JOIN InventoryProduct AS IP
ON SP.product_id=IP.product_id
WHERE IP.inventory_id='P001'
ORDER BY P.product_id;
 shop_id | shop_name | product_id | product_name | sale_price | inventory_quantity
---------+-----------+------------+--------------+------------+--------------------
 000A    | 东京      | 0001       | T恤          |       1000 |                  0
 000D    | 福冈      | 0001       | T恤          |       1000 |                  0
 000B    | 名古屋    | 0002       | 打孔器       |        500 |                120
 000A    | 东京      | 0002       | 打孔器       |        500 |                120
 000A    | 东京      | 0003       | 运动T恤      |       4000 |                200
 000B    | 名古屋    | 0003       | 运动T恤      |       4000 |                200
 000C    | 大阪      | 0003       | 运动T恤      |       4000 |                200
 000B    | 名古屋    | 0004       | 菜刀         |       3000 |                  3
 000C    | 大阪      | 0004       | 菜刀         |       3000 |                  3
 000C    | 大阪      | 0006       | 叉子         |        500 |                 99
 000B    | 名古屋    | 0006       | 叉子         |        500 |                 99
 000B    | 名古屋    | 0007       | 擦菜板       |        880 |                999
 000C    | 大阪      | 0007       | 擦菜板       |        880 |                999
(13 行记录)
```

可见联结多个表仅仅是在后面加了一个INNER JOIN...ON...子句。内联结和外联结使用方式一致。

### 交叉联结-CROSS JOIN

几乎不会用到的一种联结，只是内外联结的前奏部分。

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP CROSS JOIN Product AS P;
 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | T恤          |       1000
 000A    | 东京      | 0003       | T恤          |       1000
 000B    | 名古屋    | 0002       | T恤          |       1000
 000B    | 名古屋    | 0003       | T恤          |       1000
 000B    | 名古屋    | 0004       | T恤          |       1000
 000B    | 名古屋    | 0006       | T恤          |       1000
 000B    | 名古屋    | 0007       | T恤          |       1000
 .....
 .....
 .....
 (104 行记录)
 ```

对满足相同规则的表进行交叉联结的集合运算符是CROSS JOIN(笛卡尔积)。记录数通常是两个记录的行数的乘积。

### 习题

* 7.1

```sql
SELECT * FROM Product
UNION
SELECT * FROM Product
INTERSECT
SELECT * FROM Product
ORDER BY product_id;
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
(8 行记录)
```

* 7.2

```sql
SELECT SP.shop_id,SP.shop_name,SP.product_id,P.product_name,P.sale_price
FROM ShopProduct AS SP RIGHT OUTER JOIN Product AS P
ON SP.product_id=P.Product_id;
 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | 打孔器       |        500
 000A    | 东京      | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0002       | 打孔器       |        500
 000B    | 名古屋    | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0004       | 菜刀         |       3000
 000B    | 名古屋    | 0006       | 叉子         |        500
 000B    | 名古屋    | 0007       | 擦菜板       |        880
 000C    | 大阪      | 0003       | 运动T恤      |       4000
 000C    | 大阪      | 0004       | 菜刀         |       3000
 000C    | 大阪      | 0006       | 叉子         |        500
 000C    | 大阪      | 0007       | 擦菜板       |        880
 000D    | 福冈      | 0001       | T恤          |       1000
         |           |            | 圆珠笔       |        100
         |           |            | 高压锅       |       6800
(15 行记录)
--错误

SELECT COALESCE(SP.shop_id, '不明')  AS shop_id,
       COALESCE(SP.shop_name, '不明') AS shop_name,
       P.product_id, 
       P.product_name, 
       P.sale_price
  FROM ShopProduct SP RIGHT OUTER JOIN Product P
    ON SP.product_id = P.product_id
ORDER BY shop_id;
 shop_id | shop_name | product_id | product_name | sale_price
---------+-----------+------------+--------------+------------
 000A    | 东京      | 0001       | T恤          |       1000
 000A    | 东京      | 0002       | 打孔器       |        500
 000A    | 东京      | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0002       | 打孔器       |        500
 000B    | 名古屋    | 0003       | 运动T恤      |       4000
 000B    | 名古屋    | 0004       | 菜刀         |       3000
 000B    | 名古屋    | 0006       | 叉子         |        500
 000B    | 名古屋    | 0007       | 擦菜板       |        880
 000C    | 大阪      | 0003       | 运动T恤      |       4000
 000C    | 大阪      | 0004       | 菜刀         |       3000
 000C    | 大阪      | 0006       | 叉子         |        500
 000C    | 大阪      | 0007       | 擦菜板       |        880
 000D    | 福冈      | 0001       | T恤          |       1000
 不明    | 不明      | 0008       | 圆珠笔       |        100
 不明    | 不明      | 0005       | 高压锅       |       6800
(15 行记录)
--COALESCE函数可以转换NULL
--可见NULL本身不是可以随便转换的
```
