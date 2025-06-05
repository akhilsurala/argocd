package com.sunseed.helper;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
public class MailHelper {

    //random otp generation
    public static int otpGeneration() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        log.info("otp is : {}", otp);
        return otp;
    }

    // random password generate --> alpha numeric -- max length 30
    public static String randomPasswordGenerate() {
        String randomPassword = RandomStringUtils.randomAlphanumeric(14);
        log.info("random password is : {}", randomPassword);
        return randomPassword + "@";
    }
}