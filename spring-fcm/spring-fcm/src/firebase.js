// Import the functions you need from the SDKs you need
import {initializeApp} from "firebase/app";
import {getMessaging, onMessage, getToken} from "firebase/messaging";

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
    authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
    projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
    storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
    appId: process.env.REACT_APP_FIREBASE_APP_ID,
    measurementId: process.env.REACT_APP_FIREBASE_MEASUREMENT_ID,
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
// Initialize Firebase Messaging

const messaging = getMessaging(app);

console.log(getMessaging(app));

export const onMessageListener = () =>
    new Promise((resolve) => {
        onMessage(messaging, (payload) => {
            console.log(payload);
            resolve(payload);
        });
    });

export const getFcmRegistrationToken = async () => {
    try {
        const currentToken = await getToken(messaging, {
            vapidKey: process.env.REACT_APP_FIREBASE_VAPID_KEY,
        });
        if (currentToken) {
            console.log("current token for client: ", currentToken);
        } else {
            console.log(
                "No registration token available. Request permission to generate one."
            );
        }
    } catch (err) {
        console.log("An error occurred while retrieving token. ", err);
    }
};
