# Command ls
With the command `ls` you can list all available destinations of the message-provider. Additional information such as number of the consumers and messages are displayed (this might vary, depending on the provider).

# Syntax
`pls <target> [options]`
(short for `plunger <target> -C ls [options]`)

# Example output

List all available destinations (colored by default)

    $ pls target
    queue.application-events (1/82)
    queue.application-failover (0/0)
    queue.dlq (0/0)
    queue.tasks (4/904)
    queue.testing (temporary) (0/50)
    topic.monitoring-events (0/0)

List all destinations with messages that are durable

    $ pls target -mt
    queue.application-events (1/82)
    queue.tasks (4/904)


# Command line arguments
Available via `pls --help`

* -c,--consumer  _/ Only show destinations with consumers_
* -i,--informations  _/ When set, additional informations (like counters) are omitted._
* -m,--messages  _/ Only show destinations with messages._
* -p,--persistent  _/ Filters temporary destinations._
* -t,--temporary  _/ Filters persistent (durable) destinations._
