package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetDAO {

    private static final String JDBC_URL = "jdbc:h2:~/kakeibo";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // ⭐ 月予算取得
    public Integer findMonthlyBudget(String userId, String month) {

        String sql =
            "SELECT amount FROM budgets WHERE user_id=? AND type='month' AND target=?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, month);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("amount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ⭐ 月予算 保存（更新 or 新規）
    public void saveMonthlyBudget(String userId, String month, int amount) {

        String checkSql =
            "SELECT id FROM budgets WHERE user_id=? AND type='month' AND target=?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setString(1, userId);
            checkPs.setString(2, month);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {

                // ⭐ UPDATE
                String updateSql =
                    "UPDATE budgets SET amount=? WHERE id=?";

                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

                    updatePs.setInt(1, amount);
                    updatePs.setInt(2, rs.getInt("id"));
                    updatePs.executeUpdate();
                }

            } else {

                // ⭐ INSERT
                String insertSql =
                    "INSERT INTO budgets(user_id,type,target,amount) VALUES(?,?,?,?)";

                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {

                    insertPs.setString(1, userId);
                    insertPs.setString(2, "month");
                    insertPs.setString(3, month);
                    insertPs.setInt(4, amount);
                    insertPs.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
