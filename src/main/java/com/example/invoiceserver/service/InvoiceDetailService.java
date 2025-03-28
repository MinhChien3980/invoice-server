package com.example.invoiceserver.service;

import com.example.invoiceserver.dto.request.InvoiceDetailRequest;
import com.example.invoiceserver.dto.response.InvoiceDetailResponse;
import com.example.invoiceserver.entity.DetailInvoice;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.repo.DetailInvoiceRepository;
import com.example.invoiceserver.repo.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceDetailService {
    DetailInvoiceRepository invoiceDetailRepository;
    InvoiceRepository invoiceRepository;

//    public InvoiceDetailResponse addInvoiceDetail(InvoiceDetailRequest request) {
//        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
//                .orElseThrow(() -> new RuntimeException("Invoice not found"));
//
//        DetailInvoice detail = DetailInvoice.builder()
//
//                .invoice(invoice)
//
//                .productName(request.getProductName())
//                .amountOfProduct(request.getQuantity())
//                .price(request.getPrice())
//                .build();
//
//        DetailInvoice savedDetail = invoiceDetailRepository.save(detail);
//
//        return InvoiceDetailResponse.builder()
//                .id(savedDetail.getId())
//                .invoiceId(savedDetail.getInvoice().getId())
//                .productName(savedDetail.getProductName())
//                .quantity(savedDetail.getAmountOfProduct())
//                .price(savedDetail.getPrice())
//                .build();
//    }

//    public List<InvoiceDetailResponse> getDetailsByInvoiceId(Long invoiceId) {
//        return invoiceDetailRepository.findByInvoiceId(invoiceId)
//                .stream()
//                .map(detail -> InvoiceDetailResponse.builder()
//                        .id(detail.getId())
//                        .invoiceId(detail.getInvoice().getId())
//                        .productName(detail.getProductName())
//                        .quantity(detail.getAmountOfProduct())
//                        .price(detail.getPrice())
//                        .build())
//                .collect(Collectors.toList());
//    }
//    public InvoiceDetailResponse updateDetail(Long id, InvoiceDetailRequest request) {
//        DetailInvoice detail = invoiceDetailRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Detail not found"));
//
//        detail.setProductName(request.getProductName());
//        detail.setAmountOfProduct(request.getQuantity());
//        detail.setPrice(request.getPrice());
//
//        DetailInvoice updatedDetail = invoiceDetailRepository.save(detail);
//
//        return InvoiceDetailResponse.builder()
//                .id(updatedDetail.getId())
//                .invoiceId(updatedDetail.getInvoice().getId())
//                .productName(updatedDetail.getProductName())
//                .quantity(updatedDetail.getAmountOfProduct())
//                .price(updatedDetail.getPrice())
//                .build();
//    }
}
