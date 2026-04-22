Food Delivery - proiect Java

Aplicatie Java de tip consola care modeleaza un sistem simplificat de food delivery. Proiectul permite administrarea utilizatorilor, localurilor, soferilor, meniurilor, review-urilor si comenzilor, iar in implementarea actuala include si persistenta JDBC pentru o parte dintre entitati, plus audit CSV.

## 1. Definirea sistemului

Tema aleasa este o platforma de food delivery in care utilizatorii pot comanda mancare de la restaurante, pot lasa review-uri, iar comenzile pot fi preluate si livrate de soferi.

### Actiuni/interogari disponibile in sistem

In proiect sunt implementate cel putin urmatoarele actiuni si interogari:

1. Creare user obisnuit
2. Creare premium user
3. Creare student user
4. Creare restaurant
5. Creare sofer
6. Creare sofer express
7. Afisare lista de useri
8. Afisare lista de restaurante
9. Afisare lista de restaurante sortate alfabetic
10. Creare meniu pentru un restaurant
11. Adaugare item in meniu
12. Afisare lista de meniuri
13. Adaugare review pentru restaurant
14. Afisare lista de review-uri
15. Plasare comanda
16. Asignare sofer la comanda
17. Marcare comanda ca livrata
18. Afisare lista de comenzi

Pe langa acestea, proiectul mai include si functionalitati tehnice suplimentare deja prezente in cod:

17. Incarcare date initiale din baza de date la pornirea aplicatiei
18. Persistare in baza de date pentru useri
19. Persistare in baza de date pentru restaurante
20. Persistare in baza de date pentru soferi
21. Persistare in baza de date pentru comenzi
22. Auditarea actiunilor in fisier CSV

### Tipuri de obiecte din sistem

In modelul aplicatiei apar cel putin urmatoarele tipuri de obiecte:

1. User
2. PremiumUser
3. StudentUser
4. Restaurant
5. Driver
6. ExpressDriver
7. Order
8. OrderStatus
9. Menu
10. MenuItem
11. Review

### Componente tehnice importante

Pe langa obiectele de domeniu, proiectul mai foloseste si urmatoarele componente de infrastructura:

- InMemoryRepository pentru stocarea datelor in memorie
- FoodDeliveryService pentru logica de business
- UserJdbcService pentru persistenta userilor
- RestaurantJdbcService pentru persistenta restaurantelor
- DriverJdbcService pentru persistenta soferilor
- OrderJdbcService pentru persistenta comenzilor
- DatabaseManager pentru conexiunea la baza de date
- AuditService pentru scrierea actiunilor in audit.csv

## Structura principala

- `src/main/java/org/example/model` - clasele de baza ale sistemului
- `src/main/java/org/example/service` - repository-ul in memorie si serviciul principal
- `src/main/java/org/example/dao` - servicii JDBC pentru persistenta
- `src/main/java/org/example/db` - management conexiune baza de date si audit
- `src/main/java/org/example/FoodDeliveryApp.java` - aplicatia consola si meniul interactiv

## 2. Implementare

Cerintele de implementare sunt acoperite astfel:

- Aplicatia este implementata in Java si porneste din clasa principala `FoodDeliveryApp`.
- Clasele de model folosesc atribute private si metode getter/setter. In special, `User` are acum atribute private pentru `name`, `address`, `email` si `phoneNumber`.
- Sunt folosite mai multe colectii diferite pentru gestionarea obiectelor: `HashMap`, `LinkedHashMap`, `List` si `TreeSet`/`NavigableSet`.
- Exista cel putin o colectie sortata: colectia de restaurante sortate alfabetic dupa nume.
- Mostenirea este folosita pentru a crea clase aditionale si pentru a le stoca in colectii polimorfice: `PremiumUser` si `StudentUser` extind `User`, iar `ExpressDriver` extinde `Driver`.
- Exista o clasa serviciu principala, `FoodDeliveryService`, care expune operatiile sistemului.
- Exista repository in memorie si servicii JDBC pentru persistenta, ceea ce separa logica de business de stocarea datelor.
- Clasa `FoodDeliveryApp` joaca rolul de `Main` si face apeluri directe catre servicii.

## Rulare

Din directorul proiectului:

```bash
mvn compile
java -cp target/classes org.example.FoodDeliveryApp
```

Daca `pom.xml` nu are configurat plugin `exec`, comanda `java` de mai sus este suficienta pentru rulare.
