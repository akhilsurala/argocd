import { useEffect } from "react";

export default function useRenderTimer(name = "Component") {
    useEffect(() => {
        const start = performance.now();

        requestAnimationFrame(() => {
            const end = performance.now();
            console.log(`${name} render + paint took ${(end - start).toFixed(2)} ms`);
        });
    }, []);
}
