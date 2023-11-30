# Command put
`put` adds messages to a destination. There are two simple text formats supported (depending on the use-case):
* The message is represented in the form `<properties>\tbody`, where properties are a simple json-encoded array, separated by a tab from the body. 
* Using the `-d` switch, each line from the input is directly converted to a message, using the content of the line as body. This way no escaping of the body is necessary/performed, but properties are not possible. 

Messages can be passed by stdin or a file.

# Syntax
`pput <target> [options]`
(short for `plunger <target> -C put [options]`)

# Example
Using the escaped format:
    $ echo -e "{}\tHello world!" | pput target
    $ echo -e "{\"myproperty\":\"sample\"}\tHello\nworld!" | pput target

Using the direct format
    $ echo -e "Hello world!" | pput target -d

# Time to live
You can define the time the message is kept by the messaging-server. The ttl is written in a human readable syntax, where the amount and unit of time are written as pair. Support of this property is depending on the used provider.
Units are:
* ms = milliseconds
* s = seconds
* m = minutes
* h = hours
* d = days

Examples

    $ pput target -t "1m" # TTL 1 minute
    $ pput target -t "2h 5m 10s" # TTL 2 hours, 5 minutes and 10 seconds

# Command line arguments
Available via `pput --help`
* -d,--direct  _/ Pass line direct as message (each line, unescaped, without header)._
* -f,--file <arg>  _/ file with escaped messages (instead of stdin)._
* -p,--priority <arg>  _/ Priority._
* -s,--skip  _/ skip lines with errors._
* -t,--ttl <arg>  _/ Time to live, see documentation for format._
* -r,--routingkey <arg> _/ Routingkey (AMQP)_
* -c,--compression <arg> _/ Compression type (kafka). Supported values: none, gzip, snappy (default), lz4, or zstd._
