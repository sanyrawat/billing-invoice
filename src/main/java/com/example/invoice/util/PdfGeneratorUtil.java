package com.example.invoice.util;

import com.example.invoice.dto.InvoiceItem;
import com.example.invoice.dto.InvoiceRequest;
import com.example.invoice.exception.PdfGenerationException;
import com.example.invoice.model.InvoiceSummary;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PdfGeneratorUtil {

    private static final Font COMPANY_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 15, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String RUPEE_SYMBOL = "\u20B9";
    private static final java.awt.Color ACCENT_BLUE = new java.awt.Color(17, 93, 194);
    private static final java.awt.Color LIGHT_BLUE = new java.awt.Color(232, 240, 254);
    private static final String LOGO_PATH = "/Users/sanyrawat/.cursor/projects/Users-sanyrawat-git-billing-invoice/assets/WhatsApp_Image_2026-04-15_at_02.43.13-89bfda5d-d402-46ff-8e84-52207b029e8e.png";

    public byte[] generateInvoicePdf(InvoiceRequest request, InvoiceSummary summary) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addCompanyHeader(document, request);
            addInvoiceHeading(document);
            addBillingAndMetadataSection(document, request);
            addLineItemsTable(document, request);
            addTotalsSection(document, request, summary);
            addAmountInWords(document, summary.getGrandTotal());
            addBankDetails(document, request);
            addFooter(document, request);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            log.error("Failed to generate PDF for invoice={}", request.getInvoiceNumber(), ex);
            throw new PdfGenerationException("Unable to generate invoice PDF", ex);
        }
    }

    private void addCompanyHeader(Document document, InvoiceRequest request) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3.5f, 1.3f});
        headerTable.setSpacingAfter(8f);

        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(PdfPCell.NO_BORDER);
        companyCell.setPadding(0f);
        Paragraph companyName = new Paragraph(request.getCompanyName(), COMPANY_FONT);
        companyName.setAlignment(Element.ALIGN_LEFT);
        companyCell.addElement(companyName);

        List<String> headerLines = new ArrayList<>();
        headerLines.add(request.getCompanyAddress());
        headerLines.add(buildContactLine(request));
        headerLines.add(buildTaxLine(request));

        Paragraph companyAddress = new Paragraph(headerLines.stream()
                .filter(line -> line != null && !line.isBlank())
                .collect(Collectors.joining("\n")), BODY_FONT);
        companyAddress.setSpacingAfter(2f);
        companyCell.addElement(companyAddress);
        headerTable.addCell(companyCell);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(PdfPCell.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        logoCell.setVerticalAlignment(Element.ALIGN_TOP);
        logoCell.setPadding(0f);
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(55f, 55f);
            logo.setAlignment(Element.ALIGN_RIGHT);
            logoCell.addElement(logo);
        } catch (Exception ex) {
            log.warn("Logo could not be loaded from configured path: {}", LOGO_PATH);
        }
        headerTable.addCell(logoCell);

        document.add(headerTable);
    }

    private void addInvoiceHeading(Document document) throws DocumentException {
        PdfPCell titleCell = new PdfPCell(new Phrase("Bill of Supply", TITLE_FONT));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBackgroundColor(LIGHT_BLUE);
        titleCell.setBorderColor(ACCENT_BLUE);
        titleCell.setPadding(8f);
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.setSpacingAfter(8f);
        titleTable.addCell(titleCell);
        document.add(titleTable);
    }

    private void addBillingAndMetadataSection(Document document, InvoiceRequest request) throws DocumentException {
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setWidths(new float[]{1.15f, 1f});
        metaTable.setSpacingAfter(12f);

        PdfPCell billToCell = new PdfPCell();
        billToCell.setPadding(6f);
        Paragraph billToHeading = new Paragraph("Bill To", HEADER_FONT);
        billToHeading.setSpacingAfter(6f);
        billToCell.addElement(billToHeading);
        billToCell.addElement(new Paragraph(request.getCustomerName(), HEADER_FONT));
        billToCell.addElement(new Paragraph(request.getCustomerAddress(), BODY_FONT));
        if (request.getCustomerContactNo() != null && !request.getCustomerContactNo().isBlank()) {
            billToCell.addElement(new Paragraph("Contact No.: " + request.getCustomerContactNo(), SMALL_FONT));
        }
        if (request.getCustomerGstin() != null && !request.getCustomerGstin().isBlank()) {
            billToCell.addElement(new Paragraph("GSTIN: " + request.getCustomerGstin(), SMALL_FONT));
        }
        billToCell.setBorderColor(ACCENT_BLUE);
        metaTable.addCell(billToCell);

        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setPadding(6f);
        Paragraph invoiceDetailsHeading = new Paragraph("Invoice Details", HEADER_FONT);
        invoiceDetailsHeading.setSpacingAfter(6f);
        detailsCell.addElement(invoiceDetailsHeading);
        detailsCell.addElement(new Paragraph("Invoice No.: " + request.getInvoiceNumber(), HEADER_FONT));
        detailsCell.addElement(new Paragraph("Date: " + formatDate(request.getInvoiceDate()), HEADER_FONT));
        if (request.getPoDate() != null) {
            detailsCell.addElement(new Paragraph("PO date: " + formatDate(request.getPoDate()), BODY_FONT));
        }
        if (request.getPoNumber() != null && !request.getPoNumber().isBlank()) {
            detailsCell.addElement(new Paragraph("PO number: " + request.getPoNumber(), BODY_FONT));
        }
        if (request.getEwayBillNumber() != null && !request.getEwayBillNumber().isBlank()) {
            detailsCell.addElement(new Paragraph("E-way Bill number: " + request.getEwayBillNumber(), BODY_FONT));
        }
        detailsCell.setBorderColor(ACCENT_BLUE);
        metaTable.addCell(detailsCell);
        document.add(metaTable);
    }

    private void addLineItemsTable(Document document, InvoiceRequest request) throws DocumentException {
        PdfPTable itemsTable = new PdfPTable(7);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{0.9f, 3.2f, 1.2f, 0.7f, 1f, 1.3f, 1.3f});
        itemsTable.setSpacingAfter(12f);

        addHeaderCell(itemsTable, "Sr No");
        addHeaderCell(itemsTable, "Description");
        addHeaderCell(itemsTable, "HSN/SAC");
        addHeaderCell(itemsTable, "Qty");
        addHeaderCell(itemsTable, "Unit");
        addHeaderCell(itemsTable, "Price / Unit");
        addHeaderCell(itemsTable, "Amount");

        int serial = 1;
        for (InvoiceItem item : request.getItems()) {
            BigDecimal lineTotal = item.getQuantity()
                    .multiply(item.getUnitPrice())
                    .setScale(2, RoundingMode.HALF_UP);

            itemsTable.addCell(createBodyCell(String.valueOf(serial++), Element.ALIGN_CENTER));
            itemsTable.addCell(createBodyCell(item.getDescription(), Element.ALIGN_LEFT));
            itemsTable.addCell(createBodyCell(getOrDash(item.getHsnSac()), Element.ALIGN_CENTER));
            itemsTable.addCell(createBodyCell(formatAmount(item.getQuantity()), Element.ALIGN_CENTER));
            itemsTable.addCell(createBodyCell(getOrDash(item.getUnit()), Element.ALIGN_CENTER));
            itemsTable.addCell(createBodyCell(formatCurrency(item.getUnitPrice()), Element.ALIGN_RIGHT));
            itemsTable.addCell(createBodyCell(formatCurrency(lineTotal), Element.ALIGN_RIGHT));
        }

        document.add(itemsTable);
    }

    private void addTotalsSection(Document document, InvoiceRequest request, InvoiceSummary summary) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(42);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{1.5f, 1f});
        totalsTable.setSpacingAfter(10f);

        totalsTable.addCell(createBodyCell("Sub Total", Element.ALIGN_LEFT));
        totalsTable.addCell(createBodyCell(formatCurrency(summary.getSubtotal()), Element.ALIGN_RIGHT));

        totalsTable.addCell(createBodyCell("Tax (" + formatAmount(request.getTaxPercentage()) + "%)", Element.ALIGN_LEFT));
        totalsTable.addCell(createBodyCell(formatCurrency(summary.getTaxAmount()), Element.ALIGN_RIGHT));

        totalsTable.addCell(createHeaderBodyCell("Total", Element.ALIGN_LEFT, true));
        totalsTable.addCell(createHeaderBodyCell(formatCurrency(summary.getGrandTotal()), Element.ALIGN_RIGHT, true));

        document.add(totalsTable);
    }

    private void addAmountInWords(Document document, BigDecimal grandTotal) throws DocumentException {
        addHighlightedSection(document, "Invoice Amount In Words", convertToIndianCurrencyWords(grandTotal), 12f);
    }

    private void addBankDetails(Document document, InvoiceRequest request) throws DocumentException {
        List<String> lines = new ArrayList<>();
        if (request.getBankName() != null && !request.getBankName().isBlank()) {
            lines.add("Name: " + request.getBankName());
        }
        if (request.getAccountNumber() != null && !request.getAccountNumber().isBlank()) {
            lines.add("Account No.: " + request.getAccountNumber());
        }
        if (request.getIfscCode() != null && !request.getIfscCode().isBlank()) {
            lines.add("IFSC code: " + request.getIfscCode());
        }
        if (request.getAccountHolderName() != null && !request.getAccountHolderName().isBlank()) {
            lines.add("Account Holder's Name: " + request.getAccountHolderName());
        }
        if (lines.isEmpty()) {
            lines.add("Add bank details in request to print account information.");
        }
        addHighlightedSection(document, "Bank Details", String.join("\n", lines), 14f);
    }

    private void addFooter(Document document, InvoiceRequest request) throws DocumentException {
        addHighlightedSection(
                document,
                "Terms and conditions",
                "Thank you for doing business with us.\nAll issues are under Lucknow jurisdiction.",
                20f
        );

        Paragraph signatureFor = new Paragraph("For: " + request.getCompanyName(), HEADER_FONT);
        signatureFor.setAlignment(Element.ALIGN_RIGHT);
        document.add(signatureFor);

        Paragraph signatureLine = new Paragraph("Authorized Signature", HEADER_FONT);
        signatureLine.setAlignment(Element.ALIGN_RIGHT);
        document.add(signatureLine);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7f);
        cell.setBackgroundColor(ACCENT_BLUE);
        cell.setBorderColor(ACCENT_BLUE);
        cell.setPhrase(new Phrase(text, new Font(Font.HELVETICA, 10, Font.BOLD, java.awt.Color.WHITE)));
        table.addCell(cell);
    }

    private PdfPCell createBodyCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBorderColor(new java.awt.Color(180, 188, 204));
        return cell;
    }

    private PdfPCell createHeaderBodyCell(String text, int alignment, boolean applyColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        if (applyColor) {
            cell.setBackgroundColor(LIGHT_BLUE);
            cell.setBorderColor(ACCENT_BLUE);
        }
        return cell;
    }

    private void addHighlightedSection(Document document, String title, String content, float spacingAfter)
            throws DocumentException {
        PdfPTable sectionTable = new PdfPTable(1);
        sectionTable.setWidthPercentage(100);
        sectionTable.setSpacingAfter(spacingAfter);

        PdfPCell titleCell = new PdfPCell(new Phrase(title, HEADER_FONT));
        titleCell.setPadding(6f);
        titleCell.setBackgroundColor(LIGHT_BLUE);
        titleCell.setBorderColor(ACCENT_BLUE);
        sectionTable.addCell(titleCell);

        PdfPCell contentCell = new PdfPCell(new Phrase(content, BODY_FONT));
        contentCell.setPadding(6f);
        contentCell.setBorderColor(ACCENT_BLUE);
        sectionTable.addCell(contentCell);

        document.add(sectionTable);
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatCurrency(BigDecimal amount) {
        return RUPEE_SYMBOL + " " + formatAmount(amount);
    }

    private String getOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String buildContactLine(InvoiceRequest request) {
        String phone = request.getCompanyPhone();
        String email = request.getCompanyEmail();
        if ((phone == null || phone.isBlank()) && (email == null || email.isBlank())) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (phone != null && !phone.isBlank()) {
            builder.append("Phone no.: ").append(phone);
        }
        if (email != null && !email.isBlank()) {
            if (builder.length() > 0) {
                builder.append("   ");
            }
            builder.append("Email: ").append(email);
        }
        return builder.toString();
    }

    private String buildTaxLine(InvoiceRequest request) {
        String gstin = request.getCompanyGstin();
        String state = request.getCompanyState();
        if ((gstin == null || gstin.isBlank()) && (state == null || state.isBlank())) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (gstin != null && !gstin.isBlank()) {
            builder.append("GSTIN: ").append(gstin);
        }
        if (state != null && !state.isBlank()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("State: ").append(state);
        }
        return builder.toString();
    }

    private String formatDate(LocalDate localDate) {
        return localDate == null ? "-" : localDate.format(DATE_FORMATTER);
    }

    private String convertToIndianCurrencyWords(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        long rupees = scaled.longValue();
        int paise = scaled.remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();

        StringBuilder words = new StringBuilder();
        words.append(convertNumber(rupees)).append(" Rupees");
        if (paise > 0) {
            words.append(" and ").append(convertNumber(paise)).append(" Paise");
        }
        words.append(" only");
        return words.toString();
    }

    private String convertNumber(long number) {
        if (number == 0) {
            return "Zero";
        }
        if (number < 0) {
            return "Minus " + convertNumber(-number);
        }

        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        StringBuilder result = new StringBuilder();

        if ((number / 10000000) > 0) {
            result.append(convertNumber(number / 10000000)).append(" Crore ");
            number %= 10000000;
        }
        if ((number / 100000) > 0) {
            result.append(convertNumber(number / 100000)).append(" Lakh ");
            number %= 100000;
        }
        if ((number / 1000) > 0) {
            result.append(convertNumber(number / 1000)).append(" Thousand ");
            number %= 1000;
        }
        if ((number / 100) > 0) {
            result.append(convertNumber(number / 100)).append(" Hundred ");
            number %= 100;
        }
        if (number > 0) {
            if (result.length() > 0) {
                result.append("and ");
            }
            if (number < 20) {
                result.append(units[(int) number]);
            } else {
                result.append(tens[(int) (number / 10)]);
                if ((number % 10) > 0) {
                    result.append(" ").append(units[(int) (number % 10)]);
                }
            }
        }
        return result.toString().trim();
    }
}
