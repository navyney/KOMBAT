'use client'

import Image from "next/image";
import { useEffect, useState } from "react";

export interface Hex {
    rows: number;
    cols: number;
    size: number;
    distance: number;
    initialHex_Ally: number[];
    initialHex_Opponent: number[];
    onHexClick?: (hexId: number) => void;
    allyNeighbors: number[];
    opponentNeighbors: number[];
    currentPlayer: number;
    minions: { id: number; type: string; player: number }[];
    boardMinions: { id: number; type: string; player: number }[];
}

const HexGrid: React.FC<Hex> = ({
                                    rows, cols, size, distance, initialHex_Ally, initialHex_Opponent,
                                    onHexClick, allyNeighbors, opponentNeighbors, currentPlayer, boardMinions
                                }) => {
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

    useEffect(() => {
        setSelectedAllyHexes(initialHex_Ally);
        setSelectedOpponentHexes(initialHex_Opponent);
        setSelectedYellowHex(currentPlayer === 1 ? allyNeighbors : opponentNeighbors);
    }, [initialHex_Ally, initialHex_Opponent, allyNeighbors, opponentNeighbors, currentPlayer]);

    return (
        <div style={{ position: 'relative' }}>
            <svg
                className={"margin-left flex flex-col items-center justify-center min-h-screen"}
                width={cols * (hexWidth) + 2 * distance}
                height={rows * (hexHeight + size) + 2 * distance}
                style={{ display: "block" }}
            >
                {Array.from({ length: rows }).map((_, row) =>
                    Array.from({ length: cols }).map((_, col) => {
                        const x = col * xOffset * 3;
                        const y = row * hexHeight + (col % 2 === 1 ? 0 : hexHeight / 2);
                        const hexId = (row + 1) * 10 + (col + 1);

                        return (
                            <g key={hexId} transform={`translate(${x},${y})`}
                               onClick={() => onHexClick && onHexClick(hexId)}>
                                <polygon
                                    points={hexagonPath}
                                    stroke="black"
                                    strokeWidth={1.5}
                                    fill={
                                        selectedAllyHexes.includes(hexId)
                                            ? "#afefaf"
                                            : selectedOpponentHexes.includes(hexId)
                                                ? "#e7a09a"
                                                : selectedYellowHex.includes(hexId)
                                                    ? "yellow"
                                                    : "#f6f9f8"
                                    }
                                    style={{ cursor: "pointer" }}
                                />
                            </g>
                        );
                    })
                )}
            </svg>

            {boardMinions.map((minion) => {
                const row = Math.floor(minion.id / 10) - 1;
                const col = (minion.id % 10) - 1;
                const x = col * xOffset * 3;
                const y = row * hexHeight + (col % 2 === 1 ? 0 : hexHeight / 2);

                return (
                    <Image
                        key={`minion-${minion.id}`}
                        src={`/image/minions/chess-${minion.type}${minion.player === 1 ? '-green' : '-red'}.png`}
                        width={`${size}`}
                        height={`${size}`}
                        alt={`Minion ${minion.id}`}
                        style={{
                            position: 'absolute',
                            left: `${x + size/1.57 + size}px`,
                            top: `${y + size/1.55}px`,
                            width: `${size*1.25}px`,
                            height: `${size*1.25}px`,
                            pointerEvents: 'none'
                        }}
                    />
                );
                console.log(minion.type);
            })}
        </div>
    );
};

export default HexGrid;