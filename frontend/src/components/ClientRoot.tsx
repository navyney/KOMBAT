"use client";

import React from "react";
import WebSocketProvider from "@/providers/WebSocketProvider";

export default function ClientRoot({ children }: { children: React.ReactNode }) {
    return <WebSocketProvider>{children}</WebSocketProvider>;
}
