# User Manual

Open JAR file

Here are the current usable Commands:

## Create a client.
createclient [--force] -d=<dateOfBirth> -f=<firstName> -l=<lastName>
                    [-m=<middleNames>]

## Create a clinician.
createclinician [-a=<workAddress>] -f=<firstName> -l=<lastName>
                       [-m=<middleNames>] [-p=<password>] [-r=<region>]

## Set the attributes of an existing client.
setattribute [--bloodtype=<bloodType>] [--currentaddress=<address>]
                    [--dateofbirth=<dateOfBirth>] [--dateofdeath=<dateOfDeath>]
                    [--firstname=<firstName>] [--gender=<gender>]
                    [--height=<height>] [--lastname=<lastName>]
                    [--middlename=<middleName>] [--region=<region>]
                    [--weight=<weight>] -u=<uid>

## Modify the attribute of an existing clinician
modifyclinician [-a=<workAddress>] [-f=<firstName>] [-l=<lastName>]
                       [-m=<middleNames>] [-p=<password>] [-r=<region>] -s=<id>

## Set the organ donation choices of an existing client.
setorganstatus [--bone] [--bonemarrow] [--connectivetissue] [--cornea]
                      [--heart] [--intestine] [--kidney] [--liver] [--lung]
                      [--middleear] [--pancreas] [--skin] -u=<uid>

## Delete a client.
deleteclient -u=<uid>

## Delete a clinician.
deleteclinician -s=<id>

## Print all clients with their personal information.
printallinfo

## Print all clients with their organ donation status.
printallorgan

## Print a single client with their personal information.
printclientinfo -u=<uid>

## Print a single client with their organ information.
printclientorgan -u=<uid>

## Print a single clients update history.
getchanges -u=<uid>

## Save clients to file
save

## Load clients from file
load

## Get help on commands.
help [command]

## Undo a change.
undo

## Redo an undone change.
redo