import { useState } from "react";

const HexGrid = ({ rows = 8, cols = 8, size = 40, distance = 20 }) => {
  const [selectedHexes, setSelectedHexes] = useState<number[]>([]);
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

  const toggleHexColor = (hexId: number) => {
    setSelectedHexes((prev) =>
        prev.includes(hexId) ? prev.filter((id) => id !== hexId) : [...prev, hexId]
    );
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
              const hexId = row * cols + col + 1;

              return (
                  <g key={`${row}-${col}`} transform={`translate(${x},${y})`} onClick={() => toggleHexColor(hexId)}>
                    <polygon
                        points={hexagonPath}
                        stroke="white"
                        strokeWidth={1}
                        fill={selectedHexes.includes(hexId) ? "white" : "black"}
                        style={{ cursor: "pointer" }}
                    />
                    <text
                        x={size + distance}
                        y={size + distance}
                        fontSize="14"
                        textAnchor="middle"
                        fill={selectedHexes.includes(hexId) ? "black" : "white"}
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