"use client";
import { useRouter } from "next/navigation";

export default function Page() {
    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100">
            <h1 className="text-4xl font-bold text-black mb-6">Select Mode</h1>
            <div className="grid grid-cols-3 gap-6">
                <button className="mode-button">Player VS Player</button>
                <button className="mode-button">Player VS Bot</button>
                <button className="mode-button">Bot VS Bot</button>
            </div>
            <button
                onClick={() => router.push("/")}
                className="mt-6 px-6 py-3 text-lg font-bold text-white bg-orange-500 rounded-lg shadow-md hover:bg-orange-400 transition"
            >
                BACK
            </button>
        </div>
    );
}
