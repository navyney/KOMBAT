"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";

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

    const toggleSelectMinion = (id: number) => {
        setSelectedMinions((prev) =>
            prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
        );
    };

    const openCustomizeModal = (index: number) => {
        if (index < selectedMinions.length) {
            setCurrentMinionIndex(index);
            setShowModal(true);
        }
    };

    const handleConfirm = (details: { name: string; defense: number; strategy: string }) => {
        if (currentMinionIndex !== null) {
            setMinionDetails((prev) => ({
                ...prev,
                [selectedMinions[currentMinionIndex]]: details
            }));
        }
        setShowModal(false);
    };

    const isReadyToStart = selectedMinions.length > 0 && selectedMinions.every(id => minionDetails[id]);

    const handleStartGame = () => {
        const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
        const queryParams = new URLSearchParams({
            selectedMinions: JSON.stringify(selectedMinionData)
        });
        router.push(`/GamePage?${queryParams.toString()}`);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4 w-full h-full">
            <h1 className="text-4xl font-bold text-gray-800 mt-4 mb-8">Please Select Minions</h1>
            <h2 className="text-xl font-bold text-gray-700 mb-4">Selected Minions {selectedMinions.length}</h2>
            <div className="grid grid-cols-5 gap-4">
                {minions.map((minion) => (
                    <div key={minion.id} className="bg-white p-4 w-500 h-500 rounded-lg shadow-md text-center">
                        <img src={minion.image} alt={minion.name} className="w-24 h-24 mx-auto mb-2" />
                        <button
                            className={`w-full py-2 px-4 rounded ${selectedMinions.includes(minion.id) ? 'bg-green-500 text-white' : 'bg-gray-300'}`}
                            onClick={() => toggleSelectMinion(minion.id)}
                        >
                            {selectedMinions.includes(minion.id) ? "Selected" : "Select"}
                        </button>
                        <button
                            className="w-full mt-2 py-2 px-4 bg-blue-500 text-white rounded disabled:opacity-50"
                            onClick={() => openCustomizeModal(selectedMinions.indexOf(minion.id))}
                            disabled={!selectedMinions.includes(minion.id)}
                        >
                            Customize
                        </button>
                    </div>
                ))}
            </div>

            {showModal && currentMinionIndex !== null && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="bg-white p-6 rounded-lg shadow-md max-w-md">
                        <h2 className="text-xl font-bold text-gray-800 mb-4">Customize {minions.find(m => m.id === selectedMinions[currentMinionIndex])?.name}</h2>
                        <img src={minions.find(m => m.id === selectedMinions[currentMinionIndex])?.image} alt="Minion" className="w-24 h-24 mx-auto mb-2" />
                        <label className="block text-black mb-1">Minion's Name:</label>
                        <input className="w-full text-gray-700 mb-2 p-2 border rounded" type="text" />
                        <label className="block text-black mb-1">Minion's Defense:</label>
                        <input className="w-full text-gray-700 mb-2 p-2 border rounded" type="number" />
                        <label className="block text-black mb-1">Strategy:</label>
                        <textarea className="w-full text-gray-700 mb-2 p-2 border rounded" rows={3}></textarea>
                        <button
                            onClick={() => handleConfirm({
                                name: (document.querySelector('input[type="text"]') as HTMLInputElement).value,
                                defense: parseInt((document.querySelector('input[type="number"]') as HTMLInputElement).value),
                                strategy: (document.querySelector('textarea') as HTMLTextAreaElement).value
                            })}
                            className="w-full py-2 px-4 bg-green-500 text-white rounded"
                        >
                            Apply
                        </button>
                    </div>
                </div>
            )}

            <button
                onClick={handleStartGame}
                className={`mt-4 ${isReadyToStart ? 'bg-orange-500 hover:bg-orange-700' : 'bg-gray-500'} text-white font-bold py-2 px-4 rounded`}
                disabled={!isReadyToStart}
            >
                START
            </button>
        </div>
    );
}