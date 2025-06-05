export const getLocalStorageData = (key) =>{
    return localStorage.getItem(key);
}
export const setLocalStorageData = (key,value) =>{
    return localStorage.setItem(key,value);
}

export function saveRememberMeCredentials(cacheLogin) {
  localStorage.setItem('cacheLogin', JSON.stringify(cacheLogin));
}

export function getRememberMeCredentials() {
  return JSON.parse(localStorage.getItem('cacheLogin'));
}

export function removeRememberMeCredentials() {
  localStorage.removeItem('cacheLogin');
}

export function saveLoginInfo(data) {
  localStorage.setItem("apiToken", data.accessToken);
  localStorage.setItem("userId", data.user.userProfileId);
  localStorage.setItem("emailId", data.user.emailId);
  localStorage.setItem("firstName", data.user.firstName);
  localStorage.setItem("lastName", data.user.lastName);
  localStorage.setItem("roles", JSON.stringify(data.user.roles));
  localStorage.setItem("profilePicturePath", data.user.userProfilePicturePath);
}

export function updateUserInfo(data) {
  localStorage.setItem("firstName", data.firstName);
  localStorage.setItem("lastName", data.lastName);
}

export function clearLocalStore() {
  localStorage.removeItem("apiToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("emailId");
  localStorage.removeItem("roles");
  localStorage.removeItem("firstName");
  localStorage.removeItem("lastName");
  localStorage.removeItem("projectId");
  localStorage.removeItem("projectName");
  localStorage.removeItem("currentProjectName");
  localStorage.removeItem("profilePicturePath");
  localStorage.removeItem("post-processing-runs");
  localStorage.removeItem("pv_runs");
  localStorage.removeItem("apv_agri_runs");
  localStorage.removeItem("current-runs");
}
