'use client'
import {useEffect, useState} from "react";

export interface Hex {
    rows: number,
    cols: number,
    size: number,
    distance: number,
    initialHex_Ally: number[],
    initialHex_Opponent: number[],

    onHexClick?: (hexId: number) => void
    initialHex_Yellow: number[]
}

const HexGrid: React.FC<Hex> = ({rows, cols, size, distance, initialHex_Ally, initialHex_Opponent, onHexClick, initialHex_Yellow}) => {
    const [selectedAllyHexes, setSelectedAllyHexes] = useState<number[]>([]);
    const [selectedOpponentHexes, setSelectedOpponentHexes] = useState<number[]>([]);
    const [selectedYellowHex, setSelectedYellowHex] = useState<number[]>([]);
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

    const getNeighbors = (hexId: number) => {
        const row = Math.floor(hexId / 10);
        const col = hexId % 10;
        const isOdd = col % 2 !== 0;
        return [
            hexId - 10, // ด้านบน
            hexId + 10, // ด้านล่าง
            isOdd ? hexId + 1 : hexId - 9, // บนขวา
            isOdd ? hexId - 1 : hexId - 11, // บนซ้าย
            isOdd ? hexId + 9 : hexId - 1, // บนซ้าย / บนขวา
            isOdd ? hexId + 11 : hexId + 1, // ล่างขวา / ล่างซ้าย
        ];
    };

        const getlistNeighbors = (listHex: number[]): number[] => {
            const a: number[] = [];
            for (let i = 0; i < listHex.length; i++) {
                a.push(...getNeighbors(listHex[i]));
            }
            return a;
        };

    //เช็คว่าช่องที่กดติดกับช่องสีเขียวหรือไม่
    const canClickHex = (hexId: number) => {
        if (selectedAllyHexes.includes(hexId) || selectedOpponentHexes.includes(hexId)) return false;
        return (
            selectedAllyHexes.some((hex) => getNeighbors(hex).includes(hexId)) ||
            selectedOpponentHexes.some((hex) => getNeighbors(hex).includes(hexId))
        );
    };

    const toggleHexColor = (hexId: number) => {
        if (!canClickHex(hexId)) return;
        if (onHexClick) {
            onHexClick(hexId);
        }
    };

    useEffect(() => {

        setSelectedAllyHexes(initialHex_Ally);
        setSelectedOpponentHexes(initialHex_Opponent);
        setSelectedYellowHex(getlistNeighbors(initialHex_Yellow));
    }, [initialHex_Ally, initialHex_Opponent,selectedYellowHex]);


    return (
        <svg
            className={"margin-left flex flex-col items-center justify-center min-h-screen"}
            //width={cols * (2 * hexWidth) + 2 * distance}
            width={cols * (hexWidth) + 2 * distance}
            height={rows * (hexHeight + size) + 2 * distance}
            style={{display: "block"}}
        >
            {Array.from({length: rows}).map((_, row) =>
                Array.from({length: cols}).map((_, col) => {
                    const x = col * xOffset * 3;
                    const y = row * hexHeight + (col % 2 === 1 ? 0 : hexHeight / 2);
                    const hexId = (row + 1) * 10 + (col + 1);

                    return (
                        <g key={`${row}-${col}`} transform={`translate(${x},${y})`}
                           onClick={() => toggleHexColor(hexId)}>
                            <polygon
                                points={hexagonPath}
                                stroke="black"
                                strokeWidth={1.5}
                                fill={
                                    selectedAllyHexes.includes(hexId)
                                        ? "#afefaf"
                                        : selectedOpponentHexes.includes(hexId)
                                            ? "#e7a09a"
                                            : selectedYellowHex?.includes(hexId)
                                                ? "yellow"
                                                : "#f6f9f8"
                                }
                                style={{cursor: "pointer"}}
                            />
                            <text
                                x={size + distance}
                                y={size + distance}
                                fontSize="14"
                                textAnchor="middle"
                                fill={
                                    selectedAllyHexes.includes(hexId) || selectedOpponentHexes.includes(hexId) || selectedYellowHex?.includes(hexId)
                                        ? "#f6f9f8"
                                        : "#f6f9f8"}
                            >
                                {hexId}
                            </text>
                        </g>
                    );
                })
            )}
        </svg>
    );
}
;

export default HexGrid;