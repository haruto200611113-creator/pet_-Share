const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");


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

        const selected =
            document.querySelector(
                'input[name="gender"]:checked'
            );


        if (!selected) {

            alert(
                "性別を選択してください。"
            );

            return;
        }


        alert(
            "性別を変更しました。"
        );


        window.location.href =
            "settings.html";

    }
);