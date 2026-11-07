<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.petshare.User" %>

<%
    // ==========================================
    // ログインユーザー確認
    // ==========================================

    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect("login.html");
        return;
    }


    // ==========================================
    // 住所情報取得
    // ==========================================

    String postalCode = loginUser.getPostalCode();
    String prefecture = loginUser.getPrefecture();
    String city = loginUser.getCity();
    String address = loginUser.getAddress();
    String building = loginUser.getBuilding();


    // ==========================================
    // null対策
    // ==========================================

    if (postalCode == null) {
        postalCode = "";
    }

    if (prefecture == null) {
        prefecture = "";
    }

    if (city == null) {
        city = "";
    }

    if (address == null) {
        address = "";
    }

    if (building == null) {
        building = "";
    }


    // ==========================================
    // HTMLエスケープ
    // ==========================================

    String escapedPostalCode =
        postalCode
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");

    String escapedPrefecture =
        prefecture
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");

    String escapedCity =
        city
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");

    String escapedAddress =
        address
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");

    String escapedBuilding =
        building
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
%>

<!DOCTYPE html>
<html lang="ja">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>住所 - ペットシェアアプリ</title>

    <link
        rel="stylesheet"
        href="account-address.css"
    >

</head>


<body>


<header class="header">

    <button
        type="button"
        id="backButton"
        class="back-button"
        aria-label="戻る"
    >
        ←
    </button>

    <h1>住所</h1>

</header>



<main class="main-content">

    <section class="form-card">

        <h2>住所情報</h2>

        <p class="description">
            シェア利用時などに使用する住所を登録してください。
        </p>


        <!-- ==========================================
             郵便番号
        =========================================== -->

        <div class="form-group">

            <label for="postalCode">
                郵便番号
            </label>

            <input
                type="text"
                id="postalCode"
                placeholder="例：750-0000"
                inputmode="numeric"
                value="<%= escapedPostalCode %>"
            >

        </div>



        <!-- ==========================================
             都道府県
        =========================================== -->

        <div class="form-group">

            <label for="prefecture">
                都道府県
            </label>

            <select id="prefecture">

                <option value="">
                    選択してください
                </option>

                <option value="北海道"
                    <%= "北海道".equals(prefecture) ? "selected" : "" %>>
                    北海道
                </option>

                <option value="青森県"
                    <%= "青森県".equals(prefecture) ? "selected" : "" %>>
                    青森県
                </option>

                <option value="岩手県"
                    <%= "岩手県".equals(prefecture) ? "selected" : "" %>>
                    岩手県
                </option>

                <option value="宮城県"
                    <%= "宮城県".equals(prefecture) ? "selected" : "" %>>
                    宮城県
                </option>

                <option value="秋田県"
                    <%= "秋田県".equals(prefecture) ? "selected" : "" %>>
                    秋田県
                </option>

                <option value="山形県"
                    <%= "山形県".equals(prefecture) ? "selected" : "" %>>
                    山形県
                </option>

                <option value="福島県"
                    <%= "福島県".equals(prefecture) ? "selected" : "" %>>
                    福島県
                </option>

                <option value="茨城県"
                    <%= "茨城県".equals(prefecture) ? "selected" : "" %>>
                    茨城県
                </option>

                <option value="栃木県"
                    <%= "栃木県".equals(prefecture) ? "selected" : "" %>>
                    栃木県
                </option>

                <option value="群馬県"
                    <%= "群馬県".equals(prefecture) ? "selected" : "" %>>
                    群馬県
                </option>

                <option value="埼玉県"
                    <%= "埼玉県".equals(prefecture) ? "selected" : "" %>>
                    埼玉県
                </option>

                <option value="千葉県"
                    <%= "千葉県".equals(prefecture) ? "selected" : "" %>>
                    千葉県
                </option>

                <option value="東京都"
                    <%= "東京都".equals(prefecture) ? "selected" : "" %>>
                    東京都
                </option>

                <option value="神奈川県"
                    <%= "神奈川県".equals(prefecture) ? "selected" : "" %>>
                    神奈川県
                </option>

                <option value="新潟県"
                    <%= "新潟県".equals(prefecture) ? "selected" : "" %>>
                    新潟県
                </option>

                <option value="富山県"
                    <%= "富山県".equals(prefecture) ? "selected" : "" %>>
                    富山県
                </option>

                <option value="石川県"
                    <%= "石川県".equals(prefecture) ? "selected" : "" %>>
                    石川県
                </option>

                <option value="福井県"
                    <%= "福井県".equals(prefecture) ? "selected" : "" %>>
                    福井県
                </option>

                <option value="山梨県"
                    <%= "山梨県".equals(prefecture) ? "selected" : "" %>>
                    山梨県
                </option>

                <option value="長野県"
                    <%= "長野県".equals(prefecture) ? "selected" : "" %>>
                    長野県
                </option>

                <option value="岐阜県"
                    <%= "岐阜県".equals(prefecture) ? "selected" : "" %>>
                    岐阜県
                </option>

                <option value="静岡県"
                    <%= "静岡県".equals(prefecture) ? "selected" : "" %>>
                    静岡県
                </option>

                <option value="愛知県"
                    <%= "愛知県".equals(prefecture) ? "selected" : "" %>>
                    愛知県
                </option>

                <option value="三重県"
                    <%= "三重県".equals(prefecture) ? "selected" : "" %>>
                    三重県
                </option>

                <option value="滋賀県"
                    <%= "滋賀県".equals(prefecture) ? "selected" : "" %>>
                    滋賀県
                </option>

                <option value="京都府"
                    <%= "京都府".equals(prefecture) ? "selected" : "" %>>
                    京都府
                </option>

                <option value="大阪府"
                    <%= "大阪府".equals(prefecture) ? "selected" : "" %>>
                    大阪府
                </option>

                <option value="兵庫県"
                    <%= "兵庫県".equals(prefecture) ? "selected" : "" %>>
                    兵庫県
                </option>

                <option value="奈良県"
                    <%= "奈良県".equals(prefecture) ? "selected" : "" %>>
                    奈良県
                </option>

                <option value="和歌山県"
                    <%= "和歌山県".equals(prefecture) ? "selected" : "" %>>
                    和歌山県
                </option>

                <option value="鳥取県"
                    <%= "鳥取県".equals(prefecture) ? "selected" : "" %>>
                    鳥取県
                </option>

                <option value="島根県"
                    <%= "島根県".equals(prefecture) ? "selected" : "" %>>
                    島根県
                </option>

                <option value="岡山県"
                    <%= "岡山県".equals(prefecture) ? "selected" : "" %>>
                    岡山県
                </option>

                <option value="広島県"
                    <%= "広島県".equals(prefecture) ? "selected" : "" %>>
                    広島県
                </option>

                <option value="山口県"
                    <%= "山口県".equals(prefecture) ? "selected" : "" %>>
                    山口県
                </option>

                <option value="徳島県"
                    <%= "徳島県".equals(prefecture) ? "selected" : "" %>>
                    徳島県
                </option>

                <option value="香川県"
                    <%= "香川県".equals(prefecture) ? "selected" : "" %>>
                    香川県
                </option>

                <option value="愛媛県"
                    <%= "愛媛県".equals(prefecture) ? "selected" : "" %>>
                    愛媛県
                </option>

                <option value="高知県"
                    <%= "高知県".equals(prefecture) ? "selected" : "" %>>
                    高知県
                </option>

                <option value="福岡県"
                    <%= "福岡県".equals(prefecture) ? "selected" : "" %>>
                    福岡県
                </option>

                <option value="佐賀県"
                    <%= "佐賀県".equals(prefecture) ? "selected" : "" %>>
                    佐賀県
                </option>

                <option value="長崎県"
                    <%= "長崎県".equals(prefecture) ? "selected" : "" %>>
                    長崎県
                </option>

                <option value="熊本県"
                    <%= "熊本県".equals(prefecture) ? "selected" : "" %>>
                    熊本県
                </option>

                <option value="大分県"
                    <%= "大分県".equals(prefecture) ? "selected" : "" %>>
                    大分県
                </option>

                <option value="宮崎県"
                    <%= "宮崎県".equals(prefecture) ? "selected" : "" %>>
                    宮崎県
                </option>

                <option value="鹿児島県"
                    <%= "鹿児島県".equals(prefecture) ? "selected" : "" %>>
                    鹿児島県
                </option>

                <option value="沖縄県"
                    <%= "沖縄県".equals(prefecture) ? "selected" : "" %>>
                    沖縄県
                </option>

            </select>

        </div>



        <!-- ==========================================
             市区町村
        =========================================== -->

        <div class="form-group">

            <label for="city">
                市区町村
            </label>

            <input
                type="text"
                id="city"
                placeholder="例：下関市"
                value="<%= escapedCity %>"
            >

        </div>



        <!-- ==========================================
             番地
        =========================================== -->

        <div class="form-group">

            <label for="address">
                番地
            </label>

            <input
                type="text"
                id="address"
                placeholder="例：○○町1-2-3"
                value="<%= escapedAddress %>"
            >

        </div>



        <!-- ==========================================
             建物名・部屋番号
        =========================================== -->

        <div class="form-group">

            <label for="building">

                建物名・部屋番号

                <span>任意</span>

            </label>

            <input
                type="text"
                id="building"
                placeholder="例：○○マンション101号室"
                value="<%= escapedBuilding %>"
            >

        </div>



        <!-- ==========================================
             保存ボタン
        =========================================== -->

        <button
            type="button"
            id="saveButton"
            class="save-button"
        >
            保存する
        </button>

    </section>

</main>



<script src="account-address.js"></script>


</body>

</html>