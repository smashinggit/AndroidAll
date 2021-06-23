[toc]

# Room
[https://www.jianshu.com/p/3e358eb9ac43](https://www.jianshu.com/p/3e358eb9ac43)
[https://blog.csdn.net/gtsong/article/details/103782437?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.control&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.control](https://blog.csdn.net/gtsong/article/details/103782437?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.control&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.control)
## 为什么要使用Room

在 Android 中直接使用 SQLite 数据库存在多个缺点：
- 必须编写大量的样板代码；
- 必须为编写的每一个查询实现对象映射；
- 很难实施数据库迁移；
- 很难测试数据库；
- 如果不小心，很容易在主线程上执行长时间运行的操作。

为了解决这些问题，Google 创建了 Room，**一个在 SQLite 上提供抽象层的持久存储库**。

Room 是一个稳健的、基于对象关系映射(ORM)模型的、数据库框架。Room 提供了一套基于 SQLite 的抽象层，
在完全实现 SQLite 全部功能的同时实现更强大的数据库访问。


针对 SQLite数据库的上述缺点，Room 框架具有如下特点：

- 由于使用了动态代理，减少了样板代码；
- 在 Room 框架中使用了编译时注解，在编译过程中就完成了对 SQL 的语法检验；
- 相对方便的数据库迁移；
- 方便的可测试性；
- 保持数据库的操作远离了主线程。
- 此外 Room 还支持 RxJava2 和 LiveData。


## 通过一个案例，介绍如何使用 Room

这个使用 Room 框架的案例要经历以下几个过程：
- 设计数据库的 ER 图(非必须)；
- 添加对 Room 的依赖；
- 创建数据库实体 Entity；
- 创建数据库访问的 DAO；
- 创建数据库 Database；
- 封装数据库与业务逻辑交互的 Repository；
- 创建数据库中使用到的类型转换器；
- 考虑数据库迁移；
- 数据库的测试


## 1. 数据库 ER 图

设有一数据库，包括四个表：学生表（Student）、课程表（Course）、成绩表（Score）以及教师信息表（Teacher

## 2. 添加 Room 的依赖

```
    apply plugin: 'kotlin-kapt'
     ...

    def room_version = "2.2.5"
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"  // optional - Kotlin Extensions and Coroutines support for Room
    kapt "androidx.room:room-compiler:$room_version"
```

## 3. 创建实体
```
@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    var no: Int,
    var name: String,
    var age: Int,
    var sex: String,
    var birthday: String,
    var classes: String,
    @Embedded
    var address: Address
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = false)
    var no: Int,
    var name: String,
    var age: Int,
    var sex: String,
    var birthday: String,
    var rof: String,  //职称
    var part: String  //部门
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    var no: Int,

    var name: String,  //课程名称

    @ForeignKey(
        entity = Teacher::class,
        parentColumns = ["no"],
        childColumns = ["teacherNo"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
    var teacherNo: Int?, //教师编号(外键)
)

@Entity(tableName = "scores")
data class Score(
    @ForeignKey(
        entity = Student::class,
        childColumns = ["studentNo"],
        parentColumns = ["no"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var studentNo: String, //学号(外键)

    @ForeignKey(
        entity = Course::class,
        childColumns = ["courseNo"],
        parentColumns = ["no"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var courseNo: String,  //课程号(外键)
    
    var degree: Int        //分数
)

```

- @Entity 
声明所标记的类是一个数据表，@Entity 包括的参数有：tableName(表名），indices（表的索引），
primaryKeys（主键），foreignKeys（外键），ignoredColumns（忽略实体中的属性，不作为数据表中的字段），
inheritSuperIndices（是否集成父类的索引，默认 false）

- @ColumnInfo
用来声明数据库中的字段名

- @PrimaryKey
被修饰的属性作为数据表的主键，@PrimaryKey 包含一个参数：autoGenerate(是否允许自动创建，默认false)

- @Embedded	
用来修饰嵌套字段，被修饰的属性中的所有字段都会存在数据表中

- @ForeignKey	
在另一个实体上声明外键

- @ForeignKey.Action	
可以在onDelete（）和onUpdate（）中使用的值的常量定义。
包括：NO_ACTION, RESTRICT, SET_NULL, SET_DEFAULT, CASCADE

- @Ignore	
忽略Room的处理逻辑中标记的元素

- @Index	
声明实体的索引

- @Transaction	
将Dao类中的方法标记为事务方法

- @TypeConverter	
将方法标记为类型转换器

- @TypeConverters	
指定Room可以使用的其他类型转换器




关于@Embedded的进一步说明，我们的 Student 实体中有被@Embedded修饰的属性 Address ，
```
class Address(
    var province: String, //省
    var city: String,   //市
    var country: String //县
)
```


通常情况下将这些数据存储在数据库中有两种方式：

1. 新建一张 Address 表，用 Student 表的 id 作为外键，与 Student 表进行一一关联；
2. 将 Address 实体中的字段存储在 Student 表中，这种方式减少了数据表的创建，
   也减少了联合查询的复杂程度。
   
如果直接将这些字段打散在 Student 表中，显得不够面向对象，这时就可以使用@Embedded注解，
即显得**面向对象**，又**不用再创建数据表**，非常的优雅。


   
## 4. 创建 Dao
我们开始创建数据访问对象层 Dao，在这里我们需要定义一些对数据库增删改查的方法。

```
@Dao
interface StudentDao {

    // INSERT queries can return {@code void} or {@code long}.
    // If it is a {@code long}, the value is the SQLite rowId of the row inserted by this query.
    // Note that queries which insert multiple rows cannot return more than one rowId,
    // so avoid such statements if returning {@code long}
    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: Student): Long

    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertMulti(vararg student: Student)  //注意：当插入多条数据时，不能有返回值


    //UPDATE or DELETE queries can return {@code void} or {@code int}
    //If it is an {@code int}, the value is the number of rows affected by this query
    @Delete(entity = Student::class)
    fun delete(vararg student: Student): Int //Delete 和 UPDATE，可以处理多条数据，返回值代表影响了多少行

    //UPDATE or DELETE queries can return {@code void} or {@code int}
    //If it is an {@code int}, the value is the number of rows affected by this query
    @Update(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg student: Student): Int //Delete 和 UPDATE，可以处理多条数据，返回值代表影响了多少行


    //For single result queries, the return type can be any data object (also known as POJOs)
    //For queries that return multiple values, you can use {@link java.util.List} or {@code Array}
    //Any query result can be wrapped in a {@link androidx.lifecycle.LiveData LiveData}
    @Query("SELECT * FROM students")
    fun queryAll(): List<Student>

    @Query("SELECT * FROM students WHERE `no` = :no")
    fun queryByNo(no: Int): Student

    @Query("SELECT * FROM students WHERE age IN(:ages)")
    fun queryByAge(ages: IntArray): List<Student>

    @Query("SELECT * FROM students WHERE age BETWEEN :startAge AND :endAge")
    fun queryBetweenAge(startAge: Int, endAge: Int): List<Student>

    @Query("SELECT * FROM students WHERE name LIKE  :nameKeyWords")
    fun queryWithName(nameKeyWords: String): List<Student>
}
```


创建一个使用@Dao 注释的接口。并在其上声明使用该数据库所需的所有函数，并编写相应的 SQL 查询语句，
Room 将为你实现这些函数，并在单次事务中运行它们，
Room 支持的查询语句包括：插入、更新、删除和查询。查询语句会在编译时被校验，这意味着，如果你编写了一个无
效的应用，你会立刻发现错误   

对数据表的增删改查方法对应的注解分别是@Insert、@Delete、@Update和 @Query，其中:

- @Insert、@Update 可设置参数onConflict，处理数据冲突时采取的策略，
可以设置包括：REPLACE, ABORT,IGNORE三种策略，
REPLACE用新的数据行替换旧的数据行；
ABORT直接回滚冲突的事务；
IGNORE保持现有数据行

- @Query 中声明我们要查询的 SQL 语句，使用@Query不仅成完成查，还能进行增删改的操作。
   
**这些查询都是同步的**，也就是说，这些查询将在你触发查询的同一个线程上运行。
如果这是主线程，你的应用将崩溃，并显示 IllegalStateException，
因此，请使用在 Android 中推荐的线程处理方法，并确保其远离主线程。

当使用 LiveData 或 RxJava 时，Room 也支持异步查询，更重要的是，返回 LiveData 或 Flowable 
的查询是可观测的查询。也就是说，每当表格中的数据被更新时，就会收到通知。


## 5. 创建数据库
将实体和 DAO整合在一起的类是 RoomDatabase，先创建一个扩展 RoomDatabase 的抽象类，
对它进行注释，声明实体和相应的 DAO
```
@TypeConverters(CalendarTypeConverter::class)
@Database(entities = [Student::class, Teacher::class], version = VERSION, exportSchema = true)
abstract class StudentDataBase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao

    companion object {
        const val VERSION = 1  //初始版本，里面只有一个学生表

        @Volatile
        var INSTANCE: StudentDataBase? = null

        fun getInstance(context: Context): StudentDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, StudentDataBase::class.java, "studentDB.db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.e("Room", "onCreate")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.e("Room", "onOpen")
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            Log.e("Room", "onDestructiveMigration")
                        }
                    })
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
```

数据库的创建是一件非常消耗资源的工作，所以我们将数据库设计为单例，避免创建多个数据库对象。
另外对数据库的操作都不能放在 UI 线程中完成，否则会出现异常

在创建数据库时需要完成以下几件工作：
- 设计成单例模式，避免创建多个数据库对象消耗资源；
- 创建的数据库类需要继承 RoomDatabase，数据库类声明为抽象类；
- 需要提供方法来获取数据访问对象层(Dao)对象，方法声明为抽象方法；
- 数据库类需要使用@Database注解，

@Database包括几个参数：
entities（数据库包含的数据表，@entities注解修饰的实体），
default（数据库包含的视图），
version（数据库的版本号），
exportSchema（可以理解为开关，如果开关为 true，Room 框架会通过注解处理器将一些数据库相关的schema输出到指定的目录中，默认 true）


## 6. 封装 Repository
```
class StudentRepository(private val studentDao: StudentDao) {


    fun insert(student: Student): Long {
        return studentDao.insert(student)
    }

    fun insertMulti(vararg student: Student) {
        studentDao.insertMulti(*student)
    }


    fun delete(vararg student: Student): Int {
        return studentDao.delete(*student)
    }


    fun update(vararg student: Student): Int {
        return studentDao.update(*student)
    }


    fun queryAll(): List<Student> {
        return studentDao.queryAll()
    }

    fun queryByNo(no: Int): Student {
        return studentDao.queryByNo(no)
    }

    fun queryByAge(ages: IntArray): List<Student> {
        return studentDao.queryByAge(ages)
    }

    fun queryBetweenAge(startAge: Int, endAge: Int): List<Student> {
        return studentDao.queryBetweenAge(startAge, endAge)
    }

    fun queryWithName(nameKeyWords: String): List<Student> {
        return studentDao.queryWithName(nameKeyWords)
    }
}
```


## 7. 类型转换器@TypeConverter

在我们的球员实体PlayerModel中，球员的出生日期 dob类型是 Calendar 类型，
但是 SQLite 只支持 5 中类型，分别是NULL、INTEGER、REAL、TEXT和BLOB，这时可以用到@TypeConverters注解
```
class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
```

在数据库中添加该注解:
```
@TypeConverters(Converters::class)
```


## 8. 数据库迁移 Migration
使用Room的时候，
**如果你改变了数据库的schema但是没有更新version，app将会crash**
**而如果你更新了version但是没有提供迁移，数据库的表就会drop掉，用户将丢失数据**


### 数据库迁移背后的原理

SQLite数据库处理schema的改变是在database version的帮助下完成的。
更准确的说，每当你添加，删除，或者修改表导致schema变化的时候，你都必须增加数据库的版本号并更新
SQLiteOpenHelper.onUpgrade方法
当你从旧版本到新版本的时候，是它告诉SQLite该做什么

**如果你增加了数据库版本但是没有提供迁移，那么你的app可能会崩溃，数据可能会丢失**


如果要进行数据库迁移操作，需要在 Database 类中执行以下操作：

首先更新你的数据库版本。
```
@Database(entities = {User.class}, version = 2)

```

其次，实现一个 Migration 类，定义如何处理从旧版本到新版本的迁移：
```
static final Migration MIGRATION_1_2 = new Migration(1, 2)

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE users" +
                "ADD COLUMN address STRING");
    }
```

第三，将此 Migration 类添加为 Database 构建器的一个参数，
```
Room.databaseBuilder(context.getApplicationContext(),
    MyDatabase.class,"sample.db").addMigrations(MIGRATION_1_2).build();
```

当触发迁移后，Room 将为你验证 Schema，以确保迁移已正确完成。


### 多版本迁移

**Room可以处理大于1的版本增量：我们可以一次性定义一个从1到4的migration，让迁移的速度更快**

Migration有两个参数，startVersion和endVersion。
startVersion表示当前版本（手机上安装的版本），endVersion表示将要升级到的版本。

如果你的手机中的应用程序数据库的版本为1，那么下方Migration会将你的数据库版本从1升级到2
```
static final Migration MIGRATION_1_2 = new Migration(1, 2)
{
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database)
    {
        //执行升级相关操作
    }
};
```

以此类推，如果你的数据库需要从2升级到3，则需要写这样一个Migration。
```
private static Migration MIGRATION_2_3 = new Migration(2, 3)
{
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database)
    {
        //执行升级相关操作
    }
};

```

如果用户手机上安装的应用程序数据库版本为1，而当前要安装的应用程序数据库版本为3，这种情况该怎么办呢？
这种情况下，
**Room会先判断当前有没有从1->3的Migration升级方案，
如果有，就直接执行从1->3的升级方案，
如果没有，那么Room会按照顺序先后执行Migration(1, 2)->Migration(2, 3)以完成升级**

写好Migration之后，我们还需要通过addMigrations()方法，将升级方案添加到Room。

```
Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, DATABASE_NAME)
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
    .build();

    static final Migration MIGRATION_1_3 = new Migration(1, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))");
            
            // Copy the data
            database.execSQL(
                    "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users");
    // Remove the old table
            database.execSQL("DROP TABLE users");
    // Change the table name to the correct one
            database.execSQL("ALTER TABLE users_new RENAME TO users");
        }
    };

```

然后，我们只需把它添加到migration列表中：
```
    database = Room.databaseBuilder(context.getApplicationContext(),
            UsersDatabase.class, "Sample.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
            .build();
```


### 异常处理

假设此时我们升级系统版本为4，却没有为此写相应的Migration，则会出现一个IllegalStateException异常。
这是因为Room在升级过程中没有匹配到相应的Migration。

为了防止出现升级失败导致应用程序Crash的情况，我们可以在创建数据库时加入fallbackToDestructiveMigration()方法。
该方法能够在**出现升级异常时，重新创建数据库表**。
虽然应用程序不会Crash，但**由于数据表被重新创建，所有的数据也将会丢失**
```
Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, DATABASE_NAME)
    .fallbackToDestructiveMigration()
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
    .build();
```



#  Schema文件

在没有Room之前，如果想要验证数据库的修改是否符合我们的预期，我们可能需要找到这个数据库文件，
然后使用第三方Sqlite查看工具，对其进行查看和验证。
另外，我们希望能够查看到数据库的历次升级情况，似乎也只能通过代码版本控制工具来查看源代码。
这无疑是非常耗时耗力的。

为此，Room提供了一项功能，在每次数据库的升级过程中，它都会为你导出一个Schema文件，
这是一个json文件，里面包含了数据库的所有基本信息。
有了这些文件，开发者们就能知道数据库的历史变更情况，极大地方便了开发者们排查问题。
Schema文件是默认导出的，你只需要指定它导出的位置即可。

```
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]//指定数据库schema导出的位置
            }
        }
    }
}
```

# 销毁与重建策略
在Sqlite中修改表结构会比较麻烦。

比如我们希望将Student表中的age字段类型从INTEGER改为TEXT。

面对此类需求，最好的方式就是采用“销毁与重建策略”，该策略大致分为以下几个步骤：

1.创建一张符合我们要求的临时表temp_Student
2.将数据从旧表Student拷贝至临时表temp_Student
3.删除旧表Student
4.将临时表temp_Student重命名为Student

```
static final Migration MIGRATION_3_4 = new Migration(3, 4)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("CREATE TABLE temp_Student (" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "name TEXT," +
                    "age TEXT)");
            database.execSQL("INSERT INTO temp_Student (id, name, age) " +
                    "SELECT id, name, age FROM Student");
            database.execSQL("DROP TABLE Student");
            database.execSQL("ALTER TABLE temp_Student RENAME TO Student");
        }
    };
```

通过查看schema升级文件，我们可以看到，age字段已经被修改为TEXT类型了，说明这次的数据库修改升级是成功的。


# Room 的组成及原理




































Room由三个重要的组件组成：Database、Entity、DAO。

- Database : 包含数据库持有者，并作为与应用持久关联数据的底层连接的主要访问点

Database对应的类必须满足下面几个条件:
1. 必须是abstract类而且的extends RoomDatabase。
2. 必须在类头的注释中包含与数据库关联的实体列表(Entity对应的类)
3. 包含一个具有0个参数的抽象方法，并返回用@Dao注解的类

在运行时，你可以通过Room.databaseBuilder() 或者 Room.inMemoryDatabaseBuilder()获取Database实例。


- Entity：代表数据库中某个表的实体类。

- DAO：包含用于访问数据库的方法。




# Entity(实体)

每个Entity代表数据库中某个表的实体类。默认情况下Room会把Entity里面所有的字段对应到表上的每一列。
如果需要制定某个字段不作为表中的一列需要添加@Ignore注解

Entity的实体类都需要添加@Entity注解。而且Entity类中需要映射到表中的字段需要保证外部能访问到这些字段
- 要么把字段什么为public
- 要么实现字段的getter和setter方法


```
@Entity(tableName = "users")
class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    var age: String,

    var sex: Int,

    @Ignore
    var pic: Bitmap? = null
) 

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"]
    )]
)
class Book(
    @PrimaryKey
    var bookId: Int,
    var title: String,
    var userId: Int
)

```

上述代码通过foreignKeys之后Book表中的userId来源于User表中的id。

@ForeignKey属性介绍：
- entity：parent实体类(引用外键的表的实体)。
- parentColumns：parent外键列(要引用的外键列)。
- childColumns：child外键列(要关联的列)。
- onDelete：默认NO_ACTION，当parent里面有删除操作的时候，child表可以做的Action动作有：
  1. NO_ACTION：当parent中的key有变化的时候child不做任何动作。
  2. RESTRICT：当parent中的key有依赖的时候禁止对parent做动作，做动作就会报错。
  3. SET_NULL：当paren中的key有变化的时候child中依赖的key会设置为NULL。
  4. SET_DEFAULT：当parent中的key有变化的时候child中依赖的key会设置为默认值。
  5. CASCADE：当parent中的key有变化的时候child中依赖的key会跟着变化。


- onUpdate：默认NO_ACTION，当parent里面有更新操作的时候，child表需要做的动作。Action动作方式和onDelete是一样的

- deferred：默认值false，在事务完成之前，是否应该推迟外键约束。这个怎么理解，当我们启动一个事务插入很多
  数据的时候，事务还没完成之前。当parent引起key变化的时候。可以设置deferred为ture。让key立即改变
  
  
## 创建嵌套对象

有些情况下，你会需要将多个对象组合成一个对象。对象和对象之间是有嵌套关系的。
Room中你就可以使用@Embedded注解来表示嵌入。然后，您可以像查看其他单个列一样查询嵌入字段。

比如有一个这样的例子，User表包含的列有：id, firstName, street, state, city, and post_code。
这个时候我们的嵌套关系可以用如下代码来表示

```
public class Address {
    public String street;
    public String state;
    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}

@Entity
public class User {
    @PrimaryKey
    public int id;

    public String firstName;

    @Embedded
    public Address address;
}
```





# DAO(数据访问对象)

这个组件代表了作为DAO的类或者接口。
DAO是Room的主要组件，负责定义访问数据库的方法。Room使用过程中一般使用抽象DAO类来定义数据库的CRUD操作。
DAO可以是一个接口也可以是一个抽象类。如果它是一个抽象类，它可以有一个构造函数，它将RoomDatabase作为其唯
一参数。

DAO里面所有的操作都是依赖方法来实现的。

### Insert(插入)

当DAO里面的某个方法添加了@Insert注解。Room会生成一个实现，将所有参数插入到数据库中的一个单个事务。

@Insert注解可以设置一个属性：
onConflict：默认值是OnConflictStrategy.ABORT，表示当插入有冲突的时候的处理策

       OnConflictStrategy封装了Room解决冲突的相关策略：
       1. OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务。
       2. OnConflictStrategy.ROLLBACK：冲突策略是回滚事务。
       3. OnConflictStrategy.ABORT：冲突策略是终止事务。
       4. OnConflictStrategy.FAIL：冲突策略是事务失败。
       5. OnConflictStrategy.IGNORE：冲突策略是忽略冲突。

```
@Dao
interface UserDao {
    @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg user: User)
}
```   
INSERT queries can return {@code void} or {@code long}
当@Insert注解的方法只有一个参数的时候，这个方法也可以返回一个long，
当@Insert注解的方法有多个参数的时候则可以返回long[]或者 List<Long>
**long都是表示插入的rowId**

### Delete(删除)

当DAO里面的某个方法添加了@Delete注解。
Room会把对应的参数信息指定的行删除掉(通过参数里面的primary key找到要删除的行)

```
@Dao
interface UserDao {
  @Delete(entity = User::class)
    fun deleteUsers(vararg user: User):Int  // 返回值表示删除了多少行
}
```
@Delete对应的方法也是可以设置int返回值来表示删除了多少行。



###  Update(更新)

当DAO里面的某个方法添加了@Update注解。Room会把对应的参数信息更新到数据库里面去(会根据参数里面的primary key做更新操作)。

@Update和@Insert一样也是可以设置onConflict来表明冲突的时候的解决办法。

```
@Dao
interface UserDao {
    @Update(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateUsers(vararg user: User): Int // 返回值表示更新了多少行
}
```

@Update注解的方法也可以返回int变量。表示更新了多少行。


###  Query(查询)

@Query注解是DAO类中使用的主要注释。它允许您对数据库执行读/写操作。
@Query在编译的时候会验证准确性，所以如果查询出现问题在编译的时候就会报错。

Room还会验证查询的返回值，如果返回对象中的字段名称与查询响应中的相应列名称不匹配的时候，
Room会通过以下两种方式之一提醒您：
- 如果只有一些字段名称匹配，它会发出警告。
- 如果没有字段名称匹配，它会发生错误。

@Query注解value参数：查询语句，这也是我们查询操作最关键的部分。


```
@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    fun queryUserById(id: Int): User

    @Query("SELECT * FROM users WHERE age IN(:ages)")
    fun queryUserByAgeRange(ages: IntArray): List<User>

    @Query("SELECT * FROM users WHERE age BETWEEN :minAge AND :maxAge")
    fun queryUserBetweenAges(minAge: Int, maxAge: Int): List<User>

    @Query("SELECT * FROM users WHERE age LIKE :keyWords")
    fun queryUserWithName(keyWords: String)
}
```

返回结果可以是数组，也可以是List。

### 查询返回列的子集

有的时候可能指向返回某些特定的列信息。
```
@Dao
interface UserDao {
   @Query("SELECT name,age FROM USERS")
    fun queryNameAndAge(): List<UserTuple>  //注意：这里返回值是一个UserTuple数组而不是 User
}
```

### 多表查询

```


```



### 支持LiveData

```
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun queryAllToLiveData(): LiveData<List<User>>
}
```





# Database(数据库)

@Database注解可以用来创建数据库的持有者。
该注解定义了实体列表，该类的内容定义了数据库中的DAO列表。这也是访问底层连接的主要入口点。
注解类应该是抽象的并且扩展自RoomDatabase。

Database对应的对象(RoomDatabase)必须添加@Database注解，
@Database包含的属性：

- entities：数据库相关的所有Entity实体类，他们会转化成数据库里面的表
- version：数据库版本。
- exportSchema：默认true，也是建议传true，这样可以把Schema导出到一个文件夹里面。同时建议把这个文件夹上次到VCS。

在运行时，你可以通过调用Room.databaseBuilder()或者Room.inMemoryDatabaseBuilder()获取实例。
因为每次创建Database实例都会产生比较大的开销，所以应该将Database设计成单例的，或者直接放在Application中创建

两种方式获取Database对象的区别:

- Room.databaseBuilder()：生成Database对象，并且创建一个存在文件系统中的数据库
- Room.inMemoryDatabaseBuilder()：生成Database对象并且创建一个存在内存中的数据库。
  当应用退出的时候(应用进程关闭)数据库也消失。

```
@Database(version = 1, entities = [User::class, Book::class], exportSchema = true)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}
```


在 Application 中初始化

```
class App : Application() {
    private lateinit var mDataBase: MyDataBase

    override fun onCreate() {
        super.onCreate()

        mDataBase = Room.databaseBuilder(this, MyDataBase::class.java, "userInfoDB")
            .build()
    }
}

```


# 数据库升级 Migration

大部分情况下设计的数据库在版本的迭代过程中经常是会有变化的。比如突然某个表需要新加一个字段，需要新增一个表。
这个时候我们又不想失去之前的数据。Room里面以Migration类的形式提供可一个简化SQLite迁移的抽象层。
Migration提供了从一个版本到另一个版本迁移的时候应该执行的操作

当数据库里面表有变化的时候(不管你是新增了表，还是改变了某个表)有如下几个场景：

- 如果database的版本号不变。app操作数据库表的时候会直接crash掉。(错误的做法)
- 如果增加database的版本号。但是不提供Migration。app操作数据库表的时候会直接crash掉。（错误的做法）

- 如果增加database的版本号。同时启用fallbackToDestructiveMigration。这个时候之前的数据会被清空掉。
  如下fallbackToDestructiveMigration()设置。(不推荐的做法)

```
 mAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android_room_dev.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()  //设置迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
                    .build();

```

- 增加database的版本号，同时提供Migration。这要是Room数据迁移的重点。(最正确的做法)

所以在数据库有变化的时候，我们任何时候都应该尽量提供Migrating。Migrating让我们可以自己去处理数据库从
某个版本过渡到另一个版本的逻辑

我们用一个简单的实例来说明。
有这么个情况，数据库开始设计的时候我们就一个user表(数据库版本 1)，
第一次变化来了我们需要给user表增加一个age的列(数据库版本 2)，
过了一段时间又有变化了我们需要新增加一个book表。三个过程版本1->2->3。

数据库版本为1的时候的代码:

```
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
}

public class AppApplication extends Application {

    private AppDatabase mAppDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android_room_dev.db")
                           .allowMainThreadQueries()
                           .build();
    }

    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }
}

```

数据库版本为2的时候的代码，User增加了age列:

```
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private long    uid;
    private String  name;
    private String  address;
    private String  phone;
    private Integer age;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

@Database(entities = {User.class}, version = 2)  // 这里版本从1升为2
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

public class AppApplication extends Application {

    private AppDatabase mAppDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android_room_dev.db")
                           .allowMainThreadQueries()
                           .addMigrations(MIGRATION_1_2)  // 添加Migration
                           .build();
    }

    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }

    /**
     * 数据库版本 1->2 user表格新增了age列
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user " + " ADD COLUMN age INTEGER");
        }
    };
}

```



数据库版本为3的时候的代码，新增了一个Book表

```
@Entity
public class Book {

    @PrimaryKey(autoGenerate = true)
    private Long   uid;
    private String name;
    private Date   time;
    private Long   userId;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}

@Database(entities = {User.class, Book.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract BookDao bookDao();

}

public class AppApplication extends Application {

    private AppDatabase mAppDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android_room_dev.db")
                           .allowMainThreadQueries()
                           .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                           .build();
    }

    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }

    /**
     * 数据库版本 1->2 user表格新增了age列
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE User ADD COLUMN age integer");
        }
    };

    /**
     * 数据库版本 2->3 新增book表格
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `book` (`uid` INTEGER PRIMARY KEY autoincrement, `name` TEXT , `userId` INTEGER, 'time' INTEGER)");
        }
    };
}
```

# 五、数据库信息的导出
Room也允许你会将你数据库的表信息导出为一个json文件。你应该在版本控制系统中保存该文件，
该文件代表了你的数据库表历史记录，这样允许Room创建旧版本的数据库用于测试。
只需要在build.gradle文件中添加如下配置。编译的时候就会导出json文件

```
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["room.schemaLocation":
                             "$projectDir/schemas".toString()]
            }
        }
    }
    // 用于测试
    sourceSets {
        androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
    }
}


```










