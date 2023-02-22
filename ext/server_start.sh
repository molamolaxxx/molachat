 #!/bin/bash
java -Xmx1024m -jar ./molachat.jar \
--server.port=8550 \
--self-conf.connect-timeout=60000 \
--self-conf.close-timeout=3600000 \
--self-conf.max-client-num=20 \
--self-conf.max-session-message-num=50 \
--self-conf.max-file-size=1000 \
--self-conf.max-request-size=1000 \
--management.server.port=9002 \
--app.id=molachat_outside_001 \
--app.server-type=tomcat \
--app.version=2.3.1 \
&