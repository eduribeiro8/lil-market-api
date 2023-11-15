package com.eduribeiro8.LilMarket;

import com.eduribeiro8.LilMarket.dao.AppDAO;
import com.eduribeiro8.LilMarket.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class LilMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(LilMarketApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(AppDAO appDAO){
//		return  runner -> {
////			createCustomer(appDAO);
////			createProducts(appDAO);
////			createSale(appDAO);
////			deleteSale(appDAO);
//			findSale(appDAO);
//		};
//	}

	private void findSale(AppDAO appDAO) {
		Sale sale = appDAO.findSaleById(40);
		System.out.println(sale);
	}

	private void deleteSale(AppDAO appDAO) {
		appDAO.deleteSaleId(36);
	}

	private void createSale(AppDAO appDAO) {
		Sale sale = new Sale();
		sale.setCustomer(appDAO.findCustomerById(1));

		Date date = new Date();
		sale.setTimestamp(date);

		// Fetch the products within the same transaction to ensure they are managed.
		Product tempProduct1 = appDAO.findProductById(1);
		Product tempProduct3 = appDAO.findProductById(3);
		Product tempProduct2 = appDAO.findProductById(2);

		// Add the SaleItem instances to the Sale.
		sale.addSaleItem(tempProduct1, 4);
		sale.addSaleItem(tempProduct2, 5);
		sale.addSaleItem(tempProduct3, 6);

		// Save the Sale within the same transaction.
		appDAO.save(sale);
	}



//	private void createProducts(AppDAO appDAO) {
//		List<Product> productList = new ArrayList<>();
//		Product product1 = new Product("Product1", "", 5.23, ProductCategory.TESTE1, 23);
//		Product product2 = new Product("Product2", "", 5.85, ProductCategory.TESTE2, 25);
//		Product product3 = new Product("Product3", "", 9.65, ProductCategory.TESTE3, 43);
//		Product product4 = new Product("Product4", "", 9.62, ProductCategory.TESTE1, 34);
//
//		productList.add(product1);
//		productList.add(product2);
//		productList.add(product3);
//		productList.add(product4);
//
//		for (Product product: productList) appDAO.save(product);
//	}

	private void createCustomer(AppDAO appDAO) {
		Customer customer = new Customer();
		customer.setFirstName("Eu");
		customer.setLastName("Sei la");
		customer.setEmail("asdf@gmail.com");
		customer.setPhoneNumber("99999999999");
		customer.setDebt(0);
		appDAO.save(customer);
	}

}
