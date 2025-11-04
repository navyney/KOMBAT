"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { updateMinion, MinionType } from "@/stores/slices/minionTypeSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/stores/store";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import { setSelection } from "@/stores/slices/selectionStateSlice";
import {confirmConfig, resetConfig, updateConfig} from "@/stores/slices/configSlice";
import {resetPlayer} from "@/stores/slices/playerSlice";
import {resetGame} from "@/stores/slices/gameSlice";

const minions = [
    { id: 1, name: "pawn", image: "/image/minions/white-pawn.jpeg", color: "white" },
    { id: 2, name: "rook", image: "/image/minions/white-rook.jpeg", color: "white" },
    { id: 3, name: "knight", image: "/image/minions/white-knight.jpeg", color: "white" },
    { id: 4, name: "bishop", image: "/image/minions/white-bishop.jpeg", color: "white" },
    { id: 5, name: "queen", image: "/image/minions/white-queen.jpeg", color: "white" }
];

export default function SelectMinions() {
    const router = useRouter();
    const dispatch = useDispatch();
    const minionsFromRedux = useSelector((state: RootState) => state.miniontype);
    const stateFromRedux = useSelector((state: RootState) => state.selectionState);
    const { sendMessage, subscribe, unsubscribe, connect, isConnected } = useWebSocket();
    const [players, setPlayers] = useState(0);
    const playerId = usePlayerId();
    const [showModal, setShowModal] = useState(false);
    const [customName, setCustomName] = useState("");
    const [customDefense, setCustomDefense] = useState<number | "">("");
    const [customStrategy, setCustomStrategy] = useState("");
    const [currentMinionId, setCurrentMinionId] = useState<number | null>(null);

    const selectedMinions = stateFromRedux
        .filter(item => item.isSelected && item.id.startsWith("select "))
        .map(item => parseInt(item.id.replace("select ", "")));

    useEffect(() => {
        console.log("Hi")
        if (!playerId) return;
        if (!isConnected()) connect();

        const subCount = subscribe("/topic/player-count", (message) => {
            const count = JSON.parse(message.body);
            setPlayers(count);
        });

        // const subNav = subscribe("/topic/navigate", (message) => {
        //     const action = message.body;
        //     if (action === "start") {
        //         dispatch(resetPlayer());
        //         dispatch(resetGame());
        //         dispatch(resetConfig());
        //         window.location.href = "/";
        //     } else if (action === "back") {
        //         dispatch(resetPlayer());
        //         dispatch(resetGame());
        //         dispatch(resetConfig());
        //         window.location.href = "/select-mode";
        //     } else if (action === "next") {
        //         window.location.href = "/Not-pvp-select-type";
        //     } else if (action === "gamepage") {
        //         window.location.href = "/GamePage";
        //     }
        // });

        const subNav = subscribe("/topic/navigate", (message) => {
            const action = message.body;
            if (action === "next") router.push("/select-type");
            else if (action === "back") router.push("/select-mode");
            else if (action === "start") router.push("/");
            else if (action === "gamepage") router.push("/GamePage");
        });

        sendMessage("/join-select-minion-type", { playerId });

        return () => {
            unsubscribe(subCount);
            unsubscribe(subNav);
        };
    }, [dispatch, playerId]);

    const toggleSelectMinion = (id: number) => {
        const idStr = `select ${id}`;
        const isCurrentlySelected = stateFromRedux.find(item => item.id === idStr)?.isSelected ?? false;

        const newIsSelected = !isCurrentlySelected;
        dispatch(setSelection({ id: idStr, isSelected: newIsSelected }));
    };

    const openCustomizeModal = (minionId: number) => {
        setCurrentMinionId(minionId);
        setShowModal(true);

        const baseMinion = minions.find(m => m.id === minionId);
        const reduxMinion = minionsFromRedux.find(m => m.id === minionId);

        if (reduxMinion) {
            setCustomName(reduxMinion.name);
            setCustomDefense(reduxMinion.def);
            setCustomStrategy(reduxMinion.strategy);
        } else if (baseMinion) {
            setCustomName(baseMinion.name);
            setCustomDefense("");
            setCustomStrategy("");
        }
    };

    const handleConfirm = () => {

        const isSelected = stateFromRedux.find(item => item.id === `select ${currentMinionId}`)?.isSelected;
        if (!isSelected) return;

        if (currentMinionId !== null) {
            const baseMinion = minions.find(m => m.id === currentMinionId);
            if (!baseMinion) return;

            const updatedMinion: MinionType = {
                id: currentMinionId,
                name: customName || baseMinion.name,
                def: Number(customDefense),
                strategy: customStrategy,
            };

            dispatch(updateMinion(updatedMinion));

            sendMessage("/minion-config", {
                playerId,
                minions: [updatedMinion],
            });
        }

        setShowModal(false);
    };

    const handleStartGame = () => {
        const selectedIds = selectedMinions;

        const cleanMinions = minionsFromRedux
            .filter(m => selectedIds.includes(m.id))
            .map(({ id, name, def, strategy }) => ({ id, name, def, strategy }));

        console.log("-----Selected IDs:", selectedIds);
        console.log("-----Clean Minions:", cleanMinions);

        sendMessage("/minion-config", {
            playerId,
            minions: cleanMinions,
        });

        const selectedMinionData = selectedIds.map(id => minions.find(m => m.id === id));
        const queryParams = new URLSearchParams({
            selectedMinions: JSON.stringify(selectedMinionData)
        });

        console.log(selectedMinionData);

        router.push(`/GamePage?${queryParams.toString()}`);

        // sendMessage("/navigate", "gamepage");

    };

    // const handleMinionChange = (
    //     id: number,
    //     field: keyof MinionType,
    //     value: string | number
    // ) => {
    //     const minion = minionsFromRedux.find(m => m.id === id);
    //     if (!minion) return;
    //
    //     const updatedMinion: MinionType = {
    //         ...minion,
    //         [field]: field === "def" ? Number(value) : String(value),
    //     };
    //
    //     dispatch(updateMinion(updatedMinion));
    //
    //     sendMessage("/minion-config", {
    //         playerId,
    //         minions: minionsFromRedux.map(m => m.id === id ? updatedMinion : m),
    //     });
    // };
    const handleMinionChange = (
        id: number,
        field: keyof MinionType,
        value: string | number
    ) => {
        const minion = minionsFromRedux.find(m => m.id === id);
        if (!minion) return;

        const updatedMinion: MinionType = {
            ...minion,
            [field]: field === "def" ? Number(value) : String(value),
        };

        dispatch(updateMinion(updatedMinion));
    };

    const isReadyToStart = selectedMinions.length > 0;

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4 w-full h-full">
            <h1 className="text-4xl font-bold text-gray-800 mt-6">Please Select Minions</h1>
            <h2 className="text-xl font-bold text-gray-700 mt-4 mb-10">Selected Minions {selectedMinions.length}</h2>
            <div className="grid grid-cols-5 gap-12">
                {minions.map((minion) => {
                    const selectionId = `select ${minion.id}`;
                    const isSelected = stateFromRedux.find(item => item.id === selectionId)?.isSelected ?? false;

                    return (
                        <div key={minion.id} className="bg-white p-6 w-80 h-120 rounded-lg shadow-md text-center flex flex-col items-center border-2 border-black">
                            <img src={minion.image} alt={minion.name} className="w-64 h-64 mx-auto mb-4 border-2 border-black rounded-lg" />

                            <button
                                className={`w-full py-3 rounded-lg text-lg ${isSelected ? 'bg-green-500 text-white' : 'bg-gray-300'}`}
                                onClick={() => toggleSelectMinion(minion.id)}
                            >
                                {isSelected ? "Selected" : "Select"}
                            </button>

                            <button
                                className="w-full mt-3 py-3 bg-blue-500 text-white rounded-lg text-lg disabled:opacity-50"
                                onClick={() => openCustomizeModal(minion.id)}
                                disabled={!isSelected}
                            >
                                Customize
                            </button>
                        </div>
                    );
                })}
            </div>

            {showModal && currentMinionId !== null && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center w-full h-full">
                    <div className="bg-white rounded-lg shadow-md flex flex-row w-[80vw] h-[90vh] overflow-hidden font-[Verdana] border-[3px] border-black">
                        <div className="flex flex-col items-center justify-start p-6 w-1/2 border-r-[3px] border-black relative">
                            <h1 className="text-4xl font-bold text-gray-800 mt-10 mb-4 text-center">Customize Minion</h1>
                            <div className="w-[500px] h-[500px] border-[3px] border-gray-700 rounded-lg flex items-center justify-center bg-white shadow-md mt-20">
                                <img
                                    src={minions.find(m => m.id === currentMinionId)?.image}
                                    alt="Minion"
                                    className="w-[400px] h-[400px] object-contain"
                                />
                            </div>
                        </div>

                        <div className="flex flex-col justify-between p-6 w-1/2">
                            <div className="space-y-4 text-black">
                                <div className="flex items-center justify-between">
                                    <label className="text-lg font-bold lowercase">minion’s name :</label>
                                    <input
                                        className="border border-black rounded px-2 py-1 w-1/2 text-sm"
                                        type="text"
                                        value={customName}
                                        onChange={(e) => {
                                            setCustomName(e.target.value);
                                            if (currentMinionId !== null) {
                                                handleMinionChange(currentMinionId, "name", e.target.value);
                                            }
                                        }}
                                    />
                                </div>

                                <div className="flex items-center justify-between">
                                    <label className="text-lg font-bold lowercase">minion’s defense :</label>
                                    <input
                                        className="border border-black rounded px-2 py-1 w-1/2 text-sm"
                                        type="number"
                                        value={customDefense}
                                        onChange={(e) => {
                                            const value = parseInt(e.target.value);
                                            setCustomDefense(isNaN(value) ? 0 : value);
                                            if (currentMinionId !== null) {
                                                handleMinionChange(currentMinionId, "def", value);
                                            }
                                        }}
                                    />
                                </div>

                                <div>
                                    <label className="text-lg font-bold lowercase block mb-1">
                                        write your minion’s strategy down here ...
                                    </label>
                                    <textarea
                                        className="w-full h-[65vh] border border-black rounded px-2 py-1 text-sm"
                                        value={customStrategy}
                                        onChange={(e) => {
                                            setCustomStrategy(e.target.value);
                                            if (currentMinionId !== null) {
                                                handleMinionChange(currentMinionId, "strategy", e.target.value);
                                            }
                                        }}
                                    />
                                </div>
                            </div>

                            <div className="flex justify-end mt-4">
                                <button
                                    onClick={handleConfirm}
                                    className="bg-green-700 hover:bg-green-800 text-white text-sm font-bold py-3 px-6 rounded-full tracking-widest mb-10"
                                >
                                    APPLY
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <div className="mt-10 items-center justify-center">
                <button
                    onClick={handleStartGame}
                    className={`text-2xl ${isReadyToStart ? 'bg-orange-500 hover:bg-orange-700' : 'bg-gray-500'} text-white font-bold py-2 px-4 rounded`}
                    disabled={!isReadyToStart}
                >
                    START
                </button>
            </div>
        </div>
    );
}
