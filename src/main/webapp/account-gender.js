document.addEventListener("DOMContentLoaded", () => {

    const genderInputs =
        document.querySelectorAll('input[name="gender"]');

    const saveButton =
        document.getElementById("saveButton");

    const backButton =
        document.getElementById("backButton");


    // 戻るボタン
    backButton.addEventListener("click", () => {
        window.location.href = "settings.html";
    });


    // 保存ボタン
    saveButton.addEventListener("click", async () => {

        let selectedGender = "";

        // 選択されている性別を取得
        genderInputs.forEach(input => {

            if (input.checked) {
                selectedGender = input.value;
            }

        });


        // 未選択チェック
        if (!selectedGender) {

            alert("性別を選択してください。");

            return;
        }


        // 保存中表示
        saveButton.disabled = true;
        saveButton.textContent = "保存中...";


        try {

            const contextPath =
                window.location.pathname.split("/")[1];


            const response = await fetch(
                `/${contextPath}/UpdateGenderServlet`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded; charset=UTF-8"
                    },

                    body: new URLSearchParams({
                        gender: selectedGender
                    })
                }
            );


            const result =
                await response.json();


            if (
                response.ok &&
                result.success
            ) {

                // 変更完了メッセージ
                const message =
                    document.createElement("div");

                message.className =
                    "change-complete-message";

                message.textContent =
                    "変更完了しました";

                document.body.appendChild(message);


                // 約1.5秒後に設定画面へ
                setTimeout(() => {

                    window.location.href =
                        "settings.html";

                }, 1500);

            } else {

                alert(
                    result.message ||
                    "性別の変更に失敗しました。"
                );

                saveButton.disabled = false;
                saveButton.textContent = "保存する";
            }

        } catch (error) {

            console.error(
                "性別変更エラー：",
                error
            );

            alert(
                "性別の変更中にエラーが発生しました。"
            );

            saveButton.disabled = false;
            saveButton.textContent = "保存する";
        }

    });

});