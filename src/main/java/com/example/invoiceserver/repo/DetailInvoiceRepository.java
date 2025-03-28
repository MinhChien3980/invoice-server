package com.example.invoiceserver.repo;

import com.example.invoiceserver.entity.DetailInvoice;
import com.example.invoiceserver.entity.Invoice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface DetailInvoiceRepository extends JpaRepository<DetailInvoice, Long> {
    List<DetailInvoice> findByInvoiceId(Long invoiceId);
    @Modifying
    @Transactional
    void deleteByInvoice(Invoice invoice);
}
