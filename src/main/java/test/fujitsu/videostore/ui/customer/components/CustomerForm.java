package test.fujitsu.videostore.ui.customer.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import test.fujitsu.videostore.backend.database.DatabaseFactory;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.customer.CustomerListLogic;

/**
 * Customer edit/creation form
 */
public class CustomerForm extends Div {

    private TextField name;
    private Button save;
    private Button delete;

    private CustomerListLogic viewLogic;
    private Binder<Customer> binder;
    private Customer currentCustomer;

    public CustomerForm(CustomerListLogic customerListLogic) {
        setId("edit-form");

        VerticalLayout content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = customerListLogic;

        name = new TextField("Customer name");
        name.setId("customer-name");
        name.setWidth("100%");
        name.setRequired(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(name);

        TextField points = new TextField("Bonus points");
        points.setId("bonus-points");
        points.setWidth("100%");
        points.setRequired(true);
        points.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(points);

        binder = new Binder<>(Customer.class);
        binder.forField(name)
                .withValidator(name -> name.length() > 0, "Invalid name")
                .bind("name");
        binder.forField(points)
                .withConverter(new StringToIntegerConverter("Invalid bonus points format"))
                .bind("points");

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
        });

        save = new Button("Save");
        save.setId("save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentCustomer != null && customerNotPresent()) {
                binder.writeBeanIfValid(currentCustomer);
                viewLogic.saveCustomer(currentCustomer);
            } else
                showErrorNotification();
        });

        Button cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.setId("cancel");
        cancel.addClickListener(event -> viewLogic.cancelCustomer());
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelCustomer())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.setId("delete");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentCustomer != null) {
                viewLogic.deleteCustomer(currentCustomer);
            }
        });

        content.add(save, delete, cancel);
    }

    private boolean customerNotPresent() {
        for (Customer customer : DatabaseFactory.getCustomerList()) {
            if (customer.getName().equals(name.getValue())) return false;
        }
        return true;
    }

    private void showErrorNotification() {
        Notification notification = new Notification();
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setText("Current user already exists");
        notification.setOpened(true);
    }


    public void editCustomer(Customer customer) {
        if (customer == null) {
            customer = new Customer();
        }
        currentCustomer = customer;
        binder.readBean(customer);
        if (currentCustomer.isNewObject()) delete.setEnabled(false);
    }

}

