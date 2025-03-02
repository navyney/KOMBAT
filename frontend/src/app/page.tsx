// export default function Home() {
//   return (
//       <div className="flex items-center justify-center min-h-screen bg-white">
//         <h1 className="text-4xl font-bold text-center text-black">
//           Hello World
//         </h1>
//       </div>
//   );
// }

// // @ts-ignore
// import Page from "./start/page";
//
// export default function Home() {
//     return <Page />;
// }

"use client";
import WebSocketTest from "@/app/components/WebSocketTest";

export default function Home() {
    return (
        <div>
            <h1>Welcome to KOMBAT</h1>
            <WebSocketTest />
        </div>
    );
}
