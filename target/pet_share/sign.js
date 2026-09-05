
const form = document.getElementById("registerForm");

const agreement = document.getElementById("agreement");
const termsStatus = document.getElementById("termsStatus");


// =====================================================
// 利用規約確認状態
// =====================================================

const termsConfirmed =
    sessionStorage.getItem("termsConfirmed");

if (termsConfirmed === "true") {

    agreement.disabled = false;

    termsStatus.textContent =
        "利用規約を確認済みです。";

}


// =====================================================
// 入力内容を保存
// =====================================================

function saveFormData() {

    const formData = {

        accountName:
            document.getElementById("accountName").value,

        email:
            document.getElementById("email").value,

        password:
            document.getElementById("password").value,

        passwordConfirm:
            document.getElementById("passwordConfirm").value,

        name:
            document.getElementById("name").value,

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


    const formData =
        JSON.parse(savedData);


    document.getElementById("accountName").value =
        formData.accountName || "";

    document.getElementById("email").value =
        formData.email || "";

    document.getElementById("password").value =
        formData.password || "";

    document.getElementById("passwordConfirm").value =
        formData.passwordConfirm || "";

    document.getElementById("name").value =
        formData.name || "";

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


    if (formData.gender) {

        const gender =
            document.querySelector(
                `input[name="gender"][value="${formData.gender}"]`
            );

        if (gender) {
            gender.checked = true;
        }
    }
}


// =====================================================
// 利用規約を開く
// =====================================================

const termsLink =
    document.getElementById("termsLink");


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
// 新規登録
// =====================================================

form.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();


        // ---------------------------------------------
        // 利用規約確認チェック
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
        // 利用規約同意チェック
        // ---------------------------------------------

        if (!agreement.checked) {

            alert(
                "利用規約に同意してください。"
            );

            return;
        }


        // ---------------------------------------------
        // パスワード確認
        // ---------------------------------------------

        const password =
            document.getElementById("password").value;

        const passwordConfirm =
            document.getElementById("passwordConfirm").value;


        if (password !== passwordConfirm) {

            alert(
                "パスワードが一致していません。"
            );

            return;
        }


        // ---------------------------------------------
        // パスワード文字数
        // ---------------------------------------------

        if (password.length < 8) {

            alert(
                "パスワードは8文字以上で入力してください。"
            );

            return;
        }


        // ---------------------------------------------
        // 性別
        // ---------------------------------------------

        if (
            !document.querySelector(
                'input[name="gender"]:checked'
            )
        ) {

            alert(
                "性別を選択してください。"
            );

            return;
        }


        // ---------------------------------------------
        // 登録完了
        // ---------------------------------------------

        alert(
            "新規登録が完了しました。"
        );


        // ---------------------------------------------
        // 保存データ削除
        // ---------------------------------------------

        sessionStorage.removeItem(
            "registerFormData"
        );

        sessionStorage.removeItem(
            "termsConfirmed"
        );

        sessionStorage.removeItem(
            "termsFrom"
        );

    }
);

