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


## [2.7.0] - 2018-10-18

### Security
- dependency update

### Added
- Loyalty bonus products
- MerchantCard frames (ident-link, user-selection, checkin-button)
- Possibility to get the access token.

### Changed
- Smart.IdentService onChanged event behavior

### Removed
- Support for Java 1.7


## [2.6.0] - 2018-01-30

### Security
- fasterxml dependency update

### Removed
- Android support
- Unmaintained tests
- Volley library


## [2.5.0] - 2017-12-18

### Deprecated
- Service.IdentRequest: PROVIDER_POSTIDENT use PROVIDER_POST_IDENT instead

### Added
New Methods:
- Loyalty.CardGroupService: checkPasscodeEnabled for loyalty card and transaction type
- Loyalty.MerchantCardService: validateCSC for loyalty card
- Loyalty.MerchantCardService: validatePasscode for loyalty card

Constants:
- Loyalty.CardGroup
- Smart.Ident

Model classes:
- General.Device
- Loyalty.Customer
- Loyalty.MerchantCard
- Payment.Customer
- Payment.Transaction
- Services.Address
- Services.AddressComponents
- Services.Contract
- Services.IdentRequest
- Smart.Checkin
- Smart.Device

Dependency for Java 9


## [2.4.0] - 2017-07-13

### Fixed
- Smart.Transaction: Cancel payment transaction call


## [2.3.0] - 2017-05-08

### Deprecated
- Config param "stomp.ssl"

### Added
New Methods:
- Smart.Transaction: Fetch an End of Day Report (Kassenschnitt)
- Smart.Transaction: Start extended diagnostic analysis
- Smart.Transaction: Cancel payment transaction (different from Loyalty)

### Fixed
- Invalid list of supported SSL protocols (Only TLSv1.2 is supported currently).

### Removed
- Config param "stomp.ssl" was removed because only secure connections are supported by the SecuConnect-API.


## [2.2.0] - 2017-04-20

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
- The versions of the dependencies are now up to date in the pom.xml
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
Reworked SDK
(the list of changes are not complete)

### Added
- AppId

### Changed
- Using the JAVA ServiceLoader class
- Handling of the credentials
- Handling of exceptions


## [1.0.0] - 2015-05-15
Reworked SDK

### Changed
- Authentication / Credentials
- Stomp

### Fixed
- TLS 1.2 support


## [0.3.2] - 2016-06-10

### Changed
Update README


## [0.3.1] - 2016-06-09

### Changed
- Improve IdentsDemo
- Correct payment transaction status constants.
- Renamed customer reference in containers for payment.
- Removed dedicated methods.
- Fix POM, remove gradle build file.


## [0.3.0] - 2016-03-17

### Changed
- Rename Person classes in com.secucard.connect.model.services
used by IdentRequest and IdentResult to Entity and changed all
access methods accordingly. IdentRequests can be created for persons
or companies, so a generic entity field serving both types is more
appropriate than just a single person field.
- Improve IdentsDemo.


## [0.2.0] - 2016-03-10

### Added
- Additional properties for smart transactions.
- Additional state for payment prepays.
- Support postident

### Changed
- Adding new contract param when cancel prepay/debits transactions. Needed on trans. created by subcontracts.


## [0.1.23] - 2015-07-14

### Changed
- merchant relation removed from payment.contract


## [0.1.22] - 2015-07-08

### Added
- Add build.gradle and AndroidManifest to use library as Android library.

### Changed
- payment contract cloning enhancements


## [0.1.21] - 2015-06-17

### Added
- payment.contracts clone added


## [0.1.20] - 2015-05-13

### Added
- new bankname field for payment container


## [0.1.19] - 2015-04-23

### Added
- state constants for ident request/response auth. cache clear method for client provided.


## [0.1.18] - 2015-04-15

### Changed
- async/sync event handling in payment services


## [0.1.17] - 2015-04-13

### Changed
- payment identification document model
- event handling for payment
- basic event handling corrections


## [0.1.16] - 2015-04-02

### Changed
- async/sync event processing, payment model corrections


## [0.1.15] - 2015-04-01

### Added
- token expire time
- Add account_read to News.

### Changed
- Change icon in getStream on VolleyChannel
- ResourceDownloader
- stomp message handling correction
- Modify StoreService
- contact model changes, moved from payment.customer and services.person
- MerchantCards service completed
- payment product models and services changed (prepay, debits, ...)
- payment demo corrections
- identresult event handling and model fixed


## [0.1.11] - 2015-03-11

### Changed
- timeout for volley future
- loyalty.customer corrected, picture handling improved
- downloader fix


## [0.1.10] - 2015-03-11

### Changed
- auth canceling added, old smart service removed, additional auth device info passing prepared
- client demo updated
- setter for config
- auth deactivated für general.account access
- Modify model classes and create new service for SecuApp and MerchantCards
- Reformat code with new code format.
- Modify services for store checkin and Account.
- Compile Volley dependency from maven not from project anymore.
- media download changed
- heart beat changed, model corrections
- exception handling improved
- picture property changed
- stomp event handling
- notifications for transactions
- Modify BigDecimals to int.
- implement onChangeEventListener for Account, AccountDevices and Transaction.
- Create addCard method in SecuAppService.
- volley stream request
- Modify date format pattern for dob. Add Log to AccountDevicesService.
- token expiring

### Fixed
- auth refresh fix, device field fix
- picture url handling corrected
- geo query params fixed
- client demo fixed
- stomp close fix
- store and account device corrections
- smart.transaction models corrections
- smart.transaction start fixed


## [0.1.1] - 2015-03-02
First public release


## [0.1.0] - not released
Internal developer release



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
[1.0.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.18...v1.0.0
[2.0.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v1.0.0...v2.0.0
[2.1.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.0.0...v2.1.0
[2.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.0...v2.1.1
[2.2.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.1...v2.2.0
[2.3.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.2.0...v2.3.0
[2.4.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.3.0...v2.4.0
[2.5.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.4.0...v2.5.0
[2.6.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.5.0...v2.6.0
[2.7.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.6.0...v2.7.0
