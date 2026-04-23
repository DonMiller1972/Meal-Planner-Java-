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


    public void showFood(Map<String, List<MealModel>> mealModels){

        if (mealModels == null || mealModels.isEmpty()) {
            System.out.println("No meals saved. Add a meal first.");
            return;
        }
        System.out.println();
        for (Map.Entry<String, List<MealModel>> entry : mealModels.entrySet()) {
            String category = entry.getKey();
            List<MealModel> mealList = entry.getValue();
            System.out.println("Category: " + category);
            for (MealModel meal : mealList) {

                System.out.println("Name: " + meal.getName());
                System.out.println("Ingredients:");


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
        boolean obg = false;
        boolean show = true;
        while(circle) {
            System.out.println("What would you like to do (add, show, exit)?");

            String menu = sc.nextLine().trim().toLowerCase();
            switch (menu) {
                case "add" -> mealMap = addFood(mealMap);
                case "show" -> {
                    show=true;
                    System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                    while(show==true) {
                        menu = sc.nextLine().trim().toLowerCase();
                        if (checkCategory(menu)) {
                            if (menu.equals("all")) {
                                obg = daoMeal.hasAnyMeals();
                            } else {
                                obg = daoMeal.hasMealsByCategory(menu);
                            }
                            if (obg != true) {
                                System.out.println("No meals found.");
                                show=false;
                            } else {
                                if (menu.equals("all")) {
                                    mealMap = daoMeal.getAllMeals();
                                } else {
                                    mealMap = daoMeal.getMealsByCategory(menu);
                                }

                                //mealMap = daoMeal.getAllMeals();
                                showFood(mealMap);
                                show=false;
                            }
                        }
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

        Map<String ,List<MealModel>> map = new LinkedHashMap<>(mapMeal);

        String category ="";
        String name = "";
        List<String> ingredients;
        String parts ="";

        boolean circle = true;

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        while(circle) {
            category = sc.nextLine();
            if(checkCategory(category)){
                circle = false;
            }

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

        if (map.containsKey(category)) {

            map.get(category).add(meal);
        } else {

            List<MealModel> newList = new ArrayList<>();
            newList.add(meal);
            map.put(category, newList);
        }
        //System.out.println("The meal has been added!");

        return map;

    }

}

