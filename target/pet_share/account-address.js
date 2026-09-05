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

        const postalCode =
            document.getElementById(
                "postalCode"
            ).value.trim();

        const prefecture =
            document.getElementById(
                "prefecture"
            ).value;

        const city =
            document.getElementById(
                "city"
            ).value.trim();

        const address =
            document.getElementById(
                "address"
            ).value.trim();

        const building =
            document.getElementById(
                "building"
            ).value.trim();


        if (!postalCode ||
            !prefecture ||
            !city ||
            !address) {

            alert(
                "必須項目を入力してください。"
            );

            return;
        }


        alert(
            "住所を保存しました。"
        );


        window.location.href =
            "settings.html";

    }
);