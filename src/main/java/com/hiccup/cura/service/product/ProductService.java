package com.hiccup.cura.service.product;

import com.hiccup.cura.exception.ProductAlreadyFound;
import com.hiccup.cura.exception.ProductNotFound;
import com.hiccup.cura.mapper.ProductMapper;
import com.hiccup.cura.model.Product;
import com.hiccup.cura.repository.ProductRepository;
import com.hiccup.cura.request.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public void addProduct(ProductDto product){
        productRepository.findByServiceName(product.getServiceName()).ifPresent(p->{
            throw new ProductAlreadyFound("Opps! Cannot and new product. Product already exists with this name.");
        });
        productRepository.save(productMapper.DtoToProduct(product));
    }

    public ProductDto getProduct(String serviceName){
        Product product = productRepository.findByServiceName(serviceName).orElseThrow(() -> new ProductNotFound("The product you are trying to search is not available. It may be deleted or never existed."));
        return productMapper.productToDto(product);
    }

    public List<ProductDto> getProducts(){
        return productRepository.findAll()
                .stream().map(productMapper::productToDto)
                .collect(Collectors.toList());
    }

    public ProductDto updateProduct(Product product, Long id){
       Product updatedProduct=productRepository.findById(id)
            .map(storedProduct->{
                storedProduct.setServiceName(product.getServiceName());
                storedProduct.setDescription(product.getDescription());
                storedProduct.setPrice(product.getPrice());
                return productRepository.save(storedProduct);
            }).orElseThrow(()->new ProductNotFound("The product trying to update was deleted or never created"));
       return productMapper.productToDto(updatedProduct);
    }
}
