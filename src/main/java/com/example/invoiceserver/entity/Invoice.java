package com.example.invoiceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String customerName;
    private String userName;
    private String productName;
    private boolean statusPaid;
    private boolean statusHasInvoice;
    private boolean aproved;
    private String approveDate;
    private String dateBuy;
    private String outOfDateToPay;
    private String pdfOrImgPath;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailInvoice> invoiceDetails = new ArrayList<>();
}
