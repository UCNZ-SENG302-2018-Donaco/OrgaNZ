# User Manual

Open JAR file

Here are the current usable Commands:

## Create a client
```
createclient [--force] -d=\<dateOfBirth> -f=\<firstName> -l=\<lastName>
                  [-m=\<middleNames>]
```

## Set the attributes of an existing client
```
setattribute [--bloodtype=\<bloodType>] [--currentaddress=\<address>]
                    [--dateofbirth=\<dateOfBirth>] [--dateofdeath=\<dateOfDeath>]
                    [--firstname=\<firstName>] [--gender=\<gender>]
                    [--height=\<height>] [--lastname=\<lastName>]
                    [--middlename=\<middleName>] [--region=\<region>]
                    [--weight=\<weight>] -u=\<uid>
```

## Set organ donation choices of an existing client
```
setorganstatus [--bone] [--bonemarrow] [--connectivetissue] [--cornea]
                      [--heart] [--intestine] [--kidney] [--liver] [--lung]
                      [--middleear] [--pancreas] [--skin] -u=\<uid>
```

## Creates an organ request for a user.
```
requestorgan [-o=\<organType>] -u=\<uid>
```
## Resolves a users organ request.
```
resolveorgan [-m=\<message>] -o=\<organType> -r=<\resolveReason> -u=\<uid>
```
## Delete a client
```
deleteclient -u=\<uid>
```

## Print all clients with their personal information
```
printallinfo
```

## Print a single clients information
```
printclient -u=\<uid>
```

## Print a single client with their organ information

## Print a single user with their organ information.
```
printuserorgan -u=\<uid>
```


## Save clients to file
```
save
```

## Load clients from file
```
load
```
* This will override all current data

## Get help on commands.
```
help [command]...
```
## Create a clinician
```
createclinician [-a=\<workAddress\>] -f=\<firstName\> -l=\<lastName\>
                       [-m=\<middleNames\>] [-p=\<password\>] [-r=\<region\>] -s=\<id>
```                   
* First name, last name, and staff ID are required

## Update a clinician
```
modifyclinician [-a=\<workAddress\>] [-f=\<firstName\>] [-l=\<lastName\>]
                       [-m=\<middleNames>] [-p=\<password>] [-r=\<region>] -s=\<id>
```
## Delete a clinician
```
deleteclinician -s=\<id>
```
* Note that the default clinician (id = 0) cannot be deleted

## Get help on commands
```
help [command]
```
Displays detailed information on the given [command]

## Undo
```
undo
```
## Redo
```
redo
```
## SQL 
##### View all tables:
```
sql show tables
```
##### General SQL
```
sql [readonly statement]
```
##### Read only SQL statements are allow to be executed from the admins command line. When using quotation marks in the SQL, they must be escaped with a "\\" character. e.g.
```
sql "SELECT \"firstName\" FROM Client"
```

