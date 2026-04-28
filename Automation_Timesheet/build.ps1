$env:JAVA_HOME="D:\jdk-21_windows-x64_bin\jdk-21.0.10"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn clean install -DskipTests
mvn spring-boot:run