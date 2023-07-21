package com.mindhub.homebanking.controllers;

import com.itextpdf.text.*;
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


    @GetMapping("/downloadPDF")
    public ResponseEntity<byte[]> getTransactionsByAccountAndDateRange(Authentication authentication, @RequestParam Long id, @RequestParam String startDate, @RequestParam String endDate) throws
            ParseException, DocumentException, IOException {
        Client client = clientServices.findByEmail(authentication.getName());
        Set<Account> setAccounts = client.getAccounts();
        Account account = setAccounts.stream().filter(acc -> acc.getId() == id).findFirst().orElse(null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date dateStart = dateFormat.parse(startDate);
        Date dateEnd = dateFormat.parse(endDate);

        LocalDate startDateFinal = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDateFinal = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        Set<Transaction> transactions = transactionServices.getTransactionsByAccountAndDateRange(account, startDateFinal, endDateFinal);

        if (transactions != null && !transactions.isEmpty()) {
            //PDF
            System.out.println("hay transacciones");

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            String img = "C:\\Users\\Joaquin\\Desktop\\logo.png";
            Image image = Image.getInstance(img);
            image.scaleAbsolute(200, 100);
            document.add(image);

            BaseColor color = new BaseColor(0, 0, 0);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, color);

            document.add(new Paragraph("Welcome" + client.getFirstName(), font));
            document.add(new Paragraph("Accounts", font));


            for (Transaction transaction : transactions) {
                document.add(new Paragraph("Date: " + transaction.getDate(), font));
                document.add(new Paragraph("Description: " + transaction.getDescription(), font));
                document.add(new Paragraph("Amount: " + transaction.getAmount(), font));
                document.add(new Paragraph("---------------------------------------", font));
            }


            document.close();

            // Obtener los bytes del PDF generado
            byte[] pdfBytes = baos.toByteArray();

            // Preparar la respuesta para descargar el PDF
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Account_Resume.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } else {
            System.out.println("No se encontraron transacciones para generar el PDF.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}




