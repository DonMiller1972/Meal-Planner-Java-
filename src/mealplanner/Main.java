package mealplanner;

import mealplanner.dao.daoMeal;
import mealplanner.dbconnection.DbConnect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws SQLException {
        String DB_URL = "jdbc:postgresql:meals_db";
        Scanner sc = new Scanner(System.in);
        DbConnect db = new DbConnect(DB_URL);
        db.init();
        Connection con = db.getConnection();
        daoMeal daoMeal = new daoMeal(con);
        Rules rule = new Rules(sc, daoMeal);
        rule.mainMenu();

    }
}