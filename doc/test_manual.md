# Manual Testing Procedures 
## Story 10 
#### AC1
On the landing page, click on the button labeled "Create Donor Profile". Fill in the required text fields "First name",
"Last name" and "Date of Birth" ("Middle name" is optional). Select "Create". You will now be redirected to the 
view/edit donor page where you can set any other attributes such as gender, region and height, for example.

#### AC2
Run AC1. Click on the "Register Organs" button on the left side bar. You will now be redirected to  register/deregister 
organs panel. To register an organ, select the check box so it shows a "tick" and the program will automatically
register the organ that you selected. To deregister, un-tick the organs check box. The program will automatically 
deregister the organ that you un-ticked.

#### AC4
To save the profile, go to the Donors' view/edit page and click on the button "Save Changes" on the bottom right. 
A pop-up should appear notifying the Donor that their profile was saved successfully. 

#### AC5
When initially logged in, if you click on the undo or redo button, then a pop up message will display saying that 
"There are no left actions to undo/redo". When an attribute is changed ie blood-type from A- to AB+, you can undo 
the action even if it has been saved by clicking on the "Undo" button (AB+ will change to A- once the screen is 
refreshed). To revert the undo, click on the "Redo" button (A- will revert to AB+ once refreshed.).

#### AC6
To show the History of the particular Donor, click on the "History" button on the side bar and a table 
should appear with the time, type and description log of each action that the Donor has done.

#### AC7
Go to the "View Profile" page. If the Donor has died, then the age of the Donor at death should
be displayed at the "Age" label. To test this, you can set the "Date of Death" field to a time on or before the current
day. The calculated age will show where the "Age" label is.

#### AC8
The BMI is calculated with a simple formula of: (weight / (height^2)). To test this, edit and save the 
"Height" and "Weight" fields of a Donor in the "View Profile" panel, and then the BMI will be shown next to the BMI label.


## Story 11
#### AC1
The steps on Story 10, AC1 can be used to create a Donor profile. Once completed, you will be redirected to the 
"View Profile" panel. Donors are then able to edit their profile by clicking on any text field such as blood-type or
gender. To save their profile, click on the "Save Changes" button. Their edits are now saved. 

#### AC2
To log in, Click on the "Login as Donor" button and find your Name or ID in the list. Click "Sign in". The Donor can 
only edit their own page and not be able to access any other Donor's profiles.

#### AC3
Multiple Donors are saved once they are created, so to verify that multiple Donors exist, go to "Login as Donors" and 
you should be able to see multiple profiles. To verify that their information is also stored, you can sign in as them
and view the saved data.

#### AC4
The Donor (once signed in) is only able to view their profile as they are set to a Donor status, not a Clinician or Admin
status.

## Story 12
#### AC1
Run AppUI > Log in as a Clinician with Staff Id = 0 and password = admin > Update the middle name to "test" and save 
these changes > Check in the history tab that this has been made > Click undo > This should show an UNDO action at the
bottom of the history table > Navigate back to View Clinician > If the default details are shown, the test passes,
otherwise it fails. Continue with this test for Story 14;



## Story 13
#### AC1
Following Story 13's manual test, navigate to the history page > Click Redo > This should come up at the bottom of the 
history table showing a REDO action has occurred > Navigate to the View Clinicians page > the middle name should now 
show "test" in this field for the test to pass, otherwise it fails.



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

The remaining fields must be blank. If all these attributes are correct, the test passes.



## Story 15
Note: All clinician updates are performed on the default clinician.

#### AC1
Log into he default clinician > Upon successful login, the passwords field should be set blank > Change the password to test >
Check at the bottom of the history tab that the type column displays "UPDATE CLINICIAN" beside the time the password was
changed > Log out > Log in with the 'admin' password > A popup box saying that the StaffId and password do not match 
should appear > Log in with'test' password > This should now take you back to the 'View/Edit Clinician' page for the 
default clinician in order for this test to pass.

#### AC2
Log into the default clinician > Delete all fields from each text box (Region can remain unspecified & note that you 
cannot edit the StaffID field) > First Name and last name should appear red, as they both have invalid inputs - they are mandatory fields >
The password is blank by default as it contains the current password > Re-enter each text field with test String values, but
ensure the password is set to a memorable name, e.g. 'test' > Log out > Log in with StaffId = 0 and your new password > These
updates you made now must show in the 'View/Edit Clinician' page for this test to pass.

#### AC3
* After performing tests for either AC1 or AC2, check that the Last Modified date is set to the time when you last updated
the default Clinician. If so, this first test passes.
* Terminate any running AppUI programs > Run AppUi > Log into the default clinician > If Last Modified is set to "Not yet modified"
the test passes. Otherwise it fails.



## Story 16



## Story 17