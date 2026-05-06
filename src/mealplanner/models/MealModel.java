package mealplanner.models;

import java.util.List;

public class MealModel {
    String name;
    List<String> ingredients;
    int id;
    public MealModel(){

    }
    public MealModel(String name,List<String> ingredients) {
        this.name=name;
        this.ingredients=ingredients;
        id = 0;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "{"+ "id = " + id +"name='" + name + '\'' +
               ", ingredients=" + ingredients +
               '}';
    }
}
