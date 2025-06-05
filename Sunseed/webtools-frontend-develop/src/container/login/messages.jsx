/*
 * Landing Messages
 *
 * This contains all the text for the Landing container.
 */

import { defineMessages } from "react-intl";

export const scope = "src.container.Login";

export default defineMessages({
  signIn: {
    id: `${scope}.signIn`,
    defaultMessage: "Sign in",
  },
  adminSignIn: {
    id: `${scope}.adminSignIn`,
    defaultMessage: "Sign In as Admin",
  },
  welcomeBack: {
    id: `${scope}.welcomeBack`,
    defaultMessage: "Welcome back",
  },
});
