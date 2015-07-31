# Javadoc2Wiki
Javadoc doclet that outputs twiki formatted txt files.

This is not a full featured implementation. It was just enough to accomplish the goal I had in mind.
Each class is rendered into a new topic txt file. Summary tables are built from fields and methods and
then detail blocks are added for each method.

To compile the doclet you need to load the load the Java tools.jar which contains the JavaDoc classes
```
javac ./javadoc2wiki/WikiDoclet.java -classpath /usr/lib/jvm/default-java/lib/tools.jar
```
Once compiled you can run JavaDoc on load the doclet. Is this example the bootclasspath is set to android.jar so
as to resolve references found in an Android project.
```
javadoc -docletpath ../src -doclet javadoc2wiki.WikiDoclet \
-bootclasspath ~/workspace/android-sdk-linux/platforms/android-22/android.jar \
-sourcepath ./workspace/MyProject/app/src/main/java -subpackages com.sprague.myproject
```
