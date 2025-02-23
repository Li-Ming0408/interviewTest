package com.example.demo.shoppingReceipt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.shoppingReceipt.ShoppingReceipt.Product;
import com.example.demo.shoppingReceipt.ShoppingReceipt.Purchase;
import com.example.demo.shoppingReceipt.ShoppingReceipt.Receipt;

public class ShoppingReceiptTest {

    ShoppingReceipt shoppingReceipt = new ShoppingReceipt();
    Product error = shoppingReceipt.new Product(null, null);
    Product book = shoppingReceipt.new Product("book", "Book");
    Product potato = shoppingReceipt.new Product("potato", "Food");
    Product shirt = shoppingReceipt.new Product("shirt", "Clothing");
    Product pencil = shoppingReceipt.new Product("pencil", "Stationery");

    @Test
    void testTaxByLocation() {

        // 測試 CA 的州稅
        ShoppingReceipt.TaxInterface taxCA = shoppingReceipt.new TaxCA();

        assertThrows(IllegalArgumentException.class, () -> taxCA.taxByLocation(error));
        assertEquals(0.0975, taxCA.taxByLocation(book));
        assertEquals(0.0975, taxCA.taxByLocation(shirt));
        assertEquals(0.0, taxCA.taxByLocation(potato));
        assertEquals(0.0975, taxCA.taxByLocation(pencil));

        // 測試 NY 的州稅
        ShoppingReceipt.TaxInterface taxNY = shoppingReceipt.new TaxNY();
        assertThrows(IllegalArgumentException.class, () -> taxNY.taxByLocation(error));
        assertEquals(0.08875, taxNY.taxByLocation(book));
        assertEquals(0.0, taxNY.taxByLocation(shirt));
        assertEquals(0.0, taxNY.taxByLocation(potato));
        assertEquals(0.08875, taxNY.taxByLocation(pencil));

    }

    @Test
    void testCalculate() {

        // 測試 Use case 1
        List<Purchase> purchases1 = List.of(
                shoppingReceipt.new Purchase(book, 17.99, 1),
                shoppingReceipt.new Purchase(potato, 3.99, 1));
        Receipt receipt1 = shoppingReceipt.calculate(purchases1, "CA");
        assertNotNull(receipt1);
        assertEquals(21.98, receipt1.getSubtotal());
        assertEquals(1.80, receipt1.getTax());
        assertEquals(23.78, receipt1.getTotal());

        // 測試 Use case 2
        List<Purchase> purchases2 = List.of(
                shoppingReceipt.new Purchase(book, 17.99, 1),
                shoppingReceipt.new Purchase(pencil, 2.99, 3));
        Receipt receipt2 = shoppingReceipt.calculate(purchases2, "NY");
        assertNotNull(receipt2);
        assertEquals(26.96, receipt2.getSubtotal());
        assertEquals(2.40, receipt2.getTax(), 0.001);
        assertEquals(29.36, receipt2.getTotal());

        // 測試 Use case 3
        List<Purchase> purchases3 = List.of(
                shoppingReceipt.new Purchase(pencil, 2.99, 2),
                shoppingReceipt.new Purchase(shirt, 29.99, 1));
        Receipt receipt3 = shoppingReceipt.calculate(purchases3, "NY");
        assertNotNull(receipt3);
        assertEquals(35.97, receipt3.getSubtotal(), 0.01);
        assertEquals(0.55, receipt3.getTax(), 0.01);
        assertEquals(36.52, receipt3.getTotal(), 0.01);

        // 測試負的價錢
        assertThrows(IllegalArgumentException.class, () -> {
            List<Purchase> invalidPurchase1 = List.of(shoppingReceipt.new Purchase(book, -17.99, 1));
            shoppingReceipt.calculate(invalidPurchase1, "CA");
        });

        // 測試負的數量
        assertThrows(IllegalArgumentException.class, () -> {
            List<Purchase> invalidPurchase2 = List.of(shoppingReceipt.new Purchase(book, 17.99, -1));
            shoppingReceipt.calculate(invalidPurchase2, "NY");
        });

        // 測試未定義稅率的州
        assertThrows(IllegalArgumentException.class, () -> {
            List<Purchase> purchasesInvalid = List.of(shoppingReceipt.new Purchase(book, 17.99, 1));
            shoppingReceipt.calculate(purchasesInvalid, "DC");
        });

        // 測試空 List
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingReceipt.calculate(null, "CA");
        });
    }

    // 用 main 方法測試列印收據
    public static void main(String[] args) {

        ShoppingReceipt shoppingReceipt = new ShoppingReceipt();

        // 創建產品
        Product book = shoppingReceipt.new Product("book", "Book");
        Product potato = shoppingReceipt.new Product("potato", "Food");

        // 創建購買清單
        List<Purchase> purchases = List.of(
                shoppingReceipt.new Purchase(book, 17.99, 1),
                shoppingReceipt.new Purchase(potato, 3.99, 1));

        // 創建收據
        Receipt receipt = shoppingReceipt.calculate(purchases, "CA");

        // 列印收據
        shoppingReceipt.printReceipt(receipt);
    }
}
