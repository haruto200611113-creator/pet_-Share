package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdatePasswordServlet")
public class UpdatePasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
            request.getSession(false);

        // =====================================
        // ログイン確認
        // =====================================

        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                "ログインしてください。"
            );

            return;
        }

        User loginUser =
            (User) session.getAttribute("loginUser");


        // =====================================
        // 入力値取得
        // =====================================

        String currentPassword =
            request.getParameter("currentPassword");

        String newPassword =
            request.getParameter("newPassword");

        String confirmPassword =
            request.getParameter("confirmPassword");


        // =====================================
        // 入力チェック
        // =====================================

        if (currentPassword == null
                || currentPassword.trim().isEmpty()) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "現在のパスワードを入力してください。"
            );

            return;
        }

        if (newPassword == null
                || newPassword.trim().isEmpty()) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "新しいパスワードを入力してください。"
            );

            return;
        }

        if (confirmPassword == null
                || confirmPassword.trim().isEmpty()) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "新しいパスワード（確認）を入力してください。"
            );

            return;
        }


        // =====================================
        // 8文字以上チェック
        // =====================================

        if (newPassword.length() < 8) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "新しいパスワードは8文字以上で設定してください。"
            );

            return;
        }


        // =====================================
        // パスワード一致確認
        // =====================================

        if (!newPassword.equals(confirmPassword)) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "新しいパスワードが一致していません。"
            );

            return;
        }


        // =====================================
        // DB更新
        // =====================================

        UserDAO dao =
            new UserDAO();

        boolean result =
            dao.updatePassword(
                loginUser.getAccountId(),
                currentPassword,
                newPassword
            );


        // =====================================
        // 成功
        // =====================================

        if (result) {

            System.out.println(
                "================================="
            );

            System.out.println(
                "パスワード変更成功"
            );

            System.out.println(
                "account_id：" +
                loginUser.getAccountId()
            );

            System.out.println(
                "================================="
            );

            response.setStatus(
                HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                "パスワードを変更しました。"
            );

        }

        // =====================================
        // 失敗
        // =====================================

        else {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "現在のパスワードが正しくないか、パスワードの変更に失敗しました。"
            );
        }
    }
}