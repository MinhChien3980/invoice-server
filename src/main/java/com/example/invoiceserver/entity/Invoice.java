package com.example.invoiceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    private String userName;
    private String productName;
    private int amountOfProduct;
    private double price;
    private boolean statusPaid;
    private boolean statusHasInvoice;
    private String dateBuy;
    private String outOfDateToPay;
    private String pdfOrImgPath;
}
