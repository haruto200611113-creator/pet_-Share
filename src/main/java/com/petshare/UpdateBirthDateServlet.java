package com.petshare;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateBirthDateServlet")
public class UpdateBirthDateServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType(
                "application/json; charset=UTF-8");

        try {

            // ==============================
            // ログイン確認
            // ==============================

            HttpSession session = request.getSession(false);

            if (session == null
                    || session.getAttribute("loginUser") == null) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"ログインしてください。\"}");

                return;
            }

            // ==============================
            // ログインユーザー取得
            // ==============================

            User loginUser = (User) session.getAttribute("loginUser");

            int accountId = loginUser.getAccountId();

            // ==============================
            // 生年月日取得
            // ==============================

            String birthDate = request.getParameter("birthDate");

            if (birthDate != null) {
                birthDate = birthDate.trim();
            }

            // ==============================
            // 入力チェック
            // ==============================

            if (birthDate == null
                    || birthDate.isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"生年月日を入力してください。\"}");

                return;
            }

            // ==============================
            // DB更新
            // ==============================

            UserDAO dao = new UserDAO();

            boolean result = dao.updateBirthDate(
                    accountId,
                    birthDate);

            // ==============================
            // 更新結果
            // ==============================

            if (result) {

                // セッションのユーザー情報も更新
                loginUser.setBirthDate(birthDate);

                session.setAttribute(
                        "loginUser",
                        loginUser);

                response.getWriter().write(
                        "{\"success\":true,\"message\":\"生年月日を変更しました。\"}");

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"生年月日の変更に失敗しました。\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (!response.isCommitted()) {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"システムエラーが発生しました。\"}");
            }
        }
    }
}