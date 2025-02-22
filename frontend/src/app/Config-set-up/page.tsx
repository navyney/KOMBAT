"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import {useEffect, useState} from "react";

// สาธุขอให้ push ได้

export default function StartPage() {
    const router = useRouter();
    const [config, setConfig] = useState({
        spawnedCost: "" as number | "",
        hexPurchasedCost: "" as number | "",
        initialBudget: "" as number | "",
        initialHP: "" as number | "",
        turnBudget: "" as number | "",
        maxBudget: "" as number | "",
        interestPercentage: "" as number | "",
        maxTurn: "" as number | "",
        maxSpawn: "" as number | "",
    });

    const [error, setError] = useState<string | null>(null);
    const [isConfirmed, setIsConfirmed] = useState(false);

    useEffect(() => {
        const savedConfig = localStorage.getItem("gameConfig");
        if (savedConfig) {
            setConfig(JSON.parse(savedConfig));
        }
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
            setConfig({ ...config, [e.target.name]: value === "" ? "" : parseFloat(value) });
        }
        setIsConfirmed(false);
    };

    const handleConfirm = () => {
        const hasError = Object.values(config).some(value => value === "" || isNaN(Number(value)));
        if (hasError) {
            setError("Allow only numbers. Please check your inputs.");
            setIsConfirmed(false);
        } else {
            setError(null);
            setIsConfirmed(true);
            localStorage.setItem("gameConfig", JSON.stringify(config));
        }
    };

    const handleNext = () => {
        if (isConfirmed) {
            localStorage.setItem("gameConfig", JSON.stringify(config));
            router.push("/select-type");
        }
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full p-8"
              style={{backgroundImage: "url('/image/config.png')"}}>

            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg mt-16 space-y-4 space-x-5">
                <h1 className="text-xl font-bold text-center">Set Up Your Game Configuration</h1>
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

                {error && <p className="text-red-500 text-center font-bold">{error}</p>}

                <div className="mt-4 flex justify-end">
                    <button
                        onClick={handleConfirm}
                        className="mt-4 bg-blue-500 text-white py-2 px-10 rounded hover:bg-blue-700 transition"
                    >
                        Confirm Config
                    </button>
                </div>

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
                onClick={handleNext}
                className={`absolute cursor-pointer bottom-10 right-20 ${
                    !isConfirmed ? "opacity-50 cursor-not-allowed" : "hover:opacity-75 transition-opacity"
                }`}
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
