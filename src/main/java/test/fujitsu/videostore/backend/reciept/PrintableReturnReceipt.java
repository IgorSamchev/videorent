package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Return receipt printer
 */
public class PrintableReturnReceipt implements PrintableReceipt {

    private String orderId;
    private String customerName;
    private LocalDate rentDate;
    private LocalDate returnDate;
    private BigDecimal totalCharge;
    private List<Item> returnedItems;

    private String getOrderId() {
        return orderId;
    }

    void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String getCustomerName() {
        return customerName;
    }

    void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private LocalDate getRentDate() {
        return rentDate;
    }

    void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    private LocalDate getReturnDate() {
        return returnDate;
    }

    void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    private BigDecimal getTotalCharge() {
        return totalCharge;
    }

    void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    void setReturnedItems(List<Item> returnedItems) {
        this.returnedItems = returnedItems;
    }

    @Override
    public String print() {
        StringBuilder receipt = new StringBuilder()
                .append("ID: ").append(getOrderId()).append(" (Return)")
                .append("\n")
                .append("Rent date: ").append(getRentDate().format(DateTimeFormatter.ofPattern("dd-MM-YY")))
                .append("\n").append("Customer: ").append(getCustomerName())
                .append("\nReturn date: ").append(getReturnDate().format(DateTimeFormatter.ofPattern("dd-MM-YY")))
                .append("\n");

        returnedItems.forEach(item -> receipt.append(item.print()));

        receipt.append("\n");
        receipt.append("Total late change: ").append(getTotalCharge()).append(" EUR");

        return receipt.toString();
    }

    public static class Item implements PrintableReceipt {
        private String movieName;
        private MovieType movieType;
        private int extraDays;
        private BigDecimal extraPrice;

        String getMovieName() {
            return movieName;
        }

        void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        MovieType getMovieType() {
            return movieType;
        }

        void setMovieType(MovieType movieType) {
            this.movieType = movieType;
        }

        int getExtraDays() {
            return extraDays;
        }

        void setExtraDays(int extraDays) {
            this.extraDays = extraDays;
        }

        BigDecimal getExtraPrice() {
            return extraPrice;
        }

        void setExtraPrice(BigDecimal extraPrice) {
            this.extraPrice = extraPrice;
        }

        @Override
        public String print() {
            return getMovieName()
                    .concat(" (")
                    .concat(getMovieType().getTextualRepresentation())
                    .concat(") ")
                    .concat(Integer.toString(getExtraDays()))
                    .concat(" extra days ")
                    .concat(getExtraPrice().toString())
                    .concat(" EUR\n");
        }
    }
}
