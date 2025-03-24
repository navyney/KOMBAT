import { useMemo } from "react";

export const usePersistentPlayerId = () => {
    const playerId = useMemo(() => {
        if (typeof window === "undefined") return null;

        let id = localStorage.getItem("playerId");
        if (!id) {
            id = crypto.randomUUID();
            localStorage.setItem("playerId", id);
        }
        return id;
    }, []);

    return playerId;
};
