# Provider RabbitMQ
You can use the commands `cat` and `put` without any modifications. To enable `ls and `count`, the rabbitmq-management plugin must be installed.

Syntax:

    rabbitmq://<user>:<password>@<host>:<port>?managementPort=<managementPort>

Examples:

    pls rabbitmq://guest:guest@localhost:5672?managementPort=15672
    pcat rabbitmq://guest:guest@localhost:5672/my.queue?managementPort=15672

Usually you want to configure the message broker in the configuration file, eg.:

    Alias rabbitmq rmq
    Target rabbitmq://guest:guest@localhost:5672?managementPort=15672

And use it this way later (using the examples from above):

    pls rmq
    pcat rmq/my.queue

