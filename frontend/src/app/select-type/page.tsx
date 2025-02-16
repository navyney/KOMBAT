"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function SelectType() {
    const [numberOfTypes, setNumberOfTypes] = useState(1);
    const [showModal, setShowModal] = useState(false);
    const router = useRouter();

    const handleSubmit = (e: any) => {
        e.preventDefault();
        setShowModal(true);
    };

    const handleConfirm = () => {
        setShowModal(false); // ปิด modal
        router.push(`/setup?types=${numberOfTypes}`);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 p-4">
            <h1 className="text-4xl font-bold text-gray-800 mb-8">Select Number of Minion Types</h1>
            <form onSubmit={handleSubmit} className="w-full max-w-md bg-white p-6 rounded-lg shadow-md">
                <div className="mb-6">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="numberOfTypes">
                        How many types of minions do you want? (1 to 5 types)
                    </label>
                    <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline no-spinner"
                        id="numberOfTypes"
                        type="number"
                        min="1"
                        max="5"
                        value={numberOfTypes}
                        onChange={(e) => setNumberOfTypes(parseInt(e.target.value))}
                        required
                    />
                </div>
                <div className="flex justify-between">
                    <button
                        type="button"
                        onClick={() => router.back()}
                        className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                    >
                        BACK
                    </button>
                    <button
                        type="submit"
                        className="bg-orange-500 hover:bg-orange-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                    >
                        NEXT
                    </button>
                </div>
            </form>

            {/* Modal */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="bg-white p-6 rounded-lg shadow-md max-w-md">
                        <h2 className="text-xl font-bold text-gray-800 mb-4">Important Information</h2>
                        <p className="text-gray-700 mb-4">
                            Next, we will let you set-up your minion by assigning a name and strategy for each type.
                            About minion's Defense, both of you have to agree on defense, name, and strategy.
                            Then you have to write the same as the other player writes. You can assign 1 Defense
                            factor for each type or use the same Defense factor for all types.
                        </p>
                        <div className="flex justify-end">
                            <button
                                onClick={handleConfirm}
                                className="bg-orange-500 hover:bg-orange-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                            >
                                OK
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}