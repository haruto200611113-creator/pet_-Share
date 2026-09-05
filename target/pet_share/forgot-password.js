const forgotForm = document.getElementById("forgotForm");

forgotForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;

    console.log("再設定メール送信先:", email);

    alert(
        "パスワード再設定の案内を送信しました。\n" +
        "メールをご確認ください。"
    );
});
