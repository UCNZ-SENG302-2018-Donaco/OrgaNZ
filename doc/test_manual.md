# Manual Testing Procedures 

## Story 14
#### AC1
Run AppUI > Navigate to 'Login as Clinician' > Enter the default clinician details; StaffID - 0, Password - admin.
The test is successful if a popup box saying 'login successful' appears, and the user is navigated to the view/edit 
clinician page.

#### AC2
The program creates a default donor with default attributes each time AppUI is run. To test: Perform the same manual 
test for AC1 multiple times, and the success popup box must appear each time to consider the test successful.

#### AC3
Ensure the default StaffID's field is set to 0, and the admin's password is 'admin'. This is the generic admin which
should never have these attributes change. Staff ID's have to be an integer and can not already exist in the system, 
therefore it will never get overwritten or replaced.

#### AC4
Run AC1 > The successful login must take you to the 'View/Edit Clinician' page. All attributes of the default clinician
must be displayed. 
These include:
* First name - admin
* Last name - admin
* Work address - admin
* Staff ID - 0
* Region - Unspecified
* Creation Date - {The date & time of when AppUI was ran in the current session}
* Last Modified - Not yet modified

The remaining fields must be blank.

## Story 15
Note: All clinician updates are performed on the default clinician.
#### AC1
Log into he default clinician > Upon successful login, the passwords field should be set blank.