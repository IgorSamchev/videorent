package test.fujitsu.videostore.backend.database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database Factory.
 * <p>
 * TODO: Should be re-implemented with your file database. Current implementation is just demo for UI testing.
 */
public class DatabaseFactory {


    /**
     * Creates database "connection"/opens database from path.
     * <p>
     * TODO: Implement database parsing, fetching, creation, modification, removing from JSON or YAML file database.
     * Two example files, /db-examples/database.json and /db-examples/database.yaml.
     * Hint: MovieType.databaseId == type field in database files.
     * <p>
     * TODO: Current way of creating next ID is incorrect, make better implementation.
     *
     * @param filePath file path to database
     * @return database proxy for different tables
     */
    public static Database from(String filePath) {

        return new Database() {
            @Override
            public DBTableRepository<Movie> getMovieTable() {

                final List<Movie> movieList = new ArrayList<>();

                JSONParser parser = new JSONParser();
                /*
                  TODO: Replace fileName path.
                 */
                try {
                    Object obj = parser.parse(new FileReader("db-examples/database.json"));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray array = (JSONArray) jsonObject.get("movie");
                    for (Object json : array) {

                        JSONObject jMovie = (JSONObject) json;
                        Movie movie = new Movie();
                        movie.setId(Integer.parseInt(String.valueOf(jMovie.get("id"))));
                        movie.setName(jMovie.get("name").toString());
                        movie.setStockCount(Integer.parseInt(String.valueOf(jMovie.get("stockCount"))));
                        movieList.add(movie);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

                return new DBTableRepository<Movie>() {

                    @Override
                    public List<Movie> getAll() {
                        return movieList;
                    }

                    @Override
                    public Movie findById(int id) {
                        return movieList.stream().filter(movie -> movie.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Movie object) {
                        return movieList.remove(object);
                    }

                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            movieList.add(object);
                            return object;
                        }

                        Movie movie = findById(object.getId());

                        movie.setName(object.getName());
                        movie.setStockCount(object.getStockCount());
                        movie.setType(object.getType());

                        return movie;
                    }

                    @Override
                    public int generateNextId() {
                        return movieList.size() + 1;
                    }
                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {
                final List<Customer> customerList = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    Customer customer = new Customer();
                    customer.setId(i);
                    customer.setName("Customer " + i);
                    customer.setPoints(i * 10);

                    customerList.add(customer);
                }

                return new DBTableRepository<Customer>() {
                    @Override
                    public List<Customer> getAll() {
                        return customerList;
                    }

                    @Override
                    public Customer findById(int id) {
                        return getAll().stream().filter(customer -> customer.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Customer object) {
                        return customerList.remove(object);
                    }

                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            customerList.add(object);
                            return object;
                        }

                        Customer customer = findById(object.getId());

                        customer.setName(object.getName());
                        customer.setPoints(object.getPoints());

                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        return customerList.size() + 1;
                    }
                };
            }

            @Override
            public DBTableRepository<RentOrder> getOrderTable() {
                final List<RentOrder> orderList = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    RentOrder order = new RentOrder();
                    order.setId(i);
                    order.setCustomer(getCustomerTable().findById(i));

                    List<RentOrder.Item> orderItems = new ArrayList<>();

                    for (int a = 1; a <= 10; a++) {
                        RentOrder.Item item = new RentOrder.Item();
                        item.setMovie(getMovieTable().findById(a));
                        item.setMovieType(MovieType.REGULAR);
                        item.setPaidByBonus(a % 2 == 0);
                        item.setDays(11 - a);

                        orderItems.add(item);
                    }

                    order.setItems(orderItems);
                    orderList.add(order);
                }

                return new DBTableRepository<RentOrder>() {
                    @Override
                    public List<RentOrder> getAll() {
                        return orderList;
                    }

                    @Override
                    public RentOrder findById(int id) {
                        return getAll().stream().filter(order -> order.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(RentOrder object) {
                        return orderList.remove(object);
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            orderList.add(object);
                            return object;
                        }

                        RentOrder order = findById(object.getId());

                        order.setCustomer(object.getCustomer());
                        order.setOrderDate(order.getOrderDate());
                        order.setItems(object.getItems());

                        return order;
                    }

                    @Override
                    public int generateNextId() {
                        return orderList.size() + 1;
                    }
                };
            }
        };
    }
}
