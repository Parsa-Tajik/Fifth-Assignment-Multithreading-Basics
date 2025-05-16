import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReportGenerator {
    static class TaskRunnable implements Runnable {
        private final String path;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountPercent;
        private double totalDiscountSum;
        private int totalLines;
        private double highestCostAfterDiscount;

        // The lock's static so all threads running a TaskRunnable can use the same one.
        private static Lock lock = new ReentrantLock();

        public TaskRunnable(String path) {
            this.path = path;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountPercent = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
        }

        @Override
        public void run() {
            try {
                File file = new File(path);
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String[] lineArray = scanner.nextLine().split(",");

                    int productID = Integer.parseInt(lineArray[0]);
                    Product purchasedProduct = null;
                    for (Product p : productCatalog) {
                        if (p.getProductID() == productID) {
                            purchasedProduct = p;
                        }
                    }

                    int amount = Integer.parseInt(lineArray[1]);

                    int discountPercent = Integer.parseInt(lineArray[2]);
                    totalDiscountPercent += discountPercent;
                    double discount = purchasedProduct.getPrice() * amount * (discountPercent / 100.0);

                    double cost = (purchasedProduct.getPrice() * amount) - discount;
                    if (cost > highestCostAfterDiscount) {
                        highestCostAfterDiscount = cost;
                    }

                    totalCost += cost;
                    totalAmount += amount;
                    totalDiscountSum += discount;
                    totalLines += 1;
                }
            } catch (IOException e) {
                System.out.println("Error reading data: " + e.getMessage());
            }

            makeReport();
        }

        public void makeReport() {
            lock.lock();

            System.out.println("\u001B[34m" + "** File: " + path + " **" + "\u001B[0m");
            System.out.printf("Total Cost: %.2f\n", totalCost);
            System.out.println("Total Amount: " + totalAmount);
            System.out.printf("Total Discount: %.2f\n", totalDiscountSum);
            System.out.printf("Average discount percentage: %.2f\n\n", (double)totalDiscountPercent / totalLines);
            System.out.printf("Highest purchase amount: %.2f\n\n", highestCostAfterDiscount);

            lock.unlock();

            // get year from the path
            String[] pathArray = path.split("/");
            String yearStr = pathArray[pathArray.length - 1].split("_")[0];
            int year = Integer.parseInt(yearStr);

            yearSaleInfos.add(new YearSaleInfo(year, totalCost, totalAmount, (double)totalDiscountPercent / totalLines));
        }
    }

    static class Product {
        private int productID;
        private String productName;
        private double price;

        public Product(int productID, String productName, double price) {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID() {
            return productID;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }
    }
    private static final String[] ORDER_FILES = {
            "src/main/resources/2021_order_details.txt",
            "src/main/resources/2022_order_details.txt",
            "src/main/resources/2023_order_details.txt",
            "src/main/resources/2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[9];

    public static void loadProducts(){
        try {
            File file = new File("src/main/resources/products.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < 9; i++) {
                String[] line = reader.readLine().split(",");
                productCatalog[i] = new Product(Integer.parseInt(line[0]), line[1], Double.parseDouble(line[2]));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("***Error loading products!***");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        loadProducts();

        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (String path : ORDER_FILES) {
            pool.execute(new TaskRunnable(path));
        }
        pool.shutdown(); // pool must not wait for more tasks.

        //The awaitTermination method waits for the amount of time you specify.
        //If all tasks in the pool finish during this period, it returns true immediately.
        //However, if some tasks are still running after the specified time, it returns false.
        if (pool.awaitTermination(1, TimeUnit.MINUTES))
        {
            sortDataByYear();
            printCharts();
        }
    }

    static ArrayList<YearSaleInfo> yearSaleInfos = new ArrayList<>();
    private static void sortDataByYear() {
        for (int i = 0; i < yearSaleInfos.size(); i++) {
            for (int j = yearSaleInfos.size() - 1; j > i; j--) {
                if (yearSaleInfos.get(j).getYear() < yearSaleInfos.get(j - 1).getYear()) {
                    YearSaleInfo temp = yearSaleInfos.get(j - 1);
                    yearSaleInfos.set(j - 1, yearSaleInfos.get(j));
                    yearSaleInfos.set(j, temp);
                }
            }
        }
    }

    private static void printCharts() {
        final String colorRed = "\u001B[31m";
        final String colorYellow = "\u001B[33m";
        final String colorGreen = "\u001B[32m";
        final String colorBlue = "\u001B[34m";
        final String colorReset = "\u001B[0m";

        //System.out.println("** Total Sale Chat **");
        double maxTotalSale = 0;
        double maxTotalAmount = 0;
        double maxAverageDiscount = 0;

        // declares the maximum amounts
        for (YearSaleInfo yearSaleInfo : yearSaleInfos) {
            if (yearSaleInfo.getTotalSale() > maxTotalSale) {
                maxTotalSale = yearSaleInfo.getTotalSale();
            }
            if (yearSaleInfo.getTotalAmount() > maxTotalAmount) {
                maxTotalAmount = yearSaleInfo.getTotalAmount();
            }
            if (yearSaleInfo.getAverageDiscountPercent() > maxAverageDiscount) {
                maxAverageDiscount = yearSaleInfo.getAverageDiscountPercent();
            }
        }

        // handle chart printingss
        for (YearSaleInfo yearSaleInfo : yearSaleInfos) {
            System.out.printf(colorGreen + "%-5d┤ " + colorReset, yearSaleInfo.getYear());

            printChartLine(yearSaleInfo.getTotalSale(), maxTotalSale, colorBlue, "Total Sale: ", "$");
            System.out.print("\n");

            System.out.print(colorGreen + "     │ " + colorReset);
            printChartLine(yearSaleInfo.getTotalAmount(), maxTotalAmount, colorYellow, "Total Amount: ", "");
            System.out.print("\n");

            System.out.print(colorGreen + "     │ " + colorReset);
            printChartLine(yearSaleInfo.getAverageDiscountPercent(), maxAverageDiscount, colorRed, "Average Discount: ", "%");
            System.out.print("\n");

            System.out.println();
        }
    }

    private static void printChartLine(double value, double max, String color, String prefix, String suffix) {
        final int maxChartSize = 70;

        System.out.print(color);
        int length = (int)((value * maxChartSize) / max);
        for (int i = 0; i < length; i++) {
            System.out.print("█");
        }
        for (int i = length; i < maxChartSize; i++) {
            System.out.print("-");
        }

        System.out.printf(" %s%.2f%s", prefix, value, suffix);
        System.out.print("\u001B[0m");
    }
}