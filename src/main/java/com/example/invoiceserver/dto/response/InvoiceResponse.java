package com.example.invoiceserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String userName;
    private String productName;
    private int amountOfProduct;
    private double price;
    private boolean statusPaid;
    private boolean statusHasInvoice;
    private String dateBuy;
    private String outOfDateToPay;
    private String pdfOrImgPath;
    private MultipartFile file;
}
