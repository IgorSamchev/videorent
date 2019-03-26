package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Rent order receipt printer
 */
public class PrintableOrderReceipt implements PrintableReceipt {

    private String orderId;
    private LocalDate orderDate;
    private String customerName;
    private List<Item> orderItems;
    private BigDecimal totalPrice;
    private int remainingBonusPoints;

    private String getOrderId() {
        return orderId;
    }

    void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private LocalDate getOrderDate() {
        return orderDate;
    }

    void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    private String getCustomerName() {
        return customerName;
    }

    void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    List<Item> getOrderItems() {
        return orderItems;
    }

    void setOrderItems(List<Item> orderItems) {
        this.orderItems = orderItems;
    }

    private BigDecimal getTotalPrice() {
        return totalPrice;
    }

    void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    private int getRemainingBonusPoints() {
        return remainingBonusPoints;
    }

    void setRemainingBonusPoints(int remainingBonusPoints) {
        this.remainingBonusPoints = remainingBonusPoints;
    }


    public String print() {
        StringBuilder receipt = new StringBuilder()
                .append("ID: ").append(getOrderId())
                .append("\n");
        if (getOrderDate() != null) {
            receipt.append(getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-YY")));
        } else receipt.append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-YY")));

        receipt
                .append("\n").append("Customer: ").append(getCustomerName())
                .append("\n");


        boolean paidAnyUsingBonus = false;

        for (PrintableOrderReceipt.Item orderItem : getOrderItems()) {
            receipt.append(orderItem.print());

            if (orderItem.getPaidBonus() != null) {
                paidAnyUsingBonus = true;
            }
        }

        receipt.append("\n");
        receipt.append("Total price: ").append(getTotalPrice()).append(" EUR");

        if (paidAnyUsingBonus) {
            if (getRemainingBonusPoints() < 0) {
                receipt.append(" \nNeed more bonus points!");

            } else {
                receipt.append("\nRemaining Bonus points: ").append(getRemainingBonusPoints());

            }
        }

        return receipt.toString();
    }

    public static class Item {

        private String movieName;
        private MovieType movieType;
        private int days;
        private BigDecimal paidMoney = null;
        private Integer paidBonus = null;

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

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        BigDecimal getPaidMoney() {
            return paidMoney;
        }

        void setPaidMoney(BigDecimal paidMoney) {
            this.paidMoney = paidMoney;
        }

        Integer getPaidBonus() {
            return paidBonus;
        }

        void setPaidBonus(Integer paidBonus) {
            this.paidBonus = paidBonus;
        }

        String print() {
            StringBuilder receipt = new StringBuilder();
            receipt.append(getMovieName())
                    .append(" (")
                    .append(getMovieType().getTextualRepresentation())
                    .append(") ")
                    .append(getDays());
            if (getDays() == 1) {
                receipt.append(" day ");
            } else {
                receipt.append(" days ");
            }

            if (getPaidBonus() != null) {
                receipt.append("(Paid with ").append(getPaidBonus()).append(" Bonus points) ");
            } else {
                receipt.append(getPaidMoney()).append(" EUR");
            }

            receipt.append("\n");

            return receipt.toString();
        }
    }
}
