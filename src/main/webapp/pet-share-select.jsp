<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.petshare.Pet"%>
<%@ page import="com.petshare.PetDAO"%>
<%@ page import="jakarta.servlet.http.HttpSession"%>

<%
    HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null
            || sessionObj.getAttribute("loginUser") == null) {

        response.sendRedirect("login.html");
        return;
    }

    Object accountIdObj =
            sessionObj.getAttribute("accountId");

    if (accountIdObj == null) {

        response.sendRedirect("login.html");
        return;
    }

    int accountId =
            Integer.parseInt(accountIdObj.toString());

    PetDAO petDAO = new PetDAO();

    List<Pet> pets =
            petDAO.findByAccountId(accountId);
%>

<!DOCTYPE html>
<html lang="ja">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>ペットをシェアする</title>

    <!-- 外部CSS -->
    <link
        rel="stylesheet"
        href="pet-share-select.css"
    >

</head>


<body>


<div class="container">


    <!-- 戻る -->

    <a
        href="mypage.jsp"
        class="back"
    >
        ← マイページに戻る
    </a>


    <!-- タイトル -->

    <h1>
        ペットをシェアする
    </h1>


    <p class="description">
        シェアするペットを選択してください
    </p>



    <!-- ========================================
         ペットがいない場合
    ========================================= -->

    <% if (pets == null || pets.isEmpty()) { %>

        <div class="empty-message">

            <div class="empty-icon">
                🐾
            </div>

            <p>
                登録されているペットがありません。
            </p>

            <a
                href="pet-register.html"
                class="register-button"
            >
                ペットを登録する
            </a>

        </div>


    <% } else { %>


        <!-- ========================================
             ペット一覧
        ========================================= -->

        <div class="pet-list">


            <% for (Pet pet : pets) { %>


            <div class="pet-card">


                <!-- ペット画像 -->

                <div class="pet-image-area">

                    <% if (
                        pet.getPetImage() != null
                        &&
                        !pet.getPetImage().isBlank()
                    ) { %>

                        <img
                            src="<%= pet.getPetImage() %>"
                            alt="<%= pet.getPetName() %>"
                            class="pet-image"
                        >

                    <% } else { %>

                        <div class="no-image">
                            🐾
                        </div>

                    <% } %>

                </div>



                <!-- ペット情報 -->

                <div class="pet-info">

                    <h2>
                        <%= pet.getPetName() %>
                    </h2>


                    <p>
                        種類：
                        <%= pet.getPetType() %>
                    </p>


                    <% if (
                        pet.getBreed() != null
                        &&
                        !pet.getBreed().isBlank()
                    ) { %>

                        <p>
                            品種：
                            <%= pet.getBreed() %>
                        </p>

                    <% } %>



                    <!-- ==================================
                         シェア中の場合
                    =================================== -->

                    <% if (pet.isShared()) { %>


                        <div class="shared-status">
                            ✓ 現在シェア中
                        </div>


                        <!--
                            ここではconfirm()を使わない
                            JSで独自確認モーダルを表示する
                        -->

                        <form
                            action="pet-share-stop"
                            method="post"
                            class="stop-share-form"
                        >

                            <input
                                type="hidden"
                                name="petId"
                                value="<%= pet.getPetId() %>"
                            >


                            <button
                                type="submit"
                                class="stop-share-button"
                            >
                                シェアをやめる
                            </button>

                        </form>


                    <% } else { %>


                        <!-- ==================================
                             シェアしていない場合
                        =================================== -->

                        <a
                            href="pet-share.html?petId=<%= pet.getPetId() %>"
                            class="share-button"
                        >
                            このペットをシェアする
                        </a>


                    <% } %>


                </div>

            </div>


            <% } %>

        </div>


    <% } %>


</div>



<!-- ========================================
     シェア停止確認モーダル
========================================= -->

<div
    id="share-stop-confirm-modal"
    class="confirm-overlay"
    style="display: none;"
>


    <div
        class="confirm-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="share-stop-confirm-title"
    >


        <div class="confirm-icon">
            🐾
        </div>


        <h2 id="share-stop-confirm-title">
            シェアをやめますか？
        </h2>


        <p>
            このペットのシェアを<br>
            停止してもよろしいですか？
        </p>


        <div class="confirm-buttons">


            <button
                type="button"
                id="share-stop-cancel-button"
                class="cancel-button"
            >
                キャンセル
            </button>


            <button
                type="button"
                id="share-stop-confirm-button"
                class="confirm-button"
            >
                シェアをやめる
            </button>


        </div>

    </div>

</div>



<!-- 外部JavaScript -->

<script src="pet-share-select.js"></script>


</body>

</html>