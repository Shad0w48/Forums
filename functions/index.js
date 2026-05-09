const { onValueCreated } = require("firebase-functions/v2/database");
const { setGlobalOptions } = require("firebase-functions/v2");
const admin = require("firebase-admin");

admin.initializeApp();

// This ensures your function is deployed in the correct region (usually us-central1)
setGlobalOptions({ region: "us-central1" });

// This function "listens" to your database v2 style.
exports.sendNotificationQueue = onValueCreated("/NotificationRequests/{requestId}", async (event) => {

    // 1. Grab the data your Android app pushed
    const requestData = event.data.val();
    if (!requestData) return null;

    const targetToken = requestData.token;
    const title = requestData.title;
    const message = requestData.message;

    // 2. Format the notification for FCM v1
    const payload = {
        token: targetToken,
        notification: {
            title: title,
            body: message
        }
    };

    try {
        // 3. Send the push notification
        await admin.messaging().send(payload);
        console.log("Success! Notification sent to:", targetToken);

        // 4. Delete the request from the database after sending
        return event.data.ref.remove();

    } catch (error) {
        console.error("Failed to send notification:", error);
        return null;
    }
});

// =========================================================================
// פונקציה חדשה: מאזינה לבקשות כלליות ושולחת התראה לכל המשתמשים
// =========================================================================
exports.sendGlobalNotification = onValueCreated("/GlobalNotifications/{requestId}", async (event) => {

    // 1. קבלת הנתונים שהאפליקציה כתבה (כותרת והודעה)
    const requestData = event.data.val();
    if (!requestData) return null;

    // 2. בניית ההודעה. שים לב שכאן משתמשים ב-topic במקום ב-token
    const payload = {
        topic: "all_users",
        notification: {
            title: requestData.title,
            body: requestData.message
        }
    };

    try {
        // 3. שליחת ההתראה לכל מי שרשום לערוץ "all_users"
        await admin.messaging().send(payload);
        console.log("Global Notification sent successfully!");

        // 4. מחיקת הבקשה מהמסד לאחר השליחה המוצלחת
        return event.data.ref.remove();

    } catch (error) {
        console.error("Failed to send global notification:", error);
        return null;
    }
});