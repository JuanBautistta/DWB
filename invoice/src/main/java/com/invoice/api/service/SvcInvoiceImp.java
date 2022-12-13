package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;
	
	@Autowired
	RepoCart repoCart;
	
	@Autowired
	ProductClient productClient;

	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		/*
		 * Requerimiento 5
		 * Implementar el método para generar una factura 
		 */
		List<Cart> carts = repoCart.findByRfcAndStatus(rfc, 1);
		if (carts.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");
		List<Item> items = new ArrayList<>();
		Invoice invoice = new Invoice();
		for (Cart cart : carts) {
			Item item = new Item();
			DtoProduct product = productClient.getProduct(cart.getGtin()).getBody();
			item.setId_invoice(invoice.getInvoice_id());
			item.setGtin(cart.getGtin());
			item.setQuantity(cart.getQuantity());
			item.setUnit_price(product.getPrice());
			item.setTotal(cart.getQuantity() * product.getPrice());
			item.setTaxes(cart.getQuantity() * product.getPrice() * 0.16);
			item.setSubtotal((cart.getQuantity() * product.getPrice())-(cart.getQuantity() * product.getPrice() * 0.16));
			item.setStatus(1);
			repoItem.save(item);
			items.add(item);
			productClient.updateProductStock(cart.getGtin(), product.getStock()-cart.getQuantity());
		}
		for (Item item : items) {
			invoice.setTotal(invoice.getTotal() + item.getTotal());
			invoice.setTaxes(invoice.getTaxes() + item.getTaxes());
			invoice.setSubtotal(invoice.getSubtotal() + item.getSubtotal());
		}
		invoice.setRfc(rfc);
		invoice.setCreated_at(LocalDateTime.now());
		invoice.setStatus(1);
		repo.save(invoice);
		repoCart.clearCart(rfc);
		return new ApiResponse("invoice generated");
	}

}
