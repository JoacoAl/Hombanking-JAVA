package com.mindhub.homebanking;

import com.mindhub.homebanking.controllers.CardController;
import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.Random;

@SpringBootTest
public class CardUtilsTests {


    @Test
    public void cardNumberIsCreated(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber, hasLength(19));

    }
    @Test
    public void cvvNumberIsNotGraterThan999(){
        int cvvNumber = CardUtils.getRandomCvvNumber();
        assertThat(cvvNumber, is(not(greaterThan(999))));
    }
}
