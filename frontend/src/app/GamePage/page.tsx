// 'use client';
//
// import Home from "@/app/hexMap/page";
//
// export default function GamePage() {
//     return (
//         <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full ooverflow-hidden overflow-y-hidden">
//                 <Home />
//         </main>
//     );
// }

'use client';

import { useState } from 'react';
import HexGrid from '@/component/HexGrid';

export default function GamePage() {
    const [player1, setPlayer1] = useState({
        budget: 100,
        minions: 5,
        areas: 5,
        isBuying: false,
    });

    const [player2, setPlayer2] = useState({
        budget: 100,
        minions: 5,
        areas: 5,
        isBuying: false,
    });

    const [turn, setTurn] = useState(1); // 1 for Player 1, 2 for Player 2

    const handleBuyArea = () => {
        if (turn === 1) {
            setPlayer1((prev) => ({ ...prev, isBuying: !prev.isBuying }));
        } else {
            setPlayer2((prev) => ({ ...prev, isBuying: !prev.isBuying }));
        }
    };

    const handleHexClick = (hexId: number) => {
        if (turn === 1 && player1.isBuying) {
            setPlayer1((prev) => ({
                ...prev,
                areas: prev.areas + 1,
                budget: prev.budget - 10, // Adjust cost as needed
                isBuying: false,
            }));
        } else if (turn === 2 && player2.isBuying) {
            setPlayer2((prev) => ({
                ...prev,
                areas: prev.areas + 1,
                budget: prev.budget - 10, // Adjust cost as needed
                isBuying: false,
            }));
        }
    };

    const endTurn = () => {
        setTurn((prevTurn) => (prevTurn === 1 ? 2 : 1));
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full overflow-hidden">
            <div className="flex justify-between w-full p-4">
                {/* Player 1 UI */}
                <div className="flex flex-col items-center gap-2">
                    <h2 className="text-xl font-bold">Player 1</h2>
                    <div className="bg-yellow-200 p-2 rounded">💰 Budget: {player1.budget}</div>
                    <div className="bg-green-200 p-2 rounded">👥 Minions: {player1.minions}</div>
                    <div className="bg-red-200 p-2 rounded">🔲 Areas: {player1.areas}</div>
                    <button
                        className={`bg-blue-500 text-white p-2 rounded ${player1.isBuying ? 'opacity-50' : ''}`}
                        onClick={handleBuyArea}
                        disabled={turn !== 1 || player1.budget < 10}>
                        Buy Area
                    </button>
                </div>

                {/* Turn Display */}
                <div className="flex flex-col items-center gap-2">
                    <h2 className="text-2xl font-bold">Turn {turn}</h2>
                    <button className="bg-green-500 text-white p-2 rounded" onClick={endTurn}>
                        End Turn
                    </button>
                </div>

                {/* Player 2 UI */}
                <div className="flex flex-col items-center gap-2">
                    <h2 className="text-xl font-bold">Player 2</h2>
                    <div className="bg-yellow-200 p-2 rounded">💰 Budget: {player2.budget}</div>
                    <div className="bg-green-200 p-2 rounded">👥 Minions: {player2.minions}</div>
                    <div className="bg-red-200 p-2 rounded">🔲 Areas: {player2.areas}</div>
                    <button
                        className={`bg-blue-500 text-white p-2 rounded ${player2.isBuying ? 'opacity-50' : ''}`}
                        onClick={handleBuyArea}
                        disabled={turn !== 2 || player2.budget < 10}>
                        Buy Area
                    </button>
                </div>
            </div>

            <div
                className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full ooverflow-hidden overflow-y-hidden"
            >
            <HexGrid
                rows={8}
                cols={8}
                size={50}
                distance={20}
                initialHex_Ally={[11, 12, 13, 21, 22]}
                initialHex_Opponent={[77, 78, 86, 87, 88]}
                onHexClick={handleHexClick}
            />
            </div>
        </main>
    );
}



