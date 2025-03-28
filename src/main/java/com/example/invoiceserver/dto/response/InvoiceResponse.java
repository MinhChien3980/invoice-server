package com.example.invoiceserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String userName;
    private boolean aproved;
    private String customerName;
    private String approveDate;
    private boolean statusPaid;
    private boolean statusHasInvoice;
    private String dateBuy;
    private String outOfDateToPay;
    private String pdfOrImgPath;

    // New Field: Invoice Details
    private List<InvoiceDetailResponse> invoiceDetails;
}
