## VCSStatistics
VCSStatistics is an application to calculate commits statistics for different version control systems. Currently only Git is supported.
Report is generated into Excel file.

## Building from Source
VCSStatistics uses a [Maven][]-based build system. In the instructions below, `mvn` is invoked from the root of the source tree and servces as a cross-patform, self-contained bootstrap mechanism for the build.

### Prerequisites
[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.20` folder
extracted from the JDK download.

### Check out sources
`git clone git@github.com:ashykhmat/VCSStatistics.git`

### Import sources into your IDE
In IDE import project as existing Maven project. IDE will load automatically all required dependencies with their sources.

### Compile and test; build all jars, distribution zips, and docs
`mvn clean install`

... and discover more commands with `mvn --help`.

## Run Console application

To run VCSStatistics as a console application, after it was built, run the following command
`java -jar vcsstatistics/target/vcsstatistics-1.0-SNAPSHOT.jar -projectPath PATH_TO_PROJECT -reportPath PATH_TO_REPORT -vcs GIT -dateFrom yyyy-MM-dd -dateTo yyyy-MM-dd`

Parameter | Description | Is Required | Example Value |
------------ | ------------- | ------------- | ------------- |
`projectPath` | specifies path to the folder, that contains application to analyze | true | C:\workspace\my_application |
`reportPath` | specifies path to the folder, that will be used to store generated Excel report | true | C:\report |
`vcs` | specifies version control system type. Supported values: GIT | true | GIT |
`dateFrom` | specifies date from which statistic will be calculated | false | 2017-01-01 |
`dateTo` | specifies date to which statistic will be calculated | false | 2020-01-01 |
`help` | command to see application help information | false 

**Note that required parameters should be specified to run application correctly.**

Sample command:

`java -jar vcsstatistics/target/vcsstatistics-1.0-SNAPSHOT.jar -projectPath C:\\workspace\\my_application -reportPath C:\\report -vcs GIT -dateFrom 2017-01-01 -dateTo 2020-01-01`


[Maven]: https://maven.apache.org/
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
