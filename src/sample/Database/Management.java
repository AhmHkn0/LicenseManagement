package sample.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Management {


    public static void updateActiveByID(Integer id, int newActive) {
        String query = "UPDATE license SET active = ? WHERE id = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setInt(1, newActive);
            statement.setInt(2, id);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
            setSync(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updateBlockByID(Integer id, int newActive) {
        String query = "UPDATE license SET block = ? WHERE id = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setInt(1, newActive);
            statement.setInt(2, id);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
            setSync(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteID(Integer id) {
        String query = "DELETE FROM license WHERE id = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteCustomer(Integer customerID) {
        String query = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setInt(1, customerID);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addNewIP(String ip) {
        String query = "INSERT INTO license (ip, active, date, block) VALUES (?, ?, ?, ?)";
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, ip);
            statement.setInt(2, 0);
            statement.setDate(3, date);
            statement.setInt(4, 0);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                System.out.println("Yeni satır eklendi. Oluşturulan ID: " + id);
            }
            ConnectionPool.closeConnection(statement.getConnection());
            setSync(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addNewCustomer(String ip, String name, long discordID) {
        String query = "INSERT INTO customers (customer_ip, customer_name, customer_discord) VALUES (?, ?, ?)";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, ip);
            statement.setString(2, name);
            statement.setLong(3, discordID);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static boolean checkIfIpExists(String ip) {
        String query = "SELECT * FROM license WHERE ip = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setString(1, ip);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setSync(int value) {
        String query = "UPDATE sync SET value = ?";
        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            statement.setInt(1, value);
            statement.executeUpdate();
            ConnectionPool.closeConnection(statement.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getAllLicenseData() {
        String result = "";
        String query = "SELECT l.id, l.ip, l.active, l.date, l.block, c.customer_id, c.customer_ip, c.customer_name, c.customer_discord " +
                "FROM license l " +
                "INNER JOIN customers c ON l.ip = c.customer_ip";

        try (PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int licenseId = resultSet.getInt("id");
                String licenseIp = resultSet.getString("ip");
                int active = resultSet.getInt("active");
                String date = resultSet.getString("date");
                int block = resultSet.getInt("block");

                int customerId = resultSet.getInt("customer_id");
                String customerIp = resultSet.getString("customer_ip");
                String customerName = resultSet.getString("customer_name");
                long customerDiscord = resultSet.getLong("customer_discord");

                String rowData = licenseId + "##" + licenseIp + "##" + active + "##" + date + "##" + block + "##" +
                        customerId + "##" + customerIp + "##" + customerName + "##" + customerDiscord;

                result += rowData + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
