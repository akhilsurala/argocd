import React from 'react';

import { Backdrop, CircularProgress } from '@mui/material';

const Loading = () => {
  return (
    <Backdrop sx={{ color: '#fff', zIndex: 2000 }} open={true}>
      <CircularProgress color='inherit' />
    </Backdrop>
  );
};

export default Loading;
