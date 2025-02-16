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

export default function StartPage() {
    const router = useRouter();

    return (
        <div
            className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center fixsize-logo"
            style={{ backgroundImage: "url('/image/homepage.png')",
                     //backgroundSize: "1920px 1080px",
                     backgroundPosition: "center",
            }}
        >
            <h1
                className="text-6xl font-bold text-orange-600"
                style={{
                    textShadow: "4px 4px 0px black, -4px -4px 0px black, 4px -4px 0px black, -4px 4px 0px black",
                }}
            >
                KOMBAT
            </h1>
            <button
                onClick={() => router.push("/select-mode")}
                className="mt-6 px-8 py-4 bg-gray-800 text-white text-2xl font-bold rounded-lg shadow-lg hover:bg-gray-700 transition"
            >
                START
            </button>
        </div>
    );
}
