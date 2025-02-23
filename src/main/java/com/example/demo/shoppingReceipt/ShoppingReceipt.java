package com.example.demo.shoppingReceipt;

import java.util.List;

public class ShoppingReceipt {

    // 商品定義
    class Product {
        String name;
        String category;

        public Product(String name, String category) {
            this.name = name;
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    // 商品組合
    class Purchase {

        Product product;
        double price;
        int quantity;

        public Purchase(Product product, double price, int quantity) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return product + "  " + price + "  " + quantity;
        }
    }

    // 收據
    class Receipt {
        List<Purchase> purchases;
        double subtotal;
        double tax;
        double total;

        public Receipt(List<ShoppingReceipt.Purchase> purchases, double subtotal, double tax, double total) {
            this.purchases = purchases;
            this.subtotal = subtotal;
            this.tax = tax;
            this.total = total;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }

        public double getTax() {
            return tax;
        }

        public void setTax(double tax) {
            this.tax = tax;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

    }

    // 依不同的州定義不同的課稅方法實作
    interface TaxInterface {
        double taxByLocation(Product product);
    }

    class TaxCA implements TaxInterface {
        public double taxByLocation(Product product) {
            if (product == null || product.getCategory() == null) {
                throw new IllegalArgumentException("參數錯誤");
            }
            return (product != null && product.getCategory().equals("Food")) ? 0.0 : 0.0975;
        }
    }

    class TaxNY implements TaxInterface {
        public double taxByLocation(Product product) {
            if (product == null || product.getCategory() == null) {
                throw new IllegalArgumentException("參數錯誤");
            }
            return (product.getCategory().equals("Food") || product.getCategory().equals("Clothing")) ? 0.0
                    : 0.08875;
        }
    }

    // 計算價錢方法
    Receipt calculate(List<Purchase> purchases, String location) throws IllegalArgumentException {
        double subtotal = 0.0;
        double taxBeforeRound = 0.0;
        double tax = 0.0;
        double total = 0.0;

        TaxInterface taxInterface = null;
        if (location.equals("CA")) {
            taxInterface = new TaxCA();
        } else if (location.equals("NY")) {
            taxInterface = new TaxNY();
        } else {
            throw new IllegalArgumentException(location + "州課稅方式未定義");
        }

        if (purchases == null) {
            throw new IllegalArgumentException("參數錯誤");
        }
        for (Purchase purchase : purchases) {
            if (purchase.product == null || purchase.quantity < 0 || purchase.price < 0) {
                throw new IllegalArgumentException("品項資料錯誤");
            }
            taxBeforeRound = purchase.price * purchase.quantity * taxInterface.taxByLocation(purchase.product);
            tax += Math.ceil(taxBeforeRound * 20) / 20.0;
            subtotal += purchase.price * purchase.quantity;
        }
        total = Math.round((subtotal + tax) * 100.0) / 100.0;
        Receipt receipt = new Receipt(purchases, subtotal, tax, total);
        return receipt;
    }

    void printReceipt(Receipt receipt) {
        System.out.println("item  price  qty");
        for (Purchase purchase : receipt.purchases) {
            System.out.println(purchase.toString());
        }
        System.out.println("subtotal:  $" + (Math.round(receipt.subtotal * 100.0) / 100.0));
        System.out.println("tax:  $" + (Math.round(receipt.tax * 100.0) / 100.0));
        System.out.println("total:  $" + (Math.round(receipt.total * 100.0) / 100.0));
    }
}
