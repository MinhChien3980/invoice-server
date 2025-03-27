package com.example.invoiceserver.mapper;

import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    private static final String FILE_BASE_URL = "http://localhost:8080/files/";

    public Invoice toInvoice(InvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setUserName(request.getUserName());
        invoice.setDateBuy(request.getDateBuy());
        invoice.setAmountOfProduct(request.getAmountOfProduct());
        invoice.setStatusPaid(request.isStatusPaid());
        return invoice;
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setUserName(invoice.getUserName());
        response.setProductName(invoice.getProductName());
        response.setAmountOfProduct(invoice.getAmountOfProduct());
        response.setPrice(invoice.getPrice());
        response.setStatusPaid(invoice.isStatusPaid());
        response.setStatusHasInvoice(invoice.isStatusHasInvoice());
        response.setDateBuy(invoice.getDateBuy());
        response.setOutOfDateToPay(invoice.getOutOfDateToPay());

        // Convert stored file path to full URL
        if (invoice.getPdfOrImgPath() != null) {
            response.setPdfOrImgPath(FILE_BASE_URL + invoice.getPdfOrImgPath().replace("uploads/", ""));
        }

        return response;
    }
}
