public class YearSaleInfo {
    private int year;
    private double totalSale;
    private int totalAmount;
    private double averageDiscountPercent;

    public YearSaleInfo(int year, double totalSale, int totalAmount, Double averageDiscountPercent) {
        this.year = year;
        this.totalSale = totalSale;
        this.totalAmount = totalAmount;
        this.averageDiscountPercent = averageDiscountPercent;
    }

    public int getYear() {
        return year;
    }
    public double getTotalSale() {
        return totalSale;
    }
    public int getTotalAmount() {
        return totalAmount;
    }
    public double getAverageDiscountPercent() {
        return averageDiscountPercent;
    }
}
