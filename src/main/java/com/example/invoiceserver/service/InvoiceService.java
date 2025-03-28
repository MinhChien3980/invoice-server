package com.example.invoiceserver.service;

import com.example.invoiceserver.dto.request.InvoiceDetailRequest;
import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceDetailResponse;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.entity.DetailInvoice;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.mapper.InvoiceMapper;
import com.example.invoiceserver.repo.DetailInvoiceRepository;
import com.example.invoiceserver.repo.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final DetailInvoiceRepository detailInvoiceRepository;
    public static final String FILE_DIRECTORY = "src/main/resources/static/uploads/";

    // Save file to resources/uploads and return the file path
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(FILE_DIRECTORY, fileName);
        Files.write(filePath, file.getBytes());

        return "static/uploads/" + fileName;
    }

    // Create Invoice
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceRequest.getInvoiceNumber());
        invoice.setUserName(invoiceRequest.getUserName());
//        invoice.setProductName(invoiceRequest.getProductName());

        invoice.setCustomerName(invoiceRequest.getCustomerName());
        //không cần set approve giai đoạn này

//        invoice.setAmountOfProduct(invoiceRequest.getAmountOfProduct());
//        invoice.setPrice(invoiceRequest.getPrice());
        invoice.setStatusPaid(invoiceRequest.isStatusPaid());
        invoice.setDateBuy(invoiceRequest.getDateBuy());
        invoice.setOutOfDateToPay(invoiceRequest.getOutOfDateToPay());
        invoice.setApproveDate("");
        // Check if file exists to set statusHasInvoice
        if (invoiceRequest.getPdfOrImgPath() != null && !invoiceRequest.getPdfOrImgPath().isEmpty()) {
            invoice.setPdfOrImgPath(invoiceRequest.getPdfOrImgPath());
            invoice.setStatusHasInvoice(true);
        } else {
            invoice.setStatusHasInvoice(false);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(savedInvoice);
    }

    // Update Invoice
    @Transactional
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) throws IOException {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Update invoice fields if provided
        if (invoiceRequest.getInvoiceNumber() != null) {
            existingInvoice.setInvoiceNumber(invoiceRequest.getInvoiceNumber());
        }
        if (invoiceRequest.getUserName() != null) {
            existingInvoice.setUserName(invoiceRequest.getUserName());
        }
        if (invoiceRequest.getCustomerName() != null) {
            existingInvoice.setCustomerName(invoiceRequest.getCustomerName());
        }
        if (invoiceRequest.getDateBuy() != null) {
            existingInvoice.setDateBuy(invoiceRequest.getDateBuy());
        }
        if (invoiceRequest.getOutOfDateToPay() != null) {
            existingInvoice.setOutOfDateToPay(invoiceRequest.getOutOfDateToPay());
        }
        existingInvoice.setStatusPaid(invoiceRequest.isStatusPaid());
        existingInvoice.setAproved(invoiceRequest.isAproved());
        existingInvoice.setApproveDate(invoiceRequest.getApproveDate() != null
                ? invoiceRequest.getApproveDate().toString()
                : null);

        // Handle file upload
        if (invoiceRequest.getFile() != null && !invoiceRequest.getFile().isEmpty()) {
            String filePath = saveFile(invoiceRequest.getFile());
            existingInvoice.setPdfOrImgPath(filePath);
            existingInvoice.setStatusHasInvoice(true);
        } else {
            existingInvoice.setStatusHasInvoice(existingInvoice.getPdfOrImgPath() != null);
        }

        if (invoiceRequest.getInvoiceDetails() != null) {
            Set<Long> incomingDetailIds = invoiceRequest.getInvoiceDetails().stream()
                    .map(InvoiceDetailRequest::getId)
                    .collect(Collectors.toSet());

            // Remove any details that are not in the request
            existingInvoice.getInvoiceDetails().removeIf(detail ->
                    !incomingDetailIds.contains(detail.getId()));

            // Process incoming details
            for (InvoiceDetailRequest detailRequest : invoiceRequest.getInvoiceDetails()) {
                DetailInvoice detail;
                if (detailRequest.getId() != null) {
                    // Find existing detail
                    detail = existingInvoice.getInvoiceDetails().stream()
                            .filter(d -> d.getId().equals(detailRequest.getId()))
                            .findFirst()
                            .orElse(new DetailInvoice());
                } else {
                    // Create new detail
                    detail = new DetailInvoice();
                    detail.setInvoice(existingInvoice);
                    existingInvoice.getInvoiceDetails().add(detail);
                }

                detail.setProductName(detailRequest.getProductName());
                detail.setAmountOfProduct(detailRequest.getAmountOfProduct());
                detail.setPrice(detailRequest.getPrice());
            }
        }

        // Save the invoice with updated details
        Invoice savedInvoice = invoiceRepository.save(existingInvoice);

        // Convert to response including details
        return InvoiceResponse.builder()
                .id(savedInvoice.getId())
                .invoiceNumber(savedInvoice.getInvoiceNumber())
                .userName(savedInvoice.getUserName())
                .aproved(savedInvoice.isAproved())
                .customerName(savedInvoice.getCustomerName())
                .approveDate(savedInvoice.getApproveDate() != null ? savedInvoice.getApproveDate().toString() : null)
                .statusPaid(savedInvoice.isStatusPaid())
                .statusHasInvoice(savedInvoice.isStatusHasInvoice())
                .dateBuy(savedInvoice.getDateBuy())
                .outOfDateToPay(savedInvoice.getOutOfDateToPay())
                .pdfOrImgPath(savedInvoice.getPdfOrImgPath())
                .invoiceDetails(savedInvoice.getInvoiceDetails().stream()
                        .map(detail -> InvoiceDetailResponse.builder()
                                .id(detail.getId())
                                .productName(detail.getProductName())
                                .quantity(detail.getAmountOfProduct())
                                .price(detail.getPrice())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }

    // Get Invoice by ID
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("🚨 Invoice not found!"));

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .userName(invoice.getUserName())
                .aproved(invoice.isAproved())
                .customerName(invoice.getCustomerName())
                .approveDate(invoice.getApproveDate() != null ? invoice.getApproveDate().toString() : null)
                .statusPaid(invoice.isStatusPaid())
                .statusHasInvoice(invoice.isStatusHasInvoice())
                .dateBuy(invoice.getDateBuy())
                .outOfDateToPay(invoice.getOutOfDateToPay())
                .pdfOrImgPath(invoice.getPdfOrImgPath())
                .invoiceDetails(invoice.getInvoiceDetails().stream()
                        .map(detail -> InvoiceDetailResponse.builder()
                                .id(detail.getId())
                                .productName(detail.getProductName())
                                .quantity(detail.getAmountOfProduct())
                                .price(detail.getPrice())
                                .build()
                        ).collect(Collectors.toList()))
                .build();
    }


    // Get All Invoices
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoice -> InvoiceResponse.builder()
                        .id(invoice.getId())
                        .invoiceNumber(invoice.getInvoiceNumber())
                        .userName(invoice.getUserName())
                        .aproved(invoice.isAproved())
                        .customerName(invoice.getCustomerName())
                        .approveDate(invoice.getApproveDate() != null ? invoice.getApproveDate().toString() : null)
                        .statusPaid(invoice.isStatusPaid())
                        .statusHasInvoice(invoice.isStatusHasInvoice())
                        .dateBuy(invoice.getDateBuy())
                        .outOfDateToPay(invoice.getOutOfDateToPay())
                        .pdfOrImgPath(invoice.getPdfOrImgPath())
                        .invoiceDetails(invoice.getInvoiceDetails().stream()
                                .map(detail -> InvoiceDetailResponse.builder()
                                        .id(detail.getId())
                                        .productName(detail.getProductName())
                                        .price(detail.getPrice())
                                        .build()
                                ).collect(Collectors.toList()))
                        .build()
                ).collect(Collectors.toList());
    }


    // Delete Invoice
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("🚨 Invoice not found!"));

        detailInvoiceRepository.deleteAll(invoice.getInvoiceDetails());

        invoiceRepository.delete(invoice);
    }

    public boolean isInvoicePaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoice.isStatusPaid();
    }

    public boolean isInvoiceHasInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoice.isStatusHasInvoice();
    }
    public String saveInvoiceFile(Long invoiceId, MultipartFile file) throws IOException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(directory.getAbsolutePath(), fileName);
        Files.write(filePath, file.getBytes());

        String relativePath = "static/uploads/" + fileName;
        invoice.setPdfOrImgPath(relativePath);
        invoice.setStatusHasInvoice(true);

        invoiceRepository.save(invoice);

        return relativePath;
    }

}
