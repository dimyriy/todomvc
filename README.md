## Spring RESTful backend service for [Angular TodoMVC](https://github.com/tastejs/todomvc/tree/gh-pages/examples/angularjs)
[![Build Status](https://travis-ci.org/dimyriy/todomvc.svg?branch=master)](https://travis-ci.org/dimyriy/todomvc)

### Running application
To run application simply execute
```bash
mvn jetty:run
```
and open <http://localhost:8080> in your favorite browser
### Testing
Thanks to [Travis](http://travis-ci.org), the results of unit and integration tests can be seen at [Travis CI
site](https://travis-ci.org/dimyriy/todomvc)
#### Unit testing
Following command runs unit-tests
```bash
mvn test
```
#### Integration testing
Following command runs integration tests
```bash
mvn integration-test -P tests-integration
```
