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


    <!-- ペット追加 -->

    <div class="add-pet-area">

        <a
            href="pet-register.html"
            class="add-pet-button"
        >
            ＋ ペットを追加する
        </a>

    </div>


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
                            id="stop-share-form-<%= pet.getPetId() %>"
                        >

                            <input
                                type="hidden"
                                name="petId"
                                value="<%= pet.getPetId() %>"
                            >

                            <button
                                type="button"
                                class="stop-share-button"
                                onclick="openShareStopModal(this)"
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


                    <!-- ==================================
                         ペット削除
                    =================================== -->

                    <form
                        action="pet-delete"
                        method="post"
                        class="delete-pet-form"
                        onsubmit="return openDeleteConfirm(this);"
                    >

                        <input
                            type="hidden"
                            name="petId"
                            value="<%= pet.getPetId() %>"
                        >

                        <button
                            type="submit"
                            class="delete-pet-button"
                        >
                            ペットを削除
                        </button>

                    </form>


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


<!-- ========================================
     ペット削除確認モーダル
========================================= -->

<div
    id="pet-delete-confirm-modal"
    class="confirm-overlay"
    style="display: none;"
>

    <div
        class="confirm-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="pet-delete-confirm-title"
    >

        <div class="confirm-icon">
            🗑️
        </div>


        <h2 id="pet-delete-confirm-title">
            ペット情報を削除しますか？
        </h2>


        <p>
            このペットの登録情報を<br>
            削除してもよろしいですか？
        </p>


        <div class="confirm-buttons">


            <button
                type="button"
                id="pet-delete-cancel-button"
                class="cancel-button"
            >
                キャンセル
            </button>


            <button
                type="button"
                id="pet-delete-confirm-button"
                class="confirm-button delete-confirm-button"
            >
                ペットを削除
            </button>


        </div>


    </div>

</div>


<!-- ========================================
     ペット削除用JavaScript
========================================= -->

<script>

    let deleteTargetForm = null;


    function openDeleteConfirm(form) {

        deleteTargetForm = form;

        const modal =
            document.getElementById(
                "pet-delete-confirm-modal");

        modal.style.display = "flex";

        return false;
    }


    document.addEventListener(
        "DOMContentLoaded",
        function () {

            const modal =
                document.getElementById(
                    "pet-delete-confirm-modal");

            const cancelButton =
                document.getElementById(
                    "pet-delete-cancel-button");

            const confirmButton =
                document.getElementById(
                    "pet-delete-confirm-button");


            // =====================================
            // キャンセル
            // =====================================

            cancelButton.addEventListener(
                "click",
                function () {

                    modal.style.display = "none";

                    deleteTargetForm = null;

                });


            // =====================================
            // 削除実行
            // =====================================

            confirmButton.addEventListener(
                "click",
                function () {

                    if (deleteTargetForm !== null) {

                        modal.style.display = "none";

                        deleteTargetForm.submit();

                    }

                });


            // =====================================
            // モーダル外側クリック
            // =====================================

            modal.addEventListener(
                "click",
                function (event) {

                    if (event.target === modal) {

                        modal.style.display = "none";

                        deleteTargetForm = null;

                    }

                });

        });

</script>


<!-- ========================================
     ペット削除ボタン用CSS
========================================= -->

<style>

    .delete-pet-form {
        margin-top: 12px;
    }


    .delete-pet-button {

        width: 100%;

        padding: 10px 16px;

        border: 1px solid #d9534f;

        border-radius: 8px;

        background: #fff;

        color: #d9534f;

        font-size: 14px;

        font-weight: bold;

        cursor: pointer;

        transition:
            background 0.2s,
            color 0.2s,
            transform 0.1s;

    }


    .delete-pet-button:hover {

        background: #d9534f;

        color: #fff;

    }


    .delete-pet-button:active {

        transform: scale(0.98);

    }


    .delete-confirm-button {

        background: #d9534f !important;

    }


    .delete-confirm-button:hover {

        background: #c9302c !important;

    }

</style>


<!-- 外部JavaScript -->

<script src="pet-share-select.js"></script>


</body>

</html>