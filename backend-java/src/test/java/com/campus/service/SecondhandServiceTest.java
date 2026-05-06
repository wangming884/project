package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.SecondhandProduct;
import com.campus.entity.User;
import com.campus.mapper.SecondhandProductMapper;
import com.campus.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecondhandServiceTest {

    @Mock
    private SecondhandProductMapper productMapper;

    @Mock
    private UserMapper userMapper;

    private SecondhandService secondhandService;

    @BeforeEach
    void setUp() {
        secondhandService = new SecondhandService(productMapper, userMapper);
    }

    @Test
    void publishProductNormalizesFieldsAndInitialState() {
        User user = new User();
        user.setId(7L);
        user.setUsername("alice");
        when(userMapper.selectById(7L)).thenReturn(user);
        doAnswer(invocation -> {
            SecondhandProduct product = invocation.getArgument(0);
            product.setId(101L);
            return 1;
        }).when(productMapper).insert(any(SecondhandProduct.class));

        Map<String, Object> result = secondhandService.publishProduct(
            7L,
            "  二手耳机  ",
            "  成色很好  ",
            new BigDecimal("299.00"),
            " electronics ",
            "  https://example.com/demo.jpg  ",
            "  vx:alice-demo  "
        );

        ArgumentCaptor<SecondhandProduct> productCaptor = ArgumentCaptor.forClass(SecondhandProduct.class);
        verify(productMapper).insert(productCaptor.capture());
        SecondhandProduct savedProduct = productCaptor.getValue();

        assertEquals(101L, result.get("productId"));
        assertEquals("二手耳机", savedProduct.getTitle());
        assertEquals("成色很好", savedProduct.getDescription());
        assertEquals("electronics", savedProduct.getCategory());
        assertEquals("https://example.com/demo.jpg", savedProduct.getImages());
        assertEquals("vx:alice-demo", savedProduct.getContact());
        assertEquals("available", savedProduct.getStatus());
        assertEquals(0, savedProduct.getViews());
        assertNotNull(savedProduct.getPrice());
    }

    @Test
    void updateStatusRejectsUnsupportedStatus() {
        SecondhandProduct product = new SecondhandProduct();
        product.setId(9L);
        product.setSellerId(3L);
        when(productMapper.selectById(9L)).thenReturn(product);

        RuntimeException error = assertThrows(RuntimeException.class,
            () -> secondhandService.updateStatus(9L, 3L, "archived"));

        assertEquals("状态仅支持 available / sold / removed", error.getMessage());
        verify(productMapper, never()).updateById(any(SecondhandProduct.class));
    }

    @Test
    void getProductContactInfoDoesNotIncreaseViews() {
        SecondhandProduct product = new SecondhandProduct();
        product.setId(12L);
        product.setViews(5);
        when(productMapper.selectById(12L)).thenReturn(product);

        SecondhandProduct result = secondhandService.getProductContactInfo(12L);

        assertEquals(product, result);
        assertEquals(5, result.getViews());
        verify(productMapper, never()).updateById(any(SecondhandProduct.class));
    }

    @Test
    void getProductsNormalizesInvalidPaginationValues() {
        when(productMapper.selectPage(any(Page.class), any())).thenAnswer(invocation -> invocation.getArgument(0));

        Page<SecondhandProduct> page = secondhandService.getProducts(null, null, "unsupported", 0, 999);

        assertEquals(1L, page.getCurrent());
        assertEquals(100L, page.getSize());
    }
}
