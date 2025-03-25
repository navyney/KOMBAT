'use client';

import Image from "next/image";
import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/stores/hook";
import HexGrid from "@/component/HexGrid";
import {confirmConfig, updateConfig} from "@/stores/slices/configSlice";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import {useRouter} from "next/navigation";


// สาธุขอให้ push ได้

interface Minion {
    id: number;
    name: string;
    image: string;
    color: string;
    type: string;
    player: number;
}

export default function GamePage() {
    const router = useRouter();
    const { subscribe, sendMessage, connect, isConnected, unsubscribe } = useWebSocket();
    const [turn, setTurn] = useState(1);
    const [currentPlayer, setCurrentPlayer] = useState(1);
    const [winner, setWinner] = useState<number | null>(null);

    const playerId = usePlayerId();
    const dispatch = useAppDispatch();
    const [gameConfig, setGameConfig] = useState({
        spawnedCost: 0,
        hexPurchasedCost: 0,
        initialBudget: 0,
        initialHP: 0,
        turnBudget: 0,
        maxBudget: 0,
        interestPercentage: 0,
        maxTurn: 50,
        maxSpawn: 0,
    });

    const [playerData, setPlayerData] = useState<Record<number, {
        budget: number;
        minions: number;
        ownedHexes: number;
    }>>({
        1: { budget: 0, minions: 0, ownedHexes: 5 },
        2: { budget: 0, minions: 0, ownedHexes: 5 }
    });

    const [selectedAction, setSelectedAction] = useState<"buy" | "spawn" | null>(null);
    const [selectedHex, setSelectedHex] = useState<number | null>(null);
    const [allyHexes, setAllyHexes] = useState<number[]>([11, 12, 13, 21, 22]);
    const [opponentHexes, setOpponentHexes] = useState<number[]>([77, 78, 86, 87, 88]);
    const [hasBought, setHasBought] = useState(false);
    const [hasSpawned, setHasSpawned] = useState(false);
    const [selectedMinions, setSelectedMinions] = useState<number[]>([]);
    const [selectedMinionType, setSelectedMinionType] = useState<Record<number, string | null>>({
        1: null,
        2: null,
    });

    const [minions, setMinions] = useState<Minion[]>([]);
    const [boardMinions, setBoardMinions] = useState<{ id: number, type: string, player: number }[]>([]);
    const [allyNeighbors, setAllyNeighbors] = useState<number[]>([]);
    const [opponentNeighbors, setOpponentNeighbors] = useState<number[]>([]);


    useEffect(() => {
        if (!isConnected()) connect();

        const subConfig = subscribe("/gamestate/config", (message) => {
            const configData = JSON.parse(message.body);
            console.log("📥 Received game config:", configData);
            setGameConfig(configData);
            setPlayerData({
                1: {
                    budget: configData.initialBudget,
                    minions: 0,
                    ownedHexes: 5,
                },
                2: {
                    budget: configData.initialBudget,
                    minions: 0,
                    ownedHexes: 5,
                },
            });
        });

        return () => {
            unsubscribe(subConfig);
        };
    }, []);

    useEffect(() => {
        if(!isConnected()) connect();
        const subExe = subscribe("/topic/executeMinion", (message) => {
            const minions = JSON.parse(message.body);

        });

        return () => {
            unsubscribe(subExe);

        }
    },[currentPlayer]);

    // dummy winner checked
    useEffect(() => {
        if (turn > gameConfig.maxTurn) {
            const winnerPlayer = playerData[1].budget > playerData[2].budget ? 1 : 2;
            setWinner(winnerPlayer);
        }
    }, [turn]);

    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const selectedMinionsParam = queryParams.get("selectedMinions")
        if (selectedMinionsParam) {
            const parsedMinions: Minion[] = JSON.parse(selectedMinionsParam);
            setMinions(parsedMinions);
            setSelectedMinions(parsedMinions.map(m => m.id));
        }
        // for debug kub
        console.log("Selected Minions:", selectedMinions);
        console.log("Minions Data:", minions);
    }, []);



    const handleHexClick = (hexId: number) => {
        if (!selectedAction) {
            setSelectedMinionType(prev => ({
                ...prev,
                [currentPlayer]: null
            }));
            return;
        }

        if (selectedAction === "buy" && !hasBought) {
            const isAdjacent = currentPlayer === 1
                ? allyHexes.some(hex => getNeighbors(hex).includes(hexId))
                : opponentHexes.some(hex => getNeighbors(hex).includes(hexId));

            if (isAdjacent && !allyHexes.includes(hexId) && !opponentHexes.includes(hexId)) {
                setSelectedHex(hexId);
                setPlayerData(prev => ({
                    ...prev,
                    [currentPlayer]: {
                        ...prev[currentPlayer],
                        budget: prev[currentPlayer].budget - gameConfig.hexPurchasedCost,
                        ownedHexes: prev[currentPlayer].ownedHexes + 1
                    }
                }));
                sendMessage("/minion/buyArea",{
                    curPlayer: currentPlayer,row: hexId/10,col: hexId%10
                });
                if (currentPlayer === 1) {
                    setAllyHexes([...allyHexes, hexId]);
                } else {
                    setOpponentHexes([...opponentHexes, hexId]);
                }
                setHasBought(true);
            }

        } else if (selectedAction === "spawn" && !hasSpawned && selectedMinionType[currentPlayer] !== null) {
            const isOwned = currentPlayer === 1
                ? allyHexes.includes(hexId)
                : opponentHexes.includes(hexId);

            const isOccupied = boardMinions.some(minion => minion.id === hexId);
            if (isOccupied) {
                console.log("This hex is already occupied by another minion.");
                return;
            }

            if (!isOwned) {
                console.log("You don't own this hex. Minion placement canceled.");
                setSelectedMinionType(prev => ({
                    ...prev,
                    [currentPlayer]: null
                }));
                return;
            }

            if (isOwned && playerData[currentPlayer].minions < gameConfig.maxSpawn) {
                setPlayerData(prev => ({
                    ...prev,
                    [currentPlayer]: {
                        ...prev[currentPlayer],
                        budget: prev[currentPlayer].budget - gameConfig.spawnedCost,
                        minions: prev[currentPlayer].minions + 1
                    }
                }));
                setHasSpawned(true)

                const selectedId = selectedMinionType[currentPlayer];
                if (selectedId) {
                    const selectedMinion = minions.find(m => m.id === Number(selectedId));
                    if (selectedMinion) {
                        const newBoardMinion = {
                            id: hexId,
                            type: selectedMinion.name,
                            player: currentPlayer,
                            minionId: selectedMinion.id
                        };

                        console.log("Adding new minion:", newBoardMinion);
                        setBoardMinions(prev => [...prev, newBoardMinion]);
                    }

                    sendMessage("/minion/spawnMinion",{
                        curPlayer: currentPlayer,row: hexId/10,col: hexId%10,minion: selectedMinion?.name
                    });
                }

                setSelectedMinionType(prev => ({
                    ...prev,
                    [currentPlayer]: null
                }));
            }
        }
        setSelectedAction(null);
    };

    const handleMinionSelect = (minionId: number) => {
        if (hasSpawned) return;
        const selectedMinion = minions.find(m => m.id === minionId);
        if (selectedMinion) {
            setSelectedMinionType(prev => ({
                ...prev,
                [currentPlayer]: String(selectedMinion.id)
            }));
        }
    };

    const endTurn = () => {
        if (turn > gameConfig.maxTurn) return;
        sendMessage("/minion/endTurn",{
            curPlayer: currentPlayer
        });
        setHasBought(false);
        setHasSpawned(false);
        setSelectedMinionType(prev => ({
            ...prev,
            [currentPlayer]: null
        }));

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
        const isOdd = col % 2 !== 0;
        return [
            hexId - 10, // ด้านบน
            hexId + 10, // ด้านล่าง
            isOdd ? hexId + 1 : hexId - 9, // บนขวา
            isOdd ? hexId - 1 : hexId - 11, // บนซ้าย
            isOdd ? hexId + 9 : hexId - 1, // ล่างซ้าย
            isOdd ? hexId + 11 : hexId + 1, //ล่างขวา
        ];
    };

    const getNeighborsForPlayer = (hexes: number[]): number[] => {
        const neighbors = new Set<number>();
        hexes.forEach(hex => {
            const hexNeighbors = getNeighbors(hex);
            hexNeighbors.forEach(neighbor => {
                if (!hexes.includes(neighbor)) {
                    neighbors.add(neighbor);
                }
            });
        });
        return Array.from(neighbors);
    };

    useEffect(() => {
        if (currentPlayer === 1) {
            setAllyNeighbors(getNeighborsForPlayer(allyHexes));
        } else {
            setOpponentNeighbors(getNeighborsForPlayer(opponentHexes));
        }
    }, [currentPlayer, allyHexes, opponentHexes]);


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
                Turn {turn} / {gameConfig.maxTurn} - Player {currentPlayer}'s Turn
            </div>

            <HexGrid
                rows={8}
                cols={8}
                size={50}
                distance={20}
                initialHex_Ally={allyHexes}
                initialHex_Opponent={opponentHexes}
                onHexClick={handleHexClick}
                allyNeighbors={allyNeighbors}
                opponentNeighbors={opponentNeighbors}
                currentPlayer={currentPlayer}
                minions={minions}
                boardMinions={boardMinions}
            />

            <div className="absolute top-4 left-4 bg-green-200 p-4 rounded">
                <h3>Player 1</h3>
                <p>Budget: {playerData[1].budget}</p>
                <p>Minions: {playerData[1].minions} / {gameConfig.maxSpawn}</p>
                <p>Owned Hexes: {playerData[1].ownedHexes}</p>
                <button
                    onClick={() => {
                        setSelectedAction("buy")
                        setSelectedMinionType(prev => ({
                            ...prev,
                            [currentPlayer]: null
                        }));
                    }}
                    className={`${turn > gameConfig.maxTurn || !isPlayerTurn(1) || hasBought || hasSpawned || playerData[1].budget < gameConfig.hexPurchasedCost ? 'bg-gray-500' : 'bg-green-500'} text-white px-2 py-1 rounded hover:opacity-80 transition-opacity`}
                    disabled={turn > gameConfig.maxTurn || !isPlayerTurn(1) || hasBought || hasSpawned || playerData[1].budget < gameConfig.hexPurchasedCost}
                >
                    Buy Area
                </button>

                <button
                    onClick={() => {
                        setSelectedAction("spawn")
                        setSelectedMinionType(prev => ({
                            ...prev,
                            [currentPlayer]: null
                        }));
                    }}
                    className={`${turn > gameConfig.maxTurn || !isPlayerTurn(1) || hasSpawned || playerData[1].minions >= gameConfig.maxSpawn || playerData[1].budget < gameConfig.spawnedCost ? 'bg-gray-500' : 'bg-green-500'} text-white px-2 py-1 rounded ml-2 hover:opacity-80 transition-opacity`}
                    disabled={turn > gameConfig.maxTurn || !isPlayerTurn(1) || hasSpawned || playerData[1].minions >= gameConfig.maxSpawn || playerData[1].budget < gameConfig.spawnedCost}
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
                                className={`w-12 h-12 mx-1 cursor-pointer ${selectedMinionType[1] === String(id) ? 'border-2 border-blue-500' : ''}`}
                                onClick={() => handleMinionSelect(id)}
                            />
                        ))}
                    </div>
                </div>
            </div>

            <div
                className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full overflow-hidden overflow-y-hidden downpls"
            >
                <HexGrid
                    rows={8}
                    cols={8}
                    size={50}
                    distance={20}
                    initialHex_Ally={allyHexes}
                    initialHex_Opponent={opponentHexes}
                    onHexClick={handleHexClick}
                    allyNeighbors={allyNeighbors}
                    opponentNeighbors={opponentNeighbors}
                    currentPlayer={currentPlayer}
                    minions={minions}
                    boardMinions={boardMinions}
                />
            </div>

            <div className="absolute bottom-4 right-4 bg-red-200 p-4 rounded">
                <h3>Player 2</h3>
                <p>Budget: {playerData[2].budget}</p>
                <p>Minions: {playerData[2].minions} / {gameConfig.maxSpawn}</p>
                <p>Owned Hexes: {playerData[2].ownedHexes}</p>
                <button
                    onClick={() => {
                        setSelectedAction("buy")
                        setSelectedMinionType(prev => ({
                            ...prev,
                            [currentPlayer]: null
                        }));
                    }}
                    className={`${turn > gameConfig.maxTurn || !isPlayerTurn(2) || hasBought || hasSpawned || playerData[2].budget < gameConfig.hexPurchasedCost ? 'bg-gray-500' : 'bg-red-500'} text-white px-2 py-1 rounded hover:opacity-80 transition-opacity`}
                    disabled={turn > gameConfig.maxTurn || !isPlayerTurn(2) || hasBought || hasSpawned || playerData[2].budget < gameConfig.hexPurchasedCost}
                >
                    Buy Area
                </button>

                <button
                    onClick={() => {
                        setSelectedAction("spawn")
                        setSelectedMinionType(prev => ({
                            ...prev,
                            [currentPlayer]: null
                        }));
                    }}
                    className={`${turn > gameConfig.maxTurn || !isPlayerTurn(2) || hasSpawned || playerData[2].minions >= gameConfig.maxSpawn || playerData[2].budget < gameConfig.spawnedCost ? 'bg-gray-500' : 'bg-red-500'} text-white px-2 py-1 rounded ml-2 hover:opacity-80 transition-opacity`}
                    disabled={turn > gameConfig.maxTurn || !isPlayerTurn(2) || hasSpawned || playerData[2].minions >= gameConfig.maxSpawn || playerData[2].budget < gameConfig.spawnedCost}
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
                                className={`w-12 h-12 mx-1 cursor-pointer ${selectedMinionType[2] === String(id) ? 'border-2 border-blue-500' : ''}`}
                                onClick={() => handleMinionSelect(id)}
                            />
                        ))}
                    </div>
                </div>
            </div>

            <div
                onClick={ turn <= gameConfig.maxTurn ? endTurn : undefined}
                className={`absolute bottom-7 left-[calc(0.75%+100px)] cursor-pointer ${
                    turn > gameConfig.maxTurn ? "opacity-50 cursor-not-allowed" : "hover:opacity-80"
                }`}
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