document.addEventListener("DOMContentLoaded", function () {

    const loginForm =
        document.getElementById("loginForm");

    const emailInput =
        document.getElementById("email");

    const passwordInput =
        document.getElementById("password");

    const showPasswordButton =
    document.getElementById("showPasswordButton");

if (showPasswordButton && passwordInput) {

    showPasswordButton.addEventListener(
        "click",
        function () {

            if (passwordInput.type === "password") {

                passwordInput.type = "text";

                showPasswordButton.textContent =
                    "非表示";

            } else {

                passwordInput.type = "password";

                showPasswordButton.textContent =
                    "表示";

            }

        }
    );

}
    // ========================================
    // 前回ログインしたメールアドレスを取得
    // ========================================

    const savedEmail =
        localStorage.getItem("loginEmail");

    if (savedEmail && emailInput) {

        emailInput.value =
            savedEmail;

    }


    // ========================================
    // ログインフォームが存在しない場合
    // ========================================

    if (!loginForm) {

        return;

    }


    // ========================================
    // ログイン処理
    // ========================================

    loginForm.addEventListener(
        "submit",
        function (event) {

            const email =
                emailInput.value.trim();

            const password =
                passwordInput.value;


            // ========================================
            // 入力チェック
            // ========================================

            if (!email || !password) {

                event.preventDefault();

                alert(
                    "メールアドレスとパスワードを入力してください。"
                );

                return;

            }


            // ========================================
            // メールアドレスを保存
            // ========================================

            localStorage.setItem(
                "loginEmail",
                email
            );


            // ========================================
            // ここから先は通常のフォーム送信
            // LoginServletへ送信
            // ========================================

        }
    );

});
