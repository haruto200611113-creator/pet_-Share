package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 文字コード設定
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 入力値取得
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 未入力チェック
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            response.sendRedirect("login.html?error=empty");
            return;
        }

        email = email.trim();

        try {
            // DAOを1つだけ作成
            UserDAO userDAO = new UserDAO();

            // --------------------------------------------------
            // ログイン情報＋プロフィール情報を
            // 1回のDBアクセスで取得
            // --------------------------------------------------
            User loginUser = userDAO.login(email, password);

            // ログイン失敗
            if (loginUser == null) {
                response.sendRedirect("login.html?error=invalid");
                return;
            }

            // --------------------------------------------------
            // 既存セッションを破棄
            // --------------------------------------------------
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // 新しいセッションを作成
            HttpSession session = request.getSession(true);

            // セッションにユーザー情報を保存
            session.setAttribute("loginUser", loginUser);
            session.setAttribute("accountId", loginUser.getAccountId());
            session.setAttribute("userRank", loginUser.getUserRank());

            // セッション有効時間
            session.setMaxInactiveInterval(30 * 60);

            // ホーム画面へ
            response.sendRedirect("home.html");

        } catch (Exception e) {

            // サーバー側ログ
            e.printStackTrace();

            // システムエラー
            response.sendRedirect("login.html?error=system");
        }
    }
}