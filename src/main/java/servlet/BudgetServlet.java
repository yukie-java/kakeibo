package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/BudgetServlet")
public class BudgetServlet extends HttpServlet {

    // 予算入力画面表示
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
            request.getRequestDispatcher("WEB-INF/jsp/budget.jsp");

        dispatcher.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");

        int amount = Integer.parseInt(request.getParameter("amount"));

        String monthKey =
                request.getParameter("targetMonth");


        dao.BudgetDAO dao = new dao.BudgetDAO();
        dao.saveMonthlyBudget(userId, monthKey, amount);

        response.sendRedirect("MainServlet");
    }

}
