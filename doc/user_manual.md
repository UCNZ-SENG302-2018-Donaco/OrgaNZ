# User Manual

## Touch Screen Interface
The touch screen interface has been optimised to work on a Windows 10 touch screen device. To run the touch screen
version of the UI, enter the following command from the target directory:
```
java -jar organz-client-<version>.jar --ui=touch
```

This provides a multi-user interface which allows for multiple clinicians to work on the same device at once.

## Desktop Interface
The desktop interface can be run on any desktop environment. To run the application, the following command must be 
entered from the target directory:
```
java -jar organz-client-<version>.jar
```
Our server is hosted using docker on `http://csse-s302g7.canterbury.ac.nz`. If you wish to run a local server,
you can do this using:
```
java -jar organz-client-<version>.jar --host=<local-server>
```


If signed in as an admin, you will have access to the command line interface under the profile tab. More
details about the commands available are given on the CLI and typing `help` at any time will bring this back up.


## Default Clinician Login
Staff ID: `0`

Password: `clinician`

## Default Admin Login
Staff ID: `admin`

Password: `<No password>`
