# 数据库和SQL

结构化查询语言 (*structured query language*, SQL)是用来定义表格和完整性约束以及访问和操纵数据的语言。

## SQL概要

本书介绍的是标准SQL书写。

**法则 1-3** 学会标准SQL就可以在各种RDBMS中书写SQL语句了。

### SQL语句及其种类

* DDL (Data Definition Language, 数据定义语言)
  用来创建或者删除存储数据用的数据库以及数据库中的表等对象。
  * CREATE 创建数据库和表等对象
  * DROP 删除数据库和表等对象
  * ALTER 修改数据库和表等对象

* DML (Data Manipulation Language, 数据操纵语言)
  用来查询或者变更表中的记录。
  * SELECT 查询表中的记录
  * INSERT 向表中插入记录
  * UPDATE 更新表中的记录
  * DELETE 删除表中的数据

* DCL(Data Control Language, 数据控制语言)
  确认或取消对表内容的变更，设定用户操作权限
  * COMMIT 确认对数据库中的数据进行的变更
  * ROLLBACK　取消对数据库中的数据进行的变更
  * GRANT　赋予用户权限
  * REVOKE　取消用户权限

**法则 1-4** SQL根据功能不同分为三类，其中DML使用最多。

### SQL基本书写规则

**法则 1-5** SQL语句以分号(;)结尾。

**法则 1-6** 关键字不区分大小写。
本记录中

* 关键字大写
* 表名首字母大写
* 其余(列名等)小写

**法则 1-7** 字符串和日期常数需要使用单引号(')括起来;数字常数无需加注单引号。
本记录中日期的格式为'年-月-日'。

**法则 1-8** 单词之间使用半角空格或换行符进行分隔。

## 表的创建

### 数据库的创建

在创建表之前必须先**创建数据库**：
语法

```xml
CREATE DATABASE <数据库名称>;
```

代码清单
```xml
CREATE DATABASE shop;
```

实际开发中会有更多的内容，此处仅为最少的项目。

### 创建表

```xml
CREATE TABLE <表名>
<列名1> <数据类型> <该列所需的约束>,
<列名2> <数据类型> <该列所需的约束>,
<列名3> <数据类型> <该列所需的约束>,
<列名4> <数据类型> <该列所需的约束>,
...
<该表的约束1>,<该表的约束2>,....);
```

### 命名规则

**法则 1-9** 数据库名称、表名和列名可以使用三种字符：半角英文字符、半角数字、下划线。
**法则 1-10** 名称必须以半角英文字母开头。
**法则 1-11** 名称不能重复。

### 数据类型的指定

数据类型表示数据的种类，包括数字型、字符型和日期型等。列不能存储与该列声明类型不同的数据。

* INTEGER
  * 指定存储整数的列的数据类型。
* CHAR
  * 指定用来存储字符串的列。字符串以定长存储在指定为CHAR的列中。不足的补空格。
* VARCHAR
  * 变长字符串，允许存储任意长度的字符串。(Oracle中推荐使用VARCHAR2。)
* DATE
  * 指定存储时间的列。

### 约束的设置

约束是指除指定数据类型外，对列中存储的数据进行限制或追加的条件的功能。

```xml
product_id CHAR(4) NOT NULL;
product_name VARCHAR(100) NOT NULL;
PRODUCT_type VARCHAR(32) NOT NULL;
```

上面设置了NOT NULL约束。NULL代表空，前面加上NOT表示输入不许为空。如果输入为空，则会报错。

```xml
PRIMARY KEY (PRODUCT_ID));
```

设置**主键约束**。所谓键，就是在指定特定数据时使用的列的组合。主键就是可以特定一行数据(唯一确定一行数据)的列。

## 表的更新和删除

### 表的删除

只需一句

```xml
DROP TABLE <表名>;
```  

删除Product表
```xml
DROP TABLE Product;
```

**法则 1-12** 删除的表无法恢复。
(删库跑路！！！)

### 表定义的更新*ALTER　TABLE* 语句

```xml
ALTER TABLE <表名> ADD COLUMN<列的定义>;
```
  
>注：Oracle和SQL Server不用写COLUMN；
>Oracle可同时添加多列
>ALTER TABLE <表名> ADD (<列名>,<列名>,...);

**法则 1-13** 表定义变更后无法恢复。

### 向表中插入数据

此时仅尝试，具体插入等语句在第四章学习。

```xml
begin transaction;
insert into Product values('0001','T恤衫','衣服',1000,50,'2009-09-20');
insert into Product values('0002','打孔器','办公用品',500,320,'2009-09-11');
insert into Product values('0003','运动T恤','衣服',4000,2800,NULL);
insert into Product values('0004','菜刀','厨房用具',3000,2800,'2009-09-20');
insert into Product values('0005','高压锅','厨房用具',6800,5000,'2009-01-15');
insert into Product values('0006','叉子','厨房用具',500,NULL,'2009-09-20');
insert into Product values('0007','擦菜板','厨房用具',880,790,'2008-04-28');
insert into Product values('0008','圆珠笔','办公用品',100,NULL,'2009-11-11');
commit;
```

begin transaction;表示开始插入行的开始，commit;表示确认插入的语句。

**表的改名**

```xml
ALTER TABLE <当前表名> RENAME TO <目标表名>
```

