# Change Log

## [0.3.0] - 2016-03-17
### Changed
- Rename Person classes in com.secucard.connect.model.services
used by IdentRequest and IdentResult to Entity and changed all
access methods accordingly. IdentRequests can be created for persons
or companies, so a generic entity field serving both types is more
appropriate than just a single person field.

### Added
- Adding changelog.