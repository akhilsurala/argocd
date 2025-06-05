import { BASE_URL_WEBSOCKET_URL } from "../api/config";
import { updateNotificationCount } from "../redux/action/homeAction";

let ws;
let reconnectInterval = 1000;
const maxReconnectInterval = 30000;
let shouldReconnect = true;

export const connectWebSocket = (emailId, token, dispatch) => {
  if (!emailId || !token) return;
  // Ensure reconnection is enabled when connecting
  shouldReconnect = true;

  ws = new WebSocket(
    `${BASE_URL_WEBSOCKET_URL}/ws?username=${encodeURIComponent(
      emailId
    )}&token=Bearer ${token}`
  );

  ws.onopen = () => {
    console.log("WebSocket connection established");
    reconnectInterval = 1000; // Reset the reconnect interval
  };

  ws.onmessage = (event) => {
    console.log("Received message:", event.data);
    dispatch(updateNotificationCount(event.data)); // Handle the message
  };

  ws.onclose = () => {
    console.log("WebSocket connection closed");
    if (shouldReconnect) {
      console.log("Attempting to reconnect...");
      reconnectWebSocket(emailId, token, dispatch);
    }
  };

  ws.onerror = (error) => {
    console.error("WebSocket error:", error);
    ws.close(); // Close the connection to trigger the `onclose` event
  };
};

const reconnectWebSocket = (emailId, token, dispatch) => {
  setTimeout(() => {
    console.log(
      `Reconnecting WebSocket in ${reconnectInterval / 1000} seconds...`
    );
    connectWebSocket(emailId, token, dispatch);
    reconnectInterval = Math.min(reconnectInterval * 2, maxReconnectInterval);
  }, reconnectInterval);
};

export const disconnectWebSocket = () => {
  shouldReconnect = false; // Disable reconnection attempts
  if (ws) {
    ws.close(); // Close the WebSocket connection
    console.log("WebSocket connection closed manually");
  }
};
