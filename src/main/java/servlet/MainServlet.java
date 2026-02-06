package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.BudgetDAO;
import dao.ExpenseDAO;
import model.Expense;



@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 一覧表示
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("doGet called");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String userId = (String) session.getAttribute("userId");

        // ===== 条件取得 =====
        String month = request.getParameter("month");
        String category = request.getParameter("category");

        ExpenseDAO dao = new ExpenseDAO();

        // ===== 一覧 =====
        List<Expense> list = dao.findForCsv(userId, month, category);
        request.setAttribute("expenseList", list);

        // ===== 合計 =====
        int total = dao.sumForCsv(userId, month, category);
        request.setAttribute("total", total);

        // ===== カテゴリ別 =====
        Map<String, Integer> categorySum =
                dao.sumByCategory(userId, month);
        request.setAttribute("categorySum", categorySum);

        // ===== 月別 =====
        Map<String, Integer> monthSum =
                dao.sumByMonth(userId, category);
        request.setAttribute("monthSum", monthSum);

        // ===== 条件保持 =====
        request.setAttribute("month", month);
        request.setAttribute("category", category);

        // ===== 予算取得 =====
        java.time.YearMonth now = java.time.YearMonth.now();

        String prevMonth = now.minusMonths(1).toString();
        String currentMonth = now.toString();
        String nextMonth = now.plusMonths(1).toString();

        BudgetDAO budgetDao = new BudgetDAO();

        Integer prevBudget =
                budgetDao.findMonthlyBudget(userId, prevMonth);

        Integer currentBudget =
                budgetDao.findMonthlyBudget(userId, currentMonth);

        Integer nextBudget =
                budgetDao.findMonthlyBudget(userId, nextMonth);

        request.setAttribute("prevMonth", prevMonth);
        request.setAttribute("currentMonth", currentMonth);
        request.setAttribute("nextMonth", nextMonth);

        request.setAttribute("prevBudget", prevBudget);
        request.setAttribute("currentBudget", currentBudget);
        request.setAttribute("nextBudget", nextBudget);
        
     // ===== 達成率 =====
        double budgetRate = 0;

        if (currentBudget != null && currentBudget > 0) {
            budgetRate = (double) total / currentBudget * 100;
        }

        request.setAttribute("budgetRate", budgetRate);


        // ===== 今月アラート =====
        boolean needBudgetAlert = (currentBudget == null);
        request.setAttribute("needBudgetAlert", needBudgetAlert);

        RequestDispatcher dispatcher =
                request.getRequestDispatcher("WEB-INF/jsp/main.jsp");
        dispatcher.forward(request, response);
    }

    // 登録
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("★★★ doPost HIT ★★★");
    
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String date = request.getParameter("date");
        String category = request.getParameter("category");
        int amount = Integer.parseInt(request.getParameter("amount"));
        String memo = request.getParameter("memo");
        
        System.out.println("=== doPost START ===");
        System.out.println("date=" + date);
        System.out.println("category=" + category);
        System.out.println("amount=" + amount);
        System.out.println("memo=" + memo);


        Expense exp = new Expense(userId, date, category, amount, memo);
        ExpenseDAO dao = new ExpenseDAO();
        dao.insert(exp);

        // PRGパターン（二重送信防止）
        response.sendRedirect("MainServlet");
    }
}
