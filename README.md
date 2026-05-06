# 🍽 Meal Planner (Java)

A console-based meal planning application that allows users to create, manage, 
and save weekly meal plans along with automatically generated shopping lists.

## 📌 Features

* Add meals with categories and ingredients
* Store data in a PostgreSQL database
* Generate a weekly meal plan (breakfast, lunch, dinner)
* View saved plans
* Automatically calculate required ingredients
* Save shopping list to a file

## 🧠 How It Works

1. User adds meals categorized as:
   * Breakfast
   * Lunch
   * Dinner
2. Meals and ingredients are stored in the database:
   * `meals` – meal info
   * `ingredients` – linked to meals
   * `plan` – selected meals for each day
3. When planning:
   * User selects meals for each day
   * Plan is saved in DB
4. When saving:
   * App calculates ingredient usage
   * Aggregates duplicates
   * Outputs shopping list to a file

## 🗂 Database Schema

### `meals`

| Column   | Type       |
| -------- | ---------- |
| meal_id  | INTEGER PK |
| category | VARCHAR    |
| meal     | VARCHAR    |

### `ingredients`

| Column        | Type       |
| ------------- | ---------- |
| ingredient_id | INTEGER PK |
| ingredient    | VARCHAR    |
| meal_id       | FK → meals |

### `plan`

|   Column     |   Type     |
| ------------ | ---------- |
| meal_option  | VARCHAR    |
| meal_category| VARCHAR    |
| meal_id      | FK → meals |

## ▶️ Usage

```bash
What would you like to do (add, show, plan, list plan, save, exit)?
```
### Commands
* `add` — add a new meal
* `show` — display meals
* `plan` — create weekly plan
* `list plan` — view current plan
* `save` — save shopping list to file
* `exit` — exit application

## 💾 Example Output (Shopping List)
```
eggs x4
milk x2
cheese
tomato x3
```
## ⚙️ Technologies

* Java (Core)
* JDBC
* PostgreSQL
* Collections (`Map`, `List`, `Set`)

## 🚀 Key Implementation Details

* Uses `Map<String, Integer>` to count ingredients
* Avoids duplicate ingredients using `Set` or validation logic
* Ensures clean output formatting (no trailing spaces)
* Uses batch inserts for performance

---

## 👨‍💻 Author

Dmytro Melnyk


## 📄 License

This project is for educational purposes.
