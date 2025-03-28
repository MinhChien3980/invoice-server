package com.example.invoiceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InvoiceRequest {
    private String invoiceNumber;
    private String userName;
    private boolean aproved;
    private LocalDateTime approveDate;
    private String customerName;
    private boolean statusPaid;
    private boolean statusHasInvoice;
    private String dateBuy;
    private String outOfDateToPay;
    private String pdfOrImgPath;
    private MultipartFile file;

}
