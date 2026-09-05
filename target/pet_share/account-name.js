const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");

const nameInput =
    document.getElementById("name");


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
            nameInput.value.trim();


        if (!value) {

            alert(
                "氏名を入力してください。"
            );

            return;
        }


        alert(
            "氏名を変更しました。"
        );


        window.location.href =
            "settings.html";

    }
);