# User Manual

Open JAR file

Here are the current usable Commands:

## Create a user

createuser [--force] -d=<dateOfBirth> -f=<firstName> -l=<lastName>
                  [-m=<middleNames>]


## Set the attributes of an existing user

setattribute [--bloodtype=<bloodType>] [--currentaddress=<address>]
                    [--dateofbirth=<dateOfBirth>] [--dateofdeath=<dateOfDeath>]
                    [--firstname=<firstName>] [--gender=<gender>]
                    [--height=<height>] [--lastname=<lastName>]
                    [--middlename=<middleName>] [--region=<region>]
                    [--weight=<weight>] -u=<uid>


## Set the organ donation choices of an existing user

setorganstatus [--bone] [--bonemarrow] [--connectivetissue] [--cornea]
                      [--heart] [--intestine] [--kidney] [--liver] [--lung]
                      [--middleear] [--pancreas] [--skin] -u=<uid>



## Deletes a user

deleteuser -u=<uid>



## Print all users with their cliental information.

printallinfo


## Print a single user with their cliental information.

printuser -u=<uid>



## Print a single user with their organ information.

printuserorgan -u=<uid>



## Save people to file

save


## Load people from file

load


## Get help on commands.

help [command]...

## Undo

undo

## Redo

redo