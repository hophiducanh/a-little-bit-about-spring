## References
- [Firebase Cloud Messaging in React: A Comprehensive Guide](https://medium.com/simform-engineering/firebase-cloud-messaging-in-react-a-comprehensive-guide-b5e325452f97)
- [[ReactJS] Push notification sử dụng Firebase Cloud Messaging](https://viblo.asia/p/reactjs-push-notification-su-dung-firebase-cloud-messaging-yZjJYE9XJOE#_a-foreground-4)
- [Push notifications with React and Firebase](https://blog.logrocket.com/push-notifications-react-firebase/)

----
----

## Create App

### `npx create-react-app spring-fcm`

----
----

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

> ### If process.env does not work, hard code the value 

====
====

## what is the difference between foreground push notification vs background push notification for web
The key difference between foreground and background push notifications for web lies in the state of the web application when the push notification arrives:

**Foreground Push Notification:**

* **App state:** The web application is actively visible and in use by the user.
* **Delivery:** The push notification is delivered directly to the application's JavaScript code.
* **Handling:** The application itself is responsible for displaying the notification using the `onMessage` event listener. This allows for more customization of the notification's appearance and behavior.
* **Example:** You're browsing a social media website and receive a notification about a new message. The application can instantly display the notification popup without any additional steps.

**Background Push Notification:**

* **App state:** The web application is minimized, closed, or in the background.
* **Delivery:** The push notification is delivered to the service worker, a separate script that runs in the background even when the app is not actively in use.
* **Handling:** The service worker uses the `onBackgroundMessage` event listener to handle the notification. It can then display a notification using the `showNotification` method, though the notification's appearance and behavior might be limited by the browser.
* **Example:** You receive a notification about a new email while your email app is minimized. The service worker intercepts the notification and displays a basic notification on your screen.

**In essence:**

* Foreground notifications offer more flexibility and customization since the app directly handles them.
* Background notifications ensure users are informed even when the app isn't actively used, but with potentially less control over the notification's look and feel.

-----
-----

## why react app firebase require firebase-messaging-sw.js for foreground push notification, not background only?

The `firebase-messaging-sw.js` service worker file is a crucial part of the Firebase Cloud Messaging (FCM) system, especially for web applications built with React or any other framework. It's responsible for handling push notifications both in the foreground and background. Here's why it's necessary even for foreground notifications:

1. **Service Worker Lifecycle and Control**: Service workers, including `firebase-messaging-sw.js`, operate in the background and are separate from the web page. They act as a proxy between the browser and the network. Even for foreground notifications, the service worker is responsible for receiving messages from the Firebase servers and then delivering these messages to the web application.

2. **Background Processing**: While it's more evident why you need a service worker for background notifications (as the service worker can receive messages even when the web app is not active in a browser tab), for foreground notifications, it helps in processing and handling the notification data consistently, whether the app is in the foreground or background.

3. **Consistency and Reliability**: Using the service worker, Firebase ensures a consistent and reliable mechanism to handle notifications. It abstracts the complexity of managing different browser tabs and notification delivery, making sure that messages are delivered effectively, no matter the state of the application.

4. **Performance and Resource Management**: Service workers run in a separate thread from the main browser UI and are optimized for low resource consumption. This is beneficial for performance, as it means that push notification handling doesn't block or slow down the main thread of your web application.

5. **Compliance with Web Standards**: Utilizing service workers for push notifications is a standard practice and follows the web push protocol. This standardization ensures broad compatibility across modern browsers and provides a secure environment for message handling.

In summary, even though it might seem that a service worker like `firebase-messaging-sw.js` would only be necessary for handling notifications when a React app is in the background, it actually plays a critical role in managing push notifications in both foreground and background scenarios. It ensures a consistent, reliable, and efficient mechanism for receiving and processing notifications, following best practices and standards of the web platform.

> A service worker is necessary for handling both foreground and background push notifications with Firebase Cloud Messaging (FCM) on web platforms

----
----

## [Can I use firebase cloud messaging without notification permission? (Javascript)](https://stackoverflow.com/questions/53403792/can-i-use-firebase-cloud-messaging-without-notification-permission-javascript?noredirect=1&lq=1)

> The problem is that Firebase Messaging is only using 1 method to deliver notifications. That is the **Push API specification** spec, and that specification (wrongly and unfortunately) does not allow a service worker to receive messages without the user allowing an unrelated permission to show notifications.

> The fix would be for the Firebase Messaging team to provide a different way to deliver messages to active web pages -- long polling, or websockets.

> But it would be extra work for them, and may be not enough people are requesting it.
