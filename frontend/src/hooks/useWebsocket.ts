import { useDispatch } from "react-redux";
import SockJS from "sockjs-client";
import { Client, Message } from "@stomp/stompjs";
import { Subscription } from "stompjs";
import {setPlayerId} from "@/stores/slices/playerSlice";

let stompClient: Client | null = null;
let activeSubscriptions: Subscription[] = [];

export const useWebSocket = () => {
    const dispatch = useDispatch();

    const host = process.env.NEXT_PUBLIC_API_HOST || "localhost";
    const port = process.env.NEXT_PUBLIC_API_PORT || "8080";
    const serverUrl = `http://${host}:${port}`;

    const subscribe = (destination: string, callback: (payload: Message) => void) => {
        if (stompClient && stompClient.connected) {
            const subscription = stompClient.subscribe(destination, callback);
            activeSubscriptions.push(subscription);
            console.log(`-Subscribed to ${destination}`);
            return subscription;
        } else {
            console.warn("-Cannot subscribe: WebSocket is not connected.-");
        }
    };

    const unsubscribe = (subscription: Subscription | undefined) => {
        if (subscription && stompClient && stompClient.connected) {
            subscription.unsubscribe();
            activeSubscriptions = activeSubscriptions.filter(s => s.id !== subscription.id);
            console.log(`Unsubscribed from ${subscription.id}`);
        } else {
            console.warn("-Cannot unsubscribe: Invalid subscription or WebSocket not connected.-");
        }
    };

    const sendMessage = (destination: string, message: any) => {
        const isTopic = destination.startsWith("/topic");
        const dest = isTopic ? destination : `/app${destination}`;
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: dest,
                body: typeof message === "string" ? message : JSON.stringify(message),
            });
            console.log("Sending message to", dest, "payload:", message);
        } else {
            console.warn("-Cannot send message: WebSocket is not connected.-");
        }
    };


    const connect = () => {
        if (stompClient && stompClient.connected) {
            console.log("WebSocket already connected");
            return;
        }

        if (stompClient) {
            stompClient.deactivate();
        }

        let playerId = localStorage.getItem("playerId");

        if (!playerId) {
            playerId = crypto.randomUUID();
            localStorage.setItem("playerId", playerId);
        }

        const socket = new SockJS(`${serverUrl}/ws`);
        stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,

            onConnect: () => {
                console.log("---Connected to WebSocket successfully---");
                stompClient?.publish({
                    destination: "/app/join-game",
                    body: JSON.stringify({ playerId }),
                });
                console.log("--Connecting to WebSocket at:", serverUrl);
                dispatch(setPlayerId(playerId)); // when connect successfully
            },
            onDisconnect: () => {
                console.log("---Disconnected from WebSocket---");
            },
        });

        stompClient.activate();
    };

    const disconnect = () => {
        if (stompClient && stompClient.connected) {
            activeSubscriptions.forEach(sub => sub.unsubscribe());
            activeSubscriptions = [];
            stompClient.deactivate();
            console.log("--WebSocket manually disconnected--");
        } else {
            console.warn("-Cannot disconnect: WebSocket not connected.-");
        }
    };

    const isConnected = () => stompClient?.connected ?? false;

    return {
        connect,
        disconnect,
        sendMessage,
        subscribe,
        unsubscribe,
        isConnected,
    };
};
