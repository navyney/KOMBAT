"use client";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";

export default function Page() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const numberOfTypes = parseInt(searchParams.get("types") as string) || 1;

    const [currentMinion, setCurrentMinion] = useState(1);
    const [minions, setMinions] = useState(Array(numberOfTypes).fill({ name: "", defense: "", strategy: "" }));

    const handleNext = () => {
        if (currentMinion < numberOfTypes) {
            setCurrentMinion(currentMinion + 1);
        } else {
            router.push("/game"); // เมื่อกรอกข้อมูลครบทุกตัวแล้วไปหน้า game
        }
    };

    const handleChange = (e :any, field :any) => {
        const updatedMinions = [...minions];
        updatedMinions[currentMinion - 1][field] = e.target.value;
        setMinions(updatedMinions);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4">
            <h1 className="text-4xl font-bold text-gray-800 mb-8">Minion's Setup</h1>
            <div className="w-full max-w-md bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-bold text-gray-700 mb-4">Minion {currentMinion} of {numberOfTypes}</h2>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="minionName">
                        Minion's Name:
                    </label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        id="minionName"
                        type="text"
                        placeholder="Enter minion's name"
                        value={minions[currentMinion - 1].name}
                        onChange={(e) => handleChange(e, "name")}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="minionDefense">
                        Minion's Defense:
                    </label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        id="minionDefense"
                        type="text"
                        placeholder="Enter minion's defense"
                        value={minions[currentMinion - 1].defense}
                        onChange={(e) => handleChange(e, "defense")}
                    />
                </div>
                <div className="mb-6">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="strategy">
                        Write your minion's strategy down here:
                    </label>
                    <textarea
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        id="strategy"
                        placeholder="Enter strategy"
                        value={minions[currentMinion - 1].strategy}
                        onChange={(e) => handleChange(e, "strategy")}
                    ></textarea>
                </div>
                <div className="flex justify-between">
                    <button
                        onClick={() => router.back()}
                        className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                    >
                        BACK
                    </button>
                    <button
                        onClick={handleNext}
                        className="bg-orange-500 hover:bg-orange-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                    >
                        {currentMinion < numberOfTypes ? "NEXT" : "START GAME"}
                    </button>
                </div>
            </div>
        </div>
    );
}