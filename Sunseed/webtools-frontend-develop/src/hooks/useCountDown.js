import { useEffect, useState } from 'react';

const useCountdown = (targetDate) => {
  const [countDown, setCountDown] = useState(
    targetDate ? targetDate - new Date().getTime() : 0
  );

  useEffect(() => {
    if (!targetDate) return;

    const interval = setInterval(() => {
      const timeRemaining = targetDate - new Date().getTime();
      if (timeRemaining > 0) {
        setCountDown(timeRemaining);
      } else {
        clearInterval(interval);
        setCountDown(0);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [targetDate]);

  return getReturnValues(countDown);
};

const getReturnValues = (countDown) => {
  const days = Math.floor(countDown / (1000 * 60 * 60 * 24));
  const hours = Math.floor((countDown % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((countDown % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((countDown % (1000 * 60)) / 1000);

  return [days, hours, minutes, seconds];
};

export { useCountdown };
