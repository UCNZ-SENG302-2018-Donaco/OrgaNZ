# User Manual

Open JAR file

Here are the current usable Commands:

## Create a user
```
createuser [--force] -d=<dateOfBirth> -f=<firstName> -l=<lastName>
                  [-m=<middleNames>]
```

## Set the attributes of an existing user
```
setattribute [--bloodtype=<bloodType>] [--currentaddress=<address>]
                    [--dateofbirth=<dateOfBirth>] [--dateofdeath=<dateOfDeath>]
                    [--firstname=<firstName>] [--gender=<gender>]
                    [--height=<height>] [--lastname=<lastName>]
                    [--middlename=<middleName>] [--region=<region>]
                    [--weight=<weight>] -u=<uid>
```

## Set the organ donation choices of an existing user
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
## Deletes a user
```
deleteuser -u=<uid>
```


## Print all users with their personal information.
```
printallinfo
```

## Print a single user with their personal information.
```
printuser -u=<uid>
```


## Print a single user with their organ information.
```
printuserorgan -u=<uid>
```


## Save clients to file
```
save
```

## Load clients from file
```
load
```

## Get help on commands.
```
help [command]...
```
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
sql SELECT \"firstName\" FROM Client
```

