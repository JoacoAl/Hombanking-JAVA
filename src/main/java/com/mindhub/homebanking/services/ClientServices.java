package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ClientServices {

    List<ClientDTO> getAllClients ();

    Client findById (Long id);

    Client findByEmail (String email);

    void save (Client client);

}
