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


## [2.17.0] - 2023-03-13
[2.17.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.16.0...v2.17.0

### Security
- Update dependencies to the latest versions.


## [2.16.0] - 2021-12-13
[2.16.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.15.0...v2.16.0

### Security
- Update dependencies to the latest versions.

### Added
- Using the Apache Maven registry from GitHub.


## [2.15.0] - 2021-07-02
[2.15.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.14.0...v2.15.0

### Changed
- The timeout was changed for the methods `ping`, `sendLogMessage` and the `OfflineMessagesThread` from 5 to 9,
  to be more tolerant against DNS updates.
- To solve some possible deadlocks, the StompClient is now using `ReentrantReadWriteLock` instead of `synchronized` to make the socket Thread safe.
- If there was some `IOException` in the StompClient during sending a message (f.e. because of a closed socket),
  the `initConnection` method is now called automatically and a second attempt will be done.

## [2.14.0] - 2021-04-15
[2.14.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.13.4...v2.14.0

### Security
- dependency update

### Added
- Ensuring that the stomp socket has some timeout set.
- StompClient.connect(): added some more debug logs

### Fixed
- License information in the pom file

### Removed
- smart.model.Basket: removed unused parameter `texts`


## [2.13.4] - 2020-05-19
[2.13.4]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.13.3...v2.13.4

### Added
- smart.model.PrepaidSale: added `errorDetails`
- SecucardConnect.open(): added some more debug logs


## [2.13.3] - 2019-12-03
[2.13.3]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.13.2...v2.13.3

### Changed
- Remote logging: smaller improvements
- Stomp "awaitReceipt" logic changed


## [2.13.2] - 2019-12-02
[2.13.2]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.13.1...v2.13.2

### Changed
- Remote logging: also for exceptions in JaxRsChannel enabled
- Timeout for session refresh is now configurable via "stomp.sessionRefreshTimoutSec" and the default is now 15 seconds.


## [2.13.1] - 2019-11-26
[2.13.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.13.0...v2.13.1

### Changed
- Smart.Transactions: smaller improvements
- Remote logging: smaller improvements


## [2.13.0] - 2019-11-22
[2.13.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.12.0...v2.13.0

### Security
- dependency updates

### Added
- Smart.Transactions: Background thread for Offline functionality (cache & offline receipt)
- Remote logging


## [2.12.0] - 2019-09-26
[2.12.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.11.0...v2.12.0

### Security
- dependency updates

### Added
- Smart.Transactions: Offline functionality (cache & offline receipt)
- Smart.Ident: display placeholder page


## [2.11.0] - 2019-08-07
[2.11.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.10.0...v2.11.0

### Security
- dependency updates

### Added
- Payment: TransactionsService


## [2.10.0] - 2019-05-06
[2.10.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.9.0...v2.10.0

### Added
- Smart.Model.Transaction: prepaidSales


## [2.9.0] - 2019-02-21
[2.9.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.8.0...v2.9.0

### Security
- dependency update "com.google.guava"

### Added
- Payment: SecupayPayoutService
- Payment.Model.Transaction: redirectUrl.urlPush
- Payment.Model.Transaction: demo

### Changed
- toString now includes the "id" and the "object" if it extends from SecuObject
- Move TransferAccount from SecupayPrepay to it's own file
- code format of the folder "com.secucard.connect.product"

### Removed
- Payment.SecupayInvoice: container (because it not supported via API)


## [2.8.0] - 2019-01-30
[2.8.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.7.1...v2.8.0

### Changed
- The Callback type of smart.TransactionService.cancel(id, callback) is now smart.model.Transaction


## [2.7.1] - 2019-01-22
[2.7.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.7.0...v2.7.1

### Security
- fasterxml.jackson dependency update


## [2.7.0] - 2018-10-18
[2.7.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.6.0...v2.7.0

### Security
- dependency update

### Added
- Loyalty bonus products
- MerchantCard frames (ident-link, user-selection, checkin-button)
- Possibility to get the access token.

### Changed
- Smart.IdentService onChanged event behavior
- Additional error information for exception "Failed to read secucard server response"

### Fixed
- Definition error in the ObjectList model
- Payment: cancel method in SecupayCreditcardsService, SecupayDebitsService, SecupayInvoicesService and SecupayPrepaysService

### Removed
- Support for Java 1.7


## [2.6.0] - 2018-01-30
[2.6.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.5.0...v2.6.0

### Security
- fasterxml.jackson dependency update

### Removed
- Android support
- Unmaintained tests
- Volley library


## [2.5.0] - 2017-12-18
[2.5.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.4.0...v2.5.0

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
[2.4.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.3.0...v2.4.0

### Fixed
- Smart.Transaction: Cancel payment transaction call


## [2.3.0] - 2017-05-08
[2.3.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.2.0...v2.3.0

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
[2.2.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.1...v2.2.0

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
[2.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.1.0...v2.1.1

### Addded
- Missing transaction status constants

### Changed
- Renamed product/payment/model/Container - getAssigned() to getCustomer()
- Renamed product/payment/model/Container - setAssigned() to setCustomer()


## [2.1.0] - 2016-05-06
[2.1.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v2.0.0...v2.1.0

### Added
- DataStorage
- Pagination for lists (getScrollableList)

### Changed
- DeviceCredentials - buildDeviceId()

### Removed
- product/smart/model/Transaction - paymentRequested
- product/smart/model/Transaction - paymentExecuted


## [2.0.0] - 2016-03-21
[2.0.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v1.0.0...v2.0.0

Reworked SDK
(the list of changes are not complete)

### Added
- AppId

### Changed
- Using the JAVA ServiceLoader class
- Handling of the credentials
- Handling of exceptions


## [1.0.0] - 2015-05-15
[1.0.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.18...v1.0.0

Reworked SDK

### Changed
- Authentication / Credentials
- Stomp

### Fixed
- TLS 1.2 support


## [0.3.2] - 2016-06-10
[0.3.2]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.3.1...v0.3.2

### Changed
Update README


## [0.3.1] - 2016-06-09
[0.3.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.3.0...v0.3.1

### Changed
- Improve IdentsDemo
- Correct payment transaction status constants.
- Renamed customer reference in containers for payment.
- Removed dedicated methods.
- Fix POM, remove gradle build file.


## [0.3.0] - 2016-03-17
[0.3.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.2.0...v0.3.0

### Changed
- Rename Person classes in com.secucard.connect.model.services
used by IdentRequest and IdentResult to Entity and changed all
access methods accordingly. IdentRequests can be created for persons
or companies, so a generic entity field serving both types is more
appropriate than just a single person field.
- Improve IdentsDemo.


## [0.2.0] - 2016-03-10
[0.2.0]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.23...v0.2.0

### Added
- Additional properties for smart transactions.
- Additional state for payment prepays.
- Support postident

### Changed
- Adding new contract param when cancel prepay/debits transactions. Needed on trans. created by subcontracts.


## [0.1.23] - 2015-07-14
[0.1.23]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.22...v0.1.23

### Changed
- merchant relation removed from payment.contract


## [0.1.22] - 2015-07-08
[0.1.22]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.21...v0.1.22

### Added
- Add build.gradle and AndroidManifest to use library as Android library.

### Changed
- payment contract cloning enhancements


## [0.1.21] - 2015-06-17
[0.1.21]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.20...v0.1.21

### Added
- payment.contracts clone added


## [0.1.20] - 2015-05-13
[0.1.20]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.19...v0.1.20

### Added
- new bankname field for payment container


## [0.1.19] - 2015-04-23
[0.1.19]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.18...v0.1.19

### Added
- state constants for ident request/response auth. cache clear method for client provided.


## [0.1.18] - 2015-04-15
[0.1.18]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.17...v0.1.18

### Changed
- async/sync event handling in payment services


## [0.1.17] - 2015-04-13
[0.1.17]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.16...v0.1.17

### Changed
- payment identification document model
- event handling for payment
- basic event handling corrections


## [0.1.16] - 2015-04-02
[0.1.16]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.15...v0.1.16

### Changed
- async/sync event processing, payment model corrections


## [0.1.15] - 2015-04-01
[0.1.15]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.11...v0.1.15

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
[0.1.11]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.10...v0.1.11

### Changed
- timeout for volley future
- loyalty.customer corrected, picture handling improved
- downloader fix


## [0.1.10] - 2015-03-11
[0.1.10]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.1...v0.1.10

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
[0.1.1]:https://github.com/secucard/secucard-connect-java-sdk/compare/v0.1.0...v0.1.1
First public release


## [0.1.0] - not released
[0.1.0]:https://github.com/secucard/secucard-connect-java-sdk/tree/v0.1.0
Internal developer release

