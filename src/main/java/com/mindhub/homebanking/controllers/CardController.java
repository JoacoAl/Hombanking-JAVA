package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.Random;


@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CardRepository cardRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication) {

        Client client = clientRepository.findByEmail(authentication.getName());

        Random randomValue = new Random();
        String randomCardNumber;

        do {

            //randomCardNumber = randomValue.nextInt(9999) + " " + randomValue.nextInt(9999) + " " + randomValue.nextInt(9999)+ " " + randomValue.nextInt(9999);
             randomCardNumber = String.format("%04d %04d %04d %04d",
                    randomValue.nextInt(9999), randomValue.nextInt(9999),
                    randomValue.nextInt(9999), randomValue.nextInt(9999));

        } while (cardRepository.findByNumber(randomCardNumber) != null);
        //existByNumber true or false


        int randomCvvNumber = randomValue.nextInt(999);
        String formattedCvvNumber = String.format("%03d", randomCvvNumber);
        int cvvInteger = Integer.parseInt(formattedCvvNumber);


        if (client.getCards().stream().filter(card -> card.getType() == cardType).count() >= 3) {

            return new ResponseEntity<>("You cannot have more than 3 cards per type",HttpStatus.FORBIDDEN);

        }
        //verifica si existe una tarjeta del mismo tipo y color creada
        if (client.getCards().stream().anyMatch(card -> card.getType() == cardType && card.getColor() == cardColor)) {
            return new ResponseEntity<>("You cannot create a card of the same type and color", HttpStatus.FORBIDDEN);
        } else {
            Card card = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, randomCardNumber, cvvInteger, LocalDate.now(), LocalDate.now().plusYears(5));
            client.addCard(card);
            cardRepository.save(card);
            return new ResponseEntity<>("the card was created successfully",HttpStatus.CREATED);
        }

    }
}


