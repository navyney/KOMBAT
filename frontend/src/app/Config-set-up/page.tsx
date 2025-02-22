"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useState } from "react";

export default function StartPage() {
    const router = useRouter();
    const [config, setConfig] = useState({
        spawnedCost: "",
        hexPurchasedCost: "",
        initialBudget: "",
        initialHP: "",
        turnBudget: "",
        maxBudget: "",
        interestPercentage: "",
        maxTurn: "",
        maxSpawn: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setConfig({ ...config, [e.target.name]: e.target.value });
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full p-8"
              style={{ backgroundImage: "url('/image/config.png')" }}>

            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg mt-16 space-y-4 space-x-5">
                <h1 className="text-xl font-bold text-center">set-up your Game Configuration</h1>
                {Object.keys(config).map((key) => (
                    <div key={key} className="flex justify-between items-center">
                        <label className="font-medium capitalize">{key.replace(/([A-Z])/g, ' $1')} :</label>
                        <input
                            type="number"
                            name={key}
                            value={config[key as keyof typeof config]}
                            onChange={handleChange}
                            className="border rounded p-2 w-32 text-center"
                        />
                    </div>
                ))}
            </div>

            <div
                onClick={() => router.push("/select-mode")}
                className="absolute cursor-pointer bottom-10 left-20"
            >
                <Image
                    src="/image/back-button.png"
                    alt="back"
                    width={150}
                    height={150}
                    className="hover:opacity-75 transition-opacity"
                />
            </div>

            <div
                onClick={() => router.push("/select-type")}
                className="absolute cursor-pointer bottom-10 right-20"
            >
                <Image
                    src="/image/next-button.png"
                    alt="next"
                    width={150}
                    height={150}
                    className="hover:opacity-75 transition-opacity"
                />
            </div>
        </main>
    );
}
