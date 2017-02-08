@echo off

rem call example
rem install.cmd "C:\Program Files (x86)\JetBrains\IntelliJ IDEA 2016.3" 2016.3

if %1== goto help
if %2== goto help

if not exist "%~1\" goto errdir

call mvn install:install-file -Dfile=%1\lib\idea.jar -DgroupId=org.jetbrains -DartifactId=idea -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\openapi.jar -DgroupId=org.jetbrains -DartifactId=openapi -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\annotations.jar -DgroupId=org.jetbrains -DartifactId=annotations -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\util.jar -DgroupId=org.jetbrains -DartifactId=util -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\extensions.jar -DgroupId=org.jetbrains -DartifactId=extensions -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\plugins\maven\lib\maven.jar -DgroupId=org.jetbrains.plugins -DartifactId=maven -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\plugins\maven\lib\maven-server-api.jar -DgroupId=org.jetbrains.plugins -DartifactId=maven-server-api -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\trove4j.jar -DgroupId=org.jetbrains -DartifactId=trove4j -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\jdom.jar -DgroupId=org.jetbrains -DartifactId=jdom -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\lib\kotlin-runtime.jar -DgroupId=org.jetbrains -DartifactId=kotlin-runtime -Dversion=%2 -Dpackaging=jar
call mvn install:install-file -Dfile=%1\plugins\properties\lib\properties.jar -DgroupId=org.jetbrains.plugins -DartifactId=properties -Dversion=%2 -Dpackaging=jar

goto fin

:help
echo Usage: install.bat [IDEA dir] [IDEA version]
goto fin

:errdir
echo Directory %1 does not exist.
goto fin

:fin
