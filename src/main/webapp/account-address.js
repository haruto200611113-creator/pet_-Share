const backButton =
    document.getElementById("backButton");

const saveButton =
    document.getElementById("saveButton");


// ==============================
// 戻るボタン
// ==============================

backButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "settings.html";

    }
);


// ==============================
// 保存ボタン
// ==============================

saveButton.addEventListener(
    "click",
    async function () {

        // 保存ボタンが押されたことを確認
        console.log(
            "住所保存ボタンが押されました"
        );


        // ==============================
        // 入力値を取得
        // ==============================

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


        // ==============================
        // 必須項目チェック
        // ==============================

        if (
            !postalCode ||
            !prefecture ||
            !city ||
            !address
        ) {

            alert(
                "必須項目を入力してください。"
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
            "住所更新を開始します"
        );

       console.log(
    "送信先：" +
    contextPath +
    "/UpdateAddressServlet"
);

// 保存中の表示
saveButton.disabled = true;
saveButton.textContent = "保存中...";

try {

            // ==============================
            // Java Servletへ送信
            // ==============================

            const response =
                await fetch(
                    contextPath +
                    "/UpdateAddressServlet",
                    {

                        method: "POST",

                        headers: {

                            "Content-Type":
                                "application/x-www-form-urlencoded; charset=UTF-8"

                        },

                        body:
                            new URLSearchParams({

                                postalCode:
                                    postalCode,

                                prefecture:
                                    prefecture,

                                city:
                                    city,

                                address:
                                    address,

                                building:
                                    building

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
        "住所の保存に成功しました"
    );

    // 画面中央に変更完了メッセージを表示
    const message = document.createElement("div");
    message.className = "change-complete-message";
    message.textContent = "変更完了しました";

    document.body.appendChild(message);

    // 1.5秒後に設定画面へ戻る
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
                    "住所の保存に失敗しました"
                );

                console.error(
                    "エラーメッセージ：" +
                    message
                );


                alert(
                    "住所の保存に失敗しました。\n" +
                    message
                );

            }

        }


        // ==============================
        // 通信エラー
        // ==============================

               catch (error) {

            console.error(
                "住所更新エラー：",
                error
            );


            alert(
                "住所の保存中にエラーが発生しました。"
            );

                } finally {

    saveButton.disabled = false;
    saveButton.textContent = "保存する";

}

    }
);
