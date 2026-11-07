package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateEmailServlet")
public class UpdateEmailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // =====================================
        // 文字コード設定
        // =====================================

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");


        // =====================================
        // セッション取得
        // =====================================

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


        // =====================================
        // ログインユーザー取得
        // =====================================

        User loginUser =
            (User) session.getAttribute("loginUser");


        // =====================================
        // メールアドレス取得
        // =====================================

        String email =
            request.getParameter("email");


        // =====================================
        // 入力チェック
        // =====================================

        if (email == null
                || email.trim().isEmpty()) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "メールアドレスを入力してください。"
            );

            return;
        }


        email = email.trim();


        // =====================================
        // メールアドレス形式チェック
        // =====================================

        String emailPattern =
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

        if (!email.matches(emailPattern)) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "正しいメールアドレスを入力してください。"
            );

            return;
        }


        // =====================================
        // 現在のメールアドレスと同じか確認
        // =====================================

        if (email.equalsIgnoreCase(
                loginUser.getEmail())) {

            response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "現在と同じメールアドレスです。"
            );

            return;
        }


        // =====================================
        // UserDAOでDB更新
        // =====================================

        UserDAO dao =
            new UserDAO();

        boolean result =
            dao.updateEmail(
                loginUser.getAccountId(),
                email
            );


        // =====================================
        // 更新結果
        // =====================================

        if (result) {

            // セッション上のユーザー情報も更新
            loginUser.setEmail(email);

            session.setAttribute(
                "loginUser",
                loginUser
            );


            System.out.println(
                "================================="
            );

            System.out.println(
                "メールアドレス変更成功"
            );

            System.out.println(
                "account_id：" +
                loginUser.getAccountId()
            );

            System.out.println(
                "変更後メールアドレス：" +
                email
            );

            System.out.println(
                "================================="
            );


            response.setStatus(
                HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                "メールアドレスを更新しました。"
            );

        } else {

            System.err.println(
                "メールアドレス変更に失敗しました。"
            );

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                "メールアドレスの更新に失敗しました。"
            );
        }
    }
}
