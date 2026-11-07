<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.petshare.User" %>

<%
    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect("login.html");
        return;
    }

    String gender = loginUser.getGender();

if (gender == null) {
    gender = "";
}

// DBの値を画面表示用の日本語に変換
switch (gender) {
    case "male":
        gender = "男性";
        break;

    case "female":
        gender = "女性";
        break;

    case "other":
        gender = "その他";
        break;

    case "undisclosed":
        gender = "回答しない";
        break;
}

%>

<!DOCTYPE html>
<html lang="ja">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>性別 - ペットシェアアプリ</title>

    <link
        rel="stylesheet"
        href="account-gender.css"
    >

</head>

<body>

<header class="header">

    <button
        type="button"
        id="backButton"
        class="back-button"
    >
        ←
    </button>

    <h1>
        性別
    </h1>

</header>


<main class="main-content">

    <section class="form-card">

        <h2>
            性別の変更
        </h2>

        <p class="description">
            性別を選択してください。
        </p>


        <div class="gender-list">

            <label class="gender-option">

                <input
                    type="radio"
                    name="gender"
                    value="男性"
                    <%= "男性".equals(gender) ? "checked" : "" %>
                >

                <span>
                    男性
                </span>

            </label>


            <label class="gender-option">

                <input
                    type="radio"
                    name="gender"
                    value="女性"
                    <%= "女性".equals(gender) ? "checked" : "" %>
                >

                <span>
                    女性
                </span>

            </label>


            <label class="gender-option">

                <input
                    type="radio"
                    name="gender"
                    value="その他"
                    <%= "その他".equals(gender) ? "checked" : "" %>
                >

                <span>
                    その他
                </span>

            </label>


            <label class="gender-option">

                <input
                    type="radio"
                    name="gender"
                    value="回答しない"
                    <%= "回答しない".equals(gender) ? "checked" : "" %>
                >

                <span>
                    回答しない
                </span>

            </label>

        </div>


        <button
            type="button"
            id="saveButton"
            class="save-button"
        >
            保存する
        </button>

    </section>

</main>


<script src="account-gender.js"></script>

</body>

</html>