import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import { connectWebSocket } from "@/stores/slices/webSocketSlice";

interface Props {
    children: React.ReactNode;
}

const WebSocketProvider: React.FC<Props> = ({ children }) => {
    const dispatch = useDispatch();

    useEffect(() => {
        // Connect WebSocket only once when app starts
        dispatch(connectWebSocket());

        // Do NOT disconnect on unmount to persist WebSocket across pages
    }, [dispatch]);

    return <>{children}</>;
};

export default WebSocketProvider;
