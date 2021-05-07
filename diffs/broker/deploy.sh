deleted file mode 100644
index 2104686..0000000
--- a/broker/deploy.sh
+++ /dev/null
@@ -1,6 +0,0 @@
-#!/bin/bash
-
-docker pull tokongs/ffi02-broker:latest
-docker stop broker
-docker rm broker
-docker run --name=broker --restart=always -p 8000:8000 -p 1883:1883 -d tokongs/ffi02:latest
