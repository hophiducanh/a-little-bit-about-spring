import React, {useEffect} from "react";
import {onMessageListener, getFcmRegistrationToken} from "./firebase"; // Assuming messaging is exported from firebase.js

function App() {
    useEffect(() => {
        getFcmRegistrationToken().then(r => r);

        onMessageListener().then((data) => {
            console.log("Receive foreground: ", data);
        });
    });

    return (
        <div className="App">
            <header className="App-header">
                <p>Receive FCM Messages in React</p>
            </header>
        </div>
    );
}

export default App;
