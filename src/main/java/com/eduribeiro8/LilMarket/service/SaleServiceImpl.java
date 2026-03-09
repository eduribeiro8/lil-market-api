package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleItemRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.*;
import com.eduribeiro8.LilMarket.mapper.SaleMapper;
import com.eduribeiro8.LilMarket.repository.*;
import com.eduribeiro8.LilMarket.rest.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final BatchService batchService;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponseDTO save(SaleRequestDTO saleRequestDTO) {
        Sale revisedSale = new Sale();
        BigDecimal profit = BigDecimal.ZERO;

        Customer customer = customerRepository.findById(saleRequestDTO.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer(id = " + saleRequestDTO.customerId() + " not found"));
        revisedSale.setCustomer(customer);

        User user = userRepository.findById(saleRequestDTO.userId())
                .orElseThrow(() -> new UserNotFoundException("User(id = " + saleRequestDTO.userId() + ") not found"));
        revisedSale.setUser(user);

        for (SaleItemRequestDTO saleItem: saleRequestDTO.items()){
            Product product =
                    productRepository.findById(saleItem.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product(id = " + saleItem.productId() +  ") not found"));

            List<Batch> batches = batchService.findBatchesInStock(product, saleItem.quantity());

            BigDecimal remainingToRecord = saleItem.quantity();

            for (Batch batch: batches){
                if (remainingToRecord.compareTo(BigDecimal.ZERO) <= 0) break;
                SaleItem revisedSaleItem = new SaleItem();

                revisedSaleItem.setProduct(product);
                revisedSaleItem.setSale(revisedSale);
                revisedSaleItem.setBatch(batch);

                BigDecimal quantityFromThisBatch = remainingToRecord.min(batch.getQuantityInStock());

                revisedSaleItem.setQuantity(quantityFromThisBatch);
                revisedSaleItem.setUnitPrice(batch.getProduct().getPrice());
                revisedSaleItem.setSubtotal(revisedSaleItem.getUnitPrice().multiply(revisedSaleItem.getQuantity()));
                BigDecimal diff = batch.getProduct().getPrice().subtract(batch.getPurchasePrice());
                profit = profit.add(diff.multiply(quantityFromThisBatch));

                remainingToRecord = remainingToRecord.subtract(quantityFromThisBatch);

                revisedSale.addSaleItem(revisedSaleItem);
            }

            batches = batchService.decrementBatches(batches, product, saleItem.quantity());
        }

        revisedSale.setAmountPaid(saleRequestDTO.amountPaid());
        revisedSale.setNetProfit(profit);
        revisedSale.setNotes(saleRequestDTO.notes());
        revisedSale.resolvePaymentStatus();

        if (!revisedSale.getPaymentStatus().equals(PaymentStatus.PAID)){
            if (saleRequestDTO.isOnAccount()){
                customer.addDebt(revisedSale.getTotal().subtract(revisedSale.getAmountPaid()));
            }else{
                throw new BusinessException("As it's not an OnAccount sale, sale can not be completed");
            }
        }

        Sale savedSale = saleRepository.save(revisedSale);

        if (revisedSale.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            CustomerPayment customerPayment = new CustomerPayment();
            customerPayment.setCustomer(customer);
            customerPayment.setAmountPaid(revisedSale.getAmountPaid());
            customerPayment.setPaymentMethod(saleRequestDTO.paymentMethod());
            customerPayment.setNotes(
                    "Cliente pagou R$" + revisedSale.getAmountPaid() + " de uma compra de R$"
                            + revisedSale.getTotal() + " (saleId = " + savedSale.getId() + ")");
            customerPaymentRepository.save(customerPayment);
        }

        return saleMapper.toResponse(savedSale);
    }

    @Override
    public SaleResponseDTO findSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale(id = " + id + ") not found"));

        return saleMapper.toResponse(sale);
    }

    @Override
    public List<SaleResponseDTO> getSalesByDate(OffsetDateTime start, OffsetDateTime end) {
        if (start.isAfter(end)){
            throw new InvalidDateIntervalException("A data final não pode ser anterior à data inicial");
        }

        List<Sale> sales = saleRepository.findByTimestampBetween(start, end);

        if (sales.isEmpty()){
            throw new SaleNotFoundException("Nenhuma venda encontrada no intervalo requisitado");
        }

        return saleMapper.toResponseList(sales);
    }

    @Override
    @Transactional
    public SaleResponseDTO update(SaleRequestDTO saleRequestDTO) {
        Sale saleToSave = saleMapper.toEntity(saleRequestDTO);

        Sale saleSaved = saleRepository.save(saleToSave);

        return  saleMapper.toResponse(saleSaved);
    }
}
