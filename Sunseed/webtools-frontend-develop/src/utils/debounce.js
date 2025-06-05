
let timer;
  export const myDebounce = (func, data, delay) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      func(data);
    }, delay);
  };