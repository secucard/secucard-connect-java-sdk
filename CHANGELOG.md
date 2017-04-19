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

## [2.2.0] - 2017-04-21

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


## [0.3.2] - 10 Jun 2016
297b211976b9a2e5a3400ea6975e2b693ca73378

## [0.3.1] - 9 Jun 2016
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

## [0.2.0] - 10 Mar 2016
6bd4089fbb0de82f3537854426415b2032f46298

## [0.1.23] - 14 Jul 2015
789c75aa84dd61a8a00b61f14d14df189803f284

## [0.1.22] - 8 Jul 2015
447af00af0de5e66eccc4496844bf36861b1af66

## [0.1.21] - 17 Jun 2015
f6ff8ddc0ea2b0816da54b196d6b43514b742e60

## [0.1.20] - 13 May 2015
b82e1cdefc8d5ff7e4259eb26479d248e09a89e2

## [0.1.19] - 23 Apr 2015
f57797b01de674e5153ba213fcf1cb6a6c915521

## [0.1.18] - 15 Apr 2015
97742f773dd6425a6b0e981003fc4f12aa745583

## [0.1.17] - 13 Apr 2015
6b6bd98a8fc29dd0caa9b99e1a951b7e37238cce

## [0.1.16] - 2 Apr 2015
100cac4f0a26e39370d10ab805ca0c0ef6b8a54e

## [0.1.15] - 1 Apr 2015
fa26aa2c74d57bacb094e8c4c0e1158c186bf5ea

## [0.1.11] - 11 Mar 2015
6ba4ce01ce970f28c730a3a27a3d8d3eea061fa9

## [0.1.10] - 11 Mar 2015
fcd8d467f970532ed70a779e0c4a45d2d90a1220

## [0.1.1] - 2015-03-02
fe6a21b594ef4c9295606cba64720f38452b0ce1

## [0.1.0] - not released
e2ea08986d4e1656016d079bbe6f19b713ceac62




[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/e2ea08986d4e1656016d079bbe6f19b713ceac62...fe6a21b594ef4c9295606cba64720f38452b0ce1
[0.1.10]:https://github.com/secucard/secucard-connect-java-sdk/compare/fe6a21b594ef4c9295606cba64720f38452b0ce1...fcd8d467f970532ed70a779e0c4a45d2d90a1220
[0.1.11]:https://github.com/secucard/secucard-connect-java-sdk/compare/fcd8d467f970532ed70a779e0c4a45d2d90a1220...6ba4ce01ce970f28c730a3a27a3d8d3eea061fa9
[0.1.15]:https://github.com/secucard/secucard-connect-java-sdk/compare/6ba4ce01ce970f28c730a3a27a3d8d3eea061fa9...fa26aa2c74d57bacb094e8c4c0e1158c186bf5ea
[0.1.16]:https://github.com/secucard/secucard-connect-java-sdk/compare/fa26aa2c74d57bacb094e8c4c0e1158c186bf5ea...100cac4f0a26e39370d10ab805ca0c0ef6b8a54e
[0.1.17]:https://github.com/secucard/secucard-connect-java-sdk/compare/100cac4f0a26e39370d10ab805ca0c0ef6b8a54e...6b6bd98a8fc29dd0caa9b99e1a951b7e37238cce
[0.1.18]:https://github.com/secucard/secucard-connect-java-sdk/compare/6b6bd98a8fc29dd0caa9b99e1a951b7e37238cce...97742f773dd6425a6b0e981003fc4f12aa745583
[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/
[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/
[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/
[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/