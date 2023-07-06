package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import org.springframework.stereotype.Service;

public interface CardServices {

    Card findByNumber (String number);
    void save (Card card);
}
