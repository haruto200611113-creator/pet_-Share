package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 日本語文字化け防止
        request.setCharacterEncoding("UTF-8");

        // HTMLからデータを取得
        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");

        String phone =
                request.getParameter("phone");

        String address =
                request.getParameter("address");


        // Userオブジェクトを作成
        User user = new User(
                name,
                email,
                password,
                phone,
                address
        );


        // データベース登録
        UserDAO dao = new UserDAO();

        boolean result = dao.insert(user);


        // 登録結果
        if (result) {

            response.setContentType(
                    "text/html; charset=UTF-8"
            );

            response.getWriter().println("""
                <!DOCTYPE html>
                <html lang="ja">

                <head>
                    <meta charset="UTF-8">
                    <title>登録完了</title>
                </head>

                <body>

                    <h1>登録が完了しました！</h1>

                    <p>ペットシェアへようこそ！</p>

                    <a href="index.html">
                        トップページへ
                    </a>

                </body>

                </html>
            """);

        } else {

            response.setContentType(
                    "text/html; charset=UTF-8"
            );

            response.getWriter().println("""
                <h1>登録に失敗しました</h1>

                <a href="sign.html">
                    戻る
                </a>
            """);
        }
    }
}