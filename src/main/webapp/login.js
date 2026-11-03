document.addEventListener("DOMContentLoaded", function () {


const loginForm = document.getElementById("loginForm");

if (!loginForm) {
    return;
}

loginForm.addEventListener("submit", function (event) {

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    // 入力チェック
    if (!email || !password) {
        event.preventDefault();
        alert("メールアドレスとパスワードを入力してください。");
        return;
    }

    // 入力内容に問題がなければ、
    // 通常のフォーム送信でLoginServletへ送信する
});


});
