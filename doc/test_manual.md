# Manual Testing Procedures 
## Story 10 
#### AC1
On the landing page, click on the button labeled "Create Donor Profile". Fill in the required text fields "First name",
"Last name" and "Date of Birth" ("Middle name" is optional). Select "Create". You will now be redirected to the 
view/edit donor page where you can set any other attributes such as gender, region and height, for example.

#### AC2
Run AC1 or if you have an existing donor profile, click on the "Login as Donor" button and navigate the list until you
find your name, then click "Sign In". Click on the "Register Organs" button on the left side bar. You will now be 
redirected to  register/deregister organs panel. To register an organ, select the check box so it shows a "tick" 
and the program will automatically register the organ that you selected. To deregister, un-tick the organs check box. 
The program will automatically deregister the organ that you un-ticked.

#### AC3

#### AC4
To save the profile, go to the Donors' view/edit page and click on the button "Save Changes" on the bottom right. 
A pop-up should appear notifying the Donor that their profile was saved successfully. 

#### AC5
To test that the undo and redo buttons work, first sign in to the Donor's account. When initially logged in, if you
click on the undo or redo button, then a pop up message will display saying that "There are no left actions to undo/redo".
When an attribute is changed ie blood-type from A- to AB+, you can undo the action even if it has been saved by clicking 
on the "Undo" button (AB+ will change to A- once the screen is refreshed). To revert the undo, click on the "Redo" button
 (A- will revert to AB+ once refreshed.).

#### AC6
To show the History of the particular Donor, first log in to the application using the "Login as Donor" button. Then,
click on the "History" button on the side bar and a table should appear with the time, type and description log of each
action that the Donor has done.

#### AC7
Login as a Donor, and go to the "View Profile" page. If the Donor has died, then the age of the Donor at death should
be displayed at the "Age" label. To test this, you can set the "Date of Death" field to a time on or before the current
day. The calculated age will show where the "Age" label is.

#### AC8
The BMI is calculated with a simple formula of: (weight / (height^2)). To test this, edit and save the 
"Height" and "Weight" fields of a Donor in the "View Profile" panel, and then the BMI will be shown next to the BMI label.

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