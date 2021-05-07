deleted file mode 100755
index 79b4d0c..0000000
--- a/client/jrepl.sh
+++ /dev/null
@@ -1,3 +0,0 @@
-#!/bin/sh
-
-jshell --class-path `cat ./build/tmp/compileJava/source-classes-mapping.txt`:target/classes
