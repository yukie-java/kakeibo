package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ログイン画面を表示（GET）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher("WEB-INF/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    // ログイン処理（POST）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // フォームから値を取得
        String userId = request.getParameter("userId");
        String pass = request.getParameter("pass");

        // ★最初はダミー判定でOK（後でDAOに置き換える）
        boolean loginResult = false;
        if ("test".equals(userId) && "1234".equals(pass)) {
            loginResult = true;
        }

        if (loginResult) {
            // セッションにユーザーIDを保存
            HttpSession session = request.getSession();
            session.setAttribute("userId", userId);

            // メイン画面へ
            response.sendRedirect("MainServlet");


        } else {
            // ログイン失敗 → ログイン画面へ戻す
            response.sendRedirect("LoginServlet");
        }
    }
}
