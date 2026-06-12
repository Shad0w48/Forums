const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");

// Initialize the Firebase Admin SDK to allow our server to talk to the database
admin.initializeApp();

// ============================================================================
// FUNCTION 1: TARGETED NOTIFICATIONS (For Comments/Replies)
// This function wakes up whenever a new child is added to "/NotificationRequests/"
// ============================================================================
exports.sendNotificationQueue = onValueCreated({
    ref: "/NotificationRequests/{requestId}",
    instance: "forums-da48c-default-rtdb",
    region: "us-central1"
}, async (event) => {
    // 1. Grab the data that the Android app just pushed to the database
    const requestData = event.data.val();
    if (!requestData) return; // If it's empty, stop the function

    let fcmToken = requestData.token;

    // 2. TOKEN LOOKUP: If the app sent a User ID instead of a direct token,
    // we need to look into the 'users' database to find that specific user's token.
    if (!fcmToken && requestData.targetUserId) {
        try {
            // Go to users -> [Their ID] -> fcmToken
            const tokenSnapshot = await admin.database().ref(`users/${requestData.targetUserId}/fcmToken`).once('value');
            fcmToken = tokenSnapshot.val(); // Extract the actual token string
        } catch (error) {
            console.error("Error fetching user token from database:", error);
        }
    }

    // 3. SAFETY CHECK: If we still don't have a token, we can't send a message.
    if (!fcmToken) {
        console.log("No FCM token found for user ID:", requestData.targetUserId);
        return event.data.ref.remove(); // Delete the request so it doesn't get stuck in a loop
    }

    // 4. BUILD THE MESSAGE: Construct the exact payload that Google expects
    const payload = {
        token: fcmToken, // The exact phone to send the notification to
        notification: {
            title: requestData.title,     // e.g., "New Reply!"
            body: requestData.message     // e.g., "Ilya commented on your post"
        },
        android: {
            priority: "high", // Forces the phone to wake up and show the notification immediately
            notification: {
                channelId: "default_channel_id", // Must match the channel ID in Android Java code
                sound: "default"
            }
        }
    };

    // 5. SEND IT: Tell Firebase Cloud Messaging to deliver the payload
    try {
        await admin.messaging().send(payload);
        console.log("Personal notification sent successfully!");
        return event.data.ref.remove(); // Clean up the database after a successful send
    } catch (error) {
        console.error("FCM Error:", error);
        return event.data.ref.remove(); // Clean up even if it fails
    }
});


// ============================================================================
// FUNCTION 2: GLOBAL NOTIFICATIONS (For New Forums)
// This function wakes up whenever a new child is added to "/GlobalNotifications/"
// ============================================================================
exports.sendGlobalNotification = onValueCreated({
    ref: "/GlobalNotifications/{requestId}",
    instance: "forums-da48c-default-rtdb",
    region: "us-central1"
}, async (event) => {
    // 1. Grab the data from the database
    const requestData = event.data.val();
    if (!requestData) return;

    // 2. BUILD THE MESSAGE: Notice this uses 'topic' instead of 'token'
    const payload = {
        topic: "all_users", // Sends to ANY phone subscribed to this topic
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

    // 3. SEND IT
    try {
        await admin.messaging().send(payload);
        console.log("Global notification sent.");
        return event.data.ref.remove(); // Clean up the database
    } catch (error) {
        console.error("Global FCM Error:", error);
        return event.data.ref.remove(); // Clean up the database
    }
});
exports.deleteUserAccount = onValueCreated({
    ref: "/DeleteRequests/{requestId}",
    instance: "forums-da48c-default-rtdb",
    region: "us-central1"
}, async (event) => {
    // 1. Read the data sent from the Android app
    const requestData = event.data.val();

    // If the request is empty or missing a uid, stop immediately.
    if (!requestData || !requestData.uid) return;

    const targetUid = requestData.uid;

    try {
        // 2. Delete the user from Firebase Authentication
        // This permanently destroys their login credentials so they can never sign in again.
        await admin.auth().deleteUser(targetUid);
        console.log("Successfully deleted user from Auth:", targetUid);

    } catch (error) {
        // 3. Handle Auth Errors Gracefully
        // If the admin clicked twice and the user is already deleted, Firebase throws an error.
        // We catch it so the function doesn't crash, allowing the rest of the code to run.
        if (error.code === 'auth/user-not-found') {
            console.log("User was already deleted from Auth. Moving on...");
        } else {
            console.error("Error deleting user from Auth:", error);
        }
    }

    try {
        // 4. Mark the user as Banned in the Realtime Database
        // We update their profile in the 'users' node rather than deleting it.
        // This keeps their name visible on old posts/comments, but adds an 'isBanned' flag.
        await admin.database().ref(`/users/${targetUid}`).update({
            isBanned: true
        });
        console.log("Successfully marked user as banned in Database:", targetUid);

    } catch (error) {
        console.error("Error updating user's isBanned status in database:", error);
    } finally {
        // 5. Clean up the Request Queue
        // The 'finally' block is crucial. It ALWAYS runs, regardless of whether
        // the steps above succeeded or failed. This guarantees the request is deleted
        // so you don't end up with hundreds of stuck requests in your database.
        return event.data.ref.remove();
    }
});