const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");

const birthDate =
    document.getElementById("birthDate");


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

        if (!birthDate.value) {

            alert(
                "生年月日を選択してください。"
            );

            return;
        }


        alert(
            "生年月日を変更しました。"
        );


        window.location.href =
            "settings.html";

    }
);