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

    request.setCharacterEncoding("UTF-8");

    String email = request.getParameter("email");
    String password = request.getParameter("password");

    // 入力チェック
    if (email == null || email.trim().isEmpty()
            || password == null || password.isEmpty()) {

        response.sendRedirect(
            request.getContextPath() + "/login.html?error=empty"
        );
        return;
    }

    try {

        UserDAO userDAO = new UserDAO();

        // ① ログイン認証
        User loginUser = userDAO.login(email.trim(), password);

        // 認証失敗
        if (loginUser == null) {

            response.sendRedirect(
                request.getContextPath() + "/login.html?error=invalid"
            );
            return;
        }

        // ② アカウントIDから登録情報を取得
        User fullUser = userDAO.findById(loginUser.getAccountId());

        // ユーザー情報取得失敗
        if (fullUser == null) {

            response.sendRedirect(
                request.getContextPath() + "/login.html?error=system"
            );
            return;
        }

        System.out.println("===== LoginServlet 実行確認 =====");
        System.out.println("メールアドレス：" + email);
        System.out.println("ログイン認証成功");
        System.out.println("移動先：home.html");
        // ③ 古いセッションを破棄
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        // ④ 新しいセッションを作成
        HttpSession session = request.getSession(true);

        // ⑤ ユーザー情報を保存
        session.setAttribute("loginUser", fullUser);

        // セッション有効期限30分
        session.setMaxInactiveInterval(30 * 60);

        // ⑥ マイページへ移動
        response.sendRedirect(
            request.getContextPath() + "/home.html"
        );

    } catch (Exception e) {

        e.printStackTrace();

        response.sendRedirect(
            request.getContextPath() + "/login.html?error=system"
        );
    }
}

@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {

    response.sendRedirect(
        request.getContextPath() + "/login.html"
    );
}


}
