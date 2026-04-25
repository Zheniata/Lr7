package org.example.server.manager;

import org.example.common.models.Coordinates;
import org.example.common.models.Organization;
import org.example.common.models.OrganizationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager(String url, String username, String password) throws SQLException{
        this.connection = DriverManager.getConnection(url, username, password);

    }

    public List<Organization> getAllOrganizations() throws SQLException{
        List<Organization> list = new ArrayList<>();

        String sql = "SELECT * FROM organizations ORDER BY id";
        try (Statement stm = connection.createStatement();
        ResultSet res = stm.executeQuery(sql)){
             while (res.next()){
                 Organization organization = new Organization();
                 organization.setId(res.getLong("id"));
                 organization.setName(res.getString("name"));
                 organization.setAnnualTurnover(res.getFloat("annual_turnover"));
                 organization.setOwnerId(res.getLong("owner_id"));
                 String typeStr = res.getString("type");
                 if (typeStr != null) {
                     organization.setType(OrganizationType.valueOf(typeStr));
                 }
                 Double x = res.getObject("coordinates_x", Double.class);
                 Long y = res.getObject("coordinates_y", Long.class);
                 if (x != null || y != null){
                     organization.setCoordinates(new Coordinates(x, y));
                 }

                 list.add(organization);
             }
        }

        return list;
    }

    public long saveOrganization(Organization organization, Long ownerId) throws SQLException{

        String sql = "INSERT INTO organizations (name, coordinates_x, coordinates_y, annual_turnover, type, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, organization.getName());
            Coordinates coords = organization.getCoordinates();
            if (coords != null) {
                pstmt.setObject(2, coords.getX(), Types.DOUBLE);
                pstmt.setObject(3, coords.getY(), Types.BIGINT);
            } else {
                pstmt.setNull(2, Types.DOUBLE);
                pstmt.setNull(3, Types.BIGINT);
            }
            pstmt.setFloat(4, organization.getAnnualTurnover());
            pstmt.setString(5, organization.getType().name());
            pstmt.setLong(6, ownerId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Сохранение не удалось");
            }

            try (ResultSet res = pstmt.getGeneratedKeys()) {
                if (res.next()) {
                    return res.getLong(1);
                }
            }
            throw new SQLException("Не удалось получить сгенерированнный id");
        }

    }

    public boolean updateOrganization(Organization org) throws SQLException{
        String sql = "UPDATE organization SET name = ?, coordinates_x = ?, coordinates_y = ?, annual_turnover = ?," +
                "type = ? WHERE id = ?";
        try (PreparedStatement prt = connection.prepareStatement(sql)){
            prt.setString(1, org.getName());
            Coordinates coords = org.getCoordinates();
            if (coords != null){
                prt.setObject(2, coords.getX(), Types.DOUBLE);
                prt.setObject(3, coords.getY(), Types.BIGINT);
            } else {
                prt.setNull(2, Types.DOUBLE);
                prt.setNull(3, Types.BIGINT);
            }
            prt.setFloat(4, org.getAnnualTurnover());
            prt.setString(5, org.getType() != null ? org.getType().name() : null);
            prt.setLong(6, org.getId());

            int count = prt.executeUpdate();
            boolean success = count > 0;

            if (success) {
                System.out.println("Организация с id = " + org.getId() + " обновлена");
            } else {
                System.out.println("Организация с id = " + org.getId() + " не найдена");
            }
            return success;
        }
    }

    public boolean deleteOrganization(long id) throws SQLException{
        String sql = "DELETE FROM organization WHERE id = ?";
        try (PreparedStatement prt = connection.prepareStatement(sql)){
            prt.setLong(1, id);
            int count = prt.executeUpdate();
            boolean success = count > 0;
            if (success) {
                System.out.println("Организация с id = " + id + " удалена");
            } else {
                System.out.println("Организация с id = " + id + "не найдена");
            }
            return success;
        }
    }

    public void clearAllOrganizations() throws SQLException{
        String sql = "DELETE FROM organization";
        try (Statement stmt = connection.createStatement()){
             stmt.executeUpdate(sql);
             System.out.println("Все организации удалены");
        }
    }

    public void close() throws SQLException{
        connection.close();
    }
}
