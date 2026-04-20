package mealplanner.models;

import java.util.List;

public class MealModel {
    String name;
    List<String> ingredients;

    public MealModel(String name,List<String> ingredients) {
        this.name=name;
        this.ingredients=ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' +
               ", ingredients=" + ingredients +
               '}';
    }
}
