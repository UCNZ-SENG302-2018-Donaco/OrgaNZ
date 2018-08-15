# Design Decisions

**Styling**

We had a very simple approach to the styling; clean, tabbed spaces and whitespace between methods as well as
commenting if necessary. Each class had a Javadoc header explaining the purpose of it as well as the version
that it implemented and the Authors and date created.

**Commands and Utilities**

Our project was set up in two main files: Commands and Utilities. As weird as it may sound,
any classes inheriting off our command base was put into the commands section and anything that related to
the utilisation of the data surrounding the client was put into the utilities section, including blood type converter,
gender and the local date converter. We also had a client manager that used functions requested from the commands and
updated the client's information using the utilities section.

**Testing**

The testing of the project was simple: Unit testing. we would test whether the classes would produce
valid results, what would happen if a user forgot to enter a necessary part into the command, what processes
would occur if there were duplicate results entered, and any other valid acceptance or rejecting tests.


**APIs used**


**Picocli**

For the structure of the CLI, we decided to use the picocli API which is specific in creating a clean
command line interface in Java applications.

Picocli is a one-file framework for creating Java command line applications with almost zero code.
It supports a variety of command line syntax styles including POSIX, GNU, MS-DOS and more.
It generates highly customizable usage help messages with ANSI colors and styles.
Picocli-based applications can have command line TAB completion showing available options,
option parameters and subcommands, for any level of nested subcommands.


**Gson**

Gson is a Java library that can be used to convert Java Objects into their JSON representation.
It can also be used to convert a JSON string to an equivalent Java object.
Gson can work with arbitrary Java objects including pre-existing objects that you do not have
source-code of.

There are a few open-source projects that can convert Java objects to JSON.
However, most of them require that you place Java annotations in your classes; something that you
can not do if you do not have access to the source-code. Most also do not fully support the use
of Java Generics. Gson considers both of these as very important design goals.



**ControlsFX (More notably, CheckComboBoxes)**

CheckComboboxes were chosen as they reduce the number of clicks that users require to filter their lists. It allows them to select
multiple regions/organs at once, rather than only one using a drop down box. They have been implemented in a number
of clinician windows including the list of transplant requests, searching clients and organs to donate.