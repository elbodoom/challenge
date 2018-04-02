# challenge
The Asset Management Digital Challenge

**This implementation only works in a single machine and it is not possible to scale due the repository has been storing the accounts in memory** 

### Improvements that should be done in the code before going to Production
#### Archictecture
* The first thing I would change in the code is to split this project and small ones to enable microservices architecture, for an example, one project to _Notification Service_, another one to _Repository Service_, and so on
* After such re-structuring I would use SpringCloud to build the microservices infra-structure, using Discovery Service (_Eureka_) to facilitate the service location, Feign Client to facilitate the integration among the microservices and Spring Cloud Config Server to centralize the configuration/properties
* Implement a reverse-proxy (we could use Zuul from Spring Cloud Netflix) to centralize some required features like Authorization and Authentication
* Unfortunately I have no much experience with Event Sourcing model, but, I think such solution should be implemented following, at least, the Event Sourcing model. All transfer/transaction would be handled as an event, with that it would be possible to recreate the Account balance state in any point of time   

##### Repository Service
* Replace the in-memory account storage to a Database solution
* Replace the return type of AccountsRepository#getAccount from Account to Optional\<Account>

##### Account Service
* After the replacement of the storage, it would be necessary to change the AccountService#executeTransfer to be executed in an Atomic transaction. If something wrong happens either in withdraw or in deposit, the balance state should be not changed.  

#### API
* I would review all implemented APIs to raise properly exceptions, instead to return a success code for all scenrarios. For an example in **AccountsController#getAccount**, every time a success code is returned even if an Account does not exist, in this case I would raise a Exception with Http Status Code = 404 with a propertly message.
* I would document the APIs using Swagger, to be more specific, using Springfox api 
* I would create REST representations using HATEOAS principle, as it is done in **AccountsController#transfer**, for an example creating a reference to **TransferReceipt** using **location** header parameter in the response 

#### Tests
* I would improve the tests creating different spring profiles to execute unit and integration tests
* Review all created tests to tell Spring to load only the classes used in the test. In the current implementation Spring is loading all classes in the project
