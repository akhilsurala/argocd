/*
 * Landing Messages
 *
 * This contains all the text for the Landing container.
 */

import { defineMessages } from "react-intl";

export const scope = "src.container.Home";

export default defineMessages({
  apvSim: {
    id: `${scope}.apvSim`,
    defaultMessage: "APV Sim",
  },
  apvSimOne: {
    id: `${scope}.apvSimOne`,
    defaultMessage: "Core design for APV Farm",
  },
  apvControl: {
    id: `${scope}.apvControl`,
    defaultMessage: "APV Control",
  },
  apvControlOne: {
    id: `${scope}.apvControlOne`,
    defaultMessage: "Controller and visualizer for your APV Farm Environment in realtime",
  },
  apvManage: {
    id: `${scope}.apvManage`,
    defaultMessage: "APV Manage",
  },
  apvManageOne: {
    id: `${scope}.apvManageOne`,
    defaultMessage: "General App for Managing Farm operations",
  },
});
