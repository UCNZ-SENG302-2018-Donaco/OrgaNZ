# User Manual

Open JAR file

Here are the current usable Commands:

## Create a user

createuser [--force] -d=\<dateOfBirth> -f=\<firstName> -l=\<lastName>
                  [-m=\<middleNames>]


## Set the attributes of an existing user

setattribute [--bloodtype=\<bloodType>] [--currentaddress=\<address>]
                    [--dateofbirth=\<dateOfBirth>] [--dateofdeath=\<dateOfDeath>]
                    [--firstname=\<firstName>] [--gender=\<gender>]
                    [--height=\<height>] [--lastname=\<lastName>]
                    [--middlename=\<middleName>] [--region=\<region>]
                    [--weight=\<weight>] -u=\<uid>


## Set organ donation choices

setorganstatus [--bone] [--bonemarrow] [--connectivetissue] [--cornea]
                      [--heart] [--intestine] [--kidney] [--liver] [--lung]
                      [--middleear] [--pancreas] [--skin] -u=\<uid>



## Deletes a user

deleteuser -u=\<uid>



## Print all users with their personal information.

printallinfo


## Print a single users information

printuser -u=\<uid>



## Print a single user with their organ information.

printuserorgan -u=\<uid>



## Save clients to file

save


## Load clients from file

load

* This will override all current data

## Create a clinician

createclinician [-a=\<workAddress\>] -f=\<firstName\> -l=\<lastName\>
                       [-m=\<middleNames\>] [-p=\<password\>] [-r=\<region\>]
                       
* First name and last name are required

## Update a clinician

modifyclinician [-a=\<workAddress\>] [-f=\<firstName\>] [-l=\<lastName\>]
                       [-m=\<middleNames>] [-p=\<password>] [-r=\<region>] -s=\<id>
                       

## Delete a clinician

deleteclinician -s=\<id>

* Note that the default clinician (id = 0) cannot be deleted

## Get help on commands.

help [command]...

Displays detailed information on the given [command]

## Undo

undo

## Redo

redo