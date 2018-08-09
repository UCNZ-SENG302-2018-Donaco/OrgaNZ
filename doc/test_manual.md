# Manual Testing Procedures 
## Story 10 
#### AC1
On the landing page, click on the button labeled "Create Client Profile". Fill in the required text fields "First name",
"Last name" and "Date of Birth" ("Middle name" is optional). Select "Create". You will now be redirected to the 
view/edit client page where you can set any other attributes such as gender, region and height, for example.

#### AC2
Run AC1. Click on the "Register Organs" button on the left side bar. You will now be redirected to  register/deregister 
organs panel. To register an organ, select the check box so it shows a "tick" and the program will automatically
register the organ that you selected. To deregister, un-tick the organs check box. The program will automatically 
deregister the organ that you un-ticked.

#### AC4
To save the profile, go to the Clients' view/edit page and click on the button "Save Changes" on the bottom right. 
A pop-up should appear notifying the Client that their profile was saved successfully. 

#### AC5
When initially logged in, if you click on the undo or redo button, then a pop up message will display saying that 
"There are no left actions to undo/redo". When an attribute is changed ie blood-type from A- to AB+, you can undo 
the action even if it has been saved by clicking on the "Undo" button (AB+ will change to A- once the screen is 
refreshed). To revert the undo, click on the "Redo" button (A- will revert to AB+ once refreshed.).

#### AC6
To show the History of the particular Client, click on the "History" button on the side bar and a table 
should appear with the time, type and description log of each action that the Client has done.

#### AC7
Go to the "View Profile" page. If the Client has died, then the age of the Client at death should
be displayed at the "Age" label. To test this, you can set the "Date of Death" field to a time on or before the current
day. The calculated age will show where the "Age" label is.

#### AC8
The BMI is calculated with a simple formula of: (weight / (height^2)). To test this, edit and save the 
"Height" and "Weight" fields of a Client in the "View Profile" panel, and then the BMI will be shown next to the BMI label.


## Story 11
#### AC1
The steps on Story 10, AC1 can be used to create a Client profile. Once completed, you will be redirected to the 
"View Profile" panel. Clients are then able to edit their profile by clicking on any text field such as blood-type or
gender. To save their profile, click on the "Save Changes" button. Their edits are now saved. 

#### AC2
To log in, Click on the "Login as Client" button and find your Name or ID in the list. Click "Sign in". The Client can 
only edit their own page and not be able to access any other Client's profiles.

#### AC3
Multiple Clients are saved once they are created, so to verify that multiple Clients exist, go to "Login as Clients" and 
you should be able to see multiple profiles. To verify that their information is also stored, you can sign in as them
and view the saved data.

#### AC4
The Client (once signed in) is only able to view their profile as they are set to a Client status, not a Clinician or Admin
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
The program creates a default client with default attributes each time AppUI is run. To test: Perform the same manual 
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

## Story 25

####AC1
Log in as a clinician and on the side bar, navigate to the transplants section. If you haven't done yet,
you should load in a save file for all clients so that the table will be populated. To filter a region,
 click on the check box on top of the table, next to the "Region" label and select the desired regions to be
 filtered. The same can be applied to the Organs. Then press on the "Filter" button on the top right
 and the table should refresh itself with whatever has been selected.
 
####AC2
There should be no confusion as to how the filtering process works as most applications/websites 
use this method for filtering.

####AC3
You can also sort the table columns in alphabetical order (or by date/time for the date one) by clicking
on the head of the table. You can also choose to view a client by double-clicking on their row. This 
will navigate you to their client page.

## Story 32

#### AC3

Run the `@Ignore`d tests in `controller.administrator.StaffListControllerTest.java` and `controller.administrator.SearchClientsControllerAdministratorTest.java` in headful mode.
Run the `@Ignore`d test file `controller.clinician.ViewClinicianControllerTest.java`

## Story 46
Context - Viewing the clinician details page.
Logging in as an admin and viewing clinicians given the option for admins to search for all clinicians.
Logging in as a clinician only allows them to strictly see only their details. They cannot search for details about any other clinicians

As an admin, they can change the details of the default clinician

## Story 47
Go to the Organs To Donate page, and check there is a coloured countdown and it sorts by default, with the closest to expiring organs at the top