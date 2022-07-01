package com.example.androidforeversource;

import org.testng.annotations.Test;
import static org.junit.Assert.*;

public class EstimateTests {
    @Test
    public void estimateGetCostTest(){
        Estimate estimate = new Estimate();
        Product product = new Product();

        product.currentPrice = 10d;
        estimate.products.add(product);

        product.currentPrice = 20d;
        estimate.products.add(product);

        assertEquals("30", estimate.getCost());
    }
}