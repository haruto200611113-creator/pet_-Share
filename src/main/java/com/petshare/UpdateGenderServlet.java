package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateGenderServlet")
public class UpdateGenderServlet extends HttpServlet {

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

                        // ログイン確認
                        HttpSession session = request.getSession(false);

                        if (session == null
                                        || session.getAttribute("loginUser") == null) {

                                response.setStatus(
                                                HttpServletResponse.SC_UNAUTHORIZED);

                                response.getWriter().write(
                                                "{\"success\":false,\"message\":\"ログインしてください。\"}");

                                return;
                        }

                        // ログインユーザー取得
                        User loginUser = (User) session.getAttribute("loginUser");

                        int accountId = loginUser.getAccountId();

                        // 入力された性別を取得
                        String gender = request.getParameter("gender");

                        if (gender != null) {
                                gender = gender.trim();
                        }

                        // 入力チェック
                        if (gender == null
                                        || gender.isEmpty()) {

                                response.setStatus(
                                                HttpServletResponse.SC_BAD_REQUEST);

                                response.getWriter().write(
                                                "{\"success\":false,\"message\":\"性別を選択してください。\"}");

                                return;
                        }

                        // 選択できる性別をチェック
                        if (!gender.equals("男性")
                                        && !gender.equals("女性")
                                        && !gender.equals("その他")
                                        && !gender.equals("回答しない")) {

                                response.setStatus(
                                                HttpServletResponse.SC_BAD_REQUEST);

                                response.getWriter().write(
                                                "{\"success\":false,\"message\":\"正しい性別を選択してください。\"}");

                                return;
                        }

                        // DB更新
                        UserDAO dao = new UserDAO();

                        boolean result = dao.updateGender(
                                        accountId,
                                        gender);

                        if (result) {

                                // セッションのユーザー情報も更新
                                loginUser.setGender(gender);

                                session.setAttribute(
                                                "loginUser",
                                                loginUser);

                                response.getWriter().write(
                                                "{\"success\":true,\"message\":\"性別を変更しました。\"}");

                        } else {

                                response.setStatus(
                                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                                response.getWriter().write(
                                                "{\"success\":false,\"message\":\"性別の変更に失敗しました。\"}");
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