package com.starfall;

import com.starfall.DatabaseHandling.DatabaseController;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        DatabaseController database = new DatabaseController();
        System.out.println(database.getAsteroid(sc.nextLine()).getDiameter());
    }
}