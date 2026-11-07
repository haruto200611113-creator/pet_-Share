document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.getElementById("backButton");

    const saveButton =
        document.getElementById("saveButton");

    const emailInput =
        document.getElementById("email");


    // ==============================
    // 戻るボタン
    // ==============================

    if (backButton) {

        backButton.addEventListener(
            "click",
            function () {

                window.location.href =
                    "settings.html";

            }
        );

    }


    // ==============================
    // 保存ボタン
    // ==============================

    if (saveButton) {

        saveButton.addEventListener(
            "click",
            async function () {

                console.log(
                    "メールアドレス保存ボタンが押されました"
                );


                // ==============================
                // 新しいメールアドレスを取得
                // ==============================

                const email =
                    emailInput.value.trim();


                // ==============================
                // 入力チェック
                // ==============================

                if (!email) {

                    alert(
                        "新しいメールアドレスを入力してください。"
                    );

                    return;

                }


                // ==============================
                // メールアドレス形式チェック
                // ==============================

                const emailPattern =
                    /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

                if (!emailPattern.test(email)) {

                    alert(
                        "正しいメールアドレスを入力してください。"
                    );

                    return;

                }


                // ==============================
                // コンテキストパス取得
                // ==============================

                const contextPath =
                    window.location.pathname.substring(
                        0,
                        window.location.pathname.lastIndexOf("/")
                    );


                console.log(
                    "メールアドレス更新を開始します"
                );

                console.log(
                    "送信先：" +
                    contextPath +
                    "/UpdateEmailServlet"
                );


                // ==============================
                // 保存中表示
                // ==============================

                saveButton.disabled = true;
                saveButton.textContent = "保存中...";


                try {

                    // ==============================
                    // Servletへ送信
                    // ==============================

                    const response =
                        await fetch(
                            contextPath +
                            "/UpdateEmailServlet",
                            {

                                method: "POST",

                                headers: {
                                    "Content-Type":
                                        "application/x-www-form-urlencoded; charset=UTF-8"
                                },

                                body:
                                    new URLSearchParams({
                                        email: email
                                    })

                            }
                        );


                    console.log(
                        "Servletからのレスポンス：" +
                        response.status
                    );


                    // ==============================
                    // 保存成功
                    // ==============================

                    if (response.ok) {

                        console.log(
                            "メールアドレスの保存に成功しました"
                        );


                        // 画面中央にメッセージを表示
                        const message =
                            document.createElement("div");

                        message.className =
                            "change-complete-message";

                        message.textContent =
                            "変更完了しました";

                        document.body.appendChild(
                            message
                        );


                        // 1.5秒後に設定画面へ
                        setTimeout(function () {

                            window.location.href =
                                "settings.html";

                        }, 1500);

                    }


                    // ==============================
                    // 保存失敗
                    // ==============================

                    else {

                        const message =
                            await response.text();

                        console.error(
                            "メールアドレスの保存に失敗しました"
                        );

                        console.error(
                            "エラーメッセージ：" +
                            message
                        );

                        alert(
                            "メールアドレスの保存に失敗しました。\n" +
                            message
                        );


                        // 保存ボタンを元に戻す
                        saveButton.disabled = false;
                        saveButton.textContent = "保存する";

                    }

                }


                // ==============================
                // 通信エラー
                // ==============================

                catch (error) {

                    console.error(
                        "メールアドレス更新エラー：",
                        error
                    );

                    alert(
                        "メールアドレスの保存中にエラーが発生しました。"
                    );


                    // 保存ボタンを元に戻す
                    saveButton.disabled = false;
                    saveButton.textContent = "保存する";

                }

            }
        );

    }

});