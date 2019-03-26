package test.fujitsu.videostore.ui.inventory;

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
import com.vaadin.flow.router.RouteAlias;
import test.fujitsu.videostore.backend.database.DatabaseFactory;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.inventory.components.MovieForm;
import test.fujitsu.videostore.ui.inventory.components.MovieGrid;

import java.util.ArrayList;
import java.util.List;

@Route(value = VideoStoreInventory.VIEW_NAME, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class VideoStoreInventory extends HorizontalLayout
        implements HasUrlParameter<String> {

    public static boolean isNewMovie = false;
    public static final String VIEW_NAME = "Inventory";
    private MovieGrid grid;
    private MovieForm form;
    private TextField filter;

    private ListDataProvider<Movie> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private VideoStoreInventoryLogic viewLogic = new VideoStoreInventoryLogic(this);
    private Button newMovie;

    public VideoStoreInventory() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new MovieGrid();
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));
        grid.setDataProvider(dataProvider);

        form = new MovieForm(viewLogic);



        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setId("filter");
        filter.setPlaceholder("Filter by name");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            if (filter != null){
                List<Movie> movieList = DatabaseFactory.getMovieList();
                List<Movie> temp = new ArrayList<>();
                for (Movie m : movieList){
                    if (m.getName().toLowerCase().contains(filter.getValue().toLowerCase())){
                        temp.add(m);
                    }
                }
                setMovies(temp);
            }
        });


        newMovie = new Button("New Movie");
        newMovie.setId("new-item");
        newMovie.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newMovie.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newMovie.addClickListener(click -> {
            isNewMovie = true;
            viewLogic.newMovie();
        });


        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newMovie);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    void setNewMovieEnabled() {
        newMovie.setEnabled(true);
    }

    void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    void selectRow(Movie row) {
        grid.getSelectionModel().select(row);
    }

    void addMovie(Movie movie) {
        dataProvider.getItems().add(movie);
        grid.getDataProvider().refreshAll();
    }

    void updateMovie(Movie movie) {
        dataProvider.refreshItem(movie);
    }

    void removeMovie(Movie movie) {
        dataProvider.getItems().remove(movie);
        dataProvider.refreshAll();
    }

    void editMovie(Movie movie) {
        showForm(movie != null);
        form.editMovie(movie);
    }

    void showForm(boolean show) {
        form.setVisible(show);
    }

    void setMovies(List<Movie> movies) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(movies);
        dataProvider.refreshAll();
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }
}
