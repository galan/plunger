# Command cat
The `cat`-command retrieves messages from the message-provider, either browsing (only retrieving, but not removing) or consuming (removed from the destination).

For each message the properties and body will be displayed; the output is human-readable. For further processing (see [pput](https://github.com/galan/plunger/wiki/Command-put)) a simple format is available, where each message is displayed in one line.

# Syntax
`pcat <target> [options]`
(short for `plunger <target> -C cat [options]`)

# Example output

Consume messages on the specified destination (normally colored)

    $ pcat target -r
    JMSDeliveryMode: 2
    JMSDestination: HornetQQueue[testing]
    JMSMessageID: ID:0566f28b-c9c2-11e2-9b79-0024e8431f8e
    JMSPriority: 4
    JMSRedelivered: false
    JMSReplyTo: 
    JMSTimestamp: 1369984163104 (2013-05-31 09:09:23 CEST)
    JMSXDeliveryCount: 0
    myproperty: sample
    Hello world!

# Command line arguments
Available via `pcat --help`
* -b,--body  _/ Suppresses the body._
* -c,--cut <arg>  _/ Cuts the body after n characters, adding ... when characters were removed._
* -d,--seperator  _/ Suppresses the delimiting separator._
* -e,--escape  _/ Escapes the message. When the output is intended for further processing, this switch will map all output to single line. The properties are formatted as json, the body is escaped as well. This form is required for put._
* -n,--limit <arg>  _/ Limits the messages to the first n elements in a queue or received by a topic._
* -p,--properties  _/ Suppresses the properties._
* -r,--remove  _/ Read messages will also be removed from the queue._
* -s,--selector <arg>  _/ selector to filter the targets result. (JMS)_
