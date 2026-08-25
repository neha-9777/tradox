@REM Maven Wrapper for Windows
@echo off

set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM Find java.exe
if defined JAVA_HOME (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE=java.exe"
)

"%JAVA_EXE%" -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%~dp0" %WRAPPER_LAUNCHER% %*
