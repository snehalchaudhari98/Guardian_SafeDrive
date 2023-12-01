# Guardian SafeDrive (Branch: Shishir_ArchanaSrikanth)

## Steps to demo Notification system

### In-App Notifications

1. Run the Android application
2. Navigate to DM- notifications tab by clicking on the menu icon
3. Click on any of the first three buttons
4. If Notification permissions aren't given to the application, you're prompted to give permission
5. Clicking on any of the messages gives out a unique notification with a title, and body.

### Firebase Cloud Messaging Notifications

1. Follow steps 1-3 as per previous section
2. Click on any of the last two buttons
3. First click of the button fires the FCM engine and triggers a push notification after about 30 seconds
4. Every subsequent click of the last two buttons gives push notifications which are being triggered through the Firebase Cloud Messaging API v1. 