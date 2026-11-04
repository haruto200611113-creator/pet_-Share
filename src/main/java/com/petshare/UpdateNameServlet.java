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

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // ログイン情報を取得
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "ログインしてください。");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        // 入力された氏名を取得
        String name = request.getParameter("name");

        if (name == null || name.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "氏名を入力してください。");
            return;
        }

        name = name.trim();

        // データベースを更新
        UserDAO userDAO = new UserDAO();

        boolean result = userDAO.updateName(
                loginUser.getAccountId(),
                name
        );

        if (result) {

            // セッション内の氏名も更新
            loginUser.setName(name);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("success");

        } else {

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "氏名の更新に失敗しました。");
        }
    }
}