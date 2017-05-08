# Command count
`count` simply returns the number of messages or consumer from a destination.

# Syntax
`pcount <target> [options]`
(short for `plunger <target> -C count [options]`)

# Example

Return the number of messages for a destination:

    $ pcount target

# Command line arguments
Available via `pcount --help`
* -c,--consumer  _/ Count consumers for destination (instead of messages)._

