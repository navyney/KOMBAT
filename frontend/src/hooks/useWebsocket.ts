import { useDispatch, useSelector } from "react-redux";
import SockJS from "sockjs-client";
import {
    Client,
    IMessage as Message,
    StompSubscription as Subscription,
    Stomp
} from "@stomp/stompjs";
import {
    setWebSocketClient,
    setConnectionStatus,
    selectWebsocket,
} from "@/stores/slices/webSocketSlice";
import { useAppSelector } from "@/stores/hook";
//import {Stomp} from "@stomp/stompjs";

export const useWebSocket = () => {
    const dispatch = useDispatch();
    const { client, connected } = useAppSelector(selectWebsocket);
    const serverUrl = process.env.API_BASE_URL;

    const connect = () => {
        try {
            const webSocket = new SockJS(`${serverUrl}/ws`);
            const stompClient = Stomp.over(webSocket);
            stompClient.debug = () => {}; // disable logging

            stompClient.connect({}, () => {
                dispatch(setWebSocketClient(stompClient));
                dispatch(setConnectionStatus(true));
                console.log("✅ WebSocket connected");
            });
        } catch (e) {
            console.error("WebSocket connection error:", e);
        }
    };

    const disconnect = () => {
        if (client && connected) {
            client.deactivate();
            dispatch(setWebSocketClient(null));
            dispatch(setConnectionStatus(false));
            console.log("🛑 WebSocket disconnected");
        } else {
            console.log("No active WebSocket connection to disconnect.");
        }
    };

    const sendMessage = (destination: string, message: any) => {
        if (client && connected) {
            client.publish({
                destination: `/app${destination}`,
                body: JSON.stringify(message),
            });
            console.log("📤 Sent message to", destination, message);
        } else {
            console.warn("Cannot send: No active WebSocket connection.");
        }
    };

    const subscribe = (destination: string, callback: (payload: Message) => void): Subscription | undefined => {
        if (client && connected) {
            const subscription = client.subscribe(destination, callback);
            console.log(`✅ Subscribed to ${destination}`);
            return subscription;
        } else {
            console.warn("Cannot subscribe: No active WebSocket connection.");
        }
    };

    const unsubscribe = (subscription: Subscription | undefined) => {
        if (client && connected && subscription) {
            subscription.unsubscribe();
            console.log(`🛑 Unsubscribed from ${subscription.id}`);
        } else {
            console.warn("No active subscription to unsubscribe.");
        }
    };

    return {
        connect,
        disconnect,
        sendMessage,
        subscribe,
        unsubscribe,
    };
};
