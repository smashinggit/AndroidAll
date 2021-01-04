[toc]

# MySQL数据库


# 一、MySQL是什么

简单地说：数据库(Database或DB)是存储、管理数据的容器
严格地说：数据库是“按照某种数据结构对数据进行组织、存储和管理的容器”


# 二、数据库的常见概念

- DB：数据库，存储数据的容器
- DBMS：数据库管理系统，又称位数据库软件或数据库产品，用于创建或管理DB.
- SQL：结构化结构查询语言，用于和数据库通信的，是几乎所有的主流数据库软件通用的语言，
       不是某个数据库软件特有的

- 所谓安装数据库服务器，只是在机器上装了一个数据库管理程序，这个管理程序可以管理多个数据库，
  一般开发人员针对每一个应用创建一个数据库
  
- 为保存应用中实体的数据，一般会在数据库创建多个表，以保存程序中的实体的数据；

- 数据库服务器、数据库和表的关系如图所示：
[数据库.png]



# 三、数据库存储数据的特点

- 数据存放到表中，然后表再放到库中。
- 一个库中可以又多张表，每张表具有唯一的表名来标识自己
- 表中有一个或多个列，列又称为**字段**,相当于Java中的属性
- 表中，每一行数据，相当于java中的对象。
- 表的一行称之为一条记录；
- 表中一条记录对应一个java对象的数据。

# 四、SQL语言的分类

## DML（Data Manipulation Language）:
  数据操纵语句，用于**添加、删除、修改、查询**数据库记录，并检查数据完整性。
  
用途：DML用于查询于修改数据记录，包括如下SQL语句：
  
- INSERT：添加数据到数据库中。
- DELETE：删除数据库中的数据。
- UPDATE ：修改数据库中的数据。
- SELECT：选择(查询)数据.

  

## DDL(Data Definition Language)：
  数据定义语句，用于库和表的创建、修改、删除。
  
用途：DDL用于定义数据库的结构，比如创建、修改或删除 数据库对象，包括如下SQL语句：
  
- CREATE TABLE：创建数据库表.
- ALTER TABLE：更改表结构、添加、删除、修改列长度.
- DROP TABLE：删除表.
- CREATE INDEX：在表上建立索引.
- DROP INDEX：删除索引.

  
## DCL(Data Control Language):
  数据库控制语句，用于定义用户的访问权限和安全级别。
  
用途：DCL用来控制数据库的访问，包括如下SQL语句：

- GRANT：授予访问权限.
- REVOKE：撤销访问权限.
- COMMIT ：提交事务处理.
- ROLLBACK: 事务处理回退.
- SAVEPOINT:设置保存点.
- LOCK：对数据库的特定部分进行锁定.


# 五、数据库的基本操作

## 创建数据库

```
CREATE DATABASE [IF NOT EXISTS] <数据库名> [[DEFAULT] CHARACTER SET <字符集名>] [[DEFAULT] COLLATE <校对规则名>];
```

也可以直接简便的创建数据库：
```
CREATE DATABASE [IF NOT EXISTS] <数据库名>
```

- IF NOT EXISTS：在创建数据库之前进行判断，只有该数据库目前尚不存在时才能执行操作。
                 此选项可以用来避免数据库已经存在而重复创建的错误。
- [DEFAULT] CHARACTER SET：指定数据库的默认字符集。
- [DEFAULT] COLLATE：指定字符集的默认校对规则。


## 创建表

```
CREATE TABLE table_name(
    field1 datetype DEFAULT NULL,
    field2 datetype COMMENT NULL,
    field3 datetype,
)
```

field：指定列名。
datatype：指定列类型。

注意：
   创建表时，要根据需保存的数据创建相应的列，并根据数据的类型定义相应的列类型，
   实现添加自增长语句，主键字段后加 auto increment(只适用MySQL)



## 修改表 : ALTER TABLE

使用 ALTER TABLE 语句追加、修改、或者删除列的语法。

- 添加列
    
```
ALTER TABLE  table_name
ADD ( column  datatype  [DEFAULT expr]
     [,column  datatype ] ...)

```
- 修改列

```
ALTER TABLE table_name
MODIFY ( column datatype [DEFAULT expr] 
         [,column  datatype ] ... )
```

- 删除列

```
ALERT TABLE table_name
DROP column

```

- 修改表的名称
```
Rename table 表名 to 新表名
```

- 修改表的字符集
```
alter table student character set utf8;

```

## 对表中数据的操作

### 增 ：INSERT

语法：
```
INSERT INTO table_name [ ( column [, cloumn ...] ) ]
VALUES (value [, value ...])      
```

插入数据
- 为每一列添加一个新值。
- 按列的默认顺序列出各个列的值。
- 在INSERT 子句中随意列出列名和他们的值。
- 字符和日期数据应包含在单引号中。
- 使用这种语法一次只能向表中插入一条数据


例如：
```
INSERT INTO users(id,name,age)
VALUES (001,'张三',16)

```


### 删

语法
```
DELETE FROM table_name
[WHERE condition]
```

例如:
```
DELETE FROM users 
WHERE id = 001
```

如果省略WHERE字句，则表中的全部数据都将被删除。
```
DELETE FROM users
```


### 改 : UPDATE

语法
```
UPDATE table_name 
SET  column = value [,column = value, ...]
[WHERE  condition]
```

- 可以一次更新多条数据。
- 如果需要回滚护具，需要保证在DML前，进行设置： SET AUTOCOMMIT = FALSE;

例如：
```
UPDATE users 
SET age = 17
WHERE name = '张三'
```

如果省略WHERE字句，则表中的所有数据都将被更新。
```
UPDATE users 
SET age = 17
```



### 查

语法:
```
SELECT * | {[DISTINCT] column | expression [alias] , ...}
FROM  table_name
[WHERE condition]
```


- SELECT : 标识选择哪些列
- FROM : 标识从哪个表中选择

例如：

```
// 选择全部列
SELECT * FROM users

//选择特定的列
SELECT name,age FROM USERS
```


### 过滤和排序数据

#### WHERE

将不能满足条件的进行过滤掉。

```
SELECT * | {[DISTINCT] column | expression [alias] , ...}
FROM  table_name
[WHERE condition]
```

WHERE字句紧随FROM字句


#### BETWEEN 

```
// 查询 age 在15到25之间的学生姓名和年龄
SELECT name,age FROM users 
WHERE age between 15 and 25
```

#### IN 

使用 IN运算显示列表中的值。

```
// 查询id 为 001,002,003 的学生姓名和年龄
SELECT name,age FROM users
WHERE id IN (001,002,003)
```


#### LIKE 查询约束
 
使用LIKE运算选择类似的值。
选择条件可以包含字符或数字：
- %   代表零各或多个字符（任意个字符）
- _   代表一个字符

```
SELECT name FROM users
WHERE name LIKE '张%'

```


#### ORDER BY

使用ORDER BY 子句排序
- ASC(ascend):升序
- DESC(descend)：降序

ORDER BY 子句在SELECT语句的 结尾

```
SELECT name,age FROM users 
ORDER BY ASC
```

###  求值函数
- AVG()  平均数
- MAX()
- MIN()
- SUM() 
- COUNT()  计数


```
SELECT AVG(age),MAX(age),MIN(age),SUM(age)
FROM users
where sex = '男'
```

```
// COUNT(*) 返回表中记录总数,适用于任意数据类型。
SELECT COUNT(*) 
FROM users
```


## 多表查询

```
SELECT table1.column, table2.column
FROM  table1,table2
WHERE table1.column1 = tab;e2.column2
```

- 在列表中有相同列时，在列名之间加上表名前缀
- 在WHERE子句中写入连接条件


# 约束

## 什么是约束？

- 为了保证数据的一致性和完整性，SQL 规范以**约束**的方式对表数据进行额外的条件限制
- 约束是表级的强制规定
- 可以在创建表时规定约束（通过 CREATE TABLE 语句，或者在表创建之后也可以(通 过 ALTER TABLE 语句

## 六种约束

1. NOT NULL : 非空约束，规定某个字段不为空
2. UNIQUE : 唯一约束，规定某个字段在表中是唯一的
3. PRIMARY KEY ：主键(非空且唯一)
4. FOREIGN KEY : 外键 
5. CHECK : 检查约束
6. DEFAULT : 默认值


### NOT NULL
 
非空约束用于确保当前列的值不为空值，非空约束只能出现在表对象的列上

### UNIQUE    

同一个表可以有多个唯一约束，多个列组合的约束。
在创建唯一约束的时候，如果不给唯一约束名称，就 默认和列名相同
MySQL会给唯一约束的列上默认创建一个唯一索引。

示例：
```
CREATE TABLE users( id INT NOT NULL, name VARCHAR(25), password VARCHAR(16),
#使用表级约束语法
CONSTRAINT uk_name_pwd  UNIQUE(name,password) 
)

```

添加唯一约束
```
ALERT TABLE users
ADD UNIQUN(name,password)


ALERT TABLE users
MODIFY name VARCHAR(20) UNIQUE
```

删除约束
```
ALERT TABLE users
DROP INDEX uk_name_pwd
```


### PRIMARY KEY 约束

- 主关键字约束指定表的一列或几列的组合的值在表中具有惟一性，
  即能惟一地指定一行记录。每个表中只能有一列被指定为主关键字
- 主键约束相当于唯一约束+非空约束的组合，主键约束列不允许重复，也不允许出现空值
- 如果是多列组合的主键约束，那么这些列都不允许为空值，并且组合的值不允许重复。
- 多列组成的主键叫联合主键，而且联合主键约束只能设定为表级约束；
  单列组成的主键，既可设定为列级约束，也可以设定为表级约束。
- MySQL的主键名总是PRIMARY，当创建主键约束时，系统默认会在所在的列和列组合上建立对应的唯一索引。

联合主键：
联合主键就是用2个或2个以上的字段组成主键。用这个主键包含的字段作为主键，这个组合在数据表中是唯一，且加了主键索引。
可以这么理解，比如，你的订单表里有很多字段，一般情况只要有个订单号bill_no做主键就可以了，
但是，现在要求可能会有补充订单，使用相同的订单号，那么这时单独使用订单号就不可以了，因为会有重复。
那么你可以再使用个订单序列号bill_seq来作为区别。把bill_no和bill_seq设成联合主键。
即使bill_no相同，bill_seq不同也是可以的

```
// 这是列级模式
CREATE TABLE users( id INT AUTO INCREAMENT PRIMARY KEY , name VARCHAR(20))

// 这是表级模式
CREATE TABLE users( id INT NOT NULL  AUTO INCREAMENT,
     name VARCHAR(20),
   CONSTRAINT users_id_pk PRIMARY KEY(id) 
)

// 添加主键约束（将UserId作为主键）
ALERT TABLE users
ADD CONSTRAINT pk_id PRIMARY KEY(id)

```

### FOREIGN KEY 约束

外关键字约束定义了表之间的关系。
当一个表中的一个列或多个列的组合和其它表中的主关键字定义相同时，就可以将这些列或列的组合定义为外关键字，
并设定它适合哪个表中哪些列相关联

当在定义主关键字约束的表中更新列值时，其它表中有与之相关联的外关键字约束的表中的外关键字列也将被相应地做相同的更新

  

```
//添加外键约束 (主表UserInfo和从表UserOrder建立关系，关联字段UserId)
ALERT TABLE UserOrder
ADD CONSTRAINT fk_id_id  FOREIGN KEY(id) REFERENCES UserInfo(id)

```


```
"Persons" 表：
P_Id	LastName	FirstName	Address	City
1	Hansen	Ola	Timoteivn 10	Sandnes
2	Svendson	Tove	Borgvn 23	Sandnes
3	Pettersen	Kari	Storgt 20	Stavanger

"Orders" 表：
O_Id	OrderNo	P_Id
1	77895	3
2	44678	3
3	22456	2
4	24562	1

```
- "Orders" 表中的 "P_Id" 列指向 "Persons" 表中的 "P_Id" 列
- "Persons" 表中的 "P_Id" 列是 "Persons" 表中的 PRIMARY KEY。
- "Orders" 表中的 "P_Id" 列是 "Orders" 表中的 FOREIGN KEY。

FOREIGN KEY 约束用于预防破坏表之间连接的行为。
FOREIGN KEY 约束也能防止非法数据插入外键列，因为它必须是它指向的那个表中的值之一。








- SQL语言 大小写不敏感。
- SQL可以写在一行或者多行。
- 关键字不能被缩写也不能分行。
- 各字句一般要分行写。
