@echo off
	set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_79
	set CLASSPATH=.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;
	set PATH=%JAVA_HOME%\bin
	java -cp .;.\dependency\*;boss-job-0.0.1-SNAPSHOT.jar com.yaochen.boss.SSLTest
@pause