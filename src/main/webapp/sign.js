"use strict";

/*
 * 新規登録画面 sign.html 用 JavaScript
 *
 * ・利用規約確認状態を sessionStorage で管理
 * ・利用規約確認後に「利用規約に同意する」を有効化
 * ・利用規約ページへ移動する前に入力内容を保存
 * ・sign.html に戻ってきたときに入力内容を復元
 * ・パスワードは保存しない
 * ・入力チェック
 * ・登録成功時に「登録が完了しました」を表示
 */


/* =========================================================
 * HTML要素の取得
 * ========================================================= */

const form = document.getElementById("registerForm");
const agreement = document.getElementById("agreement");
const termsStatus = document.getElementById("termsStatus");
const termsLink = document.getElementById("termsLink");


/* =========================================================
 * HTML要素の存在確認
 * ========================================================= */

if (!form || !agreement || !termsStatus) {

    console.error(
        "登録画面のHTML要素を取得できません。"
    );

    throw new Error(
        "HTML要素が見つかりません。"
    );
}


/* =========================================================
 * 利用規約の確認状態を更新
 * ========================================================= */

function updateTermsStatus() {

    const confirmed =
        sessionStorage.getItem("termsConfirmed");

    if (confirmed === "true") {

        // 利用規約を確認済み
        agreement.disabled = false;

        termsStatus.textContent =
            "利用規約を確認済みです。";

    } else {

        // 利用規約をまだ確認していない
        agreement.checked = false;
        agreement.disabled = true;

        termsStatus.textContent =
            "利用規約を確認してから同意してください。";
    }
}


/* =========================================================
 * 登録フォームの入力内容を保存
 *
 * パスワード・確認用パスワードは保存しない
 * ========================================================= */

function saveFormData() {

    const formData = {

        accountName:
            document.getElementById("accountName").value,

        email:
            document.getElementById("email").value,

        lastName:
            document.getElementById("lastName").value,

        firstName:
            document.getElementById("firstName").value,

        birthDate:
            document.getElementById("birthDate").value,

        gender:
            document.querySelector(
                'input[name="gender"]:checked'
            )?.value || "",

        phone:
            document.getElementById("phone").value,

        postalCode:
            document.getElementById("postalCode").value,

        prefecture:
            document.getElementById("prefecture").value,

        city:
            document.getElementById("city").value,

        address:
            document.getElementById("address").value,

        building:
            document.getElementById("building").value
    };


    sessionStorage.setItem(
        "registerFormData",
        JSON.stringify(formData)
    );
}


/* =========================================================
 * 保存していた入力内容を復元
 * ========================================================= */

function restoreFormData() {

    const savedData =
        sessionStorage.getItem("registerFormData");

    // 保存データがなければ何もしない
    if (!savedData) {
        return;
    }


    try {

        const formData =
            JSON.parse(savedData);


        /* -------------------------
         * 各入力項目を復元
         * ------------------------- */

        document.getElementById(
            "accountName"
        ).value =
            formData.accountName || "";


        document.getElementById(
            "email"
        ).value =
            formData.email || "";


        document.getElementById(
            "lastName"
        ).value =
            formData.lastName || "";


        document.getElementById(
            "firstName"
        ).value =
            formData.firstName || "";


        document.getElementById(
            "birthDate"
        ).value =
            formData.birthDate || "";


        document.getElementById(
            "phone"
        ).value =
            formData.phone || "";


        document.getElementById(
            "postalCode"
        ).value =
            formData.postalCode || "";


        document.getElementById(
            "prefecture"
        ).value =
            formData.prefecture || "";


        document.getElementById(
            "city"
        ).value =
            formData.city || "";


        document.getElementById(
            "address"
        ).value =
            formData.address || "";


        document.getElementById(
            "building"
        ).value =
            formData.building || "";


        /* -------------------------
         * 性別を復元
         * ------------------------- */

        if (formData.gender) {

            const gender =
                document.querySelector(
                    `input[name="gender"][value="${formData.gender}"]`
                );

            if (gender) {
                gender.checked = true;
            }
        }


    } catch (error) {

        console.error(
            "保存した入力内容の復元に失敗しました。",
            error
        );

        sessionStorage.removeItem(
            "registerFormData"
        );
    }
}


/* =========================================================
 * 利用規約ページへのリンク
 *
 * sign.html
 * ↓
 * terms.html
 *
 * 移動する前に入力内容を保存する
 * ========================================================= */

if (termsLink) {

    termsLink.addEventListener(
        "click",
        function () {

            // 利用規約ページから戻ってくる場所を記録
            sessionStorage.setItem(
                "termsFrom",
                "signup"
            );


            // 入力内容を保存
            saveFormData();
        }
    );
}


/* =========================================================
 * 初期処理
 * ========================================================= */

// 利用規約の確認状態を確認
updateTermsStatus();

// 保存されている入力内容を復元
restoreFormData();


/* =========================================================
 * 登録フォーム送信処理
 * ========================================================= */

form.addEventListener(
    "submit",
    function (event) {

        /*
         * HTMLの通常送信を一旦止める
         *
         * 入力チェックが終わるまで
         * RegisterServletへ送信しない
         */
        event.preventDefault();


        console.log(
            "新規登録フォームの入力チェックを開始します。"
        );


        /* =================================================
         * 利用規約の確認チェック
         * ================================================= */

        const confirmed =
            sessionStorage.getItem("termsConfirmed");


        if (confirmed !== "true") {

            alert(
                "先に利用規約を確認してください。"
            );

            return;
        }


        /* =================================================
         * 利用規約への同意チェック
         * ================================================= */

        if (!agreement.checked) {

            alert(
                "利用規約に同意してください。"
            );

            return;
        }


        /* =================================================
         * パスワードチェック
         * ================================================= */

        const password =
            document.getElementById(
                "password"
            ).value;


        const passwordConfirm =
            document.getElementById(
                "confirmPassword"
            ).value;


        // パスワード一致確認
        if (password !== passwordConfirm) {

            alert(
                "パスワードが一致していません。"
            );

            return;
        }


        // パスワード8文字以上確認
        if (password.length < 8) {

            alert(
                "パスワードは8文字以上で入力してください。"
            );

            return;
        }


        /* =================================================
         * 性別チェック
         * ================================================= */

        const selectedGender =
            document.querySelector(
                'input[name="gender"]:checked'
            );


        if (!selectedGender) {

            alert(
                "性別を選択してください。"
            );

            return;
        }


        /* =================================================
         * 入力内容を保存
         * ================================================= */

        saveFormData();


        console.log(
            "入力チェック成功。フォームを送信します。"
        );


        /*
         * 通常のフォーム送信を実行
         *
         * RegisterServlet
         * ↓
         * データベース登録
         * ↓
         * sign.html?register=success
         */
        HTMLFormElement.prototype.submit.call(form);

    }
);


/* =========================================================
 * 登録完了メッセージ
 *
 * RegisterServletから
 *
 * sign.html?register=success
 *
 * で戻ってきた場合に実行する
 * ========================================================= */

const params =
    new URLSearchParams(
        window.location.search
    );


if (
    params.get("register") === "success"
) {

    /* =====================================================
     * 登録完了メッセージを表示
     * ===================================================== */

    const message =
        document.createElement("div");


    message.className =
        "change-complete-message";


    message.textContent =
        "登録が完了しました";


    document.body.appendChild(
        message
    );


    /* =====================================================
     * 登録完了後に保存データを削除
     * ===================================================== */

    sessionStorage.removeItem(
        "registerFormData"
    );

    sessionStorage.removeItem(
        "termsConfirmed"
    );

    sessionStorage.removeItem(
        "termsFrom"
    );


    /* =====================================================
     * 1.5秒後にログイン画面へ移動
     * ===================================================== */

    setTimeout(
        function () {

            window.location.href =
                "login.html";

        },
        1500
    );
}