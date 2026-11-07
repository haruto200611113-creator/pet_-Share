<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="com.petshare.User" %>

<%
    // セッションからログインユーザー情報を取得
    User loginUser = (User) session.getAttribute("loginUser");

    // ログインしていない場合はログイン画面へ戻す
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }
%>

<%!
    // HTML特殊文字をエスケープする処理
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    // 性別のDB値を画面表示用に変換
    private String displayGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return "未登録";
        }
        switch (gender.trim().toLowerCase()) {
            case "male": return "男性";
            case "female": return "女性";
            case "other": return "その他";
            case "undisclosed": return "回答しない";
            default: return gender;
        }
    }

    // 空欄を「未登録」として表示
    private String displayValue(String value) {
        return (value == null || value.trim().isEmpty()) ? "未登録" : value;
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

    <title>マイページ - ペットシェアアプリ</title>

    <link
        rel="stylesheet"
        href="mypage.css"
    >
</head>

<body>


<!-- ========================================
     ヘッダー
========================================= -->

<header class="header">

    <button
        type="button"
        id="backButton"
        class="back-button"
        aria-label="ホームに戻る"
    >
        ←
    </button>

    <h1>
        マイページ
    </h1>

</header>



<!-- ========================================
     メイン
========================================= -->

<main class="main">


    <!-- ====================================
         プロフィール
    ===================================== -->

    <section class="profile-card">

        <!-- プロフィール画像 -->

        <div class="profile-icon">

            <img
                id="profileImage"
                src=""
                alt="プロフィール画像"
            >

            <span id="profileDefaultIcon">
                🐾
            </span>

        </div>


        <!-- プロフィール情報 -->

        <div class="profile-info">

           <h2 id="userName" data-server-rendered="true">
    <%= escapeHtml(loginUser.getAccountName()) %>
</h2>

<p id="userArea" data-server-rendered="true">
    📍 <%= escapeHtml(loginUser.getPrefecture()) %>
    <%= escapeHtml(loginUser.getCity()) %>
</p>

        </div>


        <!-- プロフィール編集 -->

        <a
            href="profile-edit.html"
            class="edit-button"
        >
            編集
        </a>

    </section>



    <!-- ====================================
         登録情報
    ===================================== -->

    <section class="section">

        <h2 class="section-title">登録情報</h2>

        <div class="registration-card">

            <div class="registration-row">
                <span class="registration-label">アカウント名</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getAccountName())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">メールアドレス</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getEmail())) %></span>
            </div>

            <div class="registration-row">
    <span class="registration-label">氏名</span>
    <span class="registration-value">
        <%= escapeHtml(displayValue(loginUser.getLastName())) %>
        <%= escapeHtml(displayValue(loginUser.getFirstName())) %>
    </span>
</div>

            <div class="registration-row">
                <span class="registration-label">生年月日</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getBirthDate())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">性別</span>
                <span class="registration-value"><%= escapeHtml(displayGender(loginUser.getGender())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">電話番号</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getPhone())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">郵便番号</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getPostalCode())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">都道府県</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getPrefecture())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">市区町村</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getCity())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">番地</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getAddress())) %></span>
            </div>

            <div class="registration-row">
                <span class="registration-label">建物名</span>
                <span class="registration-value"><%= escapeHtml(displayValue(loginUser.getBuilding())) %></span>
            </div>

        </div>

    </section>



    <!-- ====================================
         利用状況
    ===================================== -->

    <section class="section">

        <h2 class="section-title">
            利用状況
        </h2>


        <div class="status-grid">


            <!-- ====================================
                 自分の投稿
            ===================================== -->

            <a
                href="my-posts.html"
                class="status-card"
            >

                <span class="status-icon">
                    🐶
                </span>


                <div>

                    <span class="status-label">
                        自分の投稿
                    </span>

                    <strong id="postCount">
                        0
                    </strong>

                    <span class="unit">
                        件
                    </span>

                </div>


                <span class="arrow">
                    ›
                </span>

            </a>



            <!-- ====================================
                 いいね
            ===================================== -->

            <a
                href="likes.html"
                class="status-card"
            >

                <span class="status-icon">
                    ❤️
                </span>


                <div>

                    <span class="status-label">
                        いいねしたペット
                    </span>

                    <strong id="likeCount">
                        0
                    </strong>

                    <span class="unit">
                        件
                    </span>

                </div>


                <span class="arrow">
                    ›
                </span>

            </a>



            <!-- ====================================
                 シェア履歴
            ===================================== -->

            <a
                href="share-history.html"
                class="status-card"
            >

                <span class="status-icon">
                    📅
                </span>


                <div>

                    <span class="status-label">
                        シェア履歴
                    </span>

                    <strong id="shareCount">
                        0
                    </strong>

                    <span class="unit">
                        件
                    </span>

                </div>


                <span class="arrow">
                    ›
                </span>

            </a>



            <!-- ====================================
                 申し込み状況
            ===================================== -->

            <a
                href="application-status.html"
                class="status-card"
            >

                <span class="status-icon">
                    📝
                </span>


                <div>

                    <span class="status-label">
                        申し込み状況
                    </span>

                    <strong id="applicationCount">
                        0
                    </strong>

                    <span class="unit">
                        件
                    </span>

                </div>


                <span class="arrow">
                    ›
                </span>

            </a>


        </div>

    </section>



    <!-- ====================================
         その他
    ===================================== -->

    <section class="section">

        <h2 class="section-title">
            その他
        </h2>


        <div class="menu-card">


            <!-- ====================================
                 設定
            ===================================== -->

            <a
                href="settings.html"
                class="menu-row"
            >

                <span class="menu-icon">
                    ⚙️
                </span>

                <span class="menu-text">
                    設定
                </span>

                <span class="arrow">
                    ›
                </span>

            </a>



            <!-- ====================================
                 利用規約
            ===================================== -->

            <a
                href="terms.html"
                class="menu-row"
                id="termsLink"
            >

                <span class="menu-icon">
                    📄
                </span>

                <span class="menu-text">
                    利用規約
                </span>

                <span class="arrow">
                    ›
                </span>

            </a>



            <!-- ====================================
                 プライバシーポリシー
            ===================================== -->

            <a
                href="privacy.html"
                class="menu-row"
                id="privacyLink"
            >

                <span class="menu-icon">
                    🔒
                </span>

                <span class="menu-text">
                    プライバシーポリシー
                </span>

                <span class="arrow">
                    ›
                </span>

            </a>


        </div>

    </section>


</main>



<!-- ========================================
     下部ナビ
========================================= -->

<nav class="bottom-nav">


    <!-- ホーム -->

    <a
        href="home.html"
        class="nav-item"
        data-page="home"
    >

        <span class="nav-icon">
            🏠
        </span>

        <span>
            ホーム
        </span>

    </a>



    <!-- いいね -->

    <a
        href="likes.html"
        class="nav-item"
        data-page="like"
    >

        <span class="nav-icon">
            ❤️
        </span>

        <span>
            いいね
        </span>

    </a>



    <!-- チャット -->

    <a
        href="chat.html"
        class="nav-item"
        data-page="chat"
    >

        <span class="nav-icon">
            💬
        </span>

        <span>
            チャット
        </span>

    </a>



    <!-- アカウント -->

    <a
    href="mypage.jsp"
    class="nav-item active"
    data-page="account"
>

        <span class="nav-icon">
            👤
        </span>

        <span>
            アカウント
        </span>

    </a>


</nav>



<script src="mypage.js"></script>

</body>

</html>