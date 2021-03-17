# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.8.0] - 2021-03-17

### Added
* [common] Migrated plunger to JDK 11
* [common] Added CHANGELOG.md
* [kafka] Added `tx` and `txTimeout` argument, to enable transactional producer (by [@flecno](https://github.com/flecno))
* [kafka] Added `async` argument, where a Callback is used (by [@flecno](https://github.com/flecno))

### Changed
* [common] Updated jansi library (by [@flecno](https://github.com/flecno))
* [common] Improved output performance (by [@flecno](https://github.com/flecno))
* [kafka] Enabled lz4 compression for producers (by [@flecno](https://github.com/flecno))
* [kafka] Increased producer batch-size-config from 16384 to 100k (by [@flecno](https://github.com/flecno))
* [kafka] Increased producer linger-ms-config from 1 to 50 (by [@flecno](https://github.com/flecno))


## [1.7.1] - 2019-07-15

### Changed
* [kafka] Improved schema-registry URL behavior for kafka provider.
* [kafka] Changed kafka timeout default from 1s to 4s.


## [1.7.0] - 2019-07-11

### Added
* [kafka] Added support for avro-schemas to the kafka provider (by [@clairefautsch](https://github.com/clairefautsch) and [@eberdt](https://github.com/eberdt))

### Changed
* [kafka] Updated rabbitmq library (Security CVE)
* [kafka] Updated kafka library


## [1.6.2] - 2018-10-05

### Added
* [common] Added JAVA_OPTS environment variable support


## [1.6.1] - 2018-04-16

### Fixed
* [rabbitmq] Fixed minor api-change in rabbitmq with newer versions (3.6.x).


## [1.6.0] - 2018-02-26

### Added
* [kafka] Support for kafka-headers

### Changed
* [kafka] Kafka-Client update to 1.0.0
* [kafka] Renamed kafka meta-data headers, now starting with `kafka.`


## [1.5.9] - 2017-11-01

### Added
* [kafka] Kafka option `acks` for producer, defaults to `all`.


## [1.5.8] - 2017-11-01

### Changed
* [kafka] changed commitSync behaviour for kafka provider cat command.


## [1.5.7] - 2017-07-07

### Added
* [kafka] Added `maxPartitionFetchBytes` - Sets the `max.partition.fetch.bytes` option for the kafka consumer (pcat)
* [kafka] Added `maxRequestSize` - Sets the `max.request.size option` for the kafka producer (pput)


## [1.5.6] - 2017-03-22

### Added
* [common] Added `-d` option to `put`, so you can pass lines with plain content directly to the message broker (each line a message, unescaped, without header).
* [kafka] Added `maxPollRecords` parameter to kafka provider


## [1.5.5] - 2017-03-01

### Changed
* [kafka] Sort output of kafka topics when using command `ls`


## [1.5.4] - 2017-02-15

### Fixed
* [kafka] NPE `fix` for key parameter handling in kafka provider


## [1.5.3] - 2017-02-13

### Changed
* [kafka] key parameter behaviour improved (will use plunger `key` parameter, or `key` message-header)

### Removed
* [kafka] Removed JMX calls for kafka `ls` command






[Unreleased]: https://github.com/galan/plunger/compare/v1.8.0...HEAD
[1.8.0]: https://github.com/galan/plunger/compare/v1.7.1...v1.8.0
[1.7.1]: https://github.com/galan/plunger/compare/v1.7.0...v1.7.1
[1.7.0]: https://github.com/galan/plunger/compare/v1.6.2...v1.7.0
[1.6.2]: https://github.com/galan/plunger/compare/v1.6.1...v1.6.2
[1.6.1]: https://github.com/galan/plunger/compare/v1.6.0...v1.6.1
[1.6.0]: https://github.com/galan/plunger/compare/v1.5.9...v1.6.0
[1.5.9]: https://github.com/galan/plunger/compare/v1.5.8...v1.5.9
[1.5.8]: https://github.com/galan/plunger/compare/v1.5.7...v1.5.8
[1.5.7]: https://github.com/galan/plunger/compare/v1.5.6...v1.5.7
[1.5.6]: https://github.com/galan/plunger/compare/v1.5.5...v1.5.6
[1.5.5]: https://github.com/galan/plunger/compare/v1.5.4...v1.5.5
[1.5.4]: https://github.com/galan/plunger/compare/v1.5.3...v1.5.4



[0.0.1]: https://github.com/galan/plunger/releases/tag/v0.0.1
