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
    defaultMessage: `"An offset allows you to plant in 2 rows on 1 bed,
    the offset is distance from center of bed at which seed is planted. Keep 0 for 1 row."
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
    defaultMessage: `Tilt of module, if fixed tilt. Trackers have variable tilt.<br />If not sure, keep it same as your latitude.`,
  },

  // Crop Parameters
  o1: {
    id: `${cropScope}.o1`,
    defaultMessage: `An offset allows you to plant in 2 rows on 1 bed,<br />
    the offset is distance from center of bed at which seed is planted. Keep 0 for 1 row.`,
  },
  s1: {
    id: `${cropScope}.s1`,
    defaultMessage: `spacing between 2 plants`,
  },
  o2: {
    id: `${cropScope}.o2`,
    defaultMessage: `Initial Offset, used only once - at the start of first plant.`,
  },
  stretch: {
    id: `${cropScope}.stretch`,
    defaultMessage: `Crops in the system have a predefined lifespan included in their name (e.g., Tomato 90 Days). The stretch parameter allows users to adjust this duration within ±X% to accommodate regional variations.`,
  },
  interBed: {
    id: `${cropScope}.stretch`,
    defaultMessage: `Defines how multiple crops are arranged across beds. Each bed type is assigned a number, and the pattern (e.g., 001 for two of Bed 1 followed by one of Bed 2) determines how they repeat in the layout.`,
  },


  startPointOffset: {
    id: `${cropScope}.startPointOffset`,
    defaultMessage: `Changing this offset changes how your beds are w.r.t modules above. If the offset equals bed spacing, the beds again come to same position. Hence maximum offset is bed c/c spacing.`,
  },
  bedAzimuth: {
    id: `${cropScope}.bedAzimuth`,
    defaultMessage: `"Along" implies your beds look parallel to your PV rows, if seen from above.<br />
    "Across" implies they are orthogonal`,
  },
});
