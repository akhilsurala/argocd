import * as React from 'react';
import SvgIcon from '@mui/material/SvgIcon';

export function PvIcon(props) {
  return (
    <SvgIcon {...props}  viewBox="0 0 16 11" >
      <path
        d="M8.53129 4V8.89205H2.8125L3.95064 4.31117C3.99747 4.12998 4.18951 4 4.40964 4H8.53129Z"
        fill={props.fill}
      />
      <path
        d="M8.53182 9.67969V14.5757H1.97465C1.83414 14.5757 1.69831 14.5245 1.60932 14.4299C1.52033 14.3393 1.48754 14.2172 1.51565 14.103L2.61631 9.67969H8.53182Z"
        fill={props.fill}
      />
      <path
        d="M15.1875 8.89205H9.46875V4H13.5904C13.8105 4 14.0026 4.12998 14.0494 4.31117L15.1875 8.89205Z"
        fill={props.fill}
      />
      <path
        d="M16.4849 14.103C16.513 14.2172 16.4802 14.3393 16.3912 14.4299C16.3023 14.5245 16.1664 14.5757 16.0259 14.5757H9.46875V9.67969H15.3843L16.4849 14.103Z"
        fill={props.fill}
      />
    </SvgIcon>
  );
}