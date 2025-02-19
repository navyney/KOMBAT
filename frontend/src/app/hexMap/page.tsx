import HexGrid from "@/component/HexGrid"; // เปลี่ยน path ให้ถูกต้อง

export default function Home() {
    return (
        <main>
            <HexGrid rows={8} cols={8} size={40} distance={20} />
        </main>
    );
}
