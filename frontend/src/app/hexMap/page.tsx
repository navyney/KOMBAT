import HexGrid from "@/component/HexGrid";

export default function Home() {
    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full ooverflow-hidden overflow-y-hidden">
            <HexGrid rows={8} cols={8} size={50} distance={20}
                     initialHex_Ally={[11, 12, 13, 21, 22]}
                     initialHex_Opponent={[77, 78, 86, 87, 88]}/>
        </main>
    );
}
