package com.hiccup.cura.mapper;

import com.hiccup.cura.model.Product;
import com.hiccup.cura.request.ProductDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ModelMapper modelMapper;

    public ProductDto productToDto(Product product){
        return modelMapper.map(product,ProductDto.class);
    }

    public Product DtoToProduct(ProductDto product){
        return modelMapper.map(product,Product.class);
    }
}
