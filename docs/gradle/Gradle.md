[toc]


#

Everything in Gradle sits on top of two basic concepts: projects and tasks.

Every Gradle build is made up of one or more projects
Each project is made up of one or more tasks

**The script API**
When Gradle executes a Groovy build script (.gradle), it compiles the script into a class which
implements Script. This means that all of the properties and methods declared by the Script interface
are available in your script

When Gradle executes a Kotlin build script (.gradle.kts), it compiles the script into a subclass of
KotlinBuildScript. This means that all of the visible properties and functions declared by the
KotlinBuildScript type are available in your script.




## Project
有一个Java接口 Project
This interface is the main API you use to interact with Gradle from your build file

**Lifecycle**
During build initialisation, Gradle assembles a <code>Project</code> object for each project
which is to participate in the build

**Task**
A project is essentially a collection of {@link Task} objects. Each task performs some basic piece of work

**Dependencies**
A project generally has a number of dependencies it needs in order to do its work

**Multi-project Builds**
Projects are arranged into a hierarchy of projects. A project has a name, and a fully qualified path which
uniquely identifies it in the hierarchy

**Plugins**
Plugins can be used to modularise and reuse project configuration.

**Dynamic Project Properties**
you can use any of the methods and properties on the <code>Project</code> interface directly in your script.

For Example：
 defaultTasks('some-task')  // Delegates to Project.defaultTasks()
 reportsDir = file('reports') // Delegates to Project.file() and the Java Plugin

You can also access the <code>Project</code> instance using the <code>project</code> property
For Example：
you could use <code>project.name</code> rather than <code>name</code> to access the project's name.


Project has 5 property 'scopes', which it searches for properties. You can access these properties by name in
your build file, or by calling the project's {@link #property(String)} method.

The scopes are:
1. The <code>Project</code> object itself.

2. The <em>extra</em> properties of the project.  Each project maintains a map of extra properties, which
   can contain any arbitrary name -&gt; value pair

3. The <em>extensions</em> added to the project by the plugins.
   Each extension is available as a read-only property with the same name as the extension.</li>

4. The <em>convention</em> properties added to the project by the plugins. A plugin can add properties and methods
   to a project through the project's {@link Convention} object

5. The tasks of the project.  A task is accessible by using its name as a property name.
   For example, a task called <code>compile</code> is accessible as the <code>compile</code> property


**Extra Properties**
All extra properties must be defined through the &quot;ext&quot; namespace

## Task
A <code>Task</code> represents a single atomic piece of work for a build
Each task belongs to a {@link Project}

create a task :
 task myTask
 task myTask { configure closure }
 task myTask(type: SomeType)
 task myTask(type: SomeType) { configure closure }


## Copy、AbstractCopyTask、CopySpec
AbstractCopyTask： AbstractCopyTask is the base class for all copy tasks
Copy： Copies files into a destination directory 。This task can also rename and filter files as it copies
CopySpec：A set of specifications for copying files.

Examples:
 task copyDocs(type: Copy) {
      from 'src/main/doc'
      into 'build/target/doc'
  }


  //for including in the copy task
  def dataContent = copySpec {  //这里是一个copySpec
      from 'src/data'
      include '*.data'
  }

  task initConfig(type: Copy) {
      from('src/main/config') {
          include '**&#47;*.properties'
          include '**&#47;*.xml'
          filter(ReplaceTokens, tokens: [version: '2.3.1'])
      }
      from('src/main/config') {
          exclude '**&#47;*.properties', '**&#47;*.xml'
      }
      from('src/main/languages') {
          rename 'EN_US_(.*)', '$1'
      }
      into 'build/target/config'
      exclude '**&#47;*.bak'

      includeEmptyDirs = false

      with dataContent  //with 方法的意思是 Adds the given specs as a child of this spec.
  }





## 定义Task

1. Defining tasks using strings for task names
```
task("hello"){
 doLast {
        println "hello"
    }
}

task("copyTask",type:Copy){
  from(file('srcDir'))
  into(buildDir)
}
```

2. Defining tasks using the tasks container
```
tasks.create('hello') {
    doLast {
        println "hello"
    }
}

tasks.create('copy', Copy) {
    from(file('srcDir'))
    into(buildDir)
}
```

## 相关API和语法
**声明变量**
1. Using local variables
def test = "test"

2. Using extra properties
Extra properties can be added, read and set via the owning object’s ext property.

ext {
    springVersion = "3.1.0.RELEASE"
    emailNotification = "build@master.org"
}

**Optional parentheses(圆括号) on method calls**

test.systemProperty 'some.prop', 'value'
test.systemProperty('some.prop', 'value')

**List and map literals**
Groovy provides some shortcuts for defining List and Map instances

// List literal
test.includes = ['org/gradle/api/**', 'org/gradle/internal/**']

List<String> list = new ArrayList<String>()
list.add('org/gradle/api/**')
list.add('org/gradle/internal/**')
test.includes = list

// Map literal.
Map<String, String> map = [key1:'value1', key2: 'value2']

// Groovy will coerce named arguments
// into a single map argument  //实际上，它会把参数 'java' 转换成一个 map
apply plugin: 'java'

For instance, the “apply” method (where you typically apply plugins) actually takes a map
parameter


**Copying a single file**
task copyReport(type: Copy) {
    from file("$buildDir/reports/my-report.pdf")
    into file("$buildDir/toArchive")
}

task copyReport2(type: Copy) {
    from "$buildDir/reports/my-report.pdf"
    into "$buildDir/toArchive"
}


**Copying multiple files**
task copyReportsForArchiving(type: Copy) {
    from "$buildDir/reports/my-report.pdf", "src/docs/manual.pdf"
    into "$buildDir/toArchive"
}

task copyPdfReportsForArchiving(type: Copy) {
    from "$buildDir/reports"
    include "*.pdf"                //筛选
    into "$buildDir/toArchive"
}

You can include files in subdirectories by using an Ant-style glob pattern (**/*),
as done in this updated example:

task copyAllPdfReportsForArchiving(type: Copy) {
    from "$buildDir/reports"
    include "**/*.pdf"       //这样写会包含子目录  注意：这样拷贝会保留子目录的文件结构，如果只想要文件不保留结构，请看下面fileTree的部分
    into "$buildDir/toArchive"
}

**Copying an entire directory**
//这样会把 reports 目录下的所有文件拷贝到 toArchive 目录中，但是包含 reports 本身
task copyReportsDirForArchiving(type: Copy) {
    from "$buildDir/reports"
    into "$buildDir/toArchive"
}

//这样会把 reports 目录下的所有文件拷贝到 toArchive 目录中，并且包含 reports 本身
task copyReportsDirForArchiving2(type: Copy) {
    from("$buildDir") {
        include "reports/**"
    }
    into "$buildDir/toArchive"
}

**Archiving a directory as a ZIP**

//把 $buildDir/tmp/kotlin-classes/debug 打包成 my-distribution2.zip
task packageDistribution2(type: Zip) {
    archiveFileName = "my-distribution2.zip"
    destinationDirectory = file("$buildDir/zip")

    from "$buildDir/tmp/kotlin-classes/debug"
}

// 注意：  into "classes" 的意思是把 $buildDir/tmp/kotlin-classes/debug 中放到 classes 文件夹下
// 所以这里打包出来的zip 根目录是 ckasses
task packageDistribution2(type: Zip) {
    archiveFileName = "my-distribution2.zip"
    destinationDirectory = file("$buildDir/zip")

    from "$buildDir/tmp/kotlin-classes/debug"
    into "classes"
}


**Unpacking archives**
The two functions of interest are Project.zipTree(java.lang.Object) and
Project.tarTree(java.lang.Object), which produce a FileTree from a corresponding archive file.
That file tree can then be used in a from() specification


**Unpacking a ZIP file**
task unpackFiles(type: Copy) {
    from zipTree("src/resources/thirdPartyResources.zip")
    into "$buildDir/resources"
}


//jar的名字结构： [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]

**Make Jar**
task makeJar(type: Jar) {

    setArchivesBaseName sdkName
    destinationDir file(sdkDir)

//    from("src/main/java")
    from(zipTree("$buildDir/intermediates/aar_main_jar/release/classes.jar"))

    //第三方的jar包
    from(zipTree("libs/test.jar"))
}

**Make Uber Jar**
//jar的名字结构： [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
//注意：compile 只能打包在上面的 dependencies 配置中使用 compile 的库，但是compile又报过时
//目前已有的解决方法，但是未能找到特别优雅的方案
// https://stackoverflow.com/questions/47910578/not-able-to-copy-configurations-dependencies-after-upgrading-gradle-plugin-for-a
task makeUberJar(type: Jar) {
    setArchivesBaseName sdkName
    archiveClassifier = 'uber'
    destinationDir file(sdkDir)

    from("src/main/java")

    from {
        configurations.compile.collect {
            println("${it.name}")
            it.isDirectory() ? it : zipTree(it)
        }
    }
}


**Creating a file tree**
// Create a file tree with a base directory
ConfigurableFileTree tree = fileTree(dir: 'src/main')

// Add include and exclude patterns to the tree
tree.include '**/*.java'
tree.exclude '**/Abstract*'


// Create a tree using closure
tree = fileTree('src') {
    include '**/*.java'
}

// Create a tree using a map
tree = fileTree(dir: 'src', include: '**/*.java')
tree = fileTree(dir: 'src', includes: ['**/*.java', '**/*.xml'])
tree = fileTree(dir: 'src', include: '**/*.java', exclude: '**/*test*/**')


**Using archives as file trees**
// Create a ZIP file tree using path
FileTree zip = zipTree('someFile.zip')

// Create a TAR file tree using path
FileTree tar = tarTree('someFile.tar')

//tar tree attempts to guess the compression based on the file extension
//however if you must specify the compression explicitly you can:
FileTree someTar = tarTree(resources.gzip('someTar.ext'))

**Selecting the files to copy**
task copyTaskWithPatterns (type: Copy) {
    from 'src/main/webapp'
    into "$buildDir/explodedWar"
    include '**/*.html'
    include '**/*.jsp'
    exclude { FileTreeElement details ->
        details.file.name.endsWith('.html') &&
            details.file.text.contains('DRAFT')
    }
}



























