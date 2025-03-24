"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";

import { updateMinion, resetMinion,MinionType } from "@/stores/slices/minionTypeSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/stores/store";



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

            const reduxMinion = minionsFromRedux.find(m => m.id === minionId);

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

    const isReadyToStart =
        selectedMinions.length > 0 &&
        selectedMinions.every((id) => {
            const baseMinion = minions.find((m) => m.id === id);
            if (!baseMinion) return false;
            const reduxMinion = minionsFromRedux.find((m) => m.name === baseMinion.name);
            return reduxMinion && reduxMinion.strategy.trim() !== "";
        });

    const handleStartGame = () => {
        const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
        const queryParams = new URLSearchParams({
            selectedMinions: JSON.stringify(selectedMinionData)
        });
        router.push(`/GamePage?${queryParams.toString()}`);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4 w-full h-full">
            <h1 className="text-4xl font-bold text-gray-800 mt-6">Please Select Minions</h1>
            <h2 className="text-xl font-bold text-gray-700 mt-4 mb-10">Selected Minions {selectedMinions.length}</h2>
            <div className="grid grid-cols-5 gap-12">
                {minions.map((minion) => (
                    <div key={minion.id}
                         className="bg-white p-6 w-80 h-120 rounded-lg shadow-md text-center flex flex-col items-center">
                        <img src={minion.image} alt={minion.name} className="w-64 h-64 mx-auto mb-4"/>

                        {/* ปรับให้ปุ่มพอดีกับกรอบ */}
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
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center w-auto h-auto">
                    <div className="bg-white p-8 rounded-lg shadow-md max-w-2xl w-[800px] h-[90vh]">
                        <h2 className="text-3xl font-bold text-gray-800 mb-6">
                            Customize {minions.find(m => m.id === selectedMinions[currentMinionIndex])?.name}
                        </h2>

                        {/* รูป Minion ใหญ่ขึ้น */}
                        <img src={minions.find(m => m.id === selectedMinions[currentMinionIndex])?.image}
                             alt="Minion"
                             className="w-32 h-32 mx-auto mb-4"/>

                        {/* กล่อง Minion Name และ Defense อยู่ข้างกัน */}
                        <div className="flex gap-4">
                            {/* Minion Name */}
                            <div className="flex-1">
                                <label className="block text-xl text-black mb-2">Minion's Name:</label>
                                <input
                                    className="w-full text-gray-700 p-3 border rounded text-lg"
                                    type="text"
                                    value={customName}
                                    onChange={(e) => setCustomName(e.target.value)}
                                />
                            </div>

                            {/* Minion Defense */}
                            <div className="flex-1">
                                <label className="block text-xl text-black mb-2">Minion's Defense:</label>
                                <input
                                    className="w-full text-gray-700 p-3 border rounded text-lg"
                                    type="number"
                                    value={customDefense}
                                    onChange={(e) => setCustomDefense(e.target.value)}
                                />
                            </div>
                        </div>

                        {/* Strategy ช่องเต็มความกว้าง */}
                        <label className="block text-xl text-black mt-4 mb-2">Strategy:</label>
                        <textarea
                            className="w-full h-[45vh] text-gray-700 mb-4 p-3 border rounded text-lg"
                            rows={4}
                            value={customStrategy}
                            onChange={(e) => setCustomStrategy(e.target.value)}
                        />

                        {/* ปุ่มใหญ่ขึ้น */}
                        <button
                            onClick={handleConfirm}
                            className="w-full py-3 px-6 bg-green-500 text-white rounded-lg text-xl"
                        >
                            Apply
                        </button>
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
