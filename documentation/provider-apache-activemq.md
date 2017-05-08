# Provider Apache ActiveMQ
You can use the commands `cat` and `put` without any modifications. To enable `ls` and `count` you need to enable JMX in ActiveMQ and add it to your target.

Enabling JMX by adding the following line to `$AMQ_HOME/bin/activemq`:

    ACTIVEMQ_SUNJMX_START="-Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

Syntax:

    amq://<user>:<password>@<host>:<port>?jmxPort=<jmxPort>

Usually you want to configure the message broker in the configuration file, eg.:

    Alias amq
    Target amq://guest:guest@localhost:61616?jmxPort=1099

And use it this way later:

    # 1099 is the default port and can be omitted
    pls amq://localhost:61616
    pls amq://localhost:61616?jmxPort=9876

