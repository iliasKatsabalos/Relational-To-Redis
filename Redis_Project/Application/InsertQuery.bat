@echo off
cls
:start
echo Insert query file
java -jar InsertQuery.jar
javac -cp jedis-2.8.1.jar Executable.java
java Executable
goto start