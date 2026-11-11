document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.getElementById("backButton");

    const cancelButton =
        document.getElementById("cancelButton");

    const registerButton =
        document.getElementById("registerButton");

    const imageInput =
        document.getElementById("petImage");

    const imagePreview =
        document.getElementById("petImagePreview");

    const imageDefault =
        document.getElementById("petImageDefault");


    /* =========================
       戻る
    ========================= */

    backButton.addEventListener("click", function () {
        window.location.href = "home.html";
    });


    /* =========================
       キャンセル
    ========================= */

    cancelButton.addEventListener("click", function () {
        window.location.href = "home.html";
    });


    /* =========================
       画像プレビュー
    ========================= */

    imageInput.addEventListener("change", function () {

        const file = this.files[0];

        if (!file) {

            imagePreview.src = "";
            imagePreview.style.display = "none";

            imageDefault.style.display = "inline";

            return;
        }


        if (!file.type.startsWith("image/")) {

            alert("画像ファイルを選択してください。");

            this.value = "";

            imagePreview.src = "";
            imagePreview.style.display = "none";

            imageDefault.style.display = "inline";

            return;
        }


        const reader = new FileReader();

        reader.onload = function (event) {

            imagePreview.src =
                event.target.result;

            imagePreview.style.display =
                "block";

            imageDefault.style.display =
                "none";
        };

        reader.readAsDataURL(file);
    });


    /* =========================
       ペット登録
    ========================= */

    registerButton.addEventListener(
        "click",
        async function () {

            /*
             * 必須項目確認
             */

            const petName =
                document.getElementById("petName").value.trim();

            const petType =
                document.getElementById("petType").value;


            if (petName === "") {

                alert("ペットの名前を入力してください。");

                document.getElementById("petName").focus();

                return;
            }


            if (petType === "") {

                alert("ペットの種類を選択してください。");

                document.getElementById("petType").focus();

                return;
            }


            /*
             * FormData作成
             */

            const formData =
                new FormData();


            formData.append(
                "petName",
                petName
            );

            formData.append(
                "petType",
                petType
            );

            formData.append(
                "breed",
                document.getElementById("breed").value
            );

            formData.append(
                "gender",
                document.getElementById("gender").value
            );

            formData.append(
                "birthDate",
                document.getElementById("birthDate").value
            );

            formData.append(
                "weight",
                document.getElementById("weight").value
            );

            formData.append(
                "personality",
                document.getElementById("personality").value
            );

            formData.append(
                "features",
                document.getElementById("features").value
            );

            formData.append(
                "likes",
                document.getElementById("likes").value
            );

            formData.append(
                "dislikes",
                document.getElementById("dislikes").value
            );

            formData.append(
                "otherAnimals",
                document.getElementById("otherAnimals").value
            );

            formData.append(
                "childrenCompatibility",
                document.getElementById("childrenCompatibility").value
            );

            formData.append(
                "foodInfo",
                document.getElementById("foodInfo").value
            );

            formData.append(
                "walkInfo",
                document.getElementById("walkInfo").value
            );

            formData.append(
                "toiletInfo",
                document.getElementById("toiletInfo").value
            );

            formData.append(
                "aloneTime",
                document.getElementById("aloneTime").value
            );

            formData.append(
                "careInfo",
                document.getElementById("careInfo").value
            );

            formData.append(
                "vaccination",
                document.getElementById("vaccination").value
            );

            formData.append(
                "neutering",
                document.getElementById("neutering").value
            );

            formData.append(
                "medicalHistory",
                document.getElementById("medicalHistory").value
            );

            formData.append(
                "allergies",
                document.getElementById("allergies").value
            );

            formData.append(
                "medication",
                document.getElementById("medication").value
            );

            formData.append(
                "precautions",
                document.getElementById("precautions").value
            );

            formData.append(
                "sharePrice",
                document.getElementById("sharePrice").value
            );

            formData.append(
                "shareStartDate",
                document.getElementById("shareStartDate").value
            );

            formData.append(
                "shareEndDate",
                document.getElementById("shareEndDate").value
            );

            formData.append(
                "shareConditions",
                document.getElementById("shareConditions").value
            );


            /*
             * 画像
             */

            if (imageInput.files.length > 0) {

                formData.append(
                    "petImage",
                    imageInput.files[0]
                );
            }


            /*
             * ボタンを一時的に無効化
             */

            registerButton.disabled = true;

            registerButton.textContent =
                "登録中...";


            try {

                const response =
                    await fetch(
                        "pet-register",
                        {
                            method: "POST",
                            body: formData
                        }
                    );


                if (response.redirected) {

    window.location.href = response.url;

    return;
}


/*
 * Servletからリダイレクトされなかった場合
 */

if (response.ok) {

    alert("ペットを登録しました。");

    window.location.href = "home.html";

} else {

    alert("ペットの登録に失敗しました。");

    registerButton.disabled = false;

    registerButton.textContent = "ペットを登録する";
}

            } catch (error) {

                console.error(error);

                alert(
                    "通信エラーが発生しました。"
                );

                registerButton.disabled =
                    false;

                registerButton.textContent =
                    "ペットを登録する";
            }

        }
    );


    /* =========================
       登録成功メッセージ
    ========================= */

    const params =
        new URLSearchParams(
            window.location.search
        );


    if (params.get("success") === "true") {

        alert(
            "ペットを登録しました。"
        );

        window.history.replaceState(
            {},
            document.title,
            "pet-register.html"
        );
    }


    if (params.get("error") === "true") {

        alert(
            "ペットの登録に失敗しました。"
        );

        window.history.replaceState(
            {},
            document.title,
            "pet-register.html"
        );
    }

});