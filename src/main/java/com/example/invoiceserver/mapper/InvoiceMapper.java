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
        invoice.setStatusPaid(request.isStatusPaid());
        return invoice;
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setUserName(invoice.getUserName());
        response.setCustomerName(invoice.getCustomerName());
        response.setAproved(invoice.isAproved());
        response.setApproveDate(invoice.getApproveDate() != null ? invoice.getApproveDate().toString() : null);
        response.setStatusPaid(invoice.isStatusPaid());
        response.setStatusHasInvoice(invoice.isStatusHasInvoice());
        response.setDateBuy(invoice.getDateBuy());
        response.setOutOfDateToPay(invoice.getOutOfDateToPay());

        if (invoice.getPdfOrImgPath() != null) {
            response.setPdfOrImgPath(FILE_BASE_URL + invoice.getPdfOrImgPath().replace("static/uploads/", ""));
        }

        return response;
    }
}
