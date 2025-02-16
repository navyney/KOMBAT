'use client'
import { useEffect, useState } from "react";

export interface Hex {
  rows: number;
  cols: number;
  size: number;
  distance: number;
  initialHex_Ally: number[];
}

const HexGrid: React.FC<Hex> = ({
                                  rows = 8,
                                  cols = 8,
                                  size = 40,
                                  distance = 20,
                                  initialHex_Ally = [11, 12, 13, 21, 22]
                                }) => {
  const [selectedHexes, setSelectedHexes] = useState<number[]>([]);
  const [selectedYellowHex, setSelectedYellowHex] = useState<number | null>(null);

  const hexWidth = size * 2;
  const hexHeight = Math.sqrt(3) * size;
  const xOffset = hexWidth * 0.25;
  const yOffset = hexHeight * 0.5;

  const hexagonPath = [
    [xOffset + distance, (yOffset * 2) + distance],
    [(xOffset * 3) + distance, (yOffset * 2) + distance],
    [(xOffset * 4) + distance, yOffset + distance],
    [(xOffset * 3) + distance, distance],
    [xOffset + distance, distance],
    [distance, yOffset + distance],
    [xOffset + distance, (yOffset * 2) + distance],
  ]
      .map(([x, y]) => `${x},${y}`)
      .join(" ");

  useEffect(() => {
    setSelectedHexes(initialHex_Ally);
  }, [initialHex_Ally]);

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

  const canClickHex = (hexId: number) => {
    if (selectedHexes.includes(hexId)) return false;
    if (selectedYellowHex === hexId) return true;

    return selectedHexes.some((hex) => getNeighbors(hex).includes(hexId));
  };

  const toggleHexColor = (hexId: number) => {
    if (!canClickHex(hexId)) return;

    setSelectedYellowHex((prev) => (prev === hexId ? null : hexId));
  };

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
              const hexId = (row + 1) * 10 + (col + 1);

              return (
                  <g key={`${row}-${col}`} transform={`translate(${x},${y})`} onClick={() => toggleHexColor(hexId)}>
                    <polygon
                        points={hexagonPath}
                        stroke="white"
                        strokeWidth={1}
                        fill={
                          selectedHexes.includes(hexId)
                              ? "green"
                              : selectedYellowHex === hexId
                                  ? "yellow"
                                  : "black"
                        }
                        style={{ cursor: canClickHex(hexId) ? "pointer" : "not-allowed" }}
                    />
                    <text
                        x={size + distance}
                        y={size + distance}
                        fontSize="14"
                        textAnchor="middle"
                        fill={selectedHexes.includes(hexId) || selectedYellowHex === hexId ? "black" : "white"}
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