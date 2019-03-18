package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple receipt creation service
 * <p>
 * Note! All calculations should be in another place. Here we just setting already calculated data. Feel free to refactor.
 */
public class OrderToReceiptService {

    /**
     * Converts rent order to printable receipt
     *
     * @param order rent object
     * @return Printable receipt object
     */
    public PrintableOrderReceipt convertRentOrderToReceipt(RentOrder order) {
        PrintableOrderReceipt printableOrderReceipt = new PrintableOrderReceipt();
        int bonusRemaining = order.getCustomer().getPoints();

        printableOrderReceipt.setOrderId(order.isNewObject() ? "new" : Integer.toString(order.getId()));
        printableOrderReceipt.setOrderDate(order.getOrderDate());
        printableOrderReceipt.setCustomerName(order.getCustomer().getName());

        List<PrintableOrderReceipt.Item> itemList = new ArrayList<>();
        printableOrderReceipt.setOrderItems(itemList);

        for (RentOrder.Item orderItem : order.getItems()) {
            PrintableOrderReceipt.Item item = new PrintableOrderReceipt.Item();
            item.setDays(orderItem.getDays());
            item.setMovieName(orderItem.getMovie().getName());
            item.setMovieType(orderItem.getMovieType());

            if (orderItem.isPaidByBonus()) {
                double bonusPay = OrderCalculator.calculateOrder(item);
                item.setPaidBonus((int) bonusPay);
                bonusRemaining = (int) (bonusRemaining - bonusPay);
            } else {
                double orderPrice = OrderCalculator.calculateOrder(item);
                item.setPaidMoney(BigDecimal.valueOf(orderPrice));
            }

            itemList.add(item);
        }

        BigDecimal totalPrice = OrderCalculator.calculateTotalPrice(printableOrderReceipt, order);
        printableOrderReceipt.setTotalPrice(totalPrice);

        printableOrderReceipt.setRemainingBonusPoints(bonusRemaining);

        return printableOrderReceipt;
    }

    /**
     * Converts return order to printable receipt
     *
     * @param order return object
     * @return Printable receipt object
     */
    public PrintableReturnReceipt convertRentOrderToReceipt(ReturnOrder order) {
        PrintableReturnReceipt receipt = new PrintableReturnReceipt();

        receipt.setOrderId(Integer.toString(order.getRentOrder().getId()));
        receipt.setCustomerName(order.getRentOrder().getCustomer().getName());
        receipt.setRentDate(order.getRentOrder().getOrderDate());
        receipt.setReturnDate(order.getReturnDate());

        List<PrintableReturnReceipt.Item> returnedItems = new ArrayList<>();
        if (order.getItems() != null) {
            for (RentOrder.Item rentedItem : order.getItems()) {
                PrintableReturnReceipt.Item item = new PrintableReturnReceipt.Item();
                item.setMovieName(rentedItem.getMovie().getName());
                item.setMovieType(rentedItem.getMovieType());
                // TODO: Set calculated data how much later rented movie was returned
                item.setExtraDays(0);
                // TODO: Set calculated data how much it will cost extra days
                item.setExtraPrice(BigDecimal.ZERO);

                returnedItems.add(item);
            }
        }
        receipt.setReturnedItems(returnedItems);

        // TODO: Set calculated total extra charge for all movies
        receipt.setTotalCharge(BigDecimal.ZERO);

        return receipt;
    }

}
