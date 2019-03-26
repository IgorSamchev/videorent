package test.fujitsu.videostore.ui.inventory.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import test.fujitsu.videostore.backend.database.DatabaseFactory;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.inventory.VideoStoreInventory;
import test.fujitsu.videostore.ui.inventory.VideoStoreInventoryLogic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Movie form
 */
public class MovieForm extends Div {

    private TextField name;
    private ComboBox<MovieType> type;
    private Button save;
    private Button delete;

    private VideoStoreInventoryLogic viewLogic;
    private Binder<Movie> binder;
    private Movie currentMovie;

    public MovieForm(VideoStoreInventoryLogic videoStoreInventoryLogic) {
        setId("edit-form");

        VerticalLayout content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = videoStoreInventoryLogic;

        name = new TextField("Movie name");
        name.setId("movie-name");
        name.setWidth("100%");
        name.setRequired(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(name);

        type = new ComboBox<>("Movie type");
        type.setId("movie-type");
        type.setWidth("100%");
        type.setRequired(true);
        type.setItems(MovieType.values());
        type.setItemLabelGenerator(MovieType::getTextualRepresentation);
        content.add(type);

        TextField stockCount = new TextField("In stock");
        stockCount.setId("stock-count");
        stockCount.setWidth("100%");
        stockCount.setRequired(true);
        stockCount.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        stockCount.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(stockCount);

        // Binding field to domain
        binder = new Binder<>(Movie.class);
        binder.forField(name)
                .bind("name");
        binder.forField(type)
                .bind("type");
        binder.forField(stockCount).withConverter(new StockCountConverter())
                .bind("stockCount");

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
            delete.setEnabled(!VideoStoreInventory.isNewMovie);


        });

        save = new Button("Save");
        save.setId("save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            VideoStoreInventory.isNewMovie = false;
            if (currentMovie != null) {
                if (name.getValue().length() > 1) {
                    if (type.getValue() == MovieType.NEW || type.getValue() == MovieType.REGULAR
                            || type.getValue() == MovieType.OLD) {
                        binder.writeBeanIfValid(currentMovie);
                        viewLogic.saveMovie(currentMovie);
                    } else {
                        Notification.show("Select Movie Type", 2000, Notification.Position.MIDDLE);
                    }
                } else {
                    Notification.show("Select Movie name", 2000, Notification.Position.MIDDLE);
                }
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> {
            VideoStoreInventory.isNewMovie = false;
            viewLogic.cancelMovie();
        });
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelMovie())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setId("delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {

            if (currentMovie != null) {
                viewLogic.deleteMovie(currentMovie);
            }
        });

        content.add(save, delete, cancel);
    }

    public void editMovie(Movie movie) {
        if (movie == null) {
            movie = new Movie();
        }
        currentMovie = movie;
        binder.readBean(movie);


        for (RentOrder order : DatabaseFactory.getOrderList()){
            for (RentOrder.Item item : order.getItems()){
                if (item.getMovie().getName().equals(movie.getName())) delete.setEnabled(false);

            }
        }
        delete.setVisible(true);


    }

    private static class StockCountConverter extends StringToIntegerConverter {

        StockCountConverter() {
            super(0, "Could not convert value to " + Integer.class.getName()
                    + ".");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            format.setDecimalSeparatorAlwaysShown(false);
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            return format;
        }
    }
}
