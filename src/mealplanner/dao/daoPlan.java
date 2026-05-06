package mealplanner.dao;

import mealplanner.models.Category;
import mealplanner.models.DayPlan;
import mealplanner.models.MealModel;
import mealplanner.models.Week;

import java.sql.*;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.Map;

public class daoPlan {
    public Connection con;

    public daoPlan(Connection con) {
        this.con = con;
    }

    public void savePlan(Map<Week, DayPlan> map) {
        deletePlan();

        String sql = "INSERT INTO PLAN (MEAL_OPTION, MEAL_CATEGORY, MEAL_ID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (Map.Entry<Week, DayPlan> entry : map.entrySet()) {
                Week day = entry.getKey();
                DayPlan dp = entry.getValue();

                for (Category category : Category.values()) {
                    MealModel meal = dp.getMeal(category);

                    ps.setString(1, day.name().toLowerCase());
                    ps.setString(2, category.name().toLowerCase());
                    ps.setInt(3, meal.getId());

                    ps.addBatch();
                }
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Week, DayPlan> getPlan() {
        String sql = """
                                SELECT 
                                    p.meal_option,
                                    p.meal_category,
                                    m.meal_id,
                                    m.meal,
                                    i.ingredient
                                FROM plan p
                                JOIN meals m ON p.meal_id = m.meal_id
                                LEFT JOIN ingredients i ON m.meal_id = i.meal_id;
                """;

        Map<Week, DayPlan> result = new LinkedHashMap<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();


            Map<Integer, MealModel> mealCache = new LinkedHashMap<>();

            while (rs.next()) {

                String dayStr = rs.getString("meal_option").toUpperCase();
                String categoryStr = rs.getString("meal_category").toUpperCase();
                int mealId = rs.getInt("meal_id");
                String mealName = rs.getString("meal");
                String ingredient = rs.getString("ingredient");

                Week day = Week.valueOf(dayStr);
                Category category = Category.valueOf(categoryStr);

                DayPlan dayPlan = result.computeIfAbsent(day, d -> new DayPlan());

                MealModel meal = dayPlan.getMeal(category);

                if (meal == null) {
                    meal = new MealModel();
                    meal.setName(mealName);
                    meal.setIngredients(new ArrayList<>());

                    dayPlan.setMeal(category, meal);
                }

                if (ingredient != null) {
                    if (!meal.getIngredients().contains(ingredient)) {
                        meal.getIngredients().add(ingredient);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    private void deletePlan() {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM plan")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasPlan() {
        String sql = "SELECT 1 FROM plan LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
