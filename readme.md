## READ ME
------
##### Version 1
------
getpreference is used as the primary storage for persistence. Session keys such as IMG_LAST and IS_FROM_CAMERA
are used as session persistence, in case the activity gets destroyed on rotation. 

##### Version 2
------
This version includes all of the UI-shells for future implementation. We start by enabling the ActionBar which for now does
not contain any menu options, but does present the title. On the main activity, we have sliding tabs that present three panes.
The first is the start pane which will allow users to access the necessary options to start an exercise activity. History pane is
left blank for now. The Setting pane will allow users to set global app fields.

Edge cases:
* Privacy setting persists
* Orientation of gallery-picked image persists
* Units preferred persists
* Input Type and Activity Type persists
* List view is presented correctly upon rotation


##### Version 3
------
This version includes the sqlite implementation with usage of AsyncLoader for history fragment view
and the loading of individual exercise entry. A Runnable was also used to delete entries.

Edge Cases:
* Units are converted
* Units from the preferences are used when inserting
* Units from the preferences are used when displaying
* end to end unit conversion

##### Version 4
------
This version includes the Map Location Manager, MapView, NotificationManager, and Services.
(worked with Richard Cornew)

Edge Cases:
* Map is redrawn on onCreate
* LocationService sends new locations via Message Handler
* GPS and Automatic saves correctly in the same format as Manual entry
* Notification leads back to Map Activity correctly.



