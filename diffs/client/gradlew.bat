index ac1b06f..9109989 100644
--- a/client/gradlew.bat
+++ b/client/gradlew.bat
@@ -40,7 +40,7 @@ if defined JAVA_HOME goto findJavaFromJavaHome
 
 set JAVA_EXE=java.exe
 %JAVA_EXE% -version >NUL 2>&1
-if "%ERRORLEVEL%" == "0" goto execute
+if "%ERRORLEVEL%" == "0" goto init
 
 echo.
 echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
@@ -54,7 +54,7 @@ goto fail
 set JAVA_HOME=%JAVA_HOME:"=%
 set JAVA_EXE=%JAVA_HOME%/bin/java.exe
 
-if exist "%JAVA_EXE%" goto execute
+if exist "%JAVA_EXE%" goto init
 
 echo.
 echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
@@ -64,14 +64,28 @@ echo location of your Java installation.
 
 goto fail
 
+:init
+@rem Get command-line arguments, handling Windows variants
+
+if not "%OS%" == "Windows_NT" goto win9xME_args
+
+:win9xME_args
+@rem Slurp the command line arguments.
+set CMD_LINE_ARGS=
+set _SKIP=2
+
+:win9xME_args_slurp
+if "x%~1" == "x" goto execute
+
+set CMD_LINE_ARGS=%*
+
 :execute
 @rem Setup the command line
 
 set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
 
-
 @rem Execute Gradle
-"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
+"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%
 
 :end
 @rem End local scope for the variables with windows NT shell
