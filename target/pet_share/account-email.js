const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");

const email =
    document.getElementById("email");


backButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "settings.html";

    }
);


saveButton.addEventListener(
    "click",
    function () {

        const value =
            email.value.trim();


        if (!value) {

            alert(
                "メールアドレスを入力してください。"
            );

            return;
        }


        if (!email.checkValidity()) {

            alert(
                "正しいメールアドレスを入力してください。"
            );

            return;
        }


        alert(
            "メールアドレスを変更しました。"
        );


        window.location.href =
            "settings.html";

    }
);