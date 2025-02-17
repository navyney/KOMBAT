import HexGrid from "@/component/HexGrid";

export default function Home() {
    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full ooverflow-hidden overflow-y-hidden">
            <HexGrid rows={8} cols={8} size={50} distance={20}
                     initialHex_Ally={[11, 12, 13, 21, 22]}
                     initialHex_Opponent={[77, 78, 86, 87, 88]}/>
        </main>
    );
}


// import HexGrid from "@/component/HexGrid"; // เปลี่ยน path ให้ถูกต้อง
//
// export default function Home() {
//     return (
//         <main>
//             <HexGrid rows={8} cols={8} size={40} distance={20}
//                      initialHex_Ally={[11, 12, 13, 21, 22]}
//                      initialHex_Opponent={[77, 78, 86, 87, 88]}/>
//         </main>
//     );
// }

// import HexGrid from "@/component/HexGrid";
//
// export default function Home() {
//     return (
//         <div className="relative flex items-center justify-center min-h-screen bg-orange-100 p-4">
//             {/* ฝั่ง Player 1 (ซ้าย) */}
//             <div className="absolute top-10 left-4 flex flex-col items-start">
//                 <div className="flex items-center gap-2 mb-4">
//                     <div className="w-10 h-10 bg-brown-700 rounded-full"></div>
//                     <h2 className="text-lg font-bold text-brown-700">Player 1</h2>
//                 </div>
//                 <div className="flex flex-col gap-2">
//                     <button className="w-24 py-2 bg-yellow-200 rounded-full border border-black flex items-center justify-center">
//                         💰
//                     </button>
//                     <button className="w-24 py-2 bg-green-200 rounded-full border border-black flex items-center justify-center">
//                         👥
//                     </button>
//                     <button className="w-24 py-2 bg-red-200 rounded-full border border-black flex items-center justify-center">
//                         ⬡
//                     </button>
//                     <div className="flex gap-2">
//                         <button className="px-4 py-2 bg-blue-300 rounded-lg border border-black">SPAWN MINION</button>
//                         <button className="px-4 py-2 bg-green-300 rounded-lg border border-black">BUY AREA</button>
//                     </div>
//                 </div>
//             </div>
//
//             {/* ตรงกลางเป็น HexGrid */}
//             <main>
//                 <HexGrid
//                     rows={8}
//                     cols={8}
//                     size={40}
//                     distance={20}
//                     initialHex_Ally={[11, 12, 13, 21, 22]}
//                     initialHex_Opponent={[77, 78, 86, 87, 88]}
//                 />
//             </main>
//
//             {/* ฝั่ง Player 2 (ขวา) */}
//             <div className="absolute top-10 right-4 flex flex-col items-end">
//                 <div className="flex items-center gap-2 mb-4">
//                     <h2 className="text-lg font-bold text-orange-700">Player 2</h2>
//                     <div className="w-10 h-10 bg-orange-700 rounded-full"></div>
//                 </div>
//                 <div className="flex flex-col gap-2">
//                     <div className="flex gap-2">
//                         <button className="px-4 py-2 bg-blue-300 rounded-lg border border-black">SPAWN MINION</button>
//                         <button className="px-4 py-2 bg-green-300 rounded-lg border border-black">BUY AREA</button>
//                     </div>
//                     <button className="w-24 py-2 bg-red-200 rounded-full border border-black flex items-center justify-center">
//                         ⬡
//                     </button>
//                     <button className="w-24 py-2 bg-green-200 rounded-full border border-black flex items-center justify-center">
//                         👥
//                     </button>
//                     <button className="w-24 py-2 bg-yellow-200 rounded-full border border-black flex items-center justify-center">
//                         💰
//                     </button>
//                 </div>
//             </div>
//
//             {/* ปุ่ม DONE (ล่างซ้าย) */}
//             <button className="absolute bottom-10 left-10 w-24 h-24 bg-green-600 text-white text-xl font-bold rounded-full border-4 border-brown-700 shadow-lg">
//                 DONE
//             </button>
//
//             {/* แสดง Turn ด้านบนขวา */}
//             <div className="absolute top-10 right-1/2 translate-x-1/2 bg-orange-400 px-6 py-3 rounded-xl border border-black">
//                 <h3 className="text-xl font-bold text-black">Turn 1</h3>
//                 <p className="text-black text-sm text-center">Player 1 turn</p>
//             </div>
//         </div>
//     );
// }

// "use client"
//
// import { useState } from "react";
// // @ts-ignore
// import HexGrid from "@/component/HexGrid";
// // @ts-ignore
// import { Button } from "@/components/ui/button";
//
// export default function Home() {
//     const [mode, setMode] = useState<"spawn" | "buy" | null>(null);
//     const [ownedHexes, setOwnedHexes] = useState<{ [key: number]: "green" | "red" }>({
//         11: "green", 12: "green", 13: "green", 21: "green", 22: "green", // Player 1 owned
//         77: "red", 78: "red", 86: "red", 87: "red", 88: "red", // Player 2 owned
//     });
//     const [playerTurn, setPlayerTurn] = useState<"green" | "red">("green");
//
//     const handleHexClick = (hexId: number) => {
//         if (mode === "spawn") {
//             if (ownedHexes[hexId] === playerTurn) {
//                 console.log("Minion spawned on", hexId);
//             } else {
//                 alert("พื้นที่นี้ไม่ใช่ของคุณ ไม่สามารถลงได้");
//             }
//         } else if (mode === "buy") {
//             if (!ownedHexes[hexId]) {
//                 setOwnedHexes((prev) => ({ ...prev, [hexId]: playerTurn }));
//             }
//         }
//     };
//
//     return (
//         <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4">
//             <div className="flex gap-4 mb-4">
//                 <Button onClick={() => setMode("spawn")} className="bg-blue-400">Spawn Minion</Button>
//                 <Button onClick={() => setMode("buy")} className="bg-green-400">Buy Area</Button>
//             </div>
//             <main>
//                 <HexGrid
//                     rows={8}
//                     cols={8}
//                     size={40}
//                     distance={20}
//                     initialHex_Ally={[11, 12, 13, 21, 22]}
//                     initialHex_Opponent={[77, 78, 86, 87, 88]}
//                     onHexClick={handleHexClick}
//                     ownedHexes={ownedHexes}
//                 />
//             </main>
//         </div>
//     );
// }
