package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

/**
 * 新規登録処理
 */
@Override
protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    // =====================================
    // 文字コードの設定
    // =====================================
    request.setCharacterEncoding("UTF-8");


    // =====================================
    // sign.htmlから入力データを取得
    // =====================================

    String accountName =
            request.getParameter("accountName");

    String email =
            request.getParameter("email");

    String password =
            request.getParameter("password");

    String confirmPassword =
            request.getParameter("confirmPassword");

    String name =
            request.getParameter("name");

    String birthDate =
            request.getParameter("birthDate");

    String gender =
            request.getParameter("gender");

    String phone =
            request.getParameter("phone");

    String postalCode =
            request.getParameter("postalCode");

    String prefecture =
            request.getParameter("prefecture");

    String city =
            request.getParameter("city");

    String address =
            request.getParameter("address");

    String building =
            request.getParameter("building");


    // =====================================
    // 必須項目チェック
    // =====================================

    if (isEmpty(accountName)
            || isEmpty(email)
            || isEmpty(password)
            || isEmpty(confirmPassword)
            || isEmpty(name)
            || isEmpty(birthDate)
            || isEmpty(gender)
            || isEmpty(phone)
            || isEmpty(postalCode)
            || isEmpty(prefecture)
            || isEmpty(city)
            || isEmpty(address)) {

        response.sendRedirect(
                "sign.html?error=empty"
        );

        return;
    }


    // =====================================
    // パスワード一致チェック
    // =====================================

    if (!password.equals(confirmPassword)) {

        response.sendRedirect(
                "sign.html?error=password"
        );

        return;
    }


    // =====================================
    // Userオブジェクトを作成
    // =====================================

    User user = new User();

    user.setAccountName(accountName);

    user.setEmail(email);

    user.setPassword(password);

    user.setName(name);

    user.setBirthDate(birthDate);

    user.setGender(gender);

    user.setPhone(phone);

    user.setPostalCode(postalCode);

    user.setPrefecture(prefecture);

    user.setCity(city);

    user.setAddress(address);

    user.setBuilding(building);


    // =====================================
    // UserDAOを使ってデータベースへ登録
    // =====================================

    UserDAO dao =
            new UserDAO();

    boolean result =
            dao.insert(user);


    // =====================================
    // 登録結果
    // =====================================

    if (result) {

        // 登録成功
        response.sendRedirect(
                "login.html?register=success"
        );

    } else {

        // 登録失敗
        response.sendRedirect(
                "sign.html?error=database"
        );
    }
}


// =====================================
// 空文字チェック
// =====================================

private boolean isEmpty(String value) {

    return value == null
            || value.trim().isEmpty();
}
}
