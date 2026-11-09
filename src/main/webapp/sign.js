"use strict";

// =====================================================
// HTML要素の取得
// =====================================================

const form = document.getElementById("registerForm");
const agreement = document.getElementById("agreement");
const termsStatus = document.getElementById("termsStatus");
const termsLink = document.getElementById("termsLink");


// =====================================================
// HTML要素の存在確認
// =====================================================

if (!form || !agreement || !termsStatus) {
    console.error("登録画面のHTML要素を取得できません。");
    throw new Error("HTML要素が見つかりません。");
}


// =====================================================
// 利用規約確認状態
// =====================================================

function updateTermsStatus() {

    const confirmed =
        sessionStorage.getItem("termsConfirmed");

    if (confirmed === "true") {

        agreement.disabled = false;

        termsStatus.textContent =
            "利用規約を確認済みです。";

    } else {

        agreement.checked = false;
        agreement.disabled = true;

        termsStatus.textContent =
            "利用規約を確認してから同意してください。";
    }
}

updateTermsStatus();


// =====================================================
// 入力内容を保存
// =====================================================

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


// =====================================================
// 保存した入力内容を復元
// =====================================================

function restoreFormData() {

    const savedData =
        sessionStorage.getItem("registerFormData");

    if (!savedData) {
        return;
    }

    try {

        const formData =
            JSON.parse(savedData);

        document.getElementById("accountName").value =
            formData.accountName || "";

        document.getElementById("email").value =
            formData.email || "";

        document.getElementById("lastName").value =
    formData.lastName || "";

document.getElementById("firstName").value =
    formData.firstName || "";

        document.getElementById("birthDate").value =
            formData.birthDate || "";

        document.getElementById("phone").value =
            formData.phone || "";

        document.getElementById("postalCode").value =
            formData.postalCode || "";

        document.getElementById("prefecture").value =
            formData.prefecture || "";

        document.getElementById("city").value =
            formData.city || "";

        document.getElementById("address").value =
            formData.address || "";

        document.getElementById("building").value =
            formData.building || "";


        // 性別の復元

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


// =====================================================
// 利用規約を開く
// =====================================================

if (termsLink) {

    termsLink.addEventListener(
        "click",
        function () {

            // 新規登録から利用規約を開いたことを保存

            sessionStorage.setItem(
                "termsFrom",
                "signup"
            );

            // 入力内容を保存

            saveFormData();
        }
    );
}


// =====================================================
// ページ読み込み時に入力内容を復元
// =====================================================

restoreFormData();


// =====================================================
// 新規登録処理
// =====================================================

form.addEventListener(
    "submit",
    function (event) {

        // JavaScriptで入力チェックを行う

        event.preventDefault();

        console.log(
            "新規登録フォームの入力チェックを開始します。"
        );


        // ---------------------------------------------
        // 利用規約の確認
        // ---------------------------------------------

        const confirmed =
            sessionStorage.getItem("termsConfirmed");

        if (confirmed !== "true") {

            alert(
                "先に利用規約を確認してください。"
            );

            return;
        }


        // ---------------------------------------------
        // 利用規約への同意
        // ---------------------------------------------

        if (!agreement.checked) {

            alert(
                "利用規約に同意してください。"
            );

            return;
        }


        // ---------------------------------------------
        // パスワード取得
        // ---------------------------------------------

        const password =
            document.getElementById("password").value;

        const passwordConfirm =
            document.getElementById("confirmPassword").value;


        // ---------------------------------------------
        // パスワード一致確認
        // ---------------------------------------------

        if (password !== passwordConfirm) {

            alert(
                "パスワードが一致していません。"
            );

            return;
        }


        // ---------------------------------------------
        // パスワード文字数確認
        // ---------------------------------------------

        if (password.length < 8) {

            alert(
                "パスワードは8文字以上で入力してください。"
            );

            return;
        }


        // ---------------------------------------------
        // 性別確認
        // ---------------------------------------------

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


        // ---------------------------------------------
        // 入力内容を保存
        // ---------------------------------------------

        saveFormData();


        // ---------------------------------------------
        // JavaのRegisterServletへ送信
        // ---------------------------------------------

        console.log(
            "入力チェック成功。フォームを送信します。"
        );

        // submitイベントを再発生させずに送信

        HTMLFormElement.prototype.submit.call(form);

    }
    // 新規登録完了メッセージを表示
const params = new URLSearchParams(window.location.search);

if (params.get("register") === "success") {

    const message = document.createElement("div");
    message.className = "change-complete-message";
    message.textContent = "登録が完了しました";

    document.body.appendChild(message);

    // 登録完了後は保存していた入力内容を削除
    sessionStorage.removeItem("registerFormData");
    sessionStorage.removeItem("termsConfirmed");
    sessionStorage.removeItem("termsFrom");

    // 1.5秒後にログイン画面へ移動
    setTimeout(function () {

        window.location.href = "login.html";

    }, 1500);
}
);