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
                con.rollback();
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
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public boolean hasAnyMeals() {
        return isMealEmpty("SELECT 1 FROM meals LIMIT 1", null);
    }

    public boolean hasMealsByCategory(String category) {
        return isMealEmpty("SELECT 1 FROM meals WHERE category = ? LIMIT 1", category);
    }

    private boolean isMealEmpty(String sql, String category){

        try (PreparedStatement stmt = con.prepareStatement(sql)){
             if(category!=null){
                stmt.setString(1, category);
            }
             ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return false;
                }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
        }

    public Map<String, List<MealModel>> getMealsByCategory(String category) throws SQLException {
        String sql = """
                SELECT m.category, m.meal, i.ingredient
                FROM meals m
                LEFT JOIN ingredients i ON m.meal_id = i.meal_id
                WHERE m.category = ?
                ORDER BY m.meal_id;
                """;
        return getMealsInternal(sql, category);
    }

    public Map<String, List<MealModel>> getAllMeals() throws SQLException {
        String sql = """
                SELECT m.category, m.meal, i.ingredient
                FROM meals m
                LEFT JOIN ingredients i ON m.meal_id = i.meal_id
                ORDER BY m.meal_id;
                """;
        return getMealsInternal(sql, null);
    }

    private Map<String, List<MealModel>> getMealsInternal(String sql, String categoryMeal) throws SQLException {
        Map<String, List<MealModel>> map = new LinkedHashMap<>();

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            if (categoryMeal != null) {
                stmt.setString(1, categoryMeal);
            }
            ResultSet rs = stmt.executeQuery();

            Map<String, MealModel> mealMap = new LinkedHashMap<>();

            while (rs.next()) {
                String category = rs.getString("category");
                String mealName = rs.getString("meal");
                String ingredient = rs.getString("ingredient");

                List<MealModel> mealsInCategory = map.computeIfAbsent(category, k -> new ArrayList<>());


                MealModel mealModel = mealsInCategory.stream()
                        .filter(m -> m.getName().equals(mealName))
                        .findFirst()
                        .orElse(null);


                if (mealModel == null) {
                    mealModel = new MealModel(mealName, new ArrayList<>());
                    mealsInCategory.add(mealModel);
                }


                if (ingredient != null) {
                    mealModel.getIngredients().add(ingredient);
                }
            }

            return map;
        }
    }


}
