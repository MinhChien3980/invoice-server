package com.example.invoiceserver.controller;

import com.example.invoiceserver.dto.request.InvoiceRequest;
import com.example.invoiceserver.dto.response.InvoiceResponse;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.repo.InvoiceRepository;
import com.example.invoiceserver.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Create Invoice
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest invoiceRequest) throws IOException {
        return ResponseEntity.ok(invoiceService.createInvoice(invoiceRequest));
    }

    // Get Invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // Get All Invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // Update Invoice
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(@PathVariable Long id, @RequestBody InvoiceRequest invoiceRequest) throws IOException {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceRequest));
    }

    // Delete Invoice
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("invoiceNumber") String invoiceNumber) {
        try {
            // Ensure directory exists
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save file with a unique name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(FILE_DIRECTORY, fileName);
            Files.write(filePath, file.getBytes());

            // Retrieve invoice by number
            Optional<Invoice> invoiceOpt = invoiceRepository.findByInvoiceNumber(invoiceNumber);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoice.setPdfOrImgPath("uploads/" + fileName);
                invoice.setStatusHasInvoice(true);
                invoiceRepository.save(invoice);

                return ResponseEntity.ok("File uploaded successfully. File Path: " + "uploads/" + fileName);
            } else {
                return ResponseEntity.status(404).body("Invoice not found!");
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }
    @PostMapping("/{invoiceId}/upload")
    public ResponseEntity<String> uploadInvoiceFile(@PathVariable Long invoiceId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            String filePath = invoiceService.saveInvoiceFile(invoiceId, file);
            return ResponseEntity.ok("File uploaded successfully. File Path: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }

}
