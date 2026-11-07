console.log("account-name.js 読み込み成功");

document.addEventListener("DOMContentLoaded", () => {

    const lastNameInput = document.getElementById("lastName");
    const firstNameInput = document.getElementById("firstName");
    const saveButton = document.getElementById("saveButton");
    const backButton = document.getElementById("backButton");

    // 設定画面に戻る
    backButton.addEventListener("click", () => {
        window.location.href = "settings.html";
    });

    // 氏名を保存する
    saveButton.addEventListener("click", async () => {

        const lastName = lastNameInput.value.trim();
        const firstName = firstNameInput.value.trim();

        // 姓の未入力チェック
        if (lastName === "") {
            alert("姓を入力してください。");
            lastNameInput.focus();
            return;
        }

        // 名の未入力チェック
        if (firstName === "") {
            alert("名を入力してください。");
            firstNameInput.focus();
            return;
        }

        // 保存中
        saveButton.disabled = true;
        saveButton.textContent = "保存中...";

        try {

            // コンテキストパスを取得
            const contextPath =
                window.location.pathname.substring(
                    0,
                    window.location.pathname.lastIndexOf("/")
                );

            // Servletへ送信
            const response = await fetch(
                `${contextPath}/UpdateNameServlet`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded; charset=UTF-8"
                    },
                    body: new URLSearchParams({
                        lastName: lastName,
                        firstName: firstName
                    })
                }
            );

            console.log(
                "氏名変更Servletの応答:",
                response.status
            );

            console.log(
                "送信した姓:",
                lastName
            );

            console.log(
                "送信した名:",
                firstName
            );

            // 保存成功
            if (response.ok) {

                console.log("氏名の保存に成功しました");

                // 変更完了メッセージを作成
                const message =
                    document.createElement("div");

                message.className =
                    "change-complete-message";

                message.textContent =
                    "変更完了しました";

                // 画面に表示
                document.body.appendChild(message);

                // 1.5秒後に設定画面へ戻る
                setTimeout(() => {
                    window.location.href =
                        "settings.html";
                }, 1500);

                return;
            }

            // 保存失敗
            const errorMessage =
                await response.text();

            alert(
                "氏名の変更に失敗しました。\n" +
                errorMessage
            );

            saveButton.disabled = false;
            saveButton.textContent = "保存する";

        } catch (error) {

            console.error(
                "氏名変更エラー:",
                error
            );

            alert(
                "通信エラーが発生しました。"
            );

            saveButton.disabled = false;
            saveButton.textContent = "保存する";
        }
    });
});
