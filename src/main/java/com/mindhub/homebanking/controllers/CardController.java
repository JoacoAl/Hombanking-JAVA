package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardPaymentDTO;
import com.mindhub.homebanking.dtos.TransferDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountServices;
import com.mindhub.homebanking.services.CardServices;
import com.mindhub.homebanking.services.ClientServices;
import com.mindhub.homebanking.services.TransactionServices;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    ClientServices clientServices;
    @Autowired
    CardServices cardServices;
    @Autowired
    TransactionServices transactionServices;
    @Autowired
    AccountServices accountServices;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication) {

        Client client = clientServices.findByEmail(authentication.getName());

        //card number
        String randomCardNumber;
        do {
            randomCardNumber = CardUtils.getCardNumber();
        } while (cardServices.findByNumber(randomCardNumber) != null);

        //cvv number
        int randomCvvNumber = CardUtils.getRandomCvvNumber();
        String formattedCvvNumber = String.format("%03d", randomCvvNumber);
        int cvvInteger = Integer.parseInt(formattedCvvNumber);


        if (client.getCards().stream().filter(card -> card.getType() == cardType && card.isActive()).count() >= 3) {

            return new ResponseEntity<>("You cannot have more than 3 cards per type", HttpStatus.FORBIDDEN);

        }
        //verifica si existe una tarjeta del mismo tipo y color creada
        if (client.getCards().stream().anyMatch(card -> card.getType() == cardType && card.getColor() == cardColor && card.isActive())) {
            return new ResponseEntity<>("You cannot create a card of the same type and color", HttpStatus.FORBIDDEN);
        } else {
            Card card = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, randomCardNumber, cvvInteger, LocalDate.now(), LocalDate.now().plusYears(5), true);
            client.addCard(card);
            cardServices.save(card);
            return new ResponseEntity<>("the card was created successfully", HttpStatus.CREATED);
        }
    }

    @PutMapping("/clients/current/cards")
    public ResponseEntity<Object> deleteCards(@RequestParam String cardNumber, Authentication authentication) {
        //revisar los datos que pido antes
        Client client = clientServices.findByEmail(authentication.getName());
        Set<Card> cards = client.getCards();
        Card card = cards.stream()
                .filter(c -> c.getNumber().equals(cardNumber))
                .findFirst()
                .orElse(null);

        if (client == null) {
            return new ResponseEntity<>("There was a problem authenticating your session", HttpStatus.FORBIDDEN);
        }
        if (card == null) {
            return new ResponseEntity<>("The card does not exist", HttpStatus.FORBIDDEN);
        }
        if (!cards.stream().anyMatch(c -> c.equals(card))) {
            return new ResponseEntity<>("Something went wrong, the card number does not belong to your current cards", HttpStatus.FORBIDDEN);
        } else {
            card.setActive(false);
            cardServices.save(card);
            return new ResponseEntity<>("The card was deleted, correctly", HttpStatus.OK);
        }
    }

    @GetMapping("/getCards")
    public Set<CardDTO> getActiveCards(Authentication authentication) {
        if (authentication.getName() == null) {
            new ResponseEntity<>("The user authentication process failed", HttpStatus.FORBIDDEN);

        }
        Client client = clientServices.findByEmail(authentication.getName());
        Set<CardDTO> cardsDtoSet = client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet());

        return cardsDtoSet.stream().filter(cardDTO -> cardDTO.isActive()).collect(Collectors.toSet());

    }

    @Transactional
    @PostMapping("/implementCardPayments")
    public ResponseEntity<Object> cardPayments(@RequestBody CardPaymentDTO cardPaymentDTO) {

        Card card = cardServices.findByNumber(cardPaymentDTO.getNumber());
        Client client = card.getClient();
        System.out.println(client.getFirstName());
        Set<Account> clientAccounts = client.getAccounts().stream().filter(acc -> acc.isActive()).collect(Collectors.toSet());
        Account account = clientAccounts.stream().filter(acc -> acc.getBalance() > cardPaymentDTO.getAmount()).findFirst().orElse(null);


        if (cardPaymentDTO.getAmount() <= 0 || cardPaymentDTO.getDescription().isBlank() || cardPaymentDTO.getNumber().isBlank() || cardPaymentDTO.getCvv() == 0) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (client == null) {
            return new ResponseEntity<>("The client does not exist", HttpStatus.FORBIDDEN);
        }

        if (account == null) {
            return new ResponseEntity<>("Insuficient founds", HttpStatus.FORBIDDEN);
        }

        if (!card.isActive()) {
            return new ResponseEntity<>("The card is expired", HttpStatus.FORBIDDEN);
        }
        if (cardPaymentDTO.getCvv() !=  card.getCvv()){
            return new ResponseEntity<>("The security code is wrong", HttpStatus.FORBIDDEN);
        }
        else {

            Transaction transactionDebit = new Transaction(TransactionType.DEBIT, -cardPaymentDTO.getAmount(), cardPaymentDTO.getDescription() + " " + account.getNumber(), LocalDate.now(), true, account.getBalance() - cardPaymentDTO.getAmount());

            account.setBalance(account.getBalance() - cardPaymentDTO.getAmount());
            accountServices.save(account);

            account.addTransactions(transactionDebit);
            transactionServices.save(transactionDebit);


            return new ResponseEntity<>("The payment was successfuly", HttpStatus.CREATED);
        }


    }


}


