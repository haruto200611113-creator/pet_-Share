<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.petshare.User" %>
<%
    // ログイン情報を取得
    User loginUser = (User) session.getAttribute("loginUser");

    // ログインしていない場合
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 現在の氏名を取得
    String currentName = loginUser.getName();

    if (currentName == null) {
        currentName = "";
    }
    System.out.println("氏名確認：" + loginUser.getName());

    // HTMLへの出力時に特殊文字をエスケープ
    String escapedName = currentName
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
%>
<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>氏名 - ペットシェアアプリ</title>

    <link rel="stylesheet" href="account-name.css">
</head>

<body>

<header class="header">

    <button
        type="button"
        id="backButton"
        class="back-button">
        ←
    </button>

    <h1>氏名</h1>

</header>

<main class="main-content">

    <section class="form-card">

        <h2>氏名の変更</h2>

        <p class="description">
            本人確認などで使用する氏名を入力してください。
        </p>

        <div class="form-group">

            <label for="name">氏名</label>

            <input
                type="text"
                id="name"
                name="name"
                value="<%= escapedName %>"
                placeholder="例：山田 太郎"
                required
            >

        </div>

        <button
            type="button"
            id="saveButton"
            class="save-button">
            保存する
        </button>

    </section>

</main>

<script src="account-name.js"></script>

</body>
</html>