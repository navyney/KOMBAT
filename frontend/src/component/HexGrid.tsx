'use client'
import { useEffect, useState } from "react";

export interface Hex{
    rows:number
    cols:number
    size:number
    distance:number
    initialHex_Ally:number[]
    initialHex_Opponent:number[]
}
const HexGrid: React.FC<Hex> = ({ rows = 8, cols = 8, size = 40, distance = 20,initialHex_Ally = [11,12,13,21,22],initialHex_Opponent = [77,78,86,87,88] }) => {
  const [selectedAllyHexes, setSelectedAllyHexes] = useState<number[]>([]);
  const [selectedOpponentHexes, setSelectedOpponentHexes] = useState<number[]>([]);
  const [selectedYellowHex, setSelectedYellowHex] = useState<number | null>(null);
  const hexWidth = size * 2;
  const hexHeight = Math.sqrt(3) * size;
  const xOffset = hexWidth * 0.25;
  const yOffset = hexHeight * 0.5;

  const hexagonPath = [
    [xOffset + distance , (yOffset * 2) + distance],
    [(xOffset * 3) + distance,(yOffset * 2) + distance],
    [(xOffset * 4) + distance, yOffset + distance],
    [(xOffset * 3) + distance, distance],
    [xOffset + distance, distance],
    [distance, yOffset + distance],
    [xOffset + distance, (yOffset * 2) + distance],
  ]
    .map(([x, y]) => `${x},${y}`)
    .join(" ");

    const getNeighbors = (hexId: number) => {
        const row = Math.floor(hexId / 10);
        const col = hexId % 10;
        const isOdd = row % 2 !== 0;
        return [
          hexId - 10, // ด้านบน
          hexId + 10, // ด้านล่าง
          hexId - 1, // ซ้าย
          hexId + 1, // ขวา
          isOdd ? hexId - 9 : hexId - 11, // บนซ้าย / บนขวา
          isOdd ? hexId + 11 : hexId + 9, // ล่างขวา / ล่างซ้าย
        ];
      };

    //เช็คว่าช่องที่กดติดกับช่องสีเขียวหรือไม่
    const canClickHex = (hexId: number) => {
        if (selectedAllyHexes.includes(hexId) || selectedOpponentHexes.includes(hexId)) return false;
        if (selectedYellowHex === hexId) return true;

        return (
          selectedAllyHexes.some((hex) => getNeighbors(hex).includes(hexId)) ||
          selectedOpponentHexes.some((hex) => getNeighbors(hex).includes(hexId))
        );
      };

    const toggleHexColor = (hexId: number) => {
        if (!canClickHex(hexId)) return;

        setSelectedYellowHex((prev) => (prev === hexId ? null : hexId));

      };

      useEffect(() => {
        setSelectedAllyHexes(initialHex_Ally);
        setSelectedOpponentHexes(initialHex_Opponent);
      }, [initialHex_Ally, initialHex_Opponent]);

  return (
    <svg
      width={cols * (2 * hexWidth) + 2 * distance}
      height={rows * (hexHeight + size) + 2 * distance}
      style={{ backgroundColor: "black" }}
    >
      {Array.from({ length: rows }).map((_, row) =>
        Array.from({ length: cols }).map((_, col) => {
          const x = col * xOffset * 3;
          const y = row * hexHeight + (col % 2 === 1 ? 0 : hexHeight / 2);
          const hexId = (row+1) * 10  + (col + 1);

          return (
            <g key={`${row}-${col}`} transform={`translate(${x},${y})`} onClick={() => toggleHexColor(hexId)}>
              <polygon
                points={hexagonPath}
                stroke="white"
                strokeWidth={1}
                fill={
                    selectedAllyHexes.includes(hexId)
                      ? "green"
                        : selectedOpponentHexes.includes(hexId)
                        ? "red"
                            : selectedYellowHex === hexId
                            ? "yellow"
                                : "black"
                  }
                style={{ cursor: "pointer" }}
              />
              <text
                x={size + distance}
                y={size + distance}
                fontSize="14"
                textAnchor="middle"
                fill={
                    selectedAllyHexes.includes(hexId) || selectedOpponentHexes.includes(hexId) || selectedYellowHex === hexId
                      ? "black"
                      : "white"}
              >
                {hexId}
              </text>
            </g>
          );
        })
      )}
    </svg>
  );
};

export default HexGrid;
