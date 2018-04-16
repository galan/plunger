Plunger is a simple messaging client for the shell. It will closely follow the principles of *nix programs that allow chaining by piping output from one program to another. The output is readable by humans, textual, but also usable for further processing and manipulation.

It is written in Java 8, and provides convenience scripts for bash.

# Available commands
* [ls](command-ls.md) - lists the available destinations
* [cat](command-cat.md) - consumes/reads messages
* [put](command-put.md) - sends messages to a destination
* [count](command-count.md) - shows the number of messages/consumers for a destination

# Usage
Basic usage is:
`plunger <target> [options]`

You can always use `--help` to see the common arguments or, when given, the command-specific arguments.

## Common arguments
* -C,--command \<arg>  _/ The command to execute against the target_
* -colors \<arg>  _/ Highlights the output_
* -h,--help  _/ Print program usage_
* -v,--verbose  _/ Verbose mode. Causes plunger to print debugging messages about its progress._
* -version  _/ Prints the version_

## Shortcuts
There is a shortcut for each command, so instead of writing eg. `plunger <target> -C ls -rv` you can simply write `pls <target> -rv`. The name of the shortcut is `p<Command>`.

## Target
The target is an uri that represents the message broker. You can either pass a target directly, or define an alias for easier usage in the `.plunger`file (see below).

The direct notation is `provider://[username[:password]@]host[:port][/destination]`

# Configuration file
plunger makes use of a configuration file, when available. It is comparable with the ~/.ssh/config file. Targets can be predefined for easier access with aliases. You can asign multiple aliases to a target.

## Location
The configuration file will be selected in the following order:
* `$XDG_CONFIG_HOME/plunger/targets`
* `$HOME/.config/plunger/targets`
* `$HOME/.plunger`

## Syntax
Available properties are `Alias` and `Target`

Simple example:

    Alias local hq
    Target hornetq://username:password@localhost:5445/queue.test

So instead of writing eg. `pls hornetq://username:password@localhost:5445/queue.test` you can simply write `pls local` or `pls hq`. You also can override elements such as the destination, eg. `pls hq/queue.other`.

Note: When a destination is predefined, and you want to show all queues with `pls`, simply erase the destination by appending a slash like eg. `pls hq/`.

# Provider
Plunger is messaging system agnostic, any message broker can be added. To do this, you have to implement the CommandProvider interface as SPI.

## Available provider
* JBoss HornetQ [setup](provider-jboss-hornetq.md) >= 2.2.5 -- protocol-name: hornetq
* Apache ActiveMQ [setup](provider-apache-activemq.md) >= 5.8.0 -- protocol-name: amq
* RabbitMQ [setup](provider-rabbitmq.md) >= 3.5.x -- protocol-name: rabbitmq
* Apache Kafka [setup](provider-apache-kafka.md) >= 0.10.1 -- protocol-name: kafka

Other provides are planned, and might be implemented (of course you can create a PR as well). Take a look at the [issue-list](https://github.com/galan/plunger/issues?state=open).

# Building sources
See [Building plunger](plunger-build.md)
