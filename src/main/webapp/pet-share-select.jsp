<%@ page language="java"
    contentType="text/html; charset=UTF-8"
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

    Object accountIdObj =
            sessionObj.getAttribute("accountId");

    if (accountIdObj == null) {

        response.sendRedirect("login.html");
        return;
    }

    int accountId =
            Integer.parseInt(
                    accountIdObj.toString());


    // =====================================
    // 自分のペット一覧取得
    // =====================================

    PetDAO petDAO = new PetDAO();

    List<Pet> pets =
            petDAO.findByAccountId(accountId);
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
                repeat(
                    auto-fit,
                    minmax(280px, 1fr)
                );

            gap: 25px;
        }


        .pet-card {
            background: #ffffff;

            border-radius: 18px;

            padding: 20px;

            box-shadow:
                0 5px 20px
                rgba(0, 0, 0, 0.08);
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


        /* =====================================
           シェアするボタン
        ===================================== */

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


        /* =====================================
           現在シェア中
        ===================================== */

        .shared {
            margin-top: 15px;

            padding: 10px;

            border-radius: 8px;

            background: #eef5ee;

            color: #557058;

            text-align: center;

            font-weight: bold;
        }


        /* =====================================
           シェアをやめるボタン
        ===================================== */

        .stop-share-button {
            display: block;

            width: 100%;

            margin-top: 10px;

            padding: 13px;

            border: none;

            border-radius: 10px;

            background: #b85c5c;

            color: white;

            font-size: 16px;

            font-weight: bold;

            cursor: pointer;
        }


        .stop-share-button:hover {
            opacity: 0.85;
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
/* ========================================
   完了メッセージ
======================================== */

.change-complete-message {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    padding: 28px 50px;

    background: #ffffff;

    border-radius: 16px;

    color: #4b3b33;

    font-size: 24px;
    font-weight: 600;

    box-shadow: 0 6px 25px rgba(0, 0, 0, 0.18);

    z-index: 9999;

    animation: messageFadeIn 0.2s ease;
}

/* エラーメッセージ */

.change-error-message {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    padding: 28px 50px;

    background: #ffffff;

    border-radius: 16px;

    color: #b85c5c;

    font-size: 22px;
    font-weight: 600;

    box-shadow: 0 6px 25px rgba(0, 0, 0, 0.18);

    z-index: 9999;

    animation: messageFadeIn 0.2s ease;
}

@keyframes messageFadeIn {

    from {
        opacity: 0;
        transform: translate(-50%, -50%) scale(0.95);
    }

    to {
        opacity: 1;
        transform: translate(-50%, -50%) scale(1);
    }
}
    </style>

</head>


<body>

<%
    String shareStopSuccess =
            request.getParameter("shareStopSuccess");

    String shareError =
            request.getParameter("shareError");
%>

<% if ("true".equals(shareStopSuccess)) { %>

<div id="share-stop-success-message"
     class="change-complete-message">
    シェアを停止しました
</div>

<% } %>

<% if (shareError != null) { %>

<div id="share-error-message"
     class="change-error-message">
    <%= shareError %>
</div>

<% } %>
<div class="container">


    <!-- =====================================
         マイページへ戻る
    ===================================== -->

    <a href="mypage.jsp"
        class="back">

        ← マイページに戻る

    </a>


    <h1>
        ペットをシェアする
    </h1>


    <p class="description">
        シェアするペットを選択してください
    </p>


    <!-- =====================================
         ペットがない場合
    ===================================== -->

    <% if (pets == null || pets.isEmpty()) { %>


        <div class="empty">

            <h2>
                登録されているペットがありません
            </h2>


            <p>
                ペットを登録してから
                シェアすることができます。
            </p>


            <a href="pet-register.html"
                class="register-button">

                ペットを登録する

            </a>

        </div>


    <% } else { %>


        <!-- =====================================
             ペット一覧
        ===================================== -->

        <div class="pet-list">


            <% for (Pet pet : pets) { %>


                <div class="pet-card">


                    <!-- =====================================
                         ペット画像
                    ===================================== -->

                    <% if (pet.getPetImage() != null
                            && !pet.getPetImage().isBlank()) { %>


                        <img
                            src="<%= pet.getPetImage() %>"
                            alt="<%= pet.getPetName() %>"
                            class="pet-image">


                    <% } else { %>


                        <div
                            class="pet-image"
                            style="
                                display:flex;
                                align-items:center;
                                justify-content:center;
                                font-size:60px;
                            ">

                            🐾

                        </div>


                    <% } %>


                    <!-- =====================================
                         ペット名
                    ===================================== -->

                    <div class="pet-name">

                        <%= pet.getPetName() %>

                    </div>


                    <!-- =====================================
                         ペット情報
                    ===================================== -->

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


                    <!-- =====================================
                         シェア状態
                    ===================================== -->

                    <% if (pet.isShared()) { %>


                        <!-- =================================
                             シェア中
                        ================================= -->

                        <div class="shared">

                            ✓ 現在シェア中

                        </div>


                        <!-- =================================
                             シェアをやめる
                        ================================= -->

                        <form
                            action="pet-share-stop"
                            method="post"
                            onsubmit="
                                return confirm(
                                    'このペットのシェアをやめますか？'
                                );
                            ">

                            <input
                                type="hidden"
                                name="petId"
                                value="<%= pet.getPetId() %>">


                            <button
                                type="submit"
                                class="stop-share-button">

                                シェアをやめる

                            </button>

                        </form>


                    <% } else { %>


                        <!-- =================================
                             シェアしていない
                        ================================= -->

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
<script>
    setTimeout(function () {

        const successMessage =
            document.getElementById(
                "share-stop-success-message"
            );

        const errorMessage =
            document.getElementById(
                "share-error-message"
            );

        if (successMessage) {
            successMessage.style.display = "none";
        }

        if (errorMessage) {
            errorMessage.style.display = "none";
        }

        // URLからパラメータを削除
        const url =
            new URL(window.location.href);

        url.searchParams.delete(
            "shareStopSuccess"
        );

        url.searchParams.delete(
            "shareError"
        );

        window.history.replaceState(
            {},
            document.title,
            url.pathname
        );

    }, 3000);
</script>
</html>