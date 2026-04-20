package mealplanner.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnect {
    static final String DB_URL = "jdbc:postgresql:meals_db";
    String USER = "postgres";
    String PASS = "1111";
    String url;


    //private final String url;

    private Connection cnn;

    public DbConnect(String fileName) {
        this.url = DB_URL;

    }

    public void init() throws SQLException {
        this.cnn = DriverManager.getConnection(url, USER, PASS);
        createTable();

    }

    public void closeConnection() {
        if (cnn != null) {
            try {
                cnn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createTable() {
        String sqlMeals =
                "CREATE TABLE IF NOT EXISTS meals (" +
                "meal_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "category VARCHAR(50) NOT NULL, " +
                "meal VARCHAR(100) NOT NULL" +
                ");";

        String sqlIngredients =
                "CREATE TABLE IF NOT EXISTS ingredients (" +
                "ingredient_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "ingredient VARCHAR(100) NOT NULL, " +
                "meal_id INT NOT NULL, " +
                "CONSTRAINT fk_meal " +
                "FOREIGN KEY (meal_id) " +
                "REFERENCES meals(meal_id) " +
                "ON DELETE CASCADE" +
                ");";

        try (Statement stmt = cnn.createStatement()) {

            stmt.execute(sqlMeals);
            stmt.execute(sqlIngredients);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return cnn;
    }
}
