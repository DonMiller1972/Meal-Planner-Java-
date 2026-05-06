package mealplanner.models;

import java.util.EnumMap;
import java.util.Map;

public class DayPlan {
    private Map<Category, MealModel> meals =  new EnumMap<>(Category.class);
    private MealModel mealModel;

    public void setBreakfast(MealModel meal) {
        meals.put(Category.BREAKFAST, meal);
    }

    public MealModel getBreakfast() {
        return meals.get(Category.BREAKFAST);
    }

    public void setLunch(MealModel meal) {
        meals.put(Category.LUNCH, meal);
    }

    public MealModel getLunch() {
        return meals.get(Category.LUNCH);
    }

    public void setDinner(MealModel meal) {
        meals.put(Category.DINNER, meal);
    }

    public MealModel getDinner() {
        return meals.get(Category.DINNER);
    }

    public void setMeal(Category category, MealModel meal) {
        meals.put(category, meal);
    }

    public MealModel getMeal(Category category) {
        return meals.get(category);
    }
}
