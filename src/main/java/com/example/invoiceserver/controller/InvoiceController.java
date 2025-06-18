package com.example.invoiceserver.controller;

import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.repo.InvoiceRepository;
import com.example.invoiceserver.service.InvoiceService;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceController {
    InvoiceService invoiceService;
    InvoiceRepository invoiceRepository;
    private static final String FILE_DIRECTORY = "src/main/resources/uploads/";

    // Create Invoice - Requires ADMIN or USER role
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        try {
            InvoiceResponse response = invoiceService.createInvoice(invoiceRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating invoice: " + e.getMessage());
        }
    }

    // Get All Invoices - Available to all authenticated users
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // Update Invoice - Only ADMIN can update
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @RequestBody InvoiceRequest invoiceRequest) throws IOException {
        InvoiceResponse updatedInvoice = invoiceService.updateInvoice(id, invoiceRequest);
        return ResponseEntity.ok(updatedInvoice);
    }
    // Upload file for invoice - Available to both roles
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("invoiceNumber") String invoiceNumber) {
        try {
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(FILE_DIRECTORY, fileName);
            Files.write(filePath, file.getBytes());

            Optional<Invoice> invoiceOpt = invoiceRepository.findByInvoiceNumber(invoiceNumber);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoice.setPdfOrImgPath("static/uploads/" + fileName);
                invoice.setStatusHasInvoice(true);
                invoiceRepository.save(invoice);

                return ResponseEntity.ok("File uploaded successfully. File Path: " + "static/uploads/" + fileName);
            } else {
                return ResponseEntity.status(404).body("Invoice not found!");
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }
    @PostMapping("/{invoiceId}/upload")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadInvoiceFile(@PathVariable Long invoiceId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            String filePath = invoiceService.saveInvoiceFile(invoiceId, file);
            System.out.println("✅ File saved at: " + filePath); // Debugging log
            return ResponseEntity.ok("File uploaded successfully. File Path: " + filePath);
        } catch (Exception e) {
            System.err.println("🚨 File upload failed: " + e.getMessage()); // Debugging log
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok("✅ Invoice and associated details deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 Invoice not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        try {
            InvoiceResponse response = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 Invoice not found: " + e.getMessage());
        }
    }


}
