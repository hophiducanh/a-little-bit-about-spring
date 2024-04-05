const eventSource = new EventSource('http://localhost:8182/sse');

console.log(eventSource);

eventSource.onopen = (event) => {
    console.log('SSE Connection Opened!');
    // Perform any actions upon successful connection (optional)
};

eventSource.onmessage = (event) => {
    const data = JSON.parse(event.data); // Parse received JSON data
    // Update your UI or application state based on the received data
    console.log('Received data:', data); // Example logging
};

// Handle connection errors gracefully
eventSource.onerror = (error) => {
    console.error('SSE Connection Error:', error);
    // Implement reconnection logic or error handling as needed
};