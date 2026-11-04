console.log("account-name.js 読み込み成功");
document.addEventListener("DOMContentLoaded", () => {

    const nameInput = document.getElementById("name");
    const saveButton = document.getElementById("saveButton");
    const backButton = document.getElementById("backButton");

    // 設定画面に戻る
    backButton.addEventListener("click", () => {
        location.href = "settings.html";
    });

    // 氏名を保存する
    saveButton.addEventListener("click", async () => {

        const name = nameInput.value.trim();

        // 未入力チェック
        if (name === "") {
            alert("氏名を入力してください。");
            nameInput.focus();
            return;
        }

        // 保存中はボタンを無効化
        saveButton.disabled = true;
        saveButton.textContent = "保存中...";

        try {

           const response = await fetch(
    `${window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/"))}/UpdateNameServlet`,
    {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: new URLSearchParams({
            name: name
        })
    }
);
console.log("氏名変更Servletの応答:", response.status);
console.log("送信した氏名:", name);

            if (response.ok) {

                alert("氏名を変更しました。");

                // 設定画面に戻る
                location.href = "settings.html";

            } else {

                const message = await response.text();
                alert("氏名の変更に失敗しました。\n" + message);

            }

        } catch (error) {

            console.error("氏名変更エラー:", error);
            alert("通信エラーが発生しました。");

        } finally {

            saveButton.disabled = false;
            saveButton.textContent = "保存する";

        }

    });

});