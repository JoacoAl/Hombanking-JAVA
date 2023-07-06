package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountServices;
import com.mindhub.homebanking.services.ClientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientServices clientServices;
    @Autowired
    private AccountServices accountServices;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/clients")
    public List<ClientDTO> getClientsDTO() {
        return clientServices.getAllClients();
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClientDTO(@PathVariable Long id) { /*variable de ruta*/
        Client client = clientServices.findById(id);
        ClientDTO clientDTO = new ClientDTO(client);
        return clientDTO;
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(

            @RequestParam String firstName, @RequestParam String lastName,

            @RequestParam String email, @RequestParam String password) {


        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (clientServices.findByEmail(email) != null) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }
        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientServices.save(client);

        String randomNumber;

        do {
            Random randomValue = new Random();
            randomNumber = "VIN" + randomValue.nextInt(99999999);
        }while (accountServices.findByNumber(randomNumber) != null);

        Account starterAccount = new Account(randomNumber, LocalDate.now(), 0);

        client.addAccount(starterAccount);

        accountServices.save(starterAccount);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/clients/current")
    public ResponseEntity<Object> getClientCurrent(Authentication authentication) {
        Client client = clientServices.findByEmail(authentication.getName());
        if (client != null) {
            ClientDTO clientDTO = new ClientDTO(client);
            return new ResponseEntity<>(clientDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
    }

}
