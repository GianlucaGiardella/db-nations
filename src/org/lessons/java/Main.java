package org.lessons.java;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_nations";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            String query = "SELECT countries.country_id as id, " +
                    "countries.name as country, " +
                    "regions.name as region, " +
                    "continents.name as continent FROM `countries` " +
                    "JOIN regions ON countries.region_id = regions.region_id " +
                    "JOIN continents ON regions.continent_id = continents.continent_id " +
                    "ORDER BY countries.name;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        int countryId = resultSet.getInt(1);
                        String countryName = resultSet.getString(2);
                        String regionName = resultSet.getString(3);
                        String continentName = resultSet.getString(4);

                        System.out.println(countryId + " - " + countryName + " - " + regionName + " - " + continentName);
                    }

                } catch (SQLException e) {
                    System.out.println("Unable to execute query");
                }

            } catch (SQLException e) {
                System.out.println("Unable to prepare statement");
            }

        } catch (SQLException e) {
            System.out.println("Unable to connect");
        }
    }
}
