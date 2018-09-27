# Design Decisions

#### Styling
We had a very simple approach to the styling; clean, tabbed spaces and whitespace between methods as well as
commenting if necessary. Each class had a Javadoc header explaining the purpose of it as well as the version
that it implemented and the Authors and date created.

## External Libraries

#### Spring Boot
Spring boot provides us with a simple stand-alone framework to build our client-server architecture. Since Spring
focuses on the pluming of enterprise applications, it allows us to focus on the application-level business logic without
having to deal with ties to specific deployment environments.


#### Hibernate
Hibernate allows us to persistently store our data without needing to worry too much about the details of database access.
It is an object-relational mapper, so we just set up the relationships between objects and then Hibernate reflects this in
the database's table structure and whenever we need to query data. 


#### Google HTTP Client
This library provides a simple and robust bare bones HTTP Client, initially used for external API queries.
It now also backs the Spring REST service used for client server architecture.


#### MySQL Connector
This library provides MySQL direct database access, used for the SQL command which requires direct access, as opposed
to the Hibernate higher level library.

#### Controls FX
* *CheckComboBoxes* - These were chosen as they reduce the number of clicks that users require to filter their lists. 
It allows them to select multiple regions/organs at once, rather than only one using a drop down box. They have been implemented 
in a numberof clinician windows including the list of transplant requests, searching clients and organs to donate.
* *Notifications* - These were chosen as they provide a passive notification for users in the bottom corner of the screen
which is not intrusive and annoying for the system users.
* *Range Slider* - The slider provides an intuitive way for users to filter by an age range.


#### Cucumber
Cucumber is a testing library that we use for integration testing with our server. it allows our integration tests to be more readable for
comparing with acceptance criteria and a faster time to create.


#### Jackson
Jackson is a Java library that can be used to convert Java Objects into their JSON representation.
It can also be used to convert a JSON string to an equivalent Java object. Jackson allows us to use JSON both
for saving/loading data and for transferring our objects between the client and server, easily deserializing them at
either end.


#### Picocli
For the structure of the CLI, we decided to use the picocli API which is specific in creating a clean
command line interface in Java applications.

Picocli is a one-file framework for creating Java command line applications with almost zero code.
It supports a variety of command line syntax styles including POSIX, GNU, MS-DOS and more.
It generates highly customizable usage help messages with ANSI colors and styles.
Picocli-based applications can have command line TAB completion showing available options,
option parameters and sub-commands, for any level of nested sub-commands.


#### JJWI
jjwt is a Java JSON Web Token. We use it to assist with our client-server architectures authentication. It gives us assurance that
token generation is handled properly.


#### Commons CSV
This library was used to save time on creating our own csv parser. This provides a robust csv parser.

#### TestFX
TestFX provides us with a front-end testing framework that reduces the amount of manual testing we have to perform.
Basic functionality of the pages forms can be tested to ensure new development of our project isn't breaking existing
functionality.


#### Mockito
This robust testing library allows us to ensure our tests only focus on core logic. This is used to mock server
and external influences into expected results.

