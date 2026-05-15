const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");

admin.initializeApp();

// Targeted Notifications
exports.sendNotificationQueue = onValueCreated({
    ref: "/NotificationRequests/{requestId}",
    instance: "forums-da48c-default-rtdb",
    region: "us-central1"
}, async (event) => {
    const requestData = event.data.val();
    if (!requestData) return;

    const payload = {
        token: requestData.token,
        notification: {
            title: requestData.title,
            body: requestData.message
        },
        android: {
            priority: "high", // Forces the phone to show the notification immediately
            notification: {
                channelId: "default_channel_id", // Must match your Java code exactly
                sound: "default"
            }
        }
    };

    try {
        await admin.messaging().send(payload);
        console.log("Personal notification sent.");
        return event.data.ref.remove();
    } catch (error) {
        console.error("FCM Error:", error);
    }
});

// Global Notifications
exports.sendGlobalNotification = onValueCreated({
    ref: "/GlobalNotifications/{requestId}",
    instance: "forums-da48c-default-rtdb",
    region: "us-central1"
}, async (event) => {
    const requestData = event.data.val();
    if (!requestData) return;

    const payload = {
        topic: "all_users",
        notification: {
            title: requestData.title,
            body: requestData.message
        },
        android: {
            priority: "high",
            notification: {
                channelId: "default_channel_id",
                sound: "default"
            }
        }
    };

    try {
        await admin.messaging().send(payload);
        console.log("Global notification sent.");
        return event.data.ref.remove();
    } catch (error) {
        console.error("Global FCM Error:", error);
    }
});