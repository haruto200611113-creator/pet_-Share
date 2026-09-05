const backButton =
    document.getElementById("backButton");

const addPaymentButton =
    document.getElementById("addPaymentButton");

const paymentForm =
    document.getElementById("paymentForm");

const savePaymentButton =
    document.getElementById("savePaymentButton");


// ==============================
// 戻るボタン
// ==============================

backButton.addEventListener("click", function () {

    window.location.assign("settings.html");

});


// ==============================
// 支払い方法追加フォーム表示
// ==============================

addPaymentButton.addEventListener("click", function () {

    paymentForm.classList.toggle("hidden");

});


// ==============================
// 支払い方法登録
// ==============================

savePaymentButton.addEventListener("click", function () {

    const cardName =
        document.getElementById("cardName").value.trim();

    const cardNumber =
        document.getElementById("cardNumber").value.trim();

    const expiry =
        document.getElementById("expiry").value.trim();

    const securityCode =
        document.getElementById("securityCode").value.trim();


    if (
        !cardName ||
        !cardNumber ||
        !expiry ||
        !securityCode
    ) {

        alert("すべての項目を入力してください。");

        return;

    }


    alert("支払い方法を登録しました。");

    paymentForm.classList.add("hidden");

});