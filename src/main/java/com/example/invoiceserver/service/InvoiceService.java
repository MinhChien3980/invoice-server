package com.example.invoiceserver.service;

import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.mapper.InvoiceMapper;
import com.example.invoiceserver.repo.InvoiceRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    public static final String FILE_DIRECTORY = "src/main/resources/uploads/";

    // Save file to resources/uploads and return the file path
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // No file uploaded
        }

        // Ensure directory exists
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(FILE_DIRECTORY + fileName);
        Files.write(filePath, file.getBytes());

        return "uploads/" + fileName; // Return relative path
    }

    // Create Invoice
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceRequest.getInvoiceNumber());
        invoice.setUserName(invoiceRequest.getUserName());
        invoice.setProductName(invoiceRequest.getProductName());
        invoice.setAmountOfProduct(invoiceRequest.getAmountOfProduct());
        invoice.setPrice(invoiceRequest.getPrice());
        invoice.setStatusPaid(invoiceRequest.isStatusPaid());
        invoice.setStatusHasInvoice(invoiceRequest.isStatusHasInvoice());
        invoice.setDateBuy(invoiceRequest.getDateBuy());
        invoice.setOutOfDateToPay(invoiceRequest.getOutOfDateToPay());
        invoice.setPdfOrImgPath(invoiceRequest.getPdfOrImgPath()); // File path

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(savedInvoice);
    }

    // Update Invoice
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) throws IOException {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Invoice updatedInvoice = invoiceMapper.toInvoice(invoiceRequest);
        updatedInvoice.setId(existingInvoice.getId());

        // Save new file if uploaded
        if (invoiceRequest.getFile() != null && !invoiceRequest.getFile().isEmpty()) {
            String filePath = saveFile(invoiceRequest.getFile());
            updatedInvoice.setPdfOrImgPath(filePath);
            updatedInvoice.setStatusHasInvoice(true);
        } else {
            updatedInvoice.setPdfOrImgPath(existingInvoice.getPdfOrImgPath());
        }

        Invoice savedInvoice = invoiceRepository.save(updatedInvoice);
        return invoiceMapper.toInvoiceResponse(savedInvoice);
    }

    // Get Invoice by ID
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    // Get All Invoices
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toInvoiceResponse)
                .collect(Collectors.toList());
    }

    // Delete Invoice
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found");
        }
        invoiceRepository.deleteById(id);
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

        // Save the file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file provided.");
        }

        // Ensure directory exists
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save file with unique name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(FILE_DIRECTORY + fileName);
        Files.write(filePath, file.getBytes());

        // Update invoice with file path
        invoice.setPdfOrImgPath("uploads/" + fileName);
        invoice.setStatusHasInvoice(true);
        invoiceRepository.save(invoice);

        return "uploads/" + fileName;
    }

}
