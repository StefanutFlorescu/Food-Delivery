package org.example;

import org.example.model.*;
import org.example.service.*;

import java.util.Scanner;

public class FoodDeliveryApp {
    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();

        repo.loadFromDb();
        FoodDeliveryService svc = new FoodDeliveryService(repo);

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("--- Food Delivery ---");
            System.out.println("1. Creeaza user");
            System.out.println("2. Creeaza premium user");
            System.out.println("3. Creeaza student user");
            System.out.println("4. Creeaza local");
            System.out.println("5. Creeaza sofer");
            System.out.println("6. Creeaza sofer express");
            System.out.println("7. Listeaza useri");
            System.out.println("8. Listeaza localuri");
            System.out.println("9. Listeaza localuri (sortate)");
            System.out.println("10. Creeaza meniu pentru local");
            System.out.println("11. Adauga item in meniu");
            System.out.println("12. Listeaza meniuri");
            System.out.println("13. Adauga review" );
            System.out.println("14. Listeaza review-uri");
            System.out.println("15. Plaseaza comanda");
            System.out.println("16. Asigneaza sofer la comanda");
            System.out.println("17. Marcheaza comanda ca livrata");
            System.out.println("18. Listeaza comenzi");
            System.out.println("0. Iesire");
            System.out.print("Alegere: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1" -> {
                        System.out.print("Nume user: ");
                        String n = sc.nextLine();
                        System.out.print("Adresa: ");
                        String a = sc.nextLine();
                        System.out.print("Email: ");
                        String e = sc.nextLine();
                        System.out.print("Telefon: ");
                        String phone = sc.nextLine();
                        User u = svc.createUser(n, a, e, phone);
                        svc.persistUserToDb(u);
                        System.out.println("Creat: " + u);
                    }
                    case "2" -> {
                        System.out.print("Nume premium user: ");
                        String n = sc.nextLine();
                        System.out.print("Adresa: ");
                        String a = sc.nextLine();
                        System.out.print("Email: ");
                        String e = sc.nextLine();
                        System.out.print("Telefon: ");
                        String phone = sc.nextLine();
                        System.out.print("Puncte loialitate: ");
                        int p = Integer.parseInt(sc.nextLine());
                        PremiumUser pu = svc.createPremiumUser(n, a, e, phone, p);
                        svc.persistUserToDb(pu);
                        System.out.println("Creat: " + pu);
                    }
                    case "3" -> {
                        System.out.print("Nume student user: ");
                        String n = sc.nextLine();
                        System.out.print("Adresa: ");
                        String a = sc.nextLine();
                        System.out.print("Email: ");
                        String e = sc.nextLine();
                        System.out.print("Telefon: ");
                        String phone = sc.nextLine();
                        System.out.print("Universitate: ");
                        String university = sc.nextLine();
                        System.out.print("Reducere student (%): ");
                        int discount = Integer.parseInt(sc.nextLine());
                        StudentUser su = svc.createStudentUser(n, a, e, phone, university, discount);
                        svc.persistUserToDb(su);
                        System.out.println("Creat: " + su);
                    }
                    case "4" -> {
                        System.out.print("Nume local: ");
                        String n = sc.nextLine();
                        System.out.print("Adresa: ");
                        String a = sc.nextLine();
                        Restaurant r = svc.createRestaurant(n, a);
                        svc.persistRestaurantToDb(r);
                        System.out.println("Creat: " + r);
                    }
                    case "5" -> {
                        System.out.print("Nume sofer: ");
                        String n = sc.nextLine();
                        Driver d = svc.createDriver(n);
                        svc.persistDriverToDb(d);
                        System.out.println("Creat: " + d);
                    }
                    case "6" -> {
                        System.out.print("Nume sofer express: ");
                        String n = sc.nextLine();
                        System.out.print("Tip vehicul: ");
                        String vehicleType = sc.nextLine();
                        System.out.print("Multiplicator viteza: ");
                        double speedMultiplier = Double.parseDouble(sc.nextLine());
                        ExpressDriver d = svc.createExpressDriver(n, vehicleType, speedMultiplier);
                        svc.persistDriverToDb(d);
                        System.out.println("Creat: " + d);
                    }
                    case "7" -> svc.listUsers().forEach(System.out::println);
                    case "8" -> svc.listRestaurants().forEach(System.out::println);
                    case "9" -> svc.listRestaurantsSorted().forEach(System.out::println);
                    case "10" -> {
                        System.out.print("Restaurant id pentru meniu: ");
                        int rid = Integer.parseInt(sc.nextLine());
                        Menu m = svc.createMenuForRestaurant(rid);
                        System.out.println("Meniu creat: " + m);
                    }
                    case "11" -> {
                        System.out.print("Menu id: ");
                        int mid = Integer.parseInt(sc.nextLine());
                        System.out.print("Nume item: ");
                        String iname = sc.nextLine();
                        System.out.print("Pret: ");
                        double price = Double.parseDouble(sc.nextLine());
                        MenuItem mi = svc.addMenuItem(mid, iname, price);
                        System.out.println(mi == null ? "Meniu inexistent" : "Item adaugat: " + mi);
                    }
                    case "12" -> svc.listMenus().forEach(System.out::println);
                    case "13" -> {
                        System.out.print("User id: ");
                        int uid = Integer.parseInt(sc.nextLine());
                        System.out.print("Restaurant id: ");
                        int rid = Integer.parseInt(sc.nextLine());
                        System.out.print("Rating (1-5): ");
                        int rating = Integer.parseInt(sc.nextLine());
                        System.out.print("Comentariu: ");
                        String c = sc.nextLine();
                        Review rv = svc.addReview(uid, rid, rating, c);
                        System.out.println("Review adaugat: " + rv);
                    }
                    case "14" -> svc.listReviews().forEach(System.out::println);
                    case "15" -> {
                        System.out.print("User id: ");
                        int uid = Integer.parseInt(sc.nextLine());
                        System.out.print("Restaurant id: ");
                        int rid = Integer.parseInt(sc.nextLine());
                        System.out.print("Item(s) descriere: ");
                        String it = sc.nextLine();
                        Order o = svc.placeOrder(uid, rid, it);
                        svc.persistOrderToDb(o);
                        System.out.println("Comanda plasata: " + o);
                    }
                    case "16" -> {
                        System.out.print("Order id: ");
                        int oid = Integer.parseInt(sc.nextLine());
                        System.out.print("Driver id: ");
                        int did = Integer.parseInt(sc.nextLine());
                        boolean ok = svc.assignDriver(oid, did);
                        System.out.println(ok ? "Sofer asignat" : "Eroare asignare");
                    }
                    case "17" -> {
                        System.out.print("Order id: ");
                        int oid = Integer.parseInt(sc.nextLine());
                        boolean ok = svc.completeOrder(oid);
                        System.out.println(ok ? "Comanda livrata" : "Eroare" );
                    }
                    case "18" -> svc.listOrders().forEach(System.out::println);
                    case "0" -> running = false;
                    default -> System.out.println("Optiune invalida");
                }
            } catch (Exception e) {
                System.out.println("Eroare: " + e.getMessage());
            }
            System.out.println();
        }
        sc.close();
    }
}
