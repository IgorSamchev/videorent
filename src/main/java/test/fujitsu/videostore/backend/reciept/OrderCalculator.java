package test.fujitsu.videostore.backend.reciept;

import net.bytebuddy.asm.Advice;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OrderCalculator {

    static BigDecimal calculateTotalPrice(PrintableOrderReceipt printableOrderReceipt, RentOrder order){
        double totalPrice = 0;
        List<PrintableOrderReceipt.Item> list = printableOrderReceipt.getOrderItems();
        for (PrintableOrderReceipt.Item item : list){
            if (item.getMovieType() == MovieType.NEW) totalPrice += 3 * item.getDays();
            else if (item.getMovieType() == MovieType.REGULAR) totalPrice += 2 * item.getDays();
            else if (item.getMovieType() == MovieType.OLD) totalPrice += item.getDays();
        }

        List<RentOrder.Item> orderList = order.getItems();
        for (RentOrder.Item item : orderList){
            if (item.isPaidByBonus()){
                if (item.getMovieType() == MovieType.NEW) totalPrice -= 3 * item.getDays();
                else if (item.getMovieType() == MovieType.REGULAR) totalPrice -= 2 * item.getDays();
                else if (item.getMovieType() == MovieType.OLD) totalPrice -= item.getDays();
            }
        }
        return BigDecimal.valueOf(totalPrice);
    }


    public static double calculateOrder(PrintableOrderReceipt.Item item) {
        double orderPrice = 0;
        switch (item.getMovieType()){
            case NEW: orderPrice = 3 * item.getDays();
            case REGULAR: orderPrice =  2 * item.getDays();
            case OLD: orderPrice = item.getDays();
        }
        return orderPrice;
    }


    public static int calculateExtraDays(RentOrder.Item rentedItem, PrintableReturnReceipt receipt, ReturnOrder order) {
//        Period interval = Period.between(order.getRentOrder().getOrderDate().plusDays(2), LocalDate.now().plusDays(1));
        int totalDays = (int) ChronoUnit.DAYS.between(order.getRentOrder().getOrderDate(), LocalDate.now());
        return totalDays - rentedItem.getDays();
    }
}
