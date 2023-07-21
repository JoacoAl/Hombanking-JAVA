package com.mindhub.homebanking.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.dtos.TransferDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountServices;
import com.mindhub.homebanking.services.ClientServices;
import com.mindhub.homebanking.services.TransactionServices;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TransactionsController {

    @Autowired
    AccountServices accountServices;
    @Autowired
    ClientServices clientServices;

    @Autowired
    TransactionServices transactionServices;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransfer(Authentication authentication, @RequestBody TransferDTO transferDTO) {
        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> clientAccounts = client.getAccounts();

        if (transferDTO.getAmount() == 0 || transferDTO.getDescription().isBlank() || transferDTO.getNumberAccount().isBlank() || transferDTO.getDestinationAccount().isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (accountServices.findByNumber(transferDTO.getNumberAccount()).equals(transferDTO.getDestinationAccount())) {
            return new ResponseEntity<>("Same accounts", HttpStatus.FORBIDDEN);
        }

        if (accountServices.findByNumber(transferDTO.getNumberAccount()) == null) {
            return new ResponseEntity<>("Origin account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (!clientAccounts.stream().anyMatch(account -> account.getNumber().equals(transferDTO.getNumberAccount()))) {
            return new ResponseEntity<>("This account does not belong to this client", HttpStatus.FORBIDDEN);
        }

        if (accountServices.findByNumber(transferDTO.getDestinationAccount()) == null) {
            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
        }

        if (accountServices.findByNumber(transferDTO.getNumberAccount()).getBalance() < transferDTO.getAmount()) {
            return new ResponseEntity<>("Insuficient founds", HttpStatus.FORBIDDEN);
        } else {
            Account accountOrigin = accountServices.findByNumber(transferDTO.getNumberAccount());
            Account accountDestination = accountServices.findByNumber(transferDTO.getDestinationAccount());


            Transaction transactionDebit = new Transaction(TransactionType.DEBIT, -transferDTO.getAmount(), transferDTO.getDescription() + " " + transferDTO.getDestinationAccount(), LocalDate.now(), true, accountOrigin.getBalance() - transferDTO.getAmount());
            Transaction transactionCredit = new Transaction(TransactionType.CREDIT, transferDTO.getAmount(), transferDTO.getDescription() + " " + transferDTO.getNumberAccount(), LocalDate.now(), true, accountDestination.getBalance() + transferDTO.getAmount());

            accountOrigin.setBalance(accountServices.findByNumber(transferDTO.getNumberAccount()).getBalance() - transferDTO.getAmount());
            accountServices.save(accountOrigin);

            accountDestination.setBalance(accountServices.findByNumber(transferDTO.getDestinationAccount()).getBalance() + transferDTO.getAmount());
            accountServices.save(accountDestination);


            accountServices.findByNumber(transferDTO.getNumberAccount()).addTransactions(transactionDebit);
            accountServices.findByNumber(transferDTO.getDestinationAccount()).addTransactions(transactionCredit);
            transactionServices.save(transactionDebit);
            transactionServices.save(transactionCredit);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }


    @PostMapping("/downloadPDF")
    public ResponseEntity<ByteArrayOutputStream> getTransactionsByAccountAndDateRange(Authentication authentication, @RequestParam Long id, @RequestParam String startDate, @RequestParam String endDate) throws
            ParseException, DocumentException {
        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> setAccounts = client.getAccounts();
        Account account = setAccounts.stream().filter(acc -> acc.getId() == id).findFirst().orElse(null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date dateStart = dateFormat.parse(startDate);
        Date dateEnd = dateFormat.parse(endDate);

        LocalDate startDateFinal = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDateFinal = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        Set<Transaction> transactions = transactionServices.getTransactionsByAccountAndDateRange(account, startDateFinal, endDateFinal);


        //PDF
        System.out.println("hay transacciones");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Paragraph title = new Paragraph("Account Information");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Account holder: " + client.getFirstName() + " " + client.getLastName()));
        document.add(new Paragraph("Account number: " + account.getNumber()));
        document.add(new Paragraph("Account type: " + account.getTypeAccount()));
        document.add(new Paragraph("Actual Balance: " + account.getBalance()));

        // Agregar tabla con las transacciones
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Date"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Description"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (Transaction transaction : transactions) {
            table.addCell(transaction.getDate().toString());
            table.addCell(transaction.getDescription());
            table.addCell(String.valueOf(transaction.getAmount()));
        }

        document.add(table);
        document.close();

        return new ResponseEntity<ByteArrayOutputStream>(HttpStatus.ACCEPTED);
    }

}




