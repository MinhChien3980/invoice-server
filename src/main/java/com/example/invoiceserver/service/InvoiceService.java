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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    public static final String FILE_DIRECTORY = "src/main/resources/static/uploads/";

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
        Path filePath = Paths.get(FILE_DIRECTORY, fileName);
        Files.write(filePath, file.getBytes());

        return "static/uploads/" + fileName; // Return relative path
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
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) throws IOException {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        System.out.println("hhhh"+invoiceRequest.toString());
        // Chỉ cập nhật các giá trị nếu chúng được gửi trong request
        if (invoiceRequest.getInvoiceNumber() != null && !invoiceRequest.getInvoiceNumber().isEmpty()) {
            existingInvoice.setInvoiceNumber(invoiceRequest.getInvoiceNumber());
        }
        if (invoiceRequest.getUserName() != null && !invoiceRequest.getUserName().isEmpty()) {
            existingInvoice.setUserName(invoiceRequest.getUserName());
        }
        if (invoiceRequest.getCustomerName() != null && !invoiceRequest.getCustomerName().isEmpty()) {
            existingInvoice.setCustomerName(invoiceRequest.getCustomerName());
        }
        if (invoiceRequest.getDateBuy() != null) {
            existingInvoice.setDateBuy(invoiceRequest.getDateBuy());
        }
        if (invoiceRequest.getOutOfDateToPay() != null) {
            existingInvoice.setOutOfDateToPay(invoiceRequest.getOutOfDateToPay());
        }
        if (invoiceRequest.isStatusPaid() != existingInvoice.isStatusPaid()) {
            existingInvoice.setStatusPaid(invoiceRequest.isStatusPaid());
        }
        if (invoiceRequest.isAproved() != existingInvoice.isAproved()) {
            existingInvoice.setAproved(invoiceRequest.isAproved());
        }

        // Cập nhật ngày approve nếu được gửi từ request
        if (invoiceRequest.getApproveDate() != null) {
            existingInvoice.setApproveDate(invoiceRequest.getApproveDate().toString());
        }

        // Nếu có file mới được tải lên, cập nhật file và set `statusHasInvoice = true`
        if (invoiceRequest.getFile() != null && !invoiceRequest.getFile().isEmpty()) {
            String filePath = saveFile(invoiceRequest.getFile());
            existingInvoice.setPdfOrImgPath(filePath);
            existingInvoice.setStatusHasInvoice(true);
        } else {
            existingInvoice.setStatusHasInvoice(existingInvoice.getPdfOrImgPath() != null && !existingInvoice.getPdfOrImgPath().isEmpty());
        }

        // Lưu cập nhật vào cơ sở dữ liệu
        Invoice savedInvoice = invoiceRepository.save(existingInvoice);
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

        // Ensure directory exists
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save file with unique name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(directory.getAbsolutePath(), fileName);
        Files.write(filePath, file.getBytes());

        // Store relative file path
        String relativePath = "uploads/" + fileName;
        invoice.setPdfOrImgPath(relativePath);
        invoice.setStatusHasInvoice(true);
        invoiceRepository.save(invoice);

        return relativePath;
    }

}
