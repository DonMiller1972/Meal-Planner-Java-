package mealplanner;

import mealplanner.dao.daoPlan;
import mealplanner.models.Category;
import mealplanner.models.DayPlan;
import mealplanner.models.MealModel;
import mealplanner.dao.daoMeal;
import mealplanner.models.Week;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

public class Rules {
    Scanner sc;
    daoMeal daoMeal;
    daoPlan daoPlan;

    public Rules(Scanner sc, daoMeal daoMeal, daoPlan daoPlan) {
        this.sc=sc;
        this.daoMeal=daoMeal;
        this.daoPlan = daoPlan;
    }

    public String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();

    }


    public void  showPlan(Map<Week, DayPlan> plan ){
        for(Week w : plan.keySet()){
            System.out.println(capitalize(w.toString()));
            System.out.println("Breakfast: " + plan.get(w).getBreakfast().getName());
            System.out.println("Lunch: " + plan.get(w).getLunch().getName());
            System.out.println("Dinner: " + plan.get(w).getDinner().getName());
        }
    }

    public Map<String, Integer> calculateIngredients(Map<Week, DayPlan> plan) {
        Map<String, Integer> result = new HashMap<>();
         for (DayPlan dayPlan : plan.values()) {
            for (Category category : Category.values()) {
                MealModel meal = dayPlan.getMeal(category);
                for (String ingredient : meal.getIngredients()) {

                    Integer currentCount = result.get(ingredient.trim());

                    if (currentCount == null) {

                        result.put(ingredient, 1);

                    } else {

                        int newCount = currentCount + 1;
                        result.put(ingredient, newCount);

                    }
                }

            }
        }

        return result;
    }

    public void printSaveIng(Map<String, Integer> ingredients, String path){

        File file = new File(path);
        try (PrintWriter printWriter = new PrintWriter(file)) {

            for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
                int count = entry.getValue();

                printWriter.printf("%s%s\r\n", entry.getKey(), (count == 1 ? "" : " x"+Integer.toString(count)));

            }
            System.out.println("Saved!");

        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }


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
        Map<Week, DayPlan> weekMap = new EnumMap<>(Week.class);
        boolean obg = false;
        boolean show = true;
        boolean isPlanGeneratedInThisSession = false;

        Map<String, Integer> ing = new HashMap<>();
        while(circle) {
            System.out.println("What would you like to do (add, show, plan, list plan, save, exit)?");

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
                                showFood(mealMap);
                                show=false;
                            }
                        }
                    }
                }
                case "plan" ->{
                    mealMap = daoMeal.getAllMealsOrderByMeal();

                    for(Week day: Week.values()){
                        weekMap.put(day, getChoiceMealDay(day, mealMap));

                    }
                    showPlan(weekMap);
                    daoPlan.savePlan(weekMap);
                    isPlanGeneratedInThisSession = true;

                }
                case "list plan"-> {
                    weekMap = daoPlan.getPlan();
                    showPlan(weekMap);
                }
                case "save"-> {
                    if(daoPlan.hasPlan()){
                        //                     //  &&isPlanGeneratedInThisSession==true) {
                        System.out.println("Input a filename:");
                        String fileName = sc.nextLine().trim();
                        weekMap = daoPlan.getPlan();
                        ing = calculateIngredients(weekMap);


                        printSaveIng(ing, fileName);


                    }else{
                        System.out.println("Unable to save. Plan your meals first.");
                    }
                }
                case "exit" -> {
                    circle = false;
                    System.out.println("Bye!");
                }
            }

        }
    }
    void printMeals(List<MealModel> meals) {
        for (MealModel meal : meals) {
            System.out.println(meal.getName());
        }
    }

    public DayPlan getChoiceMealDay(Week day, Map <String, List<MealModel>> mealMap ){
        DayPlan plan = new DayPlan();

        String categoryW = "";
        boolean isPresent;
        String dayW = capitalize(day.toString());
        boolean inp;
        System.out.println(dayW);
        for(Category category: Category.values()) {
                inp = true;
                isPresent = false;
                MealModel meal = new MealModel();
                //categoryW = category.toString().substring(0,1)+ category.toString().substring(1).toLowerCase() ;
                categoryW =  category.toString().toLowerCase() ;
                List<MealModel> mealList = mealMap.get(category.toString().toLowerCase());
                printMeals(mealList);
            while(inp==true) {
                System.out.printf("Choose the %s for %s from the list above:\n", categoryW, dayW);
                String mealName = sc.nextLine();
                for (MealModel model : mealList) {
                    if (mealName.equals(model.getName())) {
                        meal = model;
                        isPresent = true;
                        break;
                    }
                }
                if (isPresent == true) {

                    inp = false;
                    plan.setMeal(category, meal);

                } else {
                    System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                }
            }
        }

        System.out.printf("Yeah! We planned the meals for %s.\n", dayW);

        return plan;
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

        return map;

    }

}

