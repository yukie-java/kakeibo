package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ExpenseDAO;
import model.Expense;

@WebServlet("/CsvServlet")
public class CsvServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("userId") == null) {
      response.sendRedirect("LoginServlet");
      return;
    }

    String userId = (String) session.getAttribute("userId");
    String month = request.getParameter("month");     // yyyy-MM
    String category = request.getParameter("category");

    ExpenseDAO dao = new ExpenseDAO();
    List<Expense> list = dao.findForCsv(userId, month, category);

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=\"expenses.csv\""
    );

    PrintWriter out = response.getWriter();
    out.print('\uFEFF'); // BOM（Excel対策）
    out.println("date,category,amount,memo");

    for (Expense e : list) {
      out.printf("%s,%s,%d,%s%n",
          safe(e.getDate()),
          safe(e.getCategory()),
          e.getAmount(),
          safe(e.getMemo())
      );
    }
  }

  // ★ クラスの一番下
  private static String safe(String s) {
    if (s == null) return "";
    String escaped = s.replace("\"", "\"\"");
    return "\"" + escaped + "\"";
  }
}
