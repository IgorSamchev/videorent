package test.fujitsu.videostore.ui.database;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.File;

@Route("DatabaseSelection")
@PageTitle("Database Selection")
@HtmlImport("css/shared-styles.html")
public class DatabaseSelectionView extends FlexLayout {

    private TextField databasePath;
    private Button selectDatabaseButton;

    public DatabaseSelectionView() {
        setSizeFull();
        setClassName("database-selection-screen");

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(buildLoginForm());

        add(centeringLayout);
    }

    private Component buildLoginForm() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("310px");

        databasePath = new TextField("Enter database file path");
        databasePath.setId("database-path");
        databasePath.setRequired(true);

        verticalLayout.add(databasePath);

        HorizontalLayout buttons = new HorizontalLayout();
        verticalLayout.add(buttons);

        selectDatabaseButton = new Button("Select database");
        selectDatabaseButton.setId("database-select");
        selectDatabaseButton.addClickListener(event -> selectDatabase());
        selectDatabaseButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        buttons.add(selectDatabaseButton);

        return verticalLayout;
    }

    private static void showErrorNotification() {
        Notification notification = new Notification();
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setText("Wrong DB path");
        notification.setOpened(true);
    }

    private void selectDatabase() {
        selectDatabaseButton.setEnabled(true);
        File file = new File(databasePath.getValue());
        if (databasePath.getValue().contains(".json") && file.length() > 0) {
            CurrentDatabase.set(databasePath.getValue());
            if (getUI().isPresent()) getUI().get().navigate("");
        } else {
            showErrorNotification();
        }

    }
}
