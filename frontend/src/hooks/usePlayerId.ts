import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "@/stores/hook";
import { setPlayerId } from "@/stores/slices/playerSlice";

export const usePlayerId = () => {
    const dispatch = useAppDispatch();
    const playerId = useAppSelector((state) => state.player.playerId);

    useEffect(() => {
        if (!playerId && typeof window !== "undefined") {
            const stored = localStorage.getItem("playerId");
            if (stored) dispatch(setPlayerId(stored));
        }
    }, [playerId, dispatch]);

    return playerId;
};
