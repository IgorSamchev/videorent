package test.fujitsu.videostore.ui.order;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.database.DatabaseFactory;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.order.components.OrderForm;
import test.fujitsu.videostore.ui.order.components.OrderGrid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route(value = OrderList.VIEW_NAME, layout = MainLayout.class)
public class OrderList extends HorizontalLayout implements HasUrlParameter<String> {

    static final String VIEW_NAME = "OrderList";
    private OrderGrid grid;
    private OrderForm form;
    private TextField filter;

    private ListDataProvider<RentOrder> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private OrderListLogic viewLogic = new OrderListLogic(this);
    private Button newOrder;

    public OrderList() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new OrderGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new OrderForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);
        setFlexGrow(0, barAndGridLayout);
        setFlexGrow(1, form);

        viewLogic.init();
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setId("filter");
        filter.setPlaceholder("Filter by ID or Customer name");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            if (filter != null) {
                List<RentOrder> rentOrderList = DatabaseFactory.getOrderList();
                List<RentOrder> temp = new ArrayList<>();
                for (RentOrder r : rentOrderList) {
                    if (filter.getValue().equals(String.valueOf(r.getId()))
                            || r.getCustomer().getName().toLowerCase().contains(filter.getValue().toLowerCase()))
                        temp.add(r);
                }
                setOrders(temp);
            }
        });

        newOrder = new Button("New Order");
        newOrder.setId("new-item");
        newOrder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newOrder.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newOrder.addClickListener(click -> viewLogic.newOrder());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newOrder);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    void setNewOrderEnabled() {
        newOrder.setEnabled(true);
    }

    void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    void selectRow(RentOrder row) {
        grid.getSelectionModel().select(row);
    }

    void addOrder(RentOrder order) {
        order.setOrderDate(LocalDate.now());
        dataProvider.getItems().add(order);
        grid.getDataProvider().refreshAll();
    }

    void updateOrder(RentOrder order) {
        dataProvider.refreshItem(order);
    }

    void removeOrder(RentOrder order) {
        dataProvider.getItems().remove(order);
        grid.getDataProvider().refreshAll();
    }

    void editOrder(RentOrder order) {
        showForm(order != null);
        form.editOrder(order);
    }

    void showForm(boolean show) {
        form.setVisible(show);
    }

    void setOrders(List<RentOrder> orders) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(orders);
        grid.getDataProvider().refreshAll();
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }
}
