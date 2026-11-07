<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.petshare.User" %>

<%
    // ログイン中のユーザーを取得
    User loginUser = (User) session.getAttribute("loginUser");

    // ログインしていない場合はログイン画面へ
    if (loginUser == null) {
        response.sendRedirect("login.html");
        return;
    }

    // 現在のメールアドレスを取得
    String currentEmail = loginUser.getEmail();

    if (currentEmail == null) {
        currentEmail = "";
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

    <title>メールアドレス - ペットシェアアプリ</title>

    <link
        rel="stylesheet"
        href="account-email.css"
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
        メールアドレス
    </h1>

</header>


<main class="main-content">

    <section class="form-card">

        <h2>
            メールアドレス変更
        </h2>

        <p class="description">
            登録するメールアドレスを入力してください。
        </p>

        <div class="form-group">

            <label for="currentEmail">
                現在のメールアドレス
            </label>

            <input
                type="email"
                id="currentEmail"
                value="<%= currentEmail %>"
                readonly
            >

        </div>


        <div class="form-group">

            <label for="email">
                新しいメールアドレス
            </label>

            <input
                type="email"
                id="email"
                placeholder="example@example.com"
            >

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


<script src="account-email.js"></script>

</body>

</html>
