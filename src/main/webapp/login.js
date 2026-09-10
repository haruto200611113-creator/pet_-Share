const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", function (event) {

    event.preventDefault();


    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value;


    // 入力チェック
    if (!email || !password) {

        alert(
            "メールアドレスとパスワードを入力してください。"
        );

        return;
    }


    // 現在は仮ログイン
    // Java・データベース接続後に変更する

    location.href = "home.html";

});