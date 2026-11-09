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

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("===== 新規登録処理開始 =====");

        try {

            // 入力データ取得
            String accountName = request.getParameter("accountName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            String lastName = request.getParameter("lastName");
String firstName = request.getParameter("firstName");
            String birthDate = request.getParameter("birthDate");
            String gender = request.getParameter("gender");

            String phone = request.getParameter("phone");
            String postalCode = request.getParameter("postalCode");
            String prefecture = request.getParameter("prefecture");
            String city = request.getParameter("city");
            String address = request.getParameter("address");
            String building = request.getParameter("building");

            // 入力確認ログ
            System.out.println("アカウント名：" + accountName);
            System.out.println("メールアドレス：" + email);
            System.out.println("姓：" + lastName);
System.out.println("名：" + firstName);
            System.out.println("生年月日：" + birthDate);
            System.out.println("性別：" + gender);

            // 必須項目チェック
            if (isEmpty(accountName)
                    || isEmpty(email)
                    || isEmpty(password)
                    || isEmpty(confirmPassword)
                    || isEmpty(lastName)
                    || isEmpty(firstName)
                    || isEmpty(birthDate)
                    || isEmpty(gender)
                    || isEmpty(phone)
                    || isEmpty(postalCode)
                    || isEmpty(prefecture)
                    || isEmpty(city)
                    || isEmpty(address)) {

                System.out.println("必須項目が未入力です。");

                response.sendRedirect(
                        request.getContextPath() + "/sign.html?error=empty"
                );
                return;
            }

            // パスワード一致確認
            if (!password.equals(confirmPassword)) {

                System.out.println("パスワードが一致しません。");

                response.sendRedirect(
                        request.getContextPath() + "/sign.html?error=password"
                );
                return;
            }

            // パスワード文字数確認
            if (password.length() < 8) {

                System.out.println("パスワードが8文字未満です。");

                response.sendRedirect(
                        request.getContextPath() + "/sign.html?error=passwordLength"
                );
                return;
            }

            // Userオブジェクト作成
            User user = new User();

            user.setAccountName(accountName);
            user.setEmail(email);
            user.setPassword(password);

            user.setLastName(lastName);
            user.setFirstName(firstName);
            user.setBirthDate(birthDate);
            user.setGender(gender);

            user.setPhone(phone);
            user.setPostalCode(postalCode);
            user.setPrefecture(prefecture);
            user.setCity(city);
            user.setAddress(address);
            user.setBuilding(building);

            // データベース登録
            System.out.println("UserDAO登録処理開始");

            UserDAO dao = new UserDAO();

            boolean result = dao.insert(user);

            System.out.println("データベース登録結果：" + result);

            // 登録結果判定
            if (result) {

                System.out.println("新規登録成功");

              response.sendRedirect("sign.html?register=success");

            } else {

                System.out.println("新規登録失敗");

                response.sendRedirect(
                        request.getContextPath() + "/sign.html?error=database"
                );
            }

        } catch (Exception e) {

            System.err.println("新規登録処理でエラーが発生しました。");
            e.printStackTrace();

            if (!response.isCommitted()) {
                response.sendRedirect(
                        request.getContextPath() + "/sign.html?error=system"
                );
            }

        } finally {

            System.out.println("===== 新規登録処理終了 =====");
        }
    }

    // 空文字チェック
    private boolean isEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }
}