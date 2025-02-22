'use client';

import Image from "next/image";
import { useState, useEffect } from "react";
import HexGrid from "@/component/HexGrid";

interface Minion {
    id: number;
    name: string;
    image: string;
    color: string;
}

export default function GamePage() {
    const [turn, setTurn] = useState(1);
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [playerData, setPlayerData] = useState<Record<number, {
        budget: number;
        minions: number;
        ownedHexes: number
    }>>({
        1: {budget: 100, minions: 3, ownedHexes: 5},
        2: {budget: 100, minions: 3, ownedHexes: 5}
    });
    const maxTurn = 50;
    const [selectedAction, setSelectedAction] = useState<"buy" | "spawn" | null>(null);
    const [selectedHex, setSelectedHex] = useState<number | null>(null);
    const [allyHexes, setAllyHexes] = useState<number[]>([11, 12, 13, 21, 22]);
    const [opponentHexes, setOpponentHexes] = useState<number[]>([77, 78, 86, 87, 88]);
    const [hasBought, setHasBought] = useState(false);
    const [hasSpawned, setHasSpawned] = useState(false);
    const [selectedMinions, setSelectedMinions] = useState<number[]>([]);
    const [minions, setMinions] = useState<Minion[]>([]);

    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const selectedMinionsParam = queryParams.get("selectedMinions");

        if (selectedMinionsParam) {
            const parsedMinions: Minion[] = JSON.parse(selectedMinionsParam);
            setMinions(parsedMinions);
            setSelectedMinions(parsedMinions.map(m => m.id));
        }
        console.log("Selected Minions:", selectedMinions);
        console.log("Minions Data:", minions);
    }, []);


    const handleHexClick = (hexId: number) => {
        if (!selectedAction) return;

        if (selectedAction === "buy" && !hasBought) {
            const isAdjacent = [...allyHexes, ...opponentHexes].some(hex => getNeighbors(hex).includes(hexId));

            if (isAdjacent && !allyHexes.includes(hexId) && !opponentHexes.includes(hexId)) {
                setSelectedHex(hexId);
                setPlayerData(prev => ({
                    ...prev,
                    [currentPlayer]: {
                        ...prev[currentPlayer],
                        budget: prev[currentPlayer].budget - 10,
                        ownedHexes: prev[currentPlayer].ownedHexes + 1
                    }
                }));
                if (currentPlayer === 1) {
                    setAllyHexes([...allyHexes, hexId]);
                } else {
                    setOpponentHexes([...opponentHexes, hexId]);
                }
                setHasBought(true);
            }
        } else if (selectedAction === "spawn" && !hasSpawned ) {
            setHasSpawned(true);
        }
        setSelectedAction(null);
    };

    const endTurn = () => {
        setHasBought(false);
        setHasSpawned(false);
        if (currentPlayer === 1) {
            setCurrentPlayer(2);
        } else {
            setCurrentPlayer(1);
            setTurn(turn + 1);
        }
    };

    const getNeighbors = (hexId: number) => {
        const row = Math.floor(hexId / 10);
        const col = hexId % 10;
        const isOdd = row % 2 !== 0;
        return [
            hexId - 10,
            hexId + 10,
            hexId - 1,
            hexId + 1,
            isOdd ? hexId - 9 : hexId - 11,
            isOdd ? hexId + 11 : hexId + 9,
        ];
    };

    const isPlayerTurn = (player: number) => currentPlayer === player;

    const getMinionImage = (minionId: number, player: number) => {
        const minion = minions.find((m) => m.id === minionId);
        if (!minion) {
            // @ts-ignore
            return `/image/minions/white-${minion.name.toLowerCase}.png`;
        }

        return player === 1
            ? `/image/minions/green-${minion.name.toLowerCase()}.png`
            : `/image/minions/red-${minion.name.toLowerCase()}.png`;
    };


    return (
        <main
            className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full overflow-hidden">
            <div className="absolute top-4 right-4 bg-gray-800 text-white px-4 py-2 rounded">
                Turn {turn} / {maxTurn} - Player {currentPlayer}'s Turn
            </div>

            <div className="absolute top-4 left-4 bg-green-200 p-4 rounded">
                <h3>Player 1</h3>
                <p>Budget: {playerData[1].budget}</p>
                <p>Minions: {playerData[1].minions}</p>
                <p>Owned Hexes: {playerData[1].ownedHexes}</p>
                <button
                    onClick={() => setSelectedAction("buy")}
                    className={`${!isPlayerTurn(1) || hasBought || hasSpawned ? 'bg-gray-500' : 'bg-green-500'} text-white px-2 py-1 rounded hover:opacity-80 transition-opacity`}
                    disabled={!isPlayerTurn(1) || hasBought || hasSpawned}
                >
                    Buy Area
                </button>
                <button
                    onClick={() => setSelectedAction("spawn")}
                    className={`${!isPlayerTurn(1) || hasSpawned ? 'bg-gray-500' : 'bg-green-500'} text-white px-2 py-1 rounded ml-2 hover:opacity-80 transition-opacity`}
                    disabled={!isPlayerTurn(1) || hasSpawned}
                >
                    Spawn Minion
                </button>
                <div className="mt-4">
                    <h4>Selected Minions:</h4>
                    <div className="flex">
                        {selectedMinions.map((id: number) => (
                            <img
                                key={id}
                                src={getMinionImage(id, 1)}
                                alt="Minion"
                                className="w-12 h-12 mx-1"
                            />
                        ))}
                    </div>
                </div>
            </div>

            <div
                className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full overflow-hidden overflow-y-hidden downpls"
            >
                <HexGrid rows={8} cols={8} size={50} distance={20} initialHex_Ally={allyHexes}
                         initialHex_Opponent={opponentHexes} onHexClick={handleHexClick}/>
            </div>

            <div className="absolute bottom-4 right-4 bg-red-200 p-4 rounded">
                <h3>Player 2</h3>
                <p>Budget: {playerData[2].budget}</p>
                <p>Minions: {playerData[2].minions}</p>
                <p>Owned Hexes: {playerData[2].ownedHexes}</p>
                <button
                    onClick={() => setSelectedAction("buy")}
                    className={`${!isPlayerTurn(2) || hasBought || hasSpawned ? 'bg-gray-500' : 'bg-red-500'} text-white px-2 py-1 rounded hover:opacity-80 transition-opacity`}
                    disabled={!isPlayerTurn(2) || hasBought || hasSpawned}
                >
                    Buy Area
                </button>
                <button
                    onClick={() => setSelectedAction("spawn")}
                    className={`${!isPlayerTurn(2) || hasSpawned ? 'bg-gray-500' : 'bg-red-500'} text-white px-2 py-1 rounded ml-2 hover:opacity-80 transition-opacity`}
                    disabled={!isPlayerTurn(2) || hasSpawned}
                >
                    Spawn Minion
                </button>
                <div className="mt-4">
                    <h4>Selected Minions:</h4>
                    <div className="flex">
                        {selectedMinions.map((id: number) => (
                            <img
                                key={id}
                                src={getMinionImage(id, 2)}
                                alt="Minion"
                                className="w-12 h-12 mx-1"
                            />
                        ))}
                    </div>
                </div>
            </div>

            <div
                onClick={endTurn}
                className="absolute cursor-pointer bottom-7 left-[calc(0.75%+100px)]"
            >
                <Image
                    src="/image/DoneButton.png"
                    alt="End Turn"
                    width={150}
                    height={150}
                    className="hover:opacity-80 transition-opacity"
                />
            </div>
        </main>
    );

}