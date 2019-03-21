package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class OrderCalculator {

    static BigDecimal calculateTotalPrice(PrintableOrderReceipt printableOrderReceipt, RentOrder order) {
        double totalPrice = 0;
        List<PrintableOrderReceipt.Item> list = printableOrderReceipt.getOrderItems();
        for (PrintableOrderReceipt.Item item : list) {
            if (item.getMovieType() == MovieType.NEW) totalPrice += 3 * item.getDays();
            else if (item.getMovieType() == MovieType.REGULAR) totalPrice += 2 * item.getDays();
            else if (item.getMovieType() == MovieType.OLD) totalPrice += item.getDays();
        }

        List<RentOrder.Item> orderList = order.getItems();
        for (RentOrder.Item item : orderList) {
            if (item.isPaidByBonus()) {
                if (item.getMovieType() == MovieType.NEW) totalPrice -= 3 * item.getDays();
                else if (item.getMovieType() == MovieType.REGULAR) totalPrice -= 2 * item.getDays();
                else if (item.getMovieType() == MovieType.OLD) totalPrice -= item.getDays();
            }
        }
        return BigDecimal.valueOf(totalPrice);
    }


    public static int calculateOrder(PrintableOrderReceipt.Item item) {
        int orderPrice = 0;
        switch (item.getMovieType()) {
            case NEW:
                orderPrice = 3 * item.getDays();
                break;
            case REGULAR:
                orderPrice = 2 * item.getDays();
                break;
            case OLD:
                orderPrice = 1 * item.getDays();
                break;
        }
        return orderPrice;
    }


    public static int calculateExtraDays(RentOrder.Item rentedItem, ReturnOrder order) {
        int totalDays = (int) ChronoUnit.DAYS.between(order.getRentOrder().getOrderDate(), LocalDate.now());
        return totalDays - rentedItem.getDays();
    }

    public static int calculateExtraDaysPayForEachMovie(RentOrder.Item rentedItem, ReturnOrder order) {
        int extraDays = OrderCalculator.calculateExtraDays(rentedItem, order);
        if (rentedItem.getMovieType() == MovieType.NEW) return extraDays * 3;
        if (rentedItem.getMovieType() == MovieType.REGULAR) return extraDays * 2;
        return  extraDays;
    }

    public static int getTotalCharge(ReturnOrder order) {
        int totalCharge = 0;
        if (order.getItems() != null) {

            for (RentOrder.Item rentedItem : order.getItems()) {
                int extraPay = calculateExtraDaysPayForEachMovie(rentedItem, order);
                totalCharge += extraPay;
            }
        }
        return totalCharge;
    }
}
