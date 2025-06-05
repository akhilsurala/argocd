/*
 * Landing Messages
 *
 * This contains all the text for the Landing container.
 */

import { defineMessages } from "react-intl";

export const scope = "src.container.PvParameters";
export const cropScope = "src.container.CropParameters";

export default defineMessages({
  azimuth: {
    id: `${scope}.azimuth`,
    defaultMessage: `
    Overall bearing of your system. <br />
    0 - NORTH <br />
    90 - EAST <br />
    180 - SOUTH <br />
    270 - WEST <br />
    Can be any number in between.However, if not sure, in Northern hemisphere, should be 0 an in southern hemisphere should be 180.
    `,
  },
  maxAnglesOfTracking: {
    id: `${scope}.maxAnglesOfTracking`,
    defaultMessage: `This is the MAX range by which the tracking module swings, from sunrise to sunset. If not sure, keep it 45 degrees.`,
  },
  moduleMaskPattern: {
    id: `${scope}.moduleMaskPattern`,
    defaultMessage: `A pattern of 110 will mean, to remove every third module.<br />
    A Pattern of 10 will mean to remove every second module.`,
  },
  gapBetweenModules: {
    id: `${scope}.gapBetweenModules`,
    defaultMessage: `If not sure, default gap may be kept 25 mm.<br />
    Increasing this gap may increase sun penetration in your farm, but at the cost of a more expensive structure.`,
  },
  height: {
    id: `${scope}.height`,
    defaultMessage: `The distance from ground to center of the cross section of your modules.`,
  },
  pitchOfRow: {
    id: `${scope}.pitchOfRow`,
    defaultMessage: `Inter Row Distance. For Agri only sims, use the PV pitch only - This would be a bound on Simulation size.`,
  },
  tiltIfFt: {
    id: `${scope}.tiltIfFt`,
    defaultMessage: `Tilt of module, if fixed tilt. Trackers have variable tilt.<br />If not sure, keep it same as your latitudetude.`,
  },

  // Crop Parameters
  o1: {
    id: `${cropScope}.o1`,
    defaultMessage: `An offset allows you to plant in 2 rows on 1 bed,<br />
    the offset is distance from center of bed at which seed is planted. Keep 0 for 1 row.`,
  },
  startPointOffset: {
    id: `${cropScope}.startPointOffset`,
    defaultMessage: `Changing this offset changes how your beds are w.r.t modules above. If the offset equals bed spacing, the beds again come to same position. Hence maximum offset is bed c/c spacing.`,
  },
  bedAzimuth: {
    id: `${cropScope}.bedAzimuth`,
    defaultMessage: `<b>Along</b> implies your beds look parallel to your PV rows, if seen from above.<br />
    <b>Across</b> implies they are orthogonal.<br />
    In Agri only simulation - <b>Along</b> means Orthogonal to the Pitch.`,
  },
  tempControl: {
    id: `${cropScope}.tempControl`,
    defaultMessage: `The temperature at which the crop is grown.`,
  },


});
