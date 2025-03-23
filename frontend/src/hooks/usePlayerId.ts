import { useEffect, useState } from "react";

export function usePlayerId(): string | null {
    const [playerId, setPlayerId] = useState<string | null>(null);

    useEffect(() => {
        const existing = sessionStorage.getItem("playerId");

        if (existing) {
            setPlayerId(existing);
        } else {
            const isOnStartPage = window.location.pathname === "/";
            if (isOnStartPage) {
                const newId = crypto.randomUUID(); // 🔄 ใช้อันนี้แทน uuidv4()
                sessionStorage.setItem("playerId", newId);
                setPlayerId(newId);
            }
        }
    }, []);

    return playerId;
}
