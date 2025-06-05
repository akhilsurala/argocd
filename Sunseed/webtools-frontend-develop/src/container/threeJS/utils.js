


const hexToRgb = (hex) => {
    const bigint = parseInt(hex.slice(1), 16);
    return { r: (bigint >> 16) & 255, g: (bigint >> 8) & 255, b: bigint & 255 };
}



export const interpolateColor = (start, end, normalizedValue) => {
    const startRgb = hexToRgb(start.color);
    const endRgb = hexToRgb(end.color);

    const rangePercentage =
        (normalizedValue - start.percentage) /
        (end.percentage - start.percentage);

    const r = startRgb.r + rangePercentage * (endRgb.r - startRgb.r);
    const g = startRgb.g + rangePercentage * (endRgb.g - startRgb.g);
    const b = startRgb.b + rangePercentage * (endRgb.b - startRgb.b);

    return { r: Math.round(r), g: Math.round(g), b: Math.round(b) };
}

export const findGradientSegment = (normalizedValue, gradientStops) => {

    const memoizedGradientStops = gradientStops;

    for (let i = 0; i < memoizedGradientStops.length - 1; i++) {
        const currentStop = memoizedGradientStops[i];
        const nextStop = memoizedGradientStops[i + 1];

        if (
            normalizedValue >= currentStop.percentage &&
            normalizedValue <= nextStop.percentage
        ) {
            return { start: currentStop, end: nextStop };
        }
    }
    return {
        start: memoizedGradientStops[memoizedGradientStops.length - 1],
        end: memoizedGradientStops[memoizedGradientStops.length - 1],
    };
}

