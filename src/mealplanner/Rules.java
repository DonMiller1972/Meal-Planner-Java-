package mealplanner;

import mealplanner.models.MealModel;
import mealplanner.dao.daoMeal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Rules {
    Scanner sc;
    daoMeal daoMeal;

    public Rules(Scanner sc, daoMeal daoMeal) {
        this.sc=sc;
        this.daoMeal=daoMeal;
    }
/*
    public readMenu(Scanner sc){

    }*/
    public void showCategory(){

    }

    public void showFood(Map<String, List<MealModel>> mealModels){

        if (mealModels == null || mealModels.isEmpty()) {
            System.out.println("No meals saved. Add a meal first.");
            return; // Выходим из метода, ничего не печатая
        }
        System.out.println();
        for (Map.Entry<String, List<MealModel>> entry : mealModels.entrySet()) {
            String category = entry.getKey();
            List<MealModel> mealList = entry.getValue();

            for (MealModel meal : mealList) {
                System.out.println("Category: " + category);
                System.out.println("Name: " + meal.getName());
                System.out.println("Ingredients:");

                // Печатаем каждый ингредиент с новой строки
                for (String ingredient : meal.getIngredients()) {
                    System.out.println(ingredient.strip());
                }
                System.out.println(); // Отступ между рецептами
            }
        }

    }
    public boolean checkCategory(String category){
        boolean check=false;
        if ((category.equals("breakfast")|| category.equals("lunch")||category.equals("dinner"))&&category.matches("^[a-z]+$")) {
            check=true;
        }else{
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            check=false;
        }
        return check;
    }

    public void mainMenu() throws SQLException {
        boolean  circle = true;
        Map<String, List<MealModel>> mealMap = new LinkedHashMap<>();
        while(circle) {
            System.out.println("What would you like to do (add, show, exit)?");
            String menu = sc.nextLine().trim().toLowerCase();
            switch (menu) {
                case "add" -> mealMap = addFood(mealMap);
                case "show" -> {
                    if (daoMeal.isMealEmpty()) {
                        System.out.println("Empty");
                    }else{
                        mealMap = daoMeal.getAllMeals();
                        showFood(mealMap);
                    }

                }
                case "exit" -> {
                    circle = false;
                    System.out.println("Bye!");
                }
            }

        }
    }

    public Map<String , List<MealModel>>  addFood(Map<String ,List<MealModel>> mapMeal) throws SQLException {
        //MealModel meal = new MealModel();
        Map<String ,List<MealModel>> map = new LinkedHashMap<>(mapMeal);

        String category ="";
        String name = "";
        List<String> ingredients;
        String parts ="";
        //boolean check;
        boolean circle = true;

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        while(circle) {
            category = sc.nextLine();
            if(checkCategory(category)){
                circle = false;
            }
            /*else{
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }*/
        }
        circle = true;
        System.out.println("Input the meal's name:");
        while(circle) {
            name = sc.nextLine();
            if(name.matches("^\\s*[a-z]+([ -][a-z]+)*$")) {
                circle = false;
            }else{
                System.out.println("Wrong format. Use letters only!");
            }
        }
        circle = true;
        System.out.println("Input the ingredients:");
        String regex = "^[a-z]+([ -][a-z]+)*(,\\s*[a-z]+([ -][a-z]+)*)*$";
        while(circle) {
            parts = sc.nextLine();
            if(parts.matches(regex)){
                circle = false;
            }else{
                System.out.println("Wrong format. Use letters only!");
            }
        }
        ingredients = Arrays.asList(parts.split(","));
        MealModel meal = new MealModel(name, ingredients);
        boolean add = daoMeal.addMeal(category, meal);
        System.out.println("Added meal: " + add);
        //meal.setName(name);
        //meal.setIngredients(ingredients);
        if (map.containsKey(category)) {
            // Если категория есть, берем существующий список и добавляем в него
            map.get(category).add(meal);
        } else {
            // Если категории нет, создаем новый список, добавляем блюдо и кладем в карту
            List<MealModel> newList = new ArrayList<>();
            newList.add(meal);
            map.put(category, newList);
        }
        System.out.println("The meal has been added!");
/*
        System.out.printf("Category: %s\n",category);
        System.out.printf("Name: %s\n",mealName);
        System.out.println("Ingredients:");
        for (String ingredient : list) {
            System.out.printf("%s\n",ingredient);
        }*/
        return map;

    }

    }

