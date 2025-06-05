export const maskEmail = (email) => {
    if(!email) return "";
    // Split the email address into local part and domain part
    const [localPart, domainPart] = email.split('@');

    // Mask characters in the local part, keeping the first and last characters
    const maskedLocalPart = localPart.length > 2 ?
        localPart.charAt(0) + '*'.repeat(localPart.length - 2) + localPart.charAt(localPart.length - 1) :
        localPart; // Keep the local part intact if it's too short to mask

    // Construct the masked email address
    const maskedEmail = maskedLocalPart + '@' + domainPart;
    
    return maskedEmail;
}