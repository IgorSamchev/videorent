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
import java.time.LocalDate;
import java.util.*;

/**
 * Database Factory.
 * <p>
 */
public class DatabaseFactory {
    public static List<Movie> movieList = new ArrayList<>();
    public static List<Customer> customerList = new ArrayList<>();
    public static List<RentOrder> orderList = new ArrayList<>();

    private static int movieMaxID = 0;
    private static int customerMaxID = 0;
    private static int orderMaxID = 0;

    public static List<Movie> getMovieList() {
        return movieList;
    }

    public static List<Customer> getCustomerList(){
        return customerList;
    }

    public static List<RentOrder> getOrderList() {
        return orderList;
    }

    public static Movie findMovieById(int id) {
        return movieList.stream().filter(movie -> movie.getId() == id).findFirst().get();
    }

    public static Customer findCustomerById(int id) {
        return customerList.stream().filter(customer -> customer.getId() == id).findFirst().get();
    }

    public static RentOrder findOrderById(int id) {
        return orderList.stream().filter(rentOrder -> rentOrder.getId() == id).findFirst().get();
    }

    public static Database from(String filePath) {

        return new Database() {
            @Override
            public DBTableRepository<Movie> getMovieTable() {


                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray array = (JSONArray) jsonObject.get("movie");

                    for (Object json : array) {
                        JSONObject jMovie = (JSONObject) json;
                        Movie movie = new Movie();
                        int movieID = Integer.parseInt(String.valueOf(jMovie.get("id")));
                        movie.setId(movieID);
                        if (movieID > movieMaxID) movieMaxID = movieID;
                        movie.setName(jMovie.get("name").toString());
                        movie.setStockCount(Integer.parseInt(String.valueOf(jMovie.get("stockCount"))));

                        if (String.valueOf(jMovie.get("type")).equals("1")) movie.setType(MovieType.NEW);
                        if (String.valueOf(jMovie.get("type")).equals("2")) movie.setType(MovieType.REGULAR);
                        if (String.valueOf(jMovie.get("type")).equals("3")) movie.setType(MovieType.OLD);

                        if (Integer.parseInt(String.valueOf(jMovie.get("id"))) > movieMaxID) {
                            movieMaxID = Integer.parseInt(String.valueOf(jMovie.get("id")));
                        }
                        boolean contains = false;
                        if (!movieList.isEmpty()){
                            for (Movie m : movieList){
                                if (m.getName().equals(movie.getName())) contains = true;
                            }
                        }
                        if (!contains) movieList.add(movie);

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
                        return ++movieMaxID;
                    }
                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {


                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray array = (JSONArray) jsonObject.get("customer");

                    for (Object json : array) {
                        JSONObject jCustomer = (JSONObject) json;
                        Customer customer = new Customer();
                        int customerID = Integer.parseInt(String.valueOf(jCustomer.get("id")));
                        customer.setId(customerID);
                        if (customerID > customerMaxID) customerMaxID = customerID;
                        customer.setName(jCustomer.get("name").toString());
                        customer.setPoints(Integer.parseInt(String.valueOf(jCustomer.get("points"))));

                        if (Integer.parseInt(String.valueOf(jCustomer.get("id"))) > customerMaxID) {
                            customerMaxID = Integer.parseInt(String.valueOf(jCustomer.get("id")));
                        }
                        boolean contains = false;
                        if (!customerList.isEmpty()){
                            for (Customer c : customerList){
                                if (c.getName().equals(customer.getName())) contains = true;
                            }
                        }
                        if (!contains) customerList.add(customer);

                    }

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
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
                        return ++customerMaxID;
                    }
                };
            }
        //TODO fix me
            @Override
            public DBTableRepository<RentOrder> getOrderTable() {


                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray array = (JSONArray) jsonObject.get("order");

                    for (Object json : array) {
                        JSONObject jRentOrder = (JSONObject) json;
                        RentOrder order = new RentOrder();
                        int orderID = Integer.parseInt(String.valueOf(jRentOrder.get("id")));
                        order.setId(orderID);
                        if (orderID > orderMaxID) orderMaxID = orderID;
                        getCustomerTable();
                        order.setCustomer(getCustomerList().stream().filter(customer -> customer.getId()
                                == (Integer.parseInt(String.valueOf(jRentOrder.get("customer"))))).findFirst().get());
                        LocalDate date = LocalDate.parse(String.valueOf(jRentOrder.get("orderDate")));
                        order.setOrderDate(date);

                        List<RentOrder.Item> orderItems = new ArrayList<>();
                        JSONArray items = (JSONArray) jRentOrder.get("items");
                        for (Object object : items) {
                            JSONObject jItem = (JSONObject) object;
                            RentOrder.Item item = new RentOrder.Item();
                            item.setMovie(getMovieList().stream().filter(movie -> movie.getId() == (Integer.parseInt(String.valueOf(jItem.get("movie"))))).findFirst().get());

                            int type = Integer.parseInt(String.valueOf(jItem.get("type")));

                            if (type == 1) item.setMovieType(MovieType.NEW);
                            else if (type == 2) item.setMovieType(MovieType.REGULAR);
                            else if (type == 3) item.setMovieType(MovieType.OLD);

                            item.setPaidByBonus((Boolean) jItem.get("paidByBonus"));
                            item.setDays(Integer.parseInt(String.valueOf(jItem.get("days"))));

                            orderItems.add(item);
                        }
                        order.setItems(orderItems);

                        boolean contains = false;
                        if (!orderList.isEmpty()){
                            for (RentOrder rentOrder : orderList){
                                if (rentOrder.getId() == order.getId()) contains = true;
                            }
                        }
                        if (!contains) orderList.add(order);
                    }

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
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
                        return ++orderMaxID;
                    }
                };
            }
        };
    }
}
