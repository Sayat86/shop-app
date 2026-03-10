package com.example.shopapp.product.controller;

import com.example.shopapp.product.dto.ProductCardResponse;
import com.example.shopapp.product.dto.ProductResponse;
import com.example.shopapp.product.entity.ProductStatus;
import com.example.shopapp.product.service.ProductService;
import com.example.shopapp.security.JwtService;
import com.example.shopapp.testdata.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@WithMockUser
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnProducts() throws Exception {

        ProductCardResponse response =
                TestDataFactory.productCardResponse();

        Page<ProductCardResponse> page =
                new PageImpl<>(List.of(response));

        when(productService.getCatalog(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(
                        get("/api/products")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void shouldReturnProductBySlug() throws Exception {

        when(productService.getBySlug("iphone"))
                .thenReturn(TestDataFactory.productResponse());

        mockMvc.perform(
                        get("/api/products/slug/iphone")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("iphone"))
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.brandName").value("Apple"));
    }

    @Test
    void shouldReturnPopularProducts() throws Exception {

        List<ProductResponse> products = List.of(
                new ProductResponse(
                        1L,
                        "iPhone",
                        "iphone",
                        "Smartphone",
                        ProductStatus.ACTIVE,
                        1L,
                        "Apple",
                        "iphone.jpg",
                        4.8,
                        120,
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
        ));

        when(productService.getPopularProducts(5))
                .thenReturn(products);

        mockMvc.perform(
                        get("/api/products/popular")
                                .param("limit", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteProduct() throws Exception {

        mockMvc.perform(
                        delete("/api/products/1")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(1L);
    }
}