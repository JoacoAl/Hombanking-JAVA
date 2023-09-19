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
import java.text.NumberFormat;
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

        if (transferDTO.getDescription().isBlank() || transferDTO.getNumberAccount().isBlank() || transferDTO.getDestinationAccount().isBlank()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (transferDTO.getAmount() <= 0){
            return new ResponseEntity<>("The amount dasdasdsa", HttpStatus.FORBIDDEN);
        }

        if (transferDTO.getNumberAccount().equals(transferDTO.getDestinationAccount())) {
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
    public ResponseEntity<InputStreamResource> getTransactionsByAccountAndDateRange(Authentication authentication, @RequestParam Long id, @RequestParam String startDate, @RequestParam String endDate) throws
            ParseException, DocumentException, IOException {


        //verificar los datos


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

        if (transactions != null && !transactions.isEmpty()) {
            //PDF
            System.out.println("hay transacciones");

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Logo
            String imgPath = "C:\\Users\\Joaquin\\Desktop\\logo.png";
            Image logoImage = Image.getInstance(imgPath);
            logoImage.scaleAbsolute(60, 50);
            logoImage.setAlignment(Element.ALIGN_CENTER);
            document.add(logoImage);

            // Title


            // Account Information
            Font infoFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK);

            // Color para la palabra "Account"
            BaseColor accountColor = new BaseColor(100, 100, 100);

            Paragraph accountNumber = new Paragraph();
            accountNumber.add(new Chunk("Account Number: ", infoFont));
            accountNumber.add(new Chunk(account.getNumber(), new Font(infoFont.getFamily(), infoFont.getSize(), infoFont.getStyle(), accountColor)));

            Paragraph accountOwner = new Paragraph();
            accountOwner.add(new Chunk("Account Owner: ", infoFont));
            accountOwner.add(new Chunk(account.getClient().getFirstName() + " " + account.getClient().getLastName(), new Font(infoFont.getFamily(), infoFont.getSize(), infoFont.getStyle(), accountColor)));

            Paragraph balance = new Paragraph();
            balance.add(new Chunk("Balance: ", infoFont));
            balance.add(new Chunk(String.valueOf(account.getBalance()), new Font(infoFont.getFamily(), infoFont.getSize(), infoFont.getStyle(), accountColor)));

            Paragraph accountType = new Paragraph();
            accountType.add(new Chunk("Account Type: ", infoFont));
            accountType.add(new Chunk(account.getTypeAccount().toString(), new Font(infoFont.getFamily(), infoFont.getSize(), infoFont.getStyle(), accountColor)));

            // Añadir un espacio antes de los párrafos de la cuenta
            accountNumber.setSpacingAfter(10f);
            accountOwner.setSpacingAfter(10f);
            balance.setSpacingAfter(10f);
            accountType.setSpacingAfter(10f);

            document.add(accountNumber);
            document.add(accountOwner);
            document.add(balance);
            document.add(accountType);

            // Transactions Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setPaddingTop(5); // Espaciado interno para todas las celdas

           // Table Headers
            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
            String[] headers = {"Type", "Date", "Description", "Amount", "Current Balance"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

           // Table Data
            Font cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(); // Formateador de moneda
            for (Transaction transaction : transactions) {
                PdfPCell typeCell = new PdfPCell();

                // Crear un objeto Phrase para contener el texto de la celda "Type"
                Phrase typePhrase = new Phrase(String.valueOf(transaction.getType()), cellFont);

                // Aplicar color y aumentar el tamaño solo a la celda "Type"
                if ("DEBIT".equals(transaction.getType())) {
                    typeCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    typePhrase.getFont().setColor(BaseColor.RED);
                } else if ("CREDIT".equals(transaction.getType())) {
                    typeCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    typePhrase.getFont().setColor(BaseColor.GREEN);
                }

                // Añadir el Phrase a la celda "Type"
                typeCell.addElement(typePhrase);

                // Establecer tamaño de celda para toda la tabla
                typeCell.setFixedHeight(25f);
                table.addCell(typeCell);

                table.addCell(new Phrase(transaction.getDate().toString(), cellFont));
                table.addCell(new Phrase(transaction.getDescription(), cellFont));
                table.addCell(new Phrase(currencyFormatter.format(transaction.getAmount()), cellFont));
                table.addCell(new Phrase(currencyFormatter.format(transaction.getBalance()), cellFont));
            }

            document.add(table);


            document.close();

            // Preparar la respuesta para descargar el PDF
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            HttpHeaders headerss = new HttpHeaders();
            headerss.add("Content-Disposition", "attachment; filename=archivo.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headerss)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bais));
        } else {
            System.out.println("No se encontraron transacciones para generar el PDF.");
            return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
        }

    }

}




