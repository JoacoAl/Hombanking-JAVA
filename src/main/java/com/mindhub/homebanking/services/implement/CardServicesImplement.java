package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServicesImplement implements CardServices {
@Autowired
    CardRepository cardRepository;
    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public void save(Card card) {
        cardRepository.save(card);


    }
}
