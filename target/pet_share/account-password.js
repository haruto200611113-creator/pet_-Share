const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");


/* =========================
   戻る
========================= */

backButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "settings.html";

    }
);


/* =========================
   パスワード表示・非表示
========================= */

const showButtons =
    document.querySelectorAll(
        ".show-button"
    );


showButtons.forEach(
    function (button) {

        button.addEventListener(
            "click",
            function () {

                const targetId =
                    button.dataset.target;

                const target =
                    document.getElementById(
                        targetId
                    );


                if (target.type === "password") {

                    target.type =
                        "text";

                    button.textContent =
                        "非表示";

                } else {

                    target.type =
                        "password";

                    button.textContent =
                        "表示";

                }

            }
        );

    }
);


/* =========================
   パスワード変更
========================= */

saveButton.addEventListener(
    "click",
    function () {

        const currentPassword =
            document.getElementById(
                "currentPassword"
            ).value;

        const newPassword =
            document.getElementById(
                "newPassword"
            ).value;

        const confirmPassword =
            document.getElementById(
                "confirmPassword"
            ).value;


        if (!currentPassword ||
            !newPassword ||
            !confirmPassword) {

            alert(
                "すべての項目を入力してください。"
            );

            return;
        }


        if (newPassword.length < 8) {

            alert(
                "新しいパスワードは8文字以上にしてください。"
            );

            return;
        }


        if (
            newPassword !==
            confirmPassword
        ) {

            alert(
                "新しいパスワードが一致していません。"
            );

            return;
        }


        alert(
            "パスワードを変更しました。"
        );


        window.location.href =
            "settings.html";

    }
);