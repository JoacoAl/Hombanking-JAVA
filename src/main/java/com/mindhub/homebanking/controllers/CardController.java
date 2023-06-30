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

import javax.swing.event.HyperlinkEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IllegalFormatCodePointException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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

            randomCardNumber = randomValue.nextInt(9999) + " " + randomValue.nextInt(9999) + " " + randomValue.nextInt(9999) + " " + randomValue.nextInt(9999);

        } while (cardRepository.findByNumber(randomCardNumber) != null);


        int randomCvvNumber = randomValue.nextInt(999);


        if (client.getCards().stream().filter(card -> card.getType() == cardType).count() >= 3) {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
        if (client.getCards().stream().anyMatch(card -> card.getType() == cardType && card.getColor() == cardColor)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        else {
            Card card = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, randomCardNumber, randomCvvNumber, LocalDate.now(), LocalDate.now().plusYears(5));
            client.addCard(card);
            cardRepository.save(card);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

    }
}


