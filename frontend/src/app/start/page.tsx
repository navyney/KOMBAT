// "use client";
// import { useRouter } from "next/navigation";
//
// export default function Page() {
//     const router = useRouter();
//
//     return (
//         <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100"
//              style={{ backgroundImage: "url('/public/... .png')" }}
//         >
//             <h1 className="text-6xl font-bold text-orange-600 shadow-lg">KOMBAT</h1>
//             <button
//                 onClick={() => router.push("/select-mode")}
//                 className="mt-8 px-6 py-3 text-lg font-bold text-white bg-gray-800 rounded-lg shadow-md hover:bg-gray-700 transition"
//             >
//                 START
//             </button>
//         </div>
//     );
// }

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
                    alt="End Turn"
                    width={450}
                    height={450}
                    className="hover:opacity-90 transition-opacity"
                />
            </div>

        </main>
    );
}
