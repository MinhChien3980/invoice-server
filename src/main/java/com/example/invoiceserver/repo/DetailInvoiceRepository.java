package com.example.invoiceserver.repo;

import com.example.invoiceserver.entity.DetailInvoice;
import com.example.invoiceserver.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailInvoiceRepository extends JpaRepository<DetailInvoice, Long> {
//    Optional<DetailInvoice> findByDeInvoiceNumber(String invoiceNumber);
    List<DetailInvoice> findByInvoiceId(Long invoiceId);

}
