package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.services.CardServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public final class CardUtils {

    private CardUtils() {
    }

    public static Random randomValue = new Random();

    public static String getCardNumber() {
        String cardNumber = String.format("%04d %04d %04d %04d",
                randomValue.nextInt(9999), randomValue.nextInt(9999),
                randomValue.nextInt(9999), randomValue.nextInt(9999));
        return cardNumber;
    }

    public static int getRandomCvvNumber(){
        int randomCvv = randomValue.nextInt(999);
        return randomCvv;
    }




}


