# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased] - YYYY-MM-DD

### Security

### Deprecated

### Added

### Changed

### Fixed

### Removed

## [2.2.0] - 2017-04-19

### Added
Added missing payment stuff (it's now identical with the .net-sdk and the php-sdk).
- SecupayDebitsService
- SecupayPrepaysService
- Transaction - Accrual
- Transaction - Basket
- Transaction - Experience
- Transaction - OptData
- Transaction - PaymentAction
- Transaction - Recipient
- Transaction - RedirectUrl
- Transaction - Subscription

### Changed
- The contract id is now optional in the cancel payment transaction methods

## [2.1.1] - 2016-06-10

### Addded
- Missing transaction status constants

### Changed
- Renamed product/payment/model/Container - getAssigned() to getCustomer()
- Renamed product/payment/model/Container - setAssigned() to setCustomer()

## [2.1.0] - 2016-05-06

### Added
- DataStorage
- Pagination for lists (getScrollableList)

### Changed
- DeviceCredentials - buildDeviceId()

### Removed
- product/smart/model/Transaction - paymentRequested
- product/smart/model/Transaction - paymentExecuted


## [2.0.0] - 2016-03-21
(the list of changes are not complete)

### Added
- AppId

### Changed
- Using the JAVA ServiceLoader class
- Handling of the credentials
- Handling of exceptions

## [1.0.0] - 2015-05-15
(the list of changes are not complete)

### Added
- Device Credentials
- Stomp

### Changed
- Authentication / Credentials

### Fixed
- TLS 1.2 support


## [0.3.2] - 2016-06-10
297b211976b9a2e5a3400ea6975e2b693ca73378

## [0.3.1] - 2016-06-09
9df781c4788f8d1b383a11ef14bfe1919d1c722d

## [0.3.0] - 2016-03-17
db1716b565b0859740d31c2bc474b051c727dc6a
(the list of changes are not complete)

### Changed
- Rename Person classes in com.secucard.connect.model.services
used by IdentRequest and IdentResult to Entity and changed all
access methods accordingly. IdentRequests can be created for persons
or companies, so a generic entity field serving both types is more
appropriate than just a single person field.
- Improve IdentsDemo.

## [0.2.0] - 2016-03-10
6bd4089fbb0de82f3537854426415b2032f46298

## [0.1.23] - 2015-07-14
789c75aa84dd61a8a00b61f14d14df189803f284

## [0.1.22] - 2015-07-08
447af00af0de5e66eccc4496844bf36861b1af66

## [0.1.21] - 2015-06-17
5bb82b079ac2b9095c31f85fda1c15a049a26b7d

## [0.1.20] - 2015-05-13
b82e1cdefc8d5ff7e4259eb26479d248e09a89e2

## [0.1.19] - 2015-04-23
f57797b01de674e5153ba213fcf1cb6a6c915521

## [0.1.18] - 2015-04-15
97742f773dd6425a6b0e981003fc4f12aa745583

## [0.1.17] - 2015-04-13
6b6bd98a8fc29dd0caa9b99e1a951b7e37238cce

## [0.1.16] - 2015-04-02
100cac4f0a26e39370d10ab805ca0c0ef6b8a54e

## [0.1.15] - 2015-04-01
fa26aa2c74d57bacb094e8c4c0e1158c186bf5ea

## [0.1.11] - 2015-03-11
6ba4ce01ce970f28c730a3a27a3d8d3eea061fa9

## [0.1.10] - 2015-03-11
fcd8d467f970532ed70a779e0c4a45d2d90a1220

## [0.1.1] - 2015-03-02
fe6a21b594ef4c9295606cba64720f38452b0ce1

## [0.1.0] - not released
e2ea08986d4e1656016d079bbe6f19b713ceac62




[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.0...v0.1.1
[0.1.10]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.1...v0.1.10
[0.1.11]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.10...v0.1.11
[0.1.15]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.11...v0.1.15
[0.1.16]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.15...v0.1.16
[0.1.17]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.16...v0.1.17
[0.1.18]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.17...v0.1.18
[0.1.19]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.18...v0.1.19
[0.1.20]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.19...v0.1.20
[0.1.21]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.20...v0.1.21
[0.1.22]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.21...v0.1.22
[0.1.23]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.22...v0.1.23
[0.2.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.23...v0.2.0
[0.3.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.2.0...v0.3.0
[0.3.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.3.0...v0.3.1
[0.3.2]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.3.1...v0.3.2

[1.0.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.3.2...v1.0.0

[2.1.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.0.0...v2.1.0
[2.1.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.0.0...v2.1.0
[2.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.0...v2.1.1
[2.2.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.0...v2.2.0