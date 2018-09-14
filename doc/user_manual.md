# OrgaNZ User Manual

## Running the app

### Touchscreen interface
The touch screen interface has been optimised to work on a Windows 10 touch screen device. To run the touch screen
version of the UI, enter the following command from the target directory:
```
java -jar organz-client-<version>.jar --ui=touch
```

This provides a multi-user interface which allows for multiple clinicians to work on the same device at once.

### Desktop interface
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

## Logging in

To use the app, you will first need to log in.

### As a client

Select Log in and choose a client, or select Sign up and create a new client.

### As a staff member (clinician or administrator)

Select Staff login and enter a username and and password. The username and password for default staff members are as follows:

#### Default clinician login
Staff ID: `0`

Password: `clinician`

#### Default administrator login
Staff ID: `admin`

Password: `<No password>`

# Pages

Some pages (coming soon: all pages) in our app are documented below.
At the top of each section, who can access the page, and how to get to the page, are outlined.

Note that all pages available to clinicians are also available to administrators.
Pages available to clients are also available to clinicians (and administrators), but only when viewing a particular client.
Some pages available to clients only have their full functionality unlocked when viewing as a clinician (or administrator).

### View Client page

Available to: clients. How to access: Client → View Client Profile.

The view client page is used to view and edit the details of a client.
These details can be edited:

* name (first, middle, last, and preferred)
* date of birth
* gender (birth and identity)
* profile photo
* location (address, region, country, and hospital)
* blood type
* height
* weight
* death details (if marked as dead)
  * date
  * time
  * country
  * region
  * city
  
And these details can be viewed:

* Age
* BMI
* Time and date profile was created
* Time and ate profile was last modified


### Settings page

Available to: administrators. How to access: File → Settings.

The settings page can be used to set which countries clients can register as living in, and what hospitals have which transplant programs.

To set which countries clients can register as living in, tick the relevant countries, then click Apply.

To set what hospitals have which transplant programs, select the relevant hospital, tick the relevant organs, the click Apply.

### Command line interface page

Available to: administrators. How to access: Profile → CLI.

More details about the commands available are given on the CLI, and typing `help` at any time will bring this back up.

