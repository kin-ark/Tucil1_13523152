@echo off
echo Compiling Java files...
javac -d bin -sourcepath src src/com/kinan/iqpuzzlerpro/*.java src/com/kinan/iqpuzzlerpro/game/*.java src/com/kinan/iqpuzzlerpro/gui/*.java src/com/kinan/iqpuzzlerpro/io/*.java src/com/kinan/iqpuzzlerpro/solver/*.java

echo Creating JAR file...
echo Main-Class: com.kinan.iqpuzzlerpro.App > manifest.txt
jar cfm bin/IQPuzzlerPro.jar manifest.txt -C bin .

echo Build complete! Run with:
echo java -jar bin/IQPuzzlerPro.jar
pause