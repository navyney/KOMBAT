"use client";
import {useEffect, useState} from "react";
import { useRouter } from "next/navigation";

import { updateMinion, resetMinion,MinionType } from "@/stores/slices/minionTypeSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/stores/store";

import { useWebSocket } from "@/hooks/useWebsocket"; // หรือ path ที่ใช้จริงในโปรเจกต์

const minions = [
    { id: 1, name: "pawn", image: "/image/minions/white-pawn.jpeg", color: "white" },
    { id: 2, name: "rook", image: "/image/minions/white-rook.jpeg", color: "white" },
    { id: 3, name: "knight", image: "/image/minions/white-knight.jpeg", color: "white" },
    { id: 4, name: "bishop", image: "/image/minions/white-bishop.jpeg", color: "white" },
    { id: 5, name: "queen", image: "/image/minions/white-queen.jpeg", color: "white" }
];

export default function SelectMinions() {
    const [selectedMinions, setSelectedMinions] = useState<number[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [currentMinionIndex, setCurrentMinionIndex] = useState<number | null>(null);
    const [minionDetails, setMinionDetails] = useState<Record<number, { name: string; defense: number; strategy: string }>>({});
    const router = useRouter();

    const [customName, setCustomName] = useState<string>("");
    const [customDefense, setCustomDefense] = useState<number | string>("");
    const [customStrategy, setCustomStrategy] = useState<string>("");
    const minionsFromRedux = useSelector((state: RootState) => state.miniontype);
    const dispatch = useDispatch();

    const { connect, sendMessage, isConnected } = useWebSocket();

    useEffect(() => {
        console.log("🌐 [SelectMinionsPage] WebSocket Connected:", isConnected());
    }, []);

    const toggleSelectMinion = (id: number) => {
        setSelectedMinions((prev) =>
            prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
        );
    };

    const openCustomizeModal = (index: number) => {
        if (index < selectedMinions.length) {
            setCurrentMinionIndex(index);
            setShowModal(true);

            const minionId = selectedMinions[index];
            const baseMinion = minions.find(m => m.id === minionId);
            if (!baseMinion) return;

            const reduxMinion = minionsFromRedux.find((m) => m.id === minionId);


            if (reduxMinion) {
                setCustomName(reduxMinion.name);
                setCustomDefense(reduxMinion.def);
                setCustomStrategy(reduxMinion.strategy);
            } else {
                setCustomName(baseMinion.name);
                setCustomDefense("");
                setCustomStrategy("");
            }
        }
    };

    const handleConfirm = () => {
        if (currentMinionIndex !== null) {
            const minionId = selectedMinions[currentMinionIndex];
            const baseMinion = minions.find(m => m.id === minionId);
            if (!baseMinion) return;

            const updatedMinion: MinionType = {
                id: minionId,
                name: customName || baseMinion.name,
                def: Number(customDefense),
                strategy: customStrategy,
            };

            dispatch(updateMinion({
                id: minionId,
                name: customName || baseMinion.name,
                def: Number(customDefense),
                strategy: customStrategy
            }));
        }

        setShowModal(false);
    };

    const isReadyToStart = selectedMinions.length > 0;

    // const handleStartGame = () => {
    //     const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
    //     const queryParams = new URLSearchParams({
    //         selectedMinions: JSON.stringify(selectedMinionData)
    //     });
    //     router.push(`/GamePage?${queryParams.toString()}`);
    // };

    const handleStartGame = () => {
        // 👇 ส่งข้อมูลผ่าน WebSocket ก่อน
        const cleanMinions = minionsFromRedux
            .filter((m) => selectedMinions.includes(m.id)) // ✅ filter เฉพาะที่เลือก
            .map(({ id, name, def, strategy }) => ({
                id,
                name,
                def,
                strategy,
            }));


        sendMessage("/app/minion-config", {
            playerId: localStorage.getItem("playerId"),
            minions: cleanMinions,
        });

        // 👇 จากนั้นค่อยเปลี่ยนหน้า
        const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
        const queryParams = new URLSearchParams({
            selectedMinions: JSON.stringify(selectedMinionData)
        });

        console.log("🚀 Sending minions: ", cleanMinions);

        router.push(`/GamePage?${queryParams.toString()}`);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4 w-full h-full">
            <h1 className="text-4xl font-bold text-gray-800 mt-6">Please Select Minions</h1>
            <h2 className="text-xl font-bold text-gray-700 mt-4 mb-10">Selected Minions {selectedMinions.length}</h2>
            <div className="grid grid-cols-5 gap-12">
                {minions.map((minion) => (
                    <div key={minion.id}
                         className="bg-white p-6 w-80 h-120 rounded-lg shadow-md text-center flex flex-col items-center border-2 border-black">
                        <img src={minion.image} alt={minion.name} className="w-64 h-64 mx-auto mb-4 border-2 border-black rounded-lg"/>

                        <button
                            className={`w-full py-3 rounded-lg text-lg ${selectedMinions.includes(minion.id) ? 'bg-green-500 text-white' : 'bg-gray-300'}`}
                            onClick={() => toggleSelectMinion(minion.id)}
                        >
                            {selectedMinions.includes(minion.id) ? "Selected" : "Select"}
                        </button>

                        <button
                            className="w-full mt-3 py-3 bg-blue-500 text-white rounded-lg text-lg disabled:opacity-50"
                            onClick={() => openCustomizeModal(selectedMinions.indexOf(minion.id))}
                            disabled={!selectedMinions.includes(minion.id)}
                        >
                            Customize
                        </button>
                    </div>
                ))}
            </div>

            {showModal && currentMinionIndex !== null && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center w-full h-full">
                    <div className="bg-white rounded-lg shadow-md flex flex-row w-[80vw] h-[90vh] overflow-hidden font-[Verdana] border-[3px] border-black">

                        {/* 🟡 ซ้าย: รูปมินเนียน */}
                        <div
                            className="flex flex-col items-center justify-start p-6 w-1/2 border-r-[3px] border-black relative">
                            <h1 className="text-4xl font-bold text-gray-800 mt-10 mb-4 text-center">
                                Customize Minion
                            </h1>

                            <div
                                className="w-[500px] h-[500px] border-[3px] border-gray-700 rounded-lg flex items-center justify-center bg-white shadow-md mt-20">
                                <img
                                    src={minions.find(m => m.id === selectedMinions[currentMinionIndex])?.image}
                                    alt="Minion"
                                    className="w-[400px] h-[400px] object-contain"
                                />
                            </div>
                        </div>


                        {/* 🔵 ขวา: แบบฟอร์มกรอก */}
                        <div className="flex flex-col justify-between p-6 w-1/2">
                            <div className="space-y-4 text-black">
                                <div className="flex items-center justify-between">
                                    <label className="text-lg font-bold lowercase">minion’s name :</label>
                                    <input
                                        className="border border-black rounded px-2 py-1 w-1/2 text-sm"
                                        type="text"
                                        value={customName}
                                        onChange={(e) => setCustomName(e.target.value)}
                                    />
                                </div>

                                <div className="flex items-center justify-between">
                                    <label className="text-lg font-bold lowercase">minion’s defense :</label>
                                    <input
                                        className="border border-black rounded px-2 py-1 w-1/2 text-sm"
                                        type="number"
                                        value={customDefense}
                                        onChange={(e) => setCustomDefense(e.target.value)}
                                    />
                                </div>

                                <div>
                                    <label className="text-lg font-bold lowercase block mb-1">
                                        write your minion’s strategy down here ...
                                    </label>
                                    <textarea
                                        className="w-full h-[65vh] border border-black rounded px-2 py-1 text-sm"
                                        value={customStrategy}
                                        onChange={(e) => setCustomStrategy(e.target.value)}
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
                    onClick={() => router.push("/GamePage")}
                    className={`text-2xl ${isReadyToStart ? 'bg-orange-500 hover:bg-orange-700' : 'bg-gray-500'} text-white font-bold py-2 px-4 rounded`}
                    disabled={!isReadyToStart}
                >
                    START
                </button>
            </div>
        </div>
    );
}