# SENG302 Team 700: Donaco
![OrgaNZ logo](https://eng-git.canterbury.ac.nz/seng302-2018/team-700/uploads/aa0996125ee57a65f6db44f06e51d034/ORGANZ.png "OrgaNZ Logo")

[![OrgaNZ demo](https://olliechick.co.nz/images/organz.jpg "OrgaNZ demo")](https://youtu.be/mG5o0Ly3CCo)

## Team Info
Developers:
* Dylan Carlyle <dca87@uclive.ac.nz>
* Elizabeth Wilson <ewi32@uclive.ac.nz>
* Alex Tompkins <ato47@uclive.ac.nz>
* Jack Steel <jes143@uclive.ac.nz>
* James Toohey <jto59@uclive.ac.nz>
* Matthew Smit <mjs351@uclive.ac.nz>
* Ollie Chick <och26@uclive.ac.nz>
* Tom Kearsley <tke29@uclive.ac.nz>

Scrum Master:
* Liam Beckett <lsb35@uclive.ac.nz>

## Product Info
Title: **OrgaNZ**

Version: **Sprint 7 (Final) Release**

Description: **This project has been designed to increase the deceased organ donation rates in New Zealand.
It provides people in New Zealand with a system they can use to register as organ donors, or request organ transplants.**

## External Libraries

This application makes use of a number of external libraries, including:
* Spring Boot
* Jackson
* PicoCLI
* ControlsFX
* Hibernate
* MySqlConnector
* TUIOFX

And also makes use of these external libraries for testing purposes:
* JUnit 4
* Mockito
* Cucumber
* TestFX

Design decisions about our external libraries used can be found under our `doc/design_decisions.md` file.

All applicable copyright notices and licenses for the libraries used are provided in the `LICENSES.txt` file.

## Usage

Please refer to the documentation found in [`doc/User_Manual.pdf`](../master/doc/User_Manual.pdf) to find out more on how to use the application.

For more information on the design decisions that were made, please refer to the documentation found in
[`doc/design_decisions.md`](../master/doc/design_decisions.md).

To see an example of a file that loads in the user data, please direct your attention to
[`doc/examples/11300_clients.csv`](../master/doc/examples/11300_clients.csv).

Note that the server and database are no longer running, so if you want to run the program, you will have to run the server, specify where this server is when you run the client, and change [the hibernate config file](https://github.com/UCNZ-SENG302-2018-Donaco/OrgaNZ/blob/master/server/src/main/resources/hibernate.cfg.xml) to point to a database.

## Demonstration

For a quick video showing the app in action, see https://youtu.be/mG5o0Ly3CCo.
