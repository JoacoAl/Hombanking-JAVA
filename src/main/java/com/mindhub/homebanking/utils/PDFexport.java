package com.mindhub.homebanking.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import org.hibernate.mapping.Array;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class PDFexport {

    private Set<Transaction> transactionSet;

    private Account account;

    public PDFexport(Set<Transaction> transactionSet, Account account) {
        this.transactionSet = transactionSet;
        this.account = account;
    }

    private void writeTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(1);


        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        BaseColor color = new BaseColor(0, 0, 0);
        font.setColor(color);

        cell.setPhrase(new Phrase("Type", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Date", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Description", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Amount", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Current Balance", font));

        table.addCell(cell);


    }

    private void writeTableData(PdfPTable table) {
        transactionSet.stream().forEach(tr -> {

            table.addCell(String.valueOf(tr.getType()));
            table.addCell(tr.getDate().toString());
            table.addCell(tr.getDescription());
            table.addCell(String.valueOf(tr.getAmount()));
            table.addCell(String.valueOf(tr.getBalance()));

        });

    }

    public void export(HttpServletResponse response) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        String img = "C:\\Users\\Joaquin\\Desktop\\logo.png";
        Image image = Image.getInstance(img);
        image.scaleAbsolute(200, 100);
        document.add(image);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(new BaseColor(0,0,0));

        Paragraph p = new Paragraph("Account resume", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        writeTable(table);
        writeTableData(table);

        document.add(table);

        document.close();



    }


}
