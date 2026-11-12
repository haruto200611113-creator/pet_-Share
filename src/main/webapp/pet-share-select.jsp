<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.petshare.Pet"%>
<%@ page import="com.petshare.PetDAO"%>
<%@ page import="jakarta.servlet.http.HttpSession"%>

<%
    // =====================================
    // ログイン確認
    // =====================================

    HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null
            || sessionObj.getAttribute("loginUser") == null) {

        response.sendRedirect("login.html");
        return;
    }

    // =====================================
    // アカウントID取得
    // =====================================

    Object accountIdObj = sessionObj.getAttribute("accountId");

    if (accountIdObj == null) {

        response.sendRedirect("login.html");
        return;
    }

    int accountId = Integer.parseInt(accountIdObj.toString());

    // =====================================
    // 自分のペット一覧取得
    // =====================================

    PetDAO petDAO = new PetDAO();
    List<Pet> pets = petDAO.findByAccountId(accountId);
%>

<!DOCTYPE html>
<html lang="ja">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>ペットをシェアする</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family:
                "Noto Sans JP",
                sans-serif;

            background: #f8f6f3;
            color: #4b3b33;
        }

        .container {
            width: 90%;
            max-width: 900px;
            margin: 50px auto;
        }

        h1 {
            text-align: center;
            margin-bottom: 10px;
        }

        .description {
            text-align: center;
            margin-bottom: 35px;
            color: #777;
        }

        .back {
            display: inline-block;
            margin-bottom: 25px;
            color: #4b3b33;
            text-decoration: none;
        }

        .pet-list {
            display: grid;
            grid-template-columns:
                repeat(auto-fit, minmax(280px, 1fr));

            gap: 25px;
        }

        .pet-card {
            background: #ffffff;
            border-radius: 18px;
            padding: 20px;
            box-shadow:
                0 5px 20px rgba(0, 0, 0, 0.08);
        }

        .pet-image {
            width: 100%;
            height: 220px;
            object-fit: cover;
            border-radius: 14px;
            background: #eee;
            display: block;
        }

        .pet-name {
            font-size: 22px;
            font-weight: bold;
            margin-top: 15px;
        }

        .pet-info {
            margin-top: 8px;
            color: #777;
        }

        .share-button {
            display: block;
            width: 100%;
            margin-top: 20px;
            padding: 13px;
            border: none;
            border-radius: 10px;
            background: #6f8f72;
            color: white;
            text-align: center;
            text-decoration: none;
            font-size: 16px;
            cursor: pointer;
        }

        .share-button:hover {
            opacity: 0.85;
        }

        .shared {
            margin-top: 15px;
            padding: 10px;
            border-radius: 8px;
            background: #eef5ee;
            color: #557058;
            text-align: center;
        }

        .empty {
            text-align: center;
            background: white;
            padding: 50px 20px;
            border-radius: 18px;
        }

        .register-button {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 25px;
            border-radius: 10px;
            background: #6f8f72;
            color: white;
            text-decoration: none;
        }

    </style>

</head>

<body>

<div class="container">

    <a href="mypage.jsp" class="back">
        ← マイページに戻る
    </a>

    <h1>ペットをシェアする</h1>

    <p class="description">
        シェアするペットを選択してください
    </p>

    <% if (pets == null || pets.isEmpty()) { %>

        <div class="empty">

            <h2>登録されているペットがありません</h2>

            <p>
                ペットを登録してからシェアすることができます。
            </p>

            <a href="pet-register.html"
                class="register-button">
                ペットを登録する
            </a>

        </div>

    <% } else { %>

        <div class="pet-list">

            <% for (Pet pet : pets) { %>

                <div class="pet-card">

                    <% if (pet.getPetImage() != null
                            && !pet.getPetImage().isBlank()) { %>

                        <img
                            src="<%= pet.getPetImage() %>"
                            alt="<%= pet.getPetName() %>"
                            class="pet-image">

                    <% } else { %>

                        <div class="pet-image"
                            style="
                                display:flex;
                                align-items:center;
                                justify-content:center;
                                font-size:60px;
                            ">
                            🐾
                        </div>

                    <% } %>

                    <div class="pet-name">
                        <%= pet.getPetName() %>
                    </div>

                    <div class="pet-info">

                        種類：
                        <%= pet.getPetType() %>

                        <% if (pet.getBreed() != null
                                && !pet.getBreed().isBlank()) { %>

                            <br>
                            品種：
                            <%= pet.getBreed() %>

                        <% } %>

                    </div>

                    <% if (pet.isShared()) { %>

                        <div class="shared">
                            ✓ 現在シェア中
                        </div>

                    <% } else { %>

                        <a
                            href="pet-share.html?petId=<%= pet.getPetId() %>"
                            class="share-button">

                            このペットをシェアする

                        </a>

                    <% } %>

                </div>

            <% } %>

        </div>

    <% } %>

</div>

</body>

</html>
