export const getThetaValue = (theta, thetaType) => {
    // Convert theta from degrees to radians
    const angle = Math.abs(theta) * (Math.PI / 180);

    const thetaValue = thetaType === "sin" ? Math.sin(angle) : thetaType === 'cos' ? Math.cos(angle) : thetaType === 'tan' ? Math.tan(angle) : 0;
    // Calculate the sine of the angle
    const threshold = 1e-10;

    // Normalize the result to zero if it's within the threshold
    if (Math.abs(thetaValue) < threshold) {
      return 0;
    }
    return thetaValue;
  };