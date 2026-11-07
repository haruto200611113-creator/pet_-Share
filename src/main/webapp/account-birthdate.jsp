<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.petshare.User" %>

<%
    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect("login.html");
        return;
    }

    String birthDate = loginUser.getBirthDate();

    String birthYear = "";
    String birthMonth = "";
    String birthDay = "";

    if (birthDate != null && !birthDate.isEmpty()) {

        String[] dateParts = birthDate.split("-");

        if (dateParts.length == 3) {
            birthYear = dateParts[0];
            birthMonth = dateParts[1];
            birthDay = dateParts[2];
        }
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

    <title>生年月日 - ペットシェアアプリ</title>

    <link
        rel="stylesheet"
        href="account-birthdate.css"
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
        生年月日
    </h1>

</header>


<main class="main-content">

    <section class="form-card">

        <h2>
            生年月日の変更
        </h2>

        <p class="description">
            登録する生年月日を選択してください。
        </p>


        <div class="form-group">

            <label>
                生年月日
            </label>


            <div class="birthdate-inputs">

                <div class="birthdate-field">

                    <input
                        type="number"
                        id="birthYear"
                        value="<%= birthYear %>"
                        min="1900"
                        max="2100"
                        placeholder="YYYY"
                    >

                    <span>
                        年
                    </span>

                </div>


                <div class="birthdate-field">

                    <input
                        type="number"
                        id="birthMonth"
                        value="<%= birthMonth %>"
                        min="1"
                        max="12"
                        placeholder="MM"
                    >

                    <span>
                        月
                    </span>

                </div>


                <div class="birthdate-field">

                    <input
                        type="number"
                        id="birthDay"
                        value="<%= birthDay %>"
                        min="1"
                        max="31"
                        placeholder="DD"
                    >

                    <span>
                        日
                    </span>

                </div>

            </div>

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


<script src="account-birthdate.js"></script>

</body>

</html>