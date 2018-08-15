# Design Decisions

#### Styling
We had a very simple approach to the styling; clean, tabbed spaces and whitespace between methods as well as
commenting if necessary. Each class had a Javadoc header explaining the purpose of it as well as the version
that it implemented and the Authors and date created.

#### Commands and Utilities
Our project was set up in two main files: Commands and Utilities. As weird as it may sound,
any classes inheriting off our command base was put into the commands section and anything that related to
the utilisation of the data surrounding the client was put into the utilities section, including blood type converter,
gender and the local date converter. We also had a client manager that used functions requested from the commands and
updated the client's information using the utilities section.

#### Testing
The testing of the project was simple: Unit testing. we would test whether the classes would produce
valid results, what would happen if a user forgot to enter a necessary part into the command, what processes
would occur if there were duplicate results entered, and any other valid acceptance or rejecting tests.


## Libraries

#### Sprint Boot
Spring boot provides us with a simple stand-alone framework to build our client-server architecture. Since Spring
focuses on the pluming of enterprise applications, it allows us to focus on the application-level business logic without
having to deal with ties to specific deployment environments.


#### Hibernate
> Fill this in


#### Google HTTP Client
> Fill this in


#### MySQL Connector
> Fill this in

#### Controls FX
* *CheckComboBoxes* - These were chosen as they reduce the number of clicks that users require to filter their lists. 
It allows them to select multiple regions/organs at once, rather than only one using a drop down box.
* *Notifications* - These were chosen as they provide a passive notification for users in the bottom corner of the screen
which is not intrusive and annoying for the system users.
* *Range Slider* - The slider provides an intuitive way for users to filter by an age range.


#### Cucumber
Cucumber is a testing library that we use for integration testing with our server. it allows our integration tests to be more readable for
comparing with acceptance criteria and a faster time to create.


#### Gson
Gson is a Java library that can be used to convert Java Objects into their JSON representation.
It can also be used to convert a JSON string to an equivalent Java object.
Gson can work with arbitrary Java objects including pre-existing objects that you do not have
source-code of.

There are a few open-source projects that can convert Java objects to JSON.
However, most of them require that you place Java annotations in your classes; something that you
can not do if you do not have access to the source-code. Most also do not fully support the use
of Java Generics. Gson considers both of these as very important design goals.


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

#### JLine 2
> Fill this in

#### Mockito
> Fill this in

