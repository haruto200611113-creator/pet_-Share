package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateNameServlet")
public class UpdateNameServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // UTF-8設定
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        System.out.println("===== 名前変更処理開始 =====");

        try {

            // セッション取得
            HttpSession session = request.getSession(false);

            // 未ログインチェック
            if (session == null
                    || session.getAttribute("loginUser") == null) {

                System.out.println("ログインしていません。");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"ログインしてください。\"}");

                return;
            }

            // ログイン中のUserを取得
            User loginUser = (User) session.getAttribute("loginUser");

            // アカウントID取得
            int accountId = loginUser.getAccountId();

            // 姓・名を取得
            String lastName = request.getParameter("lastName");

            String firstName = request.getParameter("firstName");

            // 前後の空白を削除
            if (lastName != null) {
                lastName = lastName.trim();
            }

            if (firstName != null) {
                firstName = firstName.trim();
            }

            // 入力確認ログ
            System.out.println("アカウントID：" + accountId);
            System.out.println("姓：" + lastName);
            System.out.println("名：" + firstName);

            // 必須チェック
            if (isEmpty(lastName)
                    || isEmpty(firstName)) {

                System.out.println("姓または名が未入力です。");

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"姓と名を入力してください。\"}");

                return;
            }

            // DAO
            UserDAO dao = new UserDAO();

            // データベース更新
            boolean result = dao.updateName(
                    accountId,
                    lastName,
                    firstName);

            System.out.println(
                    "データベース更新結果：" + result);

            // 更新成功
            if (result) {

                // セッションのUser情報も更新
                loginUser.setLastName(lastName);
                loginUser.setFirstName(firstName);

                session.setAttribute(
                        "loginUser",
                        loginUser);

                System.out.println("名前変更成功");

                response.getWriter().write(
                        "{\"success\":true,\"message\":\"名前を変更しました。\"}");

            } else {

                System.out.println("名前変更失敗");

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"名前の変更に失敗しました。\"}");
            }

        } catch (Exception e) {

            System.err.println(
                    "名前変更処理でエラーが発生しました。");

            e.printStackTrace();

            if (!response.isCommitted()) {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                response.getWriter().write(
                        "{\"success\":false,\"message\":\"システムエラーが発生しました。\"}");
            }

        } finally {

            System.out.println("===== 名前変更処理終了 =====");
        }
    }

    /**
     * 空文字チェック
     */
    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}
