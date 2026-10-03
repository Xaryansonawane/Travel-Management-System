@REM Maven wrapper batch file with Java 17 configuration
@echo off
if exist "C:\Users\aryan\.jdks\ms-17.0.20" (
    set "JAVA_HOME=C:\Users\aryan\.jdks\ms-17.0.20"
    set "PATH=C:\Users\aryan\.jdks\ms-17.0.20\bin;%PATH%"
)
set "MAVEN_EXE=C:\Users\aryan\.maven\maven-3.10.0-rc-1\bin\mvn.cmd"
if exist "%MAVEN_EXE%" (
    "%MAVEN_EXE%" %*
) else (
    mvn %*
)
