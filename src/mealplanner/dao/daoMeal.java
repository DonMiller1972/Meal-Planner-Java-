package mealplanner.dao;

import mealplanner.models.MealModel;

import java.sql.*;
import java.util.*;

public class daoMeal {
    public Connection con;

    public daoMeal(Connection con){
        this.con = con;
    }

    public boolean addMeal(String category, MealModel meal) throws SQLException {
        String sqlAddMeal = "INSERT INTO meals(category, meal) VALUES ( ?, ?) RETURNING meal_id";
        con.setAutoCommit(false);
        boolean truth = false;
            try (PreparedStatement ps = con.prepareStatement(sqlAddMeal)) {
                ps.setString(1, category);
                ps.setString(2, meal.getName());

                ResultSet rs = ps.executeQuery();
                rs.next();

                int mealId = rs.getInt("meal_id");

                truth = addIngredients(mealId, meal.getIngredients());

                if(truth!=true){
                    con.rollback();


                }else{
                    con.commit();
                    System.out.println("The meal has been added!\n");
                }

            } catch (SQLException e){
              try {
                con.rollback(); // ❌ откат при любой ошибке
              } catch (SQLException ex) {
                ex.printStackTrace();
              }
              e.printStackTrace();
              truth = false;

            } finally {
                try {
                    con.setAutoCommit(true); // вернуть как было
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return truth;
    }

    public boolean addIngredients(int mealId, List<String> mealIngredients) throws SQLException {
        String sqlAddIngredients = "INSERT INTO ingredients(ingredient, meal_id) VALUES ( ?, ?)";


        try (PreparedStatement ps = con.prepareStatement(sqlAddIngredients)) {

            for(String ingredient: mealIngredients) {
                ps.setString(1, ingredient);
                ps.setInt(2, mealId);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        }


    }


    public boolean isMealEmpty(){
        String sql = "SELECT 1 FROM meals LIMIT 1";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println(rs.next());
            if (!rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public Map<String, List<MealModel>> getAllMeals(){
        Map<String, List<MealModel>> map = new LinkedHashMap<>();
        String sql = """
        SELECT m.category, m.meal, i.ingredient
        FROM meals m
        LEFT JOIN ingredients i ON m.meal_id = i.meal_id
        ORDER BY m.category, m.meal
    """;

       // Map<String, List<MealModel>> result = new LinkedHashMap<>();

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // временная мапа для склейки одинаковых meals
            Map<String, MealModel> mealMap = new LinkedHashMap<>();

            while (rs.next()) {
                String category = rs.getString("category");
                String mealName = rs.getString("meal");
                String ingredient = rs.getString("ingredient");

                // ключ для уникального блюда
                String key = category ;

                MealModel meal = mealMap.get(key);

                if (meal == null) {
                    meal = new MealModel(mealName, new ArrayList<>());
                    mealMap.put(key, meal);

                    map.computeIfAbsent(category, k -> new ArrayList<>())
                            .add(meal);
                }

                if (ingredient != null) {
                    meal.getIngredients().add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }


}
