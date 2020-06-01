# 查询基础

## SELECT语句基础

SELECT语句是SQL中使用最多最基本的SQL语句。

### 列的查询

<kbd>SELECT <列名>,...<br>
 FROM <表名>;</kbd><br>
 此SQL语句包含了SELECT、FROM两个子句。前者后面跟想要查询的列的名称，后者跟目标表的名称。

```SQL
 SELECT Product_id,Product_name,sale_price FROM Product;
 product_id | product_name | sale_price
------------+--------------+------------
 0002       | 打孔器       |        500
 0003       | 运动T恤      |       4000
 0004       | 菜刀         |       3000
 0005       | 高压锅       |       6800
 0006       | 叉子         |        500
 0007       | 擦菜板       |        880
 0008       | 圆珠笔       |        100
 0001       | T恤衫        |       1000
(8 行记录)
# 之前插入时0001号插入失败，最后补上的，可见原始查询仅仅是照搬插入顺序，并不能排序
```

查询结果中列的顺序和SELECT子句中顺序一致。

<kbd>SELECT * FROM <表名></kbd><br>
此命令表示列出所有列，但无法指定列的顺序。

```SQL
SELECT
*
FROM
<表名>
;
#是允许的，但是
SELECT *

FROM <表名>;# 是不允许的，即不能出现无意义空行。
```

**法则 2-1** 星号(*)表示全部列的意思。

### 位列设定别名

目的是为了使结果更直观、方便查看。别名可以为中文，但需要用双引号括起来。

```SQL
SELECT Product_id AS "商品编号",Product_name AS "商品名称",purchase_price AS "进货单价"  FROM Product;
 商品编号 | 商品名称 | 进货单价
----------+----------+----------
 0002     | 打孔器   |      320
 0003     | 运动T恤  |     2800
 0004     | 菜刀     |     2800
 0005     | 高压锅   |     5000
 0006     | 叉子     |
 0007     | 擦菜板   |      790
 0008     | 圆珠笔   |
 0001     | T恤衫    |      500
(8 行记录)
```

**法则 2-2** 设定汉字别名需要用双引号(")括起来。

### 常数的查询

下述SELECT子句中，'商品'是*字符串常数*，第二列38是*数字常数*，'2009-02-24'是*日期常数*。它们将与Product_id列和Product_name列一起被查询出来(更像是给查询结果添加常数)。

```SQL
SELECT '商品' AS string ,38 AS number ,'2009-02-24'AS date ,Product_id AS "商品编号",Product_name AS "商品名称" FROM Product;
 string | number |    date    | 商品编号 | 商品名称
--------+--------+------------+----------+----------
 商品   |     38 | 2009-02-24 | 0002     | 打孔器
 商品   |     38 | 2009-02-24 | 0003     | 运动T恤
 商品   |     38 | 2009-02-24 | 0004     | 菜刀
 商品   |     38 | 2009-02-24 | 0005     | 高压锅
 商品   |     38 | 2009-02-24 | 0006     | 叉子
 商品   |     38 | 2009-02-24 | 0007     | 擦菜板
 商品   |     38 | 2009-02-24 | 0008     | 圆珠笔
 商品   |     38 | 2009-02-24 | 0001     | T恤衫
(8 行记录)
```

SELECT子句中除了可以书写常数，也可以书写计算式(下节出现)。

### 从结果中删除重复行

如果想知道Product表中商品的种类

```sql
SELECT Product_type FROM Product;
 product_type
--------------
 办公用品
 衣服
 厨房用具
 厨房用具
 厨房用具
 厨房用具
 办公用品
 衣服
(8 行记录)
```

虽可以这样做，但有过多的重复项，无意义。这时可以使用DISTINCT来筛选掉重复项。

```SQL
SELECT DISTINCT Product_type FROM Product;
 product_type
--------------
 衣服
 办公用品
 厨房用具
(3 行记录)
```

**法则 2-3** 在SELECT语句中使用DISTINCT可以删除重复行。
在使用DISTINCT中，NULL也会被视为一类数据。DISTINCT可以应用于多个列：

```sql
SELECT DISTINCT Product_type,regist_date FROM Product;
 product_type | regist_date
--------------+-------------
 衣服         |
 厨房用具     | 2008-04-28
 衣服         | 2009-09-20
 办公用品     | 2009-11-11
 厨房用具     | 2009-01-15
 办公用品     | 2009-09-11
 厨房用具     | 2009-09-20
(7 行记录)
```

可见Product_type与regist_date同时一致的数据被合并了。DISTINCT只能放在第一个列名之前。

### WHERE语句(条件查询)

```sql
SELECT <列名>,...FROM<表名> WHERE<条件表达式>;
```

实例如下：

```sql
SELECT Product_id AS "编号",Product_name AS "商品名称",Product_type AS 商品类别 FROM Product WHERE Product_type='厨房用具';
 编号 | 商品名称 | 商品类别
------+----------+----------
 0004 | 菜刀     | 厨房用具
 0005 | 高压锅   | 厨房用具
 0006 | 叉子     | 厨房用具
 0007 | 擦菜板   | 厨房用具
(4 行记录)
# 查询条件可以不是查询的目标列：
SELECT Product_id AS "编号",Product_name AS "商品名称" FROM Product WHERE Product_type='厨房用具';
 编号 | 商品名称
------+----------
 0004 | 菜刀
 0005 | 高压锅
 0006 | 叉子
 0007 | 擦菜板
(4 行记录)
```

SQL子句的顺序是固定的，不能随意更改。
**法则 2-4** WHERE子句紧跟在FROM子句之后。

### 注释

SQL的注释分为1行注释(--)和多行注释(/\*和\*/之间);
**法则 2-5** 注释是SQL语句中用来标识说明或注意事项的部分。-

## 算术运算符和比较运算符

### 算术运算符

```sql
SELECT Product_id AS "商品编号",Product_name AS "商品名称",sale_price AS "价格",sale_price*2 AS "价格*2" FROM Product;
 商品编号 | 商品名称 | 价格 | 价格*2
----------+----------+------+--------
 0002     | 打孔器   |  500 |   1000
 0003     | 运动T恤  | 4000 |   8000
 0004     | 菜刀     | 3000 |   6000
 0005     | 高压锅   | 6800 |  13600
 0006     | 叉子     |  500 |   1000
 0007     | 擦菜板   |  880 |   1760
 0008     | 圆珠笔   |  100 |    200
 0001     | T恤衫    | 1000 |   2000
(8 行记录)
```

**法则 2-6** SELECT子句中可以使用常数或者表达式。
加减乘除被称为算术运算符，SQL不仅仅支持算术运算符也支持其他，暂且不表。需要注意的是NULL，有NULL参与的计算，结果一定是NULL。

### 比较运算符

|运算符|含义|
|------|------|
|=|和~相等|
|<>|和~不相等 |
|>=|大于等于~|
|>|大于~ |
|<=|小于等于~ |
|<|小于~  |

```sql
SELECT Product_id,Product_name,product_type,sale_price FROM Product WHERE sale_price>1000;
 product_id | product_name | product_type | sale_price
------------+--------------+--------------+------------
 0003       | 运动T恤      | 衣服         |       4000
 0004       | 菜刀         | 厨房用具     |       3000
 0005       | 高压锅       | 厨房用具     |       6800
(3 行记录)
SELECT Product_id,Product_name,product_type,regist_date FROM Product WHERE regist_date>'2009-09-27';
 product_id | product_name | product_type | regist_date
------------+--------------+--------------+-------------
 0008       | 圆珠笔       | 办公用品     | 2009-11-11
(1 行记录)
--可以以时间查询
SELECT Product_id AS "商品编号",Product_name AS "商品名称",product_type AS "商品类别",sale_price AS "售价",sale_price-purchase_price AS "利润"  FROM Product WHERE sale_price-purchase_price>500;
 商品编号 | 商品名称 | 商品类别 | 售价 | 利润
----------+----------+----------+------+------
 0003     | 运动T恤  | 衣服     | 4000 | 1200
 0005     | 高压锅   | 厨房用具 | 6800 | 1800
(2 行记录)
--WHERE子句也可以使用计算表达式
```

**法则 2-8** 字符串类型的数据原则上按照字典顺序进行排序，不能与数字的大小顺序混淆。

### 对NULL使用比较运算符

无法通过上述手段查询出NULL记录。所以SQL有单独的针对NULL的运算符<kbd>IS NULL</kbd><br>

```sql
SELECT Product_id AS "商品编号",Product_name AS "商品名称",product_type AS "商品类别",sale_price AS "售价",sale_price-purchase_price AS "利润"  FROM Product WHERE purchase_price IS NULL;
 商品编号 | 商品名称 | 商品类别 | 售价 | 利润
----------+----------+----------+------+------
 0006     | 叉子     | 厨房用具 |  500 |
 0008     | 圆珠笔   | 办公用品 |  100 |
(2 行记录)
--从利润一列可验证，与NULL计算所得一定是NULL
```

若想选取不是NULL的数据，也有运算符<kbd>IS　NOT　NULL</kbd><br>

```sql
SELECT Product_id AS "商品编号",Product_name AS "商品名称",product_type AS "商品类别",sale_price AS "售价",sale_price-purchase_price AS "利润"  FROM Product WHERE purchase_price IS NOT NULL;
 商品编号 | 商品名称 | 商品类别 | 售价 | 利润
----------+----------+----------+------+------
 0002     | 打孔器   | 办公用品 |  500 |  180
 0003     | 运动T恤  | 衣服     | 4000 | 1200
 0004     | 菜刀     | 厨房用具 | 3000 |  200
 0005     | 高压锅   | 厨房用具 | 6800 | 1800
 0007     | 擦菜板   | 厨房用具 |  880 |   90
 0001     | T恤衫    | 衣服     | 1000 |  500
(6 行记录)
```

**法则 2-9** 希望选取NULL记录时，需要在条件表达式中使用IS　NULL运算符。希望选取不是NULL的记录时使用IS NOT NULL运算符。

### 逻辑运算符

SQL中逻辑运算符为NOT、AND、OR;  
用法和真值与其他语言并无不同，也可以通过括号进行优先级更改等，不难，实操一次略过。

```sql
SELECT Product_id AS "商品编号",Product_name AS "商品名称",product_type AS "商品类别",sale_price AS "售价",sale_price-purchase_price AS "利润"  FROM Product WHERE purchase_price IS NOT NULL AND(regist_date>'2008-12-31' OR sale_price-purchase_price>=500);
 商品编号 | 商品名称 | 商品类别 | 售价 | 利润
----------+----------+----------+------+------
 0002     | 打孔器   | 办公用品 |  500 |  180
 0003     | 运动T恤  | 衣服     | 4000 | 1200
 0004     | 菜刀     | 厨房用具 | 3000 |  200
 0005     | 高压锅   | 厨房用具 | 6800 | 1800
 0001     | T恤衫    | 衣服     | 1000 |  500
(5 行记录)
--筛选的是进价非空，注册时间晚于2008-12-31或利润大于500的商品。
```

**法则 2-10** NOT运算符用来否定某一条件，但不能乱用。<br>
**法则 2-11** 多个查询条件进行组合时，需要使用AND运算符或OR运算符。<br>
**法则 2-12** 文氏图很方便。<br>
**法则 2-13** AND运算符的优先级高于OR运算符。想要优先执行OR运算符时需要使用括号。<br>
**法则 2-14**　通过创建真值表，无论多复杂的条件都会更容易理解。<br>

### 含有NULL时的真值

SQL独特的一点，其是三值逻辑，即除真假外还有不确定的选项。

* AND的真值表简述为：同真为真，不同为假，真不为不，假不为假，不不为不。
* OR的真值表简述为：有真则真，全假为假，真不为真，假不为不，不不为不。

所以尽量不要录入NULL值，这也是为什么创建表时某些列要求NOT　NULL约束的原因。
