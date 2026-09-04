const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // 入力チェック
    if (!email || !password) {
        alert("メールアドレスとパスワードを入力してください。");
        return;
    }

    // 現在は仮ログイン
    // 将来的にはここをJava APIに変更する
    /*
    fetch("/api/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.href = "home.html";
        } else {
            alert("メールアドレスまたはパスワードが違います。");
        }
    });
    */

    // 仮ログイン成功
    location.href = "home.html";
});