document.addEventListener("DOMContentLoaded", () => {

    const birthYearInput =
        document.getElementById("birthYear");

    const birthMonthInput =
        document.getElementById("birthMonth");

    const birthDayInput =
        document.getElementById("birthDay");

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

        const year =
            birthYearInput.value.trim();

        const month =
            birthMonthInput.value.trim();

        const day =
            birthDayInput.value.trim();


        // 入力チェック
        if (!year || !month || !day) {

            alert("生年月日をすべて入力してください。");

            return;
        }


        // 年のチェック
        if (year.length !== 4) {

            alert("年は4桁で入力してください。");

            return;
        }


        // 月のチェック
        const monthNumber =
            Number(month);

        if (
            monthNumber < 1 ||
            monthNumber > 12
        ) {

            alert("月は1～12で入力してください。");

            return;
        }


        // 日のチェック
        const dayNumber =
            Number(day);

        if (
            dayNumber < 1 ||
            dayNumber > 31
        ) {

            alert("日は1～31で入力してください。");

            return;
        }


        // 月・日を2桁にする
        const formattedMonth =
            String(monthNumber).padStart(2, "0");

        const formattedDay =
            String(dayNumber).padStart(2, "0");


        // yyyy-MM-dd 形式にする
        const birthDate =
            `${year}-${formattedMonth}-${formattedDay}`;


        // 保存中表示
        saveButton.disabled = true;
        saveButton.textContent = "保存中...";


        try {

            const contextPath =
                window.location.pathname.split("/")[1];


            const response = await fetch(
                `/${contextPath}/UpdateBirthDateServlet`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded; charset=UTF-8"
                    },

                    body: new URLSearchParams({
                        birthDate: birthDate
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
                    "生年月日の変更に失敗しました。"
                );

                saveButton.disabled = false;
                saveButton.textContent = "保存する";
            }

        } catch (error) {

            console.error(
                "生年月日変更エラー：",
                error
            );

            alert(
                "生年月日の変更中にエラーが発生しました。"
            );

            saveButton.disabled = false;
            saveButton.textContent = "保存する";
        }

    });

});