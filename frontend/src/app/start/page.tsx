"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";

export default function StartPage() {
    const router = useRouter();

    return (
        <main>
            <div
                className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full"
                style={{
                    backgroundImage: "url('/image/Desktop - 1.png')",
                    //backgroundSize: "1920px 1080px",
                    backgroundPosition: "center",
                }}
            ></div>

            <div
                onClick={() => router.push("/select-mode")}
                className="absolute cursor-pointer bottom-80 left-[calc(33%+100px)]"
            >
                <Image
                    src="/image/startbutton.png"
                    alt="start"
                    width={450}
                    height={450}
                    className="hover:opacity-90 transition-opacity"
                />
            </div>

        </main>
    );
}
