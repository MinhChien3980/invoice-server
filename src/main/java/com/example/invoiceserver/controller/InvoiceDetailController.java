package com.example.invoiceserver.controller;

import com.example.invoiceserver.dto.request.InvoiceDetailRequest;
import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceDetailResponse;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.service.InvoiceDetailService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-details")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceDetailController {
//    InvoiceDetailService invoiceDetailService;
//
//    @PostMapping
//    public ResponseEntity<InvoiceDetailResponse> addInvoiceDetail(@RequestBody InvoiceDetailRequest request) {
//        return ResponseEntity.ok(invoiceDetailService.addInvoiceDetail(request));
//    }
//
//    @GetMapping("/{invoiceId}")
//    public ResponseEntity<List<InvoiceDetailResponse>> getDetails(@PathVariable Long invoiceId) {
//        return ResponseEntity.ok(invoiceDetailService.getDetailsByInvoiceId(invoiceId));
//    }
//    // Update Invoice
//    @PutMapping("/update/{id}")
//    public ResponseEntity<InvoiceDetailResponse> updateInvoice(
//            @PathVariable Long id,
//            @RequestBody InvoiceDetailRequest invoiceDetaileRequest) throws IOException {
//        InvoiceDetailResponse updatedInvoice = invoiceDetailService.updateDetail(id, invoiceDetaileRequest);
//        return ResponseEntity.ok(updatedInvoice);
//    }
//    //create detail

}
