package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Expense;






public class ExpenseDAO {
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

    public List<Expense> findAll(String userId) {
    	 
    	
    	List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id=? ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Expense e = new Expense();
                e.setId(rs.getInt("id"));
                e.setUserId(rs.getString("user_id"));
                e.setDate(rs.getString("date"));
                e.setCategory(rs.getString("category"));
                e.setAmount(rs.getInt("amount"));
                e.setMemo(rs.getString("memo"));
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(Expense exp) {
        String sql = "INSERT INTO expenses(user_id, date, category, amount, memo) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, exp.getUserId());
            ps.setString(2, exp.getDate());
            ps.setString(3, exp.getCategory());
            ps.setInt(4, exp.getAmount());
            ps.setString(5, exp.getMemo());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(int id) {

        String sql = "DELETE FROM expenses WHERE id=?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int sumByUser(String userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE user_id=?";
        int total = 0;

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    public Map<String, Integer> sumByCategory(String userId) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT category, COALESCE(SUM(amount),0) FROM expenses "
                   + "WHERE user_id=? GROUP BY category";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    public Map<String, Integer> sumByMonth(String userId) {
        Map<String, Integer> map = new HashMap<>();
        String sql =
            "SELECT FORMATDATETIME(date,'yyyy-MM'), COALESCE(SUM(amount),0) "
          + "FROM expenses WHERE user_id=? "
          + "GROUP BY FORMATDATETIME(date,'yyyy-MM') "
          + "ORDER BY FORMATDATETIME(date,'yyyy-MM')";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Expense> findForCsv(String userId, String month, String category) {
        List<Expense> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT * FROM expenses WHERE user_id = ?"
        );

        if (month != null && !month.isEmpty()) {
            sql.append(" AND FORMATDATETIME(date, 'yyyy-MM') = ?");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
        }

        sql.append(" ORDER BY date");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setString(idx++, userId);

            if (month != null && !month.isEmpty()) {
                ps.setString(idx++, month);
            }
            if (category != null && !category.isEmpty()) {
                ps.setString(idx++, category);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Expense e = new Expense();
                e.setDate(rs.getString("date"));
                e.setCategory(rs.getString("category"));
                e.setAmount(rs.getInt("amount"));
                e.setMemo(rs.getString("memo"));
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int sumForCsv(String userId, String month, String category) {

        StringBuilder sql = new StringBuilder(
            "SELECT COALESCE(SUM(amount),0) FROM expenses WHERE user_id=?"
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (month != null && !month.isEmpty()) {
            sql.append(" AND FORMATDATETIME(date,'yyyy-MM')=?");
            params.add(month);
        }

        if (category != null && !category.isEmpty()) {
            sql.append(" AND category=?");
            params.add(category);
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
 
    public Map<String, Integer> sumByCategory(String userId, String month) {

        Map<String, Integer> map = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(
            "SELECT category, SUM(amount) FROM expenses WHERE user_id=?"
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (month != null && !month.isEmpty()) {
            sql.append(" AND FORMATDATETIME(date,'yyyy-MM')=?");
            params.add(month);
        }

        sql.append(" GROUP BY category");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, Integer> sumByMonth(String userId, String category) {

        Map<String, Integer> map = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(
            "SELECT FORMATDATETIME(date,'yyyy-MM'), SUM(amount) FROM expenses WHERE user_id=?"
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (category != null && !category.isEmpty()) {
            sql.append(" AND category=?");
            params.add(category);
        }

        sql.append(" GROUP BY FORMATDATETIME(date,'yyyy-MM') ORDER BY 1");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

 
}
