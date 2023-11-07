package org.lessons.java;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PASSWORD");

    private final static String QUERY_SEARCH = """
            SELECT c.country_id as id,
            c.name as country,
            r.name as region,
            c2.name as continent
            FROM countries c
            JOIN regions r ON c.region_id = r.region_id
            JOIN continents c2 ON r.continent_id = c2.continent_id
            WHERE c.name LIKE ?
            ORDER BY c.name;
            """;

    private final static String QUERY_INFO = """
            SELECT c.name
            FROM countries c
            WHERE country_id = ?;
            """;

    private final static String QUERY_LANGUAGES = """
            SELECT l.language
            FROM countries c
            JOIN country_languages cl ON c.country_id = cl.country_id
            JOIN languages l ON cl.language_id = l.language_id
            WHERE c.country_id = ?
            ORDER BY l.language;
            """;

    private final static String QUERY_STATS = """
            SELECT c.country_id,
            c.name,
            cs.year,
            cs.population,
            cs.gdp
            FROM countries c
            JOIN country_stats cs ON c.country_id = cs.country_id
            WHERE c.country_id = ?
            ORDER BY cs.year DESC
            LIMIT 1;
            """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("\n- Cerca la nazione: ");
            String input = scanner.nextLine();

            boolean hasResults = false;

            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_SEARCH)) {

                preparedStatement.setString(1, "%" + input + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        hasResults = true;

                        int countryId = resultSet.getInt(1);
                        String countryName = resultSet.getString(2);
                        String regionName = resultSet.getString(3);
                        String continentName = resultSet.getString(4);

                        System.out.println(countryId + " - " + countryName + " - " + regionName + " - " + continentName);
                    }

                } catch (SQLException e) {
                    System.out.println("\n- Impossibile eseguire la query");
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.out.println("\n- Impossibile preparare la dichiarazione");
                e.printStackTrace();
            }

            if (hasResults) {
                System.out.println("\n- Scegli l'id:");
                int inputId = Integer.parseInt(scanner.nextLine());

                try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_INFO)) {

                    preparedStatement.setInt(1, inputId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String countryName = resultSet.getString(1);
                            System.out.println("\nDettagli del paese: " + countryName);
                        }
                    } catch (SQLException e) {
                        System.out.println("\n- Impossibile eseguire la query");
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    System.out.println("\n- Impossibile preparare la dichiarazione");
                    e.printStackTrace();
                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_LANGUAGES)) {

                    preparedStatement.setInt(1, inputId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {

                        List<String> languages = new ArrayList<>();

                        while (resultSet.next()) {
                            languages.add(resultSet.getString(1));
                        }

                        System.out.println("Lingue: " + String.join(", ", languages));

                    } catch (SQLException e) {
                        System.out.println("\n- Impossibile eseguire la query");
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    System.out.println("\n- Impossibile preparare la dichiarazione");
                    e.printStackTrace();
                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_STATS)) {

                    preparedStatement.setInt(1, inputId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {

                        if (resultSet.next()) {
                            int year = resultSet.getInt("year");
                            BigDecimal population = resultSet.getBigDecimal("population");
                            BigDecimal gdp = resultSet.getBigDecimal("gdp");

                            System.out.println("Statistiche più recenti" +
                                    "\nAnno: " + year +
                                    "\nPopolazione: " + population +
                                    "\nGDP: " + gdp
                            );
                        }

                    } catch (SQLException e) {
                        System.out.println("\n- Impossibile eseguire la query");
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    System.out.println("\n- Impossibile preparare la dichiarazione");
                    e.printStackTrace();
                }

            } else {
                System.out.println("\n- Nessuno risultato trovato");
            }

        } catch (SQLException e) {
            System.out.println("\n- Impossibile connettersi");
            e.printStackTrace();
        }

        scanner.close();
    }
}

