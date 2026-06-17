package com.wex.product_service.service;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;


    public ResponseEntity<List<Product>> findAllProducts(){
        return new ResponseEntity<>(productRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Product> findProductById(String id){
        return new ResponseEntity<>(productRepo.findById(id).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<Product> findProductByName(String name){
        return  new ResponseEntity<>(productRepo.findByName(name).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<Product> saveProduct(Product product){
        Product saved = productRepo.save(product);
        return new ResponseEntity<>(saved,HttpStatus.OK);
    }

    public ResponseEntity<Product> updateProduct(Product product){
        Product updated = productRepo.save(product);
        return new ResponseEntity<>(updated,HttpStatus.OK);
    }

    public ResponseEntity<String> deleteProduct(String id){
        productRepo.deleteById(id);
        return new ResponseEntity<>("product deleted successfully",HttpStatus.OK);
    }

    public ResponseEntity<OrderItem> addToOrder(String productId, int quantity) {
        Product product = productRepo.findById(productId).orElse(null);
        OrderItem orderItem = new OrderItem();
        orderItem.setName(product.getName());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()*quantity);
        orderItem.setDescription(product.getDescription());

        product.setAmount(product.getAmount()-quantity);
        updateProduct(product);

        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }
}
