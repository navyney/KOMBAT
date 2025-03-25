// "use client";
// import {useEffect, useState} from "react";
// import { useRouter } from "next/navigation";
//
// import { updateMinion, resetMinion,MinionType } from "@/stores/slices/minionTypeSlice";
// import { useDispatch, useSelector } from "react-redux";
// import { RootState } from "@/stores/store";
//
// import { useWebSocket } from "@/hooks/useWebsocket";
// import {usePlayerId} from "@/hooks/usePlayerId";
// import {setSelection, toggleSelection} from "@/stores/slices/selectionStateSlice";
//
// const minions = [
//     { id: 1, name: "pawn", image: "/image/minions/white-pawn.jpeg", color: "white" },
//     { id: 2, name: "rook", image: "/image/minions/white-rook.jpeg", color: "white" },
//     { id: 3, name: "knight", image: "/image/minions/white-knight.jpeg", color: "white" },
//     { id: 4, name: "bishop", image: "/image/minions/white-bishop.jpeg", color: "white" },
//     { id: 5, name: "queen", image: "/image/minions/white-queen.jpeg", color: "white" }
// ];
//
// export default function SelectMinions() {
//     const [showModal, setShowModal] = useState(false);
//     const [currentMinionIndex, setCurrentMinionIndex] = useState<number | null>(null);
//     const [minionDetails, setMinionDetails] = useState<Record<number, { name: string; defense: number; strategy: string }>>({});
//     const router = useRouter();
//
//     const [customName, setCustomName] = useState<string>("");
//     const [customDefense, setCustomDefense] = useState<number | string>("");
//     const [customStrategy, setCustomStrategy] = useState<string>("");
//     const minionsFromRedux = useSelector((state: RootState) => state.miniontype);
//     const stateFromRedux = useSelector((state: RootState) => state.selectionState);
//     const dispatch = useDispatch();
//
//     const { connect, sendMessage, isConnected, subscribe, unsubscribe } = useWebSocket();
//     const playerId = usePlayerId();
//     const selectedMinions = stateFromRedux
//         .filter(item => item.isSelected && item.id.startsWith("select "))
//         .map(item => parseInt(item.id.replace("select ", "")));
//
//     useEffect(() => {
//         console.log("🌐 [SelectMinionsPage] WebSocket Connected:", isConnected());
//
//         sendMessage("/join-select-minion-type", { playerId });
//
//         const subToggle = subscribe("/topic/minion-select", (message) => {
//             const { id, isSelected, playerId: senderId } = JSON.parse(message.body);
//
//             // ✅ ถ้าไม่ใช่เราค่อย dispatch (กันไม่ให้ set ซ้ำ)
//             if (senderId !== playerId) {
//                 console.log("🧩 [WS Received from OTHER] id:", id, " isSelected:", isSelected);
//                 dispatch(setSelection({ id, isSelected }));
//             }
//         });
//
//
//         return () => {
//             unsubscribe(subToggle);
//         };
//
//     }, [dispatch]);
//
//     useEffect(() => {
//         console.log("🧠 Redux current state:", stateFromRedux);
//     }, [stateFromRedux]);
//
//     // const toggleSelectMinion = (id: number) => {
//     //     const idStr = `select ${id}`;
//     //     const isCurrentlySelected = selectedMinions.includes(id);
//     //
//     //     // ✅ อัปเดต local state
//     //     setSelectedMinions((prev) =>
//     //         isCurrentlySelected ? prev.filter((m) => m !== id) : [...prev, id]
//     //     );
//     //
//     //     // ✅ อัปเดต Redux selectionState
//     //     dispatch(setSelection({
//     //         id: idStr,
//     //         isSelected: !isCurrentlySelected
//     //     }));
//     //
//     //     // ✅ ส่งไป backend
//     //     sendMessage("/minion-select", {
//     //         playerId,
//     //         id: idStr,
//     //         isSelected: !isCurrentlySelected
//     //     });
//     // };
//
//     // const toggleSelectMinion = (id: number) => {
//     //     const idStr = `select ${id}`;
//     //     const isCurrentlySelected = stateFromRedux.find(item => item.id === idStr)?.isSelected ?? false;
//     //
//     //     // ✅ ไม่ต้อง setSelectedMinions แล้ว
//     //
//     //     // ✅ อัปเดต Redux
//     //     dispatch(setSelection({
//     //         id: idStr,
//     //         isSelected: !isCurrentlySelected
//     //     }));
//     //
//     //     // ✅ ส่งไป backend
//     //     sendMessage("/minion-select", {
//     //         playerId,
//     //         id: idStr,
//     //         isSelected: !isCurrentlySelected
//     //     });
//     // };
//
//     const toggleSelectMinion = (id: number) => {
//         const idStr = `select ${id}`;
//         const isCurrentlySelected = stateFromRedux.find(item => item.id === idStr)?.isSelected ?? false;
//
//         // ✅ อัปเดตตัวเองทันที
//         dispatch(setSelection({
//             id: idStr,
//             isSelected: !isCurrentlySelected
//         }));
//
//         // ✅ ส่งไป backend (จะกระจายให้ทุกคนรวมถึงตัวเองก็ได้)
//         sendMessage("/minion-select", {
//             playerId,
//             id: idStr,
//             isSelected: !isCurrentlySelected
//         });
//     };
//
//
//
//     const openCustomizeModal = (index: number) => {
//         if (index < selectedMinions.length) {
//             setCurrentMinionIndex(index);
//             setShowModal(true);
//
//             const minionId = selectedMinions[index];
//             const baseMinion = minions.find(m => m.id === minionId);
//             if (!baseMinion) return;
//
//             const reduxMinion = minionsFromRedux.find((m) => m.id === minionId);
//
//
//             if (reduxMinion) {
//                 setCustomName(reduxMinion.name);
//                 setCustomDefense(reduxMinion.def);
//                 setCustomStrategy(reduxMinion.strategy);
//             } else {
//                 setCustomName(baseMinion.name);
//                 setCustomDefense("");
//                 setCustomStrategy("");
//             }
//         }
//     };
//
//     const handleConfirm = () => {
//         if (currentMinionIndex !== null) {
//             const minionId = selectedMinions[currentMinionIndex];
//             const baseMinion = minions.find(m => m.id === minionId);
//             if (!baseMinion) return;
//
//             const updatedMinion: MinionType = {
//                 id: minionId,
//                 name: customName || baseMinion.name,
//                 def: Number(customDefense),
//                 strategy: customStrategy,
//             };
//
//             dispatch(updateMinion({
//                 id: minionId,
//                 name: customName || baseMinion.name,
//                 def: Number(customDefense),
//                 strategy: customStrategy
//             }));
//         }
//
//         setShowModal(false);
//     };
//
//     const isReadyToStart = selectedMinions.length > 0;
//
//     // const handleStartGame = () => {
//     //     const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
//     //     const queryParams = new URLSearchParams({
//     //         selectedMinions: JSON.stringify(selectedMinionData)
//     //     });
//     //     router.push(`/GamePage?${queryParams.toString()}`);
//     // };
//
//     // const handleStartGame = () => {
//     //     // 👇 ส่งข้อมูลผ่าน WebSocket ก่อน
//     //     const cleanMinions = minionsFromRedux
//     //         .filter((m) => selectedMinions.includes(m.id)) // ✅ filter เฉพาะที่เลือก
//     //         .map(({ id, name, def, strategy }) => ({
//     //             id,
//     //             name,
//     //             def,
//     //             strategy,
//     //         }));
//     //
//     //
//     //     sendMessage("/app/minion-config", {
//     //         playerId: localStorage.getItem("playerId"),
//     //         minions: cleanMinions,
//     //     });
//     //
//     //     // 👇 จากนั้นค่อยเปลี่ยนหน้า
//     //     const selectedMinionData = selectedMinions.map(id => minions.find(m => m.id === id));
//     //     const queryParams = new URLSearchParams({
//     //         selectedMinions: JSON.stringify(selectedMinionData)
//     //     });
//     //
//     //     console.log("🚀 Sending minions: ", cleanMinions);
//     //
//     //     router.push(`/GamePage?${queryParams.toString()}`);
//     // };
//
//     const handleStartGame = () => {
//         const selectedIds = stateFromRedux
//             .filter(item => item.isSelected && item.id.startsWith("select"))
//             .map(item => parseInt(item.id.replace("select ", "")));
//
//         const cleanMinions = minionsFromRedux
//             .filter(m => selectedIds.includes(m.id))
//             .map(({ id, name, def, strategy }) => ({
//                 id,
//                 name,
//                 def,
//                 strategy,
//             }));
//
//         sendMessage("/app/minion-config", {
//             playerId: localStorage.getItem("playerId"),
//             minions: cleanMinions,
//         });
//
//         const selectedMinionData = selectedIds.map(id => minions.find(m => m.id === id));
//         const queryParams = new URLSearchParams({
//             selectedMinions: JSON.stringify(selectedMinionData)
//         });
//
//         router.push(`/GamePage?${queryParams.toString()}`);
//     };
//
//
//     const handleMinionChange = (
//         id: number,
//         field: keyof MinionType,
//         value: string | number
//     ) => {
//         const minion = minionsFromRedux.find((m) => m.id === id);
//         if (!minion) return;
//
//         const updatedMinion: MinionType = {
//             ...minion,
//             [field]: field === "def" ? Number(value) : String(value), // def เป็นตัวเลข
//         };
//
//         dispatch(updateMinion(updatedMinion)); // ✅ อัปเดต Redux
//
//         // ส่ง minion ทั้งหมดกลับ backend
//         sendMessage("/minion-config", {
//             playerId,
//             minions: minionsFromRedux.map((m) =>
//                 m.id === id ? updatedMinion : m
//             ),
//         });
//
//         console.log("📤 Sent updated minions:", updatedMinion);
//     };
//
//     return (
//         <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4 w-full h-full">
//             <h1 className="text-4xl font-bold text-gray-800 mt-6">Please Select Minions</h1>
//             <h2 className="text-xl font-bold text-gray-700 mt-4 mb-10">Selected Minions {selectedMinions.length}</h2>
//             <div className="grid grid-cols-5 gap-12">
//                 {minions.map((minion, index) => {
//                     const selectionId = `select ${minion.id}`;
//                     const isSelected = stateFromRedux.find(item => item.id === selectionId)?.isSelected ?? false;
//
//                     return (
//                         <div key={minion.id} className="bg-white p-6 w-80 h-120 rounded-lg shadow-md text-center flex flex-col items-center border-2 border-black">
//                             <img src={minion.image} alt={minion.name} className="w-64 h-64 mx-auto mb-4 border-2 border-black rounded-lg" />
//
//                             <button
//                                 className={`w-full py-3 rounded-lg text-lg ${isSelected ? 'bg-green-500 text-white' : 'bg-gray-300'}`}
//                                 onClick={() => toggleSelectMinion(minion.id)}
//                             >
//                                 {isSelected ? "Selected" : "Select"}
//                             </button>
//
//                             <button
//                                 className="w-full mt-3 py-3 bg-blue-500 text-white rounded-lg text-lg disabled:opacity-50"
//                                 onClick={() => openCustomizeModal(minion.id)}
//                                 disabled={!isSelected}
//                             >
//                                 Customize
//                             </button>
//                         </div>
//                     );
//                 })}
//             </div>
//
//             {showModal && currentMinionIndex !== null && (
//                 <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center w-full h-full">
//                     <div className="bg-white rounded-lg shadow-md flex flex-row w-[80vw] h-[90vh] overflow-hidden font-[Verdana] border-[3px] border-black">
//
//                         {/* 🟡 ซ้าย: รูปมินเนียน */}
//                         <div
//                             className="flex flex-col items-center justify-start p-6 w-1/2 border-r-[3px] border-black relative">
//                             <h1 className="text-4xl font-bold text-gray-800 mt-10 mb-4 text-center">
//                                 Customize Minion
//                             </h1>
//
//                             <div
//                                 className="w-[500px] h-[500px] border-[3px] border-gray-700 rounded-lg flex items-center justify-center bg-white shadow-md mt-20">
//                                 <img
//                                     src={minions.find(m => m.id === selectedMinions[currentMinionIndex])?.image}
//                                     alt="Minion"
//                                     className="w-[400px] h-[400px] object-contain"
//                                 />
//                             </div>
//                         </div>
//
//
//                         {/* 🔵 ขวา: แบบฟอร์มกรอก */}
//                         <div className="flex flex-col justify-between p-6 w-1/2">
//                             <div className="space-y-4 text-black">
//                                 <div className="flex items-center justify-between">
//                                     <label className="text-lg font-bold lowercase">minion’s name :</label>
//                                     <input
//                                         className="border border-black rounded px-2 py-1 w-1/2 text-sm"
//                                         // type="text"
//                                         // value={customName}
//                                         // onChange={(e) => setCustomName(e.target.value)}
//                                         type="text"
//                                         value={customName}
//                                         onChange={(e) => {
//                                             setCustomName(e.target.value); // ยังเก็บไว้แสดงใน input
//                                             if (currentMinionIndex !== null) {
//                                                 handleMinionChange(selectedMinions[currentMinionIndex], "name", e.target.value);
//                                             }
//                                         }}
//                                     />
//                                 </div>
//
//                                 <div className="flex items-center justify-between">
//                                     <label className="text-lg font-bold lowercase">minion’s defense :</label>
//                                     <input
//                                         className="border border-black rounded px-2 py-1 w-1/2 text-sm"
//                                         // type="number"
//                                         // value={customDefense}
//                                         // onChange={(e) => setCustomDefense(e.target.value)}
//                                         type="number"
//                                         value={customDefense}
//                                         onChange={(e) => {
//                                             setCustomDefense(e.target.value);
//                                             if (currentMinionIndex !== null) {
//                                                 handleMinionChange(selectedMinions[currentMinionIndex], "def", e.target.value);
//                                             }
//                                         }}
//                                     />
//                                 </div>
//
//                                 <div>
//                                     <label className="text-lg font-bold lowercase block mb-1">
//                                         write your minion’s strategy down here ...
//                                     </label>
//                                     <textarea
//                                         className="w-full h-[65vh] border border-black rounded px-2 py-1 text-sm"
//                                         // value={customStrategy}
//                                         // onChange={(e) => setCustomStrategy(e.target.value)}
//                                         value={customStrategy}
//                                         onChange={(e) => {
//                                             setCustomStrategy(e.target.value);
//                                             if (currentMinionIndex !== null) {
//                                                 handleMinionChange(selectedMinions[currentMinionIndex], "strategy", e.target.value);
//                                             }
//                                         }}
//                                     />
//                                 </div>
//                             </div>
//
//                             <div className="flex justify-end mt-4">
//                                 <button
//                                     onClick={handleConfirm}
//                                     className="bg-green-700 hover:bg-green-800 text-white text-sm font-bold py-3 px-6 rounded-full tracking-widest mb-10"
//                                 >
//                                     APPLY
//                                 </button>
//                             </div>
//                         </div>
//                     </div>
//                 </div>
//             )}
//
//
//             <div className="mt-10 items-center justify-center">
//                 <button
//                     onClick={() => handleStartGame()}
//                     className={`text-2xl ${isReadyToStart ? 'bg-orange-500 hover:bg-orange-700' : 'bg-gray-500'} text-white font-bold py-2 px-4 rounded`}
//                     disabled={!isReadyToStart}
//                 >
//                     START
//                 </button>
//             </div>
//         </div>
//     );
// }

"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { updateMinion, MinionType } from "@/stores/slices/minionTypeSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/stores/store";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import { setSelection } from "@/stores/slices/selectionStateSlice";

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
    const { connect, sendMessage, isConnected, subscribe, unsubscribe } = useWebSocket();
    const playerId = usePlayerId();

    const [showModal, setShowModal] = useState(false);
    const [customName, setCustomName] = useState("");
    const [customDefense, setCustomDefense] = useState<number | "">("");
    const [customStrategy, setCustomStrategy] = useState("");
    const [currentMinionId, setCurrentMinionId] = useState<number | null>(null);
    const [customizingPlayer, setCustomizingPlayer] = useState<string | null>(null);


    const selectedMinions = stateFromRedux
        .filter(item => item.isSelected && item.id.startsWith("select "))
        .map(item => parseInt(item.id.replace("select ", "")));

    useEffect(() => {
        if (!playerId) return;

        // ✅ Join minion type selection
        sendMessage("/join-select-minion-type", { playerId });

        // 🔔 Subscribe: Minion select toggle
        const subToggle = subscribe("/topic/minion-select", (message) => {
            const { id, isSelected, playerId: senderId } = JSON.parse(message.body);
            if (senderId !== playerId) {
                dispatch(setSelection({ id, isSelected }));
            }
        });

        // 🔔 Subscribe: Minion customize apply
        const subApply = subscribe("/topic/minion-customize-apply", (message) => {
            const { id } = JSON.parse(message.body);
            // currently unused, but you can add logic here if needed
        });

        // 🔔 Subscribe: Close modal when needed
        const subClose = subscribe("/topic/minion-close-modal", (message) => {
            const { minionId } = JSON.parse(message.body);
            if (currentMinionId === minionId) {
                setShowModal(false);
            }
        });

        // 🔔 Subscribe: Open customize modal
        const subCustomize = subscribe("/topic/minion-customize", (message) => {
            const { minionId, playerId: senderId } = JSON.parse(message.body);

            setCurrentMinionId(minionId);
            setShowModal(true);
            setCustomizingPlayer(senderId);

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
        });

        // 🔔 Subscribe: Navigation
        const subNav = subscribe("/topic/navigate", (message) => {
            const action = message.body;
            if (action === "next") router.push("/select-type");
            else if (action === "back") router.push("/select-mode");
            else if (action === "start") router.push("/");
            else if (action === "gamepage") router.push("/GamePage");
        });

        const subMinionUpdated = subscribe("/topic/minion-updated", (message) => {
            const { playerId: senderId, minions } = JSON.parse(message.body);
            if (senderId === playerId) return; // ✅ ข้ามตัวเอง

            for (const minion of minions) {
                dispatch(updateMinion(minion));
            }
        });

        const subUpdated = subscribe("/topic/minion-updated", (message) => {
            const { minions, playerId: senderId } = JSON.parse(message.body);
            if (senderId === playerId) return; // อย่าอัปเดตตัวเองซ้ำ

            minions.forEach((minion: MinionType) => {
                dispatch(updateMinion(minion));
            });
        });

        // 🧼 Cleanup
        return () => {
            unsubscribe(subToggle);
            unsubscribe(subApply);
            unsubscribe(subClose);
            unsubscribe(subCustomize);
            unsubscribe(subNav);
            unsubscribe(subMinionUpdated);
            unsubscribe(subUpdated);
        };

    }, [dispatch, playerId, currentMinionId, router, minionsFromRedux]);

    const toggleSelectMinion = (id: number) => {
        const idStr = `select ${id}`;
        const isCurrentlySelected = stateFromRedux.find(item => item.id === idStr)?.isSelected ?? false;

        const newIsSelected = !isCurrentlySelected;

        dispatch(setSelection({ id: idStr, isSelected: newIsSelected }));

        sendMessage("/minion-select", {
            playerId,
            id: idStr,
            isSelected: newIsSelected,
        });
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

        // sendMessage("/minion-customize", {
        //     playerId,
        //     minionId,
        // });
    };

    // const handleConfirm = () => {
    //     if (currentMinionId !== null) {
    //         const baseMinion = minions.find(m => m.id === currentMinionId);
    //         if (!baseMinion) return;
    //
    //         const updatedMinion: MinionType = {
    //             id: currentMinionId,
    //             name: customName || baseMinion.name,
    //             def: Number(customDefense),
    //             strategy: customStrategy,
    //         };
    //
    //         dispatch(updateMinion(updatedMinion));
    //     }
    //     setShowModal(false);
    // };
    const handleConfirm = () => {
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

            // ✅ Broadcast ไปยังทุกคนว่า modal นี้ปิดได้แล้ว
            sendMessage("/topic/minion-close-modal", {
                playerId,
                minionId: currentMinionId,
            });
        }

        setShowModal(false); // ✅ ปิดของตัวเองทันที
    };


    const handleStartGame = () => {
        const selectedIds = selectedMinions;

        const cleanMinions = minionsFromRedux
            .filter(m => selectedIds.includes(m.id))
            .map(({ id, name, def, strategy }) => ({ id, name, def, strategy }));

        console.log("🔥 Selected IDs:", selectedIds);
        console.log("📦 Clean Minions:", cleanMinions);

        sendMessage("/minion-config", {
            playerId,
            minions: cleanMinions,
        });

        const selectedMinionData = selectedIds.map(id => minions.find(m => m.id === id));
        const queryParams = new URLSearchParams({
            selectedMinions: JSON.stringify(selectedMinionData)
        });

        sendMessage("/navigate", "gamepage");
    };

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

        sendMessage("/minion-update", {
            playerId,
            minions: [updatedMinion],
        });
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